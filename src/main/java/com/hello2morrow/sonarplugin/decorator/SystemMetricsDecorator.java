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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sonar.api.batch.Decorator;
import org.sonar.api.batch.DecoratorContext;
import org.sonar.api.measures.Measure;
import org.sonar.api.resources.Project;
import org.sonar.api.resources.Qualifiers;
import org.sonar.api.resources.Resource;

import com.hello2morrow.sonarplugin.foundation.Utilities;
import com.hello2morrow.sonarplugin.metric.SonargraphBuildUnitMetrics;
import com.hello2morrow.sonarplugin.metric.SonargraphSystemMetrics;

public class SystemMetricsDecorator implements Decorator {

  private static final Logger LOG = LoggerFactory.getLogger(SystemMetricsDecorator.class);

  public boolean shouldExecuteOnProject(Project project) {
    return true;
  }

  public void decorate(@SuppressWarnings("rawtypes") Resource resource, DecoratorContext context) {
    if ( !Utilities.isAggregationProject(context, SonargraphBuildUnitMetrics.INSTRUCTIONS)) {
      return;
    }

    double biggestCycleGroupSize = -1.0;
    double highestACD = -1.0;
    double highestNCCD = -1.0;

    int childContextCounter = 0;

    for (DecoratorContext childContext : context.getChildren()) {

      Measure m = childContext.getMeasure(SonargraphBuildUnitMetrics.MODULE_NOT_PART_OF_SONARGRAPH_WORKSPACE);
      if (m != null) {
        LOG.warn("Skipping module [" + childContext.getResource().getName() + "] because it is not part of the Sonargraph workspace.");
        continue;
      }
      if (0 == childContextCounter) {
        List<Measure> measures = new ArrayList<Measure>();
        measures.add(childContext.getMeasure(SonargraphSystemMetrics.ARCHITECTURE_VIOLATIONS));
        measures.add(childContext.getMeasure(SonargraphSystemMetrics.CYCLE_WARNINGS));
        measures.add(childContext.getMeasure(SonargraphSystemMetrics.WORKSPACE_WARNINGS));
        measures.add(childContext.getMeasure(SonargraphSystemMetrics.ALL_WARNINGS));
        saveMeasuresToContext(context, measures);
      }

      Measure cycleGroup = childContext.getMeasure(SonargraphSystemMetrics.BIGGEST_CYCLE_GROUP);
      Measure acd = childContext.getMeasure(SonargraphSystemMetrics.ACD);
      Measure nccd = childContext.getMeasure(SonargraphSystemMetrics.NCCD);
      Measure localHighestACD = childContext.getMeasure(SonargraphSystemMetrics.HIGHEST_ACD);
      Measure localHighestNCCD = childContext.getMeasure(SonargraphSystemMetrics.HIGHEST_NCCD);

      biggestCycleGroupSize = getBiggerValue(biggestCycleGroupSize, cycleGroup);
      highestACD = getBiggerValue(highestACD, acd, localHighestACD);
      highestNCCD = getBiggerValue(highestNCCD, nccd, localHighestNCCD);

      childContextCounter++;
    }

    context.saveMeasure(SonargraphSystemMetrics.BIGGEST_CYCLE_GROUP, biggestCycleGroupSize);
    context.saveMeasure(SonargraphSystemMetrics.HIGHEST_ACD, highestACD);
    context.saveMeasure(SonargraphSystemMetrics.HIGHEST_NCCD, highestNCCD);

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
    Measure violatingTypes = context.getMeasure(SonargraphBuildUnitMetrics.VIOLATING_TYPES);
    if (null != violatingTypes) {
      LOG.debug("Number of violating types: " + violatingTypes.getValue());
    }
    Measure internalTypes = context.getMeasure(SonargraphBuildUnitMetrics.INTERNAL_TYPES);
    if (null != violatingTypes) {
      LOG.debug("Number of internal types: " + internalTypes.getValue());
    }
    Measure unassignedTypes = context.getMeasure(SonargraphBuildUnitMetrics.UNASSIGNED_TYPES);

    if (internalTypes != null && internalTypes.getValue() > 0) {
      if (violatingTypes != null) {
        context.saveMeasure(SonargraphSystemMetrics.VIOLATING_TYPES_PERCENT, 100.0 * violatingTypes.getValue()
            / internalTypes.getValue());
      }
      if (unassignedTypes != null) {
        context.saveMeasure(SonargraphSystemMetrics.UNASSIGNED_TYPES_PERCENT, 100 * unassignedTypes.getValue()
            / internalTypes.getValue());
      }
    }
  }

  private void saveCyclicityMeasures(DecoratorContext context) {
    Measure cyclicity = context.getMeasure(SonargraphBuildUnitMetrics.CYCLICITY);
    Measure packages = context.getMeasure(SonargraphBuildUnitMetrics.INTERNAL_PACKAGES);
    Measure cyclicPackages = context.getMeasure(SonargraphBuildUnitMetrics.CYCLIC_PACKAGES);

    if (cyclicity == null || packages == null || cyclicPackages == null) {
      LOG.error("Problem in aggregator (cannot calculate relative cyclicity values) on project: "
          + context.getProject().getKey());
    } else {
      double relCyclicity = 100.0 * Math.sqrt(cyclicity.getValue()) / packages.getValue();
      double relCyclicPackages = 100.0 * cyclicPackages.getValue() / packages.getValue();

      context.saveMeasure(SonargraphSystemMetrics.RELATIVE_CYCLICITY, relCyclicity);
      context.saveMeasure(SonargraphSystemMetrics.CYCLIC_PACKAGES_PERCENT, relCyclicPackages);
    }
  }

  private void saveMeasuresToContext(DecoratorContext context, List<Measure> measures) {
    for (Measure measure : measures) {
      context.saveMeasure(measure.getMetric(), measure.getValue());
    }
  }

  public boolean shouldDecorateResource(@SuppressWarnings("rawtypes") Resource resource) {
    LOG.debug("Checking for resource type: " + resource.getQualifier());
    return Arrays.asList(Qualifiers.PROJECT, Qualifiers.MODULE, Qualifiers.VIEW, Qualifiers.SUBVIEW).contains(
        resource.getQualifier());
  }

}
