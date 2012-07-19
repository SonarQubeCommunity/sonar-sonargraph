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

import org.apache.commons.configuration.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sonar.api.batch.Sensor;
import org.sonar.api.batch.SensorContext;
import org.sonar.api.measures.Measure;
import org.sonar.api.resources.JavaPackage;
import org.sonar.api.resources.Project;
import org.sonar.api.resources.Resource;
import org.sonar.api.rules.Rule;
import org.sonar.api.rules.RuleFinder;
import org.sonar.api.rules.Violation;

import com.hello2morrow.sonarplugin.decorator.AlertDecorator;
import com.hello2morrow.sonarplugin.foundation.ReportFileReader;
import com.hello2morrow.sonarplugin.foundation.SonargraphStandaloneMetricNames;
import com.hello2morrow.sonarplugin.foundation.SonargraphPluginBase;
import com.hello2morrow.sonarplugin.foundation.Utilities;
import com.hello2morrow.sonarplugin.metric.SonargraphDerivedMetrics;
import com.hello2morrow.sonarplugin.metric.SonargraphSimpleMetrics;
import com.hello2morrow.sonarplugin.metric.internal.SonargraphInternalMetrics;
import com.hello2morrow.sonarplugin.processor.ArchitectureViolationProcessor;
import com.hello2morrow.sonarplugin.processor.IProcessor;
import com.hello2morrow.sonarplugin.processor.TaskProcessor;
import com.hello2morrow.sonarplugin.processor.WarningProcessor;
import com.hello2morrow.sonarplugin.xsd.ReportContext;
import com.hello2morrow.sonarplugin.xsd.XsdAttributeRoot;
import com.hello2morrow.sonarplugin.xsd.XsdBuildUnits;
import com.hello2morrow.sonarplugin.xsd.XsdCycleGroup;
import com.hello2morrow.sonarplugin.xsd.XsdCycleGroups;
import com.hello2morrow.sonarplugin.xsd.XsdCyclePath;

/**
 * The Sonargraph-Sonar report is only generated for the sub-modules, not for the aggregating project. Therefore some dashboard metrics that
 * cannot be calculated as the sum of the child project metrics have to be
 * 
 * 
 * @author Ingmar
 * 
 */
public final class SonargraphSensor implements Sensor {

  private static final int NO_DECIMAL = 0;

  public static final Logger LOG = LoggerFactory.getLogger(SonargraphSensor.class);

  private static final String REPORT_DIR = "sonargraph-sonar-plugin";
  private static final String REPORT_NAME = "sonargraph-sonar-report.xml";

  private final Map<String, Number> metrics = new HashMap<String, Number>();
  private SensorContext sensorContext;
  private final RuleFinder ruleFinder;

  private ReportContext report;

  public SonargraphSensor(RuleFinder ruleFinder) {
    this.ruleFinder = ruleFinder;
    if (ruleFinder == null) {
      LOG.warn("No RulesManager provided to sensor");
    }
  }

  /* called from maven */
  public boolean shouldExecuteOnProject(Project project) {
    return true;
  }

  /* package access to ease testing */
  void setReport(ReportContext report) {
    this.report = report;
  }

  /* package access to ease testing */
  void setSensorContext(SensorContext context) {
    this.sensorContext = context;
  }

  public void analyse(final Project project, SensorContext sensorContext) {
    if (null == project || null == sensorContext) {
      LOG.error("Major error calling Sonargraph Sonar Plugin: Project and / or sensorContext are null. "
          + "Please check your project configuration!");
      return;
    }

    if (Utilities.isRootParentProject(project)) {
      LOG.debug("There is no Sonargraph Sonar report generated for aggregating maven modules.");
      return;
    }
    LOG.info("------------------------------------------------------------------------");
    LOG.info("Execute sonar-sonargraph-plugin for " + project.getName());
    LOG.info("------------------------------------------------------------------------");

    this.sensorContext = sensorContext;
    Configuration configuration = project.getConfiguration();
    this.metrics.clear();

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

    XsdAttributeRoot buildUnit = retrieveBuildUnit(project.getKey(), report);

    if (buildUnit == null) {
      LOG.error("No Sonargraph build units found in report for [" + project.getName()
          + "]. Module will not be processed by Sonargraph!");
      Measure m = new Measure(SonargraphInternalMetrics.MODULE_NOT_PART_OF_SONARGRAPH_WORKSPACE);
      sensorContext.saveMeasure(m);
      return;
    }

    LOG.debug("Analysing buildUnit: " + buildUnit.getName());

    /** Load all attributes and values in map */
    Utilities.readAttributesToMap(buildUnit, metrics);

    Number numberOfStatements = metrics.get("NumberOfStatements");
    if (numberOfStatements == null || numberOfStatements.intValue() < 1) {
      LOG.warn("No code to be analysed in [" + project.getName() + "]. Module will not be processed by Sonargraph!");
      Measure m = new Measure(SonargraphInternalMetrics.MODULE_NOT_PART_OF_SONARGRAPH_WORKSPACE);
      sensorContext.saveMeasure(m);
      return;
    }

    this.analyseBuildUnit(report, buildUnit);
    this.analyseStructuralCostMetrics(configuration);
    this.analyseCylceGroups(report, buildUnit, project);
    this.analyseArchitectureMeasures(project, report, buildUnit);
    this.analyseViolationsWarningsTasks(report, sensorContext, ruleFinder, buildUnit);
    this.analyseDashboardMetricsForRootProject(report, project);

    AlertDecorator.setAlertLevels(new SensorProjectContext(sensorContext));
  }

  /* package access for testing */
  XsdAttributeRoot retrieveBuildUnit(String projectKey, ReportContext report) {
    if (null == report) {
      return null;
    }

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
    }
    return null;
  }

  private void analyseViolationsWarningsTasks(ReportContext report, SensorContext sensorContext, RuleFinder ruleFinder,
      XsdAttributeRoot buildUnit) {
    // TODO: Under what conditions can the ruleFinder be null?
    if (ruleFinder != null) {
      IProcessor architectureViolationHandler = new ArchitectureViolationProcessor(ruleFinder, sensorContext);
      architectureViolationHandler.process(report, buildUnit);
      IProcessor warningProcessor = new WarningProcessor(ruleFinder, sensorContext);
      warningProcessor.process(report, buildUnit);
      IProcessor taskProcessor = new TaskProcessor(ruleFinder, sensorContext);
      taskProcessor.process(report, buildUnit);
    } else {
      LOG.error("RuleFinder must be set in constructor!");
    }
  }

  private void analyseStructuralCostMetrics(Configuration configuration) {
    double indexCost = configuration.getDouble(SonargraphPluginBase.COST_PER_INDEX_POINT,
        SonargraphPluginBase.COST_PER_INDEX_POINT_DEFAULT);
    if (indexCost > 0) {
      Measure erosionIndex = this.sensorContext.getMeasure(SonargraphSimpleMetrics.EROSION_INDEX);
      double structuralDebtCost = 0;
      if (null != erosionIndex) {
        structuralDebtCost = erosionIndex.getValue() * indexCost;
      }
      Utilities.saveMeasureToContext(sensorContext, SonargraphSimpleMetrics.EROSION_COST, structuralDebtCost,
          NO_DECIMAL);
      Utilities.saveExistingMeasureToContext(sensorContext, metrics, SonargraphStandaloneMetricNames.TASKS,
          SonargraphSimpleMetrics.TASKS, NO_DECIMAL);
    }
  }

  /* package access to ease testing */
  void analyseBuildUnit(final ReportContext report, final XsdAttributeRoot buildUnit) {
    Number internalPackages = Utilities.getBuildUnitMetricValue(metrics,
        SonargraphStandaloneMetricNames.INTERNAL_PACKAGES);
    Utilities.saveExistingMeasureToContext(sensorContext, metrics, SonargraphStandaloneMetricNames.INTERNAL_PACKAGES,
        SonargraphSimpleMetrics.INTERNAL_PACKAGES, NO_DECIMAL);

    if (internalPackages.intValue() == 0) {
      LOG.warn("No packages found in buildUnit " + buildUnit.getName());
      return;
    }

    Utilities.saveExistingMeasureToContext(sensorContext, metrics, SonargraphStandaloneMetricNames.ACD,
        SonargraphSimpleMetrics.ACD, 1);
    Utilities.saveExistingMeasureToContext(sensorContext, metrics, SonargraphStandaloneMetricNames.NCCD,
        SonargraphSimpleMetrics.NCCD, 1);
    Utilities.saveExistingMeasureToContext(sensorContext, metrics, SonargraphStandaloneMetricNames.INSTRUCTIONS,
        SonargraphSimpleMetrics.INSTRUCTIONS, NO_DECIMAL);
    Utilities.saveExistingMeasureToContext(sensorContext, metrics, SonargraphStandaloneMetricNames.JAVA_FILES,
        SonargraphSimpleMetrics.JAVA_FILES, NO_DECIMAL);
    Utilities.saveExistingMeasureToContext(sensorContext, metrics, SonargraphStandaloneMetricNames.TYPE_DEPENDENCIES,
        SonargraphSimpleMetrics.TYPE_DEPENDENCIES, NO_DECIMAL);
    Utilities.saveExistingMeasureToContext(sensorContext, metrics, SonargraphStandaloneMetricNames.EROSION_REFS,
        SonargraphSimpleMetrics.EROSION_REFS, NO_DECIMAL);
    Utilities.saveExistingMeasureToContext(sensorContext, metrics, SonargraphStandaloneMetricNames.EROSION_TYPES,
        SonargraphSimpleMetrics.EROSION_TYPES, NO_DECIMAL);
    Utilities.saveExistingMeasureToContext(sensorContext, metrics,
        SonargraphStandaloneMetricNames.STUCTURAL_DEBT_INDEX, SonargraphSimpleMetrics.EROSION_INDEX, NO_DECIMAL);
  }

  private void analyseCylceGroups(ReportContext report, XsdAttributeRoot buildUnit, Project project) {
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

    Utilities.saveMeasureToContext(sensorContext, SonargraphDerivedMetrics.BIGGEST_CYCLE_GROUP, biggestCycleGroupSize,
        NO_DECIMAL);
    Utilities.saveMeasureToContext(sensorContext, SonargraphSimpleMetrics.CYCLICITY, cyclicity, NO_DECIMAL);

    double packages = sensorContext.getMeasure(SonargraphSimpleMetrics.INTERNAL_PACKAGES).getValue();
    if (packages > 0) {
      double relCyclicity = 100.0 * Math.sqrt(cyclicity) / packages;
      double relCyclicPackages = 100.0 * cyclicPackages / packages;
      Utilities.saveMeasureToContext(sensorContext, SonargraphDerivedMetrics.RELATIVE_CYCLICITY, relCyclicity,
          NO_DECIMAL);
      Utilities.saveMeasureToContext(sensorContext, SonargraphDerivedMetrics.CYCLIC_PACKAGES_PERCENT,
          relCyclicPackages, 1);
    } else {
      Utilities.saveMeasureToContext(sensorContext, SonargraphDerivedMetrics.RELATIVE_CYCLICITY, 0, NO_DECIMAL);
      Utilities.saveMeasureToContext(sensorContext, SonargraphDerivedMetrics.CYCLIC_PACKAGES_PERCENT, 0, 1);
    }

    Utilities.saveMeasureToContext(sensorContext, SonargraphSimpleMetrics.CYCLIC_PACKAGES, cyclicPackages, NO_DECIMAL);
  }

  private String getBuildUnitName(XsdCycleGroup group) {
    if ("(Default Build Unit)".equals(group.getParent())) {
      return group.getElementScope();
    }
    return group.getParent();
  }

  private void analyseArchitectureMeasures(Project project, ReportContext report, XsdAttributeRoot buildUnit) {
    LOG.debug("Analysing architectural measures of build unit: " + buildUnit.getName());
    if ( !hasBuildUnitMetric(metrics, SonargraphStandaloneMetricNames.UNASSIGNED_TYPES)) {
      LOG.info("No architecture measures found for build unit: " + buildUnit.getName());
      return;
    }
    double types = Utilities.saveExistingMeasureToContext(sensorContext, metrics,
        SonargraphStandaloneMetricNames.INTERNAL_TYPES, SonargraphSimpleMetrics.INTERNAL_TYPES, NO_DECIMAL).getValue();
    assert types >= 1.0 : "Project must not be empty !";

    Utilities.saveExistingMeasureToContext(sensorContext, metrics,
        SonargraphStandaloneMetricNames.VIOLATING_DEPENDENCIES, SonargraphSimpleMetrics.VIOLATING_TYPE_DEPENDENCIES,
        NO_DECIMAL);
    Measure violatingTypes = Utilities.saveExistingMeasureToContext(sensorContext, metrics,
        SonargraphStandaloneMetricNames.VIOLATING_TYPES, SonargraphSimpleMetrics.VIOLATING_TYPES, NO_DECIMAL);
    double violatingTypesPercent = 100.0 * violatingTypes.getValue() / types;
    Utilities.saveMeasureToContext(sensorContext, SonargraphDerivedMetrics.VIOLATING_TYPES_PERCENT,
        violatingTypesPercent, 1);
    Utilities.saveExistingMeasureToContext(sensorContext, metrics,
        SonargraphStandaloneMetricNames.VIOLATING_REFERENCES, SonargraphSimpleMetrics.VIOLATING_REFERENCES, NO_DECIMAL);
    Utilities.saveExistingMeasureToContext(sensorContext, metrics, SonargraphStandaloneMetricNames.IGNORED_VIOLATIONS,
        SonargraphSimpleMetrics.IGNORED_VIOLATONS, NO_DECIMAL);
    Measure unassignedTypes = Utilities.saveExistingMeasureToContext(sensorContext, metrics,
        SonargraphStandaloneMetricNames.UNASSIGNED_TYPES, SonargraphSimpleMetrics.UNASSIGNED_TYPES, NO_DECIMAL);
    double unassignedTypesPercent = 100.0 * unassignedTypes.getValue() / types;
    Utilities.saveMeasureToContext(sensorContext, SonargraphDerivedMetrics.UNASSIGNED_TYPES_PERCENT,
        unassignedTypesPercent, 1);

    if (hasBuildUnitMetric(metrics, SonargraphStandaloneMetricNames.DUPLICATE_WARNINGS)) {
      Utilities.saveExistingMeasureToContext(sensorContext, metrics,
          SonargraphStandaloneMetricNames.DUPLICATE_WARNINGS, SonargraphSimpleMetrics.DUPLICATE_WARNINGS, NO_DECIMAL);
    }

    if (Utilities.isSingleModuleProject(project)) {
      LOG.debug("Skipping the processing of dashboard warning metrics, as they are handled separately.");
      return;
    }
    Utilities.saveExistingMeasureToContext(sensorContext, metrics, SonargraphStandaloneMetricNames.ALL_WARNINGS,
        SonargraphSimpleMetrics.ALL_WARNINGS, NO_DECIMAL);
    Utilities.saveExistingMeasureToContext(sensorContext, metrics, SonargraphStandaloneMetricNames.CYCLE_WARNINGS,
        SonargraphSimpleMetrics.CYCLE_WARNINGS, NO_DECIMAL);

    Utilities.saveExistingMeasureToContext(sensorContext, metrics, SonargraphStandaloneMetricNames.WORKSPACE_WARNINGS,
        SonargraphSimpleMetrics.WORKSPACE_WARNINGS, NO_DECIMAL);
    if (hasBuildUnitMetric(metrics, SonargraphStandaloneMetricNames.THRESHOLD_WARNINGS)) {
      Utilities.saveExistingMeasureToContext(sensorContext, metrics,
          SonargraphStandaloneMetricNames.THRESHOLD_WARNINGS, SonargraphSimpleMetrics.THRESHOLD_WARNINGS, NO_DECIMAL);
    }
    Utilities.saveExistingMeasureToContext(sensorContext, metrics, SonargraphStandaloneMetricNames.IGNORED_WARNINGS,
        SonargraphSimpleMetrics.IGNORED_WARNINGS, NO_DECIMAL);
  }

  private void analyseDashboardMetricsForRootProject(ReportContext report, Project project) {
    XsdAttributeRoot attributesPart = report.getAttributes();
    Map<String, Number> metrics = new HashMap<String, Number>(70);
    Utilities.readAttributesToMap(attributesPart, metrics);

    if (Utilities.isSingleModuleProject(project)) {
      Utilities.saveExistingMeasureToContext(sensorContext, metrics, SonargraphStandaloneMetricNames.ALL_WARNINGS,
          SonargraphSimpleMetrics.ALL_WARNINGS, NO_DECIMAL);
      Utilities.saveExistingMeasureToContext(sensorContext, metrics, SonargraphStandaloneMetricNames.CYCLE_WARNINGS,
          SonargraphSimpleMetrics.CYCLE_WARNINGS, NO_DECIMAL);
      Utilities.saveExistingMeasureToContext(sensorContext, metrics,
          SonargraphStandaloneMetricNames.THRESHOLD_WARNINGS, SonargraphSimpleMetrics.THRESHOLD_WARNINGS, NO_DECIMAL);
      Utilities.saveExistingMeasureToContext(sensorContext, metrics,
          SonargraphStandaloneMetricNames.WORKSPACE_WARNINGS, SonargraphSimpleMetrics.WORKSPACE_WARNINGS, NO_DECIMAL);
      Utilities.saveExistingMeasureToContext(sensorContext, metrics, SonargraphStandaloneMetricNames.IGNORED_WARNINGS,
          SonargraphSimpleMetrics.IGNORED_WARNINGS, NO_DECIMAL);
      return;
    }

    /*
     * Save values to internal metrics. The decorator executed on the root parent module will retrieve them and store them in the visible
     * metrics.
     */
    Utilities.saveExistingMeasureToContext(sensorContext, metrics, SonargraphStandaloneMetricNames.ALL_WARNINGS,
        SonargraphInternalMetrics.SYSTEM_ALL_WARNINGS, NO_DECIMAL);
    Utilities.saveExistingMeasureToContext(sensorContext, metrics, SonargraphStandaloneMetricNames.CYCLE_WARNINGS,
        SonargraphInternalMetrics.SYSTEM_CYCLE_WARNINGS, NO_DECIMAL);
    Utilities.saveExistingMeasureToContext(sensorContext, metrics, SonargraphStandaloneMetricNames.THRESHOLD_WARNINGS,
        SonargraphInternalMetrics.SYSTEM_THRESHOLD_WARNINGS, NO_DECIMAL);
    Utilities.saveExistingMeasureToContext(sensorContext, metrics, SonargraphStandaloneMetricNames.WORKSPACE_WARNINGS,
        SonargraphInternalMetrics.SYSTEM_WORKSPACE_WARNINGS, NO_DECIMAL);
    Utilities.saveExistingMeasureToContext(sensorContext, metrics, SonargraphStandaloneMetricNames.IGNORED_WARNINGS,
        SonargraphInternalMetrics.SYSTEM_IGNORED_WARNINGS, NO_DECIMAL);
  }

  private boolean hasBuildUnitMetric(Map<String, Number> metrics, String key) {
    return metrics.get(key) != null;
  }

  @SuppressWarnings("unchecked")
  private void handlePackageCycleGroup(XsdCycleGroup group) {
    Rule rule = ruleFinder.findByKey(SonargraphPluginBase.PLUGIN_KEY, SonargraphPluginBase.CYCLE_GROUP_RULE_KEY);

    if (rule == null) {
      return;
    }

    List<Resource<JavaPackage>> packages = new ArrayList<Resource<JavaPackage>>();
    for (XsdCyclePath pathElement : group.getCyclePath()) {
      String fqName = pathElement.getParent();
      Resource<JavaPackage> javaPackage = sensorContext.getResource(new JavaPackage(fqName));

      if (javaPackage == null) {
        LOG.error("Cannot obtain resource " + fqName);
      } else {
        packages.add(javaPackage);
      }
    }

    for (Resource<JavaPackage> jPackage : packages) {
      Violation v = Violation.create(rule, jPackage);
      List<Resource<JavaPackage>> tempPackages = new ArrayList<Resource<JavaPackage>>(packages);
      tempPackages.remove(jPackage);
      StringBuffer buffer = new StringBuffer();
      buffer.append("Package participates in a cycle group");

      boolean first = true;
      for (Resource<JavaPackage> tPackage : tempPackages) {
        if (first) {
          buffer.append(" with package(s): ").append(tPackage.getName());
          first = false;
        } else {
          buffer.append(", ").append(tPackage.getName());
        }
      }

      v.setMessage(buffer.toString());
      v.setLineId(null);
      sensorContext.saveViolation(v);
    }
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
