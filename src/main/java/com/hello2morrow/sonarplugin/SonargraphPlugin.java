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

import java.util.ArrayList;
import java.util.List;

import org.sonar.api.Extension;
import org.sonar.api.Properties;
import org.sonar.api.Property;
import org.sonar.api.SonarPlugin;

import com.hello2morrow.sonarplugin.decorator.SonargraphMetricAggregator;
import com.hello2morrow.sonarplugin.view.SonargraphArchitectureDashboard;
import com.hello2morrow.sonarplugin.view.SonargraphCyclicityDashboard;
import com.hello2morrow.sonarplugin.view.SonargraphStructuralDebtDashboard;

/**
 * This class is the container for all others extensions
 */
@Properties({@Property(
    key = SonargraphPluginBase.CURRENCY,
    defaultValue = SonargraphPluginBase.CURRENCY_DEFAULT,
    name = "Currency",
    project = false,
    module = false,
    global = true),

    @Property(
        key = SonargraphPluginBase.COST_PER_INDEX_POINT,
        defaultValue = "" + SonargraphPluginBase.COST_PER_INDEX_POINT_DEFAULT,
        name = "Cost per metric point of 'Structural debt index' (0 means not displayed)",
        project = false,
        module = false,
        global = true)})

public final class SonargraphPlugin extends SonarPlugin
{
  
  public List<Class<? extends Extension>> getExtensions()
  {
    List<Class<? extends Extension>> list = new ArrayList<Class<? extends Extension>>();

    list.add(SonargraphMetrics.class);
    list.add(SonargraphSensor.class);
    list.add(SonargraphRulesRepository.class);
    list.add(SonargraphStructuralDebtDashboard.class);
    list.add(SonargraphCyclicityDashboard.class);
    list.add(SonargraphArchitectureDashboard.class);
    list.add(SonargraphMetricAggregator.class);
    return list;
  }
}
