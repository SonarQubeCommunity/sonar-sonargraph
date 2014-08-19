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

import com.hello2morrow.sonarplugin.foundation.PluginVersionReader;
import com.hello2morrow.sonarplugin.foundation.SonargraphPluginBase;
import com.hello2morrow.sonarplugin.foundation.SonargraphStandaloneMetricNames;
import com.hello2morrow.sonarplugin.foundation.Utilities;
import com.hello2morrow.sonarplugin.metric.SonargraphAlertThresholds;
import com.hello2morrow.sonarplugin.metric.SonargraphDerivedMetrics;
import com.hello2morrow.sonarplugin.metric.SonargraphSimpleMetrics;
import com.hello2morrow.sonarplugin.metric.internal.SonargraphInternalMetrics;
import com.hello2morrow.sonarplugin.persistence.IReportReader;
import com.hello2morrow.sonarplugin.persistence.PersistenceUtilities;
import com.hello2morrow.sonarplugin.persistence.ReportFileReader;
import com.hello2morrow.sonarplugin.processor.ArchitectureViolationProcessor;
import com.hello2morrow.sonarplugin.processor.CycleGroupProcessor;
import com.hello2morrow.sonarplugin.processor.IProcessor;
import com.hello2morrow.sonarplugin.processor.TaskProcessor;
import com.hello2morrow.sonarplugin.processor.WarningProcessor;
import com.hello2morrow.sonarplugin.xsd.XsdAttributeRoot;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sonar.api.batch.Sensor;
import org.sonar.api.batch.SensorContext;
import org.sonar.api.batch.fs.FileSystem;
import org.sonar.api.component.ResourcePerspectives;
import org.sonar.api.config.Settings;
import org.sonar.api.measures.Measure;
import org.sonar.api.profiles.RulesProfile;
import org.sonar.api.resources.Project;
import org.sonar.api.resources.Qualifiers;

import java.util.HashMap;
import java.util.Map;

/**
 * The Sonargraph-Sonar report is only generated for the sub-modules, not for the aggregating project. Therefore some dashboard metrics that
 * cannot be calculated as the sum of the child project metrics have to be temporarily saved to internal metrics and be examined in a
 * decorator.
 *
 */
public final class SonargraphSensor implements Sensor {

  private static final String NOT_PROCESSED_MESSAGE = "Module will not be processed by Sonargraph!";
  private static final String SEPARATOR = "----------------------------------------------------------------";
  private static final int SONARGRAPH_METRICS_COUNT = 70;
  private static final double HUNDRET_PERCENT = 100.0;
  private static final int NO_DECIMAL = 0;

  private static final Logger LOG = LoggerFactory.getLogger(SonargraphSensor.class);

  private final Map<String, Number> buildUnitmetrics;
  private final Map<String, Number> systemMetrics;

  private SensorContext sensorContext;
  private final RulesProfile profile;

  private final IReportReader reportReader;
  private final Settings settings;
  private final FileSystem fileSystem;
  private final ResourcePerspectives perspectives;

  public SonargraphSensor(RulesProfile profile, Settings settings, FileSystem moduleFileSystem, ResourcePerspectives perspectives) {
    this.profile = profile;
    this.settings = settings;
    this.fileSystem = moduleFileSystem;
    this.perspectives = perspectives;
    reportReader = new ReportFileReader();
    buildUnitmetrics = new HashMap<String, Number>();
    systemMetrics = new HashMap<String, Number>(SONARGRAPH_METRICS_COUNT);
  }

  /**
   * Used by JUnit tests
   */
  SonargraphSensor(final RulesProfile profile, Settings settings, SensorContext sensorContext, FileSystem moduleFileSystem, ResourcePerspectives perspectives) {
    this(profile, settings, moduleFileSystem, perspectives);
    this.sensorContext = sensorContext;
  }

  /* called from maven */
  @Override
  public boolean shouldExecuteOnProject(Project project) {
    if (Utilities.isAggregatingProject(project)) {
      return false;
    }

    boolean isSonargraphProject = Utilities.isSonargraphProject(this.fileSystem, this.profile);
    if (!isSonargraphProject) {
      LOG.warn(SEPARATOR);
      LOG.warn("Sonargraph: Project" + project.getName() + " [" + project.getKey()
        + "] is not processed, since no Sonargraph rules are activated in current SonarQube quality profile.");
      LOG.warn(SEPARATOR);
    }

    return isSonargraphProject;
  }

  @Override
  public void analyse(final Project project, SensorContext sensorContext) {
    if (project == null || sensorContext == null) {
      LOG.error("Major error calling Sonargraph Sonar Plugin: Project and / or sensorContext are null. " + "Please check your project configuration!");
      return;
    }

    LOG.info("Sonargraph: Execute for module " + project.getName() + " [" + project.getKey() + "]");

    this.sensorContext = sensorContext;
    reportReader.readSonargraphReport(project, fileSystem, settings);

    XsdAttributeRoot buildUnit = reportReader.retrieveBuildUnit(project);

    if (buildUnit == null) {
      LOG.warn("No Sonargraph build units found in report for [" + project.getName() + "]. " + NOT_PROCESSED_MESSAGE);
      Measure m = new Measure(SonargraphInternalMetrics.MODULE_NOT_PART_OF_SONARGRAPH_WORKSPACE);
      sensorContext.saveMeasure(m);
      return;
    }

    LOG.debug("Analysing buildUnit: " + buildUnit.getName());

    PersistenceUtilities.readAttributesToMap(buildUnit, buildUnitmetrics);

    XsdAttributeRoot attributesPart = reportReader.getReport().getAttributes();
    PersistenceUtilities.readAttributesToMap(attributesPart, systemMetrics);

    Number numberOfStatements = buildUnitmetrics.get(SonargraphStandaloneMetricNames.INSTRUCTIONS);
    if (numberOfStatements == null || numberOfStatements.intValue() < 1) {
      LOG.warn("No code to be analysed in [" + project.getName() + "]. " + NOT_PROCESSED_MESSAGE);
      Measure m = new Measure(SonargraphInternalMetrics.MODULE_NOT_PART_OF_SONARGRAPH_WORKSPACE);
      sensorContext.saveMeasure(m);
      return;
    }

    this.analyseBuildUnit();
    this.analyseMetricsForStructuralDebtDashbox(buildUnit, project);
    this.analyseMetricsForStructureDashbox(buildUnit, project);
    this.analyseMetricsForArchitectureDashbox(buildUnit, project);
  }

  @Override
  public String toString() {
    return "Sonar-Sonargraph-Plugin [" + PluginVersionReader.INSTANCE.getVersion() + "]";
  }

  /**
   * This method retrieves general metrics from the report generated by Sonargraph
   */
  /* package access to ease testing */
  void analyseBuildUnit() {
    Utilities.saveExistingMeasureToContext(sensorContext, buildUnitmetrics, SonargraphStandaloneMetricNames.INTERNAL_PACKAGES, SonargraphSimpleMetrics.INTERNAL_PACKAGES,
      SonargraphAlertThresholds.getThreshold(SonargraphSimpleMetrics.INTERNAL_PACKAGES), NO_DECIMAL);
    Utilities.saveExistingMeasureToContext(sensorContext, buildUnitmetrics, SonargraphStandaloneMetricNames.JAVA_FILES, SonargraphSimpleMetrics.JAVA_FILES,
      SonargraphAlertThresholds.getThreshold(SonargraphSimpleMetrics.JAVA_FILES), NO_DECIMAL);
    Utilities.saveExistingMeasureToContext(sensorContext, buildUnitmetrics, SonargraphStandaloneMetricNames.TYPE_DEPENDENCIES, SonargraphSimpleMetrics.TYPE_DEPENDENCIES,
      SonargraphAlertThresholds.getThreshold(SonargraphSimpleMetrics.TYPE_DEPENDENCIES), NO_DECIMAL);
  }

  private void analyseMetricsForStructuralDebtDashbox(XsdAttributeRoot buildUnit, Project project) {
    Number structuralDebtIndex = buildUnitmetrics.get(SonargraphStandaloneMetricNames.STUCTURAL_DEBT_INDEX);
    Utilities.saveExistingMeasureToContext(sensorContext, buildUnitmetrics, SonargraphStandaloneMetricNames.STUCTURAL_DEBT_INDEX, SonargraphSimpleMetrics.STRUCTURAL_DEBT_INDEX,
      SonargraphAlertThresholds.getThreshold(SonargraphSimpleMetrics.STRUCTURAL_DEBT_INDEX), NO_DECIMAL);
    double indexCost = this.determineCostPerIndexPoint();
    if (indexCost > 0) {
      double structuralDebtCost = 0;
      if (structuralDebtIndex != null && structuralDebtIndex.intValue() > 0) {
        structuralDebtCost = structuralDebtIndex.intValue() * indexCost;
      }
      Utilities.saveMeasureToContext(sensorContext, SonargraphSimpleMetrics.STRUCTURAL_DEBT_COST, structuralDebtCost,
        SonargraphAlertThresholds.getThreshold(SonargraphSimpleMetrics.STRUCTURAL_DEBT_COST), NO_DECIMAL);
    }
    if (project.getQualifier().equals(Qualifiers.MODULE)) {
      Utilities.saveExistingMeasureToContext(sensorContext, buildUnitmetrics, SonargraphStandaloneMetricNames.TASKS, SonargraphSimpleMetrics.TASKS,
        SonargraphAlertThresholds.getThreshold(SonargraphSimpleMetrics.TASKS), NO_DECIMAL);
      Utilities.saveExistingMeasureToContext(sensorContext, systemMetrics, SonargraphStandaloneMetricNames.TASKS, SonargraphInternalMetrics.SYSTEM_ALL_TASKS, null, NO_DECIMAL);
    } else {
      Utilities.saveExistingMeasureToContext(sensorContext, systemMetrics, SonargraphStandaloneMetricNames.TASKS, SonargraphSimpleMetrics.TASKS,
        SonargraphAlertThresholds.getThreshold(SonargraphSimpleMetrics.TASKS), NO_DECIMAL);
    }
    IProcessor taskProcessor = new TaskProcessor(project, fileSystem, profile, sensorContext, perspectives);
    taskProcessor.process(reportReader.getReport(), buildUnit);
  }

  private double determineCostPerIndexPoint() {
    String indexCostString = this.settings.getString(SonargraphPluginBase.COST_PER_INDEX_POINT);
    double indexCost = SonargraphPluginBase.COST_PER_INDEX_POINT_DEFAULT;
    if (indexCostString != null && indexCostString.trim().length() > 0) {
      try {
        indexCost = new Double(indexCostString.trim()).doubleValue();
      } catch (NumberFormatException e) {
        LOG.error("Property " + SonargraphPluginBase.COST_PER_INDEX_POINT + " must be a double value." + " Ignoring value '" + indexCostString + "' and using default value '"
          + indexCost + "'.");
      }
    }
    return indexCost;
  }

  private void analyseMetricsForStructureDashbox(XsdAttributeRoot buildUnit, Project project) {

    LOG.debug("Analysing cycleGroups of buildUnit: " + buildUnit.getName());
    CycleGroupProcessor processor = new CycleGroupProcessor(project, profile, fileSystem, perspectives);
    processor.process(reportReader.getReport(), buildUnit);

    double cyclicity = processor.getCyclicity();
    double biggestCycleGroupSize = processor.getBiggestCycleGroupSize();
    double cyclicPackages = processor.getCyclicPackages();

    Utilities.saveMeasureToContext(sensorContext, SonargraphDerivedMetrics.BIGGEST_CYCLE_GROUP, biggestCycleGroupSize,
      SonargraphAlertThresholds.getThreshold(SonargraphDerivedMetrics.BIGGEST_CYCLE_GROUP), NO_DECIMAL);
    Utilities.saveMeasureToContext(sensorContext, SonargraphSimpleMetrics.CYCLICITY, cyclicity, SonargraphAlertThresholds.getThreshold(SonargraphSimpleMetrics.CYCLICITY),
      NO_DECIMAL);
    Utilities.saveMeasureToContext(sensorContext, SonargraphSimpleMetrics.CYCLIC_PACKAGES, cyclicPackages,
      SonargraphAlertThresholds.getThreshold(SonargraphSimpleMetrics.CYCLIC_PACKAGES), NO_DECIMAL);

    /* For the aggregating project, these derived metrics are calculated in the SonargraphDerivedMetricsDecorator */
    double packages = sensorContext.getMeasure(SonargraphSimpleMetrics.INTERNAL_PACKAGES).getValue();
    if (packages > 0) {
      double relCyclicity = HUNDRET_PERCENT * Math.sqrt(cyclicity) / packages;
      double relCyclicPackages = HUNDRET_PERCENT * cyclicPackages / packages;
      Utilities.saveMeasureToContext(sensorContext, SonargraphDerivedMetrics.RELATIVE_CYCLICITY, relCyclicity,
        SonargraphAlertThresholds.getThreshold(SonargraphDerivedMetrics.RELATIVE_CYCLICITY), 1);
      Utilities.saveMeasureToContext(sensorContext, SonargraphDerivedMetrics.CYCLIC_PACKAGES_PERCENT, relCyclicPackages,
        SonargraphAlertThresholds.getThreshold(SonargraphDerivedMetrics.CYCLIC_PACKAGES_PERCENT), 1);
    } else {
      Utilities.saveMeasureToContext(sensorContext, SonargraphDerivedMetrics.RELATIVE_CYCLICITY, 0,
        SonargraphAlertThresholds.getThreshold(SonargraphDerivedMetrics.RELATIVE_CYCLICITY), NO_DECIMAL);
      Utilities.saveMeasureToContext(sensorContext, SonargraphDerivedMetrics.CYCLIC_PACKAGES_PERCENT, 0,
        SonargraphAlertThresholds.getThreshold(SonargraphDerivedMetrics.CYCLIC_PACKAGES_PERCENT), 1);
    }
    Utilities.saveExistingMeasureToContext(sensorContext, buildUnitmetrics, SonargraphStandaloneMetricNames.EROSION_REFS, SonargraphSimpleMetrics.REFERENCES_TO_REMOVE,
      SonargraphAlertThresholds.getThreshold(SonargraphSimpleMetrics.REFERENCES_TO_REMOVE), NO_DECIMAL);
    Utilities.saveExistingMeasureToContext(sensorContext, buildUnitmetrics, SonargraphStandaloneMetricNames.EROSION_TYPES, SonargraphSimpleMetrics.TYPE_DEPENDENCIES_TO_CUT,
      SonargraphAlertThresholds.getThreshold(SonargraphSimpleMetrics.TYPE_DEPENDENCIES_TO_CUT), NO_DECIMAL);

    Utilities.saveExistingMeasureToContext(sensorContext, buildUnitmetrics, SonargraphStandaloneMetricNames.ACD, SonargraphSimpleMetrics.ACD,
      SonargraphAlertThresholds.getThreshold(SonargraphSimpleMetrics.ACD), 1);
    Utilities.saveExistingMeasureToContext(sensorContext, buildUnitmetrics, SonargraphStandaloneMetricNames.NCCD, SonargraphSimpleMetrics.NCCD,
      SonargraphAlertThresholds.getThreshold(SonargraphSimpleMetrics.NCCD), 1);

    /* rACD is not displayed on the dashbox but fits well into this category */
    Utilities.saveExistingMeasureToContext(sensorContext, buildUnitmetrics, SonargraphStandaloneMetricNames.RELATIVE_ACD, SonargraphSimpleMetrics.RELATIVE_ACD,
      SonargraphAlertThresholds.getThreshold(SonargraphSimpleMetrics.RELATIVE_ACD), 1);
    Utilities.saveExistingMeasureToContext(sensorContext, buildUnitmetrics, SonargraphStandaloneMetricNames.INSTRUCTIONS, SonargraphSimpleMetrics.INSTRUCTIONS,
      SonargraphAlertThresholds.getThreshold(SonargraphSimpleMetrics.INSTRUCTIONS), NO_DECIMAL);
  }

  private void analyseMetricsForArchitectureDashbox(XsdAttributeRoot buildUnit, Project project) {
    this.analyseArchitectureMeasures(buildUnit);
    this.analyseWarnings(project);
    if (profile != null) {
      IProcessor architectureViolationHandler = new ArchitectureViolationProcessor(project, profile, fileSystem, perspectives);
      architectureViolationHandler.process(reportReader.getReport(), buildUnit);
      IProcessor warningProcessor = new WarningProcessor(project, profile, fileSystem, this.perspectives);
      warningProcessor.process(reportReader.getReport(), buildUnit);
    } else {
      LOG.error("RuleFinder must be set in constructor!");
    }
  }

  private void analyseArchitectureMeasures(XsdAttributeRoot buildUnit) {
    LOG.debug("Analysing architectural measures of build unit: " + buildUnit.getName());
    if (!buildUnitmetrics.containsKey(SonargraphStandaloneMetricNames.UNASSIGNED_TYPES)) {
      LOG.info("No architecture measures found for build unit: " + buildUnit.getName());
      return;
    }
    double types = Utilities.saveExistingMeasureToContext(sensorContext, buildUnitmetrics, SonargraphStandaloneMetricNames.INTERNAL_TYPES, SonargraphSimpleMetrics.INTERNAL_TYPES,
      SonargraphAlertThresholds.getThreshold(SonargraphSimpleMetrics.INTERNAL_TYPES), NO_DECIMAL).getValue();
    assert types >= 1.0 : "Project must not be empty !";

    Utilities.saveExistingMeasureToContext(sensorContext, buildUnitmetrics, SonargraphStandaloneMetricNames.VIOLATING_DEPENDENCIES,
      SonargraphSimpleMetrics.VIOLATING_TYPE_DEPENDENCIES, SonargraphAlertThresholds.getThreshold(SonargraphSimpleMetrics.VIOLATING_TYPE_DEPENDENCIES), NO_DECIMAL);
    Measure violatingTypes = Utilities.saveExistingMeasureToContext(sensorContext, buildUnitmetrics, SonargraphStandaloneMetricNames.VIOLATING_TYPES,
      SonargraphSimpleMetrics.VIOLATING_TYPES, SonargraphAlertThresholds.getThreshold(SonargraphSimpleMetrics.VIOLATING_TYPES), NO_DECIMAL);
    double violatingTypesPercent = HUNDRET_PERCENT * violatingTypes.getValue() / types;
    Utilities.saveMeasureToContext(sensorContext, SonargraphDerivedMetrics.VIOLATING_TYPES_PERCENT, violatingTypesPercent,
      SonargraphAlertThresholds.getThreshold(SonargraphDerivedMetrics.VIOLATING_TYPES_PERCENT), 1);
    Utilities.saveExistingMeasureToContext(sensorContext, buildUnitmetrics, SonargraphStandaloneMetricNames.VIOLATING_REFERENCES, SonargraphSimpleMetrics.VIOLATING_REFERENCES,
      SonargraphAlertThresholds.getThreshold(SonargraphSimpleMetrics.VIOLATING_REFERENCES), NO_DECIMAL);
    Utilities.saveExistingMeasureToContext(sensorContext, buildUnitmetrics, SonargraphStandaloneMetricNames.IGNORED_VIOLATIONS, SonargraphSimpleMetrics.IGNORED_VIOLATONS,
      SonargraphAlertThresholds.getThreshold(SonargraphSimpleMetrics.IGNORED_VIOLATONS), NO_DECIMAL);
    Measure unassignedTypes = Utilities.saveExistingMeasureToContext(sensorContext, buildUnitmetrics, SonargraphStandaloneMetricNames.UNASSIGNED_TYPES,
      SonargraphSimpleMetrics.UNASSIGNED_TYPES, SonargraphAlertThresholds.getThreshold(SonargraphSimpleMetrics.UNASSIGNED_TYPES), NO_DECIMAL);
    double unassignedTypesPercent = HUNDRET_PERCENT * unassignedTypes.getValue() / types;
    Utilities.saveMeasureToContext(sensorContext, SonargraphDerivedMetrics.UNASSIGNED_TYPES_PERCENT, unassignedTypesPercent,
      SonargraphAlertThresholds.getThreshold(SonargraphDerivedMetrics.UNASSIGNED_TYPES_PERCENT), 1);
  }

  private void analyseWarnings(Project project) {
    if (project.getQualifier().equals(Qualifiers.MODULE)) {
      LOG.debug("Values for warning metrics are only taken from build unit section for child module projects.");
      Utilities.saveExistingMeasureToContext(sensorContext, buildUnitmetrics, SonargraphStandaloneMetricNames.ALL_WARNINGS, SonargraphSimpleMetrics.ALL_WARNINGS,
        SonargraphAlertThresholds.getThreshold(SonargraphSimpleMetrics.ALL_WARNINGS), NO_DECIMAL);
      Utilities.saveExistingMeasureToContext(sensorContext, buildUnitmetrics, SonargraphStandaloneMetricNames.CYCLE_WARNINGS, SonargraphSimpleMetrics.CYCLE_WARNINGS,
        SonargraphAlertThresholds.getThreshold(SonargraphSimpleMetrics.CYCLE_WARNINGS), NO_DECIMAL);
      Utilities.saveExistingMeasureToContext(sensorContext, buildUnitmetrics, SonargraphStandaloneMetricNames.DUPLICATE_WARNINGS, SonargraphSimpleMetrics.DUPLICATE_WARNINGS,
        SonargraphAlertThresholds.getThreshold(SonargraphSimpleMetrics.DUPLICATE_WARNINGS), NO_DECIMAL);
      Utilities.saveExistingMeasureToContext(sensorContext, buildUnitmetrics, SonargraphStandaloneMetricNames.WORKSPACE_WARNINGS, SonargraphSimpleMetrics.WORKSPACE_WARNINGS,
        SonargraphAlertThresholds.getThreshold(SonargraphSimpleMetrics.WORKSPACE_WARNINGS), NO_DECIMAL);
      Utilities.saveExistingMeasureToContext(sensorContext, buildUnitmetrics, SonargraphStandaloneMetricNames.THRESHOLD_WARNINGS, SonargraphSimpleMetrics.THRESHOLD_WARNINGS,
        SonargraphAlertThresholds.getThreshold(SonargraphSimpleMetrics.THRESHOLD_WARNINGS), NO_DECIMAL, false);
      Utilities.saveExistingMeasureToContext(sensorContext, buildUnitmetrics, SonargraphStandaloneMetricNames.IGNORED_WARNINGS, SonargraphSimpleMetrics.IGNORED_WARNINGS,
        SonargraphAlertThresholds.getThreshold(SonargraphSimpleMetrics.IGNORED_WARNINGS), NO_DECIMAL);

      /*
       * Save overall system warnings to internal metrics. The decorator executed on the root parent module will retrieve them and store
       * them in the visible metrics.
       */
      Utilities.saveExistingMeasureToContext(sensorContext, systemMetrics, SonargraphStandaloneMetricNames.ALL_WARNINGS, SonargraphInternalMetrics.SYSTEM_ALL_WARNINGS, null,
        NO_DECIMAL);
      Utilities.saveExistingMeasureToContext(sensorContext, systemMetrics, SonargraphStandaloneMetricNames.CYCLE_WARNINGS, SonargraphInternalMetrics.SYSTEM_CYCLE_WARNINGS, null,
        NO_DECIMAL);
      Utilities.saveExistingMeasureToContext(sensorContext, systemMetrics, SonargraphStandaloneMetricNames.THRESHOLD_WARNINGS, SonargraphInternalMetrics.SYSTEM_THRESHOLD_WARNINGS,
        null, NO_DECIMAL);
      Utilities.saveExistingMeasureToContext(sensorContext, systemMetrics, SonargraphStandaloneMetricNames.WORKSPACE_WARNINGS, SonargraphInternalMetrics.SYSTEM_WORKSPACE_WARNINGS,
        null, NO_DECIMAL);
      Utilities.saveExistingMeasureToContext(sensorContext, systemMetrics, SonargraphStandaloneMetricNames.IGNORED_WARNINGS, SonargraphInternalMetrics.SYSTEM_IGNORED_WARNINGS,
        null, NO_DECIMAL);

    } else {
      LOG.debug("Values for warning metrics are only taken from general section to also include logical cycle group warnings.");
      Utilities.saveExistingMeasureToContext(sensorContext, systemMetrics, SonargraphStandaloneMetricNames.ALL_WARNINGS, SonargraphSimpleMetrics.ALL_WARNINGS,
        SonargraphAlertThresholds.getThreshold(SonargraphSimpleMetrics.ALL_WARNINGS), NO_DECIMAL);
      Utilities.saveExistingMeasureToContext(sensorContext, systemMetrics, SonargraphStandaloneMetricNames.CYCLE_WARNINGS, SonargraphSimpleMetrics.CYCLE_WARNINGS,
        SonargraphAlertThresholds.getThreshold(SonargraphSimpleMetrics.CYCLE_WARNINGS), NO_DECIMAL);
      Utilities.saveExistingMeasureToContext(sensorContext, systemMetrics, SonargraphStandaloneMetricNames.DUPLICATE_WARNINGS, SonargraphSimpleMetrics.DUPLICATE_WARNINGS,
        SonargraphAlertThresholds.getThreshold(SonargraphSimpleMetrics.DUPLICATE_WARNINGS), NO_DECIMAL);
      Utilities.saveExistingMeasureToContext(sensorContext, systemMetrics, SonargraphStandaloneMetricNames.WORKSPACE_WARNINGS, SonargraphSimpleMetrics.WORKSPACE_WARNINGS,
        SonargraphAlertThresholds.getThreshold(SonargraphSimpleMetrics.WORKSPACE_WARNINGS), NO_DECIMAL);
      Utilities.saveExistingMeasureToContext(sensorContext, systemMetrics, SonargraphStandaloneMetricNames.THRESHOLD_WARNINGS, SonargraphSimpleMetrics.THRESHOLD_WARNINGS,
        SonargraphAlertThresholds.getThreshold(SonargraphSimpleMetrics.THRESHOLD_WARNINGS), NO_DECIMAL);
      Utilities.saveExistingMeasureToContext(sensorContext, systemMetrics, SonargraphStandaloneMetricNames.IGNORED_WARNINGS, SonargraphSimpleMetrics.IGNORED_WARNINGS,
        SonargraphAlertThresholds.getThreshold(SonargraphSimpleMetrics.IGNORED_WARNINGS), NO_DECIMAL);
    }
  }
}
