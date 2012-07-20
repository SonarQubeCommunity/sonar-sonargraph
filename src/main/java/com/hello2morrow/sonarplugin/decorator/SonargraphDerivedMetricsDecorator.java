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

import java.util.Arrays;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sonar.api.batch.Decorator;
import org.sonar.api.batch.DecoratorContext;
import org.sonar.api.measures.Measure;
import org.sonar.api.resources.Project;
import org.sonar.api.resources.Qualifiers;
import org.sonar.api.resources.Resource;

import com.hello2morrow.sonarplugin.foundation.Utilities;
import com.hello2morrow.sonarplugin.metric.SonargraphSimpleMetrics;
import com.hello2morrow.sonarplugin.metric.SonargraphDerivedMetrics;
import com.hello2morrow.sonarplugin.metric.internal.SonargraphInternalMetrics;

public class SonargraphDerivedMetricsDecorator implements Decorator {

  private static final Logger LOG = LoggerFactory.getLogger(SonargraphDerivedMetricsDecorator.class);

  public boolean shouldExecuteOnProject(Project project) {
    return true;
  }

  public void decorate(@SuppressWarnings("rawtypes") Resource resource, DecoratorContext context) {
    if ( !Utilities.isAggregationProject(context, SonargraphSimpleMetrics.INSTRUCTIONS)) {
      return;
    }

    double biggestCycleGroupSize = -1.0;
    double highestACD = -1.0;
    double highestRelativeACD = -1.0;
    double highestNCCD = -1.0;

    for (DecoratorContext childContext : context.getChildren()) {

      Measure m = childContext.getMeasure(SonargraphInternalMetrics.MODULE_NOT_PART_OF_SONARGRAPH_WORKSPACE);
      if (m != null) {
        LOG.warn("Skipping module [" + childContext.getResource().getName()
            + "] because it is not part of the Sonargraph workspace or does not contain any code.");
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

    context.saveMeasure(SonargraphDerivedMetrics.BIGGEST_CYCLE_GROUP, biggestCycleGroupSize);
    context.saveMeasure(SonargraphDerivedMetrics.HIGHEST_ACD, highestACD);
    context.saveMeasure(SonargraphDerivedMetrics.HIGHEST_RELATIVE_ACD, highestRelativeACD);
    context.saveMeasure(SonargraphDerivedMetrics.HIGHEST_NCCD, highestNCCD);

    saveCyclicityMeasures(context);
    saveTypeMeasures(context);

    AlertDecorator.setAlertLevels(new DecoratorProjectContext(context));
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
    if (null != violatingTypes) {
      LOG.debug("Number of internal types: " + internalTypes.getValue());
    }
    Measure unassignedTypes = context.getMeasure(SonargraphSimpleMetrics.UNASSIGNED_TYPES);

    if (internalTypes != null && internalTypes.getValue() > 0) {
      if (violatingTypes != null) {
        context.saveMeasure(SonargraphDerivedMetrics.VIOLATING_TYPES_PERCENT, 100.0 * violatingTypes.getValue()
            / internalTypes.getValue());
      }
      if (unassignedTypes != null) {
        context.saveMeasure(SonargraphDerivedMetrics.UNASSIGNED_TYPES_PERCENT, 100 * unassignedTypes.getValue()
            / internalTypes.getValue());
      }
    }
  }

  private void saveCyclicityMeasures(DecoratorContext context) {
    Measure cyclicity = context.getMeasure(SonargraphSimpleMetrics.CYCLICITY);
    Measure packages = context.getMeasure(SonargraphSimpleMetrics.INTERNAL_PACKAGES);
    Measure cyclicPackages = context.getMeasure(SonargraphSimpleMetrics.CYCLIC_PACKAGES);

    if (cyclicity == null || packages == null || cyclicPackages == null) {
      LOG.error("Problem in aggregator (cannot calculate relative cyclicity values) on project: "
          + context.getProject().getKey());
    } else {
      double relCyclicity = 100.0 * Math.sqrt(cyclicity.getValue()) / packages.getValue();
      double relCyclicPackages = 100.0 * cyclicPackages.getValue() / packages.getValue();

      context.saveMeasure(SonargraphDerivedMetrics.RELATIVE_CYCLICITY, relCyclicity);
      context.saveMeasure(SonargraphDerivedMetrics.CYCLIC_PACKAGES_PERCENT, relCyclicPackages);
    }
  }

  public boolean shouldDecorateResource(@SuppressWarnings("rawtypes") Resource resource) {
    LOG.debug("Checking for resource type: " + resource.getQualifier());
    return Arrays.asList(Qualifiers.PROJECT, Qualifiers.MODULE, Qualifiers.VIEW, Qualifiers.SUBVIEW).contains(
        resource.getQualifier());
  }

}
