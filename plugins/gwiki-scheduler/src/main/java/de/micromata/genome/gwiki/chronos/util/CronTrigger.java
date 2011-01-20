/////////////////////////////////////////////////////////////////////////////
//
// $RCSfile: CronTrigger.java,v $
//
// Project   genome
//
// Author    Wolfgang Jung (w.jung@micromata.de)
// Created   03.01.2007
// Copyright Micromata 03.01.2007
//
// $Id: CronTrigger.java,v 1.4 2007/03/09 07:25:11 roger Exp $
// $Revision: 1.4 $
// $Date: 2007/03/09 07:25:11 $
//
/////////////////////////////////////////////////////////////////////////////
package de.micromata.genome.gwiki.chronos.util;

import java.util.Date;

import de.micromata.genome.gwiki.chronos.JobCompletion;
import de.micromata.genome.gwiki.chronos.Scheduler;
import de.micromata.genome.gwiki.chronos.Trigger;
import de.micromata.genome.gwiki.chronos.logging.GLog;
import de.micromata.genome.gwiki.chronos.logging.GenomeLogCategory;
import de.micromata.genome.gwiki.chronos.spi.CronExpression;

/**
 * @see CronExpression
 * @author roger
 * 
 */
public class CronTrigger implements Trigger
{
  private Date nextFireTime;

  private final CronExpression cronExpression;

  public CronTrigger(final String arg)
  {
    cronExpression = new CronExpression(arg);
  }

  public String getTriggerDefinition()
  {
    return cronExpression.toString();
  }

  public Date updateAfterRun(final Scheduler scheduler, final JobCompletion cause)
  {
    switch (cause) {
      case JOB_COMPLETED:
      case EXCEPTION:
        nextFireTime = calculateNext();
        break;
      case EXPECTED_RETRY: {
        Date rd = new Date(new Date().getTime() + scheduler.getJobRetryTime() * 1000);
        Date nf = calculateNext();
        long nt = Math.min(rd.getTime(), nf.getTime());
        nextFireTime = new Date(nt);
        break;
      }
      case SERVICE_UNAVAILABLE:
        Date rd = new Date(new Date().getTime() + scheduler.getServiceRetryTime() * 1000);
        Date nf = calculateNext();
        long nt = Math.min(rd.getTime(), nf.getTime());
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

  private Date calculateNext()
  {
    if (nextFireTime == null)
      nextFireTime = new Date();
    nextFireTime = cronExpression.getNextFireTime(nextFireTime);
    return nextFireTime;
  }

  public Date getNextFireTime(final Date now)
  {
    return cronExpression.getNextFireTime(now);
  }

  public void setNextFireTime(Date nextFireTime)
  {
    this.nextFireTime = nextFireTime;
  }

  @Override
  public String toString()
  {
    return "CronTrigger[" + getTriggerDefinition() + "] firing at " + nextFireTime;
  }
}
