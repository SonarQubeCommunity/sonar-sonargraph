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
import org.junit.Test;
import org.sonar.api.batch.DecoratorContext;
import org.sonar.api.measures.Measure;
import org.sonar.api.measures.Metric;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class SonargraphAlertThresholdsTest {

  @Test
  public void testSimpleMetric() {
    DecoratorContext context = mock(DecoratorContext.class);
    Measure measure0 = createMeasureAndSetValue(context, SonargraphSimpleMetrics.ALL_WARNINGS, 0.0);
    verify(measure0).setAlertStatus(Metric.Level.OK);

    Measure measure1 = createMeasureAndSetValue(context, SonargraphSimpleMetrics.ALL_WARNINGS, 1.0);
    verify(measure1).setAlertStatus(Metric.Level.WARN);

    Measure measure19 = createMeasureAndSetValue(context, SonargraphSimpleMetrics.ALL_WARNINGS, 19.0);
    verify(measure19).setAlertStatus(Metric.Level.WARN);

    Measure measure20 = createMeasureAndSetValue(context, SonargraphSimpleMetrics.ALL_WARNINGS, 20.0);
    verify(measure20).setAlertStatus(Metric.Level.ERROR);
  }

  @Test
  public void testConnectedMetric() {
    DecoratorContext context = mock(DecoratorContext.class);
    // SonargraphSimpleMetrics.NCCD, new AlertThreshold(6.5, 10.0

    Measure nccd = setupMetric(SonargraphSimpleMetrics.NCCD);
    when(nccd.getAlertStatus()).thenReturn(Metric.Level.OK);
    when(nccd.getAlertText()).thenReturn(SonargraphSimpleMetrics.ACD.getKey());

    Measure acd = setupMetric(SonargraphSimpleMetrics.ACD);
    when(context.getMeasure(any(Metric.class))).thenReturn(nccd);

    SonargraphAlertThresholds.addAlertToMeasure(context, nccd, 6.0);
    verify(nccd).setAlertStatus(Metric.Level.OK);
    SonargraphAlertThresholds.addAlertToMeasure(context, acd, 60.0);
    verify(acd).setAlertStatus(Metric.Level.OK);

    Measure nccd7 = setupMetric(SonargraphSimpleMetrics.NCCD);
    when(nccd7.getAlertStatus()).thenReturn(Metric.Level.WARN);
    when(nccd7.getAlertText()).thenReturn(SonargraphSimpleMetrics.ACD.getKey());

    Measure acd60 = setupMetric(SonargraphSimpleMetrics.ACD);
    when(context.getMeasure(any(Metric.class))).thenReturn(nccd7);

    SonargraphAlertThresholds.addAlertToMeasure(context, nccd7, 7.0);
    verify(nccd7).setAlertStatus(Metric.Level.WARN);
    SonargraphAlertThresholds.addAlertToMeasure(context, acd60, 60.0);
    verify(acd60).setAlertStatus(Metric.Level.WARN);

    Measure nccd10 = setupMetric(SonargraphSimpleMetrics.NCCD);
    when(nccd10.getAlertStatus()).thenReturn(Metric.Level.ERROR);
    when(nccd10.getAlertText()).thenReturn(SonargraphSimpleMetrics.ACD.getKey());

    Measure acd61 = setupMetric(SonargraphSimpleMetrics.ACD);
    when(context.getMeasure(any(Metric.class))).thenReturn(nccd10);

    SonargraphAlertThresholds.addAlertToMeasure(context, nccd10, 10.0);
    verify(nccd10).setAlertStatus(Metric.Level.ERROR);
    SonargraphAlertThresholds.addAlertToMeasure(context, acd61, 61.0);
    verify(acd61).setAlertStatus(Metric.Level.ERROR);
  }

  @Test
  public void testGetThreshold() {
    AlertThreshold threshold = new AlertThreshold(6.5, 10.0);
    assertEquals("Threshold values don't match", threshold, SonargraphAlertThresholds.getThreshold(SonargraphSimpleMetrics.NCCD));
  }

  @Test
  public void testGetConnectedMetric() {
    assertEquals("Connected metric not correct", SonargraphSimpleMetrics.NCCD, SonargraphAlertThresholds.getConnectedMetric(SonargraphSimpleMetrics.ACD));
  }

  private Measure createMeasureAndSetValue(DecoratorContext context, Metric metric, double value) {
    Measure measure = setupMetric(metric);
    SonargraphAlertThresholds.addAlertToMeasure(context, measure, value);
    return measure;
  }

  private Measure setupMetric(Metric metric) {
    Measure measure = mock(Measure.class);
    when(measure.getMetric()).thenReturn(metric);
    return measure;
  }
}
