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

import com.hello2morrow.sonarplugin.foundation.InMemorySensorStorage;
import com.hello2morrow.sonarplugin.foundation.TestHelper;
import org.junit.Test;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.sonar.api.batch.SensorContext;
import org.sonar.api.batch.fs.FilePredicate;
import org.sonar.api.batch.fs.FileSystem;
import org.sonar.api.batch.fs.InputComponent;
import org.sonar.api.batch.fs.InputDir;
import org.sonar.api.batch.fs.InputFile;
import org.sonar.api.batch.rule.Severity;
import org.sonar.api.batch.sensor.internal.SensorStorage;
import org.sonar.api.batch.sensor.issue.Issue;
import org.sonar.api.batch.sensor.issue.IssueLocation;
import org.sonar.api.batch.sensor.issue.NewIssue;
import org.sonar.api.batch.sensor.issue.NewIssueLocation;
import org.sonar.api.config.Settings;
import org.sonar.api.resources.Project;
import org.sonar.api.resources.Qualifiers;
import org.sonar.api.rule.RuleKey;
import org.sonar.plugins.java.Java;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.TreeSet;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class SonargraphSensorTest {

  private static final String REPORT = "src/test/resources/sonargraph-sonar-report.xml";

  @Test
  public void testToString() {
    final SonargraphSensor sensor = new SonargraphSensor(null);
    assertTrue("wrong toString() format", sensor.toString().matches(SonargraphSensor.PLUGIN_NAME + " \\[[0-9\\.]+\\]"));
  }

  @Test
  public void testShouldExecuteOnProject() {
    final SonargraphSensor sensor = new SonargraphSensor(TestHelper.initSettings(REPORT));
    final Project project = new Project("hello2morrow:AlarmClock", "", "AlarmClock");
    assertTrue(sensor.shouldExecuteOnProject(project));

    final Project module = new Project("hello2morrow:Foundation", "", "Foundation");
    module.setParent(project);
    assertTrue(sensor.shouldExecuteOnProject(project));
    assertTrue(sensor.shouldExecuteOnProject(module));
  }

  @Test
  public void testIsValidProject() throws IOException {
    final SonargraphSensor sensor = new SonargraphSensor(TestHelper.initSettings(null));
    assertFalse("Not valid if both project and context == null", sensor.isValidProject(null, null));
    final Project project = mock(Project.class);
    assertFalse("Not valid if context == null", sensor.isValidProject(project, null));

    final Project module = new Project("hello2morrow:Foundation", "", "Foundation");
    when(project.isRoot()).thenReturn(Boolean.TRUE);
    when(project.getModules()).thenReturn(Arrays.asList(module));

    final SensorContext context = TestHelper.initSensorContext(TestHelper.initFileSystem(null), mock(SensorStorage.class));
    assertFalse("Not valid if project == null", sensor.isValidProject(null, context));
    assertFalse("Not valid for root parent project", sensor.isValidProject(project, context));
    assertFalse("Not valid for module without configured report", sensor.isValidProject(module, context));

    final SensorContext context2 = TestHelper.initSensorContext(TestHelper.initFileSystem(new TreeSet<String>(Arrays.asList("CPlusPlus"))), mock(SensorStorage.class));
    assertFalse("Project not valid due to non-Java language", sensor.isValidProject(project, context2));

    final SensorContext context3 = TestHelper.initSensorContext(TestHelper.initFileSystem(null), mock(SensorStorage.class), false);
    assertFalse("Project not valid due to not activated Sonargraph rules", sensor.isValidProject(module, context3));

    final File root = new File(".").getCanonicalFile();
    final File baseDir = new File(root, "src/test/AlarmClockMain");

    final FileSystem fileSystem = mock(FileSystem.class);
    when(fileSystem.baseDir()).thenReturn(baseDir);
    when(fileSystem.languages()).thenReturn(new TreeSet<String>(Arrays.asList(Java.KEY)));
    final SensorContext context4 = TestHelper.initSensorContext(fileSystem, mock(SensorStorage.class));
    final SonargraphSensor sensor2 = new SonargraphSensor(TestHelper.initSettings(TestHelper.REPORT_PATH2));
    assertTrue("Must be a valid prooject", sensor2.isValidProject(module, context4));
  }

  @Test
  public void testAnalyse() throws IOException {
    final Project project = mock(Project.class);
    doReturn("hello2morrow:AlarmClock").when(project).key();
    doReturn("AlarmClock").when(project).name();
    doReturn(Qualifiers.MODULE).when(project).getQualifier();

    final File root = new File(".").getCanonicalFile();
    final File baseDir = new File(root, "src/test/AlarmClockMain");

    final FileSystem fileSystem = mock(FileSystem.class);
    when(fileSystem.baseDir()).thenReturn(baseDir);
    when(fileSystem.languages()).thenReturn(new TreeSet<String>(Arrays.asList(Java.KEY)));

    final InMemorySensorStorage sensorStorage = new InMemorySensorStorage();
    final SensorContext sensorContext = TestHelper.initSensorContext(fileSystem, sensorStorage);
    final Settings settings = TestHelper.initSettings(TestHelper.REPORT_PATH2);
    final SonargraphSensor sensor = new SonargraphSensor(settings);

    when(fileSystem.inputFile(any(FilePredicate.class))).thenAnswer(new Answer<InputFile>() {
      @Override
      public InputFile answer(final InvocationOnMock invocation) throws Throwable {
        final Object arg = invocation.getArguments()[0];
        final FilePredicate predicate = (FilePredicate) arg;
        return findFile(baseDir, baseDir, predicate);
      }
    });
    when(fileSystem.inputDir(any(File.class))).thenAnswer(new Answer<InputDir>() {
      @Override
      public InputDir answer(final InvocationOnMock invocation) throws Throwable {
        final Object arg = invocation.getArguments()[0];
        final File dir = (File) arg;
        final InputDir mocked = mock(InputDir.class);
        final String canonicalPath = dir.getCanonicalPath();
        when(mocked.absolutePath()).thenReturn(canonicalPath);
        when(mocked.file()).thenReturn(dir);
        when(mocked.relativePath()).thenReturn(calculateRelativePath(baseDir, canonicalPath).toString());
        return mocked;
      }
    });

    when(sensorContext.newIssue()).thenAnswer(new Answer<Issue>() {
      @Override
      public Issue answer(final InvocationOnMock invocation) throws Throwable {
        return new TestIssue(sensorStorage);
      }
    });

    sensor.analyse(project, sensorContext);
    assertEquals("Wrong number of issue rules", 5, sensorStorage.getIssues().keySet().size());
    assertEquals("Wrong total number of issues", 31, sensorStorage.getNumberOfIssues());
  }

  private InputFile findFile(final File baseDir, final File currentDir, final FilePredicate predicate) throws IOException {
    for (final File next : currentDir.listFiles()) {
      if (next.isDirectory()) {
        final InputFile match = findFile(baseDir, next, predicate);
        if (match != null) {
          return match;
        }
      }
      final InputFile mocked = mock(InputFile.class);
      final String canonicalPath = next.getCanonicalPath();
      final Path pathRelative = calculateRelativePath(baseDir, canonicalPath);
      when(mocked.absolutePath()).thenReturn(canonicalPath.replace("\\", "/"));
      when(mocked.relativePath()).thenReturn(pathRelative.toString().replace("\\", "/"));
      when(mocked.file()).thenReturn(next);
      when(mocked.isFile()).thenReturn(true);
      if (predicate.apply(mocked)) {
        return mocked;
      }
    }
    return null;
  }

  private Path calculateRelativePath(final File baseDir, final String canonicalPath) throws IOException {
    final Path pathAbsolute = Paths.get(canonicalPath);
    final Path pathBase = Paths.get(baseDir.getCanonicalPath());
    final Path pathRelative = pathBase.relativize(pathAbsolute);
    return pathRelative;
  }

  private class TestIssue implements NewIssue, Issue {

    private IssueLocation issueLocation;
    private Double effort;
    private Severity severity;
    private NewIssueLocation location;
    private final SensorStorage storage;
    private RuleKey rule;

    public TestIssue(final InMemorySensorStorage storage) {
      this.storage = storage;
    }

    @Override
    public RuleKey ruleKey() {
      return rule;
    }

    @Override
    public Double effortToFix() {
      return effort;
    }

    @Override
    public Severity overriddenSeverity() {
      return severity;
    }

    @Override
    public IssueLocation primaryLocation() {
      return issueLocation;
    }

    @Override
    public List<Flow> flows() {
      return Collections.emptyList();
    }

    @Override
    public NewIssue forRule(final RuleKey ruleKey) {
      this.rule = ruleKey;
      return this;
    }

    @Override
    public NewIssue effortToFix(final Double effortToFix) {
      effort = effortToFix;
      return this;
    }

    @Override
    public NewIssue overrideSeverity(final Severity severity) {
      this.severity = severity;
      return this;
    }

    @Override
    public NewIssue at(final NewIssueLocation primaryLocation) {
      this.location = primaryLocation;
      return this;
    }

    @Override
    public NewIssue addLocation(final NewIssueLocation secondaryLocation) {
      assert false : "Not to be used";
      return this;
    }

    @Override
    public NewIssue addFlow(final Iterable<NewIssueLocation> flowLocations) {
      assert false : "Not to be used";
      return this;
    }

    @Override
    public NewIssueLocation newLocation() {
      final NewIssueLocation location = mock(NewIssueLocation.class);

      when(location.on(any(InputComponent.class))).thenReturn(location);
      when(location.message(any(String.class))).thenReturn(location);
      return location;
    }

    @Override
    public void save() {
      storage.store(this);
    }

    @Override
    public String toString() {
      return "Issue for rule: " + ruleKey();
    }
  }
}
