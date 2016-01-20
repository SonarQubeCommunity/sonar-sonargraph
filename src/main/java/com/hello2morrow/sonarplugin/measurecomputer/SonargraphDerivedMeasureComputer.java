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
package com.hello2morrow.sonarplugin.measurecomputer;

import com.hello2morrow.sonarplugin.foundation.SonarQubeUtilities;
import com.hello2morrow.sonarplugin.metric.SonargraphDerivedMetrics;
import com.hello2morrow.sonarplugin.metric.SonargraphSimpleMetrics;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sonar.api.ce.measure.Measure;
import org.sonar.api.measures.Metric;

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;

/**
 * Computes all values for an aggregating root project based on the values from modules
 */
public class SonargraphDerivedMeasureComputer extends SonargraphMeasureComputer {

  private static final Logger LOGGER = LoggerFactory.getLogger(SonargraphDerivedMeasureComputer.class);

  private static final double HUNDRET_PERCENT = 100.0;

  @Override
  List<String> getInputMetrics() {
    List<Metric<Serializable>> metrics = Arrays.asList(SonargraphSimpleMetrics.VIOLATING_TYPES, SonargraphSimpleMetrics.INTERNAL_TYPES, SonargraphSimpleMetrics.UNASSIGNED_TYPES,
      SonargraphSimpleMetrics.INTERNAL_PACKAGES, SonargraphSimpleMetrics.CYCLIC_PACKAGES, SonargraphSimpleMetrics.CYCLICITY);
    return SonarQubeUtilities.convertMetricListToKeyList(metrics);
  }

  @Override
  List<String> getOutputMetrics() {
    List<Metric<Serializable>> metrics = Arrays.asList(SonargraphDerivedMetrics.VIOLATING_TYPES_PERCENT, SonargraphDerivedMetrics.UNASSIGNED_TYPES_PERCENT,
      SonargraphDerivedMetrics.RELATIVE_CYCLICITY, SonargraphDerivedMetrics.CYCLIC_PACKAGES_PERCENT);
    return SonarQubeUtilities.convertMetricListToKeyList(metrics);
  }

  @Override
  void internalCompute(MeasureComputerContext context) {
    saveViolationMeasures(context);
    saveCyclicityMeasures(context);
  }

  private void saveViolationMeasures(MeasureComputerContext context) {
    Measure violatingTypes = context.getMeasure(SonargraphSimpleMetrics.VIOLATING_TYPES.key());
    if (null != violatingTypes) {
      LOGGER.warn("Number of violating types: " + violatingTypes.getIntValue());
    } else {
      LOGGER.error("Measure for violating types not detected");
    }

    Measure internalTypes = context.getMeasure(SonargraphSimpleMetrics.INTERNAL_TYPES.key());
    if (null != internalTypes) {
      LOGGER.warn("Number of internal types: " + internalTypes.getIntValue());
    } else {
      LOGGER.error("Measure for internalTypes not detected");
    }

    Measure unassignedTypes = context.getMeasure(SonargraphSimpleMetrics.UNASSIGNED_TYPES.key());

    if (internalTypes != null && internalTypes.getIntValue() > 0) {
      if (violatingTypes != null) {
        context.addMeasure(SonargraphDerivedMetrics.VIOLATING_TYPES_PERCENT.key(), HUNDRET_PERCENT * violatingTypes.getIntValue() / internalTypes.getIntValue());
      }
      if (unassignedTypes != null) {
        context.addMeasure(SonargraphDerivedMetrics.UNASSIGNED_TYPES_PERCENT.key(), HUNDRET_PERCENT * unassignedTypes.getIntValue() / internalTypes.getIntValue());
      }
    }
  }

  private void saveCyclicityMeasures(MeasureComputerContext context) {
    Measure cyclicity = context.getMeasure(SonargraphSimpleMetrics.CYCLICITY.key());
    Measure packages = context.getMeasure(SonargraphSimpleMetrics.INTERNAL_PACKAGES.key());
    Measure cyclicPackages = context.getMeasure(SonargraphSimpleMetrics.CYCLIC_PACKAGES.key());

    if (cyclicity == null || packages == null || cyclicPackages == null) {
      LOGGER.error("Problem in aggregator (cannot calculate relative cyclicity values) on project: " + context.getComponent().getKey());
    } else {
      double numberOfPackages = packages.getIntValue();
      if (numberOfPackages > 0.0) {
        double relCyclicity = HUNDRET_PERCENT * Math.sqrt(cyclicity.getIntValue()) / numberOfPackages;
        double relCyclicPackages = HUNDRET_PERCENT * cyclicPackages.getIntValue() / numberOfPackages;
        context.addMeasure(SonargraphDerivedMetrics.RELATIVE_CYCLICITY.key(), relCyclicity);
        context.addMeasure(SonargraphDerivedMetrics.CYCLIC_PACKAGES_PERCENT.key(), relCyclicPackages);
      }
    }
  }
}
