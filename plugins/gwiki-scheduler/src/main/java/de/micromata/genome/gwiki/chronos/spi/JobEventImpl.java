/////////////////////////////////////////////////////////////////////////////
//
// $RCSfile: JobEventImpl.java,v $
//
// Project   genome
//
// Author    Wolfgang Jung (w.jung@micromata.de)
// Created   04.01.2007
// Copyright Micromata 04.01.2007
//
// $Id: JobEventImpl.java,v 1.3 2007/02/25 13:38:59 roger Exp $
// $Revision: 1.3 $
// $Date: 2007/02/25 13:38:59 $
//
/////////////////////////////////////////////////////////////////////////////
package de.micromata.genome.gwiki.chronos.spi;

import de.micromata.genome.gwiki.chronos.JobDefinition;
import de.micromata.genome.gwiki.chronos.JobEvent;
import de.micromata.genome.gwiki.chronos.Scheduler;
import de.micromata.genome.gwiki.chronos.State;
import de.micromata.genome.gwiki.chronos.spi.jdbc.JobResultDO;
import de.micromata.genome.gwiki.chronos.spi.jdbc.TriggerJobDO;

public class JobEventImpl implements JobEvent
{
  private TriggerJobDO job;

  private JobDefinition jobDefinition;

  private JobResultDO jobResult;

  private State jobStatus;

  private Scheduler scheduler;

  /**
   * @param job
   * @param jobDefinition
   * @param jobResult
   * @param jobStatus
   * @param runner
   * @param scheduler
   */
  public JobEventImpl(final TriggerJobDO job, final JobDefinition jobDefinition, final JobResultDO jobResult, final State jobStatus,
      final Scheduler scheduler)
  {
    this.job = job;
    this.jobDefinition = jobDefinition;
    this.jobResult = jobResult;
    this.jobStatus = jobStatus;

    this.scheduler = scheduler;
  }

  public TriggerJobDO getJob()
  {
    return job;
  }

  public JobDefinition getJobDefinition()
  {
    return jobDefinition;
  }

  public JobResultDO getJobResult()
  {
    return jobResult;
  }

  public State getJobStatus()
  {
    return jobStatus;
  }

  public Scheduler getScheduler()
  {
    return scheduler;
  }

}
