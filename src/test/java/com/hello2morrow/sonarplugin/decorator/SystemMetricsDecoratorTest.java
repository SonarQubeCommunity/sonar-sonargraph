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

import junit.framework.Assert;

import org.junit.Test;
import org.sonar.api.measures.Measure;


public class SystemMetricsDecoratorTest {

  @Test
  public void testGetBiggerValueDoubleMeasureMeasure() {
    SystemMetricsDecorator decorator = new SystemMetricsDecorator();
    Measure measure = new Measure();
    measure.setValue(10.0);
    Assert.assertEquals(12.0, decorator.getBiggerValue(12.0, measure));
    Assert.assertEquals(10.0, decorator.getBiggerValue(9.4, measure));
    Assert.assertEquals(10.0, decorator.getBiggerValue(10.0, null));
    
  }

  @Test
  public void testGetBiggerValueDoubleMeasure() {
    SystemMetricsDecorator decorator = new SystemMetricsDecorator();
    Measure measure1 = new Measure();
    measure1.setValue(10.0);
    Measure measure2 = new Measure();
    measure2.setValue(8.5);
    
    Assert.assertEquals(12.0, decorator.getBiggerValue(12.0, measure1, measure2));
    Assert.assertEquals(10.0, decorator.getBiggerValue(9.4, measure1, measure2));
    Assert.assertEquals(10.0, decorator.getBiggerValue(9.4, measure1, null));
    Assert.assertEquals(8.5, decorator.getBiggerValue(7.2, null, measure2));
    Assert.assertEquals(9.4, decorator.getBiggerValue(9.4, null, null));
    
    
    
  }

}
