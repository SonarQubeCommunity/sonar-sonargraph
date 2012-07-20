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

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import org.apache.commons.configuration.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hello2morrow.sonarplugin.xsd.ReportContext;
import com.hello2morrow.sonarplugin.xsd.XsdAttributeRoot;
import com.hello2morrow.sonarplugin.xsd.XsdBuildUnits;

/**
 * Utility class for reading in the Sonargraph Report.
 * 
 * @author Ingmar
 * 
 */
public final class ReportFileReader {

  private static final Logger LOG = LoggerFactory.getLogger(ReportFileReader.class);
  private static final String REPORT_DIR = "sonargraph-sonar-plugin";
  private static final String REPORT_NAME = "sonargraph-sonar-report.xml";
  private ReportFileReader() {
  }

  /**
   * Unmarshals the Sonargraph Report into a ReportContext
   * 
   * @param reportFileName
   *          the file name of the Sonargraph Report to be opened
   * @param isRoot
   *          specifies if this project is the root project or a module
   * @return {@link ReportContext} if successful, null otherwise
   */
  public static ReportContext readSonargraphReport(String reportFileName, boolean isRoot) {
    ReportContext result = null;
    InputStream input = null;
    ClassLoader defaultClassLoader = Thread.currentThread().getContextClassLoader();

    try {
      input = new FileInputStream(reportFileName);

      Thread.currentThread().setContextClassLoader(ReportFileReader.class.getClassLoader());
      JAXBContext context = JAXBContext.newInstance("com.hello2morrow.sonarplugin.xsd");
      Unmarshaller u = context.createUnmarshaller();
      result = (ReportContext) u.unmarshal(input);
    } catch (JAXBException e) {
      LOG.error("JAXB Problem in " + reportFileName, e);
    } catch (FileNotFoundException e) {
      if (isRoot) {
        LOG.error("Cannot open Sonargraph report: " + reportFileName + ".");
        LOG.error("  Maven: Did you run the maven sonargraph goal before with the POM option <prepareForSonar>true</prepareForSonar> "
            + "or with the commandline option -Dsonargraph.prepareForSonar=true?");
        LOG.error("  Ant:   Did you create the Sonargraph XML report with the option prepareForSonar set on true? "
            + "(You can use the property 'sonar.sonargraph.report.path' to point to the location of the XML report");
      }
    } finally {
      Thread.currentThread().setContextClassLoader(defaultClassLoader);
      if (input != null) {
        try {
          input.close();
        } catch (IOException e) {
          LOG.error("Cannot close " + reportFileName, e);
        }
      }
    }
    return result;
  }

  public static String getReportFileName(String projectBuildPath, Configuration config) {
    String defaultLocation = projectBuildPath + '/' + REPORT_DIR + '/' + REPORT_NAME;
  
    return config.getString("sonar.sonargraph.report.path", defaultLocation);
  }

  /* package access for testing */
  public static XsdAttributeRoot retrieveBuildUnit(String projectKey, ReportContext report) {
    if (null == report) {
      return null;
    }
  
    XsdBuildUnits buildUnits = report.getBuildUnits();
    List<XsdAttributeRoot> buildUnitList = buildUnits.getBuildUnit();
  
    if (buildUnitList.size() == 1) {
      return buildUnitList.get(0);
    } else if (buildUnitList.size() > 1) {
  
      for (XsdAttributeRoot sonarBuildUnit : buildUnitList) {
        String buName = Utilities.getBuildUnitName(sonarBuildUnit.getName());
        if (Utilities.buildUnitMatchesAnalyzedProject(buName, projectKey)) {
          return sonarBuildUnit;
        }
      }
  
      LOG.warn("Project  with key [" + projectKey + "] could not be mapped to a build unit. "
          + "The project will not be analyzed. Check the build unit configuration of your Sonargraph system.");
    }
    return null;
  }

}
