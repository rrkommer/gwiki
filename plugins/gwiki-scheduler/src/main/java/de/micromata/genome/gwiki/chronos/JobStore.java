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
// $RCSfile: JobStore.java,v $
//
// Project   chronos
//
// Author    Roger Rene Kommer, Wolfgang Jung (w.jung@micromata.de)
// Created   26.12.2006
// Copyright Micromata 26.12.2006
//
// $Id: JobStore.java,v 1.11 2007/03/09 07:25:10 roger Exp $
// $Revision: 1.11 $
// $Date: 2007/03/09 07:25:10 $
//
/////////////////////////////////////////////////////////////////////////////
package de.micromata.genome.gwiki.chronos;

import java.util.Date;
import java.util.List;

import de.micromata.genome.gwiki.chronos.spi.DispatcherInternal;
import de.micromata.genome.gwiki.chronos.spi.jdbc.JobResultDO;
import de.micromata.genome.gwiki.chronos.spi.jdbc.SchedulerDO;
import de.micromata.genome.gwiki.chronos.spi.jdbc.SchedulerDisplayDO;
import de.micromata.genome.gwiki.chronos.spi.jdbc.TriggerJobDO;
import de.micromata.genome.gwiki.chronos.spi.jdbc.TriggerJobDisplayDO;

/**
 * Zentrales Interface für die zu speichernden Jobs. Jeder JobStore muss zu Beginn der Applikation einmalig initialisert
 * werden damit der {@link DispatcherInternal} seine Arbeit verrichten kann.
 * 
 * @author Roger Rene Kommer, Wolfgang Jung (w.jung@micromata.de)
 * 
 */
public interface JobStore
{
  // @Deprecated
  // public void setPrefix(String prefix);
  //
  // @Deprecated
  // public String getPrefix();
  public DispatcherInternal getDispatcher();

  public void setDispatcher(DispatcherInternal dispatcher);

  public List<SchedulerDO> getSchedulers();

  /**
   * Liefert eine Sequenz fuer eine JobID (PK)
   * 
   * @return
   */
  public long getNextJobId();

  /**
   * Liefert eine Sequenz fuer eine Scheduler (PK)
   * 
   * @return
   */
  public long getNextSchedulerId();

  public long getNextJobResultId();

  /**
   * Build from the parameters a new TriggerJobDO, but does not safe the the job.
   * 
   * The PK of the job will already be set, although the job itself is not inserted into db
   *
   * @param scheduler the scheduler
   * @param jobDefinition the job definition
   * @param info the info
   * @param trigger the trigger
   * @param hostName the host name
   * @param state the state
   * @return the trigger job do
   */
  public TriggerJobDO buildTriggerJob(final Scheduler scheduler, final JobDefinition jobDefinition, final Object info,
      final Trigger trigger, final String hostName, State state);

  /**
   * Build from the parameters a new TriggerJobDO, but does not safe the the job.
   * 
   * The PK of the job will already be set, although the job itself is not inserted into db
   *
   * @param scheduler the scheduler
   * @param jobName the job name
   * @param jobDefinition the job definition
   * @param info the info
   * @param trigger the trigger
   * @param hostName the host name
   * @param state the state
   * @return the trigger job do
   */
  public TriggerJobDO buildTriggerJob(final Scheduler scheduler, String jobName, final JobDefinition jobDefinition,
      final Object info,
      final Trigger trigger, final String hostName, State state);

  /**
   * Insert job.
   *
   * @param job the job
   */
  public void insertJob(TriggerJobDO job);

  /**
   * Update job.
   *
   * @param job the job
   */
  public void updateJob(TriggerJobDO job);

  /**
   * Update job with result.
   *
   * @param job the job
   */
  public void updateJobWithResult(TriggerJobDO job);

  /**
   * Insert result.
   *
   * @param result the result
   */
  public void insertResult(JobResultDO result);

  /**
   * Gibt maximal {@link Scheduler} Jobs des gegebenen Schedulers zurück, die im State Wait sind.
   *
   * @param scheduler the scheduler
   * @param foreignJobs Sollen auch Jobs, mit einer anderen Node gesucht werden?
   * @return the next jobs
   */
  public List<TriggerJobDO> getNextJobs(Scheduler scheduler, boolean foreignJobs);

  /**
   * Alternative implementation for getting jobs to run for all schedulers.
   *
   * @param minNodeBindTimeout (or backward) in milliseconds relativ to nextFireTime
   * @return TODO foreignJobs not used
   */
  public List<TriggerJobDO> getNextJobs(long minNodeBindTimeout);

  /**
   * Gibt alle Jobs mit dem Status zurueck.
   *
   * @param scheduler kann null sein.
   * @param fromDate bezieht sich auf modifiedat kann null sein
   * @param untilDate bezieht sich auf modifiedat kann null sein
   * @param state bezieht sich auf modifiedat kann null sein
   * @return the jobs
   */
  public List<TriggerJobDO> getJobs(final Scheduler scheduler, final Date fromDate, final Date untilDate,
      final State state);

  /**
   * Gibt einen Scheduler aus der Datenbank zurück.
   * <p>
   * Ist er noch nicht existent, so wird er erzeugt und ein vorbefülltes DO zurück gegeben.
   * </p>
   *
   * @param schedulerName the scheduler name
   * @return the scheduler do
   */
  public SchedulerDO createOrGetScheduler(String schedulerName);

  /**
   * Fuegt einen Job ein.
   *
   * @param scheduler the scheduler
   * @param executor the executor
   * @param info the info
   * @param trigger the trigger
   * @param hostName the host name
   * @param state the state
   * @return the trigger job do
   */
  public TriggerJobDO submit(Scheduler scheduler, JobDefinition executor, Object info, Trigger trigger, String hostName,
      State state);

  /**
   * Submit.
   *
   * @param scheduler the scheduler
   * @param jobName the job name
   * @param executor the executor
   * @param info the info
   * @param trigger the trigger
   * @param hostName the host name
   * @param state the state
   * @return the trigger job do
   */
  public TriggerJobDO submit(Scheduler scheduler, String jobName, JobDefinition executor, Object info, Trigger trigger,
      String hostName,
      State state);

  // public int getThreadPoolSize(Scheduler scheduler);

  /**
   * Service retry.
   *
   * @param job the job
   * @param resultInfo the result info
   * @param ex the ex
   * @param Scheduler the scheduler
   */
  public void serviceRetry(TriggerJobDO job, JobResultDO resultInfo, ServiceUnavailableException ex,
      Scheduler Scheduler);

  /**
   * Job started.
   *
   * @param job the job
   * @param scheduler the scheduler
   * @return the job result do
   */
  public JobResultDO jobStarted(TriggerJobDO job, Scheduler scheduler);

  /**
   * Job retry.
   *
   * @param job the job
   * @param resultInfo the result info
   * @param ex the ex
   * @param Scheduler the scheduler
   */
  public void jobRetry(TriggerJobDO job, JobResultDO resultInfo, Exception ex, Scheduler Scheduler);

  /**
   * Job completed.
   *
   * @param job the job
   * @param jobResult the job result
   * @param result the result
   * @param scheduler the scheduler
   * @param nextRun the next run
   */
  public void jobCompleted(TriggerJobDO job, JobResultDO jobResult, Object result, Scheduler scheduler, Date nextRun);

  /**
   * Aborts job. Set Jobresult and state State.STOP
   *
   * @param job the job
   * @param jobResult the job result
   * @param ex the ex
   * @param scheduler the scheduler
   */
  public void jobAborted(TriggerJobDO job, JobResultDO jobResult, Throwable ex, Scheduler scheduler);

  /**
   * Loescht job, aber nur wenn kein JobResult vorhanden ist.
   *
   * @param job the job
   * @param jobResult may be null
   * @param scheduler the scheduler
   */
  public void jobRemove(TriggerJobDO job, JobResultDO jobResult, Scheduler scheduler);

  /**
   * Job result remove.
   *
   * @param job the job
   * @param jobResult the job result
   * @param scheduler the scheduler
   */
  public void jobResultRemove(TriggerJobDO job, JobResultDO jobResult, Scheduler scheduler);

  /**
   * Gets the results.
   *
   * @param impl the impl
   * @param maxResults the max results
   * @return the results
   */
  public List<JobResultDO> getResults(TriggerJobDO impl, int maxResults);

  /**
   * Shutdown.
   *
   * @throws InterruptedException the interrupted exception
   */
  public void shutdown() throws InterruptedException;

  // public boolean setInactiveTriggerActive(Trigger trigger, Job job);
  /**
   * Versucht den Job zu reservieren.
   * <p>
   * Liefert den Job und setzt ihn auf WAIT auf ACTIVE. Dies muss eine atomare Operation sein.
   * </p>
   *
   * @param job the job
   * @return the trigger job do
   */
  public TriggerJobDO reserveJob(TriggerJobDO job);

  /**
   * Persist.
   *
   * @param scheduler the scheduler
   */
  public void persist(SchedulerDO scheduler);

  /**
   * Within transaction.
   *
   * @param runnable the runnable
   */
  public void withinTransaction(final Runnable runnable);

  /**
   * mapping getAdminJobById.
   *
   * @param pk the pk
   * @return the admin job by pk
   */
  public TriggerJobDO getAdminJobByPk(long pk);

  /**
   * getJob.
   *
   * @param pk the pk
   * @return the job by pk
   */
  public TriggerJobDO getJobByPk(long pk);

  /**
   * ibatis: setJobState
   * 
   * Update the state of given Job.
   *
   * @param pk the pk
   * @param newState the new state
   * @param oldState the old state
   * @return 0 if stored state is not oldState
   */
  public int setJobState(long pk, String newState, String oldState);

  /**
   * Gets the admin jobs.
   *
   * @param hostName may be null
   * @param jobName the job name
   * @param state may be null. In this case all Jobs with state != 'CLOSED' are returned
   * @param schedulerName may be null
   * @param resultCount the result count
   * @return the admin jobs
   */
  public List<TriggerJobDisplayDO> getAdminJobs(String hostName, String jobName, String state, String schedulerName,
      int resultCount);

  /**
   * Gets the admin jobs.
   *
   * @param hostName may be null
   * @param state may be null. In this case all Jobs with state != 'CLOSED' are returned
   * @param schedulerName may be null
   * @param resultCount the result count
   * @return the admin jobs
   */
  public List<TriggerJobDisplayDO> getAdminJobs(String hostName, String state, String schedulerName, int resultCount);

  /**
   * ibatis: getAdminJobResults.
   *
   * @param jobId the job id
   * @return the results for job
   */
  public List<JobResultDO> getResultsForJob(long jobId);

  /**
   * Return the job result for given pk.
   *
   * @param resultId the result id
   * @return the result by pk
   */
  public JobResultDO getResultByPk(long resultId);

  public List<SchedulerDisplayDO> getAdminSchedulers();

  /**
   * Return number of jobs with given state.
   *
   * @param state if state == null, return all
   * @return the job count
   */
  public long getJobCount(State state);

  /**
   * Gets the job result count.
   *
   * @param state the state
   * @return the job result count
   */
  public long getJobResultCount(State state);

  /**
   * Löscht den angegebenen Scheduler.
   *
   * @param pk the pk
   */
  public void deleteScheduler(Long pk);

  public List<String> getJobNames();

  public List<String> getUniqueJobNames();
}
