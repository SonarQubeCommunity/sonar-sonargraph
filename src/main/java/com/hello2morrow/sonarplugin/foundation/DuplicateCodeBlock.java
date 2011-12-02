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


public class DuplicateCodeBlock {
  private String projectName;
  private String buildUnitName;
  private String elementType;
  private String elementName;
  private int blockLength = 0;
  private int startLine = 0;
  private int blockId;
  
  public String getProjectName() {
    return projectName;
  }
  
  public void setProjectName(String projectName) {
    this.projectName = projectName;
  }
  
  public String getBuildUnitName() {
    return buildUnitName;
  }
  
  public void setBuildUnitName(String buildUnitName) {
    this.buildUnitName = buildUnitName;
  }
  
  public String getElementType() {
    return elementType;
  }
  
  public void setElementType(String elmentType) {
    this.elementType = elmentType;
  }
  
  public String getElementName() {
    return elementName;
  }
  
  public void setElementName(String elementName) {
    this.elementName = elementName;
  }
  
  public int getBlockLength() {
    return blockLength;
  }
  
  public void setBlockLength(int blockLength) {
    this.blockLength = blockLength;
  }
  
  public int getStartLine() {
    return startLine;
  }
  
  public void setStartLine(int startLine) {
    this.startLine = startLine;
  }
  
  public int getBlockId() {
    return blockId;
  }
  
  public void setBlockId(int blockId) {
    this.blockId = blockId;
  }
}
