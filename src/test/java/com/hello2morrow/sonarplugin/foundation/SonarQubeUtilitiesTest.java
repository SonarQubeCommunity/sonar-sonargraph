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

import org.junit.Test;
import org.sonar.api.batch.rule.Severity;
import org.sonar.api.measures.Metric;
import org.sonar.api.resources.Project;

import java.io.Serializable;
import java.util.Arrays;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class SonarQubeUtilitiesTest {

  @Test
  public void testIsRootParentProject() {
    assertFalse(SonarQubeUtilities.isRootParentProject(null));

    final Project singleProject = new Project("Test", null, "Test");
    assertFalse(SonarQubeUtilities.isRootParentProject(singleProject));

    final Project parentProject = new Project("Parent", null, "Parent");
    final Project module = new Project("Module", null, "Module");
    module.setParent(parentProject);
    assertTrue(SonarQubeUtilities.isRootParentProject(parentProject));
    assertFalse(SonarQubeUtilities.isRootParentProject(module));

    final Project parentRoot = new Project("Parent", null, "Parent");
    singleProject.setParent(parentRoot);

    assertFalse(SonarQubeUtilities.isRootParentProject(singleProject));
    assertFalse(SonarQubeUtilities.isAggregatingProject(singleProject));

    assertTrue(SonarQubeUtilities.isRootParentProject(parentRoot));
    assertTrue(SonarQubeUtilities.isAggregatingProject(parentProject));
    assertFalse(SonarQubeUtilities.isAggregatingProject(module));
  }

  @Test
  public void testConvertSeverity() {
    assertNull(SonarQubeUtilities.convertToSeverity(null));
    assertNull(SonarQubeUtilities.convertToSeverity(""));
    assertEquals(Severity.MAJOR, SonarQubeUtilities.convertToSeverity("HIGH"));
    assertEquals(Severity.MINOR, SonarQubeUtilities.convertToSeverity("MEDIUM"));
    assertEquals(Severity.INFO, SonarQubeUtilities.convertToSeverity("LOW"));
  }

  @SuppressWarnings("unchecked")
  @Test
  public void testConvertMetricListToKeys() {
    final Metric<Serializable> metric1 = mock(Metric.class);
    when(metric1.getKey()).thenReturn("key1");

    final Metric<Serializable> metric2 = mock(Metric.class);
    when(metric2.getKey()).thenReturn("key2");

    assertEquals(Arrays.asList("key1", "key2"), SonarQubeUtilities.convertMetricListToKeyList(Arrays.asList(metric1, metric2)));
  }

  @Test
  public void testIsSingleModuleProject() {
    assertFalse(SonarQubeUtilities.isSingleModuleProject(null));

    final Project project = new Project("Test", null, "Test");
    assertTrue(SonarQubeUtilities.isSingleModuleProject(project));

    final Project module = new Project("Module");
    module.setParent(project);
    assertFalse(SonarQubeUtilities.isSingleModuleProject(project));
  }

  @Test
  public void testBuildUnitMatchesAnalyzedProject() {
    String buildUnitName = "sonargraph.core";
    assertTrue(SonarQubeUtilities.buildUnitMatchesAnalyzedProject(buildUnitName, new Project("com.hello2morrow:sonargraph.core", "architect-branch_7.1.8", "test")));

    String projectKey = "com.hello2morrow:sonargraph.core";
    assertTrue(SonarQubeUtilities.buildUnitMatchesAnalyzedProject(buildUnitName, new Project(projectKey)));

    projectKey = "sonargraph.core";
    assertTrue(SonarQubeUtilities.buildUnitMatchesAnalyzedProject(buildUnitName, new Project(projectKey)));

    projectKey = "Sonargraph::com.hello2morrow:sonargraph.core";
    assertTrue(SonarQubeUtilities.buildUnitMatchesAnalyzedProject(buildUnitName, new Project(projectKey)));

    buildUnitName = SonargraphUtilities.getBuildUnitName("some.project::some.group.id:some.module.id");
    assertEquals("some.group.id:some.module.id", buildUnitName);
    final Project project = new Project("some.group.id:some.module.id");
    assertTrue(SonarQubeUtilities.buildUnitMatchesAnalyzedProject(buildUnitName, project));
  }
}
