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

import com.hello2morrow.sonarplugin.xsd.ReportContext;
import com.hello2morrow.sonarplugin.xsd.XsdArchitectureViolation;
import com.hello2morrow.sonarplugin.xsd.XsdAttribute;
import com.hello2morrow.sonarplugin.xsd.XsdAttributeCategory;
import com.hello2morrow.sonarplugin.xsd.XsdAttributeRoot;
import com.hello2morrow.sonarplugin.xsd.XsdBuildUnits;
import com.hello2morrow.sonarplugin.xsd.XsdCycleGroup;
import com.hello2morrow.sonarplugin.xsd.XsdCycleGroups;
import com.hello2morrow.sonarplugin.xsd.XsdCyclePath;
import com.hello2morrow.sonarplugin.xsd.XsdPosition;
import com.hello2morrow.sonarplugin.xsd.XsdTask;
import com.hello2morrow.sonarplugin.xsd.XsdTasks;
import com.hello2morrow.sonarplugin.xsd.XsdTypeRelation;
import com.hello2morrow.sonarplugin.xsd.XsdViolations;
import com.hello2morrow.sonarplugin.xsd.XsdWarning;
import com.hello2morrow.sonarplugin.xsd.XsdWarnings;
import com.hello2morrow.sonarplugin.xsd.XsdWarningsByAttribute;
import com.hello2morrow.sonarplugin.xsd.XsdWarningsByAttributeGroup;
import org.apache.commons.configuration.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sonar.api.batch.Sensor;
import org.sonar.api.batch.SensorContext;
import org.sonar.api.measures.Measure;
import org.sonar.api.measures.Metric;
import org.sonar.api.resources.JavaFile;
import org.sonar.api.resources.JavaPackage;
import org.sonar.api.resources.Project;
import org.sonar.api.resources.Resource;
import org.sonar.api.rules.Rule;
import org.sonar.api.rules.RuleFinder;
import org.sonar.api.rules.RulePriority;
import org.sonar.api.rules.Violation;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class SonargraphSensor implements Sensor
{
  private static final Logger LOG = LoggerFactory.getLogger(SonargraphSensor.class);

  private static final String REPORT_DIR = "sonargraph-sonar-plugin";
  private static final String REPORT_NAME = "sonargraph-sonar-report.xml";

  private static final String ACD = "AverageComponentDependency";
  private static final String NCCD = "NormalizedCumulativeComponentDependency";
  private static final String INTERNAL_PACKAGES = "NumberOfInternalNamespaces";
  private static final String INSTRUCTIONS = "NumberOfInstructions";
  private static final String UNASSIGNED_TYPES = "NumberOfNotAssignedTypes";
  private static final String VIOLATING_DEPENDENCIES = "NumberOfViolations";
  private static final String VIOLATING_TYPES = "NumberOfViolatingTypes";
  private static final String TYPE_DEPENDENCIES = "OverallNumberOfTypeDependencies";
  private static final String JAVA_FILES = "NumberOfSourceFiles";
  private static final String IGNORED_VIOLATIONS = "NumberOfIgnoredViolations";
  private static final String IGNORED_WARNINGS = "NumberOfIgnoredWarnings";
  private static final String TASKS = "NumberOfTasks";
  private static final String ALL_WARNINGS = "NumberOfWarnings";
  private static final String CYCLE_WARNINGS = "NumberOfCyclicWarnings";
  private static final String THRESHOLD_WARNINGS = "NumberOfMetricWarnings";
  private static final String WORKSPACE_WARNINGS = "NumberOfWorkspaceWarnings";
  private static final String DUPLICATE_WARNINGS = "NumberOfDuplicateCodeBlocksWarnings";
  private static final String EROSION_REFS = "StructuralErosionReferenceLevel";
  private static final String EROSION_TYPES = "StructuralErosionTypeLevel";
  private static final String INTERNAL_TYPES = "NumberOfInternalTypes";
  private static final String STUCTURAL_DEBT_INDEX = "StructuralDebtIndex";

  private final Map<String, Number> buildUnitMetrics = new HashMap<String, Number>();
  private SensorContext sensorContext;
  private final RuleFinder ruleFinder;
  private double indexCost = SonargraphPluginBase.COST_PER_INDEX_POINT_DEFAULT;
  private final Configuration configuration;

  protected static ReportContext readSonargraphReport(String fileName, String packaging)
  {
    ReportContext result = null;
    InputStream input = null;
    ClassLoader defaultClassLoader = Thread.currentThread().getContextClassLoader();

    try
    {
      Thread.currentThread().setContextClassLoader(SonargraphSensor.class.getClassLoader());
      JAXBContext context = JAXBContext.newInstance("com.hello2morrow.sonarplugin.xsd");
      Unmarshaller u = context.createUnmarshaller();

      input = new FileInputStream(fileName);
      result = (ReportContext) u.unmarshal(input);
    }
    catch (JAXBException e)
    {
      LOG.error("JAXB Problem in " + fileName, e);
    }
    catch (FileNotFoundException e)
    {
      if (!packaging.equalsIgnoreCase("pom"))
      {
        LOG.warn("Cannot open Sonargraph report: " + fileName + ".");
        LOG.warn("  Did you run the maven sonargraph goal before with the POM option <prepareForSonar>true</prepareForSonar> " + "or with the commandline option -Dsonargraph.prepareForSonar=true?");
        LOG.warn("  Is the project part of the Sonargraph architecture description?");
        LOG.warn("  Did you set the 'aggregate' to true (must be false)?");
      }
    }
    finally
    {
      Thread.currentThread().setContextClassLoader(defaultClassLoader);
      if (input != null)
      {
        try
        {
          input.close();
        }
        catch (IOException e)
        {
          LOG.error("Cannot close " + fileName, e);
        }
      }
    }
    return result;
  }

  public SonargraphSensor(RuleFinder ruleFinder, Configuration configuration)
  {
    this.configuration = configuration;
    this.ruleFinder = ruleFinder;
    if (ruleFinder == null)
    {
      LOG.warn("No RulesManager provided to sensor");
    }
  }

  public boolean shouldExecuteOnProject(Project project)
  {
    return true;
  }

  private void readAttributes(XsdAttributeRoot root)
  {
    buildUnitMetrics.clear();

    for (XsdAttributeCategory cat : root.getAttributeCategory())
    {
      for (XsdAttribute attr : cat.getAttribute())
      {
        String attrName = attr.getStandardName();
        String value = attr.getValue();

        try
        {
          if (value.contains("."))
          {
            buildUnitMetrics.put(attrName, SonargraphPluginBase.FLOAT_FORMAT.parse(value));
          }
          else
          {
            buildUnitMetrics.put(attrName, SonargraphPluginBase.INTEGER_FORMAT.parse(value));
          }
        }
        catch (ParseException e)
        {
          // Ignore this value
        }
      }
    }
  }

  private boolean hasBuildUnitMetric(String key)
  {
    return buildUnitMetrics.get(key) != null;
  }

  private double getBuildUnitMetric(String key)
  {
    Number num = buildUnitMetrics.get(key);

    if (num == null)
    {
      LOG.error("Cannot find metric <" + key + "> in generated report");
      return 0.0;
    }
    return num.doubleValue();
  }

  private Measure saveMeasure(String key, Metric metric, int precision)
  {
    double value = getBuildUnitMetric(key);

    return saveMeasure(metric, value, precision);
  }

  private Measure saveMeasure(Metric metric, double value, int precision)
  {
    Measure m = new Measure(metric, value, precision);

    sensorContext.saveMeasure(m);
    return m;
  }

  private String getAttribute(List<XsdAttribute> map, String name)
  {
    String value = null;

    for (XsdAttribute attr : map)
    {
      if (attr.getName().equals(name))
      {
        value = attr.getValue();
        break;
      }
    }
    return value;
  }

  private void analyseCycleGroups(ReportContext report, Number internalPackages, String buildUnitName)
  {
    XsdCycleGroups cycleGroups = report.getCycleGroups();
    double cyclicity = 0;
    double biggestCycleGroupSize = 0;
    double cyclicPackages = 0;

    for (XsdCycleGroup group : cycleGroups.getCycleGroup())
    {
      if (group.getNamedElementGroup().equals("Physical package") && getBuildUnitName(group).equals(buildUnitName))
      {
        int groupSize = group.getCyclePath().size();
        cyclicPackages += groupSize;
        cyclicity += groupSize * groupSize;
        if (groupSize > biggestCycleGroupSize)
        {
          biggestCycleGroupSize = groupSize;
        }
        handlePackageCycleGroup(group);
      }
    }
    saveMeasure(SonargraphMetrics.BIGGEST_CYCLE_GROUP, biggestCycleGroupSize, 0);
    saveMeasure(SonargraphMetrics.CYCLICITY, cyclicity, 0);
    saveMeasure(SonargraphMetrics.CYCLIC_PACKAGES, cyclicPackages, 0);

    double relativeCyclicity = 100.0 * Math.sqrt(cyclicity) / internalPackages.doubleValue();
    double relativeCyclicPackages = 100.0 * cyclicPackages / internalPackages.doubleValue();

    saveMeasure(SonargraphMetrics.RELATIVE_CYCLICITY, relativeCyclicity, 1);
    saveMeasure(SonargraphMetrics.INTERNAL_PACKAGES, internalPackages.doubleValue(), 0);
    saveMeasure(SonargraphMetrics.CYCLIC_PACKAGES_PERCENT, relativeCyclicPackages, 1);
  }

  @SuppressWarnings("unchecked")
  private void saveViolation(Rule rule, RulePriority priority, String fqName, int line, String msg)
  {
    Resource javaFile = sensorContext.getResource(new JavaFile(fqName));

    if (javaFile == null)
    {
      LOG.error("Cannot obtain resource " + fqName);
    }
    else
    {
      Violation v = Violation.create(rule, javaFile);

      v.setMessage(msg);
      v.setLineId(line);
      if (priority != null)
      {
        v.setSeverity(priority);
      }
      sensorContext.saveViolation(v);
    }
  }

  private String getBuildUnitName(XsdCycleGroup group)
  {
    if (group.getParent().equals("(Default Build Unit)"))
    {
      return group.getElementScope();
    }
    return group.getParent();
  }

  private String getBuildUnitName(String fqName)
  {
    String buName = "<UNKNOWN>";

    if (fqName != null)
    {
      int colonPos = fqName.indexOf("::");

      if (colonPos != -1)
      {
        buName = fqName.substring(colonPos + 2);
        if (buName.equals("(Default Build Unit)"))
        {
          // Compatibility with old SonarJ versions
          buName = fqName.substring(0, colonPos);
        }
      }
    }
    return buName;
  }

  private static String relativeFileNameToFqName(String fileName)
  {
    int lastDot = fileName.lastIndexOf('.');

    return fileName.substring(0, lastDot).replace('/', '.');
  }

  @SuppressWarnings("unchecked")
  private void handlePackageCycleGroup(XsdCycleGroup group)
  {
    Rule rule = ruleFinder.findByKey(SonargraphPluginBase.PLUGIN_KEY, SonargraphPluginBase.CYCLE_GROUP_RULE_KEY);

    if (rule != null)
    {
      for (XsdCyclePath pathElement : group.getCyclePath())
      {
        String fqName = pathElement.getParent();
        Resource<JavaPackage> javaPackage = sensorContext.getResource(new JavaPackage(fqName));

        if (javaPackage == null)
        {
          LOG.error("Cannot obtain resource " + fqName);
        }
        else
        {
          Violation v = Violation.create(rule, javaPackage);

          v.setMessage("Package participates in a cycle group");
          v.setLineId(1);
          sensorContext.saveViolation(v);
        }
      }
    }
  }

  private int handleArchitectureViolations(XsdViolations violations, String buildUnitName)
  {
    Rule rule = ruleFinder.findByKey(SonargraphPluginBase.PLUGIN_KEY, SonargraphPluginBase.ARCH_RULE_KEY);
    int count = 0;

    for (XsdArchitectureViolation violation : violations.getArchitectureViolations())
    {
      String toName = getAttribute(violation.getArchitectureViolation().getAttribute(), "To");
      String toElemType = getAttribute(violation.getArchitectureViolation().getAttribute(), "To element type").toLowerCase();
      String target = toElemType + ' ' + toName;

      for (XsdTypeRelation rel : violation.getTypeRelation())
      {
        String toType = getAttribute(rel.getAttribute(), "To");
        String msg = "Type " + toType + " from " + target + " must not be used from here";
        String bu = getAttribute(rel.getAttribute(), "From build unit");

        bu = getBuildUnitName(bu);
        if (bu.equals(buildUnitName))
        {
          for (XsdPosition pos : rel.getPosition())
          {
            if (rule != null)
            {
              String relFileName = pos.getFile();

              if (relFileName != null)
              {
                String fqName = relativeFileNameToFqName(relFileName);
                saveViolation(rule, null, fqName, Integer.valueOf(pos.getLine()), msg);
              }
            }
            count++;
          }
        }
      }

    }
    if (rule == null)
    {
      LOG.error("Sonargraph architecture rule not found");
    }
    return count;
  }

  private String getRuleKey(String attributeGroup)
  {
    if (attributeGroup.equals("Duplicate code"))
    {
      return SonargraphPluginBase.DUPLICATE_RULE_KEY;
    }
    if (attributeGroup.equals("Workspace"))
    {
      return SonargraphPluginBase.WORKSPACE_RULE_KEY;
    }
    if (attributeGroup.equals("Threshold"))
    {
      return SonargraphPluginBase.THRESHOLD_RULE_KEY;
    }
    return null;
  }

  private void handleWarnings(XsdWarnings warnings, String buildUnitName)
  {
    for (XsdWarningsByAttributeGroup warningGroup : warnings.getWarningsByAttributeGroup())
    {
      String key = getRuleKey(warningGroup.getAttributeGroup());
      if (key == null)
      {
        continue;
      }
      Rule rule = ruleFinder.findByKey(SonargraphPluginBase.PLUGIN_KEY, key);
      if (rule == null)
      {
        LOG.error("Sonargraph threshold rule not found");
        continue;
      }
      for (XsdWarningsByAttribute warningByAttribute : warningGroup.getWarningsByAttribute())
      {
        String attrName = warningByAttribute.getAttributeName();

        for (XsdWarning warning : warningByAttribute.getWarning())
        {
          String msg = attrName + "=" + getAttribute(warning.getAttribute(), "Attribute value");
          String bu = getAttribute(warning.getAttribute(), "Build unit");

          bu = getBuildUnitName(bu);
          if (bu.equals(buildUnitName))
          {
            if (warning.getPosition().size() > 0)
            {
              for (XsdPosition pos : warning.getPosition())
              {
                String relFileName = pos.getFile();

                if (relFileName != null)
                {
                  String fqName = relativeFileNameToFqName(relFileName);

                  saveViolation(rule, null, fqName, Integer.valueOf(pos.getLine()), msg);
                }
              }
            }
            else
            {
              String elemType = getAttribute(warning.getAttribute(), "Element type");

              if (elemType.equals("Class file") || elemType.equals("Source file"))
              {
                // Attach a violation at line 1
                String fileName = getAttribute(warning.getAttribute(), "Element");
                String fqName = fileName.substring(0, fileName.lastIndexOf('.')).replace('/', '.');

                saveViolation(rule, null, fqName, 1, msg);
              }
            }
          }
        }
      }
    }
  }

  private String handleDescription(String descr)
  {
    if (descr.startsWith("Fix warning"))
    {
      // TODO: handle ascending metrics correctly (99% are descending)
      return "Reduce" + descr.substring(descr.indexOf(':') + 1).toLowerCase();
    }
    if (descr.startsWith("Cut type"))
    {
      String toType = descr.substring(descr.indexOf("to "));

      return "Cut dependency " + toType;
    }
    if (descr.startsWith("Move type"))
    {
      String to = descr.substring(descr.indexOf("to "));

      return "Move " + to;
    }
    return descr;
  }

  private int handleTasks(XsdTasks tasks, String buildUnitName)
  {
    Map<String, RulePriority> priorityMap = new HashMap<String, RulePriority>();

    Rule rule = ruleFinder.findByKey(SonargraphPluginBase.PLUGIN_KEY, SonargraphPluginBase.TASK_RULE_KEY);
    int count = 0;

    if (rule == null)
    {
      LOG.error("Sonargraph task rule not found");
      return 0;
    }

    priorityMap.put("Low", RulePriority.INFO);
    priorityMap.put("Medium", RulePriority.MINOR);
    priorityMap.put("High", RulePriority.MAJOR);

    for (XsdTask task : tasks.getTask())
    {
      String bu = getAttribute(task.getAttribute(), "Build unit");

      bu = getBuildUnitName(bu);
      if (bu.equals(buildUnitName))
      {
        String priority = getAttribute(task.getAttribute(), "Priority");
        String description = getAttribute(task.getAttribute(), "Description");
        String assignedTo = getAttribute(task.getAttribute(), "Assigned to");

        description = handleDescription(description); // This should not
        // be needed,
        // but the
        // current
        // description
        // sucks

        int index = description.indexOf(" package");

        if (index > 0 && index < 8)
        {
          // Package refactorings won't get markers - this would
          // create to many non relevant markers
          count++;
        }
        else
        {
          if (assignedTo != null)
          {
            assignedTo = '[' + assignedTo.trim() + ']';
            if (assignedTo.length() > 2)
            {
              description += ' ' + assignedTo;
            }
          }
          for (XsdPosition pos : task.getPosition())
          {
            String relFileName = pos.getFile();

            if (relFileName != null)
            {
              String fqName = relativeFileNameToFqName(relFileName);
              int line = Integer.valueOf(pos.getLine());

              if (line == 0)
              {
                line = 1;
              }
              saveViolation(rule, priorityMap.get(priority), fqName, line, description);
            }
            count++;
          }
        }
      }
    }
    return count;
  }

  private void addArchitectureMeasures(ReportContext report, String buildUnitName)
  {
    double types = saveMeasure(INTERNAL_TYPES, SonargraphMetrics.INTERNAL_TYPES, 0).getValue();
    Measure unassignedTypes = saveMeasure(UNASSIGNED_TYPES, SonargraphMetrics.UNASSIGNED_TYPES, 0);
    Measure violatingTypes = saveMeasure(VIOLATING_TYPES, SonargraphMetrics.VIOLATING_TYPES, 0);
    saveMeasure(VIOLATING_DEPENDENCIES, SonargraphMetrics.VIOLATING_DEPENDENCIES, 0);
    saveMeasure(TASKS, SonargraphMetrics.TASKS, 0);
    if (hasBuildUnitMetric(THRESHOLD_WARNINGS))
    {
      saveMeasure(THRESHOLD_WARNINGS, SonargraphMetrics.THRESHOLD_WARNINGS, 0);
    }
    saveMeasure(WORKSPACE_WARNINGS, SonargraphMetrics.WORKSPACE_WARNINGS, 0);
    saveMeasure(IGNORED_VIOLATIONS, SonargraphMetrics.IGNORED_VIOLATONS, 0);
    saveMeasure(IGNORED_WARNINGS, SonargraphMetrics.IGNORED_WARNINGS, 0);
    if (hasBuildUnitMetric(DUPLICATE_WARNINGS))
    {
      saveMeasure(DUPLICATE_WARNINGS, SonargraphMetrics.DUPLICATE_WARNINGS, 0);
    }
    saveMeasure(CYCLE_WARNINGS, SonargraphMetrics.CYCLE_WARNINGS, 0);
    saveMeasure(ALL_WARNINGS, SonargraphMetrics.ALL_WARNINGS, 0);

    assert types >= 1.0 : "Project must not be empty !";

    double violatingTypesPercent = 100.0 * violatingTypes.getValue() / types;
    double unassignedTypesPercent = 100.0 * unassignedTypes.getValue() / types;

    saveMeasure(SonargraphMetrics.VIOLATING_TYPES_PERCENT, violatingTypesPercent, 1);
    saveMeasure(SonargraphMetrics.UNASSIGNED_TYPES_PERCENT, unassignedTypesPercent, 1);

    XsdViolations violations = report.getViolations();

    double violatingRefs = 0;
    double taskRefs = 0;

    if (ruleFinder != null)
    {
      violatingRefs = handleArchitectureViolations(violations, buildUnitName);
      handleWarnings(report.getWarnings(), buildUnitName);
      taskRefs = handleTasks(report.getTasks(), buildUnitName);
    }
    saveMeasure(SonargraphMetrics.ARCHITECTURE_VIOLATIONS, violatingRefs, 0);
    saveMeasure(SonargraphMetrics.TASK_REFS, taskRefs, 0);
  }

  private void analyse(IProject project, XsdAttributeRoot xsdBuildUnit, String buildUnitName, ReportContext report)
  {
    LOG.info("Adding measures for " + project.getName());

    readAttributes(xsdBuildUnit);

    Number internalPackages = getBuildUnitMetric(INTERNAL_PACKAGES);

    if (internalPackages.intValue() == 0)
    {
      LOG.warn("No packages found in project " + project.getName());
      return;
    }

    saveMeasure(ACD, SonargraphMetrics.ACD, 1);
    saveMeasure(NCCD, SonargraphMetrics.NCCD, 1);
    saveMeasure(INSTRUCTIONS, SonargraphMetrics.INSTRUCTIONS, 0);
    saveMeasure(JAVA_FILES, SonargraphMetrics.JAVA_FILES, 0);
    saveMeasure(TYPE_DEPENDENCIES, SonargraphMetrics.TYPE_DEPENDENCIES, 0);
    saveMeasure(EROSION_REFS, SonargraphMetrics.EROSION_REFS, 0);
    saveMeasure(EROSION_TYPES, SonargraphMetrics.EROSION_TYPES, 0);
    Number structuralDebtIndex = saveMeasure(STUCTURAL_DEBT_INDEX, SonargraphMetrics.EROSION_INDEX, 0).getValue();

    if (indexCost > 0)
    {
      double structuralDebtCost = structuralDebtIndex.doubleValue() * indexCost;
      saveMeasure(SonargraphMetrics.EROSION_COST, structuralDebtCost, 0);
    }
    analyseCycleGroups(report, internalPackages, buildUnitName);
    if (hasBuildUnitMetric(UNASSIGNED_TYPES))
    {
      LOG.info("Adding architecture measures for " + project.getName());
      addArchitectureMeasures(report, buildUnitName);
    }
    AlertDecorator.setAlertLevels(new SensorProjectContext(sensorContext));
  }

  void analyse(IProject project, SensorContext sensorContext, ReportContext report)
  {
    this.sensorContext = sensorContext;
    Configuration configuration = project.getConfiguration();
    this.indexCost = configuration.getDouble(SonargraphPluginBase.COST_PER_INDEX_POINT, SonargraphPluginBase.COST_PER_INDEX_POINT_DEFAULT);

    XsdBuildUnits buildUnits = report.getBuildUnits();
    List<XsdAttributeRoot> buildUnitList = buildUnits.getBuildUnit();

    if (buildUnitList.size() == 1)
    {
      XsdAttributeRoot sonarBuildUnit = buildUnitList.get(0);
      String buName = getBuildUnitName(sonarBuildUnit.getName());

      analyse(project, sonarBuildUnit, buName, report);
    }
    else if (buildUnitList.size() > 1)
    {
      boolean foundMatchingBU = false;
      for (XsdAttributeRoot sonarBuildUnit : buildUnitList)
      {
        String buName = getBuildUnitName(sonarBuildUnit.getName());
        if (buildUnitMatchesAnalyzedProject(buName, project))
        {
          analyse(project, sonarBuildUnit, buName, report);
          foundMatchingBU = true;
          break;
        }
      }
      if (!foundMatchingBU)
      {
        LOG.warn("Project " + project.getName() + " could not be mapped to a build unit. The project will not be analyzed. Check the build unit configuration of your Sonargraph system.");
      }
    }
    else
    {
      LOG.error("No build units found in report file!");
    }
  }

  private boolean buildUnitMatchesAnalyzedProject(String buName, IProject project)
  {
    final String[] elements = project.getKey().split(":");
    assert elements.length >= 1 : "project.getKey() must not return an empty string";
    final String artifactId = elements[elements.length-1];
    final String groupId = elements[0];
    final String longName = artifactId + "[" + groupId + "]";
    final String longName2 = groupId + ':' + artifactId;

    return buName.equalsIgnoreCase(artifactId) || buName.equalsIgnoreCase(longName) || buName.equalsIgnoreCase(longName2) || (buName.startsWith("...") && longName2.endsWith(buName.substring(2)));
  }

  public void analyse(Project project, SensorContext sensorContext)
  {
    LOG.info("------------------------------------------------------------------------");
    LOG.info("Execute sonar-sonargraph-plugin for " + project.getName());
    LOG.info("------------------------------------------------------------------------");

    String reportPath = getReportFileName(project);

    LOG.info("Reading Sonargraph metrics report from: "+reportPath);
    ReportContext report = readSonargraphReport(reportPath, project.getPackaging());

    if (report != null)
    {
      analyse(new ProjectDelegate(project), sensorContext, report);
    }
  }

  public String getReportFileName(Project project)
  {
    String defaultLocation = project.getFileSystem().getBuildDir().getPath() + '/' + REPORT_DIR + '/' + REPORT_NAME;

    return configuration.getString("sonar.sonargraph.report.path", defaultLocation);
  }
}
