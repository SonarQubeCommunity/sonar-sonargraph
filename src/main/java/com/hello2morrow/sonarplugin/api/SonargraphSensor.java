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

package com.hello2morrow.sonarplugin.api;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.configuration.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sonar.api.batch.Sensor;
import org.sonar.api.batch.SensorContext;
import org.sonar.api.measures.Measure;
import org.sonar.api.measures.Metric;
import org.sonar.api.resources.JavaFile;
import org.sonar.api.resources.JavaPackage;
import org.sonar.api.resources.Project;
import org.sonar.api.resources.Resource;
import org.sonar.api.rules.Rule;
import org.sonar.api.rules.RuleFinder;
import org.sonar.api.rules.RulePriority;
import org.sonar.api.rules.Violation;

import com.hello2morrow.sonarplugin.decorator.AlertDecorator;
import com.hello2morrow.sonarplugin.foundation.DuplicateCodeBlock;
import com.hello2morrow.sonarplugin.foundation.ReportFileReader;
import com.hello2morrow.sonarplugin.foundation.SonargraphPluginBase;
import com.hello2morrow.sonarplugin.foundation.Utilities;
import com.hello2morrow.sonarplugin.metric.SonargraphBuildUnitMetrics;
import com.hello2morrow.sonarplugin.metric.SonargraphSystemMetrics;
import com.hello2morrow.sonarplugin.xsd.ReportContext;
import com.hello2morrow.sonarplugin.xsd.XsdArchitectureViolation;
import com.hello2morrow.sonarplugin.xsd.XsdAttributeRoot;
import com.hello2morrow.sonarplugin.xsd.XsdBuildUnits;
import com.hello2morrow.sonarplugin.xsd.XsdCycleGroup;
import com.hello2morrow.sonarplugin.xsd.XsdCycleGroups;
import com.hello2morrow.sonarplugin.xsd.XsdCyclePath;
import com.hello2morrow.sonarplugin.xsd.XsdPosition;
import com.hello2morrow.sonarplugin.xsd.XsdTask;
import com.hello2morrow.sonarplugin.xsd.XsdTasks;
import com.hello2morrow.sonarplugin.xsd.XsdTypeRelation;
import com.hello2morrow.sonarplugin.xsd.XsdViolations;
import com.hello2morrow.sonarplugin.xsd.XsdWarning;
import com.hello2morrow.sonarplugin.xsd.XsdWarnings;
import com.hello2morrow.sonarplugin.xsd.XsdWarningsByAttribute;
import com.hello2morrow.sonarplugin.xsd.XsdWarningsByAttributeGroup;

public final class SonargraphSensor implements Sensor {

  private static final Logger LOG = LoggerFactory.getLogger(SonargraphSensor.class);

  private static final String REPORT_DIR = "sonargraph-sonar-plugin";
  private static final String REPORT_NAME = "sonargraph-sonar-report.xml";

  private static final String ACD = "AverageComponentDependency";
  private static final String NCCD = "NormalizedCumulativeComponentDependency";
  private static final String INTERNAL_PACKAGES = "NumberOfInternalNamespaces";
  private static final String INSTRUCTIONS = "NumberOfInstructions";
  private static final String UNASSIGNED_TYPES = "NumberOfNotAssignedTypes";
  private static final String VIOLATING_DEPENDENCIES = "NumberOfViolations";
  private static final String VIOLATING_REFERENCES = "NumberOfViolatingReferences";
  private static final String VIOLATING_TYPES = "NumberOfViolatingTypes";
  private static final String TYPE_DEPENDENCIES = "OverallNumberOfTypeDependencies";
  private static final String JAVA_FILES = "NumberOfSourceFiles";
  private static final String IGNORED_VIOLATIONS = "NumberOfIgnoredViolations";
  private static final String IGNORED_WARNINGS = "NumberOfIgnoredWarnings";
  private static final String TASKS = "NumberOfTasks";
  private static final String ALL_WARNINGS = "NumberOfWarnings";
  private static final String CYCLE_WARNINGS = "NumberOfCyclicWarnings";
  private static final String THRESHOLD_WARNINGS = "NumberOfMetricWarnings";
  private static final String WORKSPACE_WARNINGS = "NumberOfWorkspaceWarnings";
  private static final String DUPLICATE_WARNINGS = "NumberOfDuplicateCodeBlocksWarnings";
  private static final String EROSION_REFS = "StructuralErosionReferenceLevel";
  private static final String EROSION_TYPES = "StructuralErosionTypeLevel";
  private static final String INTERNAL_TYPES = "NumberOfInternalTypes";
  private static final String STUCTURAL_DEBT_INDEX = "StructuralDebtIndex";

  private final Map<String, Number> buildUnitMetrics = new HashMap<String, Number>();
  private SensorContext sensorContext;
  private final RuleFinder ruleFinder;

  private ReportContext report;

  public SonargraphSensor(RuleFinder ruleFinder, Configuration configuration) {
    this.ruleFinder = ruleFinder;
    if (ruleFinder == null) {
      LOG.warn("No RulesManager provided to sensor");
    }
  }

  public boolean shouldExecuteOnProject(Project project) {
    return true;
  }

  void setReport(ReportContext report) {
    this.report = report;
  }

  void setSensorContext(SensorContext context) {
    this.sensorContext = context;
  }

  public void analyse(final Project project, SensorContext sensorContext) {
    LOG.info("------------------------------------------------------------------------");
    LOG.info("Execute sonar-sonargraph-plugin for " + project.getName());
    LOG.info("------------------------------------------------------------------------");

    this.sensorContext = sensorContext;

    Configuration configuration = project.getConfiguration();

    if (Utilities.isRootParentProject(project)) {
      /* Nothing to analyse for parent project, only decorators are executed */
      return;
    }
      
    
    // This is needed to ease testing
    if (null == report) {
      /* Report has not been set - live system */
      String reportPath = this.getReportFileName(project.getFileSystem().getBuildDir().getPath(), configuration);
      LOG.info("Reading Sonargraph metrics report from: " + reportPath);
      report = ReportFileReader.readSonargraphReport(reportPath, project.isRoot());
    } else {
      /* Report has been set - test */
      LOG.info("Using Sonargraph metrics report: " + report.getName());
    }

    XsdAttributeRoot buildUnit = null;

    if (report != null) {
      buildUnit = retrieveBuildUnit(project.getKey(), report);
    }

    if (null == buildUnit) {
      LOG.error("No build units found in report file!");
      return;
    }

    LOG.info("Adding measures for " + project.getName());
    this.analyseBuildUnit(report, buildUnit);

    double indexCost = configuration.getDouble(SonargraphPluginBase.COST_PER_INDEX_POINT,
        SonargraphPluginBase.COST_PER_INDEX_POINT_DEFAULT);
    if (indexCost > 0) {
      double structuralDebtCost = sensorContext.getMeasure(SonargraphBuildUnitMetrics.EROSION_INDEX).getValue()
          * indexCost;
      saveMeasureToContext(SonargraphBuildUnitMetrics.EROSION_COST, structuralDebtCost, 0);
    }

    this.analyseSystemMeasures(report, null);
    this.analyseCylceGroups(report, buildUnit, project);

    if (hasBuildUnitMetric(UNASSIGNED_TYPES)) {
      LOG.info("Adding architecture measures for " + project.getName());
      this.addArchitectureMeasures(report, buildUnit);
    }

    // TODO: Under what conditions can the ruleFinder be null?
    if (ruleFinder != null) {
      this.handleArchitectureViolations(report, buildUnit);
      this.handleWarnings(report, buildUnit);
      this.handleTasks(report, buildUnit);
    } else {
      LOG.error("RuleFinder must be set in constructor!");
    }

    AlertDecorator.setAlertLevels(new SensorProjectContext(sensorContext));
  }


  XsdAttributeRoot retrieveBuildUnit(String projectKey, ReportContext report) {
    XsdBuildUnits buildUnits = report.getBuildUnits();
    List<XsdAttributeRoot> buildUnitList = buildUnits.getBuildUnit();

    if (buildUnitList.size() == 1) {
      return buildUnitList.get(0);
    } else if (buildUnitList.size() > 1) {

      for (XsdAttributeRoot sonarBuildUnit : buildUnitList) {
        String buName = Utilities.getBuildUnitName(sonarBuildUnit.getName());
        if (this.buildUnitMatchesAnalyzedProject(buName, projectKey)) {
          return sonarBuildUnit;
        }
      }

      LOG.warn("Project  with key [" + projectKey + "] could not be mapped to a build unit. "
          + "The project will not be analyzed. Check the build unit configuration of your Sonargraph system.");

      return null;
    } else {
      return null;
    }
  }

  void analyseBuildUnit(final ReportContext report, final XsdAttributeRoot buildUnit) {
    LOG.debug("Analysing buildUnit: " + buildUnit.getName());

    /** Load all attributes and values in map */
    Utilities.readAttributesToMap(buildUnit, buildUnitMetrics);

    Number internalPackages = this.getBuildUnitMetricValue(INTERNAL_PACKAGES);

    if (internalPackages.intValue() == 0) {
      LOG.warn("No packages found in buildUnit " + buildUnit.getName());
      return;
    }

    saveExistingMeasureToContext(INTERNAL_PACKAGES, SonargraphBuildUnitMetrics.INTERNAL_PACKAGES, 0);
    saveExistingMeasureToContext(ACD, SonargraphSystemMetrics.ACD, 1);
    saveExistingMeasureToContext(NCCD, SonargraphSystemMetrics.NCCD, 1);
    saveExistingMeasureToContext(INSTRUCTIONS, SonargraphBuildUnitMetrics.INSTRUCTIONS, 0);
    saveExistingMeasureToContext(JAVA_FILES, SonargraphBuildUnitMetrics.JAVA_FILES, 0);
    saveExistingMeasureToContext(TYPE_DEPENDENCIES, SonargraphBuildUnitMetrics.TYPE_DEPENDENCIES, 0);
    saveExistingMeasureToContext(EROSION_REFS, SonargraphBuildUnitMetrics.EROSION_REFS, 0);
    saveExistingMeasureToContext(EROSION_TYPES, SonargraphBuildUnitMetrics.EROSION_TYPES, 0);
    saveExistingMeasureToContext(STUCTURAL_DEBT_INDEX, SonargraphBuildUnitMetrics.EROSION_INDEX, 0).getValue();
  }

  void analyseCylceGroups(ReportContext report, XsdAttributeRoot buildUnit, Project project) {
    LOG.debug("Analysing cycleGroups of buildUnit: " + buildUnit.getName());

    XsdCycleGroups cycleGroups = report.getCycleGroups();
    double cyclicity = 0;
    double biggestCycleGroupSize = 0;
    double cyclicPackages = 0;

    for (XsdCycleGroup group : cycleGroups.getCycleGroup()) {
      if ("Physical package".equals(group.getNamedElementGroup())
          && getBuildUnitName(group).equals(Utilities.getBuildUnitName(buildUnit.getName()))) {
        int groupSize = group.getCyclePath().size();
        cyclicPackages += groupSize;
        cyclicity += groupSize * groupSize;
        if (groupSize > biggestCycleGroupSize) {
          biggestCycleGroupSize = groupSize;
        }
        handlePackageCycleGroup(group);
      }
    }

    saveMeasureToContext(SonargraphSystemMetrics.BIGGEST_CYCLE_GROUP, biggestCycleGroupSize, 0);
    saveMeasureToContext(SonargraphBuildUnitMetrics.CYCLICITY, cyclicity, 0);

    if (project.isRoot()) {
      /* Decorators are not executed for projects with single build unit, therefore we need this calculation here */
      double packages = sensorContext.getMeasure(SonargraphBuildUnitMetrics.INTERNAL_PACKAGES).getValue();
      if (packages > 0) {
        double relCyclicity = 100.0 * Math.sqrt(cyclicity) / packages;
        double relCyclicPackages = 100.0 * cyclicPackages / packages;
        saveMeasureToContext(SonargraphSystemMetrics.RELATIVE_CYCLICITY, relCyclicity, 0);
        saveMeasureToContext(SonargraphSystemMetrics.CYCLIC_PACKAGES_PERCENT, relCyclicPackages, 1);
      } else {
        saveMeasureToContext(SonargraphSystemMetrics.RELATIVE_CYCLICITY, 0, 0);
        saveMeasureToContext(SonargraphSystemMetrics.CYCLIC_PACKAGES_PERCENT, 0, 1);
      }
      
    }
    saveMeasureToContext(SonargraphBuildUnitMetrics.CYCLIC_PACKAGES, cyclicPackages, 0);
  }

  private String getBuildUnitName(XsdCycleGroup group) {
    if ("(Default Build Unit)".equals(group.getParent())) {
      return group.getElementScope();
    }
    return group.getParent();
  }

  void analyseSystemMeasures(ReportContext report, XsdAttributeRoot root) {
    LOG.debug("Analysing system metrics");

    Map<String, Number> systemAttributes = new HashMap<String, Number>();
    Utilities.readAttributesToMap(report.getAttributes(), systemAttributes);

    saveMeasureToContext(SonargraphSystemMetrics.ARCHITECTURE_VIOLATIONS,
        getMetricValueFromMap(VIOLATING_REFERENCES, systemAttributes), 0);
    saveMeasureToContext(SonargraphSystemMetrics.CYCLE_WARNINGS,
        getMetricValueFromMap(CYCLE_WARNINGS, systemAttributes), 0);
    saveMeasureToContext(SonargraphSystemMetrics.WORKSPACE_WARNINGS,
        getMetricValueFromMap(WORKSPACE_WARNINGS, systemAttributes), 0);
    saveMeasureToContext(SonargraphSystemMetrics.ALL_WARNINGS, getMetricValueFromMap(ALL_WARNINGS, systemAttributes), 0);
  }

  void addArchitectureMeasures(ReportContext report, XsdAttributeRoot buildUnit) {
    LOG.debug("Analysing architectural measures of build unit: " + buildUnit.getName());

    double types = saveExistingMeasureToContext(INTERNAL_TYPES, SonargraphBuildUnitMetrics.INTERNAL_TYPES, 0)
        .getValue();
    assert types >= 1.0 : "Project must not be empty !";

    Measure unassignedTypes = saveExistingMeasureToContext(UNASSIGNED_TYPES,
        SonargraphBuildUnitMetrics.UNASSIGNED_TYPES, 0);
    Measure violatingTypes = saveExistingMeasureToContext(VIOLATING_TYPES, SonargraphBuildUnitMetrics.VIOLATING_TYPES,
        0);

    // TODO: does it make sense to calculate them here for a build unit? Should it not be calculated on the overall project?
    double violatingTypesPercent = 100.0 * violatingTypes.getValue() / types;
    double unassignedTypesPercent = 100.0 * unassignedTypes.getValue() / types;
    saveMeasureToContext(SonargraphSystemMetrics.VIOLATING_TYPES_PERCENT, violatingTypesPercent, 1);
    saveMeasureToContext(SonargraphSystemMetrics.UNASSIGNED_TYPES_PERCENT, unassignedTypesPercent, 1);

    saveExistingMeasureToContext(VIOLATING_DEPENDENCIES, SonargraphBuildUnitMetrics.VIOLATING_DEPENDENCIES, 0);
    saveExistingMeasureToContext(TASKS, SonargraphBuildUnitMetrics.TASKS, 0);
    if (hasBuildUnitMetric(THRESHOLD_WARNINGS)) {
      saveExistingMeasureToContext(THRESHOLD_WARNINGS, SonargraphBuildUnitMetrics.THRESHOLD_WARNINGS, 0);
    }

    saveExistingMeasureToContext(IGNORED_VIOLATIONS, SonargraphBuildUnitMetrics.IGNORED_VIOLATONS, 0);
    saveExistingMeasureToContext(IGNORED_WARNINGS, SonargraphBuildUnitMetrics.IGNORED_WARNINGS, 0);
    if (hasBuildUnitMetric(DUPLICATE_WARNINGS)) {
      saveExistingMeasureToContext(DUPLICATE_WARNINGS, SonargraphBuildUnitMetrics.DUPLICATE_WARNINGS, 0);
    }
  }

  void handleArchitectureViolations(ReportContext report, XsdAttributeRoot buildUnit) {
    LOG.debug("Analysing architecture violation of buildUnit: " + buildUnit.getName());

    Rule rule = ruleFinder.findByKey(SonargraphPluginBase.PLUGIN_KEY, SonargraphPluginBase.ARCH_RULE_KEY);
    if (rule == null) {
      LOG.error("Sonargraph architecture rule not found");
      return;
    }
    XsdViolations violations = report.getViolations();
    String uses = "Uses ";
    for (XsdArchitectureViolation violation : violations.getArchitectureViolations()) {

      for (XsdTypeRelation rel : violation.getTypeRelation()) {
        String toType = Utilities.getAttribute(rel.getAttribute(), "To");
        String bu = Utilities.getAttribute(rel.getAttribute(), "From build unit");

        String dimension = violation.getDimension();
        String message = "";
        if (null != dimension) {
          message = dimension + " architecture violation: ";
        } else {
          message = "Architecture violation: ";
        }
        message = message + uses + toType;
        String explanation = "\nExplanation: " + Utilities.getAttribute(rel.getAttribute(), "Explanation");

        bu = Utilities.getBuildUnitName(bu);
        if (bu.equals(Utilities.getBuildUnitName(buildUnit.getName()))) {
          for (XsdPosition pos : rel.getPosition()) {
            String relFileName = pos.getFile();
            int line = 0;
            try {
              line = Integer.parseInt(pos.getLine());
            } catch (NumberFormatException ex) {
              LOG.error("Attribute \"line\" of element \"position\" is not a valid integer value: " + pos.getLine()
                  + ". Exception: " + ex.getMessage());
              continue;
            }
            if (relFileName != null && (pos.getType() != null) && (line > 0)) {
              String fqName = Utilities.relativeFileNameToFqName(relFileName);
              String msg = message + ". Usage type: " + pos.getType() + explanation;
              LOG.debug(msg);
              saveViolation(rule, null, fqName, Integer.valueOf(pos.getLine()), msg);
            }
          }
        }
      }
    }
  }

  void handleWarnings(ReportContext report, XsdAttributeRoot buildUnit) {
    LOG.debug("Analysing warnings of buildUnit: " + buildUnit.getName());

    XsdWarnings warnings = report.getWarnings();
    for (XsdWarningsByAttributeGroup warningGroup : warnings.getWarningsByAttributeGroup()) {

      String key = SonargraphPluginBase.getRuleKey(warningGroup.getAttributeGroup());
      if (key == null) {
        continue;
      }
      Rule rule = ruleFinder.findByKey(SonargraphPluginBase.PLUGIN_KEY, key);
      if (rule == null) {
        LOG.error("Sonargraph threshold rule not found");
        continue;
      }
      if ("Duplicate code".equals(warningGroup.getAttributeGroup())) {
        handleDuplicateCodeBlocks(warningGroup, rule);
        continue;
      }
      for (XsdWarningsByAttribute warningByAttribute : warningGroup.getWarningsByAttribute()) {
        String attrName = warningByAttribute.getAttributeName();

        for (XsdWarning warning : warningByAttribute.getWarning()) {
          String msg = attrName + "=" + Utilities.getAttribute(warning.getAttribute(), "Attribute value");
          String bu = Utilities.getAttribute(warning.getAttribute(), "Build unit");

          bu = Utilities.getBuildUnitName(bu);
          if (bu.equals(Utilities.getBuildUnitName(buildUnit.getName()))) {
            if (warning.getPosition().size() > 0) {
              for (XsdPosition pos : warning.getPosition()) {
                String relFileName = pos.getFile();

                if (relFileName != null) {
                  String fqName = Utilities.relativeFileNameToFqName(relFileName);
                  saveViolation(rule, null, fqName, Integer.valueOf(pos.getLine()), msg);
                }
              }
            } else {
              String elemType = Utilities.getAttribute(warning.getAttribute(), "Element type");

              if ("Class file".equals(elemType) || "Source file".equals(elemType)) {
                // Attach a violation at line 1
                String fileName = Utilities.getAttribute(warning.getAttribute(), "Element");
                String fqName = fileName.substring(0, fileName.lastIndexOf('.')).replace('/', '.');

                saveViolation(rule, null, fqName, 1, msg);
              }
            }
          }
        }
      }
    }
  }

  void handleDuplicateCodeBlocks(XsdWarningsByAttributeGroup warningGroup, Rule rule) {
    LOG.debug("Analysing duplicate code blocks");

    Map<Integer, List<DuplicateCodeBlock>> duplicateCodeBlocks = new HashMap<Integer, List<DuplicateCodeBlock>>();

    for (XsdWarningsByAttribute warnings : warningGroup.getWarningsByAttribute()) {
      for (XsdWarning warning : warnings.getWarning()) {
        DuplicateCodeBlock block = Utilities.createDuplicateCodeBlock(warning);
        if (null == block) {
          continue;
        }
        if ( !duplicateCodeBlocks.containsKey(block.getBlockId())) {
          duplicateCodeBlocks.put(block.getBlockId(), new ArrayList<DuplicateCodeBlock>());
        }
        duplicateCodeBlocks.get(block.getBlockId()).add(block);
      }
    }

    for (Entry<Integer, List<DuplicateCodeBlock>> entry : duplicateCodeBlocks.entrySet()) {
      for (DuplicateCodeBlock block : entry.getValue()) {
        String message = Utilities.generateDuplicateCodeBlockMessage(block, entry.getValue());
        String fqName = Utilities.relativeFileNameToFqName(block.getElementName());
        saveViolation(rule, null, fqName, block.getStartLine(), message);
      }
    }
  }

  void handleTasks(ReportContext report, XsdAttributeRoot buildUnit) {
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
        String priority = Utilities.getAttribute(task.getAttribute(), "Priority");
        String description = Utilities.getAttribute(task.getAttribute(), "Description");
        String assignedTo = Utilities.getAttribute(task.getAttribute(), "Assigned to");

        // This should not be needed, but the current description sucks
        description = handleDescription(description);

        int index = description.indexOf(" package");

        if (index > 0 && index < 8) {
          // Package refactorings won't get markers - this would
          // create to many non relevant markers
          count++;
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
              saveViolation(rule, priorityMap.get(priority), fqName, line, description);
            }
            count++;
          }
        }
      }
    }
    saveMeasureToContext(SonargraphBuildUnitMetrics.TASK_REFS, count, 0);
  }

  private boolean hasBuildUnitMetric(String key) {
    return buildUnitMetrics.get(key) != null;
  }

  private double getBuildUnitMetricValue(String key) {
    return getMetricValueFromMap(key, buildUnitMetrics);
  }

  private double getMetricValueFromMap(String key, Map<String, Number> metrics) {
    Number num = metrics.get(key);

    if (num == null) {
      LOG.error("Cannot find metric <" + key + "> in generated report");
      LOG.error("Probably you forgot to set the prepareForSonar option to true (see documentation)");
      return 0.0;
    }
    return num.doubleValue();
  }

  private Measure saveExistingMeasureToContext(String key, Metric metric, int precision) {
    double value = getBuildUnitMetricValue(key);

    return saveMeasureToContext(metric, value, precision);
  }

  private Measure saveMeasureToContext(Metric metric, double value, int precision) {
    Measure m = new Measure(metric, value, precision);
    sensorContext.saveMeasure(m);
    return m;
  }

  private void saveViolation(Rule rule, RulePriority priority, String fqName, int line, String msg) {
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

  @SuppressWarnings("unchecked")
  private void handlePackageCycleGroup(XsdCycleGroup group) {
    Rule rule = ruleFinder.findByKey(SonargraphPluginBase.PLUGIN_KEY, SonargraphPluginBase.CYCLE_GROUP_RULE_KEY);

    if (rule != null) {
      for (XsdCyclePath pathElement : group.getCyclePath()) {
        String fqName = pathElement.getParent();
        Resource<JavaPackage> javaPackage = sensorContext.getResource(new JavaPackage(fqName));

        if (javaPackage == null) {
          LOG.error("Cannot obtain resource " + fqName);
        } else {
          Violation v = Violation.create(rule, javaPackage);

          v.setMessage("Package participates in a cycle group");
          v.setLineId(null);
          sensorContext.saveViolation(v);
        }
      }
    }
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

  private boolean buildUnitMatchesAnalyzedProject(String buName, String projectKey) {
    final String[] elements = projectKey.split(":");
    assert elements.length >= 1 : "project.getKey() must not return an empty string";
    final String artifactId = elements[elements.length - 1];
    final String groupId = elements[0];
    final String longName = artifactId + "[" + groupId + "]";
    final String longName2 = groupId + ':' + artifactId;

    return buName.equalsIgnoreCase(artifactId) || buName.equalsIgnoreCase(longName)
        || buName.equalsIgnoreCase(longName2) || (buName.startsWith("...") && longName2.endsWith(buName.substring(2)));
  }

  private String getReportFileName(String projectBuildPath, Configuration config) {
    String defaultLocation = projectBuildPath + '/' + REPORT_DIR + '/' + REPORT_NAME;

    return config.getString("sonar.sonargraph.report.path", defaultLocation);
  }
}
