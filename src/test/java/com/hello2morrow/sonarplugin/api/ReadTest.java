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

import com.hello2morrow.sonarplugin.foundation.SonargraphPluginBase;
import com.hello2morrow.sonarplugin.foundation.TestHelper;
import com.hello2morrow.sonarplugin.persistence.IReportReader;
import com.hello2morrow.sonarplugin.persistence.ReportFileReader;
import com.hello2morrow.sonarplugin.xsd.XsdAttributeRoot;
import junit.framework.TestCase;
import org.junit.Test;
import org.sonar.api.config.Settings;
import org.sonar.api.resources.Project;

public class ReadTest extends TestCase {

  @Test
  public void testAnalyse() {
    final Project project1 = new Project("test");
    final Settings settings = TestHelper.initSettings();
    settings.setProperty(SonargraphPluginBase.REPORT_PATH, "src/test/resources/infoglue21-report.xml");
    final IReportReader reader = new ReportFileReader();
    reader.readSonargraphReport(project1, null, settings);

    final Project project = new Project("org.codehaus.sonar-plugins:infoglue21", null, "infoglue");
    final XsdAttributeRoot buildUnit = reader.retrieveBuildUnit(project);
    assertNotNull(buildUnit);
  }
}
