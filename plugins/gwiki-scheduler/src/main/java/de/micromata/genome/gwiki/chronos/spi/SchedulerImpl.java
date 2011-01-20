/////////////////////////////////////////////////////////////////////////////
//
// $RCSfile: SchedulerImpl.java,v $
//
// Project   chronos
//
// Author    Wolfgang Jung (w.jung@micromata.de)
// Created   26.12.2006
// Copyright Micromata 26.12.2006
//
// $Id: SchedulerImpl.java,v 1.23 2008-01-05 14:36:11 roger Exp $
// $Revision: 1.23 $
// $Date: 2008-01-05 14:36:11 $
//
/////////////////////////////////////////////////////////////////////////////
package de.micromata.genome.gwiki.chronos.spi;

import java.util.Date;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang.Validate;

import de.micromata.genome.gwiki.chronos.JobDefinition;
import de.micromata.genome.gwiki.chronos.JobStore;
import de.micromata.genome.gwiki.chronos.Scheduler;
import de.micromata.genome.gwiki.chronos.State;
import de.micromata.genome.gwiki.chronos.Trigger;
import de.micromata.genome.gwiki.chronos.logging.GLog;
import de.micromata.genome.gwiki.chronos.logging.GenomeLogCategory;
import de.micromata.genome.gwiki.chronos.spi.jdbc.JobResultDO;
import de.micromata.genome.gwiki.chronos.spi.jdbc.SchedulerDO;
import de.micromata.genome.gwiki.chronos.spi.jdbc.TriggerJobDO;

/**
 * Gruppiert Jobs bezüglich eines technischen Sachverhalts, z.B. einem Dienst wie Mail.
 * <p>
 * Repräsentiert im Gegensatz zum {@link SchedulerDO} die Runtime-Sicht auf einen {@link Scheduler}.
 * </p>
 */
public class SchedulerImpl implements Scheduler, RejectedExecutionHandler
{

  private String name;

  private long schedulerId = -1;

  private ThreadPoolExecutor executor;

  private BlockingQueue<Runnable> queue;

  private boolean paused = false;

  protected long nextRuntime;

  private int serviceRetryTime;

  private int jobRetryTime;

  private int jobMaxRetryCount;

  private int nodeBindingTimeout;

  private int threadPoolSize;

  /**
   * Dispatcher owns this scheduler
   */
  private DispatcherInternal dispatcher;

  public void setThreadPoolSize(int threadPoolSize)
  {
    this.threadPoolSize = threadPoolSize;
    if (executor != null)
      adjustThreadPoolSize(threadPoolSize);
  }

  public int getThreadPoolSize()
  {
    return this.threadPoolSize;
  }

  public SchedulerImpl()
  {
    super();
  }

  /**
   * Ruft {@link #initThreadPool(int)} und {@link #initProperties(SchedulerDO)} auf.
   * 
   * @param schedulerDO
   * @param logging
   */
  public SchedulerImpl(final SchedulerDO schedulerDO, final DispatcherInternal dispatcher)
  {
    Validate.notNull(schedulerDO, "schedulerDO is null");
    final String schedulerName = schedulerDO.getName();
    Validate.notNull(schedulerDO, "schedulerDO.name is null");
    Validate.notNull(dispatcher, "dispatcher is null");
    this.dispatcher = dispatcher;
    initThreadPool(schedulerDO.getThreadPoolSize(), schedulerName);
    initProperties(schedulerDO);
  }

  /**
   * Kopiert die Properties von DB-Objekt außer {@link SchedulerDO#getThreadPoolSize()} und einer schon gesetzten Id oder Namen.
   * <p>
   * Also wird eine Id nie reinitialisiert.
   * </p>
   * 
   * @param schedulerDO
   */
  private void initProperties(final SchedulerDO schedulerDO)
  {
    if (this.schedulerId == SchedulerDO.UNSAVED_SCHEDULER_ID) {
      this.schedulerId = schedulerDO.getPk();
    }
    if (this.name == null) {
      this.name = schedulerDO.getName();
    }
    this.serviceRetryTime = schedulerDO.getServiceRetryTime();
    this.jobRetryTime = schedulerDO.getJobRetryTime();
    this.jobMaxRetryCount = schedulerDO.getJobMaxRetryCount();
    this.nodeBindingTimeout = schedulerDO.getNodeBindingTimeout();
    this.threadPoolSize = schedulerDO.getThreadPoolSize();
  }

  /**
   * Erzeugt ein neues DO als Kopie der eigenen Attribute.
   * 
   * @return
   */
  public SchedulerDO getDO()
  {
    final SchedulerDO schedulerDO = new SchedulerDO();
    schedulerDO.setPk(schedulerId);
    schedulerDO.setName(name);
    schedulerDO.setServiceRetryTime(serviceRetryTime);
    schedulerDO.setJobRetryTime(jobRetryTime);
    schedulerDO.setJobMaxRetryCount(jobMaxRetryCount);
    schedulerDO.setThreadPoolSize(threadPoolSize);
    schedulerDO.setNodeBindingTimeout(nodeBindingTimeout);
    return schedulerDO;
  }

  /**
   * Reinitialisiert den Scheduler und adjustiert ggf. den Thread-Pool neu.
   * 
   * @param schedulerDO
   * @see #initProperties(SchedulerDO)
   * @see #initThreadPool(int, String)
   */
  public void reInit(final SchedulerDO schedulerDO)
  {
    initProperties(schedulerDO);
    adjustThreadPoolSize(schedulerDO.getThreadPoolSize());
  }

  /**
   * Instanziiert den Thread-Pool.
   * 
   * @param threadPoolSize
   */
  private void initThreadPool(final int threadPoolSize, final String name)
  {
    queue = new LinkedBlockingQueue<Runnable>();
    this.threadPoolSize = threadPoolSize;

    int i = threadPoolSize;
    if (threadPoolSize == 0) {
      i = 1;
      GLog.warn(GenomeLogCategory.Scheduler, "ThreadPoolSize is given with 0: " + name);
    }
    // es muss mind. ein Thread sein
    executor = new ThreadPoolExecutor(threadPoolSize, i, 1, TimeUnit.SECONDS, queue, this);
    SchedulerThreadFactory tfactory = new SchedulerThreadFactory();
    String appId = "gwiki";// StaticDaoManager.get().getShortApplicationName();
    String threadGroupName = "JCWTG[" + appId + "]: " + dispatcher.getDispatcherName() + "; " + name;
    ThreadGroup threadGroup = new ThreadGroup(dispatcher.getCreateDispatcherThreadGroup(), threadGroupName);
    tfactory.setThreadGroup(threadGroup);
    tfactory.setThreadNamePrefix("JCWT[" + appId + "]: " + dispatcher.getDispatcherName() + "; " + name);
    executor.setThreadFactory(tfactory);
  }

  /**
   * Setzt die neue Größe des Thread-Pools.
   * 
   * @param newSize
   */
  public synchronized void adjustThreadPoolSize(int newSize)
  {
    this.threadPoolSize = newSize;

    // work around das Framework kann keine 0
    // es wird also immer mind. ein Thread allociert
    if (newSize == 0) {
      newSize = 1;
      paused = true;
    }
    executor.setMaximumPoolSize(Math.max(newSize, executor.getCorePoolSize()));
    executor.setCorePoolSize(newSize);
  }

  synchronized void setNextRuntime(final long lastRuntime)
  {
    nextRuntime = lastRuntime;
  }

  /**
   * Führ einen Job aus.
   * <p>
   * <ul>
   * <li>Versucht den Job in der Datenbank für sich zu reservieren {@link JobStore#reserveJob(Long)} und</li>
   * <li>führt ihn dann mit dem {@link ThreadPoolExecutor#execute(Runnable)} aus.</li>
   * </ul>
   * 
   * @see de.micromata.genome.gwiki.chronos.Scheduler#executeJob(java.lang.Long, de.micromata.genome.gwiki.chronos.JobStore)
   */
  public synchronized boolean executeJob(final TriggerJobDO job, JobStore jobStore)
  {
    if (checkPausedScheduler() == true) {
      return false;
    }
    nextRuntime = System.currentTimeMillis();
    adjustPoolSize();

    if (checkThreadPoolExhausted(job) == true) {
      return false;
    }
    TriggerJobDO jobToExecute = jobStore.reserveJob(job);
    if (jobToExecute == null)
      return false;
    executeJobNow(jobToExecute, jobStore);
    return true;
  }

  // /**
  // * Starts a Job
  // *
  // * @param job
  // * @return false, if Job either cannot be reserved of thread pool is on limit
  // */
  // public synchronized boolean startJob(TriggerJobDO job)
  // {
  // if (checkPausedScheduler() == true)
  // return false;
  // adjustPoolSize();
  // if (checkThreadPoolExhausted(job) == true) {
  // return false;
  // }
  // JobStore jobStore = getDispatcher().getJobStore();
  // int toggled = jobStore.setJobState(job.getPk(), State.RUN.name(), job.getState().name());
  // if (toggled == 0)
  // return false;
  //
  // executeJobNow(job, jobStore);
  // return true;
  // }

  public synchronized void executeJobNow(TriggerJobDO jobToExecute, JobStore jobStore)
  {
    try {
      executor.execute(new JobRunner(this, jobToExecute));
    } catch (Exception ex) {
      /**
       * @logging
       * @reason Chronos Dispatcher Konnte einen Job nicht starten
       * @action TechAdmin kontaktieren
       */
      GLog.error(GenomeLogCategory.Scheduler, "Failed to start execution job: " + jobToExecute.getJobDefinitionString(), ex);// , new
                                                                                                                             // LogJobEventAttribute(new
                                                                                                                             // JobEventImpl(jobToExecute,
                                                                                                                             // null, null,
                                                                                                                             // State.SCHEDULED,
                                                                                                                             // this)));
      JobResultDO result = new JobResultDO();
      // result.setResultObject(ExceptionUtils.getFullStackTrace(ex));
      jobStore.jobAborted(jobToExecute, result, ex, this);
    }
  }

  /**
   * @param job
   * @param exit
   * @return
   */
  private boolean checkThreadPoolExhausted(final TriggerJobDO jobId)
  {
    if (threadPoolSize == 0) {
      GLog.trace(GenomeLogCategory.Scheduler, "scheduler is deactivated and rejecting job. scheduler: " + name + "; job: " + jobId);
      return true;
    }
    if (executor.getActiveCount() >= executor.getCorePoolSize())
      return true;
    return false;
  }

  public boolean isRunning()
  {
    return threadPoolSize > 0;
  }

  /**
   * 
   * @param waitForShutdown -1 == 1 h other milliseconds
   * @return true if all jobs are terminated
   */
  public boolean shutdown(final long waitForShutdown)
  {
    long realWait = waitForShutdown;
    if (realWait == -1)
      realWait = 1000 * 60 * 60; // 1 h
    executor.shutdown();
    if (executor.isTerminated() == true)
      return true;
    dispatcher.wakeup();

    /**
     * @logging
     * @reason Warten auf Scheduler beim Herunterfahren
     * @action Keine
     */
    GLog.note(GenomeLogCategory.Scheduler, "Wait for unfinished Jobs in Scheduler: " + name);
    try {
      boolean finished = executor.awaitTermination(realWait, TimeUnit.MILLISECONDS);
      /**
       * @logging
       * @reason Beim Herunterfahren sind alle Jobs beendet
       * @action Keine
       */
      GLog.note(GenomeLogCategory.Scheduler, "All Jobs finished in Scheduler: " + name);
      return finished;
    } catch (InterruptedException ex) {
      /**
       * @logging
       * @reason Scheduler wurde mit einer Interruption beendet
       * @action Keine
       */
      GLog.warn(GenomeLogCategory.Scheduler, "Scheduler shotdown with interrupt: " + name);
      return executor.isTerminated();
    }

  }

  /**
   * @param job
   * @return
   */
  // private boolean checkTriggerHasBeenFired(final Long jobId)
  // {
  // if (Dispatcher.getJobStore().setInactiveTriggerActive(job.getTrigger(),
  // job) == false) {
  // logging.log(Severity.NOTE, "job " + this + " has been started");
  // return true;
  // }
  // return false;
  // }
  /**
   * @return
   */
  private boolean checkPausedScheduler()
  {
    if (paused == true) {
      if (System.currentTimeMillis() < nextRuntime) {
        if (GLog.isTraceEnabled()) {
          GLog.trace(GenomeLogCategory.Scheduler, "scheduler will paused. name: " + name + "; until: " + nextRuntime);
        }
        return true;
      }
      paused = false;
    }
    return false;
  }

  /**
   * 
   */
  protected void adjustPoolSize()
  {
    if (executor.getActiveCount() < executor.getMaximumPoolSize() && executor.getCorePoolSize() < executor.getMaximumPoolSize()) {

      GLog.note(GenomeLogCategory.Scheduler, "Scheduler; Try to reduce pool size to: " + executor.getCorePoolSize() + "; sched: " + name);
      // logging.log(Severity.NOTE, "scheduler " + name + " try to reduce
      // pool size to " + executor.getCorePoolSize());
      executor.setMaximumPoolSize(Math.max(executor.getCorePoolSize(), executor.getMaximumPoolSize()));
    }
  }

  public boolean hasFreeJobSlots()
  {
    if (checkPausedScheduler() == true)
      return false;
    if (executor.getActiveCount() >= executor.getCorePoolSize())
      return false;
    return true;
  }

  public int getFreeJobSlotsCount()
  {
    return executor.getCorePoolSize() - executor.getActiveCount();
  }

  /**
   * Ruft {@link JobStore#getNextJobs(SchedulerImpl, Date)} auf.
   * 
   * @see de.micromata.genome.gwiki.chronos.Scheduler#getNextJobs(de.micromata.genome.gwiki.chronos.JobStore, boolean)
   */
  @SuppressWarnings("unchecked")
  public List<TriggerJobDO> getNextJobs(final JobStore jobStore, boolean foreignJobs)
  {
    final List<TriggerJobDO> nextJobs = jobStore.getNextJobs(this, foreignJobs);
    return nextJobs;
  }

  /**
   * @fromDate wird nicht ausgewertet
   * @untilDate NextFireTime
   */
  @Deprecated
  public List<TriggerJobDO> getJobs(final Date fromDate, final Date untilDate, final State state)
  {
    final List<TriggerJobDO> nextJobs = dispatcher.getJobStore().getJobs(this, fromDate, untilDate, state);
    return nextJobs;
  }

  public TriggerJobDO submit(final JobDefinition jobDefinition, final Object info, final Trigger trigger)
  {
    return dispatcher.getJobStore().submit(this, jobDefinition, info, trigger, getVirtualHostName(), State.WAIT);
  }

  public TriggerJobDO submit(final JobDefinition jobDefinition, final Object info, final Trigger trigger, String hostName)
  {
    return dispatcher.getJobStore().submit(this, jobDefinition, info, trigger, hostName, State.WAIT);
  }

  public String getName()
  {
    return name;
  }

  public synchronized void resume()
  {
    paused = false;
    nextRuntime = System.currentTimeMillis() - 1000;
    if (GLog.isInfoEnabled() == true)
      GLog.info(GenomeLogCategory.Scheduler, "Scheduler resumed: " + this);
  }

  public synchronized void suspend()
  {
    paused = true;
    nextRuntime = Long.MAX_VALUE;
    if (GLog.isInfoEnabled() == true)
      GLog.info(GenomeLogCategory.Scheduler, "Scheduler suspended: " + this);
  }

  public boolean isAcceptable(final JobDefinition jobDefinition, final Object argument, final Trigger trigger)
  {
    return true;
  }

  public void rejectedExecution(final Runnable r, final ThreadPoolExecutor executor)
  {
    GLog.warn(GenomeLogCategory.Scheduler, "Executor reject execution: " + this);
    throw new UnsupportedOperationException();
  }

  public int getServiceRetryTime()
  {
    return serviceRetryTime;
  }

  public void setServiceRetryTime(final int serviceRetryTime)
  {
    this.serviceRetryTime = serviceRetryTime;
  }

  public int getJobRetryTime()
  {
    return jobRetryTime;
  }

  public void setJobRetryTime(final int jobRetryTime)
  {
    this.jobRetryTime = jobRetryTime;
  }

  public synchronized void pause(final int seconds)
  {
    paused = true;
    nextRuntime = System.currentTimeMillis() + (1000L * seconds);
  }

  public long getId()
  {
    return schedulerId;
  }

  @Override
  public String toString()
  {
    if (executor == null)
      return "Scheduler[<uninitialized>]";
    return "Scheduler["
        + name
        + "] with threadPoolSize="
        + executor.getCorePoolSize()
        + " id="
        + schedulerId
        + " active="
        + executor.getActiveCount()
        + " total="
        + executor.getCompletedTaskCount()
        + " waiting="
        + executor.getQueue().size();
  }

  public Stats getSchedulerStats()
  {
    Stats s = new Stats();
    s.poolSize = executor.getCorePoolSize();
    s.poolActive = executor.getActiveCount();
    s.poolCompleted = executor.getCompletedTaskCount();
    s.poolWaiting = executor.getQueue().size();
    s.poolTaskCount = executor.getTaskCount();
    return s;
  }

  public String getVirtualHostName()
  {
    return dispatcher.getVirtualHostName();
  }

  public Scheduler getScheduler()
  {
    return this;
  }

  // public String getVm()
  // {
  // return dispatcher.getVm();
  // }

  public long getSchedulerId()
  {
    return schedulerId;
  }

  public void setSchedulerId(long schedulerId)
  {
    this.schedulerId = schedulerId;
  }

  public void setName(String name)
  {
    this.name = name;
  }

  public int getJobMaxRetryCount()
  {
    return jobMaxRetryCount;
  }

  public void setJobMaxRetryCount(int jobMaxRetryCount)
  {
    this.jobMaxRetryCount = jobMaxRetryCount;
  }

  public DispatcherInternal getDispatcher()
  {
    return dispatcher;
  }

  public int getNodeBindingTimeout()
  {
    return nodeBindingTimeout;
  }

  public void setNodeBindingTimeout(int nodeBindingTimeout)
  {
    this.nodeBindingTimeout = nodeBindingTimeout;
  }

}