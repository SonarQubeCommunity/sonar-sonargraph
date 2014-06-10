/*
 * Sonar Sonargraph Plugin
 * Copyright (C) 2009, 2010, 2011 hello2morrow GmbH
 * mailto: info AT hello2morrow DOT com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
/**
 *
 */
package com.hello2morrow.sonarplugin.processor;

import com.hello2morrow.sonarplugin.foundation.SonargraphPluginBase;
import com.hello2morrow.sonarplugin.foundation.Utilities;
import com.hello2morrow.sonarplugin.metric.SonargraphSimpleMetrics;
import com.hello2morrow.sonarplugin.persistence.PersistenceUtilities;
import com.hello2morrow.sonarplugin.xsd.ReportContext;
import com.hello2morrow.sonarplugin.xsd.XsdAttributeRoot;
import com.hello2morrow.sonarplugin.xsd.XsdPosition;
import com.hello2morrow.sonarplugin.xsd.XsdTask;
import com.hello2morrow.sonarplugin.xsd.XsdTasks;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sonar.api.batch.SensorContext;
import org.sonar.api.batch.fs.FileSystem;
import org.sonar.api.component.ResourcePerspectives;
import org.sonar.api.profiles.RulesProfile;
import org.sonar.api.resources.Project;
import org.sonar.api.rule.Severity;
import org.sonar.api.rules.ActiveRule;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Ingmar
 *
 */
public class TaskProcessor implements IProcessor {

  private static final Logger LOG = LoggerFactory.getLogger(WarningProcessor.class);
  private static final String PACKAGE = " package";
  private final SensorContext sensorContext;
  private final RulesProfile rulesProfile;
  private final ResourcePerspectives resourcePerspectives;
  private final Project project;
  private final FileSystem fileSystem;

  public TaskProcessor(Project project, FileSystem fileSystem, final RulesProfile rulesProfile, final SensorContext sensorContext, ResourcePerspectives perspectives) {
    this.project = project;
    this.fileSystem = fileSystem;
    this.rulesProfile = rulesProfile;
    this.sensorContext = sensorContext;
    this.resourcePerspectives = perspectives;
  }

  /*
   * (non-Javadoc)
   * 
   * @see com.hello2morrow.sonarplugin.handler.IProcessor#process(com.hello2morrow.sonarplugin.xsd.ReportContext,
   * com.hello2morrow.sonarplugin.xsd.XsdAttributeRoot)
   */
  @Override
  public void process(ReportContext report, XsdAttributeRoot buildUnit) {
    LOG.debug("Analysing tasks of buildUnit: " + buildUnit.getName());

    XsdTasks tasks = report.getTasks();
    Map<String, String> priorityMap = new HashMap<String, String>();

    ActiveRule rule = rulesProfile.getActiveRule(SonargraphPluginBase.PLUGIN_KEY, SonargraphPluginBase.TASK_RULE_KEY);
    int count = 0;

    if (rule == null) {
      LOG.info("Sonargraph task rule not active in current profile");
      return;
    }

    priorityMap.put("Low", Severity.INFO);
    priorityMap.put("Medium", Severity.MINOR);
    priorityMap.put("High", Severity.MAJOR);

    for (XsdTask task : tasks.getTask()) {
      String bu = PersistenceUtilities.getAttribute(task.getAttribute(), "Build unit");

      bu = Utilities.getBuildUnitName(bu);
      if (bu.equals(Utilities.getBuildUnitName(buildUnit.getName()))) {
        count = handleTask(priorityMap, rule, count, task);
      }
    }
    Utilities.saveMeasureToContext(sensorContext, SonargraphSimpleMetrics.TASK_REFS, count, 0);
  }

  private int handleTask(Map<String, String> priorityMap, ActiveRule rule, final int count, final XsdTask task) {
    String priority = PersistenceUtilities.getAttribute(task.getAttribute(), "Priority");
    String description = PersistenceUtilities.getAttribute(task.getAttribute(), "Description");
    String assignedTo = PersistenceUtilities.getAttribute(task.getAttribute(), "Assigned to");

    // This should not be needed, but the current description sucks
    description = handleDescription(description);

    int index = description.indexOf(PACKAGE);
    int counter = count;

    if (index > 0 && index < PACKAGE.length()) {
      // Package refactorings won't get markers - this would
      // create to many non relevant markers
      counter++;
    } else {
      if (assignedTo != null) {
        assignedTo = '[' + assignedTo.trim() + ']';
        if (assignedTo.length() > 2) {
          description += ' ' + assignedTo;
        }
      }
      for (XsdPosition pos : task.getPosition()) {
        String relFileName = pos.getFile();

        if (relFileName != null) {
          String fqName = Utilities.relativeFileNameToFqName(relFileName);
          int line = Integer.valueOf(pos.getLine());

          if (line == 0) {
            line = 1;
          }
          Utilities.saveViolation(project, fileSystem, resourcePerspectives, rule, priorityMap.get(priority), fqName, line, description);
        }
        counter++;
      }
    }
    return counter;
  }

  private String handleDescription(String descr) {
    if (descr.startsWith("Fix warning")) {
      // TODO: handle ascending metrics correctly (99% are descending)
      return "Reduce" + descr.substring(descr.indexOf(':') + 1).toLowerCase();
    }
    if (descr.startsWith("Cut type")) {
      String toType = descr.substring(descr.indexOf("to "));
      return "Cut dependency " + toType;
    }
    if (descr.startsWith("Move type")) {
      String to = descr.substring(descr.indexOf("to "));
      return "Move " + to;
    }
    return descr;
  }

}
