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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sonar.api.batch.SensorContext;
import org.sonar.api.rules.Rule;
import org.sonar.api.rules.RuleFinder;

import com.hello2morrow.sonarplugin.foundation.DuplicateCodeBlock;
import com.hello2morrow.sonarplugin.foundation.SonargraphPluginBase;
import com.hello2morrow.sonarplugin.foundation.Utilities;
import com.hello2morrow.sonarplugin.xsd.ReportContext;
import com.hello2morrow.sonarplugin.xsd.XsdAttributeRoot;
import com.hello2morrow.sonarplugin.xsd.XsdPosition;
import com.hello2morrow.sonarplugin.xsd.XsdWarning;
import com.hello2morrow.sonarplugin.xsd.XsdWarnings;
import com.hello2morrow.sonarplugin.xsd.XsdWarningsByAttribute;
import com.hello2morrow.sonarplugin.xsd.XsdWarningsByAttributeGroup;


/**
 * @author Ingmar
 *
 */
public class WarningProcessor implements IProcessor {
  
  private SensorContext sensorContext;
  private RuleFinder ruleFinder;
  private static final Logger LOG = LoggerFactory.getLogger(WarningProcessor.class);


  public WarningProcessor(final RuleFinder ruleFinder, final SensorContext sensorContext) {
    this.sensorContext = sensorContext;
    this.ruleFinder = ruleFinder;
  }

  public void process(ReportContext report, XsdAttributeRoot buildUnit) {
    LOG.debug("Analysing warnings of buildUnit: " + buildUnit.getName());

    XsdWarnings warnings = report.getWarnings();
    for (XsdWarningsByAttributeGroup warningGroup : warnings.getWarningsByAttributeGroup()) {

      String key = SonargraphPluginBase.getRuleKey(warningGroup.getAttributeGroup());
      if (key == null) {
        continue;
      }
      Rule rule = ruleFinder.findByKey(SonargraphPluginBase.PLUGIN_KEY, key);
      if (rule == null) {
        LOG.error("Sonargraph threshold rule not found");
        continue;
      }
      if ("Duplicate code".equals(warningGroup.getAttributeGroup())) {
        handleDuplicateCodeBlocks(warningGroup, rule);
        continue;
      }
      for (XsdWarningsByAttribute warningByAttribute : warningGroup.getWarningsByAttribute()) {
        String attrName = warningByAttribute.getAttributeName();

        for (XsdWarning warning : warningByAttribute.getWarning()) {
          String msg = attrName + "=" + Utilities.getAttribute(warning.getAttribute(), "Attribute value");
          String bu = Utilities.getAttribute(warning.getAttribute(), "Build unit");

          bu = Utilities.getBuildUnitName(bu);
          if (bu.equals(Utilities.getBuildUnitName(buildUnit.getName()))) {
            processPosition(rule, warning, msg);
          }
        }
      }
    }
  }

  private void processPosition(Rule rule, XsdWarning warning, String msg) {
    if (warning.getPosition().size() > 0) {
      for (XsdPosition pos : warning.getPosition()) {
        String relFileName = pos.getFile();

        if (relFileName != null) {
          String fqName = Utilities.relativeFileNameToFqName(relFileName);
          Utilities.saveViolation(sensorContext, rule, null, fqName, Integer.valueOf(pos.getLine()), msg);
        }
      }
    } else {
      String elemType = Utilities.getAttribute(warning.getAttribute(), "Element type");

      if ("Class file".equals(elemType) || "Source file".equals(elemType)) {
        // Attach a violation at line 1
        String fileName = Utilities.getAttribute(warning.getAttribute(), "Element");
        String fqName = fileName.substring(0, fileName.lastIndexOf('.')).replace('/', '.');

        Utilities.saveViolation(sensorContext, rule, null, fqName, 1, msg);
      }
    }
  }
  
  private void handleDuplicateCodeBlocks(XsdWarningsByAttributeGroup warningGroup, Rule rule) {
    LOG.debug("Analysing duplicate code blocks");

    Map<Integer, List<DuplicateCodeBlock>> duplicateCodeBlocks = new HashMap<Integer, List<DuplicateCodeBlock>>();

    for (XsdWarningsByAttribute warnings : warningGroup.getWarningsByAttribute()) {
      for (XsdWarning warning : warnings.getWarning()) {
        DuplicateCodeBlock block = Utilities.createDuplicateCodeBlock(warning);
        if (null == block) {
          continue;
        }
        if ( !duplicateCodeBlocks.containsKey(block.getBlockId())) {
          duplicateCodeBlocks.put(block.getBlockId(), new ArrayList<DuplicateCodeBlock>());
        }
        duplicateCodeBlocks.get(block.getBlockId()).add(block);
      }
    }

    for (Entry<Integer, List<DuplicateCodeBlock>> entry : duplicateCodeBlocks.entrySet()) {
      for (DuplicateCodeBlock block : entry.getValue()) {
        String message = Utilities.generateDuplicateCodeBlockMessage(block, entry.getValue());
        String fqName = Utilities.relativeFileNameToFqName(block.getElementName());
        Utilities.saveViolation(sensorContext, rule, null, fqName, block.getStartLine(), message);
      }
    }
  }

}
