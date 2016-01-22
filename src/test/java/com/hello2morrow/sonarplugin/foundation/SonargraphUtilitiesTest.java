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

import com.hello2morrow.sonarplugin.persistence.PersistenceUtilities;
import com.hello2morrow.sonarplugin.xsd.XsdAttribute;
import com.hello2morrow.sonarplugin.xsd.XsdAttributeRoot;
import com.hello2morrow.sonarplugin.xsd.XsdWarning;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class SonargraphUtilitiesTest {

  @Test
  public void testGetAttribute() {
    final XsdAttributeRoot attributeRoot = new XsdAttributeRoot();

    final List<XsdAttribute> list = attributeRoot.getAttribute();
    PersistenceUtilities.addAttributeToList(list, "Path", "c:/users/test");
    PersistenceUtilities.addAttributeToList(list, "Sonargraph Product", "Sonargraph 7.1.3");
    PersistenceUtilities.addAttributeToList(list, "Sonargraph User", "Mr. Hubert Hudson");

    assertEquals("c:/users/test", PersistenceUtilities.getAttribute(list, "Path"));
    assertNull(PersistenceUtilities.getAttribute(list, "Not existent"));
  }

  @Test
  public void testGetBuildUnitNameString() {
    final String name = null;
    assertEquals("<UNKNOWN>", SonargraphUtilities.getBuildUnitName(name));
    assertEquals("<UNKNOWN>", SonargraphUtilities.getBuildUnitName("AlarmClock-AlarmClock"));
    assertEquals("Old Sonarj Version", SonargraphUtilities.getBuildUnitName("Old Sonarj Version::(Default Build Unit)"));
    assertEquals("AlarmClock", SonargraphUtilities.getBuildUnitName("AlarmClock::AlarmClock"));
    assertEquals("AlarmClock", SonargraphUtilities.getBuildUnitName("AlarmClock2::AlarmClock"));
  }

  @Test
  public void testCreateDuplicateCodeBlock() {
    final XsdWarning warning = new XsdWarning();
    PersistenceUtilities.addAttributeToList(warning.getAttribute(), "Project", "AlarmClock");
    PersistenceUtilities.addAttributeToList(warning.getAttribute(), "Build unit", "AlarmClock::AlarmClock");
    PersistenceUtilities.addAttributeToList(warning.getAttribute(), "Element type", "Source file");
    PersistenceUtilities.addAttributeToList(warning.getAttribute(), "Element", "com/h2m/alarm/presentation/AlarmToConsole.java");
    PersistenceUtilities.addAttributeToList(warning.getAttribute(), "Attribute value", "37 lines");
    PersistenceUtilities.addAttributeToList(warning.getAttribute(), "Start line", "11");
    PersistenceUtilities.addAttributeToList(warning.getAttribute(), "Block id", "0");

    final DuplicateCodeBlock block = PersistenceUtilities.createDuplicateCodeBlock(warning);

    assertEquals(block.getProjectName(), "AlarmClock");
    assertEquals(block.getBuildUnitName(), "AlarmClock::AlarmClock");
    assertEquals(block.getElementType(), "Source file");
    assertEquals(block.getElementName(), "com/h2m/alarm/presentation/AlarmToConsole.java");
    assertEquals(block.getBlockLength(), 37);
    assertEquals(block.getStartLine(), 11);
    assertEquals(block.getBlockId(), 0);
  }

  @Test
  public void testGenerateDuplicateCodeBlockMessage() {
    final List<DuplicateCodeBlock> twoBlocks = createTwoCodeBlocks();
    final DuplicateCodeBlock block1 = twoBlocks.get(0);
    twoBlocks.remove(block1);
    String message = SonargraphUtilities.generateDuplicateCodeBlockMessage(block1, twoBlocks);
    final String expected = "Line 10 to 51 is a duplicate of\n" + "line 4 to 44 of com/h2m/common/observer/T1.java";
    assertEquals(expected + ".", message);

    final List<DuplicateCodeBlock> threeBlocks = createThreeCodeBlocks();
    final DuplicateCodeBlock block2 = threeBlocks.get(0);
    threeBlocks.remove(block2);
    message = SonargraphUtilities.generateDuplicateCodeBlockMessage(block2, threeBlocks);
    final String expected2 = "line 10 to 50 of com/h2m/alarm/model/AlarmClock.java";
    assertEquals("Expected additional 'and' not present!", expected + " and\n" + expected2 + ".", message);

    final List<DuplicateCodeBlock> fourBlocks = createFourCodeBlocks();
    final DuplicateCodeBlock block3 = fourBlocks.get(0);
    fourBlocks.remove(block3);
    message = SonargraphUtilities.generateDuplicateCodeBlockMessage(block3, fourBlocks);
    final String expected3 = "line 10 to 50 of com/h2m/alarm/presentation/AlarmToConsole.java";
    assertEquals("Expected additional ', and' not present!", expected + ",\n" + expected2 + " and\n" + expected3 + ".", message);
  }

  @Test
  public void testGetSourceFilePath() {
    final String groupParentPath = "AlarmClock/src/main/java/com/h2m/alarm/presentation";
    final String sourceFilePath = "com/h2m/alarm/presentation/AlarmHandler.java";

    assertEquals("AlarmClock/src/main/java/com/h2m/alarm/presentation/AlarmHandler.java", SonargraphUtilities.getSourceFilePath(groupParentPath, sourceFilePath));

  }

  private List<DuplicateCodeBlock> createTwoCodeBlocks() {
    final List<DuplicateCodeBlock> list = new ArrayList<DuplicateCodeBlock>();

    final DuplicateCodeBlock block1 = new DuplicateCodeBlock();
    block1.setBlockId(1);
    block1.setBlockLength(42);
    block1.setElementName("com/h2m/alarm/presentation/AlarmHandler.java");
    block1.setStartLine(10);
    list.add(block1);

    final DuplicateCodeBlock block2 = new DuplicateCodeBlock();
    block2.setBlockId(1);
    block2.setBlockLength(41);
    block2.setElementName("com/h2m/common/observer/T1.java");
    block2.setStartLine(4);
    list.add(block2);

    return list;
  }

  private List<DuplicateCodeBlock> createThreeCodeBlocks() {
    final List<DuplicateCodeBlock> list = createTwoCodeBlocks();

    final DuplicateCodeBlock block3 = new DuplicateCodeBlock();
    block3.setBlockId(1);
    block3.setBlockLength(41);
    block3.setElementName("com/h2m/alarm/model/AlarmClock.java");
    block3.setStartLine(10);
    list.add(block3);

    return list;
  }

  private List<DuplicateCodeBlock> createFourCodeBlocks() {
    final List<DuplicateCodeBlock> list = createThreeCodeBlocks();

    final DuplicateCodeBlock block4 = new DuplicateCodeBlock();
    block4.setBlockId(1);
    block4.setBlockLength(41);
    block4.setElementName("com/h2m/alarm/presentation/AlarmToConsole.java");
    block4.setStartLine(10);
    list.add(block4);

    return list;
  }

}
