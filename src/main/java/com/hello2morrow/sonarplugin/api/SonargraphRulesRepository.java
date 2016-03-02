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

import com.hello2morrow.sonarplugin.foundation.SonargraphPluginBase;
import org.sonar.api.batch.BatchSide;
import org.sonar.api.rule.Severity;
import org.sonar.api.server.rule.RulesDefinition;
import org.sonar.plugins.java.Java;

@BatchSide
public final class SonargraphRulesRepository implements RulesDefinition {

  private static final String SONARGRAPH_TASK = "Sonargraph Task";
  private static final String SONARGRAPH_WORKSPACE_WARNING = "Sonargraph Workspace Warning";

  public SonargraphRulesRepository() {
    super();
  }

  @Override
  public void define(final Context context) {
    final NewRepository repository = context.createRepository(SonargraphPluginBase.PLUGIN_KEY, Java.KEY).setName("Sonargraph Rules");
    final NewRule archViolationRule = repository.createRule(SonargraphPluginBase.ARCH_RULE_KEY);
    archViolationRule.setName("Sonargraph Architecture Violation");
    archViolationRule.setHtmlDescription("Violation of the architecture defined using Sonargraph");
    archViolationRule.setSeverity(Severity.MAJOR);

    final NewRule duplicatesRule = repository.createRule(SonargraphPluginBase.DUPLICATE_RULE_KEY);
    duplicatesRule.setName("Sonargraph Duplicate Code Block");
    duplicatesRule.setHtmlDescription("Duplicate code block detected by Sonargraph");
    duplicatesRule.setSeverity(Severity.MAJOR);

    final NewRule cycleGroups = repository.createRule(SonargraphPluginBase.CYCLE_GROUP_RULE_KEY);
    cycleGroups.setName("Sonargraph Cycle Group");
    cycleGroups.setHtmlDescription("Cycle group detected by Sonargraph");
    cycleGroups.setSeverity(Severity.MAJOR);

    final NewRule workspace = repository.createRule(SonargraphPluginBase.WORKSPACE_RULE_KEY);
    workspace.setName(SONARGRAPH_WORKSPACE_WARNING);
    workspace.setHtmlDescription(SONARGRAPH_WORKSPACE_WARNING);
    workspace.setSeverity(Severity.MAJOR);

    final NewRule task = repository.createRule(SonargraphPluginBase.TASK_RULE_KEY);
    task.setName(SONARGRAPH_TASK);
    task.setHtmlDescription(SONARGRAPH_TASK);
    task.setSeverity(Severity.MAJOR);

    final NewRule thresholdRule = repository.createRule(SonargraphPluginBase.THRESHOLD_RULE_KEY);
    thresholdRule.setName("Sonargraph Threshold Violation");
    thresholdRule.setHtmlDescription("Violation of a threshold defined using Sonargraph");
    thresholdRule.setSeverity(Severity.MAJOR);

    repository.done();
  }
}
