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
package com.hello2morrow.sonarplugin.decorator;

import com.hello2morrow.sonarplugin.foundation.Utilities;
import com.hello2morrow.sonarplugin.metric.SonargraphAlertThresholds;
import com.hello2morrow.sonarplugin.metric.SonargraphDerivedMetrics;
import com.hello2morrow.sonarplugin.metric.SonargraphSimpleMetrics;
import com.hello2morrow.sonarplugin.metric.internal.SonargraphInternalMetrics;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sonar.api.batch.Decorator;
import org.sonar.api.batch.DecoratorContext;
import org.sonar.api.measures.Measure;
import org.sonar.api.measures.Metric;
import org.sonar.api.profiles.RulesProfile;
import org.sonar.api.resources.Project;
import org.sonar.api.resources.Qualifiers;
import org.sonar.api.resources.Resource;

import java.util.Arrays;
import java.util.List;

public class SonargraphDerivedMetricsDecorator implements Decorator {

  private static final List<String> RESOURCES_TO_DECORATE = Arrays.asList(Qualifiers.PROJECT, Qualifiers.MODULE, Qualifiers.VIEW, Qualifiers.SUBVIEW);
  private static final double HUNDRET_PERCENT = 100.0;
  private static final Logger LOG = LoggerFactory.getLogger(SonargraphDerivedMetricsDecorator.class);
  private final RulesProfile profile;

  public SonargraphDerivedMetricsDecorator(RulesProfile profile) {
    this.profile = profile;
  }

  @Override
  public boolean shouldExecuteOnProject(Project project) {
    return (project.getQualifier().equals(Qualifiers.PROJECT) || Utilities.isAggregatingProject(project)) && Utilities.areSonargraphRulesActive(profile);
  }

  @Override
  public void decorate(Resource resource, DecoratorContext context) {
    if (!Utilities.isAggregationProject(context, SonargraphSimpleMetrics.INSTRUCTIONS)) {
      return;
    }

    double biggestCycleGroupSize = -1.0;
    double highestACD = -1.0;
    double highestRelativeACD = -1.0;
    double highestNCCD = -1.0;

    for (DecoratorContext childContext : context.getChildren()) {

      Measure m = childContext.getMeasure(SonargraphInternalMetrics.MODULE_NOT_PART_OF_SONARGRAPH_WORKSPACE);
      if (m != null) {
        LOG.warn("Skipping module [" + childContext.getResource().getName() + "] because it is not part of the Sonargraph workspace or does not contain any code.");
        continue;
      }

      Measure cycleGroup = childContext.getMeasure(SonargraphDerivedMetrics.BIGGEST_CYCLE_GROUP);
      Measure acd = childContext.getMeasure(SonargraphSimpleMetrics.ACD);
      Measure relativeACD = childContext.getMeasure(SonargraphSimpleMetrics.RELATIVE_ACD);
      Measure nccd = childContext.getMeasure(SonargraphSimpleMetrics.NCCD);

      Measure localHighestACD = childContext.getMeasure(SonargraphDerivedMetrics.HIGHEST_ACD);
      Measure localHighestNCCD = childContext.getMeasure(SonargraphDerivedMetrics.HIGHEST_NCCD);
      Measure localHighestRelativeACD = childContext.getMeasure(SonargraphDerivedMetrics.HIGHEST_RELATIVE_ACD);

      biggestCycleGroupSize = getBiggerValue(biggestCycleGroupSize, cycleGroup);
      highestACD = getBiggerValue(highestACD, acd, localHighestACD);
      highestRelativeACD = getBiggerValue(highestRelativeACD, relativeACD, localHighestRelativeACD);
      highestNCCD = getBiggerValue(highestNCCD, nccd, localHighestNCCD);
    }

    context.saveMeasure(createMeasure(context, SonargraphDerivedMetrics.BIGGEST_CYCLE_GROUP, biggestCycleGroupSize, 0));
    context.saveMeasure(createMeasure(context, SonargraphDerivedMetrics.HIGHEST_NCCD, highestNCCD, 1));
    context.saveMeasure(createMeasure(context, SonargraphDerivedMetrics.HIGHEST_ACD, highestACD, 1));
    context.saveMeasure(createMeasure(context, SonargraphDerivedMetrics.HIGHEST_RELATIVE_ACD, highestRelativeACD, 1));

    saveCyclicityMeasures(context);
    saveTypeMeasures(context);
  }

  private Measure createMeasure(DecoratorContext context, Metric metric, double value, int precision) {
    Measure measureToSave = new Measure(metric, value, precision);
    SonargraphAlertThresholds.addAlertToMeasure(context, measureToSave, value);
    return measureToSave;
  }

  double getBiggerValue(double currentHighestValue, Measure measure, Measure localHighestMeasure) {
    if (measure != null && measure.getValue() > currentHighestValue) {
      return measure.getValue();
    } else {
      return getBiggerValue(currentHighestValue, localHighestMeasure);
    }
  }

  double getBiggerValue(double currentBiggestValue, Measure measure) {
    if (measure != null && measure.getValue() > currentBiggestValue) {
      return measure.getValue();
    } else {
      return currentBiggestValue;
    }
  }

  private void saveTypeMeasures(DecoratorContext context) {
    Measure violatingTypes = context.getMeasure(SonargraphSimpleMetrics.VIOLATING_TYPES);
    if (null != violatingTypes) {
      LOG.debug("Number of violating types: " + violatingTypes.getValue());
    }
    Measure internalTypes = context.getMeasure(SonargraphSimpleMetrics.INTERNAL_TYPES);
    if (null != internalTypes) {
      LOG.debug("Number of internal types: " + internalTypes.getValue());
    }
    Measure unassignedTypes = context.getMeasure(SonargraphSimpleMetrics.UNASSIGNED_TYPES);

    if (internalTypes != null && internalTypes.getValue() > 0) {
      if (violatingTypes != null) {
        context.saveMeasure(SonargraphDerivedMetrics.VIOLATING_TYPES_PERCENT, HUNDRET_PERCENT * violatingTypes.getValue() / internalTypes.getValue());
      }
      if (unassignedTypes != null) {
        context.saveMeasure(SonargraphDerivedMetrics.UNASSIGNED_TYPES_PERCENT, HUNDRET_PERCENT * unassignedTypes.getValue() / internalTypes.getValue());
      }
    }
  }

  private void saveCyclicityMeasures(DecoratorContext context) {
    Measure cyclicity = context.getMeasure(SonargraphSimpleMetrics.CYCLICITY);
    Measure packages = context.getMeasure(SonargraphSimpleMetrics.INTERNAL_PACKAGES);
    Measure cyclicPackages = context.getMeasure(SonargraphSimpleMetrics.CYCLIC_PACKAGES);

    if (cyclicity == null || packages == null || cyclicPackages == null) {
      LOG.error("Problem in aggregator (cannot calculate relative cyclicity values) on project: " + context.getProject().getKey());
    } else {
      double relCyclicity = 0.0;
      double relCyclicPackages = 0.0;

      double numberOfPackages = packages.getValue().doubleValue();
      if (numberOfPackages > 0.0) {
        relCyclicity = HUNDRET_PERCENT * Math.sqrt(cyclicity.getValue()) / numberOfPackages;
        relCyclicPackages = HUNDRET_PERCENT * cyclicPackages.getValue() / numberOfPackages;
      }

      context.saveMeasure(createMeasure(context, SonargraphDerivedMetrics.RELATIVE_CYCLICITY, relCyclicity, 1));
      context.saveMeasure(createMeasure(context, SonargraphDerivedMetrics.CYCLIC_PACKAGES_PERCENT, relCyclicPackages, 1));

      Measure measure = context.getMeasure(SonargraphSimpleMetrics.CYCLICITY);
      SonargraphAlertThresholds.addAlertToMeasure(context, measure, measure.getValue());
      context.saveMeasure(measure);
    }
  }

  public boolean shouldDecorateResource(Resource resource) {
    LOG.debug("Checking for resource type: " + resource.getQualifier());
    return RESOURCES_TO_DECORATE.contains(resource.getQualifier());
  }
}
