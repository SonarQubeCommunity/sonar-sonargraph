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

import com.hello2morrow.sonarplugin.api.AbstractSonargraphTest;
import com.hello2morrow.sonarplugin.persistence.ReportFileReader;
import org.junit.Test;
import org.sonar.api.batch.SensorContext;
import org.sonar.api.batch.fs.FilePredicate;
import org.sonar.api.batch.fs.FileSystem;
import org.sonar.api.batch.fs.InputFile;
import org.sonar.api.batch.sensor.issue.NewIssue;
import org.sonar.api.batch.sensor.issue.NewIssueLocation;
import org.sonar.api.resources.Project;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class TaskProcessorTest extends AbstractSonargraphTest {

  public static final String REPORT = "src/test/resources/sonargraph-sonar-report2.xml";

  @Override
  protected String getReport() {
    return REPORT;
  }

  @Test
  public void test() {

    final Project project = mock(Project.class);
    when(project.key()).thenReturn("hello2morrow:AlarmClock");
    when(project.name()).thenReturn("AlarmClock");

    final InputFile file = mock(InputFile.class);
    when(file.relativePath()).thenReturn("com/h2m/alarm/model/AlarmClock.java");
    when(file.isFile()).thenReturn(true);
    final FileSystem fileSystem = getModuleFileSystem();
    when(fileSystem.inputFile(any(FilePredicate.class))).thenReturn(file);

    final NewIssueLocation location = mock(NewIssueLocation.class);
    when(location.on(file)).thenReturn(location);
    when(location.message(any(String.class))).thenReturn(location);
    final NewIssue issue = mock(NewIssue.class);
    when(issue.newLocation()).thenReturn(location);

    when(issue.at(any(NewIssueLocation.class))).thenReturn(issue);

    final SensorContext context = getSensorContext();
    when(context.newIssue()).thenReturn(issue);

    final TaskProcessor processor = new TaskProcessor(project, getSensorContext(), 2);
    final ReportFileReader reader = new ReportFileReader();
    reader.readSonargraphReport(project, getModuleFileSystem(), getSettings());
    processor.process(reader.getReport(), reader.retrieveBuildUnit(project));

    verify(location, times(2)).message("Cut dependency to 'com.h2m.common.observer.Observable' [Tester]");
  }
}
