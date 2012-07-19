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
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sonar.api.batch.AbstractSumChildrenDecorator;
import org.sonar.api.batch.DecoratorContext;
import org.sonar.api.batch.DependedUpon;
import org.sonar.api.measures.Metric;
import org.sonar.api.resources.Qualifiers;
import org.sonar.api.resources.Resource;

import com.hello2morrow.sonarplugin.foundation.Utilities;
import com.hello2morrow.sonarplugin.metric.SonargraphSimpleMetrics;

/**
 * These metrics are save to be simple summed up for the parent project. 
 * 
 * @author Ingmar
 *
 */
public final class SonargraphMetricAggregator extends AbstractSumChildrenDecorator {

  private static final Logger LOG = LoggerFactory.getLogger(SonargraphMetricAggregator.class);

  @Override
  @DependedUpon
  public List<Metric> generatesMetrics() {

    return Arrays.asList(SonargraphSimpleMetrics.INTERNAL_PACKAGES, SonargraphSimpleMetrics.JAVA_FILES,
        SonargraphSimpleMetrics.INTERNAL_TYPES, SonargraphSimpleMetrics.TYPE_DEPENDENCIES,

        /* structural debt metrics */
        SonargraphSimpleMetrics.EROSION_INDEX, SonargraphSimpleMetrics.EROSION_COST,
        SonargraphSimpleMetrics.TASKS, SonargraphSimpleMetrics.TASK_REFS,
        
        /* structure metrics */
        //TODO: check if the cyclicity can be simply aggregated... How is it done in the standalone application? 
        SonargraphSimpleMetrics.CYCLICITY, SonargraphSimpleMetrics.CYCLIC_PACKAGES,
        SonargraphSimpleMetrics.EROSION_REFS, SonargraphSimpleMetrics.EROSION_TYPES,
        SonargraphSimpleMetrics.INSTRUCTIONS,

        /* architecture metrics */
        SonargraphSimpleMetrics.VIOLATING_TYPE_DEPENDENCIES, SonargraphSimpleMetrics.VIOLATING_TYPES,
        SonargraphSimpleMetrics.VIOLATING_REFERENCES, SonargraphSimpleMetrics.IGNORED_VIOLATONS,
        SonargraphSimpleMetrics.UNASSIGNED_TYPES, 
        
        /* warnings */
        SonargraphSimpleMetrics.DUPLICATE_WARNINGS);
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
    if ( !Utilities.isAggregationProject(context, SonargraphSimpleMetrics.INSTRUCTIONS)) {
      return;
    }
    super.decorate(resource, context);

    AlertDecorator.setAlertLevels(new DecoratorProjectContext(context));
  }

  @Override
  public boolean shouldDecorateResource(@SuppressWarnings("rawtypes") Resource resource) {
    LOG.debug("Checking for resource type: " + resource.getQualifier());
    return Arrays.asList(Qualifiers.PROJECT, Qualifiers.MODULE, Qualifiers.VIEW, Qualifiers.SUBVIEW).contains(
        resource.getQualifier());
  }
}
