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

import com.hello2morrow.sonarplugin.foundation.TestHelper;
import com.hello2morrow.sonarplugin.metric.SonargraphDerivedMetrics;
import com.hello2morrow.sonarplugin.metric.SonargraphSimpleMetrics;
import org.junit.Test;
import org.sonar.api.batch.DecoratorContext;
import org.sonar.api.measures.Measure;
import org.sonar.api.resources.Project;

import java.util.ArrayList;
import java.util.List;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class SonargraphDerivedMetricsDecoratorTest {

  @Test
  public void testGetBiggerValueDoubleMeasureMeasure() {
    SonargraphDerivedMetricsDecorator decorator = new SonargraphDerivedMetricsDecorator(TestHelper.initRulesProfile(), TestHelper.initModuleFileSystem());
    Measure measure = new Measure();
    measure.setValue(10.0);
    assertEquals(12.0, decorator.getBiggerValue(12.0, measure));
    assertEquals(10.0, decorator.getBiggerValue(9.4, measure));
    assertEquals(10.0, decorator.getBiggerValue(10.0, null));
  }

  @Test
  public void testGetBiggerValueDoubleMeasure() {
    SonargraphDerivedMetricsDecorator decorator = new SonargraphDerivedMetricsDecorator(TestHelper.initRulesProfile(), TestHelper.initModuleFileSystem());
    Measure measure1 = new Measure();
    measure1.setValue(10.0);
    Measure measure2 = new Measure();
    measure2.setValue(8.5);

    assertEquals(12.0, decorator.getBiggerValue(12.0, measure1, measure2));
    assertEquals(10.0, decorator.getBiggerValue(9.4, measure1, measure2));
    assertEquals(10.0, decorator.getBiggerValue(9.4, measure1, null));
    assertEquals(8.5, decorator.getBiggerValue(7.2, null, measure2));
    assertEquals(9.4, decorator.getBiggerValue(9.4, null, null));
  }

  @Test
  public void testShouldDecorateResource() {
    SonargraphDerivedMetricsDecorator decorator = new SonargraphDerivedMetricsDecorator(TestHelper.initRulesProfile(), TestHelper.initModuleFileSystem());
    Project project = new Project("test");
    assertTrue(decorator.shouldDecorateResource(project));
  }

  @Test
  public void testDecorate() {
    SonargraphDerivedMetricsDecorator decorator = new SonargraphDerivedMetricsDecorator(TestHelper.initRulesProfile(), TestHelper.initModuleFileSystem());
    Project project = new Project("test");

    DecoratorContext child1 = mock(DecoratorContext.class);
    DecoratorContext child2 = mock(DecoratorContext.class);
    List<DecoratorContext> children = new ArrayList<DecoratorContext>();
    children.add(child1);
    children.add(child2);
    DecoratorContext context = new MockDecoratorContext(project, children);

    /* Cyclicity measures */
    context.saveMeasure(new Measure(SonargraphSimpleMetrics.CYCLICITY, 4.0));
    context.saveMeasure(new Measure(SonargraphSimpleMetrics.INTERNAL_PACKAGES, 20.0));
    context.saveMeasure(new Measure(SonargraphSimpleMetrics.CYCLIC_PACKAGES, 4.0));

    /* type measures */
    context.saveMeasure(new Measure(SonargraphSimpleMetrics.VIOLATING_TYPES, 3.0));
    context.saveMeasure(new Measure(SonargraphSimpleMetrics.INTERNAL_TYPES, 60.0));
    context.saveMeasure(new Measure(SonargraphSimpleMetrics.UNASSIGNED_TYPES, 6.0));

    double biggestCycleGroup = 12.0;
    when(child1.getMeasure(SonargraphDerivedMetrics.BIGGEST_CYCLE_GROUP)).thenReturn(
      new Measure(SonargraphDerivedMetrics.BIGGEST_CYCLE_GROUP, biggestCycleGroup));
    when(child1.getMeasure(SonargraphSimpleMetrics.ACD)).thenReturn(new Measure(SonargraphSimpleMetrics.ACD, 10.0));
    double highestRACD = 0.70;
    when(child1.getMeasure(SonargraphSimpleMetrics.RELATIVE_ACD)).thenReturn(
      new Measure(SonargraphSimpleMetrics.RELATIVE_ACD, highestRACD));
    double highestNccd = 5.3;
    when(child1.getMeasure(SonargraphSimpleMetrics.NCCD)).thenReturn(
      new Measure(SonargraphSimpleMetrics.NCCD, highestNccd));

    when(child2.getMeasure(SonargraphDerivedMetrics.BIGGEST_CYCLE_GROUP)).thenReturn(
      new Measure(SonargraphDerivedMetrics.BIGGEST_CYCLE_GROUP, 10.0));
    double highestAcd = 11.0;
    when(child2.getMeasure(SonargraphSimpleMetrics.ACD)).thenReturn(
      new Measure(SonargraphSimpleMetrics.ACD, highestAcd));
    when(child2.getMeasure(SonargraphSimpleMetrics.RELATIVE_ACD)).thenReturn(
      new Measure(SonargraphSimpleMetrics.RELATIVE_ACD, 0.60));
    when(child2.getMeasure(SonargraphSimpleMetrics.NCCD)).thenReturn(
      new Measure(SonargraphSimpleMetrics.NCCD, highestNccd));

    decorator.decorate(project, context);

    assertEquals(10.0, context.getMeasure(SonargraphDerivedMetrics.RELATIVE_CYCLICITY).getValue());
    assertEquals(20.0, context.getMeasure(SonargraphDerivedMetrics.CYCLIC_PACKAGES_PERCENT).getValue());

    assertEquals(5.0, context.getMeasure(SonargraphDerivedMetrics.VIOLATING_TYPES_PERCENT).getValue());
    assertEquals(10.0, context.getMeasure(SonargraphDerivedMetrics.UNASSIGNED_TYPES_PERCENT).getValue());

    assertEquals(biggestCycleGroup, context.getMeasure(SonargraphDerivedMetrics.BIGGEST_CYCLE_GROUP).getValue());
    assertEquals(highestAcd, context.getMeasure(SonargraphDerivedMetrics.HIGHEST_ACD).getValue());
    assertEquals(highestRACD, context.getMeasure(SonargraphDerivedMetrics.HIGHEST_RELATIVE_ACD).getValue());
    assertEquals(highestNccd, context.getMeasure(SonargraphDerivedMetrics.HIGHEST_NCCD).getValue());

  }

  @Test
  public void testDecorateNoPackages() {
    SonargraphDerivedMetricsDecorator decorator = new SonargraphDerivedMetricsDecorator(TestHelper.initRulesProfile(), TestHelper.initModuleFileSystem());
    Project project = new Project("test");

    DecoratorContext child1 = mock(DecoratorContext.class);
    DecoratorContext child2 = mock(DecoratorContext.class);
    List<DecoratorContext> children = new ArrayList<DecoratorContext>();
    children.add(child1);
    children.add(child2);
    DecoratorContext context = new MockDecoratorContext(project, children);

    context.saveMeasure(new Measure(SonargraphSimpleMetrics.CYCLICITY, 0.0));
    context.saveMeasure(new Measure(SonargraphSimpleMetrics.INTERNAL_PACKAGES, 0.0));
    context.saveMeasure(new Measure(SonargraphSimpleMetrics.CYCLIC_PACKAGES, 0.0));

    double biggestCycleGroup = 0.0;
    when(child1.getMeasure(SonargraphDerivedMetrics.BIGGEST_CYCLE_GROUP)).thenReturn(
      new Measure(SonargraphDerivedMetrics.BIGGEST_CYCLE_GROUP, biggestCycleGroup));

    when(child2.getMeasure(SonargraphDerivedMetrics.BIGGEST_CYCLE_GROUP)).thenReturn(
      new Measure(SonargraphDerivedMetrics.BIGGEST_CYCLE_GROUP, biggestCycleGroup));

    decorator.decorate(project, context);

    assertEquals(0.0, context.getMeasure(SonargraphDerivedMetrics.RELATIVE_CYCLICITY).getValue());
    assertEquals(0.0, context.getMeasure(SonargraphDerivedMetrics.CYCLIC_PACKAGES_PERCENT).getValue());
    assertEquals(biggestCycleGroup, context.getMeasure(SonargraphDerivedMetrics.BIGGEST_CYCLE_GROUP).getValue());
  }
}
