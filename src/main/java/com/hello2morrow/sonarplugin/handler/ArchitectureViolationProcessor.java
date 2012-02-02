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
package com.hello2morrow.sonarplugin.handler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sonar.api.batch.SensorContext;
import org.sonar.api.rules.Rule;
import org.sonar.api.rules.RuleFinder;

import com.hello2morrow.sonarplugin.foundation.SonargraphPluginBase;
import com.hello2morrow.sonarplugin.foundation.Utilities;
import com.hello2morrow.sonarplugin.xsd.ReportContext;
import com.hello2morrow.sonarplugin.xsd.XsdArchitectureViolation;
import com.hello2morrow.sonarplugin.xsd.XsdAttributeRoot;
import com.hello2morrow.sonarplugin.xsd.XsdPosition;
import com.hello2morrow.sonarplugin.xsd.XsdTypeRelation;
import com.hello2morrow.sonarplugin.xsd.XsdViolations;

public class ArchitectureViolationProcessor implements IProcessor {

  private RuleFinder ruleFinder;
  private SensorContext context;
  private static final Logger LOG = LoggerFactory.getLogger(ArchitectureViolationProcessor.class);


  public ArchitectureViolationProcessor(final RuleFinder ruleFinder, final SensorContext context) {
    this.ruleFinder = ruleFinder;
    this.context = context;
  }

  public void process(ReportContext report, XsdAttributeRoot buildUnit) {
    LOG.debug("Analysing architecture violation of buildUnit: " + buildUnit.getName());

    Rule rule = ruleFinder.findByKey(SonargraphPluginBase.PLUGIN_KEY, SonargraphPluginBase.ARCH_RULE_KEY);
    if (rule == null) {
      LOG.error("Sonargraph architecture rule not found");
      return;
    }
    XsdViolations violations = report.getViolations();
    String uses = "Uses ";
    for (XsdArchitectureViolation violation : violations.getArchitectureViolations()) {

      for (XsdTypeRelation rel : violation.getTypeRelation()) {
        String toType = Utilities.getAttribute(rel.getAttribute(), "To");
        String bu = Utilities.getAttribute(rel.getAttribute(), "From build unit");

        String dimension = violation.getDimension();
        String message = "";
        if (null != dimension) {
          message = dimension + " architecture violation: ";
        } else {
          message = "Architecture violation: ";
        }
        message = message + uses + toType;
        String explanation = "\nExplanation: " + Utilities.getAttribute(rel.getAttribute(), "Explanation");

        bu = Utilities.getBuildUnitName(bu);
        if (bu.equals(Utilities.getBuildUnitName(buildUnit.getName()))) {
          processPosition(rule, rel, message, explanation);
        }
      }
    }
  }

  private void processPosition(Rule rule, XsdTypeRelation rel, String message, String explanation) {
    for (XsdPosition pos : rel.getPosition()) {
      String relFileName = pos.getFile();
      int line = 0;
      try {
        line = Integer.parseInt(pos.getLine());
      } catch (NumberFormatException ex) {
        LOG.error("Attribute \"line\" of element \"position\" is not a valid integer value: " + pos.getLine()
            + ". Exception: " + ex.getMessage());
        continue;
      }
      if (relFileName != null && (pos.getType() != null) && (line > 0)) {
        String fqName = Utilities.relativeFileNameToFqName(relFileName);
        String msg = message + ". Usage type: " + pos.getType() + explanation;
        LOG.debug(msg);
        Utilities.saveViolation(context, rule, null, fqName, Integer.valueOf(pos.getLine()), msg);
      }
    }
  }

}
