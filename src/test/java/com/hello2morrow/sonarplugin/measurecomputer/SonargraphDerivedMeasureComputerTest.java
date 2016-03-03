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

import com.hello2morrow.sonarplugin.metric.SonargraphDerivedMetrics;
import com.hello2morrow.sonarplugin.metric.SonargraphSimpleMetrics;
import com.hello2morrow.sonarplugin.metric.internal.SonargraphInternalMetrics;
import org.junit.Test;
import org.sonar.api.ce.measure.Component;
import org.sonar.api.ce.measure.Measure;
import org.sonar.api.ce.measure.MeasureComputer.MeasureComputerContext;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class SonargraphDerivedMeasureComputerTest {

  @Test
  public void testInputOutputMetrics() {
    final SonargraphDerivedMeasureComputer computer = new SonargraphDerivedMeasureComputer();
    assertFalse("Input metrics expected", computer.getInputMetrics().isEmpty());
    assertFalse("Output metrics expected", computer.getOutputMetrics().isEmpty());
  }

  @Test
  public void testNeedsProcessing() {
    final SonargraphDerivedMeasureComputer computer = new SonargraphDerivedMeasureComputer();

    final Component project = mock(Component.class);
    when(project.getType()).thenReturn(Component.Type.PROJECT);
    final MeasureComputerContext context = initMeasureComputerContext();
    when(context.getComponent()).thenReturn(project);
    assertTrue(computer.needsProcessing(context));

    final Component module = mock(Component.class);
    when(module.getType()).thenReturn(Component.Type.MODULE);
    final MeasureComputerContext context2 = mock(MeasureComputerContext.class);
    when(context2.getComponent()).thenReturn(module);
    assertFalse(computer.needsProcessing(context2));

    final Component file = mock(Component.class);
    when(file.getType()).thenReturn(Component.Type.FILE);
    final MeasureComputerContext context3 = mock(MeasureComputerContext.class);
    when(context3.getComponent()).thenReturn(file);
    assertFalse(computer.needsProcessing(context3));
  }

  @Test
  public void testNothingComputedIfMetricValueMissing() {
    final SonargraphDerivedMeasureComputer computer = new SonargraphDerivedMeasureComputer();

    final Component project = mock(Component.class);
    when(project.getType()).thenReturn(Component.Type.PROJECT);
    final MeasureComputerContext context = mock(MeasureComputerContext.class);
    when(context.getComponent()).thenReturn(project);

    computer.compute(context);
    verify(context, never()).addMeasure(anyString(), anyInt());
  }

  @Test
  public void testComputedViolatingTypesPercent() {
    final SonargraphDerivedMeasureComputer computer = new SonargraphDerivedMeasureComputer();

    final Component project = mock(Component.class);
    when(project.getType()).thenReturn(Component.Type.PROJECT);
    final MeasureComputerContext context = initMeasureComputerContext();
    when(context.getComponent()).thenReturn(project);

    final Measure violatingTypes = mock(Measure.class);
    when(violatingTypes.getIntValue()).thenReturn(15);
    when(context.getMeasure(SonargraphSimpleMetrics.VIOLATING_TYPES.key())).thenReturn(violatingTypes);

    final Measure internalTypes = mock(Measure.class);
    when(internalTypes.getIntValue()).thenReturn(100);
    when(context.getMeasure(SonargraphSimpleMetrics.INTERNAL_TYPES.key())).thenReturn(internalTypes);

    computer.compute(context);
    verify(context).addMeasure(SonargraphDerivedMetrics.VIOLATING_TYPES_PERCENT.key(), 15.0);
  }

  @Test
  public void testUnassignedTypesPercent() {
    final SonargraphDerivedMeasureComputer computer = new SonargraphDerivedMeasureComputer();

    final Component project = mock(Component.class);
    when(project.getType()).thenReturn(Component.Type.PROJECT);
    final MeasureComputerContext context = initMeasureComputerContext();
    when(context.getComponent()).thenReturn(project);

    final Measure violatingTypes = mock(Measure.class);
    when(violatingTypes.getIntValue()).thenReturn(12);
    when(context.getMeasure(SonargraphSimpleMetrics.UNASSIGNED_TYPES.key())).thenReturn(violatingTypes);

    final Measure internalTypes = mock(Measure.class);
    when(internalTypes.getIntValue()).thenReturn(100);
    when(context.getMeasure(SonargraphSimpleMetrics.INTERNAL_TYPES.key())).thenReturn(internalTypes);

    computer.compute(context);
    verify(context).addMeasure(SonargraphDerivedMetrics.UNASSIGNED_TYPES_PERCENT.key(), 12.0);
  }

  @Test
  public void testCyclicityMeasures() {
    final SonargraphDerivedMeasureComputer computer = new SonargraphDerivedMeasureComputer();

    final Component project = mock(Component.class);
    when(project.getType()).thenReturn(Component.Type.PROJECT);
    final MeasureComputerContext context = initMeasureComputerContext();
    when(context.getComponent()).thenReturn(project);

    final Measure internalPackages = mock(Measure.class);
    when(internalPackages.getIntValue()).thenReturn(100);
    when(context.getMeasure(SonargraphSimpleMetrics.INTERNAL_PACKAGES.key())).thenReturn(internalPackages);

    final Measure cyclicity = mock(Measure.class);
    when(cyclicity.getIntValue()).thenReturn(16);
    when(context.getMeasure(SonargraphSimpleMetrics.CYCLICITY.key())).thenReturn(cyclicity);

    final Measure cyclicPackages = mock(Measure.class);
    when(cyclicPackages.getIntValue()).thenReturn(6);
    when(context.getMeasure(SonargraphSimpleMetrics.CYCLIC_PACKAGES.key())).thenReturn(cyclicPackages);

    computer.compute(context);

    verify(context).addMeasure(SonargraphDerivedMetrics.RELATIVE_CYCLICITY.key(), 4.0);
    verify(context).addMeasure(SonargraphDerivedMetrics.CYCLIC_PACKAGES_PERCENT.key(), 6.0);
  }

  public MeasureComputerContext initMeasureComputerContext() {
    final MeasureComputerContext context = mock(MeasureComputerContext.class);
    final Measure measure = mock(Measure.class);
    when(measure.getBooleanValue()).thenReturn(Boolean.TRUE);
    when(context.getMeasure(SonargraphInternalMetrics.ROOT_PROJECT_TO_BE_PROCESSED.key())).thenReturn(measure);
    return context;
  }
}
