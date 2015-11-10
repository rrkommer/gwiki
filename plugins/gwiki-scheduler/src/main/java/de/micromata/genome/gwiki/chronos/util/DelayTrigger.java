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
// $RCSfile: DelayTrigger.java,v $
//
// Project   genome
//
// Author    Wolfgang Jung (w.jung@micromata.de)
// Created   03.01.2007
// Copyright Micromata 03.01.2007
//
// $Id: DelayTrigger.java,v 1.7 2007/03/20 18:54:30 noodles Exp $
// $Revision: 1.7 $
// $Date: 2007/03/20 18:54:30 $
//
/////////////////////////////////////////////////////////////////////////////
package de.micromata.genome.gwiki.chronos.util;

import java.util.Date;

import org.apache.commons.lang.StringUtils;

import de.micromata.genome.gwiki.chronos.JobCompletion;
import de.micromata.genome.gwiki.chronos.Scheduler;
import de.micromata.genome.gwiki.chronos.Trigger;
import de.micromata.genome.gwiki.chronos.logging.GLog;
import de.micromata.genome.gwiki.chronos.logging.GenomeLogCategory;

public class DelayTrigger implements Trigger
{
  private Date nextFireTime;

  private final long millis;

  public DelayTrigger(final long millisToWait)
  {
    millis = millisToWait;
    nextFireTime = new Date(System.currentTimeMillis() + millisToWait);
  }

  public DelayTrigger(final String arg)
  {
    if (arg.startsWith("+") == false) {
      throw new IllegalArgumentException("wrong string format (prefiix '+' is missing: " + arg);
    }
    millis = Long.parseLong(StringUtils.trim(arg.substring(1)));
    nextFireTime = new Date(System.currentTimeMillis() + millis);
  }

  public String getTriggerDefinition()
  {
    return "+" + millis;
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
    if (GLog.isTraceEnabled() == true)
      GLog.trace(GenomeLogCategory.Scheduler, "Update firetime to: " + nextFireTime + " after " + cause + " for " + this);
    return nextFireTime == null ? null : new Date(nextFireTime.getTime());
  }

  public long getTriggerDelay()
  {
    return millis;
  }

  public Date getNextFireTime(final Date now)
  {
    return nextFireTime == null ? null : new Date(nextFireTime.getTime());
  }

  public void setNextFireTime(Date nextFireTime)
  {
    this.nextFireTime = nextFireTime;
  }

  @Override
  public String toString()
  {
    return "DelayTrigger[" + getTriggerDefinition() + "] firing at " + nextFireTime;
  }
}
