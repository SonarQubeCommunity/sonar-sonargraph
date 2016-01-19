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

package com.hello2morrow.sonarplugin.api;

import com.hello2morrow.sonarplugin.foundation.SonargraphPluginBase;
import com.hello2morrow.sonarplugin.metric.SonargraphDerivedMetrics;
import com.hello2morrow.sonarplugin.metric.SonargraphSimpleMetrics;
import com.hello2morrow.sonarplugin.metric.internal.SonargraphInternalMetrics;
import com.hello2morrow.sonarplugin.view.SonargraphArchitectureDashboard;
import com.hello2morrow.sonarplugin.view.SonargraphCyclicityDashboard;
import com.hello2morrow.sonarplugin.view.SonargraphStructuralDebtDashboard;
import org.sonar.api.Properties;
import org.sonar.api.Property;
import org.sonar.api.SonarPlugin;

import java.util.ArrayList;
import java.util.List;

/**
 * This class is the container for all others extensions
 */
@Properties({
  @Property(key = SonargraphPluginBase.CURRENCY, defaultValue = SonargraphPluginBase.CURRENCY_DEFAULT, name = "Currency", project = false, module = false, global = true),

  @Property(key = SonargraphPluginBase.COST_PER_INDEX_POINT, defaultValue = "" + SonargraphPluginBase.COST_PER_INDEX_POINT_DEFAULT,
    name = "Cost per metric point of 'Structural debt index' (0 means not displayed)", project = false, module = false, global = true),

  @Property(key = SonargraphPluginBase.REPORT_PATH, defaultValue = "", name = "Path of the Sonargraph report (empty means default value)", project = true, module = false,
    global = false)})
public final class SonargraphPlugin extends SonarPlugin {

  @SuppressWarnings("rawtypes")
  @Override
  public List getExtensions() {
    List<Class<?>> list = new ArrayList<Class<?>>();
    list.add(SonargraphRulesRepository.class);

    list.add(SonargraphSimpleMetrics.class);
    list.add(SonargraphDerivedMetrics.class);
    list.add(SonargraphInternalMetrics.class);
    list.add(SonargraphSensor.class);
    // list.add(SonargraphMetricAggregator.class);
    // list.add(SonargraphAggregatingModuleMetricAggregator.class);
    // list.add(SonargraphDerivedMetricsDecorator.class);
    // list.add(SonargraphSystemDashBoardDecorator.class);

    list.add(SonargraphStructuralDebtDashboard.class);
    list.add(SonargraphCyclicityDashboard.class);
    list.add(SonargraphArchitectureDashboard.class);
    return list;
  }

}
