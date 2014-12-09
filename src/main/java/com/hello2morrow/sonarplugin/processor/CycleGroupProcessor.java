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

import com.hello2morrow.sonarplugin.foundation.SonargraphPluginBase;
import com.hello2morrow.sonarplugin.foundation.Utilities;
import com.hello2morrow.sonarplugin.persistence.PersistenceUtilities;
import com.hello2morrow.sonarplugin.xsd.ReportContext;
import com.hello2morrow.sonarplugin.xsd.XsdAttributeRoot;
import com.hello2morrow.sonarplugin.xsd.XsdCycleGroup;
import com.hello2morrow.sonarplugin.xsd.XsdCycleGroups;
import com.hello2morrow.sonarplugin.xsd.XsdCyclePath;
import org.jfree.util.Log;
import org.sonar.api.batch.fs.FilePredicates;
import org.sonar.api.batch.fs.FileSystem;
import org.sonar.api.component.ResourcePerspectives;
import org.sonar.api.issue.Issuable;
import org.sonar.api.issue.Issuable.IssueBuilder;
import org.sonar.api.profiles.RulesProfile;
import org.sonar.api.resources.Directory;
import org.sonar.api.resources.Project;
import org.sonar.api.resources.Resource;
import org.sonar.api.rules.ActiveRule;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class CycleGroupProcessor implements IProcessor {

  private double cyclicity = 0;
  private double biggestCycleGroupSize = 0;
  private double cyclicPackages = 0;
  private final ResourcePerspectives perspectives;
  private final Project project;
  private final ActiveRule rule;
  private final FileSystem fileSystem;

  public CycleGroupProcessor(Project project, final RulesProfile rulesProfile, final ResourcePerspectives perspectives, FileSystem fileSystem) {
    this.project = project;
    this.perspectives = perspectives;
    this.rule = rulesProfile.getActiveRule(SonargraphPluginBase.PLUGIN_KEY, SonargraphPluginBase.CYCLE_GROUP_RULE_KEY);
    this.fileSystem = fileSystem;
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
  public void process(ReportContext report, XsdAttributeRoot buildUnit) {
    if (rule == null) {
      return;
    }
    FilePredicates predicates = fileSystem.predicates();
    XsdCycleGroups cycleGroups = report.getCycleGroups();

    for (XsdCycleGroup group : cycleGroups.getCycleGroup()) {
      if (PersistenceUtilities.getBuildUnitName(group).equals(Utilities.getBuildUnitName(buildUnit.getName()))) {
        if ("Physical package".equals(group.getNamedElementGroup())) {
          int groupSize = group.getCyclePath().size();
          cyclicPackages += groupSize;
          cyclicity += groupSize * groupSize;
          if (groupSize > biggestCycleGroupSize) {
            biggestCycleGroupSize = groupSize;
          }

          handlePackageCycleGroup(group, predicates);
        }
      }
    }
  }

  private void handlePackageCycleGroup(XsdCycleGroup group, FilePredicates predicates) {
    List<Resource> packages = determineCyclicPackages(group, predicates);
    for (Resource jPackage : packages) {
      addCycleIssue(jPackage, packages);
    }
  }

  private List<Resource> determineCyclicPackages(XsdCycleGroup group, FilePredicates predicates) {
    List<Resource> packages = new ArrayList<Resource>();
    for (XsdCyclePath pathElement : group.getCyclePath()) {
      final String cyclicPath = pathElement.getParent().replace('.', '/');
      Set<String> srcDirs = new HashSet<String>();

      for (File next : fileSystem.files(predicates.and())) {
        File dir = next.getParentFile();
        try {
          String canonicalPath = dir.getCanonicalPath();
          if (canonicalPath.replace('\\', '/').endsWith(cyclicPath)) {
            srcDirs.add(canonicalPath);
          }
        } catch (IOException e) {
          Log.warn("Could not get canonical path for directory '" + dir.getAbsolutePath() + "'", e);
        }
      }

      if (srcDirs.isEmpty()) {
        Log.warn("Could not locate src directory for package '" + pathElement.getParent() + "'");
        continue;
      }
      for (String path : srcDirs) {
        Directory srcDirectory = org.sonar.api.resources.Directory.fromIOFile(new File(path), project);
        if (srcDirectory != null) {
          packages.add(srcDirectory);
        }
      }
    }
    return packages;
  }

  private void addCycleIssue(Resource jPackage, List<Resource> packages) {
    Issuable issuable = perspectives.as(Issuable.class, jPackage);
    if (issuable == null) {
      // omit cyclic directory that is part of package of java class folder
      return;
    }
    IssueBuilder issueBuilder = issuable.newIssueBuilder();
    issueBuilder.severity(rule.getSeverity().toString()).ruleKey(rule.getRule().ruleKey());

    List<Resource> tempPackages = new ArrayList<Resource>(packages);
    tempPackages.remove(jPackage);
    StringBuilder builder = new StringBuilder();
    builder.append("Package participates in a cycle group");

    boolean first = true;
    for (Resource tPackage : tempPackages) {
      if (first) {
        builder.append(" with package(s): ").append(tPackage.getName());
        first = false;
      } else {
        builder.append(", ").append(tPackage.getName());
      }
    }
    issueBuilder.message(builder.toString());
    // An issue is attached to each of the packages involved in the cycle group.
    // The number of cycle group warnings in the issues drill-down can therefore differ
    // from the number given in Sonargraph Architect dashbox in the components dashboard.
    issuable.addIssue(issueBuilder.build());
  }
}
