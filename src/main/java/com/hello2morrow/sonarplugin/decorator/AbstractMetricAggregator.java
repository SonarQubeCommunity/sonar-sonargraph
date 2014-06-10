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

import com.hello2morrow.sonarplugin.foundation.Utilities;
import com.hello2morrow.sonarplugin.metric.SonargraphMetrics;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sonar.api.batch.AbstractSumChildrenDecorator;
import org.sonar.api.batch.DecoratorContext;
import org.sonar.api.batch.fs.FileSystem;
import org.sonar.api.profiles.RulesProfile;
import org.sonar.api.resources.Project;
import org.sonar.api.resources.Qualifiers;
import org.sonar.api.resources.Resource;

import java.util.Arrays;

public abstract class AbstractMetricAggregator extends AbstractSumChildrenDecorator {

  private static final Logger LOG = LoggerFactory.getLogger(AbstractMetricAggregator.class);
  private final RulesProfile profile;
  private final FileSystem moduleFileSystem;

  public AbstractMetricAggregator(RulesProfile profile, FileSystem moduleFileSystem) {
    super();
    this.profile = profile;
    this.moduleFileSystem = moduleFileSystem;
  }

  @Override
  public boolean shouldExecuteOnProject(Project project) {
    return Utilities.isSonargraphProject(project, moduleFileSystem, profile, SonargraphMetrics.getAll());
  }

  @Override
  protected final boolean shouldSaveZeroIfNoChildMeasures() {
    return true;
  }

  @Override
  public void decorate(Resource resource, DecoratorContext context) {
    if (!shouldDecorateResource(resource)) {
      return;
    }
    super.decorate(resource, context);

    AlertDecorator.setAlertLevels(new DecoratorProjectContext(context));
  }

  @Override
  public boolean shouldDecorateResource(Resource resource) {
    LOG.debug("Checking for resource type: " + resource.getQualifier());
    return Arrays.asList(Qualifiers.PROJECT, Qualifiers.MODULE, Qualifiers.VIEW, Qualifiers.SUBVIEW).contains(
      resource.getQualifier());
  }

}
