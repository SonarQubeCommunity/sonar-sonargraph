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

package com.hello2morrow.sonarplugin;

import java.util.Arrays;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sonar.api.batch.AbstractSumChildrenDecorator;
import org.sonar.api.batch.DecoratorContext;
import org.sonar.api.batch.DependedUpon;
import org.sonar.api.measures.Measure;
import org.sonar.api.measures.Metric;
import org.sonar.api.resources.Qualifiers;
import org.sonar.api.resources.Resource;

public final class SonargraphMetricAggregator extends AbstractSumChildrenDecorator {

  private static final Logger LOG = LoggerFactory.getLogger(SonargraphMetricAggregator.class);

  @Override
  @DependedUpon
  public List<Metric> generatesMetrics() {
    
    /* Some of these metrics could also be directly retrieved from the "overview" section
     * of the generated report, e.g. internal types, instructions... 
     * But it is safe to add up the values from the different build units being analyzed.
     */
    return Arrays.asList(SonargraphMetrics.CYCLICITY, SonargraphMetrics.CYCLIC_PACKAGES, 
        SonargraphMetrics.INSTRUCTIONS, 
        SonargraphMetrics.UNASSIGNED_TYPES, SonargraphMetrics.EROSION_REFS, 
        SonargraphMetrics.EROSION_TYPES, SonargraphMetrics.EROSION_COST,
        SonargraphMetrics.EROSION_INDEX, SonargraphMetrics.VIOLATING_TYPES, 
        SonargraphMetrics.INTERNAL_TYPES, SonargraphMetrics.VIOLATING_DEPENDENCIES,
        SonargraphMetrics.TYPE_DEPENDENCIES, SonargraphMetrics.JAVA_FILES, 
        SonargraphMetrics.TASKS, SonargraphMetrics.TASK_REFS, 
        SonargraphMetrics.THRESHOLD_WARNINGS, SonargraphMetrics.DUPLICATE_WARNINGS,
        SonargraphMetrics.IGNORED_VIOLATONS, SonargraphMetrics.IGNORED_WARNINGS, 
        SonargraphMetrics.WORKSPACE_WARNINGS);
       
    
    //TODO: Move to other decorator: SonargraphMetrics.ALL_WARNINGS, SonargraphMetrics.INTERNAL_PACKAGES, 
  }

  @Override
  protected boolean shouldSaveZeroIfNoChildMeasures() {
    return false;
  }

  @Override
  public void decorate(@SuppressWarnings("rawtypes") Resource resource, DecoratorContext context) {
    if ( !shouldDecorateResource(resource)) {
      return;
    }
    if (context.getChildrenMeasures(SonargraphMetrics.INSTRUCTIONS).size() == 0) {
      return;
    }
    super.decorate(resource, context);

    double biggestCycleGroupSize = -1.0;
    double highestACD = -1.0;
    double highestNCCD = -1.0;
    double architectureViolations = 0.0;
    double numberOfCycleWarnings = 0.0;

    for (DecoratorContext childContext : context.getChildren()) {
      /**
       * architecture violations and cycle warnings are retrieved directly from the top level report attribute "NumberOfViolatingReferences" and saved to the
       * top-level context
       */
      Measure violations = childContext.getMeasure(SonargraphMetrics.ARCHITECTURE_VIOLATIONS);
      if (null != violations && violations.getValue() > 0) {
        architectureViolations = violations.getValue();
      }

      Measure cycleWarningMetric = childContext.getMeasure(SonargraphMetrics.CYCLE_WARNINGS);
      if (null != cycleWarningMetric && cycleWarningMetric.getValue() > 0) {
        numberOfCycleWarnings = cycleWarningMetric.getValue();
      }

      Measure cycleGroup = childContext.getMeasure(SonargraphMetrics.BIGGEST_CYCLE_GROUP);
      Measure acd = childContext.getMeasure(SonargraphMetrics.ACD);
      Measure nccd = childContext.getMeasure(SonargraphMetrics.NCCD);
      Measure localHighestACD = childContext.getMeasure(SonargraphMetrics.HIGHEST_ACD);
      Measure localHighestNCCD = childContext.getMeasure(SonargraphMetrics.HIGHEST_NCCD);

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
    }

    context.saveMeasure(SonargraphMetrics.CYCLE_WARNINGS, numberOfCycleWarnings);
    context.saveMeasure(SonargraphMetrics.ARCHITECTURE_VIOLATIONS, architectureViolations);

    if (biggestCycleGroupSize >= 0.0 && context.getMeasure(SonargraphMetrics.BIGGEST_CYCLE_GROUP) == null) {
      context.saveMeasure(SonargraphMetrics.BIGGEST_CYCLE_GROUP, biggestCycleGroupSize);
    }

    if (highestACD >= 0.0 && context.getMeasure(SonargraphMetrics.HIGHEST_ACD) == null) {
      context.saveMeasure(SonargraphMetrics.HIGHEST_ACD, highestACD);
    }

    if (highestNCCD >= 0.0 && context.getMeasure(SonargraphMetrics.HIGHEST_NCCD) == null) {
      context.saveMeasure(SonargraphMetrics.HIGHEST_NCCD, highestNCCD);
    }

    Measure cyclicity = context.getMeasure(SonargraphMetrics.CYCLICITY);
    Measure packages = context.getMeasure(SonargraphMetrics.INTERNAL_PACKAGES);
    Measure cyclicPackages = context.getMeasure(SonargraphMetrics.CYCLIC_PACKAGES);

    if (cyclicity == null || packages == null || cyclicPackages == null) {
      LOG.error("Problem in aggregator on project: " + context.getProject().getKey());
    } else {
      double relCyclicity = 100.0 * Math.sqrt(cyclicity.getValue()) / packages.getValue();
      double relCyclicPackages = 100.0 * cyclicPackages.getValue() / packages.getValue();

      context.saveMeasure(SonargraphMetrics.RELATIVE_CYCLICITY, relCyclicity);
      context.saveMeasure(SonargraphMetrics.CYCLIC_PACKAGES_PERCENT, relCyclicPackages);
    }

    Measure violatingTypes = context.getMeasure(SonargraphMetrics.VIOLATING_TYPES);
    LOG.info("Number of violating types: " + violatingTypes);

    Measure internalTypes = context.getMeasure(SonargraphMetrics.INTERNAL_TYPES);
    Measure unassignedTypes = context.getMeasure(SonargraphMetrics.UNASSIGNED_TYPES);

    if (internalTypes != null && internalTypes.getValue() > 0) {
      if (violatingTypes != null) {
        context.saveMeasure(SonargraphMetrics.VIOLATING_TYPES_PERCENT, 100.0 * violatingTypes.getValue()
            / internalTypes.getValue());
      }
      if (unassignedTypes != null) {
        context.saveMeasure(SonargraphMetrics.UNASSIGNED_TYPES_PERCENT, 100 * unassignedTypes.getValue()
            / internalTypes.getValue());
      }
    }
    AlertDecorator.setAlertLevels(new DecoratorProjectContext(context));
  }

  @Override
  public boolean shouldDecorateResource(@SuppressWarnings("rawtypes") Resource resource) {
    LOG.info("Checking for resource type: " + resource.getQualifier());
    return Arrays.asList(Qualifiers.PROJECT, Qualifiers.MODULE, Qualifiers.VIEW, Qualifiers.SUBVIEW).contains(
        resource.getQualifier());
  }

}
