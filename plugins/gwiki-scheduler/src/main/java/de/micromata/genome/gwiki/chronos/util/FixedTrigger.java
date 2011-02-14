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
package de.micromata.genome.gwiki.chronos.util;

import java.text.ParseException;
import java.util.Date;

import de.micromata.genome.gwiki.chronos.JobCompletion;
import de.micromata.genome.gwiki.chronos.Scheduler;
import de.micromata.genome.gwiki.chronos.Trigger;
import de.micromata.genome.util.types.Converter;

/**
 * @author Roger Rene Kommer (r.kommer@micromata.de)
 * 
 */
public class FixedTrigger implements Trigger
{

  private Date nextFireTime;

  public FixedTrigger(Date nextFireTime)
  {
    this.nextFireTime = nextFireTime;
  }

  public FixedTrigger(String arg)
  {
    if (arg.startsWith("!") == true) {
      arg = arg.substring(1);
    }
    try {
      nextFireTime = Converter.isoDateFormat.get().parse(arg);
    } catch (ParseException ex) {
      throw new IllegalArgumentException("wrong date format format. expect format: "
          + Converter.isoDateFormat.get().toPattern()
          + "; given: "
          + arg);
    }
  }

  public Date getNextFireTime(Date now)
  {
    return nextFireTime;
  }

  public String getTriggerDefinition()
  {
    return "!" + Converter.isoDateFormat.get().format(nextFireTime);
  }

  public void setNextFireTime(Date nextFireTime)
  {
    this.nextFireTime = nextFireTime;
  }

  public Date updateAfterRun(final Scheduler scheduler, final JobCompletion cause)
  {
    switch (cause) {
      case JOB_COMPLETED:
      case EXCEPTION:
        nextFireTime = null;
        break;
      case EXPECTED_RETRY:
        nextFireTime = new Date(new Date().getTime() + scheduler.getJobRetryTime() * 1000);
        break;
      case SERVICE_UNAVAILABLE:
        nextFireTime = new Date(new Date().getTime() + scheduler.getServiceRetryTime() * 1000);
        break;
      case THREAD_POOL_EXHAUSTED:
        // ????
        break;
    }
    return nextFireTime == null ? null : new Date(nextFireTime.getTime());
  }

}
