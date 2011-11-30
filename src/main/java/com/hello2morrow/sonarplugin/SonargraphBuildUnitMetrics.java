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

package com.hello2morrow.sonarplugin;

import java.util.Arrays;
import java.util.List;

import org.sonar.api.measures.Metric;
import org.sonar.api.measures.Metrics;

//TODO: Separate the metrics gathered from the main section of the report from the metrics being retrieved
//      out of the build unit section.

public final class SonargraphBuildUnitMetrics implements Metrics {


  
  /* Metrics of build units that are summed up to higher - value metrics */
  /** This metric is calculated in the sensor */
  public static final Metric CYCLICITY = new Metric.Builder("sonargraph_cyclicity", "Cyclicity", Metric.ValueType.INT)
      .setDescription("Cyclicity of Project on Package Level").setDirection(Metric.DIRECTION_WORST)
      .setQualitative(true).setDomain(SonargraphProjectMetrics.DOMAIN_SONARGRAPH).create();
  
  public static final Metric INSTRUCTIONS = new Metric.Builder("sonargraph_instructions", "Byte Code Instructions",
      Metric.ValueType.INT).setDescription("Number of Byte Code Instructions").setDirection(Metric.DIRECTION_NONE)
      .setQualitative(false).setDomain(SonargraphProjectMetrics.DOMAIN_SONARGRAPH).create();

  /** TODO: Relevant metric? Seems unused at the moment */
  public static final Metric CYCLE_GROUP_SIZE = new Metric.Builder("sonargraph_cycle_group_size", "Cycle Group Size",
      Metric.ValueType.INT).setDescription("Number of Packages in Same Cycle Group")
      .setDirection(Metric.DIRECTION_WORST).setQualitative(true).setDomain(SonargraphProjectMetrics.DOMAIN_SONARGRAPH).create();

  public static final Metric UNASSIGNED_TYPES = new Metric.Builder("sonargraph_unassigned_types", "Unassigned Types",
      Metric.ValueType.INT).setDescription("Number of Types not Assigned to any Architectural Artifacts")
      .setDirection(Metric.DIRECTION_WORST).setQualitative(true).setDomain(SonargraphProjectMetrics.DOMAIN_SONARGRAPH).create();

  public static final Metric EROSION_REFS = new Metric.Builder("sonargraph_erosion_ref",
      "Structural Erosion - reference level", Metric.ValueType.INT)
      .setDescription("Number of References to Cut to Achive Zero Package Cyclicity")
      .setDirection(Metric.DIRECTION_WORST).setQualitative(true).setDomain(SonargraphProjectMetrics.DOMAIN_SONARGRAPH).create();

  public static final Metric EROSION_TYPES = new Metric.Builder("sonargraph_erosion_types",
      "Structural Erosion - type level", Metric.ValueType.INT)
      .setDescription("Number of Type Dependencies to Cut to Achive Zero Package Cyclicity")
      .setDirection(Metric.DIRECTION_WORST).setQualitative(true).setDomain(SonargraphProjectMetrics.DOMAIN_SONARGRAPH).create();

  public static final Metric EROSION_COST = new Metric.Builder("sonargraph_erosion_cost", "Structural Debt Cost",
      Metric.ValueType.INT).setDescription("Estimated Cost to Repair Structural Erosion")
      .setDirection(Metric.DIRECTION_WORST).setQualitative(true).setDomain(SonargraphProjectMetrics.DOMAIN_SONARGRAPH).create();

  public static final Metric EROSION_INDEX = new Metric.Builder("sonargraph_erosion_index", "Structural Debt Index",
      Metric.ValueType.INT).setDescription("Structural Erosion Index").setDirection(Metric.DIRECTION_WORST)
      .setQualitative(true).setDomain(SonargraphProjectMetrics.DOMAIN_SONARGRAPH).create();

  public static final Metric VIOLATING_TYPES = new Metric.Builder("sonargraph_violating_types", "Violating Types",
      Metric.ValueType.INT).setDescription("Number of Types with Outgoing Architecture Violations")
      .setDirection(Metric.DIRECTION_WORST).setQualitative(true).setDomain(SonargraphProjectMetrics.DOMAIN_SONARGRAPH).create();

  public static final Metric INTERNAL_TYPES = new Metric.Builder("sonargraph_internal_types", "Internal Types",
      Metric.ValueType.INT).setDescription("Number of Internal Types").setDirection(Metric.DIRECTION_NONE)
      .setQualitative(false).setDomain(SonargraphProjectMetrics.DOMAIN_SONARGRAPH).create();

  public static final Metric VIOLATING_DEPENDENCIES = new Metric.Builder("sonargraph_violating_dependencies",
      "Violating Dependencies", Metric.ValueType.INT).setDescription("Number of Violating Type Dependencies")
      .setDirection(Metric.DIRECTION_WORST).setQualitative(true).setDomain(SonargraphProjectMetrics.DOMAIN_SONARGRAPH).create();

  public static final Metric TYPE_DEPENDENCIES = new Metric.Builder("sonargraph_type_dependencies",
      "Type Dependencies", Metric.ValueType.INT).setDescription("Overall Number of Type Dependencies")
      .setDirection(Metric.DIRECTION_NONE).setQualitative(false).setDomain(SonargraphProjectMetrics.DOMAIN_SONARGRAPH).create();
 
  public static final Metric JAVA_FILES = new Metric.Builder("sonargraph_java_files", "Java Files",
      Metric.ValueType.INT).setDescription("Number of Java Source Files").setDirection(Metric.DIRECTION_NONE)
      .setQualitative(false).setDomain(SonargraphProjectMetrics.DOMAIN_SONARGRAPH).create();

  public static final Metric TASKS = new Metric.Builder("sonargraph_tasks", "Open Tasks", Metric.ValueType.INT)
      .setDescription("Number of Open Tasks").setDirection(Metric.DIRECTION_WORST).setQualitative(true)
      .setDomain(SonargraphProjectMetrics.DOMAIN_SONARGRAPH).create();

  public static final Metric TASK_REFS = new Metric.Builder("sonargraph_task_refs", "Open Tasks - Lines to Change",
      Metric.ValueType.INT).setDescription("Number of Lines Associated with Open Tasks")
      .setDirection(Metric.DIRECTION_WORST).setQualitative(true).setDomain(SonargraphProjectMetrics.DOMAIN_SONARGRAPH).create();

  public static final Metric THRESHOLD_WARNINGS = new Metric.Builder("sonargraph_threshold_warnings",
      "Threshold Warnings", Metric.ValueType.INT).setDescription("Number of Threshold Violations")
      .setDirection(Metric.DIRECTION_WORST).setQualitative(true).setDomain(SonargraphProjectMetrics.DOMAIN_SONARGRAPH).create();

  public static final Metric DUPLICATE_WARNINGS = new Metric.Builder("sonargraph_duplicate_warnings",
      "Duplicate Code Block Warnings", Metric.ValueType.INT).setDescription("Number of Duplicate Code Blocks")
      .setDirection(Metric.DIRECTION_WORST).setQualitative(true).setDomain(SonargraphProjectMetrics.DOMAIN_SONARGRAPH).create();

  public static final Metric IGNORED_WARNINGS = new Metric.Builder("sonargraph_ignored_warnings", "Ignored Warnings",
      Metric.ValueType.INT).setDescription("Number of Ignord Warnings and Threshold Violations")
      .setDirection(Metric.DIRECTION_NONE).setQualitative(false).setDomain(SonargraphProjectMetrics.DOMAIN_SONARGRAPH).create();

  public static final Metric IGNORED_VIOLATONS = new Metric.Builder("sonargraph_ignored_violations",
      "Ignored Violations", Metric.ValueType.INT).setDescription("Number of Ignored Type Dependency Violatins")
      .setDirection(Metric.DIRECTION_NONE).setQualitative(false).setDomain(SonargraphProjectMetrics.DOMAIN_SONARGRAPH).create();
  
  
  
  public List<Metric> getMetrics() {
    return Arrays.asList(CYCLICITY, INSTRUCTIONS, CYCLE_GROUP_SIZE,
        UNASSIGNED_TYPES, VIOLATING_TYPES, VIOLATING_DEPENDENCIES, TYPE_DEPENDENCIES, JAVA_FILES, TASKS, EROSION_INDEX,
        THRESHOLD_WARNINGS, DUPLICATE_WARNINGS, IGNORED_WARNINGS,
        IGNORED_VIOLATONS, EROSION_REFS, EROSION_TYPES, EROSION_COST, INTERNAL_TYPES,
        TASK_REFS);
  }
}
