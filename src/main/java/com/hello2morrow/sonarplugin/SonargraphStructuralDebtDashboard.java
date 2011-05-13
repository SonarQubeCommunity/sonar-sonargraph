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

import org.sonar.api.web.AbstractRubyTemplate;
import org.sonar.api.web.Description;
import org.sonar.api.web.RubyRailsWidget;

@Description("Reports metrics on the structural debt of the project.")
public final class SonargraphStructuralDebtDashboard extends AbstractRubyTemplate implements RubyRailsWidget
{
  public String getId()
  {
    return "sonargraph.structural_debt";
  }

  public String getTitle()
  {
    return "Sonargraph Structural Debt Dashbox";
  }

  protected String getTemplatePath()
  {
    return "/sonargraph_debt_widget.html.erb";
  }
}