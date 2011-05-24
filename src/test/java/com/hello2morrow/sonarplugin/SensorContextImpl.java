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
  public boolean index(Resource resource)
  {
    return false;
  }

  public boolean index(Resource resource, Resource parentReference)
  {
    return false;
  }

  public boolean isExcluded(Resource reference)
  {
    return false;
  }

  public boolean isIndexed(Resource reference, boolean acceptExcluded)
  {
    return false;
  }

  public <R extends Resource> R getResource(R reference)
  {
    return reference;
  }

  public Resource getParent(Resource reference)
  {
    return null;
  }

  public Collection<Resource> getChildren(Resource reference)
  {
    return null;
  }

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

  public Measure saveMeasure(Measure measure)
  {
    return measure;
  }

  public Measure saveMeasure(Metric metric, Double value)
  {
    return null;
  }

  public Measure getMeasure(Resource resource, Metric metric)
  {
    return null;
  }

  public String saveResource(Resource resource)
  {
    return null;
  }

  public <M> M getMeasures(Resource resource, MeasuresFilter<M> filter)
  {
    return null;
  }

  public Measure saveMeasure(Resource resource, Metric metric, Double value)
  {
    return null;
  }

  public Measure saveMeasure(Resource resource, Measure measure)
  {
    return null;
  }

  public void saveViolation(Violation violation, boolean force)
  {
  }

  public void saveViolation(Violation violation)
  {
  }

  public void saveViolations(Collection<Violation> violations)
  {
  }

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

  public void saveSource(Resource reference, String source) throws DuplicatedSourceException
  {
  }

  public void saveLink(ProjectLink link)
  {
  }

  public void deleteLink(String key)
  {
  }

  public List<Event> getEvents(Resource resource)
  {
    return null;
  }

  public Event createEvent(Resource resource, String name, String description, String category, Date date)
  {
    return null;
  }

  public void deleteEvent(Event event)
  {
  }
}
