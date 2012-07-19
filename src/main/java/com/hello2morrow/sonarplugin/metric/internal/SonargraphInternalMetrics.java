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

package com.hello2morrow.sonarplugin.metric.internal;

import java.util.Arrays;
import java.util.List;

import org.sonar.api.measures.Metric;
import org.sonar.api.measures.Metrics;

import com.hello2morrow.sonarplugin.metric.SonargraphSimpleMetrics;

/**
 * This class contains hidden metrics that are used to exchange information between the sensor and decorators. These metrics are not
 * accessible to the user.
 * 
 * @author Ingmar
 * 
 */
public class SonargraphInternalMetrics implements Metrics {

  private static final String INTERNAL_PREFIX = "Internal Metric - ";

  /** Internally used metric to flag that this maven module is not part of the Sonargraph workspace and should therefore be ignored. **/
  public static final Metric MODULE_NOT_PART_OF_SONARGRAPH_WORKSPACE = new Metric.Builder("sonargraph_ignore_module",
      INTERNAL_PREFIX + "Exclude Module", Metric.ValueType.BOOL)
      .setDescription("Module must be excluded from Sonargraph processing").setDirection(Metric.DIRECTION_NONE)
      .setQualitative(false).setDomain(SonargraphSimpleMetrics.DOMAIN_SONARGRAPH).setHidden(true).create();

  public static final Metric SYSTEM_ALL_WARNINGS = new Metric.Builder("sonargraph_system_all_warnings", INTERNAL_PREFIX
      + "System All Warnings", Metric.ValueType.INT).setDescription("Number of All Warnings")
      .setDirection(Metric.DIRECTION_WORST).setQualitative(true).setDomain(SonargraphSimpleMetrics.DOMAIN_SONARGRAPH)
      .setHidden(true).create();

  public static final Metric SYSTEM_CYCLE_WARNINGS = new Metric.Builder("sonargraph_system_cycle_warnings",
      INTERNAL_PREFIX + "System Cycle Warnings", Metric.ValueType.INT).setDescription("Number of Cycle Warnings")
      .setDirection(Metric.DIRECTION_WORST).setQualitative(true).setDomain(SonargraphSimpleMetrics.DOMAIN_SONARGRAPH)
      .setHidden(true).create();

  public static final Metric SYSTEM_THRESHOLD_WARNINGS = new Metric.Builder("sonargraph_system_threshold_warnings",
      "System Threshold Warnings", Metric.ValueType.INT).setDescription("Number of Threshold Violations")
      .setDirection(Metric.DIRECTION_WORST).setQualitative(true).setDomain(SonargraphSimpleMetrics.DOMAIN_SONARGRAPH)
      .setHidden(true).create();

  public static final Metric SYSTEM_WORKSPACE_WARNINGS = new Metric.Builder("sonargraph_system_workspace_warnings",
      "System Workspace Warnings", Metric.ValueType.INT).setDescription("Number of Sonargraph Parser Warnings")
      .setDirection(Metric.DIRECTION_WORST).setQualitative(true).setDomain(SonargraphSimpleMetrics.DOMAIN_SONARGRAPH)
      .setHidden(true).create();

  public static final Metric SYSTEM_IGNORED_WARNINGS = new Metric.Builder("sonargraph_system_ignored_warnings",
      "System Ignored Warnings", Metric.ValueType.INT)
      .setDescription("Number of Ignord Warnings and Threshold Violations").setDirection(Metric.DIRECTION_NONE)
      .setQualitative(false).setDomain(SonargraphSimpleMetrics.DOMAIN_SONARGRAPH).setHidden(true).create();

  public List<Metric> getMetrics() {
    return Arrays.asList(MODULE_NOT_PART_OF_SONARGRAPH_WORKSPACE, SYSTEM_ALL_WARNINGS, SYSTEM_CYCLE_WARNINGS,
        SYSTEM_THRESHOLD_WARNINGS, SYSTEM_WORKSPACE_WARNINGS, SYSTEM_IGNORED_WARNINGS);
  };

}
