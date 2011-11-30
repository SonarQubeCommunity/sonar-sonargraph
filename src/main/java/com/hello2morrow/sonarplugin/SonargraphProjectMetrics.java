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


public class SonargraphProjectMetrics {

  public static final String DOMAIN_SONARGRAPH = "Sonargraph";

  
  /* Top-level metrics, directly present in report or individually selected from build unit metrics (highest...) */
  public static final Metric ACD = new Metric.Builder("sonargraph_acd", "ACD", Metric.ValueType.FLOAT)
      .setDescription("Average Component Dependency (John Lakos)").setDirection(Metric.DIRECTION_WORST)
      .setQualitative(true).setDomain(DOMAIN_SONARGRAPH).create();
  
  public static final Metric NCCD = new Metric.Builder("sonargraph_nccd", "NCCD", Metric.ValueType.FLOAT)
      .setDescription("Normalized Cummulative Component Dependency (John Lakos)").setDirection(Metric.DIRECTION_WORST)
      .setQualitative(true).setDomain(DOMAIN_SONARGRAPH).create();

  public static final Metric HIGHEST_ACD = new Metric.Builder("sonargraph_highest_acd", "Highest ACD",
      Metric.ValueType.FLOAT).setDescription("The highest ACD of all build units").setDirection(Metric.DIRECTION_WORST)
      .setQualitative(true).setDomain(DOMAIN_SONARGRAPH).create();

  public static final Metric HIGHEST_NCCD = new Metric.Builder("sonargraph_highest_nccd", "Highest NCCD",
      Metric.ValueType.FLOAT).setDescription("The highest NCCD of all build units")
      .setDirection(Metric.DIRECTION_WORST).setQualitative(true).setDomain(DOMAIN_SONARGRAPH).create();

  public static final Metric BIGGEST_CYCLE_GROUP = new Metric.Builder("sonargraph_biggest_cycle_group",
      "Biggest Cycle Group", Metric.ValueType.INT).setDescription("Number of Packages in Biggest Cycle Group")
      .setDirection(Metric.DIRECTION_WORST).setQualitative(true).setDomain(DOMAIN_SONARGRAPH).create();

  public static final Metric CYCLIC_PACKAGES = new Metric.Builder("sonargraph_cyclic_packages", "Cyclic Packages",
      Metric.ValueType.INT).setDescription("Number of Cyclic Packages").setDirection(Metric.DIRECTION_WORST)
      .setQualitative(true).setDomain(SonargraphProjectMetrics.DOMAIN_SONARGRAPH).create();
  
  public static final Metric CYCLIC_PACKAGES_PERCENT = new Metric.Builder("sonargraph_cyclic_packages_percent",
      "Percentage of Cyclic Packages", Metric.ValueType.PERCENT)
      .setDescription("Percentage of Cyclically Coupled Packages").setDirection(Metric.DIRECTION_WORST)
      .setQualitative(true).setDomain(DOMAIN_SONARGRAPH).create();
  
  public static final Metric RELATIVE_CYCLICITY = new Metric.Builder("sonargraph_relative_cyclicity",
      "Relative Cyclicity", Metric.ValueType.PERCENT).setDescription("Relative Cyclicity on Package Level")
      .setDirection(Metric.DIRECTION_WORST).setQualitative(true).setDomain(DOMAIN_SONARGRAPH).create();

  public static final Metric UNASSIGNED_TYPES_PERCENT = new Metric.Builder("sonargraph_unassigned_types_percent",
      "Percentage of Unassigned Types", Metric.ValueType.PERCENT)
      .setDescription("Percentage of Types not Assigned to any Architectural Artifacts")
      .setDirection(Metric.DIRECTION_WORST).setQualitative(true).setDomain(DOMAIN_SONARGRAPH).create();
  
  public static final Metric VIOLATING_TYPES_PERCENT = new Metric.Builder("sonargraph_violating_types_percent",
      "Percentage of Violating Types", Metric.ValueType.PERCENT)
      .setDescription("Percentage of Types with Outgoing Architecture Violations").setDirection(Metric.DIRECTION_WORST)
      .setQualitative(true).setDomain(DOMAIN_SONARGRAPH).create();

  public static final Metric ARCHITECTURE_VIOLATIONS = new Metric.Builder("sonargraph_architecture_violations",
      "Architecture Violations (references)", Metric.ValueType.INT).setDescription("Number of Violating References")
      .setDirection(Metric.DIRECTION_WORST).setQualitative(true).setDomain(DOMAIN_SONARGRAPH).create();

  public static final Metric CYCLE_WARNINGS = new Metric.Builder("sonargraph_cycle_warnings", "Cycle Warnings",
      Metric.ValueType.INT).setDescription("Number of Cycle Warnings").setDirection(Metric.DIRECTION_WORST)
      .setQualitative(true).setDomain(DOMAIN_SONARGRAPH).create();
  
  public static final Metric ALL_WARNINGS = new Metric.Builder("sonargraph_all_warnings", "All Warnings",
      Metric.ValueType.INT).setDescription("Number of All Warnings").setDirection(Metric.DIRECTION_WORST)
      .setQualitative(true).setDomain(DOMAIN_SONARGRAPH).create();

  public static final Metric WORKSPACE_WARNINGS = new Metric.Builder("sonargraph_workspace_warnings",
      "Workspace Warnings", Metric.ValueType.INT).setDescription("Number of Sonargraph Parser Warnings")
      .setDirection(Metric.DIRECTION_WORST).setQualitative(true).setDomain(DOMAIN_SONARGRAPH).create();
  
  public static final Metric INTERNAL_PACKAGES = new Metric.Builder("sonargraph_packages", "Packages",
      Metric.ValueType.INT).setDescription("Number of Internal Packages").setDirection(Metric.DIRECTION_NONE)
      .setQualitative(false).setDomain(DOMAIN_SONARGRAPH).create();

  public List<Metric> getMetrics() {
    return Arrays.asList(ACD, NCCD, HIGHEST_ACD, HIGHEST_NCCD, BIGGEST_CYCLE_GROUP, CYCLIC_PACKAGES,
        CYCLIC_PACKAGES_PERCENT, RELATIVE_CYCLICITY, INTERNAL_PACKAGES, 
        ALL_WARNINGS, CYCLE_WARNINGS, WORKSPACE_WARNINGS, ARCHITECTURE_VIOLATIONS, 
        UNASSIGNED_TYPES_PERCENT, VIOLATING_TYPES_PERCENT);
  }
}
