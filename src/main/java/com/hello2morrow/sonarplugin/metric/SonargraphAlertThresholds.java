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

package com.hello2morrow.sonarplugin.metric;

import com.hello2morrow.sonarplugin.foundation.AlertThreshold;
import com.hello2morrow.sonarplugin.foundation.IProjectContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sonar.api.measures.Measure;
import org.sonar.api.measures.Metric;

import java.util.HashMap;
import java.util.Map;

public final class SonargraphAlertThresholds {

  private static final Logger LOG = LoggerFactory.getLogger(SonargraphAlertThresholds.class);
  private static final Map<Metric, AlertThreshold> THRESHOLDS = new HashMap<Metric, AlertThreshold>();
  private static final Map<Metric, Metric> CONNECTED_THRESHOLDS = new HashMap<Metric, Metric>();

  static {
    THRESHOLDS.put(SonargraphSimpleMetrics.STRUCTURAL_DEBT_INDEX, new AlertThreshold(400, 1600));
    THRESHOLDS.put(SonargraphSimpleMetrics.TASKS, new AlertThreshold(20.0, 50.0));

    THRESHOLDS.put(SonargraphDerivedMetrics.RELATIVE_CYCLICITY, new AlertThreshold(5, 15));
    THRESHOLDS.put(SonargraphDerivedMetrics.BIGGEST_CYCLE_GROUP, new AlertThreshold(4, 8));

    THRESHOLDS.put(SonargraphSimpleMetrics.NCCD, new AlertThreshold(6.5, 10.0));
    THRESHOLDS.put(SonargraphDerivedMetrics.HIGHEST_NCCD, new AlertThreshold(6.5, 10.0));

    THRESHOLDS.put(SonargraphSimpleMetrics.VIOLATING_TYPE_DEPENDENCIES, new AlertThreshold(1, 10));
    THRESHOLDS.put(SonargraphSimpleMetrics.VIOLATING_TYPES, new AlertThreshold(1.0, 10.0));
    THRESHOLDS.put(SonargraphSimpleMetrics.VIOLATING_REFERENCES, new AlertThreshold(1, 10));
    THRESHOLDS.put(SonargraphSimpleMetrics.UNASSIGNED_TYPES, new AlertThreshold(1.0, 20.0));

    THRESHOLDS.put(SonargraphSimpleMetrics.ALL_WARNINGS, new AlertThreshold(1, 20));
    THRESHOLDS.put(SonargraphSimpleMetrics.CYCLE_WARNINGS, new AlertThreshold(1, 10));
    THRESHOLDS.put(SonargraphSimpleMetrics.DUPLICATE_WARNINGS, new AlertThreshold(1, 20));
    THRESHOLDS.put(SonargraphSimpleMetrics.THRESHOLD_WARNINGS, new AlertThreshold(1.0, 25.0));
    THRESHOLDS.put(SonargraphSimpleMetrics.WORKSPACE_WARNINGS, new AlertThreshold(1.0, 10.0));

    CONNECTED_THRESHOLDS.put(SonargraphSimpleMetrics.ACD, SonargraphSimpleMetrics.NCCD);
    CONNECTED_THRESHOLDS.put(SonargraphDerivedMetrics.HIGHEST_ACD, SonargraphDerivedMetrics.HIGHEST_NCCD);
    CONNECTED_THRESHOLDS.put(SonargraphSimpleMetrics.STRUCTURAL_DEBT_COST, SonargraphSimpleMetrics.STRUCTURAL_DEBT_INDEX);
    CONNECTED_THRESHOLDS.put(SonargraphSimpleMetrics.TASK_REFS, SonargraphSimpleMetrics.TASKS);
    CONNECTED_THRESHOLDS.put(SonargraphSimpleMetrics.CYCLICITY, SonargraphDerivedMetrics.RELATIVE_CYCLICITY);
  }

  private SonargraphAlertThresholds() {
    // singleton
  }

  public static void addAlertToMeasure(final IProjectContext context, final Measure measure, final double value) {
    final AlertThreshold threshold = THRESHOLDS.get(measure.getMetric());
    if (threshold != null) {
      measure.setAlertStatus(threshold.getLevel(value));
      measure.setAlertText(measure.getMetricKey());
      return;
    }

    Metric copyAlertFromMetric = CONNECTED_THRESHOLDS.get(measure.getMetric());
    if (copyAlertFromMetric != null) {
      Measure fromMeasure = context.getMeasure(copyAlertFromMetric);
      if (fromMeasure != null) {
        measure.setAlertStatus(fromMeasure.getAlertStatus());
        measure.setAlertText(fromMeasure.getMetricKey());
      } else {
        LOG.error("From Measure '{}' does not exist.", measure.getMetricKey());
      }
    }
  }

  public static AlertThreshold getThreshold(Metric metric) {
    return THRESHOLDS.get(metric);
  }

  public static Metric getConnectedMetric(Metric connectedMetric) {
    return CONNECTED_THRESHOLDS.get(connectedMetric);
  }
}
