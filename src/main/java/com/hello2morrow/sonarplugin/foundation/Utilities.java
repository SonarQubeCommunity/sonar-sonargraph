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

import java.text.ParseException;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sonar.api.batch.DecoratorContext;
import org.sonar.api.measures.Metric;
import org.sonar.api.resources.Project;

import com.hello2morrow.sonarplugin.xsd.XsdAttribute;
import com.hello2morrow.sonarplugin.xsd.XsdAttributeCategory;
import com.hello2morrow.sonarplugin.xsd.XsdAttributeRoot;
import com.hello2morrow.sonarplugin.xsd.XsdWarning;


public final class Utilities {
  
  private static final Logger LOG = LoggerFactory.getLogger(Utilities.class);
  
  private Utilities() {}
  
  
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
  

  public static String getBuildUnitName(String fqName) {
    String buName = "<UNKNOWN>";

    if (fqName != null) {
      int colonPos = fqName.indexOf("::");

      if (colonPos != -1) {
        buName = fqName.substring(colonPos + 2);
        if ("(Default Build Unit)".equals(buName)) {
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
          LOG.error("Failed to parse value : " + value + ", " + e.getMessage());
        }
      }
    }
  }
  
  public static boolean isAggregationProject(DecoratorContext context, final Metric indicator) {
    return context.getChildrenMeasures(indicator).size() > 0;
  }
  
  public static boolean isRootParentProject(final Project project) {
    boolean isRootParentProject = false;
    if (null == project) {
      return false;
    }
    List<Project> modules = project.getModules();
    if (null == project.getParent() && null != modules && modules.size() > 0)  {
      isRootParentProject = true;
    }
    return isRootParentProject;
  }
  
  public static String generateSpaceEntity(int numberOfSpaces) {
    StringBuffer buffer = new StringBuffer();
    for (int i=0; i < numberOfSpaces; i++) {
      buffer.append("&nbsp;");
    }
    return buffer.toString();
  }

  public static String generateSpaces(int numberOfSpaces) {
    StringBuffer buffer = new StringBuffer();
    for (int i=0; i < numberOfSpaces; i++) {
      buffer.append(" ");
    }
    return buffer.toString();
  }

  public static DuplicateCodeBlock createDuplicateCodeBlock(XsdWarning warning) {
    DuplicateCodeBlock block = new DuplicateCodeBlock();
    String attribute = getAttribute(warning.getAttribute(), "Block id");
    if (null == attribute) {
      LOG.error("Duplicate code block warning does not contain the required attribute \"Block id\"");
      return null;
    }
    block.setBlockId(Integer.parseInt(attribute));
    block.setProjectName(getAttribute(warning.getAttribute(), "Project"));
    block.setBuildUnitName(getAttribute(warning.getAttribute(), "Build unit"));
    block.setElementType(getAttribute(warning.getAttribute(), "Element type"));
    
    String blockLength = getAttribute(warning.getAttribute(), "Attribute value");
    int pos = blockLength.indexOf(" lines");
    block.setBlockLength(Integer.parseInt(blockLength.substring(0, pos)));
    
    block.setElementName(getAttribute(warning.getAttribute(), "Element"));
    block.setStartLine(Integer.parseInt(getAttribute(warning.getAttribute(), "Start line")));
    return block;
  }
  
  public static void addAttributeToList(List<XsdAttribute> attributeList, String name, String value) {
    XsdAttribute attribute = new XsdAttribute();
    attribute.setName(name);
    attribute.setValue(value);
    attributeList.add(attribute);
  }

  
  public static String generateDuplicateCodeBlockMessage(DuplicateCodeBlock block, List<DuplicateCodeBlock> blocks) {
    final int endLine = block.getBlockLength() + block.getStartLine() - 1;

    final StringBuilder message = new StringBuilder();
    message.append("Line ").append(block.getStartLine()).append(" to ").append(endLine)
      .append(" is a duplicate of\n");

    final int toBeDescribed = blocks.size() - 1;
    int yetToDescribe = toBeDescribed;

    for (final DuplicateCodeBlock duplicate : blocks) {
      if (duplicate == block) {
        continue;
      }

      // No connection for first described element.
      if (toBeDescribed != yetToDescribe) {
        if (yetToDescribe == 1) {
          // Last.
          switch (toBeDescribed) {
            case 2:
              // Just two parts.
              message.append(" and\n");
              break;
            default:
              // More than two parts: Enumeration.
              message.append(", and\n");
              break;
          }
        } else {
          // Not last, and not first: enumerate.
          message.append(", ");
        }
      }

      final int endLineDuplicate = duplicate.getBlockLength() + duplicate.getStartLine() - 1;
      message.append("line " + duplicate.getStartLine() + " to " + endLineDuplicate + " of "
          + duplicate.getElementName());

      yetToDescribe--;
    }
    message.append(".");
    return message.toString();
  }
  
}
