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

import org.sonar.api.measures.Measure;
import org.sonar.api.measures.Metric;

import com.hello2morrow.sonarplugin.metric.SonargraphBuildUnitMetrics;
import com.hello2morrow.sonarplugin.metric.SonargraphSystemMetrics;

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

  private static final AlertThreshold thresholds[] = {
      new AlertThreshold(SonargraphBuildUnitMetrics.EROSION_INDEX, 400, 1600),
      new AlertThreshold(SonargraphBuildUnitMetrics.UNASSIGNED_TYPES, 1.0, 20.0),
      new AlertThreshold(SonargraphBuildUnitMetrics.VIOLATING_TYPES, 1.0, 10.0),
      new AlertThreshold(SonargraphBuildUnitMetrics.TASKS, 20.0, 50.0),
      new AlertThreshold(SonargraphBuildUnitMetrics.THRESHOLD_WARNINGS, 1.0, 25.0),
      new AlertThreshold(SonargraphSystemMetrics.WORKSPACE_WARNINGS, 1.0, 10.0),
      new AlertThreshold(SonargraphSystemMetrics.NCCD, 6.5, 10.0),
      new AlertThreshold(SonargraphSystemMetrics.HIGHEST_NCCD, 6.5, 10.0),
      new AlertThreshold(SonargraphSystemMetrics.BIGGEST_CYCLE_GROUP, 4, 8),
      new AlertThreshold(SonargraphSystemMetrics.RELATIVE_CYCLICITY, 5, 15),
      new AlertThreshold(SonargraphBuildUnitMetrics.DUPLICATE_WARNINGS, 1, 20),
      new AlertThreshold(SonargraphSystemMetrics.CYCLE_WARNINGS, 1, 10),
      new AlertThreshold(SonargraphSystemMetrics.WORKSPACE_WARNINGS, 1, 10),
      new AlertThreshold(SonargraphSystemMetrics.ALL_WARNINGS, 1, 20),
      new AlertThreshold(SonargraphSystemMetrics.ARCHITECTURE_VIOLATIONS, 1, 10),
      new AlertThreshold(SonargraphBuildUnitMetrics.VIOLATING_DEPENDENCIES, 1, 10) };

  private static void copyAlertLevel(IProjectContext context, Metric from, Metric to) {
    Measure fromMeasure = context.getMeasure(from);

    if (fromMeasure != null) {
      Measure toMeasure = context.getMeasure(to);

      if (toMeasure != null) {
        toMeasure.setAlertStatus(fromMeasure.getAlertStatus());
        context.saveMeasure(toMeasure);
      }
    }

  }

  public static void setAlertLevels(IProjectContext context) {
    for (AlertThreshold threshold : thresholds) {
      Measure m = context.getMeasure(threshold.getMetric());

      if (m != null) {
        m.setAlertStatus(threshold.getLevel(m.getValue()));
        context.saveMeasure(m);
      }
    }
    copyAlertLevel(context, SonargraphSystemMetrics.NCCD, SonargraphSystemMetrics.ACD);
    copyAlertLevel(context, SonargraphSystemMetrics.HIGHEST_NCCD, SonargraphSystemMetrics.HIGHEST_ACD);
    copyAlertLevel(context, SonargraphBuildUnitMetrics.EROSION_INDEX, SonargraphBuildUnitMetrics.EROSION_COST);
    copyAlertLevel(context, SonargraphBuildUnitMetrics.TASKS, SonargraphBuildUnitMetrics.TASK_REFS);
    copyAlertLevel(context, SonargraphSystemMetrics.RELATIVE_CYCLICITY, SonargraphBuildUnitMetrics.CYCLICITY);
  }
}
