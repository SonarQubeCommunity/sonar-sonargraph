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
package com.hello2morrow.sonarplugin.foundation;

import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.sonar.api.batch.SensorContext;
import org.sonar.api.batch.fs.FilePredicate;
import org.sonar.api.batch.fs.FilePredicates;
import org.sonar.api.batch.fs.FileSystem;
import org.sonar.api.batch.fs.InputFile;
import org.sonar.api.batch.fs.InputFile.Status;
import org.sonar.api.batch.fs.InputFile.Type;
import org.sonar.api.batch.rule.internal.ActiveRulesBuilder;
import org.sonar.api.batch.sensor.internal.SensorStorage;
import org.sonar.api.batch.sensor.measure.internal.DefaultMeasure;
import org.sonar.api.config.Settings;
import org.sonar.api.resources.Resource;
import org.sonar.api.rule.RuleKey;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class TestHelper {

  public static final String REPORT_PATH = "./src/test/resources/sonargraph-sonar-report.xml";
  public static final String REPORT_PATH2 = "./src/test/resources/sonargraph-sonar-report2.xml";

  public static Settings initSettings(final String reportPath) {
    final Settings settings = new Settings();
    settings.setProperty(SonargraphPluginBase.COST_PER_INDEX_POINT, 7.0);
    settings.setProperty(SonargraphPluginBase.REPORT_PATH, reportPath);
    return settings;
  }

  @SuppressWarnings("rawtypes")
  public static SensorContext initSensorContext(final FileSystem fileSystem, final SensorStorage sensorStorage) {
    final SensorContext sensorContext = mock(SensorContext.class);

    when(sensorContext.activeRules()).thenAnswer(new Answer() {
      @Override
      public Object answer(final InvocationOnMock invocation) throws Throwable {
        final ActiveRulesBuilder builder = new ActiveRulesBuilder();
        builder.create(RuleKey.of(SonargraphPluginBase.PLUGIN_KEY, SonargraphPluginBase.ARCH_RULE_KEY)).activate();
        builder.create(RuleKey.of(SonargraphPluginBase.PLUGIN_KEY, SonargraphPluginBase.TASK_RULE_KEY)).activate();
        builder.create(RuleKey.of(SonargraphPluginBase.PLUGIN_KEY, SonargraphPluginBase.DUPLICATE_RULE_KEY)).activate();
        builder.create(RuleKey.of(SonargraphPluginBase.PLUGIN_KEY, SonargraphPluginBase.CYCLE_GROUP_RULE_KEY)).activate();
        builder.create(RuleKey.of(SonargraphPluginBase.PLUGIN_KEY, SonargraphPluginBase.THRESHOLD_RULE_KEY)).activate();
        builder.create(RuleKey.of(SonargraphPluginBase.PLUGIN_KEY, SonargraphPluginBase.WORKSPACE_RULE_KEY)).activate();
        return builder.build();
      }
    });

    when(sensorContext.fileSystem()).thenAnswer(new Answer() {
      @Override
      public Object answer(final InvocationOnMock invocation) throws Throwable {
        return fileSystem;
      }
    });

    when(sensorContext.getResource(any(Resource.class))).thenAnswer(new Answer() {
      @Override
      public Object answer(final InvocationOnMock invocation) {
        final Object[] args = invocation.getArguments();
        return args[0];
      }
    });

    when(sensorContext.newMeasure()).thenAnswer(new Answer() {
      @Override
      public Object answer(final InvocationOnMock invocation) throws Throwable {
        return new DefaultMeasure(sensorStorage);
      }
    });

    return sensorContext;
  }

  public static FileSystem initFileSystem() {
    final FileSystem fileSystem = mock(FileSystem.class);

    when(fileSystem.hasFiles(any(FilePredicate.class))).thenAnswer(new Answer<Boolean>() {
      @Override
      public Boolean answer(final InvocationOnMock invocation) throws Throwable {
        return true;
      }
    });

    when(fileSystem.files(any(FilePredicate.class))).thenAnswer(new Answer<Iterable<File>>() {
      @Override
      public List<File> answer(final InvocationOnMock invocation) throws Throwable {
        final List<File> fileList = new ArrayList<File>();
        fileList.add(new File("com/h2m/alarm/model/AlarmClock.java"));
        return fileList;
      }
    });

    when(fileSystem.workDir()).thenReturn(new File("./src/test").getAbsoluteFile());

    when(fileSystem.predicates()).thenAnswer(new Answer<FilePredicates>() {
      @Override
      public FilePredicates answer(final InvocationOnMock invocation) throws Throwable {
        return new FilePredicates() {

          @Override
          public FilePredicate or(final FilePredicate first, final FilePredicate second) {
            return null;
          }

          @Override
          public FilePredicate or(final FilePredicate... or) {
            return null;
          }

          @Override
          public FilePredicate or(final Collection<FilePredicate> or) {
            return null;
          }

          @Override
          public FilePredicate not(final FilePredicate p) {
            return null;
          }

          @Override
          public FilePredicate none() {
            return null;
          }

          @Override
          public FilePredicate matchesPathPatterns(final String[] inclusionPatterns) {
            return null;
          }

          @Override
          public FilePredicate matchesPathPattern(final String inclusionPattern) {
            return null;
          }

          @Override
          public FilePredicate is(final File ioFile) {
            return null;
          }

          @Override
          public FilePredicate hasType(final Type type) {
            return null;
          }

          @Override
          public FilePredicate hasStatus(final Status status) {
            return null;
          }

          @Override
          public FilePredicate hasRelativePath(final String s) {
            return null;
          }

          @Override
          public FilePredicate hasPath(final String s) {
            return null;
          }

          @Override
          public FilePredicate hasLanguages(final Collection<String> languages) {
            return null;
          }

          @Override
          public FilePredicate hasLanguage(final String language) {
            return null;
          }

          @Override
          public FilePredicate hasAbsolutePath(final String s) {
            return null;
          }

          @Override
          public FilePredicate doesNotMatchPathPatterns(final String[] exclusionPatterns) {
            return null;
          }

          @Override
          public FilePredicate doesNotMatchPathPattern(final String exclusionPattern) {
            return null;
          }

          @Override
          public FilePredicate and(final FilePredicate first, final FilePredicate second) {
            return first;
          }

          @Override
          public FilePredicate and(final FilePredicate... and) {
            return new FilePredicate() {
              @Override
              public boolean apply(final InputFile inputFile) {
                return true;
              }
            };
          }

          @Override
          public FilePredicate and(final Collection<FilePredicate> and) {
            return null;
          }

          @Override
          public FilePredicate all() {
            return null;
          }

          @Override
          public FilePredicate hasLanguages(final String... arg0) {
            return null;
          }
        };
      }
    });
    return fileSystem;
  }
}
