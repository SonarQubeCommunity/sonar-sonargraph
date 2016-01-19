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
import com.hello2morrow.sonarplugin.foundation.TestHelper;
import org.junit.Before;
import org.sonar.api.batch.SensorContext;
import org.sonar.api.batch.fs.FileSystem;
import org.sonar.api.config.Settings;
import org.sonar.api.profiles.RulesProfile;
import org.sonar.api.resources.Project;

import java.io.File;

public abstract class AbstractSonargraphTest {

  private RulesProfile rulesProfile;
  private SensorContext sensorContext;
  private FileSystem moduleFileSystem;
  private Settings settings;

  public AbstractSonargraphTest() {
    super();
  }

  @Before
  public void setup() {
    rulesProfile = TestHelper.initRulesProfile();
    moduleFileSystem = TestHelper.initModuleFileSystem();
    sensorContext = TestHelper.initSensorContext(moduleFileSystem);
    settings = TestHelper.initSettings();
    settings.setProperty(SonargraphPluginBase.REPORT_PATH, getReport());
  }

  protected RulesProfile getRulesProfile() {
    return rulesProfile;
  }

  protected SensorContext getSensorContext() {
    return sensorContext;
  }

  protected FileSystem getModuleFileSystem() {
    return moduleFileSystem;
  }

  protected Settings getSettings() {
    return settings;
  }

  protected abstract String getReport();

  protected void initProjectFileSystem(File baseDir, Project project) {

  }
}
