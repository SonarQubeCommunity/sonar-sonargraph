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

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sonar.api.batch.SensorContext;
import org.sonar.api.rules.Rule;
import org.sonar.api.rules.RuleFinder;
import org.sonar.api.rules.RulePriority;

import com.hello2morrow.sonarplugin.foundation.SonargraphPluginBase;
import com.hello2morrow.sonarplugin.foundation.Utilities;
import com.hello2morrow.sonarplugin.metric.SonargraphSimpleMetrics;
import com.hello2morrow.sonarplugin.xsd.ReportContext;
import com.hello2morrow.sonarplugin.xsd.XsdAttributeRoot;
import com.hello2morrow.sonarplugin.xsd.XsdPosition;
import com.hello2morrow.sonarplugin.xsd.XsdTask;
import com.hello2morrow.sonarplugin.xsd.XsdTasks;

/**
 * @author Ingmar
 * 
 */
public class TaskProcessor implements IProcessor {

  private static final Logger LOG = LoggerFactory.getLogger(WarningProcessor.class);
  private static final String PACKAGE = " package";
  private SensorContext sensorContext;
  private RuleFinder ruleFinder;

  public TaskProcessor(final RuleFinder ruleFinder, final SensorContext sensorContext) {
    this.ruleFinder = ruleFinder;
    this.sensorContext = sensorContext;
  }

  /*
   * (non-Javadoc)
   * 
   * @see com.hello2morrow.sonarplugin.handler.IProcessor#process(com.hello2morrow.sonarplugin.xsd.ReportContext,
   * com.hello2morrow.sonarplugin.xsd.XsdAttributeRoot)
   */
  public void process(ReportContext report, XsdAttributeRoot buildUnit) {
    LOG.debug("Analysing tasks of buildUnit: " + buildUnit.getName());

    XsdTasks tasks = report.getTasks();
    Map<String, RulePriority> priorityMap = new HashMap<String, RulePriority>();

    Rule rule = ruleFinder.findByKey(SonargraphPluginBase.PLUGIN_KEY, SonargraphPluginBase.TASK_RULE_KEY);
    int count = 0;

    if (rule == null) {
      LOG.error("Sonargraph task rule not found");
      return;
    }

    priorityMap.put("Low", RulePriority.INFO);
    priorityMap.put("Medium", RulePriority.MINOR);
    priorityMap.put("High", RulePriority.MAJOR);

    for (XsdTask task : tasks.getTask()) {
      String bu = Utilities.getAttribute(task.getAttribute(), "Build unit");

      bu = Utilities.getBuildUnitName(bu);
      if (bu.equals(Utilities.getBuildUnitName(buildUnit.getName()))) {
        count = handleTask(priorityMap, rule, count, task);
      }
    }
    Utilities.saveMeasureToContext(sensorContext, SonargraphSimpleMetrics.TASK_REFS, count, 0);
  }

  private int handleTask(Map<String, RulePriority> priorityMap, Rule rule, final int count, final XsdTask task) {
    String priority = Utilities.getAttribute(task.getAttribute(), "Priority");
    String description = Utilities.getAttribute(task.getAttribute(), "Description");
    String assignedTo = Utilities.getAttribute(task.getAttribute(), "Assigned to");

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
          Utilities.saveViolation(sensorContext, rule, priorityMap.get(priority), fqName, line, description);
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
