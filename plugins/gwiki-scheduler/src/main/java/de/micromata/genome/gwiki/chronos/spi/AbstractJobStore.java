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
// Created   23.01.2008
// Copyright Micromata 23.01.2008
//
/////////////////////////////////////////////////////////////////////////////
package de.micromata.genome.gwiki.chronos.spi;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang.Validate;
import org.apache.commons.lang.exception.ExceptionUtils;

import de.micromata.genome.gwiki.chronos.JobDefinition;
import de.micromata.genome.gwiki.chronos.JobStore;
import de.micromata.genome.gwiki.chronos.Scheduler;
import de.micromata.genome.gwiki.chronos.ServiceUnavailableException;
import de.micromata.genome.gwiki.chronos.State;
import de.micromata.genome.gwiki.chronos.Trigger;
import de.micromata.genome.gwiki.chronos.spi.jdbc.JobResultDO;
import de.micromata.genome.gwiki.chronos.spi.jdbc.SchedulerDO;
import de.micromata.genome.gwiki.chronos.spi.jdbc.TriggerJobDO;
import de.micromata.genome.gwiki.chronos.spi.jdbc.TriggerJobDisplayDO;
import de.micromata.genome.util.types.Holder;

/**
 * A job store is asigned to a dispacher
 * 
 * @author roger
 * 
 */
public abstract class AbstractJobStore implements JobStore
{
  private DispatcherInternal dispatcher;

  public TriggerJobDO buildTriggerJob(final Scheduler scheduler, String jobName, final JobDefinition jobDefinition, final Object info,
      final Trigger trigger, final String hostName, State state)
  {
    final long schedulerId = scheduler.getId();
    Validate.isTrue(schedulerId != SchedulerDO.UNSAVED_SCHEDULER_ID, "Es wurde submit auf einen nicht persistenten Scheduler versucht.");
    long jobId = getNextJobId();
    TriggerJobDO job = new TriggerJobDO();
    job.setTrigger(trigger);
    job.setFireTime(trigger.getNextFireTime(new Date()));
    job.setJobName(jobName);
    job.setJobDefinition(jobDefinition);
    job.setJobArguments(info);
    job.setJobStore(this);
    job.setPk(jobId);
    job.setHostName(hostName);
    if (state == null)
      state = State.WAIT;
    job.setState(state);

    job.setScheduler(scheduler.getId());
    job.setSchedulerName(scheduler.getName());
    return job;
  }

  public TriggerJobDO buildTriggerJob(final Scheduler scheduler, final JobDefinition jobDefinition, final Object info,
      final Trigger trigger, final String hostName, State state)
  {

    return buildTriggerJob(scheduler, null, jobDefinition, info, trigger, hostName, state);
  }

  /**
   * Fügt den einen neuen Job für den Scheduler ein und persistiert ihn.
   * 
   * @see de.micromata.jchronos.JobStore#submit(de.micromata.jchronos.spi.Scheduler de.micromata.jchronos.JobDefinition, java.lang.Object,
   *      de.micromata.jchronos.Trigger)
   */
  public TriggerJobDO submit(final Scheduler scheduler, final JobDefinition jobDefinition, final Object info, final Trigger trigger,
      final String hostName, State state)
  {

    return submit(scheduler, null, jobDefinition, info, trigger, hostName, state);
  }

  /**
   * Fügt den einen neuen Job für den Scheduler ein und persistiert ihn.
   * 
   * @see de.micromata.jchronos.JobStore#submit(de.micromata.jchronos.spi. Scheduler de.micromata.jchronos.JobDefinition, java.lang.Object,
   *      de.micromata.jchronos.Trigger)
   */
  public TriggerJobDO submit(final Scheduler scheduler, String jobName, final JobDefinition jobDefinition, final Object info,
      final Trigger trigger, final String hostName, State state)
  {
    TriggerJobDO job = buildTriggerJob(scheduler, jobName, jobDefinition, info, trigger, hostName, state);
    insertJob(job);
    return job;
  }

  public void serviceRetry(final TriggerJobDO job, final JobResultDO jobResult, final ServiceUnavailableException ex,
      final Scheduler scheduler)
  {
    withinTransaction(new Runnable() {
      public void run()
      {
        job.setState(State.WAIT);
        job.setResult(jobResult);
        jobResult.setResultObject(ExceptionUtils.getFullStackTrace(ex));
        jobResult.setState(State.RETRY);
        updateJobWithResult(job);
      }
    });
  }

  public JobResultDO jobStarted(final TriggerJobDO job, final Scheduler scheduler)
  {
    final Holder<JobResultDO> ret = new Holder<JobResultDO>();

    withinTransaction(new Runnable() {
      public void run()
      {

        job.setState(State.RUN);
        DispatcherInternal disp = scheduler.getDispatcher();// Dispatcher.getInstance();

        job.setHostName(disp.getVirtualHostName());
        JobResultDO result = new JobResultDO();
        result.setExecutionStart(new Date());
        result.setHostName(disp.getVirtualHostName());
        // result.setVm(disp.getVm());
        updateJob(job);
        ret.set(result);
      }
    });
    return ret.get();
  }

  public void jobAborted(final TriggerJobDO job, final JobResultDO jobResult, final Throwable ex, final Scheduler scheduler)
  {
    withinTransaction(new Runnable() {
      public void run()
      {
        job.setState(State.STOP);
        job.setResult(jobResult);
        jobResult.setResultObject(ExceptionUtils.getFullStackTrace(ex));
        jobResult.setState(State.STOP);
        updateJobWithResult(job);

      }
    });
  }

  public void jobCompleted(final TriggerJobDO job, final JobResultDO jobResult, final Object result, final Scheduler scheduler,
      final Date nextRun)
  {

    withinTransaction(new Runnable() {
      public void run()
      {
        if (nextRun != null) {
          job.setFireTime(nextRun);
          job.setState(State.WAIT);
        } else {
          job.setState(State.FINISHED);
        }
        if (result == null) {
          if (nextRun == null && job.hasFailureResult() == false) {
            jobRemove(job, jobResult, scheduler);
          } else {
            updateJob(job);
          }
        } else {
          jobResult.setResultObject(result);
          (job).setResult(jobResult);
          updateJobWithResult(job);
        }

      }
    });
  }

  /**
   * Setzt die Daten für einem neuen Versuch und speichert den Job ab.
   * 
   * @see de.micromata.genome.gwiki.chronos.JobStore#jobRetry(de.micromata.genome.gwiki.chronos.spi.jdbc.TriggerJobDO,
   *      de.micromata.genome.gwiki.chronos.spi.jdbc.JobResultDO, de.micromata.genome.chronos.RetryException,
   *      de.micromata.genome.chronos.spi.Scheduler)
   */
  public void jobRetry(final TriggerJobDO job, final JobResultDO jobResult, final Exception ex, final Scheduler scheduler)
  {
    withinTransaction(new Runnable() {
      public void run()
      {
        job.setState(State.WAIT);
        job.setResult(jobResult);
        jobResult.setResultObject(ExceptionUtils.getFullStackTrace(ex));
        (jobResult).setState(State.RETRY);
        job.increaseRetryCount();
        updateJobWithResult(job);
      }
    });
  }

  /**
   * Speichert den Job ab und, wenn ungleich <code>null</code>, auch das {@link JobResultDO}.
   */
  public void updateJobWithResult(final TriggerJobDO job)
  {
    if (job.getResult() != null) {
      long resultPk = getNextJobResultId();
      job.getResult().setPk(resultPk);
      job.getResult().setJobPk(job.getPk());
      // job.getResult().setJobName(job.getJobName());
      if (job.getResult().getState() == null)
        job.getResult().setState(job.getState());
      insertResult(job.getResult());
      job.setCurrentResultPk(resultPk);
    }
    updateJob(job);
  }

  public DispatcherInternal getDispatcher()
  {
    return dispatcher;
  }

  public void setDispatcher(DispatcherInternal dispatcher)
  {
    this.dispatcher = dispatcher;
  }

  @SuppressWarnings("unchecked")
  public List<TriggerJobDisplayDO> getAdminJobs(String hostName, String state, String schedulerName, int resultCount)
  {
    return getAdminJobs(hostName, null, state, schedulerName, resultCount);
  }

  public List<String> getUniqueJobNames()
  {
    Set<String> jobNames = new HashSet<String>(getJobNames());
    return new ArrayList<String>(jobNames);
  }

}
