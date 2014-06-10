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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.Test;
import org.sonar.api.rules.Rule;
import org.sonar.api.rules.RuleRepository;

import com.hello2morrow.sonarplugin.foundation.JavaLanguage;
import com.hello2morrow.sonarplugin.foundation.SonargraphPluginBase;

public class SonargraphRulesRepositoryTest {

	@Test
	public void testCreateRules() {
		RuleRepository repository = new SonargraphRulesRepository();
		assertEquals(SonargraphPluginBase.PLUGIN_KEY, repository.getKey());
		assertEquals(JavaLanguage.INSTANCE.getKey(), repository.getLanguage());
		List<Rule> rules = repository.createRules();
		assertTrue(rules.contains(SonargraphRulesRepository.ARCH));
		assertTrue(rules.contains(SonargraphRulesRepository.THRESHOLD));
		assertTrue(rules.contains(SonargraphRulesRepository.TASK));
		assertTrue(rules.contains(SonargraphRulesRepository.CYCLE_GROUPS));
		assertTrue(rules.contains(SonargraphRulesRepository.WORKSPACE));
		assertTrue(rules.contains(SonargraphRulesRepository.DUPLICATES));

		assertEquals(6, rules.size());
	}
}
