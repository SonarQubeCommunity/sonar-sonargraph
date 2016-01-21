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
package com.hello2morrow.sonarplugin.processor;

import com.hello2morrow.sonarplugin.foundation.AlertThreshold;
import com.hello2morrow.sonarplugin.foundation.SonarQubeUtilities;
import com.hello2morrow.sonarplugin.foundation.SonargraphPluginBase;
import com.hello2morrow.sonarplugin.foundation.SonargraphUtilities;
import com.hello2morrow.sonarplugin.metric.SonargraphAlertThresholds;
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
import org.sonar.api.batch.fs.InputDir;
import org.sonar.api.batch.fs.InputFile;
import org.sonar.api.batch.fs.InputPath;
import org.sonar.api.batch.rule.ActiveRule;
import org.sonar.api.measures.Metric;
import org.sonar.api.resources.Project;
import org.sonar.api.resources.Resource;

import java.io.Serializable;

/**
 * @author Ingmar
 *
 */
public class TaskProcessor implements IProcessor {

  private static final Logger LOG = LoggerFactory.getLogger(WarningProcessor.class);
  private static final String PACKAGE = " package";
  private final SensorContext sensorContext;
  private final Resource project;
  private final int tasks;

  public TaskProcessor(final Project project, final SensorContext sensorContext, final int tasks) {
    this.project = project;
    this.sensorContext = sensorContext;
    this.tasks = tasks;
  }

  /*
   * (non-Javadoc)
   * 
   * @see com.hello2morrow.sonarplugin.handler.IProcessor#process(com.hello2morrow.sonarplugin.xsd.ReportContext,
   * com.hello2morrow.sonarplugin.xsd.XsdAttributeRoot)
   */
  @Override
  public void process(final ReportContext report, final XsdAttributeRoot buildUnit) {
    LOG.debug("Analysing tasks of buildUnit: " + buildUnit.getName());

    final XsdTasks tasks = report.getTasks();

    final ActiveRule rule = SonarQubeUtilities.findActiveSonargraphRule(sensorContext, SonargraphPluginBase.TASK_RULE_KEY);
    int taskReferenceCount = 0;

    if (rule == null) {
      LOG.info("Sonargraph task rule not active in current profile");
      return;
    }

    for (final XsdTask task : tasks.getTask()) {
      final String buildUnitFromTask = SonargraphUtilities.getBuildUnitName(PersistenceUtilities.getAttribute(task.getAttribute(), "Build unit"));
      if (buildUnitFromTask.equals(SonargraphUtilities.getBuildUnitName(buildUnit.getName()))) {
        taskReferenceCount += handleTask(rule, task);
      }
    }

    final Metric<Serializable> connectedMetric = SonargraphAlertThresholds.getConnectedMetric(SonargraphSimpleMetrics.TASK_REFS);
    final AlertThreshold threshold = SonargraphAlertThresholds.getThreshold(connectedMetric != null ? connectedMetric : SonargraphSimpleMetrics.TASK_REFS);

    SonarQubeUtilities.saveMeasure(project, sensorContext, SonargraphSimpleMetrics.TASK_REFS, taskReferenceCount, threshold, this.tasks);
  }

  private int handleTask(final ActiveRule rule, final XsdTask task) {
    final String severity = SonargraphUtilities.convertToSeverity(PersistenceUtilities.getAttribute(task.getAttribute(), "Priority"));
    String description = PersistenceUtilities.getAttribute(task.getAttribute(), "Description");
    String assignedTo = PersistenceUtilities.getAttribute(task.getAttribute(), "Assigned to");

    // This should not be needed, but the current description sucks
    description = handleDescription(description);

    final int index = description.indexOf(PACKAGE);
    int counter = 0;

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
      for (final XsdPosition pos : task.getPosition()) {
        final String relFileName = pos.getFile();

        if (relFileName != null) {
          int line = Integer.valueOf(pos.getLine());
          if (line == 0) {
            line = 1;
          }
          final InputPath path = SonarQubeUtilities.getInputPath(sensorContext.fileSystem(), relFileName);
          if (path != null) {
            if (path.isFile()) {
              SonarQubeUtilities.saveViolation(sensorContext, (InputFile) path, rule, severity, line, description);
            } else {
              SonarQubeUtilities.saveViolation(sensorContext, (InputDir) path, rule, severity, description);
            }
          }
        }
        counter++;
      }
    }
    return counter;
  }

  private String handleDescription(final String descr) {
    if (descr.startsWith("Fix warning")) {
      // TODO: handle ascending metrics correctly (99% are descending)
      return "Reduce" + descr.substring(descr.indexOf(':') + 1).toLowerCase();
    }
    if (descr.startsWith("Cut type")) {
      final String toType = descr.substring(descr.indexOf("to "));
      return "Cut dependency " + toType;
    }
    if (descr.startsWith("Move type")) {
      final String to = descr.substring(descr.indexOf("to "));
      return "Move " + to;
    }
    return descr;
  }

}
