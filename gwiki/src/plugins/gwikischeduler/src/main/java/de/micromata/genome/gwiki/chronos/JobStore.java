/////////////////////////////////////////////////////////////////////////////
//
// $RCSfile: JobStore.java,v $
//
// Project   chronos
//
// Author    Wolfgang Jung (w.jung@micromata.de)
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
 * Zentrales Interface für die zu speichernden Jobs. Jeder JobStore muss zu Beginn der Applikation einmalig initialisert werden damit der
 * {@link DispatcherInternal} seine Arbeit verrichten kann.
 * 
 * @author Roger Rene Kommer, Wolfgang Jung (w.jung@micromata.de)
 * 
 */
public interface JobStore
{
  /**
   * Prefix für die Job/Schedulernamen um mehrerer JobStores auf einer gemeinsamen betreiben zu können.
   * 
   * @param prefix Der Prefix, muss nicht notwendigerweise gesetzt sein. In einer Mehrbenutzerentwicklerdatenbank sollte dies der User sein.
   */
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
   * @param scheduler
   * @param jobDefinition
   * @param info
   * @param trigger
   * @param hostName
   * @param state
   * @return
   */
  public TriggerJobDO buildTriggerJob(final Scheduler scheduler, final JobDefinition jobDefinition, final Object info,
      final Trigger trigger, final String hostName, State state);

  /**
   * Build from the parameters a new TriggerJobDO, but does not safe the the job.
   * 
   * The PK of the job will already be set, although the job itself is not inserted into db
   * 
   * @param scheduler
   * @param jobName
   * @param jobDefinition
   * @param info
   * @param trigger
   * @param hostName
   * @param state
   * @return
   */
  public TriggerJobDO buildTriggerJob(final Scheduler scheduler, String jobName, final JobDefinition jobDefinition, final Object info,
      final Trigger trigger, final String hostName, State state);


  public void insertJob(TriggerJobDO job);

  public void updateJob(TriggerJobDO job);

  public void updateJobWithResult(TriggerJobDO job);

  public void insertResult(JobResultDO result);

  /**
   * Gibt maximal {@link Scheduler} Jobs des gegebenen Schedulers zurück, die im State Wait sind
   * 
   * @param scheduler
   * @param fromDate
   * @param foreignJobs Sollen auch Jobs, mit einer anderen Node gesucht werden?
   * @return
   */
  public List<TriggerJobDO> getNextJobs(Scheduler scheduler, boolean foreignJobs);

  /**
   * Alternative implementation for getting jobs to run for all schedulers
   * 
   * @param foreignJobs check for jobs
   * @param minNodeBindTimeout (or backward) in milliseconds relativ to nextFireTime
   * @return TODO foreignJobs not used
   */
  public List<TriggerJobDO> getNextJobs(long minNodeBindTimeout);

  /**
   * Gibt alle Jobs mit dem Status zurueck
   * 
   * @param scheduler kann null sein.
   * @param fromDate bezieht sich auf modifiedat kann null sein
   * @param untilDate bezieht sich auf modifiedat kann null sein
   * @param state bezieht sich auf modifiedat kann null sein
   * @return
   */
  public List<TriggerJobDO> getJobs(final Scheduler scheduler, final Date fromDate, final Date untilDate, final State state);

  /**
   * Gibt einen Scheduler aus der Datenbank zurück.
   * <p>
   * Ist er noch nicht existent, so wird er erzeugt und ein vorbefülltes DO zurück gegeben.
   * </p>
   * 
   * @param schedulerName
   * @return
   */
  public SchedulerDO createOrGetScheduler(String schedulerName);

  /**
   * Fuegt einen Job ein
   * 
   * @param scheduler
   * @param executor
   * @param info
   * @param trigger
   * @param hostName
   * @param state
   * @return
   */
  public TriggerJobDO submit(Scheduler scheduler, JobDefinition executor, Object info, Trigger trigger, String hostName, State state);

  public TriggerJobDO submit(Scheduler scheduler, String jobName, JobDefinition executor, Object info, Trigger trigger, String hostName,
      State state);

  // public int getThreadPoolSize(Scheduler scheduler);

  public void serviceRetry(TriggerJobDO job, JobResultDO resultInfo, ServiceUnavailableException ex, Scheduler Scheduler);

  public JobResultDO jobStarted(TriggerJobDO job, Scheduler scheduler);

  public void jobRetry(TriggerJobDO job, JobResultDO resultInfo, Exception ex, Scheduler Scheduler);

  public void jobCompleted(TriggerJobDO job, JobResultDO jobResult, Object result, Scheduler scheduler, Date nextRun);

  /**
   * Aborts job. Set Jobresult and state State.STOP
   * 
   * @param job
   * @param jobResult
   * @param ex
   * @param scheduler
   */
  public void jobAborted(TriggerJobDO job, JobResultDO jobResult, Throwable ex, Scheduler scheduler);

  /**
   * Loescht job, aber nur wenn kein JobResult vorhanden ist
   * 
   * @param job
   * @param jobResult may be null
   * @param scheduler
   */
  public void jobRemove(TriggerJobDO job, JobResultDO jobResult, Scheduler scheduler);

  public void jobResultRemove(TriggerJobDO job, JobResultDO jobResult, Scheduler scheduler);

  public List<JobResultDO> getResults(TriggerJobDO impl, int maxResults);

  public void shutdown() throws InterruptedException;

  // public boolean setInactiveTriggerActive(Trigger trigger, Job job);
  /**
   * Versucht den Job zu reservieren.
   * <p>
   * Liefert den Job und setzt ihn auf WAIT auf ACTIVE. Dies muss eine atomare Operation sein.
   * </p>
   */
  public TriggerJobDO reserveJob(TriggerJobDO job);

  public void persist(SchedulerDO scheduler);

  public void withinTransaction(final Runnable runnable);

  /**
   * mapping getAdminJobById
   * 
   * @param pk
   * @return
   */
  public TriggerJobDO getAdminJobByPk(long pk);

  /**
   * getJob
   * 
   * @param pk
   * @return
   */
  public TriggerJobDO getJobByPk(long pk);

  /**
   * ibatis: setJobState
   * 
   * Update the state of given Job.
   * 
   * @param pk
   * @param newState
   * @param oldState
   * @return 0 if stored state is not oldState
   */
  public int setJobState(long pk, String newState, String oldState);

  /**
   * 
   * @param hostName may be null
   * @param state may be null. In this case all Jobs with state != 'CLOSED' are returned
   * @param schedulerName may be null
   * @param resultCount
   * @return
   */
  public List<TriggerJobDisplayDO> getAdminJobs(String hostName, String jobName, String state, String schedulerName, int resultCount);

  /**
   * 
   * @param hostName may be null
   * @param state may be null. In this case all Jobs with state != 'CLOSED' are returned
   * @param schedulerName may be null
   * @param resultCount
   * @return
   */
  public List<TriggerJobDisplayDO> getAdminJobs(String hostName, String state, String schedulerName, int resultCount);

  /**
   * ibatis: getAdminJobResults
   * 
   * @param jobId
   * @return
   */
  public List<JobResultDO> getResultsForJob(long jobId);

  /**
   * Return the job result for given pk
   * 
   * @param resultId
   * @return
   */
  public JobResultDO getResultByPk(long resultId);

  public List<SchedulerDisplayDO> getAdminSchedulers();

  /**
   * Return number of jobs with given state
   * 
   * @param state if state == null, return all
   * @return
   */
  public long getJobCount(State state);

  public long getJobResultCount(State state);

  /**
   * Löscht den angegebenen Scheduler.
   * 
   * @param pk
   */
  public void deleteScheduler(Long pk);

  public List<String> getJobNames();

  public List<String> getUniqueJobNames();
}
