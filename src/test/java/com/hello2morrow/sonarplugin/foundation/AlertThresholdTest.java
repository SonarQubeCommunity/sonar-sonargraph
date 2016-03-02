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
package com.hello2morrow.sonarplugin.foundation;

import org.junit.Test;
import org.sonar.api.measures.Metric;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

public class AlertThresholdTest {

  @Test
  public void testLevels() {
    final AlertThreshold alert = new AlertThreshold(2, 4);

    assertEquals(Metric.Level.OK, alert.getLevel(1));
    assertEquals(Metric.Level.WARN, alert.getLevel(2));
    assertEquals(Metric.Level.ERROR, alert.getLevel(4));
  }

  @Test
  public void testEqualsHashCode() {
    final AlertThreshold a1 = new AlertThreshold(2, 4);
    assertEquals(a1, a1);

    final AlertThreshold a2 = new AlertThreshold(2, 4);
    assertEquals(a1, a2);
    assertEquals(a1.hashCode(), a2.hashCode());

    final AlertThreshold a3 = new AlertThreshold(3, 5);
    assertFalse(a1.equals(a3));
    assertFalse(a1.hashCode() != a2.hashCode());
  }

}
