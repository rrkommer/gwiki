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

//////////////////////////////////////////////////////////////////////////////
//
// $RCSfile: Dispatcher.java,v $
//
// Project   genome
//
// Author    Wolfgang Jung (w.jung@micromata.de)
// Created   02.01.2007
// Copyright Micromata 02.01.2007
//
// $Id: Dispatcher.java,v 1.33 2008-01-05 14:36:11 roger Exp $
// $Revision: 1.33 $
// $Date: 2008-01-05 14:36:11 $
//
/////////////////////////////////////////////////////////////////////////////
package de.micromata.genome.gwiki.chronos.spi;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.Validate;
import org.apache.log4j.Logger;

import de.micromata.genome.gwiki.chronos.JobDefinition;
import de.micromata.genome.gwiki.chronos.JobStore;
import de.micromata.genome.gwiki.chronos.Scheduler;
import de.micromata.genome.gwiki.chronos.SchedulerConfigurationException;
import de.micromata.genome.gwiki.chronos.SchedulerException;
import de.micromata.genome.gwiki.chronos.ServiceUnavailableException;
import de.micromata.genome.gwiki.chronos.State;
import de.micromata.genome.gwiki.chronos.Trigger;
import de.micromata.genome.gwiki.chronos.spi.jdbc.SchedulerDO;
import de.micromata.genome.gwiki.chronos.spi.jdbc.TriggerJobDO;
import de.micromata.genome.logging.GLog;
import de.micromata.genome.logging.GenomeLogCategory;
import de.micromata.genome.logging.LogExceptionAttribute;
import de.micromata.genome.util.runtime.HostUtils;

/**
 * Zentrale Klasse (Singleton) für die Job-Verteilung.
 * <p>
 * Pollt die Datenbank nach neuen Scheduler und Job ab und versucht diese zu Starten.
 * </p>
 * <p>
 * Hier werden die Runtime-Instanzen von Schedulern und Jobs verwaltet.
 * </p>
 * 
 */
public class DispatcherImpl implements Runnable, Dispatcher, DispatcherInternal
{

  /**
   * The Constant LOG.
   */
  private static final Logger LOG = Logger.getLogger(DispatcherImpl.class);

  /**
   * The job store.
   */
  private final JobStore jobStore;

  /**
   * Name of the application.
   */
  private String appName = "";

  /**
   * The virtual host name.
   */
  private String virtualHost;

  /**
   * The min refresh in millis.
   */
  protected long minRefreshInMillis = 10L;

  /**
   * The start refresh in millis.
   */
  protected long startRefreshInMillis = 250L;

  /**
   * The max refresh in millis.
   */
  protected long maxRefreshInMillis = 4000L;

  /**
   * time in milliseconds whereas minimal node bind (hostname) will be asumed.
   */
  protected long minNodeBindTime = 1000L;

  /**
   * The last scheduler update.
   */
  private long lastSchedulerUpdate = 0;

  /**
   * The scheduler lease time.
   */
  private long schedulerLeaseTime = 10000; // 10 seconds

  /**
   * The schedulers.
   */
  protected final Map<String, Scheduler> schedulers = new HashMap<String, Scheduler>();

  /**
   * Zuordnung der TriggerJobDO zum Namen des entsprechenden .
   */
  private final Map<String, List<TriggerJobDO>> jobs = new HashMap<String, List<TriggerJobDO>>();

  /**
   * The dispatcher thread group.
   */
  private ThreadGroup dispatcherThreadGroup;

  /**
   * The dispatcher thread.
   */
  private Thread dispatcherThread;

  /**
   * Erzeugt einen neuen Dispatcher.
   * <p>
   * Der Cluster-Diskriminator ist Empty!
   * </p>
   *
   * @param jobStore the job store
   */
  protected DispatcherImpl(final JobStore jobStore)
  {
    this.jobStore = jobStore;
    jobStore.setDispatcher(this);
    virtualHost = HostUtils.getThisHostName();
  }

  /**
   * Erzeugt einen neuen Dispatcher und gibt den virtual host name für den Cluster-Betrieb mit.
   *
   * @param virtualHost the virtual host
   * @param jobStore the job store
   */
  public DispatcherImpl(final String virtualHost, final JobStore jobStore)
  {
    this(jobStore);
    if (StringUtils.isNotEmpty(virtualHost) == true) {
      this.virtualHost = virtualHost;
    }
  }

  @Override
  public ThreadGroup getCreateDispatcherThreadGroup()
  {
    if (dispatcherThreadGroup != null) {
      return dispatcherThreadGroup;
    }
    dispatcherThreadGroup = new ThreadGroup("JCDTG[" + "gwiki" + "]: " + getDispatcherName());
    return dispatcherThreadGroup;
  }

  /**
   * Creates the thread.
   *
   * @param jobStore the job store
   * @return the thread
   */
  private Thread createThread(final JobStore jobStore)
  {
    final Thread t = new Thread(getCreateDispatcherThreadGroup(), this,
        "JCDT[" + "gwiki" + "]: " + getDispatcherName());
    t.setDaemon(true);
    return t;
  }

  @Override
  public String getDispatcherName()
  {
    return jobStore + " at " + HostUtils.getRunContext(dispatcherThread);
  }

  @Override
  public JobStore getJobStore()
  {
    return jobStore;
  }

  /**
   * Startet den Dispatcher und hält seinen Thread.
   */
  @Override
  public synchronized void startup()
  {
    GLog.info(GenomeLogCategory.Scheduler, "Starting Dispatcher");
    if (dispatcherThread == null) {
      dispatcherThread = createThread(jobStore);
    }
    if (dispatcherThread.isAlive() == false) {
      dispatcherThread.start();
    }
  }

  @Override
  public void shutdown() throws InterruptedException
  {
    shutdown(-1L);
  }

  /**
   * If dispatcher is waiting for starting a new job, wake dispatcher
   */
  @Override
  public synchronized void wakeup()
  {
    this.notify();
  }

  @Override
  public boolean isRunning()
  {
    return dispatcherThread != null;
  }

  @Override
  public void shutdown(final long waitForShutdown) throws InterruptedException
  {
    /**
     * @logging
     * @reason Chronos Dispatcher wird heruntergefahren
     * @action Keine
     */
    GLog.note(GenomeLogCategory.Scheduler, "Shutdown Dispatcher");
    if (dispatcherThread == null) {
      /**
       * @logging
       * @reason Chronos Dispatcher wird heruntergefahren, war aber bereits gestoppt
       * @action Keine
       */
      GLog.note(GenomeLogCategory.Scheduler, "Shutdown Dispatcher, was already stopped");
      return;
    }
    if (dispatcherThread.isAlive() == false) {
      /**
       * @logging
       * @reason Chronos Dispatcher wird heruntergefahren, obwohl er bereits am herunterfahren ist.
       * @action Entwickler kontaktieren
       */
      GLog.error(GenomeLogCategory.Scheduler, "Shutdown with stopped dispatcher");
      throw new IllegalStateException(this + " already stopped");
    }
    dispatcherThread.interrupt();
    if (waitForShutdown < 0L) {
      dispatcherThread.join();
    } else {
      dispatcherThread.join(waitForShutdown);
    }
    for (Scheduler sched : this.schedulers.values()) {
      sched.shutdown(waitForShutdown);
    }
    dispatcherThread = null;
    // TODO R2V5 HF2 migrate to genome
    this.schedulers.clear();

    /**
     * @logging
     * @reason Chronos Dispatcher wurde heruntergefahren
     * @action Keine
     */
    GLog.note(GenomeLogCategory.Scheduler, "Shutdown Dispatcher finished");
  }

  @Override
  public void run()
  {
    ClassLoader contextClassLoader = Thread.currentThread().getContextClassLoader();

    try {
      /**
       * @logging
       * @reason Chronos Dispatcher ist gestartet
       * @action Keine
       */
      GLog.note(GenomeLogCategory.Scheduler, "Dispatcher run started");
      long curRefreshInMillis = minRefreshInMillis;
      int loopCount = 0;
      while (Thread.interrupted() == false) {
        ++loopCount;
        // DynDaoManager.initForDeamonJobs(false);
        if (loopCount < 0) {
          loopCount = 0;
        }
        try {
          GLog.trace(GenomeLogCategory.Scheduler, "Checking schedulers: (" + curRefreshInMillis + ")");
          boolean checkForeignJobs = ((loopCount % 5) == 0);

          boolean jobRuned = checkScheduler(checkForeignJobs);

          // no sleep if a job was started
          if (jobRuned == true) {
            curRefreshInMillis = minRefreshInMillis;
            continue;
          }
        } catch (final Throwable ex) {
          /**
           * @logging
           * @reason Chronos Dispatcher hat einen Fehler entdeckt
           * @action Abhaengig von der Exception Entwickler kontaktieren
           */
          GLog.error(GenomeLogCategory.Scheduler, "Error while dispatching: " + ex, new LogExceptionAttribute(ex));
        }
        try {
          Thread.sleep(curRefreshInMillis);
          if (curRefreshInMillis == 10) {
            // neuen Job, also gleich
            curRefreshInMillis = startRefreshInMillis;
          } else if (curRefreshInMillis < maxRefreshInMillis) {
            curRefreshInMillis *= 2;
            // System.out.println("curRefreshInMillis: " +
            // curRefreshInMillis);
          }
        } catch (final InterruptedException ex) {
          /**
           * @logging
           * @reason Chronos Dispatcher wurde heruntergefahren, da ein Thread-Interupt empfangen wurde
           * @action Keine
           */
          GLog.note(GenomeLogCategory.Scheduler, "Shutting down dispatcher because interrupted");
          break;
        }
      }
      GLog.note(GenomeLogCategory.Scheduler, "Dispatcher run finished");
    } finally {

    }
  }

  /**
   * Check job store schedulers.
   */
  protected void checkJobStoreSchedulers()
  {
    long now = System.currentTimeMillis();
    if (now - lastSchedulerUpdate < schedulerLeaseTime) {
      return;
    }
    forceCheckJobStoreSchedulers();
  }

  /**
   * Force check job store schedulers.
   */
  protected void forceCheckJobStoreSchedulers()
  {
    List<SchedulerDO> schedulerDOs = getJobStore().getSchedulers();
    for (SchedulerDO s : schedulerDOs) {
      /* Scheduler sched = */createOrGetScheduler(s);
    }
  }

  protected boolean checkScheduler(boolean foreignJobs)
  {
    final List<SchedulerDO> schedulers = jobStore.getSchedulers();
    // check for runnable jobs
    boolean jobRunned = false;
    boolean allJobRunned = false;
    boolean debugLogenabled = GLog.isDebugEnabled();
    for (final SchedulerDO schedulerDO : schedulers) {
      // TODO (Rx) rrk check if scheduler is paused before selecting jobs
      // select for update in DB
      // logging.log(Severity.DEBUG, "Checking jobs for scheduler " +
      // schedulerDO);

      final Scheduler scheduler = createOrGetScheduler(schedulerDO);

      if (scheduler.hasFreeJobSlots() == false || scheduler.getThreadPoolSize() == 0) {
        continue;
      }

      final List<TriggerJobDO> nextJobs = scheduler.getNextJobs(jobStore, foreignJobs);

      for (final TriggerJobDO job : nextJobs) {
        if (debugLogenabled == true) {
          LOG.debug("Checking job: " + job.getPk() + " for " + schedulerDO);
        }
        if (checkAndExecuteJob(scheduler, job) == false) {
          allJobRunned = false;
          break;
        }
        jobRunned = true;
        allJobRunned = true;
      }
    }
    return jobRunned;
  }

  @Override
  public void persist(SchedulerDO scheduler)
  {

    Validate.notNull(scheduler, "Der Scheduler ist null");
    Validate.notNull(scheduler.getName(), "scheduler.name ist null");

    final String name = scheduler.getName();
    Scheduler si = this.getScheduler(name);
    if (si != null) {
      scheduler.setPk(si.getId());
    } else {
      scheduler.setPk(SchedulerDO.UNSAVED_SCHEDULER_ID);
    }
    /**
     * @logging
     * @reason Chronos Scheduler wird in die DB geschrieben
     * @action Keine
     */
    GLog.note(GenomeLogCategory.Scheduler, "Persist Scheduler: " + scheduler);
    jobStore.persist(scheduler);
  }

  private boolean checkAndExecuteJob(final Scheduler scheduler, final TriggerJobDO job)
  {
    // NOOP Codeif (scheduler.isAcceptable(job.getJobDefinition(),
    // scheduler, job.getTrigger()) == true) {
    // dispatch to scheduler
    try {
      if (GLog.isInfoEnabled() == true) {
        GLog.info(GenomeLogCategory.Scheduler, "Job wird gestartet: " + scheduler.getName() + "#" + job.getPk());
      }
      boolean jobExecuted = scheduler.executeJob(job, jobStore);
      return jobExecuted;
    } catch (final ServiceUnavailableException ex) {
      scheduler.pause(scheduler.getServiceRetryTime());
    }
    // }
    return false;
  }

  @Deprecated
  public Scheduler createOrGetScheduler(final String schedulerName)
  {
    Validate.notNull(schedulerName, "schedulerName ist null.");
    SchedulerDO schedulerDO = jobStore.createOrGetScheduler(schedulerName);
    return createOrGetScheduler(schedulerDO);
  }

  @Override
  public void denyNewJobs(final String schedulerName)
  {
    final Scheduler scheduler = getScheduler(schedulerName);
    if (scheduler != null) {
      SchedulerDO sched = scheduler.getDO();
      scheduler.setThreadPoolSize(0);
      sched.setThreadPoolSize(0);
      persist(sched);
    }
  }

  @Override
  public void setJobCount(final int size, final String schedulerName)
  {
    final Scheduler scheduler = getScheduler(schedulerName);
    if (scheduler != null) {
      SchedulerDO sched = scheduler.getDO();
      scheduler.setThreadPoolSize(size);
      sched.setThreadPoolSize(size);
      persist(sched);
    }
  }

  @Override
  public Scheduler createOrGetScheduler(final SchedulerDO schedulerDO)
  {
    Validate.notNull(schedulerDO, "schedulerDB ist null.");

    String schedulerName = schedulerDO.getName();
    Validate.notNull(schedulerDO, "schedulerDB.name ist null.");

    synchronized (this) {
      Scheduler result = schedulers.get(schedulerName);

      if (result != null) {
        result.reInit(schedulerDO);
        return result;
      }

      final Scheduler scheduler = new SchedulerImpl(schedulerDO, this);

      // ist der Scheduler schon in der DB?
      final SchedulerDO schedulerDB = jobStore.createOrGetScheduler(schedulerName);
      if (schedulerDB.getPk() != SchedulerDO.UNSAVED_SCHEDULER_ID) {
        schedulerDO.setPk(schedulerDB.getPk());
        if (GLog.isTraceEnabled() == true) {
          GLog.trace(GenomeLogCategory.Scheduler,
              "Reuse existing DB-Sheduler entrie. scheduler: " + schedulerName + "#" + schedulerDB.getPk());
        }
      } else {
        // ggf. ist der Prefix korrigiert
        schedulerDO.setName(schedulerDB.getName());
        /**
         * @logging
         * @reason Chronos Ein noch nicht existierender Scheduler wird in die DB geschrieben.
         * @action Keine
         */
        GLog.note(GenomeLogCategory.Scheduler, "Create a new DB-Entry for scheduler: " + schedulerName);
      }
      // im Zweifelsfall mit neuen Werten überschreiben, sonst nur neu
      // anlegen
      jobStore.persist(schedulerDO);
      // setzen der Id aus der Datenbank !
      scheduler.setSchedulerId(schedulerDO.getPk());
      // Halten mit Prefix`
      schedulers.put(schedulerName, scheduler);
      // jobs.put(schedulerName, new ArrayList<TriggerJobDO>());
      return scheduler;
    } // synchronized end
  }

  public void submit(final String schedulerName, final JobDefinition jobDefinition, final Object arg,
      final Trigger trigger)
  {
    submit(schedulerName, (String) null, jobDefinition, arg, trigger);
  }

  public void submit(final String schedulerName, String jobName, final JobDefinition jobDefinition, final Object arg,
      final Trigger trigger)
  {
    submit(schedulerName, jobName, jobDefinition, arg, trigger, getVirtualHostName());
  }

  /**
   * 
   * @param schedulerName
   * @param jobDefinition
   * @param arg
   * @param trigger
   * @return Job reference (pk)
   * @throws SchedulerConfigurationException wenn ein nicht registrierter Scheduler angesprochen wird
   * @throws SchedulerException wenn der Job im JobStore nicht angelegt werden kann.
   */
  @Override
  public long submit(final String schedulerName, String jobName, final JobDefinition jobDefinition, final Object arg,
      final Trigger trigger, String hostName)
  {
    if (hostName == null) {
      hostName = getVirtualHostName();
    }
    final Scheduler Scheduler = getScheduler(schedulerName);
    if (Scheduler == null) {
      final String msg = "Es wurde versucht einen nicht registrierten Scheduler zu benutzen: " + schedulerName;
      /**
       * @logging
       * @reason Chronos Dispatcher hat einen Job ueber einen Schedulder bekommen, wobei der Scheduler nicht
       *         eingerichtet ist.
       * @action TechAdmin kontaktieren
       */
      GLog.error(GenomeLogCategory.Scheduler,
          "Es wurde versucht einen nicht registrierten Scheduler zu benutzen: " + schedulerName);
      throw new SchedulerConfigurationException(msg);
    }

    TriggerJobDO job = jobStore.submit(Scheduler, jobName, jobDefinition, arg, trigger, hostName, State.WAIT);
    Long jobPk = job.getPk();
    if (jobPk == null) {
      // pk = null sollte nicht auftreten können ist aber abhängig von der JobStore implemenmtierung und theoretisch möglich.
      final String msg = "Beim Anlegen des Jobs ist ein Fehler aufgetreten. Die Referenz (pk) wurde nicht gesetzt : "
          + job.toString();
      /**
       * @logging
       * @reason Im Job Store wurde beim persistieren eines neuen Jobs keine Referenz (pk) vergeben.
       * @action TechAdmin kontaktieren
       */
      GLog.error(GenomeLogCategory.Scheduler,
          "Beim Anlegen des Jobs ist ein Fehler aufgetreten. Die Referenz (pk) wurde nicht gesetzt : "
              + job.toString());
      throw new SchedulerException(msg);
    }
    return jobPk.longValue();
  }

  public List<Scheduler> getSchedulers()
  {
    synchronized (this) {
      return new ArrayList<Scheduler>(schedulers.values());
    }
  }

  @Override
  public Scheduler getScheduler(final String name)
  {
    synchronized (this) {
      if (GLog.isDebugEnabled() == true) {
        GLog.debug(GenomeLogCategory.Scheduler, "Get scheduler for: " + name + " " + name);
      }
      Scheduler scheduler = schedulers.get(name);
      if (scheduler != null) {
        return scheduler;
      }
      forceCheckJobStoreSchedulers();
      scheduler = schedulers.get(name);

      return scheduler;
    }
  }

  public void setRefreshInterval(final long refreshInMillis)
  {
    this.minRefreshInMillis = refreshInMillis;
  }

  @Override
  public String toString()
  {
    return "Dispatcher for " + jobStore + " at " + getVirtualHostName() + "@" + HostUtils.getVm();
  }

  public String getAppName()
  {
    return appName;
  }

  public void setAppName(String appName)
  {
    this.appName = appName;
  }

  public Thread getDispatcherThread()
  {
    return dispatcherThread;
  }

  public void setDispatcherThread(Thread dispatcherThread)
  {
    this.dispatcherThread = dispatcherThread;
  }

  public long getMinRefreshInMillis()
  {
    return minRefreshInMillis;
  }

  public void setMinRefreshInMillis(long minRefreshInMillis)
  {
    this.minRefreshInMillis = minRefreshInMillis;
  }

  public long getStartRefreshInMillis()
  {
    return startRefreshInMillis;
  }

  public void setStartRefreshInMillis(long startRefreshInMillis)
  {
    this.startRefreshInMillis = startRefreshInMillis;
  }

  public long getMaxRefreshInMillis()
  {
    return maxRefreshInMillis;
  }

  public void setMaxRefreshInMillis(long maxRefreshInMillis)
  {
    this.maxRefreshInMillis = maxRefreshInMillis;
  }

  public Map<String, List<TriggerJobDO>> getJobs()
  {
    return jobs;
  }

  public ThreadGroup getDispatcherThreadGroup()
  {
    return dispatcherThreadGroup;
  }

  public void setDispatcherThreadGroup(ThreadGroup dispatcherThreadGroup)
  {
    this.dispatcherThreadGroup = dispatcherThreadGroup;
  }

  @Override
  public String getVirtualHostName()
  {
    // if (virtualHost != null)
    // return virtualHost;
    // return HostUtils.getNodeName();;
    // int maxLength = 60;
    // int cutLength = 0;
    // if (StringUtils.isNotEmpty(appName) == true) {
    // cutLength = appName.length() + 1;
    // }
    // if (StringUtils.isNotEmpty(discriminator) == true) {
    // cutLength = discriminator.length() + 1;
    // }
    // String nv = HostUtils.getNodeName();
    // nv = MiscStringUtils.cutRight(nv, maxLength - cutLength);
    // if (appName != null) {
    // nv += "-" + appName;
    // }
    // if (StringUtils.isNotEmpty(discriminator) == true)
    // nv += "-" + discriminator;
    // virtualHost = nv;
    return virtualHost;
  }

  public long getMinNodeBindTime()
  {
    return minNodeBindTime;
  }

  @Override
  public void setMinNodeBindTime(long minNodeBindTime)
  {
    this.minNodeBindTime = minNodeBindTime;
  }

  public String getVirtualHost()
  {
    return virtualHost;
  }

  public void setVirtualHost(String virtualHost)
  {
    this.virtualHost = virtualHost;
  }

  @Override
  public long submit(String schedulerName, JobDefinition jobDefinition, Object arg, Trigger trigger, String hostName)
  {
    return submit(schedulerName, null, jobDefinition, arg, trigger, hostName);
  }

}
