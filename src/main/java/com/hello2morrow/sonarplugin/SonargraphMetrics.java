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

import org.sonar.api.measures.Metrics;
import org.sonar.api.measures.Metric;

import java.util.List;
import java.util.Arrays;

public final class SonargraphMetrics implements Metrics {
	private static final String DOMAIN_SONARGRAPH = "Sonargraph";

	public static final Metric ACD = new Metric("sonargraph_acd", "ACD",
			"Average Component Dependency (John Lakos)",
			Metric.ValueType.FLOAT, Metric.DIRECTION_WORST, true,
			DOMAIN_SONARGRAPH);

	public static final Metric NCCD = new Metric("sonargraph_nccd", "NCCD",
			"Normalized Cummulative Component Dependency (John Lakos)",
			Metric.ValueType.FLOAT, Metric.DIRECTION_WORST, true,
			DOMAIN_SONARGRAPH);

	public static final Metric HIGHEST_ACD = new Metric(
			"sonargraph_highest_acd", "Highest ACD",
			"The highest ACD of all build units", Metric.ValueType.FLOAT,
			Metric.DIRECTION_WORST, true, DOMAIN_SONARGRAPH);

	public static final Metric HIGHEST_NCCD = new Metric(
			"sonargraph_highest_nccd", "Highest NCCD",
			"The highest NCCD of all build units", Metric.ValueType.FLOAT,
			Metric.DIRECTION_WORST, true, DOMAIN_SONARGRAPH);

	public static final Metric CYCLICITY = new Metric("sonargraph_cyclicity",
			"Cyclicity", "Cyclicity of Project on Package Level",
			Metric.ValueType.INT, Metric.DIRECTION_WORST, true,
			DOMAIN_SONARGRAPH);

	public static final Metric BIGGEST_CYCLE_GROUP = new Metric(
			"sonargraph_biggest_cycle_group", "Biggest Cycle Group",
			"Number of Packages in Biggest Cycle Group", Metric.ValueType.INT,
			Metric.DIRECTION_WORST, true, DOMAIN_SONARGRAPH);

	public static final Metric CYCLIC_PACKAGES = new Metric(
			"sonargraph_cyclic_packages", "Cyclic Packages",
			"Number of Cyclic Packages", Metric.ValueType.INT,
			Metric.DIRECTION_WORST, true, DOMAIN_SONARGRAPH);

	public static final Metric CYCLIC_PACKAGES_PERCENT = new Metric(
			"sonargraph_cyclic_packages_percent",
			"Percentage of Cyclic Packages",
			"Percentage of Cyclically Coupled Packages",
			Metric.ValueType.PERCENT, Metric.DIRECTION_WORST, true,
			DOMAIN_SONARGRAPH);

	public static final Metric RELATIVE_CYCLICITY = new Metric(
			"sonargraph_relative_cyclicity", "Relative Cyclicity",
			"Relative Cyclicity on Package Level", Metric.ValueType.PERCENT,
			Metric.DIRECTION_WORST, true, DOMAIN_SONARGRAPH);

	public static final Metric INTERNAL_PACKAGES = new Metric(
			"sonargraph_packages", "Packages", "Number of Internal Packages",
			Metric.ValueType.INT, Metric.DIRECTION_NONE, false,
			DOMAIN_SONARGRAPH);

	public static final Metric INSTRUCTIONS = new Metric(
			"sonargraph_instructions", "Byte Code Instructions",
			"Number of Byte Code Instructions", Metric.ValueType.INT,
			Metric.DIRECTION_NONE, false, DOMAIN_SONARGRAPH);

	public static final Metric CYCLE_GROUP_SIZE = new Metric(
			"sonargraph_cycle_group_size", "Cycle Group Size",
			"Number of Packages in Same Cycle Group", Metric.ValueType.INT,
			Metric.DIRECTION_WORST, true, DOMAIN_SONARGRAPH);

	public static final Metric UNASSIGNED_TYPES = new Metric(
			"sonargraph_unassigned_types", "Unassigned Types",
			"Number of Types not Assigned to any Architectural Artifacts",
			Metric.ValueType.INT, Metric.DIRECTION_WORST, true,
			DOMAIN_SONARGRAPH);

	public static final Metric UNASSIGNED_TYPES_PERCENT = new Metric(
			"sonargraph_unassigned_types_percent",
			"Percentage of Unassigned Types",
			"Percentage of Types not Assigned to any Architectural Artifacts",
			Metric.ValueType.PERCENT, Metric.DIRECTION_WORST, true,
			DOMAIN_SONARGRAPH);

	public static final Metric EROSION_REFS = new Metric(
			"sonargraph_erosion_ref", "Structural Erosion - reference level",
			"Number of References to Cut to Achive Zero Package Cyclicity",
			Metric.ValueType.INT, Metric.DIRECTION_WORST, true,
			DOMAIN_SONARGRAPH);

	public static final Metric EROSION_TYPES = new Metric(
			"sonargraph_erosion_types",
			"Structural Erosion - type level",
			"Number of Type Dependencies to Cut to Achive Zero Package Cyclicity",
			Metric.ValueType.INT, Metric.DIRECTION_WORST, true,
			DOMAIN_SONARGRAPH);

	public static final Metric EROSION_COST = new Metric(
			"sonargraph_erosion_cost", "Structural Debt Cost",
			"Estimated Cost to Repair Structural Erosion",
			Metric.ValueType.INT, Metric.DIRECTION_WORST, true,
			DOMAIN_SONARGRAPH);

	public static final Metric EROSION_INDEX = new Metric(
			"sonargraph_erosion_index", "Structural Debt Index",
			"Structural Erosion Index", Metric.ValueType.INT,
			Metric.DIRECTION_WORST, true, DOMAIN_SONARGRAPH);

	public static final Metric VIOLATING_TYPES = new Metric(
			"sonargraph_violating_types", "Violating Types",
			"Number of Types with Outgoing Architecture Violations",
			Metric.ValueType.INT, Metric.DIRECTION_WORST, true,
			DOMAIN_SONARGRAPH);

	public static final Metric VIOLATING_TYPES_PERCENT = new Metric(
			"sonargraph_violating_types_percent",
			"Percentage of Violating Types",
			"Percentage of Types with Outgoing Architecture Violations",
			Metric.ValueType.PERCENT, Metric.DIRECTION_WORST, true,
			DOMAIN_SONARGRAPH);

	public static final Metric INTERNAL_TYPES = new Metric(
			"sonargraph_internal_types", "Internal Types",
			"Number of Internal Types", Metric.ValueType.INT,
			Metric.DIRECTION_NONE, false, DOMAIN_SONARGRAPH);

	public static final Metric VIOLATING_DEPENDENCIES = new Metric(
			"sonargraph_violating_dependencies", "Violating Dependencies",
			"Number of Violating Type Dependencies", Metric.ValueType.INT,
			Metric.DIRECTION_WORST, true, DOMAIN_SONARGRAPH);

	public static final Metric ARCHITECTURE_VIOLATIONS = new Metric(
			"sonargraph_architecture_violations",
			"Architecture Violations (references)",
			"Number of Violating References", Metric.ValueType.INT,
			Metric.DIRECTION_WORST, true, DOMAIN_SONARGRAPH);

	public static final Metric TYPE_DEPENDENCIES = new Metric(
			"sonargraph_type_dependencies", "Type Dependencies",
			"Overall Number of Type Dependencies", Metric.ValueType.INT,
			Metric.DIRECTION_NONE, false, DOMAIN_SONARGRAPH);

	public static final Metric JAVA_FILES = new Metric("sonargraph_java_files",
			"Java Files", "Number of Java Source Files", Metric.ValueType.INT,
			Metric.DIRECTION_NONE, false, DOMAIN_SONARGRAPH);

	public static final Metric TASKS = new Metric("sonargraph_tasks",
			"Open Tasks", "Number of Open Tasks", Metric.ValueType.INT,
			Metric.DIRECTION_WORST, true, DOMAIN_SONARGRAPH);

	public static final Metric TASK_REFS = new Metric("sonargraph_task_refs",
			"Open Tasks - Lines to Change",
			"Number of Lines Associated with Open Tasks", Metric.ValueType.INT,
			Metric.DIRECTION_WORST, true, DOMAIN_SONARGRAPH);

	public static final Metric ALL_WARNINGS = new Metric(
			"sonargraph_all_warnings", "All Warnings",
			"Number of All Warnings", Metric.ValueType.INT,
			Metric.DIRECTION_WORST, true, DOMAIN_SONARGRAPH);

	public static final Metric CYCLE_WARNINGS = new Metric(
			"sonargraph_cycle_warnings", "Cycle Warnings",
			"Number of Cycle Warnings", Metric.ValueType.INT,
			Metric.DIRECTION_WORST, true, DOMAIN_SONARGRAPH);

	public static final Metric THRESHOLD_WARNINGS = new Metric(
			"sonargraph_threshold_warnings", "Threshold Warnings",
			"Number of Threshold Violations", Metric.ValueType.INT,
			Metric.DIRECTION_WORST, true, DOMAIN_SONARGRAPH);

	public static final Metric DUPLICATE_WARNINGS = new Metric(
			"sonargraph_duplicate_warnings", "Duplicate Code Block Warnings",
			"Number of Duplicate Code Blocks", Metric.ValueType.INT,
			Metric.DIRECTION_WORST, true, DOMAIN_SONARGRAPH);

	public static final Metric WORKSPACE_WARNINGS = new Metric(
			"sonargraph_workspace_warnings", "Workspace Warnings",
			"Number of Sonargraph Parser Warnings", Metric.ValueType.INT,
			Metric.DIRECTION_WORST, true, DOMAIN_SONARGRAPH);

	public static final Metric IGNORED_WARNINGS = new Metric(
			"sonargraph_ignored_warnings", "Ignored Warnings",
			"Number of Ignord Warnings and Threshold Violations",
			Metric.ValueType.INT, Metric.DIRECTION_NONE, false,
			DOMAIN_SONARGRAPH);

	public static final Metric IGNORED_VIOLATONS = new Metric(
			"sonargraph_ignored_violations", "Ignored Violations",
			"Number of Ignored Type Dependency Violatins",
			Metric.ValueType.INT, Metric.DIRECTION_NONE, false,
			DOMAIN_SONARGRAPH);

	public List<Metric> getMetrics() {
		return Arrays.asList(ACD, NCCD, HIGHEST_ACD, HIGHEST_NCCD, CYCLICITY,
				BIGGEST_CYCLE_GROUP, CYCLIC_PACKAGES, CYCLIC_PACKAGES_PERCENT,
				RELATIVE_CYCLICITY, INTERNAL_PACKAGES, INSTRUCTIONS,
				CYCLE_GROUP_SIZE, UNASSIGNED_TYPES, VIOLATING_TYPES,
				VIOLATING_DEPENDENCIES, TYPE_DEPENDENCIES, JAVA_FILES, TASKS,
				EROSION_INDEX, ALL_WARNINGS, CYCLE_WARNINGS,
				THRESHOLD_WARNINGS, WORKSPACE_WARNINGS, DUPLICATE_WARNINGS,
				IGNORED_WARNINGS, IGNORED_VIOLATONS, ARCHITECTURE_VIOLATIONS,
				EROSION_REFS, EROSION_TYPES, EROSION_COST, INTERNAL_TYPES,
				TASK_REFS, UNASSIGNED_TYPES_PERCENT, VIOLATING_TYPES_PERCENT);
	}
}
