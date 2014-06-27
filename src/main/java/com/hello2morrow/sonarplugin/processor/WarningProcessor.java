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
/**
 *
 */
package com.hello2morrow.sonarplugin.processor;

import com.hello2morrow.sonarplugin.foundation.DuplicateCodeBlock;
import com.hello2morrow.sonarplugin.foundation.SonargraphPluginBase;
import com.hello2morrow.sonarplugin.foundation.Utilities;
import com.hello2morrow.sonarplugin.persistence.PersistenceUtilities;
import com.hello2morrow.sonarplugin.xsd.ReportContext;
import com.hello2morrow.sonarplugin.xsd.XsdAttributeRoot;
import com.hello2morrow.sonarplugin.xsd.XsdPosition;
import com.hello2morrow.sonarplugin.xsd.XsdWarning;
import com.hello2morrow.sonarplugin.xsd.XsdWarnings;
import com.hello2morrow.sonarplugin.xsd.XsdWarningsByAttribute;
import com.hello2morrow.sonarplugin.xsd.XsdWarningsByAttributeGroup;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sonar.api.batch.fs.FileSystem;
import org.sonar.api.component.ResourcePerspectives;
import org.sonar.api.profiles.RulesProfile;
import org.sonar.api.resources.Project;
import org.sonar.api.rules.ActiveRule;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

/**
 * @author Ingmar
 *
 */
public class WarningProcessor implements IProcessor {

  private final FileSystem fileSystem;
  private final RulesProfile rulesProfile;
  private final ResourcePerspectives resourcePerspectives;
  private final Project project;
  private static final Logger LOG = LoggerFactory.getLogger(WarningProcessor.class);

  public WarningProcessor(final Project project, final RulesProfile rulesProfile, final FileSystem fileSystem, ResourcePerspectives perspectives) {
    this.project = project;
    this.fileSystem = fileSystem;
    this.rulesProfile = rulesProfile;
    this.resourcePerspectives = perspectives;
  }

  @Override
  public void process(ReportContext report, XsdAttributeRoot buildUnit) {
    LOG.debug("Analysing warnings of buildUnit: " + buildUnit.getName());

    XsdWarnings warnings = report.getWarnings();
    for (XsdWarningsByAttributeGroup warningGroup : warnings.getWarningsByAttributeGroup()) {

      String key = SonargraphPluginBase.getRuleKey(warningGroup.getAttributeGroup());
      if (key == null) {
        continue;
      }
      ActiveRule rule = rulesProfile.getActiveRule(SonargraphPluginBase.PLUGIN_KEY, key);
      if (rule == null) {
        LOG.info("Sonargraph threshold not active in current profile");
        continue;
      }
      if ("Duplicate code".equals(warningGroup.getAttributeGroup())) {
        handleDuplicateCodeBlocks(warningGroup, rule, buildUnit);
        continue;
      }
      for (XsdWarningsByAttribute warningByAttribute : warningGroup.getWarningsByAttribute()) {
        String attrName = warningByAttribute.getAttributeName();

        for (XsdWarning warning : warningByAttribute.getWarning()) {
          String msg = attrName + "=" + PersistenceUtilities.getAttribute(warning.getAttribute(), "Attribute value");
          String bu = PersistenceUtilities.getAttribute(warning.getAttribute(), "Build unit");

          bu = Utilities.getBuildUnitName(bu);
          if (bu.equals(Utilities.getBuildUnitName(buildUnit.getName()))) {
            processPosition(rule, warning, msg);
          }
        }
      }
    }
  }

  private void processPosition(ActiveRule rule, XsdWarning warning, String msg) {
    if (warning.getPosition().size() > 0) {
      for (XsdPosition pos : warning.getPosition()) {
        String relFileName = pos.getFile();

        if (relFileName != null) {
          Utilities.saveViolation(project, fileSystem, resourcePerspectives, rule, relFileName, Integer.valueOf(pos.getLine()), msg);
        }
      }
    } else {
      String elemType = PersistenceUtilities.getAttribute(warning.getAttribute(), "Element type");

      if ("Class file".equals(elemType) || "Source file".equals(elemType)) {
        // Attach a violation at line 1
        String fileName = PersistenceUtilities.getAttribute(warning.getAttribute(), "Element");
        String fqName = fileName.substring(0, fileName.lastIndexOf('.')).replace('/', '.');

        Utilities.saveViolation(project, fileSystem, resourcePerspectives, rule, fqName, 1, msg);
      }
    }
  }

  private void handleDuplicateCodeBlocks(XsdWarningsByAttributeGroup warningGroup, ActiveRule rule,
    XsdAttributeRoot buildUnit) {
    LOG.debug("Analysing duplicate code blocks");

    Map<Integer, List<DuplicateCodeBlock>> duplicateCodeBlocks = new HashMap<Integer, List<DuplicateCodeBlock>>();

    for (XsdWarningsByAttribute warnings : warningGroup.getWarningsByAttribute()) {
      for (XsdWarning warning : warnings.getWarning()) {
        DuplicateCodeBlock block = PersistenceUtilities.createDuplicateCodeBlock(warning);
        if (null == block) {
          continue;
        }
        if (!duplicateCodeBlocks.containsKey(block.getBlockId())) {
          duplicateCodeBlocks.put(block.getBlockId(), new ArrayList<DuplicateCodeBlock>());
        }
        duplicateCodeBlocks.get(block.getBlockId()).add(block);
      }
    }

    for (Entry<Integer, List<DuplicateCodeBlock>> entry : duplicateCodeBlocks.entrySet()) {
      for (DuplicateCodeBlock block : entry.getValue()) {
        String message = Utilities.generateDuplicateCodeBlockMessage(block, entry.getValue());
        String fqName = block.getElementName();
        if (Utilities.getBuildUnitName(buildUnit.getName())
          .equals(Utilities.getBuildUnitName(block.getBuildUnitName()))) {
          Utilities.saveViolation(project, fileSystem, this.resourcePerspectives, rule, fqName, block.getStartLine(), message);
        }
      }
    }
  }

}
