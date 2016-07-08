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

import com.hello2morrow.sonarplugin.foundation.DuplicateCodeBlock;
import com.hello2morrow.sonarplugin.foundation.SonarQubeUtilities;
import com.hello2morrow.sonarplugin.foundation.SonargraphPluginBase;
import com.hello2morrow.sonarplugin.foundation.SonargraphUtilities;
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
import org.sonar.api.batch.SensorContext;
import org.sonar.api.batch.fs.InputDir;
import org.sonar.api.batch.fs.InputFile;
import org.sonar.api.batch.fs.InputPath;
import org.sonar.api.batch.rule.ActiveRule;

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

  private static final Logger LOG = LoggerFactory.getLogger(WarningProcessor.class);
  private final SensorContext sensorContext;

  public WarningProcessor(final SensorContext sensorContext) {
    this.sensorContext = sensorContext;
  }

  @Override
  public void process(final ReportContext report, final XsdAttributeRoot processedBuildUnit) {
    LOG.debug("Analysing warnings of buildUnit: " + processedBuildUnit.getName());

    final XsdWarnings warnings = report.getWarnings();
    for (final XsdWarningsByAttributeGroup warningGroup : warnings.getWarningsByAttributeGroup()) {

      final String key = SonargraphPluginBase.getRuleKey(warningGroup.getAttributeGroup());
      if (key != null) {
        final ActiveRule rule = SonarQubeUtilities.findActiveSonargraphRule(sensorContext, key);
        if (rule == null) {
          LOG.info("Sonargraph rule '" + key + "' not active in current profile");
        } else if ("Duplicate code".equals(warningGroup.getAttributeGroup())) {
          handleDuplicateCodeBlocks(warningGroup, rule, processedBuildUnit);
        } else {
          processWarning(processedBuildUnit, warningGroup, rule);
        }
      }
    }
  }

  private void processWarning(final XsdAttributeRoot processedBuildUnit, final XsdWarningsByAttributeGroup warningGroup, final ActiveRule rule) {
    for (final XsdWarningsByAttribute warningByAttribute : warningGroup.getWarningsByAttribute()) {
      final String attrName = warningByAttribute.getAttributeName();
      for (final XsdWarning warning : warningByAttribute.getWarning()) {
        final String msg = attrName + "=" + PersistenceUtilities.getAttribute(warning.getAttribute(), "Attribute value");
        final String warningBuildUnit = SonargraphUtilities.getBuildUnitName(PersistenceUtilities.getAttribute(warning.getAttribute(), "Build unit"));
        if (warningBuildUnit.equals(SonargraphUtilities.getBuildUnitName(processedBuildUnit.getName()))) {
          processPosition(rule, warning, msg);
        }
      }
    }
  }

  private void processPosition(final ActiveRule rule, final XsdWarning warning, final String msg) {
    if (!warning.getPosition().isEmpty()) {
      for (final XsdPosition pos : warning.getPosition()) {
        saveViolation(rule, msg, pos);
      }
    } else {
      final String elemType = PersistenceUtilities.getAttribute(warning.getAttribute(), "Element type");

      if ("Class file".equals(elemType) || "Source file".equals(elemType)) {
        // Attach a violation at line 1
        final String fileName = PersistenceUtilities.getAttribute(warning.getAttribute(), "Element");
        final String fqName = fileName.substring(0, fileName.lastIndexOf('.')).replace('/', '.');
        final InputPath path = SonarQubeUtilities.getInputPath(sensorContext.fileSystem(), fqName);
        if (path != null) {
          SonarQubeUtilities.saveViolation(sensorContext, (InputFile) path, rule, null, 1, msg);
        } else {
          LOG.warn("Failed to locate file '" + fqName + "'");
        }
      }
    }
  }

  private void saveViolation(final ActiveRule rule, final String msg, final XsdPosition pos) {
    final String relFileName = pos.getFile();
    if (relFileName != null) {
      final InputPath path = SonarQubeUtilities.getInputPath(sensorContext.fileSystem(), relFileName);
      if (path != null) {
        saveViolationToPath(rule, msg, pos, path);
        LOG.debug("Saved warning issue [rule=" + rule.internalKey() + ", path=" + relFileName + ", description=" + msg);
      } else {
        LOG.warn("Failed to locate file '" + relFileName + "'");
      }
    }
  }

  private void saveViolationToPath(final ActiveRule rule, final String msg, final XsdPosition pos, final InputPath path) {
    if (path.isFile()) {
      final int line = Integer.parseInt(pos.getLine());
      SonarQubeUtilities.saveViolation(sensorContext, (InputFile) path, rule, null, line > 0 ? line : 1, msg);
    } else {
      SonarQubeUtilities.saveViolation(sensorContext, (InputDir) path, rule, null, msg);
    }
  }

  private void handleDuplicateCodeBlocks(final XsdWarningsByAttributeGroup warningGroup, final ActiveRule rule, final XsdAttributeRoot processedBuildUnit) {
    LOG.debug("Analysing duplicate code blocks");

    final Map<Integer, List<DuplicateCodeBlock>> duplicateCodeBlocks = new HashMap<>();

    for (final XsdWarningsByAttribute warnings : warningGroup.getWarningsByAttribute()) {
      for (final XsdWarning warning : warnings.getWarning()) {
        final DuplicateCodeBlock block = PersistenceUtilities.createDuplicateCodeBlock(warning);
        if (null == block) {
          continue;
        }
        if (!duplicateCodeBlocks.containsKey(block.getBlockId())) {
          duplicateCodeBlocks.put(block.getBlockId(), new ArrayList<DuplicateCodeBlock>());
        }
        duplicateCodeBlocks.get(block.getBlockId()).add(block);
      }
    }

    for (final Entry<Integer, List<DuplicateCodeBlock>> entry : duplicateCodeBlocks.entrySet()) {
      for (final DuplicateCodeBlock block : entry.getValue()) {
        saveDuplicateBlockIssue(rule, processedBuildUnit, entry, block);
      }
    }
  }

  private void saveDuplicateBlockIssue(final ActiveRule rule, final XsdAttributeRoot processedBuildUnit, final Entry<Integer, List<DuplicateCodeBlock>> entry,
    final DuplicateCodeBlock block) {
    final List<DuplicateCodeBlock> otherBlocks = new ArrayList<>(entry.getValue());
    otherBlocks.remove(block);
    final String message = SonargraphUtilities.generateDuplicateCodeBlockMessage(block, otherBlocks);
    final String fqName = block.getElementName();
    if (SonargraphUtilities.getBuildUnitName(processedBuildUnit.getName()).equals(SonargraphUtilities.getBuildUnitName(block.getBuildUnitName()))) {
      final InputPath path = SonarQubeUtilities.getInputPath(sensorContext.fileSystem(), fqName);
      if (path != null) {
        SonarQubeUtilities.saveViolation(sensorContext, (InputFile) path, rule, null, block.getStartLine(), message);
        LOG.debug("Saved duplicate issue [rule=" + rule.internalKey() + ", path=" + path.relativePath() + ", description=" + message);
      } else {
        LOG.warn("Failed to locate file '" + fqName + "'");
      }
    }
  }
}
