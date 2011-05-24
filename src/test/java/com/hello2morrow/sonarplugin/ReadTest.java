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

package com.hello2morrow.sonarplugin;

import com.hello2morrow.sonarplugin.xsd.ReportContext;
import junit.framework.TestCase;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.sonar.api.batch.SensorContext;
import org.sonar.api.rules.RuleFinder;

public class ReadTest extends TestCase
{
  private Mockery context = new Mockery();

  public void testAnalyse()
  {
    ReportContext report = SonargraphSensor.readSonargraphReport("src/test/resources/infoglue21-report.xml", "");

    assertNotNull(report);

    final RuleFinder ruleFinder = context.mock(RuleFinder.class);

    context.checking(new Expectations()
    {
      {
        allowing(ruleFinder).findByKey(SonargraphPluginBase.PLUGIN_KEY, SonargraphPluginBase.TASK_RULE_KEY);
        will(returnValue(SonargraphRulesRepository.TASK));
        allowing(ruleFinder).findByKey(SonargraphPluginBase.PLUGIN_KEY, SonargraphPluginBase.CYCLE_GROUP_RULE_KEY);
        will(returnValue(SonargraphRulesRepository.CYCLE_GROUPS));
        allowing(ruleFinder).findByKey(SonargraphPluginBase.PLUGIN_KEY, SonargraphPluginBase.DUPLICATE_RULE_KEY);
        will(returnValue(SonargraphRulesRepository.DUPLICATES));
        allowing(ruleFinder).findByKey(SonargraphPluginBase.PLUGIN_KEY, SonargraphPluginBase.ARCH_RULE_KEY);
        will(returnValue(SonargraphRulesRepository.ARCH));
        allowing(ruleFinder).findByKey(SonargraphPluginBase.PLUGIN_KEY, SonargraphPluginBase.THRESHOLD_RULE_KEY);
        will(returnValue(SonargraphRulesRepository.THRESHOLD));
        allowing(ruleFinder).findByKey(SonargraphPluginBase.PLUGIN_KEY, SonargraphPluginBase.WORKSPACE_RULE_KEY);
        will(returnValue(SonargraphRulesRepository.WORKSPACE));
      }
    });

    SonargraphSensor sensor = new SonargraphSensor(ruleFinder);

    final SensorContext sensorContext = new TestContext();

    final IProject project = context.mock(IProject.class);

    context.checking(new Expectations()
    {
      {
        allowing(project).getConfiguration();
        allowing(project).getArtifactId();
        will(returnValue("infoglue21"));
        allowing(project).getName();
        will(returnValue("infoglue"));
        allowing(project).getGroupId();
        will(returnValue("org.codehaus.sonar-plugins"));
      }
    });

    sensor.analyse(project, sensorContext, report);

    context.assertIsSatisfied();
  }
}
