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
import org.sonar.api.resources.Project;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

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
    assertTrue(SonarQubeUtilities.isRootParentProject(parentRoot));
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
