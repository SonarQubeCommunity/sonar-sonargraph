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

package com.hello2morrow.sonarplugin.api;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import junit.framework.TestCase;

import org.apache.commons.configuration.Configuration;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.sonar.api.batch.SensorContext;
import org.sonar.api.measures.Measure;
import org.sonar.api.measures.Metric;
import org.sonar.api.resources.Resource;
import org.sonar.api.rules.RuleFinder;

import com.hello2morrow.sonarplugin.foundation.IProject;
import com.hello2morrow.sonarplugin.foundation.ReportFileReader;
import com.hello2morrow.sonarplugin.foundation.SonargraphPluginBase;
import com.hello2morrow.sonarplugin.xsd.ReportContext;
import com.hello2morrow.sonarplugin.xsd.XsdAttributeRoot;

public class ReadTest extends TestCase {

  @SuppressWarnings("rawtypes")
  public void testAnalyse() {
    ReportContext report = ReportFileReader.readSonargraphReport("src/test/resources/infoglue21-report.xml", true);

    assertNotNull(report);

    final RuleFinder ruleFinder = mock(RuleFinder.class);

    when(ruleFinder.findByKey(SonargraphPluginBase.PLUGIN_KEY, SonargraphPluginBase.TASK_RULE_KEY)).thenReturn(
        SonargraphRulesRepository.TASK);
    when(ruleFinder.findByKey(SonargraphPluginBase.PLUGIN_KEY, SonargraphPluginBase.CYCLE_GROUP_RULE_KEY)).thenReturn(
        SonargraphRulesRepository.CYCLE_GROUPS);
    when(ruleFinder.findByKey(SonargraphPluginBase.PLUGIN_KEY, SonargraphPluginBase.DUPLICATE_RULE_KEY)).thenReturn(
        SonargraphRulesRepository.DUPLICATES);
    when(ruleFinder.findByKey(SonargraphPluginBase.PLUGIN_KEY, SonargraphPluginBase.ARCH_RULE_KEY)).thenReturn(
        SonargraphRulesRepository.ARCH);
    when(ruleFinder.findByKey(SonargraphPluginBase.PLUGIN_KEY, SonargraphPluginBase.THRESHOLD_RULE_KEY)).thenReturn(
        SonargraphRulesRepository.THRESHOLD);
    when(ruleFinder.findByKey(SonargraphPluginBase.PLUGIN_KEY, SonargraphPluginBase.WORKSPACE_RULE_KEY)).thenReturn(
        SonargraphRulesRepository.WORKSPACE);

    final Configuration config = mock(Configuration.class);

    when(config.getString(any(String.class), any(String.class))).thenAnswer(new Answer<String>() {

      public String answer(InvocationOnMock invocationOnMock) throws Throwable {
        return (String) invocationOnMock.getArguments()[1];
      }
    });

    SonargraphSensor sensor = new SonargraphSensor(ruleFinder, config);

    final SensorContext sensorContext = mock(SensorContext.class);

    when(sensorContext.getResource(any(Resource.class))).thenAnswer(new Answer() {

      public Object answer(InvocationOnMock invocation) {
        Object[] args = invocation.getArguments();
        return args[0];
      }
    });
    when(sensorContext.getMeasure(any(Metric.class))).thenAnswer(new Answer() {

      public Object answer(InvocationOnMock invocation) {
        Object arg = invocation.getArguments()[0];
        Measure result = new Measure((Metric) arg);
        result.setValue(0.0);
        return result;
      }
    });
    
    sensor.setSensorContext(sensorContext);
    
    final IProject project = mock(IProject.class);

    when(project.getConfiguration()).thenReturn(config);
    when(project.getKey()).thenReturn("org.codehaus.sonar-plugins:infoglue21");
    when(project.getName()).thenReturn("infoglue");

    XsdAttributeRoot buildUnit = sensor.retrieveBuildUnit(project.getKey(), report);
    assertNotNull(buildUnit);
    sensor.analyseBuildUnit(report, buildUnit);
  }
}
