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

  public WarningProcessor(SensorContext sensorContext) {
    this.sensorContext = sensorContext;
  }

  @Override
  public void process(ReportContext report, XsdAttributeRoot processedBuildUnit) {
    LOG.debug("Analysing warnings of buildUnit: " + processedBuildUnit.getName());

    XsdWarnings warnings = report.getWarnings();
    for (XsdWarningsByAttributeGroup warningGroup : warnings.getWarningsByAttributeGroup()) {

      String key = SonargraphPluginBase.getRuleKey(warningGroup.getAttributeGroup());
      if (key != null) {
        ActiveRule rule = SonarQubeUtilities.findActiveSonargraphRule(sensorContext, key);
        if (rule == null) {
          LOG.info("Sonargraph threshold not active in current profile");
        } else if ("Duplicate code".equals(warningGroup.getAttributeGroup())) {
          handleDuplicateCodeBlocks(warningGroup, rule, processedBuildUnit);
        } else {
          for (XsdWarningsByAttribute warningByAttribute : warningGroup.getWarningsByAttribute()) {
            String attrName = warningByAttribute.getAttributeName();
            for (XsdWarning warning : warningByAttribute.getWarning()) {
              final String msg = attrName + "=" + PersistenceUtilities.getAttribute(warning.getAttribute(), "Attribute value");
              final String warningBuildUnit = SonargraphUtilities.getBuildUnitName(PersistenceUtilities.getAttribute(warning.getAttribute(), "Build unit"));
              if (warningBuildUnit.equals(SonargraphUtilities.getBuildUnitName(processedBuildUnit.getName()))) {
                processPosition(rule, warning, msg);
              }
            }
          }
        }
      }
    }
  }

  private void processPosition(ActiveRule rule, XsdWarning warning, String msg) {
    if (!warning.getPosition().isEmpty()) {
      for (XsdPosition pos : warning.getPosition()) {
        String relFileName = pos.getFile();
        if (relFileName != null) {
          InputPath path = SonarQubeUtilities.getInputPath(sensorContext.fileSystem(), relFileName);
          if (path != null) {
            if (path.isFile()) {
              SonarQubeUtilities.saveViolation(sensorContext, (InputFile) path, rule, null, Integer.parseInt(pos.getLine()), msg);
            } else {
              SonarQubeUtilities.saveViolation(sensorContext, (InputDir) path, rule, null, msg);
            }
          }
        }
      }
    } else {
      String elemType = PersistenceUtilities.getAttribute(warning.getAttribute(), "Element type");

      if ("Class file".equals(elemType) || "Source file".equals(elemType)) {
        // Attach a violation at line 1
        String fileName = PersistenceUtilities.getAttribute(warning.getAttribute(), "Element");
        String fqName = fileName.substring(0, fileName.lastIndexOf('.')).replace('/', '.');
        InputPath path = SonarQubeUtilities.getInputPath(sensorContext.fileSystem(), fqName);
        if (path != null) {
          SonarQubeUtilities.saveViolation(sensorContext, (InputFile) path, rule, null, 1, msg);
        }
      }
    }
  }

  private void handleDuplicateCodeBlocks(XsdWarningsByAttributeGroup warningGroup, ActiveRule rule, XsdAttributeRoot processedBuildUnit) {
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
        String message = SonargraphUtilities.generateDuplicateCodeBlockMessage(block, entry.getValue());
        String fqName = block.getElementName();
        if (SonargraphUtilities.getBuildUnitName(processedBuildUnit.getName()).equals(SonargraphUtilities.getBuildUnitName(block.getBuildUnitName()))) {
          InputPath path = SonarQubeUtilities.getInputPath(sensorContext.fileSystem(), fqName);
          if (path != null) {
            SonarQubeUtilities.saveViolation(sensorContext, (InputFile) path, rule, null, block.getStartLine(), message);
          }
        }
      }
    }
  }
}
