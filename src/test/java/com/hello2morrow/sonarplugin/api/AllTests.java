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

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import com.hello2morrow.sonarplugin.decorator.SonargraphDerivedMetricsDecoratorTest;
import com.hello2morrow.sonarplugin.decorator.SonargraphSystemDashBoardDecoratorTest;
import com.hello2morrow.sonarplugin.foundation.ReportFileReaderTest;
import com.hello2morrow.sonarplugin.foundation.UtilitiesTest;
import com.hello2morrow.sonarplugin.metric.SonargraphBuildUnitMetricsTest;
import com.hello2morrow.sonarplugin.metric.SonargraphSystemMetricsTest;
import com.hello2morrow.sonarplugin.metric.internal.SonargraphInternalMetricsTest;

@RunWith(Suite.class)
@SuiteClasses({ 
  ReadTest.class, 
  SonargraphPluginTest.class, 
  SonargraphRulesRepositoryTest.class,
  SonargraphInternalMetricsTest.class,
  SonargraphSensorTest.class, 
  SonargraphDerivedMetricsDecoratorTest.class,
  SonargraphSystemDashBoardDecoratorTest.class,
  ReportFileReaderTest.class,
  UtilitiesTest.class, 
  SonargraphBuildUnitMetricsTest.class, 
  SonargraphSystemMetricsTest.class})
public class AllTests {

}
