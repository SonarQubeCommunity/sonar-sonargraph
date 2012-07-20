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


public final class SonargraphStandaloneMetricNames {

  public static final String ACD = "AverageComponentDependency";
  public static final String RELATIVE_ACD = "RelativeAverageComponentDependency";
  public static final String NCCD = "NormalizedCumulativeComponentDependency";
  public static final String INTERNAL_PACKAGES = "NumberOfInternalNamespaces";
  public static final String INSTRUCTIONS = "NumberOfInstructions";
  public static final String UNASSIGNED_TYPES = "NumberOfNotAssignedTypes";
  public static final String VIOLATING_DEPENDENCIES = "NumberOfViolations";
  public static final String VIOLATING_REFERENCES = "NumberOfViolatingReferences";
  public static final String VIOLATING_TYPES = "NumberOfViolatingTypes";
  public static final String TYPE_DEPENDENCIES = "OverallNumberOfTypeDependencies";
  public static final String JAVA_FILES = "NumberOfSourceFiles";
  public static final String IGNORED_VIOLATIONS = "NumberOfIgnoredViolations";
  public static final String IGNORED_WARNINGS = "NumberOfIgnoredWarnings";
  public static final String TASKS = "NumberOfTasks";
  public static final String ALL_WARNINGS = "NumberOfWarnings";
  public static final String CYCLE_WARNINGS = "NumberOfCyclicWarnings";
  public static final String THRESHOLD_WARNINGS = "NumberOfMetricWarnings";
  public static final String WORKSPACE_WARNINGS = "NumberOfWorkspaceWarnings";
  public static final String DUPLICATE_WARNINGS = "NumberOfDuplicateCodeBlocksWarnings";
  public static final String EROSION_REFS = "StructuralErosionReferenceLevel";
  public static final String EROSION_TYPES = "StructuralErosionTypeLevel";
  public static final String INTERNAL_TYPES = "NumberOfInternalTypes";
  public static final String STUCTURAL_DEBT_INDEX = "StructuralDebtIndex";

  private SonargraphStandaloneMetricNames()
  {}
}
