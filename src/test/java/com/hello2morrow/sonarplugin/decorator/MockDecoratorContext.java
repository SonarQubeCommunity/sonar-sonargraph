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
package com.hello2morrow.sonarplugin.decorator;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.sonar.api.batch.DecoratorContext;
import org.sonar.api.batch.Event;
import org.sonar.api.design.Dependency;
import org.sonar.api.measures.Measure;
import org.sonar.api.measures.MeasuresFilter;
import org.sonar.api.measures.Metric;
import org.sonar.api.resources.Project;
import org.sonar.api.resources.Resource;
import org.sonar.api.rules.Violation;
import org.sonar.api.violations.ViolationQuery;

import com.hello2morrow.sonarplugin.metric.SonargraphSimpleMetrics;


public class MockDecoratorContext implements DecoratorContext {

  private Map<String, Measure> measures = new HashMap<String, Measure>();
  private List<DecoratorContext> children;
  private Project project;
  
  public MockDecoratorContext(Project project, List<DecoratorContext> children) {
    this.project = project;
    this.children = children;
  }
  
  public Project getProject() {
    return this.project;
  }

  @SuppressWarnings("rawtypes")
  public Resource getResource() {
    return null;
  }

  public List<DecoratorContext> getChildren() {
    return children;
  }

  public Measure getMeasure(Metric metric) {
    return measures.get(metric.getKey());
  }

  public <M> M getMeasures(MeasuresFilter<M> filter) {
    return null;
  }

  public Collection<Measure> getChildrenMeasures(@SuppressWarnings("rawtypes") MeasuresFilter filter) {
    return null;
  }

  public Collection<Measure> getChildrenMeasures(Metric metric) {
    if (metric.equals(SonargraphSimpleMetrics.INSTRUCTIONS))
    {
      Collection<Measure> indicators = new ArrayList<Measure>(1);
      indicators.add(new Measure(SonargraphSimpleMetrics.INSTRUCTIONS, 250.0));
      return indicators;
    }
    return null;
  }

  public DecoratorContext saveMeasure(Measure measure) {
    measures.put(measure.getMetricKey(), measure);
    return this;
  }

  public DecoratorContext saveMeasure(Metric metric, Double value) {
    measures.put(metric.getKey(), new Measure(metric, value));
    return this;
  }

  public Dependency saveDependency(Dependency dependency) {
    return null;
  }

  public Set<Dependency> getDependencies() {
    return null;
  }

  public Collection<Dependency> getIncomingDependencies() {
    return null;
  }

  public Collection<Dependency> getOutgoingDependencies() {
    return null;
  }

  public List<Violation> getViolations(ViolationQuery violationQuery) {
    return null;
  }

  public List<Violation> getViolations() {
    return null;
  }

  public DecoratorContext saveViolation(Violation violation, boolean force) {
    return null;
  }

  public DecoratorContext saveViolation(Violation violation) {
    return null;
  }

  public List<Event> getEvents() {
    return null;
  }

  public Event createEvent(String name, String description, String category, Date date) {
    return null;
  }

  public void deleteEvent(Event event) {
  }

}
