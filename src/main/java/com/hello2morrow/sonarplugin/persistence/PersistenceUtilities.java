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
import com.hello2morrow.sonarplugin.foundation.Utilities;
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

  public static void addAttributeToList(List<XsdAttribute> attributeList, String name, String value) {
    XsdAttribute attribute = new XsdAttribute();
    attribute.setName(name);
    attribute.setValue(value);
    attributeList.add(attribute);
  }

  public static DuplicateCodeBlock createDuplicateCodeBlock(XsdWarning warning) {
    DuplicateCodeBlock block = new DuplicateCodeBlock();
    String attribute = PersistenceUtilities.getAttribute(warning.getAttribute(), Utilities.BLOCK_ID);
    if (null == attribute) {
      LOG.error("Duplicate code block warning does not contain the required attribute \"Block id\"");
      return null;
    }
    block.setBlockId(Integer.parseInt(attribute));
    block.setProjectName(PersistenceUtilities.getAttribute(warning.getAttribute(), Utilities.PROJECT));
    block.setBuildUnitName(PersistenceUtilities.getAttribute(warning.getAttribute(), Utilities.BUILD_UNIT));
    block.setElementType(PersistenceUtilities.getAttribute(warning.getAttribute(), Utilities.ELEMENT_TYPE));

    String blockLength = PersistenceUtilities.getAttribute(warning.getAttribute(), Utilities.ATTRIBUTE_VALUE);
    int pos = blockLength.indexOf(" lines");
    block.setBlockLength(Integer.parseInt(blockLength.substring(0, pos)));

    block.setElementName(PersistenceUtilities.getAttribute(warning.getAttribute(), Utilities.ELEMENT));
    block.setStartLine(Integer.parseInt(PersistenceUtilities.getAttribute(warning.getAttribute(), Utilities.START_LINE)));
    return block;
  }

  public static String getBuildUnitName(XsdCycleGroup group) {
    String buildUnitName = group.getElementScope();

    // special handling for reports produced with free SonarQube license or without Sonargraph system file
    if (DEFAULT_PROJECT_NAME.equals(buildUnitName) && group.getParent() != null) {
      return group.getParent();
    }

    // reports produced with commercial license
    return group.getElementScope();
  }

  public static void readAttributesToMap(XsdAttributeRoot root, final Map<String, Number> attributeMap) {
    attributeMap.clear();

    for (XsdAttributeCategory cat : root.getAttributeCategory()) {
      for (XsdAttribute attr : cat.getAttribute()) {
        String attrName = attr.getStandardName();
        String value = attr.getValue();
        processAttribute(attributeMap, attrName, value);
      }
    }
  }

  private static void processAttribute(final Map<String, Number> attributeMap, final String attrName, final String value) {
    String adjustedValue;
    if (value.startsWith("< 0.01")) {
      adjustedValue = "0.0";
    } else {
      adjustedValue = value;
    }

    try {
      if (adjustedValue.contains(".")) {
        attributeMap.put(attrName, SonargraphPluginBase.FLOAT_FORMAT.parse(adjustedValue));
      } else {
        attributeMap.put(attrName, SonargraphPluginBase.INTEGER_FORMAT.parse(adjustedValue));
      }
    } catch (ParseException e) {
      // Ignore this value
      LOG.error("Failed to parse value : " + adjustedValue + ", " + e.getMessage());
    }
  }

  public static String getAttribute(List<XsdAttribute> list, String name) {
    String value = null;

    for (XsdAttribute attr : list) {
      if (attr.getName().equals(name)) {
        value = attr.getValue();
        break;
      }
    }
    return value;
  }

  public static String getSonargraphBasePath(ReportContext report) {
    for (XsdAttribute next : report.getGeneral().getAttribute()) {
      if (BASE_PATH_ATTRIBUTE.equals(next.getName())) {
        return next.getValue();
      }
    }
    return null;
  }
}
