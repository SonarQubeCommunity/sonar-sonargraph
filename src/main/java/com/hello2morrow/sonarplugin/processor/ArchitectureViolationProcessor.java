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
import org.sonar.api.batch.SensorContext;
import org.sonar.api.batch.fs.InputComponent;
import org.sonar.api.batch.fs.InputFile;
import org.sonar.api.batch.rule.ActiveRule;

public class ArchitectureViolationProcessor implements IProcessor {

  private final SensorContext sensorContext;
  private static final Logger LOG = LoggerFactory.getLogger(ArchitectureViolationProcessor.class);

  public ArchitectureViolationProcessor(final SensorContext context) {
    this.sensorContext = context;
  }

  @Override
  public void process(final ReportContext report, final XsdAttributeRoot buildUnit) {
    LOG.debug("Analysing architecture violation of buildUnit: " + buildUnit.getName());

    final ActiveRule rule = SonarQubeUtilities.findActiveSonargraphRule(sensorContext, SonargraphPluginBase.ARCH_RULE_KEY);
    if (rule == null) {
      LOG.info("Sonargraph architecture rule not active in current profile");
      return;
    }

    final XsdViolations violations = report.getViolations();
    final String uses = "Uses ";
    for (final XsdArchitectureViolation violation : violations.getArchitectureViolations()) {

      for (final XsdTypeRelation rel : violation.getTypeRelation()) {
        final String toType = PersistenceUtilities.getAttribute(rel.getAttribute(), "To");
        String fromBuildUnit = PersistenceUtilities.getAttribute(rel.getAttribute(), "From build unit");

        final String dimension = violation.getDimension();
        String message;
        if (null != dimension) {
          message = dimension + " architecture violation: ";
        } else {
          message = "Architecture violation: ";
        }
        message = message + uses + toType;
        final String explanation = "\nExplanation: " + PersistenceUtilities.getAttribute(rel.getAttribute(), "Explanation");

        fromBuildUnit = SonargraphUtilities.getBuildUnitName(fromBuildUnit);
        if (fromBuildUnit.equals(SonargraphUtilities.getBuildUnitName(buildUnit.getName()))) {
          processPosition(sensorContext, rule, rel, message, explanation);
        }
      }
    }
  }

  private static void processPosition(final SensorContext context, final org.sonar.api.batch.rule.ActiveRule rule, final XsdTypeRelation rel, final String message,
    final String explanation) {
    for (final XsdPosition pos : rel.getPosition()) {
      final String relFileName = pos.getFile();
      int line = 0;
      try {
        line = Integer.parseInt(pos.getLine());
      } catch (final NumberFormatException ex) {
        LOG.error("Attribute \"line\" of element \"position\" is not a valid integer value: " + pos.getLine() + ". Exception: " + ex.getMessage());
        continue;
      }
      if (relFileName != null && (pos.getType() != null) && (line > 0)) {
        final String msg = message + ". Usage type: " + pos.getType() + explanation;
        LOG.debug(msg);
        final InputComponent component = SonarQubeUtilities.getInputPath(context.fileSystem(), relFileName);
        if (component != null && component.isFile()) {
          SonarQubeUtilities.saveViolation(context, (InputFile) component, rule, null, Integer.parseInt(pos.getLine()), msg);
        }
      }
    }
  }
}
