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
import com.hello2morrow.sonarplugin.metric.SonargraphSimpleMetrics;
import com.hello2morrow.sonarplugin.metric.internal.SonargraphInternalMetrics;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sonar.api.ce.measure.Measure;
import org.sonar.api.measures.Metric;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

public class SonargraphTransferInternalMeasureComputer extends SonargraphMeasureComputer {

  private static final Logger LOGGER = LoggerFactory.getLogger(SonargraphTransferInternalMeasureComputer.class);

  @Override
  void internalCompute(final MeasureComputerContext context) {
    LOGGER.info("Transfer metrics for " + context.getComponent().getKey());
    transferMetricValue(context, SonargraphInternalMetrics.SYSTEM_ALL_WARNINGS, SonargraphSimpleMetrics.ALL_WARNINGS);
    transferMetricValue(context, SonargraphInternalMetrics.SYSTEM_CYCLE_WARNINGS, SonargraphSimpleMetrics.CYCLE_WARNINGS);
    transferMetricValue(context, SonargraphInternalMetrics.SYSTEM_THRESHOLD_WARNINGS, SonargraphSimpleMetrics.THRESHOLD_WARNINGS);
    transferMetricValue(context, SonargraphInternalMetrics.SYSTEM_WORKSPACE_WARNINGS, SonargraphSimpleMetrics.WORKSPACE_WARNINGS);
    transferMetricValue(context, SonargraphInternalMetrics.SYSTEM_IGNORED_WARNINGS, SonargraphSimpleMetrics.IGNORED_WARNINGS);
    transferMetricValue(context, SonargraphInternalMetrics.SYSTEM_ALL_TASKS, SonargraphSimpleMetrics.TASKS);
  }

  private void transferMetricValue(final MeasureComputerContext context, final Metric<Serializable> from, final Metric<Serializable> to) {
    final Iterable<Measure> childrenMeasures = context.getChildrenMeasures(from.key());
    if (childrenMeasures == null) {
      return;
    }

    final Iterator<Measure> iter = childrenMeasures.iterator();
    if (!iter.hasNext()) {
      LOGGER.warn("Failed to transfer value for metric '" + to.key() + "'");
    } else {
      final int value = iter.next().getIntValue();
      LOGGER.info("Transfer value: " + to.key() + "=" + value);
      context.addMeasure(to.key(), value);
    }
  }

  @Override
  List<String> getInputMetrics() {
    return SonarQubeUtilities.convertMetricListToKeyList(Arrays.asList(SonargraphInternalMetrics.SYSTEM_ALL_WARNINGS, SonargraphInternalMetrics.SYSTEM_CYCLE_WARNINGS,
      SonargraphInternalMetrics.SYSTEM_THRESHOLD_WARNINGS, SonargraphInternalMetrics.SYSTEM_WORKSPACE_WARNINGS, SonargraphInternalMetrics.SYSTEM_IGNORED_WARNINGS,
      SonargraphInternalMetrics.SYSTEM_ALL_TASKS));
  }

  @Override
  List<String> getOutputMetrics() {
    return SonarQubeUtilities.convertMetricListToKeyList(Arrays.asList(SonargraphSimpleMetrics.ALL_WARNINGS, SonargraphSimpleMetrics.CYCLE_WARNINGS,
      SonargraphSimpleMetrics.THRESHOLD_WARNINGS, SonargraphSimpleMetrics.WORKSPACE_WARNINGS, SonargraphSimpleMetrics.IGNORED_WARNINGS, SonargraphSimpleMetrics.TASKS));
  }
}
