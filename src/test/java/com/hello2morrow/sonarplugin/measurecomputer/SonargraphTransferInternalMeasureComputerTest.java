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
import com.hello2morrow.sonarplugin.metric.internal.SonargraphInternalMetrics;
import org.junit.Test;
import org.sonar.api.ce.measure.Component;
import org.sonar.api.ce.measure.Measure;
import org.sonar.api.ce.measure.MeasureComputer;
import org.sonar.api.ce.measure.MeasureComputer.MeasureComputerContext;

import java.util.Arrays;

import static org.junit.Assert.assertFalse;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class SonargraphTransferInternalMeasureComputerTest {

  @Test
  public void testInputOutputMetrics() {
    final SonargraphTransferInternalMeasureComputer computer = new SonargraphTransferInternalMeasureComputer();
    assertFalse(computer.getInputMetrics().isEmpty());
    assertFalse(computer.getOutputMetrics().isEmpty());
  }

  @Test
  public void testCompute() {

    final MeasureComputer computer = new SonargraphTransferInternalMeasureComputer();

    final Component project = mock(Component.class);
    when(project.getType()).thenReturn(Component.Type.PROJECT);
    final MeasureComputerContext context = mock(MeasureComputerContext.class);
    when(context.getComponent()).thenReturn(project);

    final Measure measure = mock(Measure.class);
    when(measure.getBooleanValue()).thenReturn(Boolean.TRUE);
    when(context.getMeasure(SonargraphInternalMetrics.ROOT_PROJECT_TO_BE_PROCESSED.key())).thenReturn(measure);

    final Measure measure1 = mock(Measure.class);
    final int value1 = 3;
    when(measure1.getIntValue()).thenReturn(value1);
    final String metric = SonargraphInternalMetrics.SYSTEM_ALL_WARNINGS.key();
    when(context.getChildrenMeasures(metric)).thenReturn(Arrays.asList(measure1));

    computer.compute(context);

    verify(context).addMeasure(SonargraphSimpleMetrics.ALL_WARNINGS.key(), value1);
  }
}
