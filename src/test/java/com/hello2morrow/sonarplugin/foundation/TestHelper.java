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

import com.hello2morrow.sonarplugin.api.SonargraphRulesRepository;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.sonar.api.batch.SensorContext;
import org.sonar.api.component.ResourcePerspectives;
import org.sonar.api.config.Settings;
import org.sonar.api.measures.Measure;
import org.sonar.api.measures.Metric;
import org.sonar.api.profiles.RulesProfile;
import org.sonar.api.resources.Resource;
import org.sonar.api.rules.RulePriority;
import org.sonar.api.scan.filesystem.FileQuery;
import org.sonar.api.scan.filesystem.ModuleFileSystem;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class TestHelper {

  public static RulesProfile initRulesProfile() {
    RulesProfile profile = RulesProfile.create(SonargraphPluginBase.PLUGIN_KEY, "JAVA");
    profile.activateRule(SonargraphRulesRepository.TASK, RulePriority.MAJOR);
    profile.activateRule(SonargraphRulesRepository.CYCLE_GROUPS, RulePriority.MAJOR);
    profile.activateRule(SonargraphRulesRepository.DUPLICATES, RulePriority.MAJOR);
    profile.activateRule(SonargraphRulesRepository.ARCH, RulePriority.MAJOR);
    profile.activateRule(SonargraphRulesRepository.THRESHOLD, RulePriority.MAJOR);
    profile.activateRule(SonargraphRulesRepository.WORKSPACE, RulePriority.MAJOR);
    return profile;
  }

  public static Settings initSettings()
  {
    Settings settings = new Settings();
    settings.setProperty(SonargraphPluginBase.COST_PER_INDEX_POINT, 7.0);
    return settings;
  }

  @SuppressWarnings("rawtypes")
  public static SensorContext initSensorContext() {
    SensorContext sensorContext = mock(SensorContext.class);

    when(sensorContext.getResource(any(Resource.class))).thenAnswer(new Answer() {

      @Override
      public Object answer(InvocationOnMock invocation) {
        Object[] args = invocation.getArguments();
        return args[0];
      }
    });
    when(sensorContext.getMeasure(any(Metric.class))).thenAnswer(new Answer() {

      @Override
      public Object answer(InvocationOnMock invocation) {
        Object arg = invocation.getArguments()[0];
        Measure result = new Measure((Metric) arg);
        result.setValue(0.0);
        return result;
      }
    });

    return sensorContext;
  }

  public static ModuleFileSystem initModuleFileSystem() {
    ModuleFileSystem fileSystem = mock(ModuleFileSystem.class);

    when(fileSystem.files(any(FileQuery.class))).thenAnswer(new Answer<List<File>>() {

      @Override
      public List<File> answer(InvocationOnMock invocation) throws Throwable {
        List<File> fileList = new ArrayList<File>();
        fileList.add(new File("test.java"));
        return fileList;
      }

    });
    return fileSystem;
  }

  public static ResourcePerspectives initPerspectives() {
    ResourcePerspectives perspectives = mock(ResourcePerspectives.class);
    // when(perspectives.as(perspectiveClass, resource))
    return perspectives;
  }
}
