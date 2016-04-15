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
// $RCSfile: JobEventImpl.java,v $
//
// Project   genome
//
// Author    Roger Rene Kommer, Wolfgang Jung (w.jung@micromata.de)
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
   * Instantiates a new job event impl.
   *
   * @param job the job
   * @param jobDefinition the job definition
   * @param jobResult the job result
   * @param jobStatus the job status
   * @param scheduler the scheduler
   */
  public JobEventImpl(final TriggerJobDO job, final JobDefinition jobDefinition, final JobResultDO jobResult,
      final State jobStatus,
      final Scheduler scheduler)
  {
    this.job = job;
    this.jobDefinition = jobDefinition;
    this.jobResult = jobResult;
    this.jobStatus = jobStatus;

    this.scheduler = scheduler;
  }

  @Override
  public TriggerJobDO getJob()
  {
    return job;
  }

  @Override
  public JobDefinition getJobDefinition()
  {
    return jobDefinition;
  }

  @Override
  public JobResultDO getJobResult()
  {
    return jobResult;
  }

  @Override
  public State getJobStatus()
  {
    return jobStatus;
  }

  @Override
  public Scheduler getScheduler()
  {
    return scheduler;
  }

}
