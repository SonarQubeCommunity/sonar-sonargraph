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

import org.sonar.api.batch.sensor.coverage.CoverageType;
import org.sonar.api.batch.sensor.coverage.internal.DefaultCoverage;
import org.sonar.api.batch.sensor.duplication.Duplication;
import org.sonar.api.batch.sensor.highlighting.internal.DefaultHighlighting;
import org.sonar.api.batch.sensor.internal.SensorStorage;
import org.sonar.api.batch.sensor.issue.Issue;
import org.sonar.api.batch.sensor.measure.Measure;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InMemorySensorStorage implements SensorStorage {

  private final Map<String, Measure<Serializable>> measures = new HashMap<>();
  private final Map<String, List<Issue>> allIssues = new HashMap<>();
  private final Map<String, DefaultHighlighting> highlightingByComponent = new HashMap<>();
  private final Map<String, Map<CoverageType, DefaultCoverage>> coverageByComponent = new HashMap<>();

  private final List<Duplication> duplications = new ArrayList<>();

  private int issueCount = 0;

  @SuppressWarnings({"rawtypes", "unchecked"})
  @Override
  public void store(final Measure measure) {
    measures.put(measure.metric().key(), measure);
  }

  @Override
  public void store(final Issue issue) {
    List<Issue> issues = allIssues.get(issue.ruleKey().toString());
    if (issues == null) {
      issues = new ArrayList<>();
      allIssues.put(issue.ruleKey().toString(), issues);
    }
    issues.add(issue);
    issueCount++;
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

  public Measure<Serializable> getMeasure(final String key) {
    return measures.get(key);
  }

  public Map<String, List<Issue>> getIssues() {
    return Collections.unmodifiableMap(allIssues);
  }

  public int getNumberOfIssues() {
    return issueCount;
  }
}
