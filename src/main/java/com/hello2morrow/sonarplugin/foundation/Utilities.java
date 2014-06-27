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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sonar.api.batch.DecoratorContext;
import org.sonar.api.batch.SensorContext;
import org.sonar.api.batch.fs.FilePredicate;
import org.sonar.api.batch.fs.FileSystem;
import org.sonar.api.batch.fs.InputFile;
import org.sonar.api.component.ResourcePerspectives;
import org.sonar.api.issue.Issuable;
import org.sonar.api.issue.Issuable.IssueBuilder;
import org.sonar.api.issue.Issue;
import org.sonar.api.measures.Measure;
import org.sonar.api.measures.Metric;
import org.sonar.api.profiles.RulesProfile;
import org.sonar.api.resources.Project;
import org.sonar.api.resources.Resource;
import org.sonar.api.rules.ActiveRule;

import java.io.File;
import java.util.List;
import java.util.Map;

public final class Utilities {

  public static final String DEFAULT_BUILD_UNIT = "(Default Build Unit)";
  private static final String PROJECT_BUILDUNIT_SEPARATOR = "::";
  private static final String GROUP_ARTIFACT_SEPARATOR = ":";
  private static final String SPACE = " ";
  public static final String START_LINE = "Start line";
  public static final String ELEMENT = "Element";
  public static final String ATTRIBUTE_VALUE = "Attribute value";
  public static final String ELEMENT_TYPE = "Element type";
  public static final String BUILD_UNIT = "Build unit";
  public static final String PROJECT = "Project";
  public static final String BLOCK_ID = "Block id";
  private static final String SPACE_ENTITY = "&nbsp;";
  private static final String UNKNOWN = "<UNKNOWN>";
  public static final Logger LOG = LoggerFactory.getLogger(Utilities.class);

  private Utilities() {
  }

  public static String getBuildUnitName(String fqName) {
    if (fqName == null) {
      return UNKNOWN;
    }

    int projectSeparatorPos = fqName.indexOf(PROJECT_BUILDUNIT_SEPARATOR);
    if (projectSeparatorPos == -1) {
      return UNKNOWN;
    }

    String buName = fqName.substring(projectSeparatorPos + 2);

    if (DEFAULT_BUILD_UNIT.equals(buName)) {
      // Compatibility with old SonarJ versions
      buName = fqName.substring(0, projectSeparatorPos);
    }
    return buName;
  }

  @Deprecated
  public static String relativeFileNameToFqName(String fileName) {
    int lastDot = fileName.lastIndexOf('.');

    return fileName.substring(0, lastDot).replace('/', '.');
  }

  public static boolean isAggregationProject(DecoratorContext context, final Metric indicator) {
    return context.getChildrenMeasures(indicator).size() > 0;
  }

  public static boolean isAggregatingProject(final Project project) {
    if (project == null) {
      return false;
    }
    return project.getModules().size() > 0;
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

  public static String generateSpaceEntity(int numberOfSpaces) {
    StringBuffer buffer = new StringBuffer();
    for (int i = 0; i < numberOfSpaces; i++) {
      buffer.append(SPACE_ENTITY);
    }
    return buffer.toString();
  }

  public static String generateSpaces(int numberOfSpaces) {
    StringBuffer buffer = new StringBuffer();
    for (int i = 0; i < numberOfSpaces; i++) {
      buffer.append(SPACE);
    }
    return buffer.toString();
  }

  public static String generateDuplicateCodeBlockMessage(DuplicateCodeBlock block, List<DuplicateCodeBlock> blocks) {
    final int endLine = block.getBlockLength() + block.getStartLine() - 1;

    final StringBuilder message = new StringBuilder();
    message.append("Line ").append(block.getStartLine()).append(" to ").append(endLine).append(" is a duplicate of\n");
    int toBeDescribed = blocks.size() - 1;
    boolean isFirst = true;

    for (int i = 0; i < blocks.size(); i++) {
      DuplicateCodeBlock duplicate = blocks.get(i);
      if (duplicate == block) {
        continue;
      }
      int remaining = toBeDescribed - i;

      // No connection for first described element.
      if (!isFirst && blocks.size() > 2) {
        if (remaining > 0) {
          // Not last, and not first: enumerate.
          message.append(",\n");
        } else {
          // Last.
          if (toBeDescribed == 2) {
            // Just two parts.
            message.append(" and\n");
          } else {
            // More than two parts: Enumeration.
            message.append(", and\n");
          }
        }
      }
      isFirst = false;
      final int endLineDuplicate = duplicate.getBlockLength() + duplicate.getStartLine() - 1;
      message.append("line " + duplicate.getStartLine() + " to " + endLineDuplicate + " of "
        + duplicate.getElementName());
    }
    message.append(".");
    return message.toString();
  }

  public static void saveViolation(Project project, FileSystem fileSystem, ResourcePerspectives perspectives, ActiveRule rule, String priority, final String fqName, int line,
    String msg) {

    Resource resource = getResource(project, fileSystem, fqName);
    if (resource == null) {
      return;
    }

    Issuable issuable = perspectives.as(Issuable.class, resource);
    IssueBuilder issueBuilder = issuable.newIssueBuilder();
    if (line > 0) {
      issueBuilder.line(line);
    }
    if (priority != null && priority.trim().length() > 0) {
      issueBuilder.severity(priority);
    }
    else if (rule.getSeverity() != null)
    {
      issueBuilder.severity(rule.getSeverity().toString());
    }

    Issue issue = issueBuilder
      .ruleKey(rule.getRule().ruleKey())
      .message(msg)
      .build();
    issuable.addIssue(issue);
  }

  public static Resource getResource(Project project, FileSystem fileSystem, final String fqName) {
    final boolean isSourceFile = fqName.endsWith(".java");

    if (isSourceFile)
    {
      InputFile file = fileSystem.inputFile(new FilePredicate()
      {
        @Override
        public boolean apply(InputFile file) {
          LOG.error("Checking file: " + file.relativePath());
          return file.relativePath().endsWith(fqName);
        }
      });

      if (file == null) {
        LOG.error("Cannot obtain resource " + fqName);
        return null;
      }

      Resource resource = org.sonar.api.resources.File.fromIOFile(file.file(), project);
      if (resource == null) {
        LOG.error("Cannot obtain source file " + fqName);
      }
      return resource;
    }

    File dir = new File(fileSystem.baseDir(), fqName);
    Resource resource = org.sonar.api.resources.Directory.fromIOFile(dir, project);
    return resource;
  }

  public static void saveViolation(Project project, FileSystem fileSystem, ResourcePerspectives perspectives, ActiveRule rule, final String fqName,
    int line, String msg) {
    saveViolation(project, fileSystem, perspectives, rule, null, fqName, line, msg);
  }

  /**
   * Retrieves the metric from the build unit and saves it as a measure to the sensor context.
   *
   * @param sensorContext
   * @param metrics
   * @param key
   * @param metric
   * @param precision
   * @return the saved measure
   */
  public static Measure saveExistingMeasureToContext(SensorContext sensorContext, Map<String, Number> metrics,
    String key, Metric metric, int precision) {
    return saveExistingMeasureToContext(sensorContext, metrics, key, metric, precision, false);
  }

  /**
   * Retrieves the metric from the build unit and saves it as a measure to the sensor context.
   *
   * @param sensorContext
   * @param metrics
   * @param key
   * @param metric
   * @param precision
   * @return the saved measure
   */
  public static Measure saveExistingMeasureToContext(SensorContext sensorContext, Map<String, Number> metrics,
    String key, Metric metric, int precision, boolean flagMissingMetric) {
    double value = Utilities.getBuildUnitMetricValue(metrics, key, flagMissingMetric);

    return Utilities.saveMeasureToContext(sensorContext, metric, value, precision);
  }

  /**
   * Creates a new measure for the specified metric and saves it to the sensor context.
   *
   * @param sensorContext
   * @param metric
   * @param value
   * @param precision
   * @return
   */
  public static Measure saveMeasureToContext(SensorContext sensorContext, Metric metric, double value, int precision) {
    Measure m = new Measure(metric, value, precision);
    sensorContext.saveMeasure(m);
    return m;
  }

  /**
   * Creates a new measure for the specified metric and saves it to the sensor context.
   *
   * @param sensorContext
   * @param metric
   * @param value
   * @param precision
   * @param flagMissingMetric
   *          indicates if a logging statement should be generated if metric cannot be found
   * @return
   */
  public static double getBuildUnitMetricValue(Map<String, Number> metrics, String key, boolean flagMissingMetric) {
    Number num = metrics.get(key);

    if (flagMissingMetric && num == null) {
      LOG.error("Cannot find metric <" + key + "> in generated report");
      LOG.error("Make sure you set the prepareForSonar option to true (see documentation).");
      LOG.error("If you used Sonargraph Quality for report generation: "
        + "Check that your quality model used during snapshot generation contains the required Sonar metrics!");
    }

    if (num == null) {
      return 0.0;
    }

    return num.doubleValue();
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

  public static boolean isSonargraphProject(Project project, FileSystem moduleFileSystem, RulesProfile profile, List<Metric> sonargraphMetrics) {
    // Removed check for alert on sonargraph rule, since that API changed drastically
    return Java.isEnabled(moduleFileSystem) && (areSonargraphRulesActive(profile));
  }

  public static boolean areSonargraphRulesActive(RulesProfile profile) {
    return profile.getActiveRulesByRepository(SonargraphPluginBase.PLUGIN_KEY).size() > 0;
  }

}
