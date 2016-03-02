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
import org.sonar.api.measures.Metric.ValueType;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

public class SonargraphHighestMeasureComputer extends SonargraphMeasureComputer {
  private static final Logger LOGGER = LoggerFactory.getLogger(SonargraphHighestMeasureComputer.class);

  @Override
  List<String> getInputMetrics() {
    return SonarQubeUtilities.convertMetricListToKeyList(getMetrics());
  }

  @Override
  List<String> getOutputMetrics() {
    return SonarQubeUtilities.convertMetricListToKeyList(getMetrics());
  }

  private static List<Metric<Serializable>> getMetrics() {
    return Arrays.asList(
    /* Simple metrics */
    SonargraphSimpleMetrics.ACD, SonargraphSimpleMetrics.NCCD, SonargraphSimpleMetrics.RELATIVE_ACD,

    /* Highest metrics */
    SonargraphDerivedMetrics.BIGGEST_CYCLE_GROUP, SonargraphDerivedMetrics.HIGHEST_ACD, SonargraphDerivedMetrics.HIGHEST_NCCD, SonargraphDerivedMetrics.HIGHEST_RELATIVE_ACD);
  }

  @Override
  void internalCompute(final MeasureComputerContext context) {
    saveHighestMeasure(context, SonargraphSimpleMetrics.ACD, SonargraphDerivedMetrics.HIGHEST_ACD);
    saveHighestMeasure(context, SonargraphSimpleMetrics.NCCD, SonargraphDerivedMetrics.HIGHEST_NCCD);
    saveHighestMeasure(context, SonargraphSimpleMetrics.RELATIVE_ACD, SonargraphDerivedMetrics.HIGHEST_RELATIVE_ACD);
    saveHighestMeasure(context, SonargraphDerivedMetrics.BIGGEST_CYCLE_GROUP, SonargraphDerivedMetrics.BIGGEST_CYCLE_GROUP);
  }

  private void saveHighestMeasure(final MeasureComputerContext context, final Metric<Serializable> simpleMetric, final Metric<Serializable> highestMetric) {
    double highest = -1.0;
    final boolean isSimpleMetricDouble = simpleMetric.getType() == ValueType.FLOAT;

    final Iterable<Measure> childrenMeasures = context.getChildrenMeasures(simpleMetric.key());
    if (childrenMeasures == null) {
      LOGGER.error("No measures found for metric '" + simpleMetric.key() + "'");
      return;
    }
    for (final Iterator<Measure> iter = childrenMeasures.iterator(); iter.hasNext();) {
      final double value = isSimpleMetricDouble ? iter.next().getDoubleValue() : iter.next().getIntValue();
      highest = value > highest ? value : highest;
    }

    final boolean isHighestMetricDouble = highestMetric.getType() == ValueType.FLOAT;
    for (final Iterator<Measure> iter = context.getChildrenMeasures(highestMetric.key()).iterator(); iter.hasNext();) {
      final double value = isHighestMetricDouble ? iter.next().getDoubleValue() : iter.next().getIntValue();
      highest = value > highest ? value : highest;
    }

    if (highest < 0) {
      LOGGER.warn("No highest value detected for component '" + context.getComponent().getKey() + "', metric '" + highestMetric.key() + "'");
      return;
    }
    if (isHighestMetricDouble) {
      context.addMeasure(highestMetric.key(), highest);
    } else {
      context.addMeasure(highestMetric.key(), (int) highest);
    }
  }
}
