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
import org.sonar.api.batch.fs.FileSystem;
import org.sonar.api.component.ResourcePerspectives;
import org.sonar.api.issue.Issuable;
import org.sonar.api.issue.Issuable.IssueBuilder;
import org.sonar.api.profiles.RulesProfile;
import org.sonar.api.resources.Directory;
import org.sonar.api.resources.Project;
import org.sonar.api.resources.Resource;
import org.sonar.api.rules.ActiveRule;

import java.util.ArrayList;
import java.util.List;

public class CycleGroupProcessor implements IProcessor {

  private final FileSystem fileSystem;
  private double cyclicity = 0;
  private double biggestCycleGroupSize = 0;
  private double cyclicPackages = 0;
  private final ResourcePerspectives perspectives;
  private final Project project;
  private final ActiveRule rule;

  public CycleGroupProcessor(Project project, final RulesProfile rulesProfile, final FileSystem fileSystem, final ResourcePerspectives perspectives) {
    this.project = project;
    this.fileSystem = fileSystem;
    this.perspectives = perspectives;
    this.rule = rulesProfile.getActiveRule(SonargraphPluginBase.PLUGIN_KEY, SonargraphPluginBase.CYCLE_GROUP_RULE_KEY);
  }

  @Override
  public void process(ReportContext report, XsdAttributeRoot buildUnit) {
    if (rule == null) {
      return;
    }

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
        } else if ("Directory".equals(group.getNamedElementGroup())) {
          handlePackageCycleGroup(group);
        }
      }
    }
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

  private void handlePackageCycleGroup(XsdCycleGroup group) {
    List<Resource> packages = new ArrayList<Resource>();
    for (XsdCyclePath pathElement : group.getCyclePath()) {
      String projectPath = project.path() + Directory.SEPARATOR;
      if (pathElement.getParent().startsWith(projectPath)) {
        String fqName = pathElement.getParent().substring(projectPath.length());
        Resource javaPackage = Utilities.getResource(project, fileSystem, fqName);
        if (javaPackage != null) {
          packages.add(javaPackage);
        }
      }
    }

    for (Resource jPackage : packages) {
      Issuable issuable = perspectives.as(Issuable.class, jPackage);
      if (issuable == null) {
        continue;
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
      issuable.addIssue(issueBuilder.build());
    }
  }

}
