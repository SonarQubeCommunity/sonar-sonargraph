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
package com.hello2morrow.sonarplugin.foundation;

import com.hello2morrow.sonarplugin.metric.SonargraphAlertThresholds;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sonar.api.batch.SensorContext;
import org.sonar.api.batch.fs.FilePredicate;
import org.sonar.api.batch.fs.FileSystem;
import org.sonar.api.batch.fs.InputDir;
import org.sonar.api.batch.fs.InputFile;
import org.sonar.api.batch.fs.InputPath;
import org.sonar.api.batch.fs.TextRange;
import org.sonar.api.batch.rule.ActiveRule;
import org.sonar.api.batch.sensor.issue.NewIssue;
import org.sonar.api.batch.sensor.issue.NewIssueLocation;
import org.sonar.api.measures.Measure;
import org.sonar.api.measures.Metric;
import org.sonar.api.measures.Metric.ValueType;
import org.sonar.api.resources.Project;
import org.sonar.api.resources.Resource;

import java.io.File;
import java.io.Serializable;
import java.util.Collection;
import java.util.List;
import java.util.Map;

public class SonarQubeUtilities {

  private static final Logger LOG = LoggerFactory.getLogger(SonarQubeUtilities.class);

  private static final String SOURCE_FILE_NOT_FOUND_MESSAGE = "Cannot obtain source file ";
  private static final String GROUP_ARTIFACT_SEPARATOR = ":";

  public static final Double TRUE = 1.0;
  public static final Double FALSE = 0.0;
  private static final int NO_DECIMAL = 0;
  private static final int DECIMAL = 1;

  private SonarQubeUtilities() {
    // do not instantiate
  }

  public static double saveMeasure(Resource resource, SensorContext context, Map<String, Number> metrics, String sonargraphMetricName, Metric<Serializable> sonarQubeMetric) {
    final double value = SonargraphUtilities.getBuildUnitMetricValue(metrics, sonargraphMetricName, true).doubleValue();
    return saveMeasure(resource, context, sonarQubeMetric, value);
  }

  public static double saveMeasure(Resource resource, SensorContext context, Metric<Serializable> sonarQubeMetric, double value) {
    return saveMeasure(resource, context, sonarQubeMetric, value, null, -1);
  }

  public static double saveMeasure(Resource resource, SensorContext context, Metric<Serializable> sonarQubeMetric, double value, Metric<Serializable> connectedThresholdMetric,
    double thresholdMetricValue) {
    final Measure<Serializable> measure = new Measure<>(sonarQubeMetric, value, sonarQubeMetric.getType() == ValueType.INT ? NO_DECIMAL : DECIMAL);
    final Metric<Serializable> metricForThreshold = connectedThresholdMetric != null ? connectedThresholdMetric : sonarQubeMetric;
    final AlertThreshold threshold = SonargraphAlertThresholds.getThreshold(metricForThreshold);
    if (threshold != null && thresholdMetricValue >= 0) {
      measure.setAlertStatus(threshold.getLevel(value));
      measure.setAlertText(sonarQubeMetric.getKey());
    }
    context.saveMeasure(resource, sonarQubeMetric, value);
    return value;
  }

  public static void saveViolation(org.sonar.api.batch.SensorContext context, InputFile file, org.sonar.api.batch.rule.ActiveRule rule, String priority, int line, String msg) {
    NewIssue newIssue = context.newIssue();
    TextRange textRange = file.newRange(line, 0, line + 1, 0);
    NewIssueLocation location = newIssue.newLocation().on(file).at(textRange).message(msg);
    newIssue.at(location).forRule(rule.ruleKey());
    // TODO: Handle severity
    newIssue.save();
  }

  public static void saveViolation(org.sonar.api.batch.sensor.SensorContext context, InputDir dir, org.sonar.api.batch.rule.ActiveRule rule, String priority, String msg) {
    NewIssue newIssue = context.newIssue();
    NewIssueLocation location = newIssue.newLocation().on(dir).message(msg);
    newIssue.at(location).forRule(rule.ruleKey());
    // TODO: Handle severity
    newIssue.save();
  }

  public static InputPath getInputPath(FileSystem fileSystem, final String fqName) {
    final boolean isSourceFile = fqName.endsWith(".java");
    if (isSourceFile) {
      InputFile file = fileSystem.inputFile(new FilePredicate() {
        @Override
        public boolean apply(InputFile file) {
          return file.relativePath().endsWith(fqName);
        }
      });
      if (file == null) {
        LOG.error(SOURCE_FILE_NOT_FOUND_MESSAGE + fqName);
        return null;
      }
      return file;
    }

    File dir = new File(fileSystem.baseDir(), fqName);
    if (!dir.exists()) {
      return null;
    }
    return fileSystem.inputDir(dir);
  }

  public static ActiveRule findActiveSonargraphRule(SensorContext sensorContext, String ruleKey) {
    Collection<ActiveRule> rules = sensorContext.activeRules().findByRepository(SonargraphPluginBase.PLUGIN_KEY);
    ActiveRule rule = null;
    for (ActiveRule next : rules) {
      if (next.ruleKey().rule().equals(ruleKey)) {
        rule = next;
        break;
      }
    }
    return rule;
  }

  public static boolean isAggregatingProject(final Project project) {
    if (project == null) {
      return false;
    }
    return !project.getModules().isEmpty();
  }

  public static boolean isRootParentProject(final Project project) {
    boolean isRootParentProject = false;
    if (project == null) {
      return false;
    }
    List<Project> modules = project.getModules();
    if (project.getParent() == null && modules != null && !modules.isEmpty()) {
      isRootParentProject = true;
    }
    return isRootParentProject;
  }

  public static boolean isSingleModuleProject(final Project project) {
    if (project == null) {
      return false;
    }

    if (project.getModules().isEmpty() && project.getParent() == null) {
      return true;
    }
    return false;
  }

  public static boolean isSonargraphProject(SensorContext context) {
    // Removed check for alert on sonargraph rule, since that API changed drastically
    return Java.isEnabled(context.fileSystem()) && (areSonargraphRulesActive(context));
  }

  public static boolean areSonargraphRulesActive(SensorContext context) {
    return !context.activeRules().findByRepository(SonargraphPluginBase.PLUGIN_KEY).isEmpty();
  }

  public static boolean buildUnitMatchesAnalyzedProject(String buName, Project project) {
    final boolean isBranch = project.getBranch() != null && project.getBranch().length() > 0;
    final String[] elements = project.key().split(GROUP_ARTIFACT_SEPARATOR);
    assert elements.length >= 1 : "project.getKey() must not return an empty string";

    boolean result = false;

    final String groupId = elements[0];
    String artifactId = elements[elements.length - 1];
    /**
     * We need this check to support sonar.branch functionality. Branch tags are appended to the project key
     * <group-id>:<artifact-id>:<branch-tag>
     */
    if (isBranch) {
      artifactId = elements[elements.length - 2];
    }

    final String longName = artifactId + "[" + groupId + "]";
    final String longName2 = groupId + ':' + artifactId;

    if (buName.equalsIgnoreCase(artifactId)) {
      result = true;
    }
    if (buName.equalsIgnoreCase(longName)) {
      result = true;
    }
    if (buName.equalsIgnoreCase(longName2)) {
      result = true;
    }

    if (buName.startsWith("...") && longName2.endsWith(buName.substring(2))) {
      result = true;
    }

    return result;
  }

}
