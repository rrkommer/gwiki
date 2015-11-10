////////////////////////////////////////////////////////////////////////////
// 
// Copyright (C) 2010-2013 Micromata GmbH / Roger Rene Kommer
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
// http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.
// 
////////////////////////////////////////////////////////////////////////////

/////////////////////////////////////////////////////////////////////////////
//
// Project   Micromata Genome Core
//
// Author    roger@micromata.de
// Created   24.01.2008
// Copyright Micromata 24.01.2008
//
/////////////////////////////////////////////////////////////////////////////
package de.micromata.genome.gwiki.chronos.util;

import de.micromata.genome.gwiki.chronos.Trigger;

/**
 * Collection of utils to create Trigger.
 * 
 * @author roger@micromata.de
 * 
 */
public class TriggerJobUtils
{
  public static Trigger createTriggerDefinition(final String definition)
  {
    if (definition.startsWith("+")) {
      return new DelayTrigger(definition);
    } else if (definition.startsWith("!")) {
      return new FixedTrigger(definition);
    } else if (definition.startsWith("p")) {
      return new PeriodicalTrigger(definition);
    } else {
      return new CronTrigger(definition);
    }
  }
}
