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
package com.hello2morrow.sonarplugin.measurecomputer;

import com.hello2morrow.sonarplugin.metric.internal.SonargraphInternalMetrics;
import org.sonar.api.ce.measure.Component;
import org.sonar.api.ce.measure.Measure;
import org.sonar.api.ce.measure.MeasureComputer;

import java.util.ArrayList;
import java.util.List;

abstract class SonargraphMeasureComputer implements MeasureComputer {

  public SonargraphMeasureComputer() {
    super();
  }

  @Override
  public final MeasureComputerDefinition define(final MeasureComputerDefinitionContext defContext) {
    final List<String> inputMetrics = new ArrayList<>(getInputMetrics());
    inputMetrics.add(SonargraphInternalMetrics.ROOT_PROJECT_TO_BE_PROCESSED.key());
    return defContext.newDefinitionBuilder().setInputMetrics(inputMetrics.toArray(new String[] {})).setOutputMetrics(getOutputMetrics().toArray(new String[] {})).build();
  }

  @Override
  public final void compute(final MeasureComputerContext context) {
    if (context.getComponent().getType() != Component.Type.PROJECT) {
      return;
    }

    if (!needsProcessing(context)) {
      return;
    }

    internalCompute(context);
  }

  abstract void internalCompute(MeasureComputerContext context);

  abstract List<String> getOutputMetrics();

  abstract List<String> getInputMetrics();

  private boolean needsProcessing(final MeasureComputerContext context) {
    final Measure measure = context.getMeasure(SonargraphInternalMetrics.ROOT_PROJECT_TO_BE_PROCESSED.key());
    return measure != null && measure.getBooleanValue();
  }

}
