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
package com.hello2morrow.sonarplugin.persistence;

import com.hello2morrow.sonarplugin.foundation.DuplicateCodeBlock;
import com.hello2morrow.sonarplugin.foundation.SonargraphPluginBase;
import com.hello2morrow.sonarplugin.foundation.SonargraphUtilities;
import com.hello2morrow.sonarplugin.xsd.ReportContext;
import com.hello2morrow.sonarplugin.xsd.XsdAttribute;
import com.hello2morrow.sonarplugin.xsd.XsdAttributeCategory;
import com.hello2morrow.sonarplugin.xsd.XsdAttributeRoot;
import com.hello2morrow.sonarplugin.xsd.XsdCycleGroup;
import com.hello2morrow.sonarplugin.xsd.XsdWarning;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.ParseException;
import java.util.List;
import java.util.Map;

public class PersistenceUtilities {

  private static final String DEFAULT_PROJECT_NAME = "My Project";
  private static final String BASE_PATH_ATTRIBUTE = "Base Path";
  private static final Logger LOG = LoggerFactory.getLogger(PersistenceUtilities.class);

  private PersistenceUtilities() {
  }

  public static void addAttributeToList(final List<XsdAttribute> attributeList, final String name, final String value) {
    final XsdAttribute attribute = new XsdAttribute();
    attribute.setName(name);
    attribute.setValue(value);
    attributeList.add(attribute);
  }

  public static DuplicateCodeBlock createDuplicateCodeBlock(final XsdWarning warning) {
    final DuplicateCodeBlock block = new DuplicateCodeBlock();
    final String attribute = PersistenceUtilities.getAttribute(warning.getAttribute(), SonargraphUtilities.BLOCK_ID);
    if (null == attribute) {
      LOG.error("Duplicate code block warning does not contain the required attribute \"Block id\"");
      return null;
    }
    block.setBlockId(Integer.parseInt(attribute));
    block.setProjectName(PersistenceUtilities.getAttribute(warning.getAttribute(), SonargraphUtilities.PROJECT));
    block.setBuildUnitName(PersistenceUtilities.getAttribute(warning.getAttribute(), SonargraphUtilities.BUILD_UNIT));
    block.setElementType(PersistenceUtilities.getAttribute(warning.getAttribute(), SonargraphUtilities.ELEMENT_TYPE));

    final String blockLength = PersistenceUtilities.getAttribute(warning.getAttribute(), SonargraphUtilities.ATTRIBUTE_VALUE);
    final int pos = blockLength.indexOf(" lines");
    block.setBlockLength(Integer.parseInt(blockLength.substring(0, pos)));

    block.setElementName(PersistenceUtilities.getAttribute(warning.getAttribute(), SonargraphUtilities.ELEMENT));
    block.setStartLine(Integer.parseInt(PersistenceUtilities.getAttribute(warning.getAttribute(), SonargraphUtilities.START_LINE)));
    return block;
  }

  public static String getBuildUnitName(final XsdCycleGroup group) {
    final String buildUnitName = group.getElementScope();

    // special handling for reports produced with free SonarQube license or without Sonargraph system file
    if ("My Project".equals(buildUnitName) && group.getParent() != null) {
      return group.getParent();
    }

    // reports produced with commercial license
    return group.getElementScope();
  }

  public static void readAttributesToMap(final XsdAttributeRoot root, final Map<String, Number> attributeMap) {
    attributeMap.clear();
    for (final XsdAttributeCategory cat : root.getAttributeCategory()) {
      for (final XsdAttribute attr : cat.getAttribute()) {
        final String attrName = attr.getStandardName();
        addValueToMap(attributeMap, attr, attrName);
      }
    }
  }

  private static void addValueToMap(final Map<String, Number> attributeMap, final XsdAttribute attr, final String attrName) {
    String value = attr.getValue();

    try {
      if (value.startsWith("< 0.01")) {
        value = "0.0";
      }
      if (value.contains(".")) {
        attributeMap.put(attrName, SonargraphPluginBase.FLOAT_FORMAT.parse(value));
      } else {
        attributeMap.put(attrName, SonargraphPluginBase.INTEGER_FORMAT.parse(value));
      }
    } catch (final ParseException e) {
      // Ignore this value
      LOG.error("Failed to parse value : " + value + ", " + e.getMessage());
    }
  }

  public static String getAttribute(final List<XsdAttribute> list, final String name) {
    String value = null;

    for (final XsdAttribute attr : list) {
      if (attr.getName().equals(name)) {
        value = attr.getValue();
        break;
      }
    }
    return value;
  }

  public static String getSonargraphBasePath(final ReportContext report) {
    if (report == null) {
      return null;
    }
    for (final XsdAttribute next : report.getGeneral().getAttribute()) {
      if (BASE_PATH_ATTRIBUTE.equals(next.getName())) {
        return next.getValue();
      }
    }
    return null;
  }
}
