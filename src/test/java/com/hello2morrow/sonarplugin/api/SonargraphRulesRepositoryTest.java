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

import com.hello2morrow.sonarplugin.foundation.Java;
import com.hello2morrow.sonarplugin.foundation.SonargraphPluginBase;
import org.junit.Test;
import org.sonar.api.server.rule.RulesDefinition;
import org.sonar.api.server.rule.RulesDefinition.Repository;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class SonargraphRulesRepositoryTest {

  @Test
  public void testCreateRules() {
    RulesDefinition rulesDefinition = new SonargraphRulesRepository();
    RulesDefinition.Context context = new RulesDefinition.Context();
    rulesDefinition.define(context);

    Repository repository = context.repository(SonargraphPluginBase.PLUGIN_KEY);
    assertNotNull(SonargraphPluginBase.PLUGIN_KEY, repository);
    assertEquals(new Java().getKey(), repository.language());
    List<RulesDefinition.Rule> rules = repository.rules();
    assertEquals(6, rules.size());

    assertNotNull(repository.rule(SonargraphPluginBase.ARCH_RULE_KEY));
    assertNotNull(repository.rule(SonargraphPluginBase.THRESHOLD_RULE_KEY));
    assertNotNull(repository.rule(SonargraphPluginBase.TASK_RULE_KEY));
    assertNotNull(repository.rule(SonargraphPluginBase.CYCLE_GROUP_RULE_KEY));
    assertNotNull(repository.rule(SonargraphPluginBase.WORKSPACE_RULE_KEY));
    assertNotNull(repository.rule(SonargraphPluginBase.DUPLICATE_RULE_KEY));
  }
}
