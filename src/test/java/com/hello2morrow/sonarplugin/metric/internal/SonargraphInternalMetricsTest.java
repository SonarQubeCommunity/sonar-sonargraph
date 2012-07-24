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
package com.hello2morrow.sonarplugin.metric.internal;

import static org.junit.Assert.*;

import java.util.List;

import org.junit.Test;
import org.sonar.api.measures.Metric;
import org.sonar.api.measures.Metrics;


public class SonargraphInternalMetricsTest {

  @Test
  public void testGetMetrics() {
    Metrics sonargraphMetrics = new SonargraphInternalMetrics();
    List<Metric> metrics = sonargraphMetrics.getMetrics();
    assertTrue(metrics.contains(SonargraphInternalMetrics.MODULE_NOT_PART_OF_SONARGRAPH_WORKSPACE));
    assertTrue(metrics.contains(SonargraphInternalMetrics.SYSTEM_ALL_WARNINGS));
    assertTrue(metrics.contains(SonargraphInternalMetrics.SYSTEM_CYCLE_WARNINGS));
    assertTrue(metrics.contains(SonargraphInternalMetrics.SYSTEM_THRESHOLD_WARNINGS));
    assertTrue(metrics.contains(SonargraphInternalMetrics.SYSTEM_WORKSPACE_WARNINGS));
    assertTrue(metrics.contains(SonargraphInternalMetrics.SYSTEM_IGNORED_WARNINGS));
    
    assertEquals(6, metrics.size());
  }

}
