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

import com.hello2morrow.sonarplugin.metric.SonargraphSimpleMetrics;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sonar.api.ce.measure.Measure;
import org.sonar.api.measures.Metric;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

/**
 * Computes measures for aggregating root project based on the values calculated for child projects 
 *
 */
public class SonargraphAggregatedMeasureComputer extends SonargraphMeasureComputer {

  private static final Logger LOGGER = LoggerFactory.getLogger(SonargraphAggregatedMeasureComputer.class);

  @Override
  List<String> getOutputMetrics() {
    return getMetricKeys();
  }

  @Override
  List<String> getInputMetrics() {
    return getMetricKeys();
  }

  private static List<String> getMetricKeys() {
    final List<Metric<Serializable>> metrics = Arrays.asList(
    /* Warnings */
    SonargraphSimpleMetrics.DUPLICATE_WARNINGS,

    /* Size metrics */
    SonargraphSimpleMetrics.INTERNAL_PACKAGES, SonargraphSimpleMetrics.JAVA_FILES, SonargraphSimpleMetrics.INTERNAL_TYPES, SonargraphSimpleMetrics.TYPE_DEPENDENCIES,

    /* structural debt metrics */
    SonargraphSimpleMetrics.STRUCTURAL_DEBT_INDEX, SonargraphSimpleMetrics.STRUCTURAL_DEBT_COST, SonargraphSimpleMetrics.TASK_REFS,

    /* structure metrics */
    SonargraphSimpleMetrics.CYCLICITY, SonargraphSimpleMetrics.CYCLIC_PACKAGES, SonargraphSimpleMetrics.REFERENCES_TO_REMOVE, SonargraphSimpleMetrics.TYPE_DEPENDENCIES_TO_CUT,
      SonargraphSimpleMetrics.INSTRUCTIONS,

      /* architecture metrics */
      SonargraphSimpleMetrics.VIOLATING_TYPE_DEPENDENCIES, SonargraphSimpleMetrics.VIOLATING_TYPES, SonargraphSimpleMetrics.VIOLATING_REFERENCES,
      SonargraphSimpleMetrics.IGNORED_VIOLATONS, SonargraphSimpleMetrics.UNASSIGNED_TYPES);

    final List<String> keys = new ArrayList<>();
    for (final Metric<Serializable> next : metrics) {
      keys.add(next.getKey());
    }
    return keys;
  }

  @Override
  void internalCompute(final MeasureComputerContext context) {
    for (final String nextKey : getMetricKeys()) {
      final Iterable<Measure> iterable = context.getChildrenMeasures(nextKey);
      if (iterable == null) {
        LOGGER.error("Failed to find child measures for '" + nextKey + "'");
        return;
      }

      int value = 0;
      for (final Iterator<Measure> iterator = iterable.iterator(); iterator.hasNext();) {
        value += iterator.next().getIntValue();
      }

      context.addMeasure(nextKey, value);
    }
  }
}
