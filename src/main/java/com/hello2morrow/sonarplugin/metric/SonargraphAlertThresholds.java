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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sonar.api.batch.DecoratorContext;
import org.sonar.api.measures.Measure;
import org.sonar.api.measures.Metric;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

@SuppressWarnings("rawtypes")
public final class SonargraphAlertThresholds {

  private static final Logger LOG = LoggerFactory.getLogger(SonargraphAlertThresholds.class);
  private static final Map<Metric, AlertThreshold> THRESHOLDS = new HashMap<>();
  private static final Map<Metric<Serializable>, Metric<Serializable>> CONNECTED_THRESHOLDS = new HashMap<>();

  private static final int STRUCTURAL_DEBT_WARNING = 400;
  private static final int STRUCTURAL_DEBT_ERROR = 1600;
  private static final int TASK_WARNING = 20;
  private static final int TASK_ERROR = 50;

  private static final int RELATIVE_CYC_WARNING = 5;
  private static final int RELATIVE_CYC_ERROR = 15;

  private static final int BIGGEST_CYCLE_GROUP_WARNING = 4;
  private static final int BIGGEST_CYCLE_GROUP_ERROR = 8;

  private static final double NCCD_WARNING = 6.5;
  private static final double NCCD_ERROR = 10.0;
  private static final double HIGHEST_NCCD_WARNING = 6.5;
  private static final double HIGHEST_NCCD_ERROR = 10.0;

  private static final int VIOLATING_TYPE_DEP_WARNING = 1;
  private static final int VIOLATING_TYPE_DEP_ERROR = 10;

  private static final int VIOLATING_REF_WARNING = 1;
  private static final int VIOLATING_REF_ERROR = 10;
  private static final int VIOLATING_TYPES_WARNING = 1;
  private static final int VIOLATING_TYPES_ERROR = 10;
  private static final int UNASSIGNED_TYPES_WARNING = 1;
  private static final int UNASSIGNED_TYPES_ERROR = 20;

  private static final int ALL_WARNINGS_WARNING = 1;
  private static final int ALL_WARNINGS_ERROR = 20;
  private static final int CYCLE_WARNINGS_WARNING = 1;
  private static final int CYCLE_WARNING_ERROR = 10;

  private static final int DUPLICATE_WARNING = 1;
  private static final int DUPLICAT_ERROR = 20;
  private static final int THRESHOLD_WARNING = 1;
  private static final int THRESHOLD_ERROR = 25;
  private static final int WORKSPACE_WARNING = 1;
  private static final int WORKSPACE_ERROR = 10;

  static {
    THRESHOLDS.put(SonargraphSimpleMetrics.STRUCTURAL_DEBT_INDEX, new AlertThreshold(STRUCTURAL_DEBT_WARNING, STRUCTURAL_DEBT_ERROR));
    THRESHOLDS.put(SonargraphSimpleMetrics.TASKS, new AlertThreshold(TASK_WARNING, TASK_ERROR));

    THRESHOLDS.put(SonargraphDerivedMetrics.RELATIVE_CYCLICITY, new AlertThreshold(RELATIVE_CYC_WARNING, RELATIVE_CYC_ERROR));
    THRESHOLDS.put(SonargraphDerivedMetrics.BIGGEST_CYCLE_GROUP, new AlertThreshold(BIGGEST_CYCLE_GROUP_WARNING, BIGGEST_CYCLE_GROUP_ERROR));

    THRESHOLDS.put(SonargraphSimpleMetrics.NCCD, new AlertThreshold(NCCD_WARNING, NCCD_ERROR));
    THRESHOLDS.put(SonargraphDerivedMetrics.HIGHEST_NCCD, new AlertThreshold(HIGHEST_NCCD_WARNING, HIGHEST_NCCD_ERROR));

    THRESHOLDS.put(SonargraphSimpleMetrics.VIOLATING_TYPE_DEPENDENCIES, new AlertThreshold(VIOLATING_TYPE_DEP_WARNING, VIOLATING_TYPE_DEP_ERROR));
    THRESHOLDS.put(SonargraphSimpleMetrics.VIOLATING_TYPES, new AlertThreshold(VIOLATING_TYPES_WARNING, VIOLATING_TYPES_ERROR));
    THRESHOLDS.put(SonargraphSimpleMetrics.VIOLATING_REFERENCES, new AlertThreshold(VIOLATING_REF_WARNING, VIOLATING_REF_ERROR));
    THRESHOLDS.put(SonargraphSimpleMetrics.UNASSIGNED_TYPES, new AlertThreshold(UNASSIGNED_TYPES_WARNING, UNASSIGNED_TYPES_ERROR));

    THRESHOLDS.put(SonargraphSimpleMetrics.ALL_WARNINGS, new AlertThreshold(ALL_WARNINGS_WARNING, ALL_WARNINGS_ERROR));
    THRESHOLDS.put(SonargraphSimpleMetrics.CYCLE_WARNINGS, new AlertThreshold(CYCLE_WARNINGS_WARNING, CYCLE_WARNING_ERROR));
    THRESHOLDS.put(SonargraphSimpleMetrics.DUPLICATE_WARNINGS, new AlertThreshold(DUPLICATE_WARNING, DUPLICAT_ERROR));
    THRESHOLDS.put(SonargraphSimpleMetrics.THRESHOLD_WARNINGS, new AlertThreshold(THRESHOLD_WARNING, THRESHOLD_ERROR));
    THRESHOLDS.put(SonargraphSimpleMetrics.WORKSPACE_WARNINGS, new AlertThreshold(WORKSPACE_WARNING, WORKSPACE_ERROR));

    CONNECTED_THRESHOLDS.put(SonargraphSimpleMetrics.ACD, SonargraphSimpleMetrics.NCCD);
    CONNECTED_THRESHOLDS.put(SonargraphDerivedMetrics.HIGHEST_ACD, SonargraphDerivedMetrics.HIGHEST_NCCD);
    CONNECTED_THRESHOLDS.put(SonargraphSimpleMetrics.STRUCTURAL_DEBT_COST, SonargraphSimpleMetrics.STRUCTURAL_DEBT_INDEX);
    CONNECTED_THRESHOLDS.put(SonargraphSimpleMetrics.TASK_REFS, SonargraphSimpleMetrics.TASKS);
    CONNECTED_THRESHOLDS.put(SonargraphSimpleMetrics.CYCLICITY, SonargraphDerivedMetrics.RELATIVE_CYCLICITY);
  }

  private SonargraphAlertThresholds() {
    // do not instantiate
  }

  public static void addAlertToMeasure(final DecoratorContext context, final Measure measure, final double value) {
    final AlertThreshold threshold = THRESHOLDS.get(measure.getMetric());
    if (threshold != null) {
      measure.setAlertStatus(threshold.getLevel(value));
      measure.setAlertText(measure.getMetricKey());
      return;
    }

    final Metric<? extends Serializable> copyAlertFromMetric = CONNECTED_THRESHOLDS.get(measure.getMetric());
    if (copyAlertFromMetric != null) {
      final Measure fromMeasure = context.getMeasure(copyAlertFromMetric);
      if (fromMeasure != null) {
        measure.setAlertStatus(fromMeasure.getAlertStatus());
        measure.setAlertText(fromMeasure.getMetricKey());
      } else {
        LOG.error("From Measure '{}' does not exist.", measure.getMetricKey());
      }
    }
  }

  public static AlertThreshold getThreshold(final Metric metric) {
    return THRESHOLDS.get(metric);
  }

  public static Metric<Serializable> getConnectedMetric(final Metric connectedMetric) {
    return CONNECTED_THRESHOLDS.get(connectedMetric);
  }
}
