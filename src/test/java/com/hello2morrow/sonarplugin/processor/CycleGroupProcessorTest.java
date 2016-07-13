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
package com.hello2morrow.sonarplugin.processor;

import com.hello2morrow.sonarplugin.foundation.SonargraphPluginBase;
import com.hello2morrow.sonarplugin.foundation.TestHelper;
import com.hello2morrow.sonarplugin.persistence.ReportFileReader;
import com.hello2morrow.sonarplugin.xsd.ReportContext;
import com.hello2morrow.sonarplugin.xsd.XsdAttributeRoot;
import org.junit.Test;
import org.sonar.api.batch.SensorContext;
import org.sonar.api.batch.fs.FileSystem;
import org.sonar.api.batch.fs.InputDir;
import org.sonar.api.batch.rule.ActiveRule;
import org.sonar.api.batch.rule.ActiveRules;
import org.sonar.api.batch.sensor.issue.NewIssue;
import org.sonar.api.batch.sensor.issue.NewIssueLocation;
import org.sonar.api.config.Settings;
import org.sonar.api.resources.Project;
import org.sonar.api.rule.RuleKey;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class CycleGroupProcessorTest {

  @Test
  public void testProcess() throws IOException {
    final FileSystem fileSystem = TestHelper.initFileSystem(null);
    final File currentDir = new File(".").getCanonicalFile();

    final String modelPath = "AlarmClock/src/main/java/com/h2m/alarm/model";
    final InputDir modelDir = mock(InputDir.class);
    when(modelDir.absolutePath()).thenReturn("");
    when(modelDir.relativePath()).thenReturn(modelPath);
    when(modelDir.isFile()).thenReturn(Boolean.FALSE);

    final File absoluteModelDir = new File(currentDir, "src/test/AlarmClockMain/" + modelPath);
    when(fileSystem.inputDir(absoluteModelDir)).thenReturn(modelDir);

    final NewIssueLocation locationModel = mock(NewIssueLocation.class);

    when(locationModel.on(modelDir)).thenReturn(locationModel);
    when(locationModel.message(any(String.class))).thenReturn(locationModel);
    final NewIssue issue = mock(NewIssue.class);
    when(issue.newLocation()).thenReturn(locationModel);
    when(issue.at(any(NewIssueLocation.class))).thenReturn(issue);

    final SensorContext sensorContext = mock(SensorContext.class);
    when(sensorContext.newIssue()).thenReturn(issue);

    final ActiveRules activeRules = mock(ActiveRules.class);
    final ActiveRule activeRule = mock(ActiveRule.class);
    final RuleKey ruleKey = mock(RuleKey.class);
    when(ruleKey.rule()).thenReturn(SonargraphPluginBase.CYCLE_GROUP_RULE_KEY);
    when(activeRule.ruleKey()).thenReturn(ruleKey);

    when(activeRules.findByRepository(SonargraphPluginBase.PLUGIN_KEY)).thenReturn(Arrays.asList(activeRule));
    when(sensorContext.activeRules()).thenReturn(activeRules);
    when(sensorContext.fileSystem()).thenReturn(fileSystem);

    final ReportFileReader reader = new ReportFileReader();
    final Project project = mock(Project.class);
    final Settings settings = new Settings();
    settings.setProperty(SonargraphPluginBase.REPORT_PATH, TestHelper.REPORT_PATH2);
    reader.readSonargraphReport(project, fileSystem, settings);

    final ReportContext report = reader.getReport();
    final XsdAttributeRoot processedBuildUnit = report.getBuildUnits().getBuildUnit().get(0);
    final CycleGroupProcessor processor = new CycleGroupProcessor(sensorContext);
    processor.process(report, processedBuildUnit);

    assertEquals(2.0, processor.getBiggestCycleGroupSize(), 0.1);
    assertEquals(2.0, processor.getCyclicPackages(), 0.1);

    verify(issue).save();
  }
}
