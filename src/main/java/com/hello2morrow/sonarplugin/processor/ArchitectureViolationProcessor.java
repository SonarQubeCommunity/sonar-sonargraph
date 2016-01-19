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
import com.hello2morrow.sonarplugin.xsd.XsdArchitectureViolation;
import com.hello2morrow.sonarplugin.xsd.XsdAttributeRoot;
import com.hello2morrow.sonarplugin.xsd.XsdPosition;
import com.hello2morrow.sonarplugin.xsd.XsdTypeRelation;
import com.hello2morrow.sonarplugin.xsd.XsdViolations;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sonar.api.batch.fs.InputComponent;
import org.sonar.api.batch.fs.InputFile;
import org.sonar.api.batch.rule.ActiveRule;
import org.sonar.api.batch.sensor.SensorContext;

public class ArchitectureViolationProcessor implements IProcessor {

  private final SensorContext sensorContext;
  private static final Logger LOG = LoggerFactory.getLogger(ArchitectureViolationProcessor.class);

  public ArchitectureViolationProcessor(SensorContext context) {
    this.sensorContext = context;
  }

  @Override
  public void process(ReportContext report, XsdAttributeRoot buildUnit) {
    LOG.debug("Analysing architecture violation of buildUnit: " + buildUnit.getName());

    ActiveRule rule = SonarQubeUtilities.findActiveSonargraphRule(sensorContext, SonargraphPluginBase.ARCH_RULE_KEY);
    if (rule == null) {
      LOG.info("Sonargraph architecture rule not active in current profile");
      return;
    }

    XsdViolations violations = report.getViolations();
    String uses = "Uses ";
    for (XsdArchitectureViolation violation : violations.getArchitectureViolations()) {

      for (XsdTypeRelation rel : violation.getTypeRelation()) {
        String toType = PersistenceUtilities.getAttribute(rel.getAttribute(), "To");
        String fromBuildUnit = PersistenceUtilities.getAttribute(rel.getAttribute(), "From build unit");

        String dimension = violation.getDimension();
        String message = "";
        if (null != dimension) {
          message = dimension + " architecture violation: ";
        } else {
          message = "Architecture violation: ";
        }
        message = message + uses + toType;
        String explanation = "\nExplanation: " + PersistenceUtilities.getAttribute(rel.getAttribute(), "Explanation");

        fromBuildUnit = SonargraphUtilities.getBuildUnitName(fromBuildUnit);
        if (fromBuildUnit.equals(SonargraphUtilities.getBuildUnitName(buildUnit.getName()))) {
          processPosition(sensorContext, rule, rel, message, explanation);
        }
      }
    }
  }

  private void processPosition(org.sonar.api.batch.sensor.SensorContext context, org.sonar.api.batch.rule.ActiveRule rule, XsdTypeRelation rel, String message, String explanation) {
    for (XsdPosition pos : rel.getPosition()) {
      String relFileName = pos.getFile();
      int line = 0;
      try {
        line = Integer.parseInt(pos.getLine());
      } catch (NumberFormatException ex) {
        LOG.error("Attribute \"line\" of element \"position\" is not a valid integer value: " + pos.getLine() + ". Exception: " + ex.getMessage());
        continue;
      }
      if (relFileName != null && (pos.getType() != null) && (line > 0)) {
        String msg = message + ". Usage type: " + pos.getType() + explanation;
        LOG.debug(msg);
        InputComponent component = SonarQubeUtilities.getInputPath(context.fileSystem(), relFileName);
        if (component != null && component.isFile()) {
          SonarQubeUtilities.saveViolation(context, (InputFile) component, rule, "", Integer.parseInt(pos.getLine()), msg);
        }
      }
    }
  }
}
