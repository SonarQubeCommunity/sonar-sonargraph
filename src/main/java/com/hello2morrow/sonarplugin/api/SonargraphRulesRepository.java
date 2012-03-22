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

import org.sonar.api.resources.Java;
import org.sonar.api.rules.Rule;
import org.sonar.api.rules.RuleRepository;

import com.hello2morrow.sonarplugin.foundation.SonargraphPluginBase;

import java.util.Arrays;
import java.util.List;

public final class SonargraphRulesRepository extends RuleRepository {

  public static final Rule ARCH = Rule.create(SonargraphPluginBase.PLUGIN_KEY, SonargraphPluginBase.ARCH_RULE_KEY,
      "Sonargraph Architecture Violation").setDescription("Violation of the architecture defined using Sonargraph");
  public static final Rule THRESHOLD = Rule.create(SonargraphPluginBase.PLUGIN_KEY,
      SonargraphPluginBase.THRESHOLD_RULE_KEY, "Sonargraph Threshold Violation").setDescription(
      "Violation of a threshold defined using Sonargraph");
  public static final Rule DUPLICATES = Rule.create(SonargraphPluginBase.PLUGIN_KEY,
      SonargraphPluginBase.DUPLICATE_RULE_KEY, "Sonargraph Duplicate Code Block").setDescription(
      "Duplicate code block detected by Sonargraph");
  public static final Rule CYCLE_GROUPS = Rule.create(SonargraphPluginBase.PLUGIN_KEY,
      SonargraphPluginBase.CYCLE_GROUP_RULE_KEY, "Sonargraph Cycle Group").setDescription(
      "Cycle group detected by Sonargraph");
  public static final Rule WORKSPACE = Rule.create(SonargraphPluginBase.PLUGIN_KEY,
      SonargraphPluginBase.WORKSPACE_RULE_KEY, "Sonargraph Workspace Warning").setDescription(
      "Sonargraph Workspace Warning");
  public static final Rule TASK = Rule
      .create(SonargraphPluginBase.PLUGIN_KEY, SonargraphPluginBase.TASK_RULE_KEY, "Sonargraph Task")
      .setDescription("").setDescription("Sonargraph Task");

  public SonargraphRulesRepository() {
    super(SonargraphPluginBase.PLUGIN_KEY, Java.KEY);
  }

  @Override
  public List<Rule> createRules() {
    return Arrays.asList(ARCH, THRESHOLD, TASK, CYCLE_GROUPS, WORKSPACE, DUPLICATES);
  }
}
