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
import com.hello2morrow.sonarplugin.metric.SonargraphSimpleMetrics;
import org.junit.Before;
import org.junit.Test;
import org.sonar.api.batch.SensorContext;
import org.sonar.api.batch.fs.FilePredicate;
import org.sonar.api.batch.fs.FileSystem;
import org.sonar.api.resources.Project;
import org.sonar.api.resources.Qualifiers;

import java.io.File;
import java.util.Arrays;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class SonargraphSensorTest extends AbstractSonargraphTest {

  public static final String REPORT = "src/test/resources/sonargraph-sonar-report.xml";
  private SonargraphSensor sensor;

  @Override
  protected String getReport() {
    return REPORT;
  }

  @Before
  public void initSensor() {
    sensor = new SonargraphSensor(getSettings());
  }

  @Test
  public void testAnalyseRootParentProject() {
    final Project rootProject = new Project("hello2morrow:AlarmClock");
    final Project module = new Project("module");
    module.setParent(rootProject);
    final SensorContext context = mock(SensorContext.class);
    when(context.getMeasure(SonargraphSimpleMetrics.INTERNAL_PACKAGES)).thenReturn(null);

    sensor.analyse(null, context);
    assertNull(context.getMeasure(SonargraphSimpleMetrics.INTERNAL_PACKAGES));
  }

  @Test
  public void testAnalyse() {
    final Project project = mock(Project.class); // ("hello2morrow:AlarmClock", "", "AlarmClock");
    doReturn("hello2morrow:AlarmClock").when(project).key();
    doReturn("AlarmClock").when(project).name();
    doReturn(Qualifiers.MODULE).when(project).getQualifier();

    final FileSystem projectFileSystem = mock(FileSystem.class);
    final File baseDir = new File("src/test/resources");
    when(getModuleFileSystem().baseDir()).thenReturn(baseDir);
    final File sourceFile = new File(baseDir, "com/hello2morrow/sonarplugin/Test.java");
    when(projectFileSystem.baseDir()).thenReturn(baseDir);

    when(getModuleFileSystem().files(any(FilePredicate.class))).thenReturn(Arrays.asList(sourceFile));

    sensor.analyse(project, getSensorContext());
    final double value = getSensorContext().getMeasure(SonargraphSimpleMetrics.WORKSPACE_WARNINGS).getValue().doubleValue();
    assertEquals(0.0, value, 0.01);
  }

  @Test
  public void testShouldExecuteOnProject() {
    final Project project = new Project("hello2morrow:AlarmClock", "", "AlarmClock");
    assertTrue(sensor.shouldExecuteOnProject(project));

    final Project module = new Project("hello2morrow:Foundation", "", "Foundation");
    module.setParent(project);
    assertFalse(sensor.shouldExecuteOnProject(project));
    assertTrue(sensor.shouldExecuteOnProject(module));
  }

  @Test
  public void testShouldNotExecuteOnProjectWithoutReport() {
    final Project project = new Project("hello2morrow:AlarmClock", "", "AlarmClock");
    final Project module = new Project("hello2morrow:Foundation", "", "Foundation");
    module.setParent(project);
    getSettings().setProperty(SonargraphPluginBase.REPORT_PATH, "c:/fantasyPath");
    assertFalse("Sensor must not execute on aggregating project", sensor.shouldExecuteOnProject(project));
  }
}
