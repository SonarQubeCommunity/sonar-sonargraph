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
import org.sonar.api.resources.Qualifiers;

import java.util.Arrays;
import java.util.List;

/**
 * This decorator is applicable for aggregating modules that are not root projects. The warning metrics are simply added from the child
 * modules.
 *
 * @author Ingmar
 *
 */
public final class SonargraphAggregatingModuleMetricAggregator extends AbstractMetricAggregator {

  public SonargraphAggregatingModuleMetricAggregator(RulesProfile profile) {
    super(profile);
  }

  @Override
  public boolean shouldExecuteOnProject(Project project) {
    return project.getQualifier().equals(Qualifiers.MODULE) && Utilities.isAggregatingProject(project) && super.shouldExecuteOnProject(project);
  }

  @Override
  @DependedUpon
  public List<Metric> generatesMetrics() {
    return Arrays.asList(SonargraphSimpleMetrics.ALL_WARNINGS, SonargraphSimpleMetrics.CYCLE_WARNINGS, SonargraphSimpleMetrics.THRESHOLD_WARNINGS,
      SonargraphSimpleMetrics.WORKSPACE_WARNINGS, SonargraphSimpleMetrics.IGNORED_WARNINGS);
  }

  @Override
  List<Metric> addThresholdForMetrics() {
    return generatesMetrics();
  }
}
