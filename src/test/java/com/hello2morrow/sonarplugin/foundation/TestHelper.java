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

    private final Map<String, org.sonar.api.batch.sensor.measure.Measure> measures = new HashMap<>();

    private final Collection<Issue> allIssues = new ArrayList<>();

    private final Map<String, DefaultHighlighting> highlightingByComponent = new HashMap<>();
    private final Map<String, Map<CoverageType, DefaultCoverage>> coverageByComponent = new HashMap<>();

    private final List<Duplication> duplications = new ArrayList<>();

    @Override
    public void store(org.sonar.api.batch.sensor.measure.Measure measure) {
      measures.put(measure.metric().key(), measure);
    }

    @Override
    public void store(Issue issue) {
      allIssues.add(issue);
    }

    @Override
    public void store(Duplication duplication) {
      duplications.add(duplication);
    }

    @Override
    public void store(DefaultHighlighting highlighting) {
      highlightingByComponent.put(highlighting.inputFile().key(), highlighting);
    }

    @Override
    public void store(DefaultCoverage defaultCoverage) {
      String key = defaultCoverage.inputFile().key();
      if (!coverageByComponent.containsKey(key)) {
        coverageByComponent.put(key, new EnumMap<CoverageType, DefaultCoverage>(CoverageType.class));
      }
      coverageByComponent.get(key).put(defaultCoverage.type(), defaultCoverage);
    }

  }

  public static RulesProfile initRulesProfile() {
    RulesDefinition rulesDefinition = new SonargraphRulesRepository();
    RulesDefinition.Context context = new RulesDefinition.Context();
    rulesDefinition.define(context);

    Repository repository = context.repository(SonargraphPluginBase.PLUGIN_KEY);
    RulesProfile profile = RulesProfile.create(SonargraphPluginBase.PLUGIN_KEY, "JAVA");

    activateRule(repository, profile, SonargraphPluginBase.TASK_RULE_KEY);
    activateRule(repository, profile, SonargraphPluginBase.CYCLE_GROUP_RULE_KEY);
    activateRule(repository, profile, SonargraphPluginBase.DUPLICATE_RULE_KEY);
    activateRule(repository, profile, SonargraphPluginBase.ARCH_RULE_KEY);
    activateRule(repository, profile, SonargraphPluginBase.THRESHOLD_RULE_KEY);
    activateRule(repository, profile, SonargraphPluginBase.WORKSPACE_RULE_KEY);
    return profile;
  }

  private static void activateRule(Repository repository, RulesProfile profile, String ruleKey) {
    Rule rule = repository.rule(ruleKey);
    profile.activateRule(org.sonar.api.rules.Rule.create(repository.key(), rule.key(), rule.name()), null);
  }

  public static Settings initSettings() {
    Settings settings = new Settings();
    settings.setProperty(SonargraphPluginBase.COST_PER_INDEX_POINT, 7.0);
    return settings;
  }

  @SuppressWarnings("rawtypes")
  public static SensorContext initSensorContext(final FileSystem moduleFileSystem) {
    final InMemorySensorStorage sensorStorage = new InMemorySensorStorage();
    SensorContext sensorContext = mock(SensorContext.class);

    when(sensorContext.activeRules()).thenAnswer(new Answer() {

      @Override
      public Object answer(InvocationOnMock invocation) throws Throwable {
        ActiveRulesBuilder builder = new ActiveRulesBuilder();
        return builder.create(RuleKey.of(SonargraphPluginBase.PLUGIN_KEY, SonargraphPluginBase.ARCH_RULE_KEY)).activate().build();
      }
    });

    when(sensorContext.fileSystem()).thenAnswer(new Answer() {
      @Override
      public Object answer(InvocationOnMock invocation) throws Throwable {
        return moduleFileSystem;
      }
    });

    when(sensorContext.getResource(any(Resource.class))).thenAnswer(new Answer() {
      @Override
      public Object answer(InvocationOnMock invocation) {
        Object[] args = invocation.getArguments();
        return args[0];
      }
    });

    when(sensorContext.newMeasure()).thenAnswer(new Answer() {
      @Override
      public Object answer(InvocationOnMock invocation) throws Throwable {
        return new DefaultMeasure(sensorStorage);
      }
    });

    when(sensorContext.getMeasure(any(Metric.class))).thenAnswer(new Answer() {

      @Override
      public Object answer(InvocationOnMock invocation) {
        Object arg = invocation.getArguments()[0];
        Measure result = new Measure((Metric) arg);
        result.setValue(0.0);
        return result;
      }
    });

    return sensorContext;
  }

  public static FileSystem initModuleFileSystem() {
    FileSystem fileSystem = mock(FileSystem.class);

    when(fileSystem.hasFiles(any(FilePredicate.class))).thenAnswer(new Answer<Boolean>() {
      @Override
      public Boolean answer(InvocationOnMock invocation) throws Throwable {
        return true;
      }
    });

    when(fileSystem.files(any(FilePredicate.class))).thenAnswer(new Answer<Iterable<File>>() {
      @Override
      public List<File> answer(InvocationOnMock invocation) throws Throwable {
        List<File> fileList = new ArrayList<File>();
        fileList.add(new File("com/h2m/alarm/model/AlarmClock.java"));
        return fileList;
      }
    });

    when(fileSystem.predicates()).thenAnswer(new Answer<FilePredicates>() {
      @Override
      public FilePredicates answer(InvocationOnMock invocation) throws Throwable {
        return new FilePredicates() {

          @Override
          public FilePredicate or(FilePredicate first, FilePredicate second) {
            return null;
          }

          @Override
          public FilePredicate or(FilePredicate... or) {
            return null;
          }

          @Override
          public FilePredicate or(Collection<FilePredicate> or) {
            return null;
          }

          @Override
          public FilePredicate not(FilePredicate p) {
            return null;
          }

          @Override
          public FilePredicate none() {
            return null;
          }

          @Override
          public FilePredicate matchesPathPatterns(String[] inclusionPatterns) {
            return null;
          }

          @Override
          public FilePredicate matchesPathPattern(String inclusionPattern) {
            return null;
          }

          @Override
          public FilePredicate is(File ioFile) {
            return null;
          }

          @Override
          public FilePredicate hasType(Type type) {
            return null;
          }

          @Override
          public FilePredicate hasStatus(Status status) {
            return null;
          }

          @Override
          public FilePredicate hasRelativePath(String s) {
            return null;
          }

          @Override
          public FilePredicate hasPath(String s) {
            return null;
          }

          @Override
          public FilePredicate hasLanguages(Collection<String> languages) {
            return null;
          }

          @Override
          public FilePredicate hasLanguage(String language) {
            return null;
          }

          @Override
          public FilePredicate hasAbsolutePath(String s) {
            return null;
          }

          @Override
          public FilePredicate doesNotMatchPathPatterns(String[] exclusionPatterns) {
            return null;
          }

          @Override
          public FilePredicate doesNotMatchPathPattern(String exclusionPattern) {
            return null;
          }

          @Override
          public FilePredicate and(FilePredicate first, FilePredicate second) {
            return first;
          }

          @Override
          public FilePredicate and(FilePredicate... and) {
            return new FilePredicate() {
              @Override
              public boolean apply(InputFile inputFile) {
                return true;
              }
            };
          }

          @Override
          public FilePredicate and(Collection<FilePredicate> and) {
            return null;
          }

          @Override
          public FilePredicate all() {
            return null;
          }

          @Override
          public FilePredicate hasLanguages(String... arg0) {
            return null;
          }
        };
      }
    });
    return fileSystem;
  }

  public static ResourcePerspectives initPerspectives() {
    ResourcePerspectives perspectives = mock(ResourcePerspectives.class);
    // when(perspectives.as(perspectiveClass, resource))
    return perspectives;
  }
}
