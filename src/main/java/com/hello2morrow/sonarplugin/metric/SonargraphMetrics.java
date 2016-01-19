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
package com.hello2morrow.sonarplugin.metric;

import org.sonar.api.measures.Metric;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class SonargraphMetrics {

  private static final SonargraphMetrics INSTANCE = new SonargraphMetrics();
  @SuppressWarnings("rawtypes")
  private final List<Metric> allMetrics;

  private SonargraphMetrics() {
    allMetrics = new ArrayList<>();
    allMetrics.addAll(new SonargraphDerivedMetrics().getMetrics());
    allMetrics.addAll(new SonargraphSimpleMetrics().getMetrics());
  }

  @SuppressWarnings("rawtypes")
  public static List<Metric> getAll() {
    return Collections.unmodifiableList(INSTANCE.allMetrics);
  }
}
