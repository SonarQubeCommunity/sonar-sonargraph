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

import com.hello2morrow.sonarplugin.api.SonargraphRulesRepository;
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
import org.sonar.api.batch.sensor.coverage.CoverageType;
import org.sonar.api.batch.sensor.coverage.internal.DefaultCoverage;
import org.sonar.api.batch.sensor.duplication.Duplication;
import org.sonar.api.batch.sensor.highlighting.internal.DefaultHighlighting;
import org.sonar.api.batch.sensor.internal.SensorStorage;
import org.sonar.api.batch.sensor.issue.Issue;
import org.sonar.api.batch.sensor.measure.internal.DefaultMeasure;
import org.sonar.api.component.ResourcePerspectives;
import org.sonar.api.config.Settings;
import org.sonar.api.measures.Measure;
import org.sonar.api.measures.Metric;
import org.sonar.api.profiles.RulesProfile;
import org.sonar.api.resources.Resource;
import org.sonar.api.rule.RuleKey;
import org.sonar.api.server.rule.RulesDefinition;
import org.sonar.api.server.rule.RulesDefinition.Repository;
import org.sonar.api.server.rule.RulesDefinition.Rule;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class TestHelper {

  private static class InMemorySensorStorage implements SensorStorage {

    private final Map<String, org.sonar.api.batch.sensor.measure.Measure<Serializable>> measures = new HashMap<>();

    private final Collection<Issue> allIssues = new ArrayList<>();

    private final Map<String, DefaultHighlighting> highlightingByComponent = new HashMap<>();
    private final Map<String, Map<CoverageType, DefaultCoverage>> coverageByComponent = new HashMap<>();

    private final List<Duplication> duplications = new ArrayList<>();

    @Override
    public void store(final org.sonar.api.batch.sensor.measure.Measure measure) {
      measures.put(measure.metric().key(), measure);
    }

    @Override
    public void store(final Issue issue) {
      allIssues.add(issue);
    }

    @Override
    public void store(final Duplication duplication) {
      duplications.add(duplication);
    }

    @Override
    public void store(final DefaultHighlighting highlighting) {
      highlightingByComponent.put(highlighting.inputFile().key(), highlighting);
    }

    @Override
    public void store(final DefaultCoverage defaultCoverage) {
      final String key = defaultCoverage.inputFile().key();
      if (!coverageByComponent.containsKey(key)) {
        coverageByComponent.put(key, new EnumMap<CoverageType, DefaultCoverage>(CoverageType.class));
      }
      coverageByComponent.get(key).put(defaultCoverage.type(), defaultCoverage);
    }

  }

  public static RulesProfile initRulesProfile() {
    final RulesDefinition rulesDefinition = new SonargraphRulesRepository();
    final RulesDefinition.Context context = new RulesDefinition.Context();
    rulesDefinition.define(context);

    final Repository repository = context.repository(SonargraphPluginBase.PLUGIN_KEY);
    final RulesProfile profile = RulesProfile.create(SonargraphPluginBase.PLUGIN_KEY, "JAVA");

    activateRule(repository, profile, SonargraphPluginBase.TASK_RULE_KEY);
    activateRule(repository, profile, SonargraphPluginBase.CYCLE_GROUP_RULE_KEY);
    activateRule(repository, profile, SonargraphPluginBase.DUPLICATE_RULE_KEY);
    activateRule(repository, profile, SonargraphPluginBase.ARCH_RULE_KEY);
    activateRule(repository, profile, SonargraphPluginBase.THRESHOLD_RULE_KEY);
    activateRule(repository, profile, SonargraphPluginBase.WORKSPACE_RULE_KEY);
    return profile;
  }

  private static void activateRule(final Repository repository, final RulesProfile profile, final String ruleKey) {
    final Rule rule = repository.rule(ruleKey);
    profile.activateRule(org.sonar.api.rules.Rule.create(repository.key(), rule.key(), rule.name()), null);
  }

  public static Settings initSettings() {
    final Settings settings = new Settings();
    settings.setProperty(SonargraphPluginBase.COST_PER_INDEX_POINT, 7.0);
    return settings;
  }

  @SuppressWarnings("rawtypes")
  public static SensorContext initSensorContext(final FileSystem moduleFileSystem) {
    final InMemorySensorStorage sensorStorage = new InMemorySensorStorage();
    final SensorContext sensorContext = mock(SensorContext.class);

    when(sensorContext.activeRules()).thenAnswer(new Answer() {

      @Override
      public Object answer(final InvocationOnMock invocation) throws Throwable {
        final ActiveRulesBuilder builder = new ActiveRulesBuilder();
        return builder.create(RuleKey.of(SonargraphPluginBase.PLUGIN_KEY, SonargraphPluginBase.ARCH_RULE_KEY)).activate().build();
      }
    });

    when(sensorContext.fileSystem()).thenAnswer(new Answer() {
      @Override
      public Object answer(final InvocationOnMock invocation) throws Throwable {
        return moduleFileSystem;
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

    when(sensorContext.getMeasure(any(Metric.class))).thenAnswer(new Answer() {

      @Override
      public Object answer(final InvocationOnMock invocation) {
        final Object arg = invocation.getArguments()[0];
        final Measure result = new Measure((Metric) arg);
        result.setValue(0.0);
        return result;
      }
    });

    return sensorContext;
  }

  public static FileSystem initModuleFileSystem() {
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

  public static ResourcePerspectives initPerspectives() {
    final ResourcePerspectives perspectives = mock(ResourcePerspectives.class);
    // when(perspectives.as(perspectiveClass, resource))
    return perspectives;
  }
}
