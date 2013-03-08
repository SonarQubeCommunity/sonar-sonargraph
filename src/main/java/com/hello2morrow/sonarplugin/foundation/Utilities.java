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

import java.text.ParseException;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sonar.api.batch.DecoratorContext;
import org.sonar.api.batch.SensorContext;
import org.sonar.api.measures.Measure;
import org.sonar.api.measures.Metric;
import org.sonar.api.profiles.Alert;
import org.sonar.api.profiles.RulesProfile;
import org.sonar.api.resources.Java;
import org.sonar.api.resources.JavaFile;
import org.sonar.api.resources.JavaPackage;
import org.sonar.api.resources.Project;
import org.sonar.api.resources.Resource;
import org.sonar.api.rules.ActiveRule;
import org.sonar.api.rules.RulePriority;
import org.sonar.api.rules.Violation;

import com.hello2morrow.sonarplugin.xsd.XsdAttribute;
import com.hello2morrow.sonarplugin.xsd.XsdAttributeCategory;
import com.hello2morrow.sonarplugin.xsd.XsdAttributeRoot;
import com.hello2morrow.sonarplugin.xsd.XsdCycleGroup;
import com.hello2morrow.sonarplugin.xsd.XsdWarning;

public final class Utilities {

  public static final String DEFAULT_BUILD_UNIT = "(Default Build Unit)";
  private static final String SPACE = " ";
  private static final String START_LINE = "Start line";
  private static final String ELEMENT = "Element";
  private static final String ATTRIBUTE_VALUE = "Attribute value";
  private static final String ELEMENT_TYPE = "Element type";
  private static final String BUILD_UNIT = "Build unit";
  private static final String PROJECT = "Project";
  private static final String BLOCK_ID = "Block id";
  private static final String SPACE_ENTITY = "&nbsp;";
  private static final String UNKNOWN = "<UNKNOWN>";
  private static final Logger LOG = LoggerFactory.getLogger(Utilities.class);

  private Utilities() {
  }

  public static String getAttribute(List<XsdAttribute> list, String name) {
    String value = null;

    for (XsdAttribute attr : list) {
      if (attr.getName().equals(name)) {
        value = attr.getValue();
        break;
      }
    }
    return value;
  }

  public static String getBuildUnitName(String fqName) {
    if (fqName == null) {
      return UNKNOWN;
    }

    int colonPos = fqName.indexOf("::");
    if (colonPos == -1) {
      return UNKNOWN;
    }

    String buName = fqName.substring(colonPos + 2);
    if (DEFAULT_BUILD_UNIT.equals(buName)) {
      // Compatibility with old SonarJ versions
      buName = fqName.substring(0, colonPos);
    }
    return buName;
  }

  public static String getBuildUnitName(XsdCycleGroup group) {
    if (DEFAULT_BUILD_UNIT.equals(group.getParent())) {
      return group.getElementScope();
    }
    return group.getParent();
  }

  public static String relativeFileNameToFqName(String fileName) {
    int lastDot = fileName.lastIndexOf('.');

    return fileName.substring(0, lastDot).replace('/', '.');
  }

  public static void readAttributesToMap(XsdAttributeRoot root, final Map<String, Number> attributeMap) {
    attributeMap.clear();

    for (XsdAttributeCategory cat : root.getAttributeCategory()) {
      for (XsdAttribute attr : cat.getAttribute()) {
        String attrName = attr.getStandardName();
        String value = attr.getValue();

        try {
          if (value.contains(".")) {
            attributeMap.put(attrName, SonargraphPluginBase.FLOAT_FORMAT.parse(value));
          } else {
            attributeMap.put(attrName, SonargraphPluginBase.INTEGER_FORMAT.parse(value));
          }
        } catch (ParseException e) {
          // Ignore this value
          LOG.error("Failed to parse value : " + value + ", " + e.getMessage());
        }
      }
    }
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

  public static DuplicateCodeBlock createDuplicateCodeBlock(XsdWarning warning) {
    DuplicateCodeBlock block = new DuplicateCodeBlock();
    String attribute = getAttribute(warning.getAttribute(), BLOCK_ID);
    if (null == attribute) {
      LOG.error("Duplicate code block warning does not contain the required attribute \"Block id\"");
      return null;
    }
    block.setBlockId(Integer.parseInt(attribute));
    block.setProjectName(getAttribute(warning.getAttribute(), PROJECT));
    block.setBuildUnitName(getAttribute(warning.getAttribute(), BUILD_UNIT));
    block.setElementType(getAttribute(warning.getAttribute(), ELEMENT_TYPE));

    String blockLength = getAttribute(warning.getAttribute(), ATTRIBUTE_VALUE);
    int pos = blockLength.indexOf(" lines");
    block.setBlockLength(Integer.parseInt(blockLength.substring(0, pos)));

    block.setElementName(getAttribute(warning.getAttribute(), ELEMENT));
    block.setStartLine(Integer.parseInt(getAttribute(warning.getAttribute(), START_LINE)));
    return block;
  }

  public static void addAttributeToList(List<XsdAttribute> attributeList, String name, String value) {
    XsdAttribute attribute = new XsdAttribute();
    attribute.setName(name);
    attribute.setValue(value);
    attributeList.add(attribute);
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
      if ( !isFirst && blocks.size() > 2) {
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

  public static void saveViolation(SensorContext sensorContext, ActiveRule rule, RulePriority priority, String fqName,
      int line, String msg) {
    Resource<JavaPackage> javaFile = sensorContext.getResource(new JavaFile(fqName));

    if (javaFile == null) {
      LOG.error("Cannot obtain resource " + fqName);
    } else {
      Violation v = Violation.create(rule, javaFile);

      v.setMessage(msg);
      if (line == 0) {
        v.setLineId(null);
      } else {
        v.setLineId(line);
      }
      if (priority != null) {
        v.setSeverity(priority);
      }
      sensorContext.saveViolation(v);
    }
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
    final String[] elements = project.getKey().split(":");
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

  public static boolean isSonargraphProject(Project project, RulesProfile profile, List<Metric> sonargraphMetrics) {
    List<Alert> alerts = profile.getAlerts();
    boolean sonargraphAlertFound = false;
    for (Alert alert : alerts) {
      if (sonargraphMetrics.contains(alert.getMetric())) {
        sonargraphAlertFound = true;
        break;
      }
    }

    return isJavaProject(project) && (areSonargraphRulesActive(profile) || sonargraphAlertFound);
  }

  public static boolean areSonargraphRulesActive(RulesProfile profile) {
    return profile.getActiveRulesByRepository(SonargraphPluginBase.PLUGIN_KEY).size() > 0;
  }

  public static boolean isJavaProject(Project project) {
    if (project.getLanguage() == null || !project.getLanguage().equals(Java.INSTANCE)) {
      return false;
    }
    return true;
  }

}
