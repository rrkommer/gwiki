/////////////////////////////////////////////////////////////////////////////
//
// $RCSfile: Scheduler.java,v $
//
// Project   chronos
//
// Author    Wolfgang Jung (w.jung@micromata.de)
// Created   19.12.2006
// Copyright Micromata 19.12.2006
//
// $Id: Scheduler.java,v 1.9 2008-01-02 14:06:29 noodles Exp $
// $Revision: 1.9 $
// $Date: 2008-01-02 14:06:29 $
//
/////////////////////////////////////////////////////////////////////////////
package de.micromata.genome.gwiki.chronos;

import java.util.Date;
import java.util.List;

import de.micromata.genome.gwiki.chronos.spi.DispatcherInternal;
import de.micromata.genome.gwiki.chronos.spi.jdbc.SchedulerDO;
import de.micromata.genome.gwiki.chronos.spi.jdbc.TriggerJobDO;

/**
 * Gruppiert Jobs nach technischer Sicht, z.B. bzgl. eines Services wie Mail-Jobs.
 * <p>
 * Hier werden die Jobs angestossen und auf die Threads verteilt.
 * </p>
 * 
 */

public interface Scheduler
{
  public static class Stats
  {
    public int poolSize;

    public int poolActive;

    public long poolCompleted;

    public int poolWaiting;

    public long poolTaskCount;
  }

  /**
   * @return unique symbolic Name of the scheduler
   */
  public String getName();

  /**
   * Submit a Job
   * 
   * @param jobDefinition must not be null
   * @param argument must not be null
   * @param trigger must not be null
   * @return submitted job
   * @deprecated use Dispacher
   */
  @Deprecated
  public TriggerJobDO submit(JobDefinition jobDefinition, Object argument, Trigger trigger);

  /**
   * Submit a Job on given host
   * 
   * @param jobDefinition must not be null
   * @param argument must not be null
   * @param trigger must not be null
   * @return submitted job
   */
  @Deprecated
  public TriggerJobDO submit(JobDefinition jobDefinition, Object argument, Trigger trigger, String hostName);

  /**
   * von Job auf den PK des Jobs geaendert, da ansonsten Performance troubles
   */
  public List<TriggerJobDO> getNextJobs(final JobStore jobStore, boolean foreignJobs);

  /**
   * Get all Jobs with given state
   * 
   * @param state if null return all
   */
  public List<TriggerJobDO> getJobs(Date fromDate, Date untilDate, State state);

  /**
   * @return true wenn Scheduler nicht in pause und auch freie Threads hat
   */
  public boolean hasFreeJobSlots();

  /**
   * Stop executing jobs
   */
  public void suspend();

  /**
   * resume executing jobs
   */
  public void resume();

  /**
   * @deprecated
   * @param jobDefinition
   * @param argument
   * @param trigger
   * @return
   */
  public boolean isAcceptable(JobDefinition jobDefinition, Object argument, Trigger trigger);

  /**
   * return the db pk of the scheduler
   * 
   */
  public long getId();

  /**
   * set an persist the service retry time
   * 
   * @param serviceRetryTimeInSeconds
   */
  public void setServiceRetryTime(int serviceRetryTimeInSeconds);

  /**
   * 
   * @return the service retry time of this scheduler
   */
  public int getServiceRetryTime();

  /**
   * set the job retry time of this scheduler
   * 
   * @param jobRetryTimeInSeconds
   */
  public void setJobRetryTime(int jobRetryTimeInSeconds);

  /**
   * 
   * @return the job retry time of this scheduler
   */
  public int getJobRetryTime();

  /**
   * 
   * @return the maximum job retry for this scheduler
   */
  public int getJobMaxRetryCount();

  /**
   * 
   * @return the dispatcher used for this scheduler
   */
  public DispatcherInternal getDispatcher();

  /**
   * Pauses execution of jobs due ServiceUnavailable
   * 
   * @param seconds suspend the scheduler for given time
   */
  public void pause(int seconds);

  /**
   * 
   * @return true if scheduler is running with at least 1 thread. Paused scheduler are also running
   */
  public boolean isRunning();

  /**
   * Execute the given job.
   * 
   * This will be called inside the scheduler thread
   * 
   * @param job
   * @param jobStore
   * @return
   */
  public boolean executeJob(TriggerJobDO job, JobStore jobStore);

  /**
   * set the number of maximum threads used for this scheduler
   * 
   * @param threadPoolSize
   */
  public void setThreadPoolSize(int threadPoolSize);

  /**
   * 
   * @return the maximum thread count used by this scheduler
   */
  public int getThreadPoolSize();

  /**
   * 
   * @return the node bind timeout in seconds
   */
  public int getNodeBindingTimeout();

  /**
   * Sets the node bind time out. In this time a job will be executed on node
   * 
   * @param nodeBindingTimeout in seconds
   */
  public void setNodeBindingTimeout(int nodeBindingTimeout);

  /**
   * 
   * @return the internal statistics about this scheduler
   */
  public Stats getSchedulerStats();

  /**
   * shut down the scheduler thread pool
   * 
   * @param waitForShutdown if -1 wait unlimited time
   * @return true if the scheduler thread pool was shut down successfully
   */
  public boolean shutdown(final long waitForShutdown);

  /**
   * 
   * @return the DO for persistence of this scheduler
   */
  public SchedulerDO getDO();

  /**
   * reinitialize the scheduler
   * 
   * @param schedulerDO
   */
  public void reInit(final SchedulerDO schedulerDO);

  /**
   * Sets the internal pk for this scheduler
   * 
   * @param schedId
   */
  public void setSchedulerId(long schedId);

  /**
   * 
   * @return the free threads in this scheduler thread pool
   */
  public int getFreeJobSlotsCount();

}
