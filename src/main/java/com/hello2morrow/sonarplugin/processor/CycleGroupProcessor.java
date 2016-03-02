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
package com.hello2morrow.sonarplugin.processor;

import com.hello2morrow.sonarplugin.foundation.SonarQubeUtilities;
import com.hello2morrow.sonarplugin.foundation.SonargraphPluginBase;
import com.hello2morrow.sonarplugin.foundation.SonargraphUtilities;
import com.hello2morrow.sonarplugin.persistence.PersistenceUtilities;
import com.hello2morrow.sonarplugin.xsd.ReportContext;
import com.hello2morrow.sonarplugin.xsd.XsdAttributeRoot;
import com.hello2morrow.sonarplugin.xsd.XsdCycleGroup;
import com.hello2morrow.sonarplugin.xsd.XsdCycleGroups;
import com.hello2morrow.sonarplugin.xsd.XsdCyclePath;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sonar.api.batch.SensorContext;
import org.sonar.api.batch.fs.InputDir;
import org.sonar.api.batch.fs.InputFile;
import org.sonar.api.batch.fs.InputPath;
import org.sonar.api.batch.rule.ActiveRule;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class CycleGroupProcessor implements IProcessor {

  private static final Logger LOG = LoggerFactory.getLogger(CycleGroupProcessor.class);
  private static final String PHYSICAL_PACKAGE_NAMED_ELEMENT_GROUP = "Physical package";
  private static final String DIRECTORY_NAMED_ELEMENT_GROUP = "Directory";

  private double cyclicity = 0;
  private double biggestCycleGroupSize = 0;
  private double cyclicPackages = 0;
  private String sonargraphBasePath;
  private final SensorContext sensorContext;

  public CycleGroupProcessor(final SensorContext context) {
    this.sensorContext = context;
  }

  public double getCyclicity() {
    return cyclicity;
  }

  public double getBiggestCycleGroupSize() {
    return biggestCycleGroupSize;
  }

  public double getCyclicPackages() {
    return cyclicPackages;
  }

  @Override
  public void process(final ReportContext report, final XsdAttributeRoot buildUnit) {
    final ActiveRule rule = SonarQubeUtilities.findActiveSonargraphRule(sensorContext, SonargraphPluginBase.CYCLE_GROUP_RULE_KEY);
    if (rule == null) {
      LOG.info("Sonargraph cycle group rule not active in current profile");
      return;
    }

    this.sonargraphBasePath = PersistenceUtilities.getSonargraphBasePath(report);

    cyclicity = 0;
    biggestCycleGroupSize = 0;
    cyclicPackages = 0;
    boolean packageNotFound = false;

    final XsdCycleGroups cycleGroups = report.getCycleGroups();

    for (final XsdCycleGroup group : cycleGroups.getCycleGroup()) {
      if (!PersistenceUtilities.getBuildUnitName(group).equals(SonargraphUtilities.getBuildUnitName(buildUnit.getName()))) {
        continue;
      }

      final String namedElementGroup = group.getNamedElementGroup();
      packageNotFound = !createCycleGroupIssue(group, namedElementGroup, rule) || packageNotFound;
    }

    if (packageNotFound) {
      LOG.warn("Issues not created for all packages involved in cycles. "
        + "Check if configuration for Sonargraph system includes the generation of cycle warnings for source files and directories!");
    }
  }

  /**
   * @param rule 
   * @return true if issue is created for all elements, false otherwise
   */
  private boolean createCycleGroupIssue(final XsdCycleGroup group, final String namedElementGroup, final ActiveRule rule) {
    if (PHYSICAL_PACKAGE_NAMED_ELEMENT_GROUP.equals(namedElementGroup)) {
      final int groupSize = group.getCyclePath().size();
      cyclicPackages += groupSize;
      cyclicity += groupSize * groupSize;
      if (groupSize > biggestCycleGroupSize) {
        biggestCycleGroupSize = groupSize;
      }
    } else if (DIRECTORY_NAMED_ELEMENT_GROUP.equals(namedElementGroup) && !handleSrcDirectoryCycleGroup(group, rule)) {
      return false;
    } else if ("Source file".equals(namedElementGroup)) {
      handleSourceFileGroup(group, rule);
    }
    return true;
  }

  private void handleSourceFileGroup(final XsdCycleGroup group, final ActiveRule rule) {
    final List<InputPath> srcFiles = new ArrayList<>();
    srcFiles.addAll(determineCyclicSrcFiles(group));
    for (final InputPath srcFile : srcFiles) {
      addCycleIssue(srcFile, srcFiles, rule);
    }
  }

  private List<InputFile> determineCyclicSrcFiles(final XsdCycleGroup group) {
    final List<InputFile> srcFiles = new ArrayList<>();
    for (final XsdCyclePath pathElement : group.getCyclePath()) {
      final String cyclicFilePathRelative = pathElement.getParent();
      if (cyclicFilePathRelative == null) {
        LOG.error("Failed to determine relative path within system for cycleGroupParent '" + group.getParent() + "' and source file '" + pathElement.getParent() + "'");
        continue;
      }

      final InputPath inputPath = SonarQubeUtilities.getInputPath(sensorContext.fileSystem(), cyclicFilePathRelative);
      if (inputPath == null) {
        LOG.error("Failed to determine path for '" + cyclicFilePathRelative + "'");
      } else {
        assert inputPath.isFile() : "inputFile must be a file";
        srcFiles.add((InputFile) inputPath);
      }
    }

    return srcFiles;
  }

  private boolean handleSrcDirectoryCycleGroup(final XsdCycleGroup group, final ActiveRule rule) {
    final List<InputPath> srcDirectories = determineCyclicSrcDirectories(group);
    if (srcDirectories.isEmpty()) {
      return true;
    }

    boolean issueAddedForAllPackages = true;

    // No source directories are detected for class files
    for (final InputPath jPackage : srcDirectories) {
      issueAddedForAllPackages = addCycleIssue(jPackage, srcDirectories, rule) || issueAddedForAllPackages;
    }
    return issueAddedForAllPackages;
  }

  /**
   * Determines the relative path, since class directories of Sonargraph workspace might be different from Maven class directories 
   * @param group
   * @return
   */
  private List<InputPath> determineCyclicSrcDirectories(final XsdCycleGroup group) {
    final List<InputPath> packages = new ArrayList<>();
    for (final XsdCyclePath pathElement : group.getCyclePath()) {
      final String cyclicPath = pathElement.getParent();
      try {
        final String absolutePath = new File(sonargraphBasePath, cyclicPath).getCanonicalPath().replace('\\', '/');
        final InputPath srcDir = SonarQubeUtilities.getInputPath(sensorContext.fileSystem(), absolutePath, true);
        if (srcDir != null) {
          packages.add(srcDir);
        } else {
          LOG.error("Could not locate directory for '" + cyclicPath + "'.");
        }
      } catch (final IOException e) {
        LOG.error("Failed to determine absolute path for '" + cyclicPath + "'.", e);
      }
    }
    return packages;
  }

  private boolean addCycleIssue(final InputPath resource, final List<InputPath> involvedResources, final ActiveRule rule) {
    assert resource != null : "Parameter 'resource' of method 'addCycleIssue' must not be null";
    assert involvedResources != null && !involvedResources.isEmpty() : "Parameter 'involvedResources' of method 'addCycleIssue' must not be empty";
    assert rule != null : "Parameter 'rule' of method 'addCycleIssue' must not be null";

    final List<InputPath> tempInvolvedResources = new ArrayList<>(involvedResources);
    tempInvolvedResources.remove(resource);
    final StringBuilder builder = new StringBuilder();
    if (resource.isFile()) {
      builder.append("File participates in a cycle group");
    } else {
      builder.append("Package participates in a cycle group");
    }
    boolean first = true;
    for (final InputPath next : tempInvolvedResources) {
      if (first) {
        if (resource.isFile()) {
          builder.append(" with source file(s): ").append(next.relativePath());
        } else {
          builder.append(" with package(s): ").append(next.relativePath());
        }
        first = false;
      } else {
        builder.append(", ").append(next.relativePath());
      }

    }

    if (resource.isFile()) {
      SonarQubeUtilities.saveViolation(sensorContext, (InputFile) resource, rule, null, 1, builder.toString());
    } else {
      SonarQubeUtilities.saveViolation(sensorContext, (InputDir) resource, rule, null, builder.toString());
    }
    // An issue is attached to each of the packages involved in the cycle group.
    // The number of cycle group warnings in the issues drill-down can therefore differ
    // from the number given in Sonargraph Architect dashbox in the components dashboard.

    return true;
  }
}
