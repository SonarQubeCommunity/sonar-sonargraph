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

import com.hello2morrow.sonarplugin.foundation.JavaLanguage;
import com.hello2morrow.sonarplugin.foundation.TestHelper;
import com.hello2morrow.sonarplugin.metric.SonargraphSimpleMetrics;
import com.hello2morrow.sonarplugin.metric.internal.SonargraphInternalMetrics;
import org.junit.Test;
import org.sonar.api.batch.Decorator;
import org.sonar.api.batch.DecoratorContext;
import org.sonar.api.measures.Measure;
import org.sonar.api.resources.Project;

import java.util.ArrayList;
import java.util.List;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class SonargraphSystemDashBoardDecoratorTest {

  @Test
  public void testShouldExecuteOnProject() {
    Project project = new Project("project");
    project.setLanguage(JavaLanguage.INSTANCE);
    Project module = new Project("module");
    module.setParent(project);

    Decorator decorator = new SonargraphSystemDashBoardDecorator(TestHelper.initRulesProfile(), TestHelper.initModuleFileSystem());
    assertTrue(decorator.shouldExecuteOnProject(project));
    assertFalse(decorator.shouldExecuteOnProject(module));
  }

  @Test
  public void testDecorate() {
    Project project = new Project("test");
    Project module1 = new Project("module1", "", "module1");
    Project module2 = new Project("module2", "", "module2");
    module1.setParent(project);
    module2.setParent(project);

    DecoratorContext child1 = mock(DecoratorContext.class);
    DecoratorContext child2 = mock(DecoratorContext.class);
    List<DecoratorContext> children = new ArrayList<DecoratorContext>();
    children.add(child1);
    children.add(child2);
    DecoratorContext context = new MockDecoratorContext(project, children);
    double allWarnings = 1.0;
    double cycleWarnings = 2.0;
    double thresholdWarnings = 3.0;
    double workspaceWarnings = 4.0;
    double ignoredWarnings = 5.0;

    when(child1.getMeasure(SonargraphInternalMetrics.MODULE_NOT_PART_OF_SONARGRAPH_WORKSPACE)).thenReturn(
      new Measure(SonargraphInternalMetrics.MODULE_NOT_PART_OF_SONARGRAPH_WORKSPACE, 1.0));
    when(child1.getProject()).thenReturn(module1);

    when(child2.getProject()).thenReturn(module2);
    when(child2.getMeasure(SonargraphSimpleMetrics.INSTRUCTIONS)).thenReturn(
      new Measure(SonargraphSimpleMetrics.INSTRUCTIONS, 23.0));
    when(child2.getMeasure(SonargraphInternalMetrics.SYSTEM_ALL_WARNINGS)).thenReturn(
      new Measure(SonargraphInternalMetrics.SYSTEM_ALL_WARNINGS, allWarnings));
    when(child2.getMeasure(SonargraphInternalMetrics.SYSTEM_CYCLE_WARNINGS)).thenReturn(
      new Measure(SonargraphInternalMetrics.SYSTEM_CYCLE_WARNINGS, cycleWarnings));
    when(child2.getMeasure(SonargraphInternalMetrics.SYSTEM_THRESHOLD_WARNINGS)).thenReturn(
      new Measure(SonargraphInternalMetrics.SYSTEM_THRESHOLD_WARNINGS, thresholdWarnings));
    when(child2.getMeasure(SonargraphInternalMetrics.SYSTEM_WORKSPACE_WARNINGS)).thenReturn(
      new Measure(SonargraphInternalMetrics.SYSTEM_WORKSPACE_WARNINGS, workspaceWarnings));
    when(child2.getMeasure(SonargraphInternalMetrics.SYSTEM_IGNORED_WARNINGS)).thenReturn(
      new Measure(SonargraphInternalMetrics.SYSTEM_IGNORED_WARNINGS, ignoredWarnings));

    Decorator decorator = new SonargraphSystemDashBoardDecorator(TestHelper.initRulesProfile(), TestHelper.initModuleFileSystem());
    decorator.decorate(project, context);

    assertEquals(allWarnings, context.getMeasure(SonargraphSimpleMetrics.ALL_WARNINGS).getValue());
    assertEquals(cycleWarnings, context.getMeasure(SonargraphSimpleMetrics.CYCLE_WARNINGS).getValue());
    assertEquals(thresholdWarnings, context.getMeasure(SonargraphSimpleMetrics.THRESHOLD_WARNINGS).getValue());
    assertEquals(workspaceWarnings, context.getMeasure(SonargraphSimpleMetrics.WORKSPACE_WARNINGS).getValue());
    assertEquals(ignoredWarnings, context.getMeasure(SonargraphSimpleMetrics.IGNORED_WARNINGS).getValue());
  }

}
