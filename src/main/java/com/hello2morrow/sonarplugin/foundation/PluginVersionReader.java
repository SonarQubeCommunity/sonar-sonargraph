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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * The version is dynamically generated during build and this class extracts it from the generated properties file. 
 * @author Ingmar
 */
public class PluginVersionReader {
  public static final PluginVersionReader INSTANCE = new PluginVersionReader();

  private static final Logger LOGGER = LoggerFactory.getLogger(PluginVersionReader.class);

  public static final String UNKNOWN = null;

  private String version = "unknown";

  private PluginVersionReader() {
    final InputStream is = getClass().getResourceAsStream("/com/hello2morrow/sonarplugin/sonargraph/version.properties");
    final Properties props = new Properties();
    try {
      props.load(is);
      final Object versionProperty = props.get("version");
      if (versionProperty != null) {
        version = versionProperty.toString();
      }
    } catch (final IOException ex) {
      LOGGER.error("Failed to determine version of plugin", ex);
    }
  }

  /**
   * @return the current version of the plugin
   */
  public String getVersion() {
    return version;
  }
}
