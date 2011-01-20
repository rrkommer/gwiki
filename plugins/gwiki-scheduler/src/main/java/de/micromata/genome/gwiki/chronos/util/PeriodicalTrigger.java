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
import de.micromata.genome.gwiki.chronos.logging.GLog;
import de.micromata.genome.gwiki.chronos.logging.GenomeLogCategory;

public class PeriodicalTrigger implements Trigger
{
  private Date nextFireTime;

  private final Integer breakInSeconds;

  /**
   * A job triggered by this trigger will be repeated every x seconds untill it's status is set to "STOP", "FINISHED" or "CLOSED" manually
   * in the genome console.
   * 
   * @param arg: 'p' plus an integer, represents the number of seconds that the scheduler is supposed to wait till the next run is started.
   *          </br> </br> Example: "p3600" => 1 hour delay between the different runs
   */
  public PeriodicalTrigger(final String arg)
  {
    if (arg.startsWith("p") == false) {
      throw new IllegalArgumentException("wrong string format (prefiix 'p' is missing: " + arg);
    }
    breakInSeconds = Integer.parseInt(arg.substring(1));
    nextFireTime = new Date(System.currentTimeMillis() + breakInSeconds * 1000);
  }

  public String getTriggerDefinition()
  {
    return "p" + breakInSeconds.toString();
  }

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
    if (GLog.isTraceEnabled() == true)
      GLog.trace(GenomeLogCategory.Scheduler, "Update firetime to: " + nextFireTime + " after " + cause + " for " + this);
    return nextFireTime == null ? null : new Date(nextFireTime.getTime());
  }

  public Date getNextFireTime(final Date now)
  {
    return new Date(nextFireTime.getTime());
  }

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
