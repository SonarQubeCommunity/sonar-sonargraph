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
import org.sonar.api.batch.fs.FilePredicate;
import org.sonar.api.batch.fs.FileSystem;
import org.sonar.api.batch.fs.InputFile;
import org.sonar.api.batch.fs.internal.DefaultFileSystem;
import org.sonar.api.component.ResourcePerspectives;
import org.sonar.api.issue.Issuable;
import org.sonar.api.issue.Issuable.IssueBuilder;
import org.sonar.api.resources.Project;
import org.sonar.api.rule.RuleKey;
import org.sonar.api.rule.Severity;

import java.io.File;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
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
    File baseDir = new File("src/test/resources");
    File sourceFile = new File(baseDir, "com/hello2morrow/sonarplugin/Test.java");

    Project project = mock(Project.class);
    when(project.key()).thenReturn("hello2morrow:AlarmClock");
    when(project.name()).thenReturn("AlarmClock");

    DefaultFileSystem projectFileSystem = new DefaultFileSystem(baseDir);

    initProjectFileSystem(baseDir, project);

    FileSystem fileSystem = mock(FileSystem.class);
    when(getModuleFileSystem().baseDir()).thenReturn(baseDir);
    when(fileSystem.baseDir()).thenReturn(baseDir);
    InputFile inputFile = mock(InputFile.class);
    when(inputFile.file()).thenReturn(sourceFile);
    when(fileSystem.inputFile(any(FilePredicate.class))).thenReturn(inputFile);

    Issuable issuable1 = mock(Issuable.class);
    IssueBuilder issueBuilder1 = mock(IssueBuilder.class);
    initIssuableAndBuilder(issuable1, issueBuilder1);

    Issuable issuable2 = mock(Issuable.class);
    IssueBuilder issueBuilder2 = mock(IssueBuilder.class);
    initIssuableAndBuilder(issuable2, issueBuilder2);

    ResourcePerspectives perspectives = mock(ResourcePerspectives.class);
    // when(perspectives.as(any(Class.class), any(Resource.class))).thenReturn(issuable1).thenReturn(issuable2);

    TaskProcessor processor = new TaskProcessor(project, fileSystem, getRulesProfile(), getSensorContext(), perspectives);
    ReportFileReader reader = new ReportFileReader();
    reader.readSonargraphReport(project, fileSystem, getSettings());
    processor.process(reader.getReport(), reader.retrieveBuildUnit(project));

    verify(issueBuilder1).message("Cut dependency to 'com.h2m.common.observer.Observable' [Tester]");
    verify(issueBuilder1).line(new Integer(7));
    verify(issueBuilder1).severity(Severity.MINOR);

    verify(issueBuilder2).message("Cut dependency to 'com.h2m.common.observer.Observable' [Tester]");
    verify(issueBuilder2).line(new Integer(13));
    verify(issueBuilder1).severity(Severity.MINOR);
  }

  private void initIssuableAndBuilder(Issuable issuable, IssueBuilder issueBuilder) {
    when(issuable.newIssueBuilder()).thenReturn(issueBuilder);

    when(issueBuilder.message(any(String.class))).thenReturn(issueBuilder);
    when(issueBuilder.ruleKey(any(RuleKey.class))).thenReturn(issueBuilder);
  }
}
