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

import com.hello2morrow.sonarplugin.metric.SonargraphSimpleMetrics;
import org.sonar.api.measures.Metric;
import org.sonar.api.measures.Metrics;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * This class contains hidden metrics that are used to exchange information between the sensor and metric computer. These metrics are not
 * accessible to the user.
 *
 * @author Ingmar
 *
 */

@SuppressWarnings("rawtypes")
public class SonargraphInternalMetrics implements Metrics {

  private static final String INTERNAL_PREFIX = "Internal Metric - ";

  /** Internally used Metric to flag that this maven module has been processed by the sensor. **/
  public static final Metric<Serializable> MODULE_PROCESSED_BY_SENSOR = new Metric.Builder("sonargraph_module_processed_by_sensor", INTERNAL_PREFIX + "Module processed by sensor",
    Metric.ValueType.BOOL).setDescription("Module has been processed by Sonargraph sensor").setDirection(Metric.DIRECTION_NONE).setQualitative(false)
    .setDomain(SonargraphSimpleMetrics.DOMAIN_SONARGRAPH).setHidden(true).create();

  public static final Metric<Serializable> ROOT_PROJECT_TO_BE_PROCESSED = new Metric.Builder("sonargraph_root_project_to_be_processed",
    INTERNAL_PREFIX + "Root Sonargraph project", Metric.ValueType.BOOL).setDescription("Root project that needs to be processed by measure computers")
    .setDirection(Metric.DIRECTION_NONE).setQualitative(false).setDomain(SonargraphSimpleMetrics.DOMAIN_SONARGRAPH).setHidden(true).create();

  public static final Metric<Serializable> SYSTEM_ALL_WARNINGS = new Metric.Builder("sonargraph_system_all_warnings", INTERNAL_PREFIX + "System All Warnings", Metric.ValueType.INT)
    .setDescription("Number of All Warnings").setDirection(Metric.DIRECTION_WORST).setQualitative(false).setDomain(SonargraphSimpleMetrics.DOMAIN_SONARGRAPH).setHidden(true)
    .create();

  public static final Metric<Serializable> SYSTEM_CYCLE_WARNINGS = new Metric.Builder("sonargraph_system_cycle_warnings", INTERNAL_PREFIX + "System Cycle Warnings",
    Metric.ValueType.INT).setDescription("Number of Cycle Warnings").setDirection(Metric.DIRECTION_WORST).setQualitative(false)
    .setDomain(SonargraphSimpleMetrics.DOMAIN_SONARGRAPH).setHidden(true).create();

  public static final Metric<Serializable> SYSTEM_THRESHOLD_WARNINGS = new Metric.Builder("sonargraph_system_threshold_warnings", "System Threshold Warnings", Metric.ValueType.INT)
    .setDescription("Number of Threshold Violations").setDirection(Metric.DIRECTION_WORST).setQualitative(false).setDomain(SonargraphSimpleMetrics.DOMAIN_SONARGRAPH)
    .setHidden(true).create();

  public static final Metric<Serializable> SYSTEM_WORKSPACE_WARNINGS = new Metric.Builder("sonargraph_system_workspace_warnings", "System Workspace Warnings", Metric.ValueType.INT)
    .setDescription("Number of Sonargraph Parser Warnings").setDirection(Metric.DIRECTION_WORST).setQualitative(false).setDomain(SonargraphSimpleMetrics.DOMAIN_SONARGRAPH)
    .setHidden(true).create();

  public static final Metric<Serializable> SYSTEM_IGNORED_WARNINGS = new Metric.Builder("sonargraph_system_ignored_warnings", "System Ignored Warnings", Metric.ValueType.INT)
    .setDescription("Number of Ignord Warnings and Threshold Violations").setDirection(Metric.DIRECTION_NONE).setQualitative(false)
    .setDomain(SonargraphSimpleMetrics.DOMAIN_SONARGRAPH).setHidden(true).create();

  public static final Metric<Serializable> SYSTEM_ALL_TASKS = new Metric.Builder("sonargraph_system_all_tasks", "System All Tasks", Metric.ValueType.INT)
    .setDescription("Number of Tasks").setDirection(Metric.DIRECTION_WORST).setQualitative(false).setDomain(SonargraphSimpleMetrics.DOMAIN_SONARGRAPH).setHidden(true).create();

  @Override
  public List<Metric> getMetrics() {
    List<Metric<Serializable>> metrics = Arrays.asList(MODULE_PROCESSED_BY_SENSOR, ROOT_PROJECT_TO_BE_PROCESSED, SYSTEM_ALL_WARNINGS, SYSTEM_CYCLE_WARNINGS,
      SYSTEM_THRESHOLD_WARNINGS, SYSTEM_WORKSPACE_WARNINGS, SYSTEM_IGNORED_WARNINGS, SYSTEM_ALL_TASKS);
    List<Metric> newMetrics = new ArrayList<>();
    newMetrics.addAll(metrics);
    return newMetrics;
  }
}
