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
package com.hello2morrow.sonarplugin;

import java.text.ParseException;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hello2morrow.sonarplugin.xsd.ReportContext;
import com.hello2morrow.sonarplugin.xsd.XsdAttribute;
import com.hello2morrow.sonarplugin.xsd.XsdAttributeCategory;
import com.hello2morrow.sonarplugin.xsd.XsdAttributeRoot;
import com.hello2morrow.sonarplugin.xsd.XsdCycleGroup;


public class Utilities {
  
  private static final Logger LOG = LoggerFactory.getLogger(Utilities.class);
  
  private Utilities() {}
  
  
  public static XsdAttributeCategory findReportCategoryByName(final ReportContext report, final String categoryName) {
    XsdAttributeCategory category = null;
    for (XsdAttributeCategory cat : report.getAttributes().getAttributeCategory()) {
      if (cat.getName().equals(categoryName)) {
        category = cat;
        break;
      }
    }
    return category;
  }
  

  public static double getDoubleAttributeOfCategory(ReportContext report, XsdAttributeCategory category, String attributeName) {
    double attributeValue = 0.0;
    try {
      attributeValue = Double.parseDouble(getAttribute(category.getAttribute(), attributeName));
    } catch (NumberFormatException e) {
      LOG.error("Value of attribute " + attributeName + " must be a valid number " + e.getMessage());
    }
    return attributeValue;
  }

  public static int getIntAttributeOfCategory(ReportContext report, XsdAttributeCategory category, String attributeName) {
    int attributeValue = 0;
    try {
      attributeValue = Integer.parseInt(getAttribute(category.getAttribute(), attributeName));
    } catch (NumberFormatException e) {
      LOG.error("Value of attribute " + attributeName + " must be a valid number " + e.getMessage());
    }
    return attributeValue;
  }

  
  public static String getAttributeValueByStandardName(List<XsdAttribute> list, String standardName) {
    String value = null;
    for (XsdAttribute attr : list) {
      if (attr.getStandardName().equals(standardName)) {
        value = attr.getValue();
        break;
      }
    }
    return value;
    
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
  
  public static String getBuildUnitName(XsdCycleGroup group) {
    if (group.getParent().equals("(Default Build Unit)")) {
      return group.getElementScope();
    }
    return group.getParent();
  }

  public static String getBuildUnitName(String fqName) {
    String buName = "<UNKNOWN>";

    if (fqName != null) {
      int colonPos = fqName.indexOf("::");

      if (colonPos != -1) {
        buName = fqName.substring(colonPos + 2);
        if (buName.equals("(Default Build Unit)")) {
          // Compatibility with old SonarJ versions
          buName = fqName.substring(0, colonPos);
        }
      }
    }
    return buName;
  }

  

  public static String relativeFileNameToFqName(String fileName) {
    int lastDot = fileName.lastIndexOf('.');

    return fileName.substring(0, lastDot).replace('/', '.');
  }

  public static void readAttributesToMap(XsdAttributeRoot root, final Map<String, Number> attributeMap) {
    attributeMap.clear();
    
    for (XsdAttributeCategory cat : root.getAttributeCategory()) {
      for (XsdAttribute attr : cat.getAttribute()) {
        String attrName = attr.getStandardName();
        String value = attr.getValue();

        try {
          if (value.contains(".")) {
            attributeMap.put(attrName, SonargraphPluginBase.FLOAT_FORMAT.parse(value));
          } else {
            attributeMap.put(attrName, SonargraphPluginBase.INTEGER_FORMAT.parse(value));
          }
        } catch (ParseException e) {
          // Ignore this value
        }
      }
    }
  }
  
  
}
