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
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.sonar.api.batch.SensorContext;
import org.sonar.api.batch.fs.FilePredicate;
import org.sonar.api.batch.fs.FileSystem;
import org.sonar.api.batch.fs.InputFile;
import org.sonar.api.batch.rule.ActiveRule;
import org.sonar.api.batch.rule.ActiveRules;
import org.sonar.api.batch.sensor.issue.NewIssue;
import org.sonar.api.batch.sensor.issue.NewIssueLocation;
import org.sonar.api.config.Settings;
import org.sonar.api.resources.Project;
import org.sonar.api.rule.RuleKey;

import java.util.Arrays;
import java.util.List;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class WarningProcessorTest {

  @Test
  public void testProcessDuplicates() {
    final FileSystem fileSystem = TestHelper.initFileSystem();
    final String alarmClock = "com/h2m/alarm/model/AlarmClock.java";
    final InputFile alarmClockFile = mock(InputFile.class);
    when(alarmClockFile.absolutePath()).thenReturn("");
    when(alarmClockFile.relativePath()).thenReturn(alarmClock);

    final String main = "com/h2m/alarm/presentation/Main.java";
    final InputFile mainFile = mock(InputFile.class);
    when(mainFile.absolutePath()).thenReturn("");
    when(mainFile.relativePath()).thenReturn(main);

    final List<InputFile> files = Arrays.asList(alarmClockFile, mainFile);

    when(fileSystem.inputFile(any(FilePredicate.class))).thenAnswer(new Answer<InputFile>() {
      @Override
      public InputFile answer(final InvocationOnMock invocation) throws Throwable {
        final Object arg = invocation.getArguments()[0];
        final FilePredicate predicate = (FilePredicate) arg;
        for (final InputFile file : files) {
          if (predicate.apply(file)) {
            return file;
          }
        }
        return null;
      }
    });

    final NewIssueLocation locationAlarm = mock(NewIssueLocation.class);
    final NewIssueLocation locationMain = mock(NewIssueLocation.class);

    when(locationAlarm.on(alarmClockFile)).thenReturn(locationAlarm);
    when(locationAlarm.message(any(String.class))).thenReturn(locationAlarm);
    final NewIssue issue = mock(NewIssue.class);
    when(issue.newLocation()).thenReturn(locationAlarm);
    when(issue.at(any(NewIssueLocation.class))).thenReturn(issue);

    when(locationMain.on(mainFile)).thenReturn(locationMain);
    when(locationMain.message(any(String.class))).thenReturn(locationMain);
    final NewIssue issue2 = mock(NewIssue.class);
    when(issue2.newLocation()).thenReturn(locationMain);
    when(issue2.at(any(NewIssueLocation.class))).thenReturn(issue2);

    final SensorContext sensorContext = mock(SensorContext.class);
    when(sensorContext.newIssue()).thenReturn(issue).thenReturn(issue2);

    final ActiveRules activeRules = mock(ActiveRules.class);
    final ActiveRule activeRule = mock(ActiveRule.class);
    final RuleKey ruleKey = mock(RuleKey.class);
    when(ruleKey.rule()).thenReturn(SonargraphPluginBase.DUPLICATE_RULE_KEY);
    when(activeRule.ruleKey()).thenReturn(ruleKey);

    when(activeRules.findByRepository(SonargraphPluginBase.PLUGIN_KEY)).thenReturn(Arrays.asList(activeRule));
    when(sensorContext.activeRules()).thenReturn(activeRules);
    when(sensorContext.fileSystem()).thenReturn(fileSystem);

    final ReportFileReader reader = new ReportFileReader();
    final Project project = mock(Project.class);
    final Settings settings = new Settings();
    settings.setProperty(SonargraphPluginBase.REPORT_PATH, TestHelper.REPORT_PATH);
    reader.readSonargraphReport(project, fileSystem, settings);

    final ReportContext report = reader.getReport();

    final XsdAttributeRoot processedBuildUnit = report.getBuildUnits().getBuildUnit().get(0);
    final IProcessor processor = new WarningProcessor(sensorContext);
    processor.process(report, processedBuildUnit);

    verify(issue).save();
    verify(issue2).save();
  }

  @Test
  public void testThresholdWarning() {
    final FileSystem fileSystem = TestHelper.initFileSystem();
    final String alarmClock = "com/h2m/alarm/model/AlarmClock.java";
    final InputFile alarmClockFile = mock(InputFile.class);
    when(alarmClockFile.absolutePath()).thenReturn("");
    when(alarmClockFile.relativePath()).thenReturn(alarmClock);
    when(alarmClockFile.isFile()).thenReturn(Boolean.TRUE);

    final List<InputFile> files = Arrays.asList(alarmClockFile);

    when(fileSystem.inputFile(any(FilePredicate.class))).thenAnswer(new Answer<InputFile>() {
      @Override
      public InputFile answer(final InvocationOnMock invocation) throws Throwable {
        final Object arg = invocation.getArguments()[0];
        final FilePredicate predicate = (FilePredicate) arg;
        for (final InputFile file : files) {
          if (predicate.apply(file)) {
            return file;
          }
        }
        return null;
      }
    });

    final NewIssueLocation locationAlarm = mock(NewIssueLocation.class);

    when(locationAlarm.on(alarmClockFile)).thenReturn(locationAlarm);
    when(locationAlarm.message(any(String.class))).thenReturn(locationAlarm);
    final NewIssue issue = mock(NewIssue.class);
    when(issue.newLocation()).thenReturn(locationAlarm);
    when(issue.at(any(NewIssueLocation.class))).thenReturn(issue);

    final SensorContext sensorContext = mock(SensorContext.class);
    when(sensorContext.newIssue()).thenReturn(issue);

    final ActiveRules activeRules = mock(ActiveRules.class);
    final ActiveRule activeRule = mock(ActiveRule.class);
    final RuleKey ruleKey = mock(RuleKey.class);
    when(ruleKey.rule()).thenReturn(SonargraphPluginBase.THRESHOLD_RULE_KEY);
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
    final WarningProcessor processor = new WarningProcessor(sensorContext);
    processor.process(report, processedBuildUnit);

    verify(issue).save();
  }
}
