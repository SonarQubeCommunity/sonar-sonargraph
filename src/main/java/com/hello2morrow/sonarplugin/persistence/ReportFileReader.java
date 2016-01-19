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

import com.hello2morrow.sonarplugin.foundation.SonarQubeUtilities;
import com.hello2morrow.sonarplugin.foundation.SonargraphUtilities;
import com.hello2morrow.sonarplugin.xsd.ReportContext;
import com.hello2morrow.sonarplugin.xsd.XsdAttributeRoot;
import com.hello2morrow.sonarplugin.xsd.XsdBuildUnits;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sonar.api.batch.fs.FileSystem;
import org.sonar.api.config.Settings;
import org.sonar.api.resources.Project;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

/**
 * Utility class for reading in the Sonargraph Report.
 *
 * @author Ingmar
 *
 */
public class ReportFileReader implements IReportReader {

  private static final Logger LOG = LoggerFactory.getLogger(ReportFileReader.class);
  private static final String REPORT_DIR = "sonargraph-sonar-plugin";
  private static final String REPORT_NAME = "sonargraph-sonar-report.xml";
  private ReportContext report;

  public ReportFileReader() {
    super();
  }

  @Override
  public void readSonargraphReport(final Project project, FileSystem moduleFileSystem, Settings settings) {
    if (project == null) {
      LOG.error("No project provided for reading sonargraph report");
      return;
    }

    String reportFileName = determineReportFileName(moduleFileSystem, settings);
    LOG.info("Reading Sonargraph metrics report from: " + reportFileName);
    report = null;
    InputStream input = null;
    ClassLoader defaultClassLoader = Thread.currentThread().getContextClassLoader();

    try {
      input = new FileInputStream(reportFileName);

      Thread.currentThread().setContextClassLoader(ReportFileReader.class.getClassLoader());
      JAXBContext context = JAXBContext.newInstance("com.hello2morrow.sonarplugin.xsd");
      Unmarshaller u = context.createUnmarshaller();
      report = (ReportContext) u.unmarshal(input);
    } catch (JAXBException e) {
      LOG.error("JAXB Problem in " + reportFileName, e);
    } catch (FileNotFoundException e) {
      if (project.isRoot()) {
        LOG.error("Cannot open Sonargraph report: " + reportFileName + ".");
        LOG.error("  Maven: Did you run the maven sonargraph goal before with the POM option <prepareForSonar>true</prepareForSonar> "
          + "or with the commandline option -Dsonargraph.prepareForSonar=true?");
        LOG.error("  Ant:   Did you create the Sonargraph XML report with the option prepareForSonar set on true? "
          + "(You can use the property 'sonar.sonargraph.report.path' to point to the location of the XML report");
      }

      LOG.debug("No Soargraph report found", e);

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
  }

  /*
   * (non-Javadoc)
   * 
   * @see com.hello2morrow.sonarplugin.foundation.IReportReader#getReport()
   */
  @Override
  public ReportContext getReport() {
    return report;
  }

  private String determineReportFileName(FileSystem moduleFileSystem, Settings settings) {
    String configuredReportPath = SonargraphUtilities.getConfiguredReportPath(settings);

    if (moduleFileSystem == null) {
      return configuredReportPath;
    }

    if (configuredReportPath == null || configuredReportPath.length() == 0) {
      return moduleFileSystem.workDir().getParentFile().getPath() + '/' + REPORT_DIR + '/' + REPORT_NAME;
    }

    return configuredReportPath;
  }

  /*
   * (non-Javadoc)
   * 
   * @see com.hello2morrow.sonarplugin.foundation.IReportReader#retrieveBuildUnit(java.lang.String)
   */
  @Override
  public XsdAttributeRoot retrieveBuildUnit(Project project) {
    if (null == report) {
      return null;
    }

    XsdBuildUnits buildUnits = report.getBuildUnits();
    List<XsdAttributeRoot> buildUnitList = buildUnits.getBuildUnit();

    if (buildUnitList.size() == 1) {
      if (project.isRoot()) {
        return buildUnitList.get(0);
      }

      XsdAttributeRoot sonarBuildUnit = buildUnitList.get(0);
      String buName = SonargraphUtilities.getBuildUnitName(sonarBuildUnit.getName());
      if (SonarQubeUtilities.buildUnitMatchesAnalyzedProject(buName, project)) {
        return sonarBuildUnit;
      }
    } else if (buildUnitList.size() > 1) {
      for (XsdAttributeRoot sonarBuildUnit : buildUnitList) {
        String buName = SonargraphUtilities.getBuildUnitName(sonarBuildUnit.getName());
        if (SonarQubeUtilities.buildUnitMatchesAnalyzedProject(buName, project)) {
          return sonarBuildUnit;
        }
      }

      LOG.warn("Project  with key [" + project.getKey() + "] could not be mapped to a build unit. "
        + "The project will not be analyzed. Check the build unit configuration of your Sonargraph system.");
    }
    return null;
  }

  @Override
  public boolean hasSonargraphReport(FileSystem fileSystem, Settings settings) {
    String reportFileName = determineReportFileName(fileSystem, settings);
    if (reportFileName == null || !(new File(reportFileName).exists())) {
      return false;
    }
    return true;
  }
}
