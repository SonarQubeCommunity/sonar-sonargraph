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

import com.hello2morrow.sonarplugin.foundation.SonargraphPluginBase;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.sonar.api.profiles.RulesProfile;
import org.sonar.api.resources.Project;
import org.sonar.api.resources.Qualifiers;
import org.sonar.api.rules.ActiveRule;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public abstract class AbstractMetricAggregatorTest {
  private RulesProfile profile;
  private AbstractMetricAggregator aggregator;

  @Before
  public void setup() {
    profile = Mockito.mock(RulesProfile.class);
    aggregator = creatorAggregator(profile);

  }

  protected abstract AbstractMetricAggregator creatorAggregator(RulesProfile profile);

  protected RulesProfile getProfile() {
    return profile;
  }

  protected AbstractMetricAggregator getAggregator() {
    return aggregator;
  }

  @Test
  public void testShouldExecuteOnProject() {
    Project project = mock(Project.class);

    Project subModule = mock(Project.class);
    ArrayList<Project> modules = new ArrayList<Project>();
    modules.add(subModule);
    when(project.getModules()).thenReturn(modules);
    when(project.getQualifier()).thenReturn(Qualifiers.MODULE);

    List<ActiveRule> activeRules = new ArrayList<ActiveRule>();
    activeRules.add(mock(ActiveRule.class));
    when(getProfile().getActiveRulesByRepository(SonargraphPluginBase.PLUGIN_KEY)).thenReturn(activeRules).thenReturn(Collections.<ActiveRule>emptyList());

    assertTrue("Should execute on project!", getAggregator().shouldExecuteOnProject(project));
    assertFalse("Should not execute on project (no sonargraph rules)!", getAggregator().shouldExecuteOnProject(project));
  }
}
