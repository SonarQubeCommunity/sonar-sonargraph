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
package com.hello2morrow.sonarplugin.foundation;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sonar.api.batch.fs.FilePredicate;
import org.sonar.api.batch.fs.FileSystem;
import org.sonar.api.batch.fs.InputDir;
import org.sonar.api.batch.fs.InputFile;
import org.sonar.api.batch.fs.InputPath;
import org.sonar.api.batch.fs.TextRange;
import org.sonar.api.batch.rule.ActiveRule;
import org.sonar.api.batch.sensor.SensorContext;
import org.sonar.api.batch.sensor.issue.NewIssue;
import org.sonar.api.batch.sensor.issue.NewIssueLocation;

import java.io.File;
import java.util.Collection;

public class SonarQubeUtilities {

  private static final Logger LOG = LoggerFactory.getLogger(SonarQubeUtilities.class);

  private static final String SOURCE_FILE_NOT_FOUND_MESSAGE = "Cannot obtain source file ";

  private SonarQubeUtilities() {
    // do not instantiate
  }

  public static void saveViolation(org.sonar.api.batch.sensor.SensorContext context, InputFile file, org.sonar.api.batch.rule.ActiveRule rule, String priority, int line, String msg) {
    NewIssue newIssue = context.newIssue();
    TextRange textRange = file.newRange(line, 0, line + 1, 0);
    NewIssueLocation location = newIssue.newLocation().on(file).at(textRange).message(msg);
    newIssue.at(location).forRule(rule.ruleKey());
    newIssue.save();
  }

  public static void saveViolation(org.sonar.api.batch.sensor.SensorContext context, InputDir dir, org.sonar.api.batch.rule.ActiveRule rule, String priority, String msg) {
    NewIssue newIssue = context.newIssue();
    NewIssueLocation location = newIssue.newLocation().on(dir).message(msg);
    newIssue.at(location).forRule(rule.ruleKey());
    newIssue.save();
  }

  public static InputPath getInputPath(FileSystem fileSystem, final String fqName) {
    final boolean isSourceFile = fqName.endsWith(".java");
    if (isSourceFile) {
      InputFile file = fileSystem.inputFile(new FilePredicate() {
        @Override
        public boolean apply(InputFile file) {
          return file.relativePath().endsWith(fqName);
        }
      });
      if (file == null) {
        LOG.error(SOURCE_FILE_NOT_FOUND_MESSAGE + fqName);
        return null;
      }
      return file;
    }

    File dir = new File(fileSystem.baseDir(), fqName);
    if (!dir.exists()) {
      return null;
    }
    return fileSystem.inputDir(dir);
  }

  public static ActiveRule findActiveSonargraphRule(SensorContext sensorContext, String ruleKey) {
    Collection<ActiveRule> rules = sensorContext.activeRules().findByRepository(SonargraphPluginBase.PLUGIN_KEY);
    ActiveRule rule = null;
    for (ActiveRule next : rules) {
      if (next.ruleKey().rule().equals(ruleKey)) {
        rule = next;
        break;
      }
    }
    return rule;
  }

}
