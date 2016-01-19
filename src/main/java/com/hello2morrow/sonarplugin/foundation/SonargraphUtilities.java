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
package com.hello2morrow.sonarplugin.foundation;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sonar.api.rule.Severity;

import java.util.List;
import java.util.Map;

public class SonargraphUtilities {

  public static final String DEFAULT_BUILD_UNIT = "(Default Build Unit)";

  private static final Logger LOG = LoggerFactory.getLogger(SonargraphUtilities.class);

  private static final String UNKNOWN = "<UNKNOWN>";
  private static final String PROJECT_BUILDUNIT_SEPARATOR = "::";

  private SonargraphUtilities() {
    // do not instantiate
  }

  public static String getBuildUnitName(String fqName) {
    if (fqName == null) {
      return UNKNOWN;
    }

    int projectSeparatorPos = fqName.indexOf(PROJECT_BUILDUNIT_SEPARATOR);
    if (projectSeparatorPos == -1) {
      return UNKNOWN;
    }

    String buName = fqName.substring(projectSeparatorPos + 2);

    if (DEFAULT_BUILD_UNIT.equals(buName)) {
      // Compatibility with old SonarJ versions
      buName = fqName.substring(0, projectSeparatorPos);
    }
    return buName;
  }

  /**
   * Creates a new value for the specified metric and saves it to the sensor context.
   *
   * @param sensorContext
   * @param metric
   * @param value
   * @param precision
   * @param flagMissingMetric
   *          indicates if a logging statement should be generated if metric cannot be found
   * @return
   */
  public static Number getBuildUnitMetricValue(Map<String, Number> metrics, String key, boolean flagMissingMetric) {
    Number num = metrics.get(key);

    if (flagMissingMetric && num == null) {
      LOG.error("Cannot find metric <" + key + "> in generated report");
      LOG.error("Make sure you set the prepareForSonar option to true (see documentation).");
      LOG.error("If you used Sonargraph Quality for report generation: " + "Check that your quality model used during snapshot generation contains the required Sonar metrics!");
    }

    if (num == null) {
      return 0.0;
    }

    return num;
  }

  public static String getSourceFilePath(String groupParentPath, String sourceFilePath) {
    int lastIndexOf = sourceFilePath.lastIndexOf('/');
    String dirOfSourceFile = sourceFilePath.substring(0, lastIndexOf);
    if (groupParentPath.endsWith(dirOfSourceFile)) {
      return groupParentPath + sourceFilePath.substring(lastIndexOf);
    }
    return null;
  }

  public static String convertToSeverity(String priority) {
    if (priority == null || priority.trim().length() == 0) {
      return null;
    }

    String prio = priority.trim();
    if (prio.equalsIgnoreCase("HIGH")) {
      return Severity.MAJOR;
    } else if (prio.equalsIgnoreCase("MEDIUM")) {
      return Severity.MINOR;
    } else if (prio.equalsIgnoreCase("LOW")) {
      return Severity.INFO;
    }

    return null;
  }

  public static String generateDuplicateCodeBlockMessage(DuplicateCodeBlock block, List<DuplicateCodeBlock> blocks) {
    final int endLine = block.getBlockLength() + block.getStartLine() - 1;

    final StringBuilder message = new StringBuilder();
    message.append("Line ").append(block.getStartLine()).append(" to ").append(endLine).append(" is a duplicate of\n");
    int toBeDescribed = blocks.size() - 1;
    boolean isFirst = true;

    for (int i = 0; i < blocks.size(); i++) {
      DuplicateCodeBlock duplicate = blocks.get(i);
      if (duplicate == block) {
        continue;
      }
      int remaining = toBeDescribed - i;

      // No connection for first described element.
      if (!isFirst && blocks.size() > 2) {
        if (remaining > 0) {
          // Not last, and not first: enumerate.
          message.append(",\n");
        } else {
          // Last.
          if (toBeDescribed == 2) {
            // Just two parts.
            message.append(" and\n");
          } else {
            // More than two parts: Enumeration.
            message.append(", and\n");
          }
        }
      }
      isFirst = false;
      final int endLineDuplicate = duplicate.getBlockLength() + duplicate.getStartLine() - 1;
      message.append("line " + duplicate.getStartLine() + " to " + endLineDuplicate + " of " + duplicate.getElementName());
    }
    message.append(".");
    return message.toString();
  }
}
