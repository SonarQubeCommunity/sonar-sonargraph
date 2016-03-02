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

import org.sonar.api.measures.Metric;
import org.sonar.api.measures.Metrics;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@SuppressWarnings("rawtypes")
public final class SonargraphSimpleMetrics implements Metrics {

  public static final String DOMAIN_SONARGRAPH = "Sonargraph";

  /** Structural Debt Dashbox metrics */
  public static final Metric<Serializable> STRUCTURAL_DEBT_INDEX = new Metric.Builder("sonargraph_erosion_index", "Structural Debt Index", Metric.ValueType.INT)
    .setDescription("Structural Erosion Index").setDirection(Metric.DIRECTION_WORST).setQualitative(true).setDomain(DOMAIN_SONARGRAPH).create();

  public static final Metric<Serializable> STRUCTURAL_DEBT_COST = new Metric.Builder("sonargraph_erosion_cost", "Structural Debt Cost", Metric.ValueType.INT)
    .setDescription("Estimated Cost to Repair Structural Erosion").setDirection(Metric.DIRECTION_WORST).setQualitative(true).setDomain(DOMAIN_SONARGRAPH).create();

  public static final Metric<Serializable> TASKS = new Metric.Builder("sonargraph_tasks", "Open Tasks", Metric.ValueType.INT).setDescription("Number of Open Tasks")
    .setDirection(Metric.DIRECTION_WORST).setQualitative(true).setDomain(DOMAIN_SONARGRAPH).create();

  public static final Metric<Serializable> TASK_REFS = new Metric.Builder("sonargraph_task_refs", "Open Tasks - Lines to Change", Metric.ValueType.INT)
    .setDescription("Number of Lines Associated with Open Tasks").setDirection(Metric.DIRECTION_WORST).setQualitative(true).setDomain(DOMAIN_SONARGRAPH).create();

  /** Structure Dashbox metrics */
  public static final Metric<Serializable> CYCLICITY = new Metric.Builder("sonargraph_cyclicity", "Cyclicity", Metric.ValueType.INT)
    .setDescription("Cyclicity of Project on Package Level").setDirection(Metric.DIRECTION_WORST).setQualitative(true).setDomain(DOMAIN_SONARGRAPH).create();

  public static final Metric<Serializable> CYCLIC_PACKAGES = new Metric.Builder("sonargraph_cyclic_packages", "Cyclic Packages", Metric.ValueType.INT)
    .setDescription("Number of Cyclic Packages").setDirection(Metric.DIRECTION_WORST).setQualitative(true).setDomain(DOMAIN_SONARGRAPH).create();

  public static final Metric<Serializable> TYPE_DEPENDENCIES_TO_CUT = new Metric.Builder("sonargraph_erosion_types", "Structural Erosion - type level", Metric.ValueType.INT)
    .setDescription("Number of Type Dependencies to Cut to Achive Zero Package Cyclicity").setDirection(Metric.DIRECTION_WORST).setQualitative(true).setDomain(DOMAIN_SONARGRAPH)
    .create();

  public static final Metric<Serializable> REFERENCES_TO_REMOVE = new Metric.Builder("sonargraph_erosion_ref", "Structural Erosion - reference level", Metric.ValueType.INT)
    .setDescription("Number of References to Cut to Achieve Zero Package Cyclicity").setDirection(Metric.DIRECTION_WORST).setQualitative(true).setDomain(DOMAIN_SONARGRAPH)
    .create();

  public static final Metric<Serializable> ACD = new Metric.Builder("sonargraph_acd", "ACD", Metric.ValueType.FLOAT).setDescription("Average Component Dependency (John Lakos)")
    .setDirection(Metric.DIRECTION_WORST).setQualitative(true).setDomain(DOMAIN_SONARGRAPH).create();

  public static final Metric<Serializable> RELATIVE_ACD = new Metric.Builder("sonargraph_relative_acd", "Relative ACD", Metric.ValueType.FLOAT)
    .setDescription("Relative Average Component Dependency (John Lakos)").setDirection(Metric.DIRECTION_WORST).setQualitative(true).setDomain(DOMAIN_SONARGRAPH).create();

  public static final Metric<Serializable> NCCD = new Metric.Builder("sonargraph_nccd", "NCCD", Metric.ValueType.FLOAT)
    .setDescription("Normalized Cummulative Component Dependency (John Lakos)").setDirection(Metric.DIRECTION_WORST).setQualitative(true).setDomain(DOMAIN_SONARGRAPH).create();

  public static final Metric<Serializable> INSTRUCTIONS = new Metric.Builder("sonargraph_instructions", "Byte Code Instructions", Metric.ValueType.INT)
    .setDescription("Number of Byte Code Instructions").setDirection(Metric.DIRECTION_NONE).setQualitative(false).setDomain(DOMAIN_SONARGRAPH).create();

  /** Architecture Dashbox metrics */
  public static final Metric<Serializable> VIOLATING_TYPE_DEPENDENCIES = new Metric.Builder("sonargraph_violating_dependencies", "Violating Dependencies", Metric.ValueType.INT)
    .setDescription("Number of Violating Type Dependencies").setDirection(Metric.DIRECTION_WORST).setQualitative(true).setDomain(DOMAIN_SONARGRAPH).create();

  public static final Metric<Serializable> VIOLATING_TYPES = new Metric.Builder("sonargraph_violating_types", "Violating Types", Metric.ValueType.INT)
    .setDescription("Number of Types with Outgoing Architecture Violations").setDirection(Metric.DIRECTION_WORST).setQualitative(true).setDomain(DOMAIN_SONARGRAPH).create();

  public static final Metric<Serializable> VIOLATING_REFERENCES = new Metric.Builder("sonargraph_architecture_violations", "Architecture Violations (references)",
    Metric.ValueType.INT).setDescription("Number of Violating References").setDirection(Metric.DIRECTION_WORST).setQualitative(true).setDomain(DOMAIN_SONARGRAPH).create();

  public static final Metric<Serializable> IGNORED_VIOLATONS = new Metric.Builder("sonargraph_ignored_violations", "Ignored Violations", Metric.ValueType.INT)
    .setDescription("Number of Ignored Type Dependency Violatins").setDirection(Metric.DIRECTION_NONE).setQualitative(false).setDomain(DOMAIN_SONARGRAPH).create();

  public static final Metric<Serializable> UNASSIGNED_TYPES = new Metric.Builder("sonargraph_unassigned_types", "Unassigned Types", Metric.ValueType.INT)
    .setDescription("Number of Types not Assigned to any Architectural Artifacts").setDirection(Metric.DIRECTION_WORST).setQualitative(true).setDomain(DOMAIN_SONARGRAPH).create();

  /** Warnings */
  public static final Metric<Serializable> ALL_WARNINGS = new Metric.Builder("sonargraph_all_warnings", "All Warnings", Metric.ValueType.INT)
    .setDescription("Number of All Warnings").setDirection(Metric.DIRECTION_WORST).setQualitative(true).setDomain(DOMAIN_SONARGRAPH).create();

  public static final Metric<Serializable> CYCLE_WARNINGS = new Metric.Builder("sonargraph_cycle_warnings", "Cycle Warnings", Metric.ValueType.INT)
    .setDescription("Number of Cycle Warnings").setDirection(Metric.DIRECTION_WORST).setQualitative(true).setDomain(DOMAIN_SONARGRAPH).create();

  public static final Metric<Serializable> DUPLICATE_WARNINGS = new Metric.Builder("sonargraph_duplicate_warnings", "Duplicate Code Block Warnings", Metric.ValueType.INT)
    .setDescription("Number of Duplicate Code Blocks").setDirection(Metric.DIRECTION_WORST).setQualitative(true).setDomain(DOMAIN_SONARGRAPH).create();

  public static final Metric<Serializable> THRESHOLD_WARNINGS = new Metric.Builder("sonargraph_threshold_warnings", "Threshold Warnings", Metric.ValueType.INT)
    .setDescription("Number of Threshold Violations").setDirection(Metric.DIRECTION_WORST).setQualitative(true).setDomain(DOMAIN_SONARGRAPH).create();

  public static final Metric<Serializable> WORKSPACE_WARNINGS = new Metric.Builder("sonargraph_workspace_warnings", "Workspace Warnings", Metric.ValueType.INT)
    .setDescription("Number of Sonargraph Parser Warnings").setDirection(Metric.DIRECTION_WORST).setQualitative(true).setDomain(DOMAIN_SONARGRAPH).create();

  public static final Metric<Serializable> IGNORED_WARNINGS = new Metric.Builder("sonargraph_ignored_warnings", "Ignored Warnings", Metric.ValueType.INT)
    .setDescription("Number of Ignord Warnings and Threshold Violations").setDirection(Metric.DIRECTION_NONE).setQualitative(false).setDomain(DOMAIN_SONARGRAPH).create();

  /** Generic Metrics */
  public static final Metric<Serializable> INTERNAL_TYPES = new Metric.Builder("sonargraph_internal_types", "Internal Types", Metric.ValueType.INT)
    .setDescription("Number of Internal Types").setDirection(Metric.DIRECTION_NONE).setQualitative(false).setDomain(DOMAIN_SONARGRAPH).create();

  public static final Metric<Serializable> TYPE_DEPENDENCIES = new Metric.Builder("sonargraph_type_dependencies", "Type Dependencies", Metric.ValueType.INT)
    .setDescription("Overall Number of Type Dependencies").setDirection(Metric.DIRECTION_NONE).setQualitative(false).setDomain(DOMAIN_SONARGRAPH).create();

  public static final Metric<Serializable> JAVA_FILES = new Metric.Builder("sonargraph_java_files", "Java Files", Metric.ValueType.INT)
    .setDescription("Number of Java Source Files").setDirection(Metric.DIRECTION_NONE).setQualitative(false).setDomain(DOMAIN_SONARGRAPH).create();

  public static final Metric<Serializable> INTERNAL_PACKAGES = new Metric.Builder("sonargraph_packages", "Packages", Metric.ValueType.INT)
    .setDescription("Number of Internal Packages").setDirection(Metric.DIRECTION_NONE).setQualitative(false).setDomain(DOMAIN_SONARGRAPH).create();

  @Override
  public List<Metric> getMetrics() {
    final List<Metric<Serializable>> metrics = Arrays.asList(STRUCTURAL_DEBT_INDEX, STRUCTURAL_DEBT_COST, TASKS, TASK_REFS, CYCLICITY, CYCLIC_PACKAGES, TYPE_DEPENDENCIES_TO_CUT,
      REFERENCES_TO_REMOVE, ACD, RELATIVE_ACD, NCCD, INSTRUCTIONS, VIOLATING_TYPE_DEPENDENCIES, VIOLATING_TYPES, VIOLATING_REFERENCES, IGNORED_VIOLATONS, UNASSIGNED_TYPES,
      ALL_WARNINGS, CYCLE_WARNINGS, DUPLICATE_WARNINGS, THRESHOLD_WARNINGS, WORKSPACE_WARNINGS, IGNORED_WARNINGS, INTERNAL_PACKAGES, INTERNAL_TYPES, TYPE_DEPENDENCIES, JAVA_FILES);
    final List<Metric> newMetrics = new ArrayList<>();
    newMetrics.addAll(metrics);
    return newMetrics;
  }
}
