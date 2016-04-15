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

/////////////////////////////////////////////////////////////////////////////
//
// $RCSfile: PeriodicalTrigger.java, v1.0
//
// Project   genome-core
//
// Author    Alexander Fröhlich (a.froehlich@micromata.de)
// Created   14.08.2009
// Copyright Micromata 14.08.2009
//
// $Id: PeriodicalTrigger.java, v1.0 14.08.2009
// $Revision: 1.0
// $Date: 14.08.2009
//
/////////////////////////////////////////////////////////////////////////////
package de.micromata.genome.gwiki.chronos.util;

import java.util.Date;

import de.micromata.genome.gwiki.chronos.JobCompletion;
import de.micromata.genome.gwiki.chronos.Scheduler;
import de.micromata.genome.gwiki.chronos.Trigger;
import de.micromata.genome.logging.GLog;
import de.micromata.genome.logging.GenomeLogCategory;

public class PeriodicalTrigger implements Trigger
{
  private Date nextFireTime;

  private final Integer breakInSeconds;

  public PeriodicalTrigger(final String arg)
  {
    if (arg.startsWith("p") == false) {
      throw new IllegalArgumentException("wrong string format (prefiix 'p' is missing: " + arg);
    }
    breakInSeconds = Integer.parseInt(arg.substring(1));
    nextFireTime = new Date(System.currentTimeMillis() + breakInSeconds * 1000);
  }

  @Override
  public String getTriggerDefinition()
  {
    return "p" + breakInSeconds.toString();
  }

  @Override
  public Date updateAfterRun(final Scheduler scheduler, final JobCompletion cause)
  {
    switch (cause) {
      case JOB_COMPLETED:
      case EXCEPTION:
        nextFireTime = new Date(System.currentTimeMillis() + breakInSeconds * 1000);
        break;
      case EXPECTED_RETRY: {
        final Date rd = new Date(new Date().getTime() + scheduler.getJobRetryTime() * 1000);
        final Date nf = new Date(System.currentTimeMillis() + breakInSeconds * 1000);
        final long nt = Math.min(rd.getTime(), nf.getTime());
        nextFireTime = new Date(nt);
        break;
      }
      case SERVICE_UNAVAILABLE:
        final Date rd = new Date(new Date().getTime() + scheduler.getServiceRetryTime() * 1000);
        final Date nf = new Date(System.currentTimeMillis() + breakInSeconds * 1000);
        final long nt = Math.min(rd.getTime(), nf.getTime());
        nextFireTime = new Date(nt);
        break;
      case THREAD_POOL_EXHAUSTED:
        // ????
        break;
    }
    if (GLog.isTraceEnabled() == true) {
      GLog.trace(GenomeLogCategory.Scheduler,
          "Update firetime to: " + nextFireTime + " after " + cause + " for " + this);
    }
    return nextFireTime == null ? null : new Date(nextFireTime.getTime());
  }

  @Override
  public Date getNextFireTime(final Date now)
  {
    return new Date(nextFireTime.getTime());
  }

  @Override
  public void setNextFireTime(final Date nextFireTime)
  {
    this.nextFireTime = nextFireTime;
  }

  @Override
  public String toString()
  {
    return "PeriodicalTrigger[" + getTriggerDefinition() + "] firing at " + nextFireTime;
  }
}
