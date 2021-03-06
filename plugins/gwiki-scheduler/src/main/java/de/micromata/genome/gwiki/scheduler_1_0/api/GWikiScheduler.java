//
// Copyright (C) 2010-2016 Roger Rene Kommer & Micromata GmbH
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//  http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.
//

package de.micromata.genome.gwiki.scheduler_1_0.api;

import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import de.micromata.genome.chronos.ChronosServiceManager;
import de.micromata.genome.chronos.JobDefinition;
import de.micromata.genome.chronos.Trigger;
import de.micromata.genome.chronos.util.TriggerJobUtils;
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
    JobDefinition jobDefinition = new GWikiSchedClassJobDefinition(className, null);
    Trigger trigger = TriggerJobUtils.createTriggerDefinition(triggerDefintion);
    ChronosServiceManager.get().getSchedulerDAO().submit(scheduler, jobDefinition, PipeValueList.encode(args), trigger);
  }

}
