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

package com.hello2morrow.sonarplugin.decorator;

import com.hello2morrow.sonarplugin.foundation.Utilities;
import com.hello2morrow.sonarplugin.metric.SonargraphSimpleMetrics;
import org.sonar.api.batch.DependedUpon;
import org.sonar.api.measures.Metric;
import org.sonar.api.profiles.RulesProfile;
import org.sonar.api.resources.Project;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * These metrics are safe to be simply summed up for the parent project.
 *
 * @author Ingmar
 *
 */
public class SonargraphMetricAggregator extends AbstractMetricAggregator {

  public SonargraphMetricAggregator(RulesProfile profile) {
    super(profile);
  }

  @Override
  public boolean shouldExecuteOnProject(Project project) {
    return Utilities.isAggregatingProject(project) && super.shouldExecuteOnProject(project);
  }

  @Override
  @DependedUpon
  public List<Metric> generatesMetrics() {

    return Arrays.asList(SonargraphSimpleMetrics.INTERNAL_PACKAGES, SonargraphSimpleMetrics.JAVA_FILES, SonargraphSimpleMetrics.INTERNAL_TYPES,
      SonargraphSimpleMetrics.TYPE_DEPENDENCIES,

      /* structural debt metrics */
      SonargraphSimpleMetrics.STRUCTURAL_DEBT_INDEX, SonargraphSimpleMetrics.STRUCTURAL_DEBT_COST, SonargraphSimpleMetrics.TASK_REFS, SonargraphSimpleMetrics.TASKS,

      /* structure metrics */
      SonargraphSimpleMetrics.CYCLICITY, SonargraphSimpleMetrics.CYCLIC_PACKAGES, SonargraphSimpleMetrics.REFERENCES_TO_REMOVE, SonargraphSimpleMetrics.TYPE_DEPENDENCIES_TO_CUT,
      SonargraphSimpleMetrics.INSTRUCTIONS,

      /* architecture metrics */
      SonargraphSimpleMetrics.VIOLATING_TYPE_DEPENDENCIES, SonargraphSimpleMetrics.VIOLATING_TYPES, SonargraphSimpleMetrics.VIOLATING_REFERENCES,
      SonargraphSimpleMetrics.IGNORED_VIOLATONS, SonargraphSimpleMetrics.UNASSIGNED_TYPES,

      /* warnings */
      SonargraphSimpleMetrics.DUPLICATE_WARNINGS);
  }

  @Override
  List<Metric> addThresholdForMetrics() {
    List<Metric> metrics = new ArrayList<Metric>(generatesMetrics());
    // Alert level for cyclicity is copied from relative cyclicity in SonargraphDerivedMetricsDecorator
    metrics.remove(SonargraphSimpleMetrics.CYCLICITY);
    return metrics;
  }
}
