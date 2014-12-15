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

import com.hello2morrow.sonarplugin.metric.SonargraphSimpleMetrics;
import org.junit.Test;
import org.sonar.api.batch.DecoratorContext;
import org.sonar.api.measures.Measure;
import org.sonar.api.measures.Metric;
import org.sonar.api.profiles.RulesProfile;
import org.sonar.api.resources.Qualifiers;
import org.sonar.api.resources.Resource;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class SonargraphMetricAggregatorTest extends AbstractMetricAggregatorTest {

  @Test
  public void testGeneratesMetrics() {
    List<Metric> metrics = getAggregator().generatesMetrics();
    assertEquals("Wrong number of metrics", 19, metrics.size());
    assertTrue("Expected metric not present", metrics.contains(SonargraphSimpleMetrics.DUPLICATE_WARNINGS));
  }

  @Test
  public void testDecorate() {
    SonargraphMetricAggregator spy = spy((SonargraphMetricAggregator) getAggregator());
    doReturn(Arrays.asList(SonargraphSimpleMetrics.TYPE_DEPENDENCIES_TO_CUT)).when(spy).generatesMetrics();

    Resource resource = mock(Resource.class);
    when(resource.getQualifier()).thenReturn(Qualifiers.PROJECT);
    DecoratorContext context = mock(DecoratorContext.class);
    when(context.getMeasure(SonargraphSimpleMetrics.TYPE_DEPENDENCIES_TO_CUT)).thenReturn(new Measure(SonargraphSimpleMetrics.TYPE_DEPENDENCIES_TO_CUT, 2.0));
    spy.decorate(resource, context);

    Measure reference = new Measure(SonargraphSimpleMetrics.TYPE_DEPENDENCIES_TO_CUT, 2.0);
    verify(context).saveMeasure(eq(reference));
  }

  @Override
  protected AbstractMetricAggregator creatorAggregator(RulesProfile profile) {
    return new SonargraphMetricAggregator(getProfile());
  }
}
