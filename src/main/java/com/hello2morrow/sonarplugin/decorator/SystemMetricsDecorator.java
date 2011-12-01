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

  // @DependedUpon
  // public List<Metric> generatesMetrics() {
  // return Arrays.asList(
  // }
  //

  public void decorate(@SuppressWarnings("rawtypes") Resource resource, DecoratorContext context) {
    if ( !Utilities.isAggregationProject(context, SonargraphBuildUnitMetrics.INSTRUCTIONS)) {
      return;
    }

    double biggestCycleGroupSize = -1.0;
    double highestACD = -1.0;
    double highestNCCD = -1.0;

    int childContextCounter = 0;

    for (DecoratorContext childContext : context.getChildren()) {

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

      if (cycleGroup != null && cycleGroup.getValue() > biggestCycleGroupSize) {
        biggestCycleGroupSize = cycleGroup.getValue();
      }

      if (acd != null && acd.getValue() > highestACD) {
        highestACD = acd.getValue();
      } else if (localHighestACD != null && localHighestACD.getValue() > highestACD) {
        highestACD = localHighestACD.getValue();
      }

      if (nccd != null && nccd.getValue() > highestNCCD) {
        highestNCCD = nccd.getValue();
      } else if (localHighestNCCD != null && localHighestNCCD.getValue() > highestNCCD) {
        highestNCCD = localHighestNCCD.getValue();
      }
      childContextCounter++;
    }

    if (biggestCycleGroupSize >= 0.0 && context.getMeasure(SonargraphSystemMetrics.BIGGEST_CYCLE_GROUP) == null) {
      context.saveMeasure(SonargraphSystemMetrics.BIGGEST_CYCLE_GROUP, biggestCycleGroupSize);
    }

    if (highestACD >= 0.0 && context.getMeasure(SonargraphSystemMetrics.HIGHEST_ACD) == null) {
      context.saveMeasure(SonargraphSystemMetrics.HIGHEST_ACD, highestACD);
    }

    if (highestNCCD >= 0.0 && context.getMeasure(SonargraphSystemMetrics.HIGHEST_NCCD) == null) {
      context.saveMeasure(SonargraphSystemMetrics.HIGHEST_NCCD, highestNCCD);
    }

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

    Measure violatingTypes = context.getMeasure(SonargraphBuildUnitMetrics.VIOLATING_TYPES);
    LOG.debug("Number of violating types: " + violatingTypes.getValue());
    Measure internalTypes = context.getMeasure(SonargraphBuildUnitMetrics.INTERNAL_TYPES);
    LOG.debug("Number of internal types: " + internalTypes.getValue());
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

    AlertDecorator.setAlertLevels(new DecoratorProjectContext(context));
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
