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

import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.sonar.api.batch.SensorContext;
import org.sonar.api.config.Settings;
import org.sonar.api.measures.Measure;
import org.sonar.api.measures.Metric;
import org.sonar.api.profiles.RulesProfile;
import org.sonar.api.resources.Project;
import org.sonar.api.resources.Resource;

import com.hello2morrow.sonarplugin.foundation.SonargraphPluginBase;
import com.hello2morrow.sonarplugin.foundation.TestHelper;
import com.hello2morrow.sonarplugin.persistence.IReportReader;
import com.hello2morrow.sonarplugin.persistence.ReportFileReader;
import com.hello2morrow.sonarplugin.xsd.XsdAttributeRoot;

public class ReadTest extends TestCase {

  @SuppressWarnings("rawtypes")
  public void testAnalyse() {
    Project project1 = new Project("test");
    Settings settings = TestHelper.initSettings();
    settings.setProperty(SonargraphPluginBase.REPORT_PATH, "src/test/resources/infoglue21-report.xml");
    IReportReader reader = new ReportFileReader();
    reader.readSonargraphReport(project1, settings);

    assertNotNull(reader.getReport());

    final RulesProfile profile = TestHelper.initRulesProfile();
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

    SonargraphSensor sensor = new SonargraphSensor(profile, TestHelper.initSettings(), sensorContext);

    Project project = new Project("org.codehaus.sonar-plugins:infoglue21", null, "infoglue");
    XsdAttributeRoot buildUnit = reader.retrieveBuildUnit(project);
    assertNotNull(buildUnit);
    sensor.analyseBuildUnit(reader.getReport(), buildUnit);
  }
}
