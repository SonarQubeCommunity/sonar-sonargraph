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

package com.hello2morrow.sonarplugin.metric;

import java.util.Arrays;
import java.util.List;

import org.sonar.api.measures.Metric;
import org.sonar.api.measures.Metrics;

public final class SonargraphSimpleMetrics implements Metrics {

  public static final String DOMAIN_SONARGRAPH = "Sonargraph";

  /** Structural Debt Dashbox metrics */
  public static final Metric EROSION_INDEX = new Metric.Builder("sonargraph_erosion_index", "Structural Debt Index",
      Metric.ValueType.INT).setDescription("Structural Erosion Index").setDirection(Metric.DIRECTION_WORST)
      .setQualitative(true).setDomain(DOMAIN_SONARGRAPH).create();

  public static final Metric EROSION_COST = new Metric.Builder("sonargraph_erosion_cost", "Structural Debt Cost",
      Metric.ValueType.INT).setDescription("Estimated Cost to Repair Structural Erosion")
      .setDirection(Metric.DIRECTION_WORST).setQualitative(true).setDomain(DOMAIN_SONARGRAPH).create();

  public static final Metric TASKS = new Metric.Builder("sonargraph_tasks", "Open Tasks", Metric.ValueType.INT)
      .setDescription("Number of Open Tasks").setDirection(Metric.DIRECTION_WORST).setQualitative(true)
      .setDomain(DOMAIN_SONARGRAPH).create();

  public static final Metric TASK_REFS = new Metric.Builder("sonargraph_task_refs", "Open Tasks - Lines to Change",
      Metric.ValueType.INT).setDescription("Number of Lines Associated with Open Tasks")
      .setDirection(Metric.DIRECTION_WORST).setQualitative(true).setDomain(DOMAIN_SONARGRAPH).create();

  /** Structure Dashbox metrics */
  public static final Metric CYCLICITY = new Metric.Builder("sonargraph_cyclicity", "Cyclicity", Metric.ValueType.INT)
      .setDescription("Cyclicity of Project on Package Level").setDirection(Metric.DIRECTION_WORST)
      .setQualitative(true).setDomain(DOMAIN_SONARGRAPH).create();

  public static final Metric CYCLIC_PACKAGES = new Metric.Builder("sonargraph_cyclic_packages", "Cyclic Packages",
      Metric.ValueType.INT).setDescription("Number of Cyclic Packages").setDirection(Metric.DIRECTION_WORST)
      .setQualitative(true).setDomain(DOMAIN_SONARGRAPH).create();

  public static final Metric EROSION_TYPES = new Metric.Builder("sonargraph_erosion_types",
      "Structural Erosion - type level", Metric.ValueType.INT)
      .setDescription("Number of Type Dependencies to Cut to Achive Zero Package Cyclicity")
      .setDirection(Metric.DIRECTION_WORST).setQualitative(true).setDomain(DOMAIN_SONARGRAPH).create();

  public static final Metric EROSION_REFS = new Metric.Builder("sonargraph_erosion_ref",
      "Structural Erosion - reference level", Metric.ValueType.INT)
      .setDescription("Number of References to Cut to Achieve Zero Package Cyclicity")
      .setDirection(Metric.DIRECTION_WORST).setQualitative(true).setDomain(DOMAIN_SONARGRAPH).create();

  public static final Metric ACD = new Metric.Builder("sonargraph_acd", "ACD", Metric.ValueType.FLOAT)
      .setDescription("Average Component Dependency (John Lakos)").setDirection(Metric.DIRECTION_WORST)
      .setQualitative(true).setDomain(DOMAIN_SONARGRAPH).create();

  public static final Metric NCCD = new Metric.Builder("sonargraph_nccd", "NCCD", Metric.ValueType.FLOAT)
      .setDescription("Normalized Cummulative Component Dependency (John Lakos)").setDirection(Metric.DIRECTION_WORST)
      .setQualitative(true).setDomain(DOMAIN_SONARGRAPH).create();

  public static final Metric INSTRUCTIONS = new Metric.Builder("sonargraph_instructions", "Byte Code Instructions",
      Metric.ValueType.INT).setDescription("Number of Byte Code Instructions").setDirection(Metric.DIRECTION_NONE)
      .setQualitative(false).setDomain(DOMAIN_SONARGRAPH).create();

  /** TODO: Relevant metric? Seems unused at the moment */
  public static final Metric CYCLE_GROUP_SIZE = new Metric.Builder("sonargraph_cycle_group_size", "Cycle Group Size",
      Metric.ValueType.INT).setDescription("Number of Packages in Same Cycle Group")
      .setDirection(Metric.DIRECTION_WORST).setQualitative(true).setDomain(DOMAIN_SONARGRAPH).create();

  /** Architecture Dashbox metrics */
  public static final Metric VIOLATING_TYPE_DEPENDENCIES = new Metric.Builder("sonargraph_violating_dependencies",
      "Violating Dependencies", Metric.ValueType.INT).setDescription("Number of Violating Type Dependencies")
      .setDirection(Metric.DIRECTION_WORST).setQualitative(true).setDomain(DOMAIN_SONARGRAPH).create();

  public static final Metric VIOLATING_TYPES = new Metric.Builder("sonargraph_violating_types", "Violating Types",
      Metric.ValueType.INT).setDescription("Number of Types with Outgoing Architecture Violations")
      .setDirection(Metric.DIRECTION_WORST).setQualitative(true).setDomain(DOMAIN_SONARGRAPH).create();

  public static final Metric VIOLATING_REFERENCES = new Metric.Builder("sonargraph_architecture_violations",
      "Architecture Violations (references)", Metric.ValueType.INT).setDescription("Number of Violating References")
      .setDirection(Metric.DIRECTION_WORST).setQualitative(true).setDomain(DOMAIN_SONARGRAPH).create();

  public static final Metric IGNORED_VIOLATONS = new Metric.Builder("sonargraph_ignored_violations",
      "Ignored Violations", Metric.ValueType.INT).setDescription("Number of Ignored Type Dependency Violatins")
      .setDirection(Metric.DIRECTION_NONE).setQualitative(false).setDomain(DOMAIN_SONARGRAPH).create();

  public static final Metric UNASSIGNED_TYPES = new Metric.Builder("sonargraph_unassigned_types", "Unassigned Types",
      Metric.ValueType.INT).setDescription("Number of Types not Assigned to any Architectural Artifacts")
      .setDirection(Metric.DIRECTION_WORST).setQualitative(true).setDomain(DOMAIN_SONARGRAPH).create();

  /** Warnings */
  public static final Metric ALL_WARNINGS = new Metric.Builder("sonargraph_all_warnings", "All Warnings",
      Metric.ValueType.INT).setDescription("Number of All Warnings").setDirection(Metric.DIRECTION_WORST)
      .setQualitative(true).setDomain(DOMAIN_SONARGRAPH).create();

  public static final Metric CYCLE_WARNINGS = new Metric.Builder("sonargraph_cycle_warnings", "Cycle Warnings",
      Metric.ValueType.INT).setDescription("Number of Cycle Warnings").setDirection(Metric.DIRECTION_WORST)
      .setQualitative(true).setDomain(DOMAIN_SONARGRAPH).create();

  public static final Metric DUPLICATE_WARNINGS = new Metric.Builder("sonargraph_duplicate_warnings",
      "Duplicate Code Block Warnings", Metric.ValueType.INT).setDescription("Number of Duplicate Code Blocks")
      .setDirection(Metric.DIRECTION_WORST).setQualitative(true).setDomain(DOMAIN_SONARGRAPH).create();

  public static final Metric THRESHOLD_WARNINGS = new Metric.Builder("sonargraph_threshold_warnings",
      "Threshold Warnings", Metric.ValueType.INT).setDescription("Number of Threshold Violations")
      .setDirection(Metric.DIRECTION_WORST).setQualitative(true).setDomain(DOMAIN_SONARGRAPH).create();

  public static final Metric WORKSPACE_WARNINGS = new Metric.Builder("sonargraph_workspace_warnings",
      "Workspace Warnings", Metric.ValueType.INT).setDescription("Number of Sonargraph Parser Warnings")
      .setDirection(Metric.DIRECTION_WORST).setQualitative(true).setDomain(DOMAIN_SONARGRAPH).create();

  public static final Metric IGNORED_WARNINGS = new Metric.Builder("sonargraph_ignored_warnings", "Ignored Warnings",
      Metric.ValueType.INT).setDescription("Number of Ignord Warnings and Threshold Violations")
      .setDirection(Metric.DIRECTION_NONE).setQualitative(false).setDomain(DOMAIN_SONARGRAPH).create();

  /** Generic Metrics */
  public static final Metric INTERNAL_TYPES = new Metric.Builder("sonargraph_internal_types", "Internal Types",
      Metric.ValueType.INT).setDescription("Number of Internal Types").setDirection(Metric.DIRECTION_NONE)
      .setQualitative(false).setDomain(DOMAIN_SONARGRAPH).create();

  public static final Metric TYPE_DEPENDENCIES = new Metric.Builder("sonargraph_type_dependencies",
      "Type Dependencies", Metric.ValueType.INT).setDescription("Overall Number of Type Dependencies")
      .setDirection(Metric.DIRECTION_NONE).setQualitative(false).setDomain(DOMAIN_SONARGRAPH).create();

  public static final Metric JAVA_FILES = new Metric.Builder("sonargraph_java_files", "Java Files",
      Metric.ValueType.INT).setDescription("Number of Java Source Files").setDirection(Metric.DIRECTION_NONE)
      .setQualitative(false).setDomain(DOMAIN_SONARGRAPH).create();

  public static final Metric INTERNAL_PACKAGES = new Metric.Builder("sonargraph_packages", "Packages",
      Metric.ValueType.INT).setDescription("Number of Internal Packages").setDirection(Metric.DIRECTION_NONE)
      .setQualitative(false).setDomain(DOMAIN_SONARGRAPH).create();

  public List<Metric> getMetrics() {
    return Arrays.asList(EROSION_INDEX, EROSION_COST, TASKS, TASK_REFS, CYCLICITY, CYCLIC_PACKAGES, EROSION_TYPES,
        EROSION_REFS, ACD, NCCD, INSTRUCTIONS, CYCLE_GROUP_SIZE, VIOLATING_TYPE_DEPENDENCIES, VIOLATING_TYPES,
        VIOLATING_REFERENCES, IGNORED_VIOLATONS, UNASSIGNED_TYPES, ALL_WARNINGS, CYCLE_WARNINGS, DUPLICATE_WARNINGS,
        THRESHOLD_WARNINGS, WORKSPACE_WARNINGS, IGNORED_WARNINGS, INTERNAL_PACKAGES, INTERNAL_TYPES, TYPE_DEPENDENCIES,
        JAVA_FILES);
  }
}
