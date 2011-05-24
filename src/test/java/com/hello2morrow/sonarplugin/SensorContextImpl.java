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

import org.sonar.api.batch.Event;
import org.sonar.api.batch.SensorContext;
import org.sonar.api.design.Dependency;
import org.sonar.api.measures.Measure;
import org.sonar.api.measures.MeasuresFilter;
import org.sonar.api.measures.Metric;
import org.sonar.api.resources.DuplicatedSourceException;
import org.sonar.api.resources.ProjectLink;
import org.sonar.api.resources.Resource;
import org.sonar.api.rules.Violation;

import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Set;

public class SensorContextImpl implements SensorContext
{
  /**
   * Indexes a resource as a direct child of project. This method does nothing and returns true if the resource already indexed.
   *
   * @return false if the resource is excluded
   * @since 2.6
   */
  public boolean index(Resource resource)
  {
    return false;
  }

  /**
   * Indexes a resource. This method does nothing if the resource is already indexed.
   *
   * @param resource        the resource to index. Not nullable
   * @param parentReference a reference to the parent. If null, the the resource is indexed as a direct child of project.
   * @return false if the parent is not indexed or if the resource is excluded
   * @since 2.6
   */
  public boolean index(Resource resource, Resource parentReference)
  {
    return false;
  }

  /**
   * Returns true if the referenced resource is indexed and excluded.
   *
   * @since 2.6
   */
  public boolean isExcluded(Resource reference)
  {
    return false;
  }

  /**
   * Returns true if the referenced resource is indexed.
   *
   * @since 2.6
   */
  public boolean isIndexed(Resource reference, boolean acceptExcluded)
  {
    return false;
  }

  /**
   * Search for an indexed resource.
   *
   * @param reference the resource reference
   * @return the indexed resource, null if it's not indexed
   * @since 1.10. Generic types since 2.6.
   */
  public <R extends Resource> R getResource(R reference)
  {
    return reference;
  }

  /**
   * @since 2.6
   */
  public Resource getParent(Resource reference)
  {
    return null;
  }

  /**
   * @since 2.6
   */
  public Collection<Resource> getChildren(Resource reference)
  {
    return null;
  }

  /**
   * Find a project measure
   */
  public Measure getMeasure(Metric metric)
  {
    Measure result = new Measure(metric);

    result.setValue(0.0);
    return result;
  }

  /**
   * All measures of the project. Never return null.
   */
  public <M> M getMeasures(MeasuresFilter<M> filter)
  {
    return null;
  }

  /**
   * Add a measure on project
   */
  public Measure saveMeasure(Measure measure)
  {
    return measure;
  }

  /**
   * Add a measure on project
   */
  public Measure saveMeasure(Metric metric, Double value)
  {
    return null;
  }

  /**
   * Find a measure for this project
   */
  public Measure getMeasure(Resource resource, Metric metric)
  {
    return null;
  }

  /**
   * Key is updated when saving the resource.
   *
   * @return the key as saved in database. Null if the resource is set as excluded.
   * @deprecated use the methods index()
   */
  public String saveResource(Resource resource)
  {
    return null;
  }

  /**
   * Find all measures for this project. Never return null.
   */
  public <M> M getMeasures(Resource resource, MeasuresFilter<M> filter)
  {
    return null;
  }

  /**
   * Add or update a measure.
   * <p>
   * The resource is automatically saved, so there is no need to execute the method saveResource(). Does nothing if the resource is set as
   * excluded.
   * </p>
   */
  public Measure saveMeasure(Resource resource, Metric metric, Double value)
  {
    return null;
  }

  /**
   * Add or update a measure.
   * <p>
   * The resource is automatically saved, so there is no need to execute the method saveResource(). Does nothing if the resource is set as
   * excluded.
   * </p>
   */
  public Measure saveMeasure(Resource resource, Measure measure)
  {
    return null;
  }

  /**
   * Save a coding rule violation.
   *
   * @param force allows to force creation of violation even if it was supressed by {@link org.sonar.api.rules.ViolationFilter}
   * @since 2.5
   */
  public void saveViolation(Violation violation, boolean force)
  {
  }

  /**
   * Save a coding rule violation.
   */
  public void saveViolation(Violation violation)
  {
  }

  /**
   * Saves a list of violations.
   */
  public void saveViolations(Collection<Violation> violations)
  {
  }

  /**
   * Build a new dependency : from depends upon to. The dependency is NOT saved. The method saveDependency() must still be executed.
   */
  public Dependency saveDependency(Dependency dependency)
  {
    return null;
  }

  public Set<Dependency> getDependencies()
  {
    return null;
  }

  public Collection<Dependency> getIncomingDependencies(Resource to)
  {
    return null;
  }

  public Collection<Dependency> getOutgoingDependencies(Resource from)
  {
    return null;
  }

  /**
   * Save the source code of a file. The file must be have been indexed before.
   *
   * @throws org.sonar.api.resources.DuplicatedSourceException
   *          if the source has already been set on this resource
   * @since 1.10. Returns a boolean since 2.6.
   */
  public void saveSource(Resource reference, String source) throws DuplicatedSourceException
  {
  }

  /**
   * add a link to an external page like project homepage, sources (subversion, ...), continuous integration server... Example :
   * context.addLink(new ProjectLink("maven_site, "Maven site", "http://my.maven.com)
   */
  public void saveLink(ProjectLink link)
  {
  }

  /**
   * remove a link. It does not fail if key is unknown.
   */
  public void deleteLink(String key)
  {
  }

  /**
   * @param resource set null for project events
   */
  public List<Event> getEvents(Resource resource)
  {
    return null;
  }

  /**
   * Creates an event for a given date
   *
   * @param name        the event name
   * @param description the event description
   * @param category    the event category
   * @param date        the event date
   * @return the created event
   */
  public Event createEvent(Resource resource, String name, String description, String category, Date date)
  {
    return null;
  }

  /**
   * Deletes an event
   *
   * @param event the event to delete
   */
  public void deleteEvent(Event event)
  {
  }
}
