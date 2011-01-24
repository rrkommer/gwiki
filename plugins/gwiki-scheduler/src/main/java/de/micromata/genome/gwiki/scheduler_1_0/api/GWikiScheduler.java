////////////////////////////////////////////////////////////////////////////
// 
// Copyright (C) 2010 Micromata GmbH
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
package de.micromata.genome.gwiki.scheduler_1_0.api;

import java.util.Map;

import org.apache.commons.lang.StringUtils;

import de.micromata.genome.gwiki.chronos.JobDefinition;
import de.micromata.genome.gwiki.chronos.JobStore;
import de.micromata.genome.gwiki.chronos.StaticDaoManager;
import de.micromata.genome.gwiki.chronos.Trigger;
import de.micromata.genome.gwiki.chronos.util.TriggerJobUtils;
import de.micromata.genome.gwiki.scheduler_1_0.chronos.spi.GWikiSchedClassJobDefinition;
import de.micromata.genome.util.text.PipeValueList;

/**
 * @author Roger Rene Kommer (r.kommer@micromata.de)
 * 
 */
public class GWikiScheduler
{
  public static void submitJob(String scheduler, String className, String triggerDefintion, Map<String, String> args)
  {
    if (StringUtils.isBlank(scheduler) == true) {
      scheduler = "standard";
    }
    JobDefinition jobDefinition = new GWikiSchedClassJobDefinition(className);
    Trigger trigger = TriggerJobUtils.createTriggerDefinition(triggerDefintion);
    StaticDaoManager.get().getSchedulerManager().submit(scheduler, jobDefinition, PipeValueList.encode(args), trigger);
  }
  public static JobStore getJobStore()
  {
    return StaticDaoManager.get().getJobStore();
  }
  }
