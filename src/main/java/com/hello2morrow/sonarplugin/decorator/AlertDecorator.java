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

import com.hello2morrow.sonarplugin.foundation.IProjectContext;
import com.hello2morrow.sonarplugin.metric.SonargraphDerivedMetrics;
import com.hello2morrow.sonarplugin.metric.SonargraphSimpleMetrics;
import org.sonar.api.measures.Measure;
import org.sonar.api.measures.Metric;

public final class AlertDecorator {

  static class AlertThreshold {

    private final Metric metric;
    private final double warningLevel;
    private final double alertLevel;

    AlertThreshold(Metric metric, double warningLevel, double alertLevel) {
      this.metric = metric;
      this.warningLevel = warningLevel;
      this.alertLevel = alertLevel;
    }

    Metric getMetric() {
      return metric;
    }

    Metric.Level getLevel(double value) {
      if (value >= alertLevel) {
        return Metric.Level.ERROR;
      }
      if (value >= warningLevel) {
        return Metric.Level.WARN;
      }
      return Metric.Level.OK;
    }
  }

  private AlertDecorator() {
  }

  private static final AlertThreshold THRESHOLDS[] = {
    new AlertThreshold(SonargraphSimpleMetrics.STRUCTURAL_DEBT_INDEX, 400, 1600),
    new AlertThreshold(SonargraphSimpleMetrics.TASKS, 20.0, 50.0),

    new AlertThreshold(SonargraphDerivedMetrics.RELATIVE_CYCLICITY, 5, 15),
    new AlertThreshold(SonargraphDerivedMetrics.BIGGEST_CYCLE_GROUP, 4, 8),

    new AlertThreshold(SonargraphSimpleMetrics.NCCD, 6.5, 10.0),
    new AlertThreshold(SonargraphDerivedMetrics.HIGHEST_NCCD, 6.5, 10.0),

    new AlertThreshold(SonargraphSimpleMetrics.VIOLATING_TYPE_DEPENDENCIES, 1, 10),
    new AlertThreshold(SonargraphSimpleMetrics.VIOLATING_TYPES, 1.0, 10.0),
    new AlertThreshold(SonargraphSimpleMetrics.VIOLATING_REFERENCES, 1, 10),
    new AlertThreshold(SonargraphSimpleMetrics.UNASSIGNED_TYPES, 1.0, 20.0),

    new AlertThreshold(SonargraphSimpleMetrics.ALL_WARNINGS, 1, 20),
    new AlertThreshold(SonargraphSimpleMetrics.CYCLE_WARNINGS, 1, 10),
    new AlertThreshold(SonargraphSimpleMetrics.DUPLICATE_WARNINGS, 1, 20),
    new AlertThreshold(SonargraphSimpleMetrics.THRESHOLD_WARNINGS, 1.0, 25.0),
    new AlertThreshold(SonargraphSimpleMetrics.WORKSPACE_WARNINGS, 1.0, 10.0)};

  private static void copyAlertLevel(IProjectContext context, Metric from, Metric to) {
    Measure fromMeasure = context.getMeasure(from);

    if (fromMeasure != null) {
      Measure toMeasure = context.getMeasure(to);

      if (toMeasure != null) {
        toMeasure.setAlertStatus(fromMeasure.getAlertStatus());
        toMeasure.setAlertText(fromMeasure.getMetricKey());
        context.saveMeasure(toMeasure);
      }
    }
  }

  public static void setAlertLevels(IProjectContext context) {
    for (AlertThreshold threshold : THRESHOLDS) {
      Measure m = context.getMeasure(threshold.getMetric());

      if (m != null) {
        m.setAlertStatus(threshold.getLevel(m.getValue()));
        m.setAlertText(threshold.getMetric().getKey());
        context.saveMeasure(m);
      }
    }
    copyAlertLevel(context, SonargraphSimpleMetrics.NCCD, SonargraphSimpleMetrics.ACD);
    copyAlertLevel(context, SonargraphDerivedMetrics.HIGHEST_NCCD, SonargraphDerivedMetrics.HIGHEST_ACD);
    copyAlertLevel(context, SonargraphSimpleMetrics.STRUCTURAL_DEBT_INDEX, SonargraphSimpleMetrics.STRUCTURAL_DEBT_COST);
    copyAlertLevel(context, SonargraphSimpleMetrics.TASKS, SonargraphSimpleMetrics.TASK_REFS);
    copyAlertLevel(context, SonargraphDerivedMetrics.RELATIVE_CYCLICITY, SonargraphSimpleMetrics.CYCLICITY);
  }
}
