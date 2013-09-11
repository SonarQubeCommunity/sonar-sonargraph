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

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sonar.api.batch.Sensor;
import org.sonar.api.batch.SensorContext;
import org.sonar.api.config.Settings;
import org.sonar.api.measures.Measure;
import org.sonar.api.profiles.RulesProfile;
import org.sonar.api.resources.Project;
import org.sonar.api.resources.Qualifiers;

import com.hello2morrow.sonarplugin.decorator.AlertDecorator;
import com.hello2morrow.sonarplugin.foundation.SonargraphPluginBase;
import com.hello2morrow.sonarplugin.foundation.SonargraphStandaloneMetricNames;
import com.hello2morrow.sonarplugin.foundation.Utilities;
import com.hello2morrow.sonarplugin.metric.SonargraphDerivedMetrics;
import com.hello2morrow.sonarplugin.metric.SonargraphMetrics;
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
import com.hello2morrow.sonarplugin.xsd.ReportContext;
import com.hello2morrow.sonarplugin.xsd.XsdAttributeRoot;

/**
 * The Sonargraph-Sonar report is only generated for the sub-modules, not for the aggregating project. Therefore some dashboard metrics that
 * cannot be calculated as the sum of the child project metrics have to be temporarily saved to internal metrics and be examined in a
 * decorator.
 * 
 */
public final class SonargraphSensor implements Sensor {

  private static final int SONARGRAPH_METRICS_COUNT = 70;
  private static final double HUNDRET_PERCENT = 100.0;
  private static final int NO_DECIMAL = 0;
  
  //FIXME: Read this info from a properties file that is generated during the maven build 
  private static final String VERSION = "3.1.1";
  private static final Logger LOG = LoggerFactory.getLogger(SonargraphSensor.class);

  private Map<String, Number> buildUnitmetrics;
  private Map<String, Number> systemMetrics;

  private SensorContext sensorContext;
  private RulesProfile profile;

  private IReportReader reportReader;
  private Settings settings;

  public SonargraphSensor(RulesProfile profile, Settings settings) {
    this.profile = profile;
    reportReader = new ReportFileReader();
    buildUnitmetrics = new HashMap<String, Number>();
    systemMetrics = new HashMap<String, Number>(SONARGRAPH_METRICS_COUNT);
    this.settings = settings;
  }

  /**
   * Used by JUnit tests
   */
  SonargraphSensor(final RulesProfile profile, Settings settings, SensorContext sensorContext) {
    this(profile, settings);
    this.sensorContext = sensorContext;
  }

  /* called from maven */
  public boolean shouldExecuteOnProject(Project project) {
    return !Utilities.isAggregatingProject(project)
        && Utilities.isSonargraphProject(project, this.profile, SonargraphMetrics.getAll());
  }

  public void analyse(final Project project, SensorContext sensorContext) {
    if (project == null || sensorContext == null) {
      LOG.error("Major error calling Sonargraph Sonar Plugin: Project and / or sensorContext are null. "
          + "Please check your project configuration!");
      return;
    }

    LOG.info("----------------------------------------------------------------");
    LOG.info("Execute sonar-sonargraph-plugin for " + project.getName() + " [" + project.getKey() + "]");
    LOG.info("----------------------------------------------------------------");

    this.sensorContext = sensorContext;
    reportReader.readSonargraphReport(project, settings);

    XsdAttributeRoot buildUnit = reportReader.retrieveBuildUnit(project);

    if (buildUnit == null) {
      LOG.warn("No Sonargraph build units found in report for [" + project.getName()
          + "]. Module will not be processed by Sonargraph!");
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
      LOG.warn("No code to be analysed in [" + project.getName() + "]. Module will not be processed by Sonargraph!");
      Measure m = new Measure(SonargraphInternalMetrics.MODULE_NOT_PART_OF_SONARGRAPH_WORKSPACE);
      sensorContext.saveMeasure(m);
      return;
    }

    this.analyseBuildUnit(reportReader.getReport(), buildUnit);
    this.analyseMetricsForStructuralDebtDashbox(sensorContext, buildUnit, project);
    this.analyseMetricsForStructureDashbox(buildUnit);
    this.analyseMetricsForArchitectureDashbox(buildUnit, project);

    AlertDecorator.setAlertLevels(new SensorProjectContext(sensorContext));
  }

  @Override
  public String toString() {
    return SonargraphSensor.class.getSimpleName() + ", version: " + SonargraphSensor.VERSION;
  }

  /**
   * This method retrieves general metrics from the report generated by Sonargraph
   */
  /* package access to ease testing */
  void analyseBuildUnit(final ReportContext report, final XsdAttributeRoot buildUnit) {
    Utilities.saveExistingMeasureToContext(sensorContext, buildUnitmetrics,
        SonargraphStandaloneMetricNames.INTERNAL_PACKAGES, SonargraphSimpleMetrics.INTERNAL_PACKAGES, NO_DECIMAL);
    Utilities.saveExistingMeasureToContext(sensorContext, buildUnitmetrics, SonargraphStandaloneMetricNames.JAVA_FILES,
        SonargraphSimpleMetrics.JAVA_FILES, NO_DECIMAL);
    Utilities.saveExistingMeasureToContext(sensorContext, buildUnitmetrics,
        SonargraphStandaloneMetricNames.TYPE_DEPENDENCIES, SonargraphSimpleMetrics.TYPE_DEPENDENCIES, NO_DECIMAL);
  }

  private void analyseMetricsForStructuralDebtDashbox(SensorContext sensorContext, XsdAttributeRoot buildUnit,
      Project project) {
    Number structuralDebtIndex = buildUnitmetrics.get(SonargraphStandaloneMetricNames.STUCTURAL_DEBT_INDEX);
    Utilities
        .saveExistingMeasureToContext(sensorContext, buildUnitmetrics,
            SonargraphStandaloneMetricNames.STUCTURAL_DEBT_INDEX, SonargraphSimpleMetrics.STRUCTURAL_DEBT_INDEX,
            NO_DECIMAL);
    double indexCost = this.determineCostPerIndexPoint();
    if (indexCost > 0) {
      double structuralDebtCost = 0;
      if (structuralDebtIndex != null && structuralDebtIndex.intValue() > 0) {
        structuralDebtCost = structuralDebtIndex.intValue() * indexCost;
      }
      Utilities.saveMeasureToContext(sensorContext, SonargraphSimpleMetrics.STRUCTURAL_DEBT_COST, structuralDebtCost,
          NO_DECIMAL);
    }
    if (project.getQualifier().equals(Qualifiers.MODULE)) {
      Utilities.saveExistingMeasureToContext(sensorContext, buildUnitmetrics, SonargraphStandaloneMetricNames.TASKS,
          SonargraphSimpleMetrics.TASKS, NO_DECIMAL);
      Utilities.saveExistingMeasureToContext(sensorContext, systemMetrics, SonargraphStandaloneMetricNames.TASKS,
          SonargraphInternalMetrics.SYSTEM_ALL_TASKS, NO_DECIMAL);
    } else {
      Utilities.saveExistingMeasureToContext(sensorContext, systemMetrics, SonargraphStandaloneMetricNames.TASKS,
          SonargraphSimpleMetrics.TASKS, NO_DECIMAL);
    }
    IProcessor taskProcessor = new TaskProcessor(profile, sensorContext);
    taskProcessor.process(reportReader.getReport(), buildUnit);
  }

  private double determineCostPerIndexPoint() {
    String indexCostString = this.settings.getString(SonargraphPluginBase.COST_PER_INDEX_POINT);
    double indexCost = SonargraphPluginBase.COST_PER_INDEX_POINT_DEFAULT;
    if (indexCostString != null && indexCostString.trim().length() > 0) {
      try {
        indexCost = new Double(indexCostString.trim()).doubleValue();
      } catch (NumberFormatException e) {
        LOG.error("Property " + SonargraphPluginBase.COST_PER_INDEX_POINT + " must be a double value."
            + " Ignoring value '" + indexCostString + "' and using default value '" + indexCost + "'.");
      }
    }
    return indexCost;
  }

  private void analyseMetricsForStructureDashbox(XsdAttributeRoot buildUnit) {

    LOG.debug("Analysing cycleGroups of buildUnit: " + buildUnit.getName());
    CycleGroupProcessor processor = new CycleGroupProcessor(profile, sensorContext);
    processor.process(reportReader.getReport(), buildUnit);

    double cyclicity = processor.getCyclicity();
    double biggestCycleGroupSize = processor.getBiggestCycleGroupSize();
    double cyclicPackages = processor.getCyclicPackages();

    Utilities.saveMeasureToContext(sensorContext, SonargraphDerivedMetrics.BIGGEST_CYCLE_GROUP, biggestCycleGroupSize,
        NO_DECIMAL);
    Utilities.saveMeasureToContext(sensorContext, SonargraphSimpleMetrics.CYCLICITY, cyclicity, NO_DECIMAL);
    Utilities.saveMeasureToContext(sensorContext, SonargraphSimpleMetrics.CYCLIC_PACKAGES, cyclicPackages, NO_DECIMAL);

    /* For the aggregating project, these derived metrics are calculated in the SonargraphDerivedMetricsDecorator */
    double packages = sensorContext.getMeasure(SonargraphSimpleMetrics.INTERNAL_PACKAGES).getValue();
    if (packages > 0) {
      double relCyclicity = HUNDRET_PERCENT * Math.sqrt(cyclicity) / packages;
      double relCyclicPackages = HUNDRET_PERCENT * cyclicPackages / packages;
      Utilities.saveMeasureToContext(sensorContext, SonargraphDerivedMetrics.RELATIVE_CYCLICITY, relCyclicity,
          NO_DECIMAL);
      Utilities.saveMeasureToContext(sensorContext, SonargraphDerivedMetrics.CYCLIC_PACKAGES_PERCENT,
          relCyclicPackages, 1);
    } else {
      Utilities.saveMeasureToContext(sensorContext, SonargraphDerivedMetrics.RELATIVE_CYCLICITY, 0, NO_DECIMAL);
      Utilities.saveMeasureToContext(sensorContext, SonargraphDerivedMetrics.CYCLIC_PACKAGES_PERCENT, 0, 1);
    }
    Utilities.saveExistingMeasureToContext(sensorContext, buildUnitmetrics,
        SonargraphStandaloneMetricNames.EROSION_REFS, SonargraphSimpleMetrics.REFERENCES_TO_REMOVE, NO_DECIMAL);
    Utilities.saveExistingMeasureToContext(sensorContext, buildUnitmetrics,
        SonargraphStandaloneMetricNames.EROSION_TYPES, SonargraphSimpleMetrics.TYPE_DEPENDENCIES_TO_CUT, NO_DECIMAL);

    Utilities.saveExistingMeasureToContext(sensorContext, buildUnitmetrics, SonargraphStandaloneMetricNames.ACD,
        SonargraphSimpleMetrics.ACD, 1);
    Utilities.saveExistingMeasureToContext(sensorContext, buildUnitmetrics, SonargraphStandaloneMetricNames.NCCD,
        SonargraphSimpleMetrics.NCCD, 1);
    /* rACD is not displayed on the dashbox but fits well into this category */
    Utilities.saveExistingMeasureToContext(sensorContext, buildUnitmetrics,
        SonargraphStandaloneMetricNames.RELATIVE_ACD, SonargraphSimpleMetrics.RELATIVE_ACD, 1);
    Utilities.saveExistingMeasureToContext(sensorContext, buildUnitmetrics,
        SonargraphStandaloneMetricNames.INSTRUCTIONS, SonargraphSimpleMetrics.INSTRUCTIONS, NO_DECIMAL);
  }

  private void analyseMetricsForArchitectureDashbox(XsdAttributeRoot buildUnit, Project project) {
    this.analyseArchitectureMeasures(buildUnit);
    this.analyseWarnings(project);
    if (profile != null) {
      IProcessor architectureViolationHandler = new ArchitectureViolationProcessor(profile, sensorContext);
      architectureViolationHandler.process(reportReader.getReport(), buildUnit);
      IProcessor warningProcessor = new WarningProcessor(profile, sensorContext);
      warningProcessor.process(reportReader.getReport(), buildUnit);
    } else {
      LOG.error("RuleFinder must be set in constructor!");
    }
  }

  private void analyseArchitectureMeasures(XsdAttributeRoot buildUnit) {
    LOG.debug("Analysing architectural measures of build unit: " + buildUnit.getName());
    if ( !buildUnitmetrics.containsKey(SonargraphStandaloneMetricNames.UNASSIGNED_TYPES)) {
      LOG.info("No architecture measures found for build unit: " + buildUnit.getName());
      return;
    }
    double types = Utilities.saveExistingMeasureToContext(sensorContext, buildUnitmetrics,
        SonargraphStandaloneMetricNames.INTERNAL_TYPES, SonargraphSimpleMetrics.INTERNAL_TYPES, NO_DECIMAL).getValue();
    assert types >= 1.0 : "Project must not be empty !";

    Utilities.saveExistingMeasureToContext(sensorContext, buildUnitmetrics,
        SonargraphStandaloneMetricNames.VIOLATING_DEPENDENCIES, SonargraphSimpleMetrics.VIOLATING_TYPE_DEPENDENCIES,
        NO_DECIMAL);
    Measure violatingTypes = Utilities.saveExistingMeasureToContext(sensorContext, buildUnitmetrics,
        SonargraphStandaloneMetricNames.VIOLATING_TYPES, SonargraphSimpleMetrics.VIOLATING_TYPES, NO_DECIMAL);
    double violatingTypesPercent = HUNDRET_PERCENT * violatingTypes.getValue() / types;
    Utilities.saveMeasureToContext(sensorContext, SonargraphDerivedMetrics.VIOLATING_TYPES_PERCENT,
        violatingTypesPercent, 1);
    Utilities.saveExistingMeasureToContext(sensorContext, buildUnitmetrics,
        SonargraphStandaloneMetricNames.VIOLATING_REFERENCES, SonargraphSimpleMetrics.VIOLATING_REFERENCES, NO_DECIMAL);
    Utilities.saveExistingMeasureToContext(sensorContext, buildUnitmetrics,
        SonargraphStandaloneMetricNames.IGNORED_VIOLATIONS, SonargraphSimpleMetrics.IGNORED_VIOLATONS, NO_DECIMAL);
    Measure unassignedTypes = Utilities.saveExistingMeasureToContext(sensorContext, buildUnitmetrics,
        SonargraphStandaloneMetricNames.UNASSIGNED_TYPES, SonargraphSimpleMetrics.UNASSIGNED_TYPES, NO_DECIMAL);
    double unassignedTypesPercent = HUNDRET_PERCENT * unassignedTypes.getValue() / types;
    Utilities.saveMeasureToContext(sensorContext, SonargraphDerivedMetrics.UNASSIGNED_TYPES_PERCENT,
        unassignedTypesPercent, 1);
  }

  private void analyseWarnings(Project project) {
    if (project.getQualifier().equals(Qualifiers.MODULE)) {
      LOG.debug("Values for warning metrics are only taken from build unit section for child module projects.");
      Utilities.saveExistingMeasureToContext(sensorContext, buildUnitmetrics,
          SonargraphStandaloneMetricNames.ALL_WARNINGS, SonargraphSimpleMetrics.ALL_WARNINGS, NO_DECIMAL);
      Utilities.saveExistingMeasureToContext(sensorContext, buildUnitmetrics,
          SonargraphStandaloneMetricNames.CYCLE_WARNINGS, SonargraphSimpleMetrics.CYCLE_WARNINGS, NO_DECIMAL);
      Utilities.saveExistingMeasureToContext(sensorContext, buildUnitmetrics,
          SonargraphStandaloneMetricNames.DUPLICATE_WARNINGS, SonargraphSimpleMetrics.DUPLICATE_WARNINGS, NO_DECIMAL);
      Utilities.saveExistingMeasureToContext(sensorContext, buildUnitmetrics,
          SonargraphStandaloneMetricNames.WORKSPACE_WARNINGS, SonargraphSimpleMetrics.WORKSPACE_WARNINGS, NO_DECIMAL);
      Utilities.saveExistingMeasureToContext(sensorContext, buildUnitmetrics,
          SonargraphStandaloneMetricNames.THRESHOLD_WARNINGS, SonargraphSimpleMetrics.THRESHOLD_WARNINGS, NO_DECIMAL,
          false);
      Utilities.saveExistingMeasureToContext(sensorContext, buildUnitmetrics,
          SonargraphStandaloneMetricNames.IGNORED_WARNINGS, SonargraphSimpleMetrics.IGNORED_WARNINGS, NO_DECIMAL);

      /*
       * Save overall system warnings to internal metrics. The decorator executed on the root parent module will retrieve them and store
       * them in the visible metrics.
       */
      Utilities.saveExistingMeasureToContext(sensorContext, systemMetrics,
          SonargraphStandaloneMetricNames.ALL_WARNINGS, SonargraphInternalMetrics.SYSTEM_ALL_WARNINGS, NO_DECIMAL);
      Utilities.saveExistingMeasureToContext(sensorContext, systemMetrics,
          SonargraphStandaloneMetricNames.CYCLE_WARNINGS, SonargraphInternalMetrics.SYSTEM_CYCLE_WARNINGS, NO_DECIMAL);
      Utilities.saveExistingMeasureToContext(sensorContext, systemMetrics,
          SonargraphStandaloneMetricNames.THRESHOLD_WARNINGS, SonargraphInternalMetrics.SYSTEM_THRESHOLD_WARNINGS,
          NO_DECIMAL);
      Utilities.saveExistingMeasureToContext(sensorContext, systemMetrics,
          SonargraphStandaloneMetricNames.WORKSPACE_WARNINGS, SonargraphInternalMetrics.SYSTEM_WORKSPACE_WARNINGS,
          NO_DECIMAL);
      Utilities.saveExistingMeasureToContext(sensorContext, systemMetrics,
          SonargraphStandaloneMetricNames.IGNORED_WARNINGS, SonargraphInternalMetrics.SYSTEM_IGNORED_WARNINGS,
          NO_DECIMAL);

    } else {
      LOG.debug("Values for warning metrics are only taken from general section to also include logical cycle group warnings.");
      Utilities.saveExistingMeasureToContext(sensorContext, systemMetrics,
          SonargraphStandaloneMetricNames.ALL_WARNINGS, SonargraphSimpleMetrics.ALL_WARNINGS, NO_DECIMAL);
      Utilities.saveExistingMeasureToContext(sensorContext, systemMetrics,
          SonargraphStandaloneMetricNames.CYCLE_WARNINGS, SonargraphSimpleMetrics.CYCLE_WARNINGS, NO_DECIMAL);
      Utilities.saveExistingMeasureToContext(sensorContext, systemMetrics,
          SonargraphStandaloneMetricNames.DUPLICATE_WARNINGS, SonargraphSimpleMetrics.DUPLICATE_WARNINGS, NO_DECIMAL);
      Utilities.saveExistingMeasureToContext(sensorContext, systemMetrics,
          SonargraphStandaloneMetricNames.WORKSPACE_WARNINGS, SonargraphSimpleMetrics.WORKSPACE_WARNINGS, NO_DECIMAL);
      Utilities.saveExistingMeasureToContext(sensorContext, systemMetrics,
          SonargraphStandaloneMetricNames.THRESHOLD_WARNINGS, SonargraphSimpleMetrics.THRESHOLD_WARNINGS, NO_DECIMAL);
      Utilities.saveExistingMeasureToContext(sensorContext, systemMetrics,
          SonargraphStandaloneMetricNames.IGNORED_WARNINGS, SonargraphSimpleMetrics.IGNORED_WARNINGS, NO_DECIMAL);
    }
  }
}
