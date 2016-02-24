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

//////////////////////////////////////////////////////////////////////////////
//
// $RCSfile: Dispatcher.java,v $
//
// Project   genome
//
// Author    Roger Rene Kommer
// Created   02.01.2007
// Copyright Micromata 02.01.2007
//
// $Id: Dispatcher.java,v 1.33 2008-01-05 14:36:11 roger Exp $
// $Revision: 1.33 $
// $Date: 2008-01-05 14:36:11 $
//
/////////////////////////////////////////////////////////////////////////////
package de.micromata.genome.gwiki.chronos.spi;

import java.util.Date;
import java.util.Iterator;
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
import de.micromata.genome.gwiki.chronos.manager.LogJobEventAttribute;
import de.micromata.genome.gwiki.chronos.spi.jdbc.SchedulerDO;
import de.micromata.genome.gwiki.chronos.spi.jdbc.TriggerJobDO;
import de.micromata.genome.logging.GLog;
import de.micromata.genome.logging.GenomeLogCategory;
import de.micromata.genome.logging.LogExceptionAttribute;

/**
 * Zentrale Klasse (Singleton) für die Job-Verteilung.
 * <p>
 * Pollt die Datenbank nach neuen {@link Scheduler} und {@link Job} ab und versucht diese zu Starten.
 * <p/>
 * Diese Implementierung geht davon aus, dass es eine implizite minimale Nodebindtimeout gibt, so dass ein lokaler Cache
 * der zu startenden Jobs verwaltet werden kann und damit die Anzahl der selects reduziert werden kann.
 * </p>
 * <p>
 * Hier werden die Runtime-Instanzen von Schedulern und Jobs verwaltet.
 * </p>
 * TODO minRefreshInMillis, minRefreshInMillis, maxRefreshInMillis not used
 */
public class DispatcherImpl2 extends DispatcherImpl
{
  private static Logger LOG = Logger.getLogger(DispatcherImpl2.class);
  private ReservedJobs reservedJobs = new ReservedJobs();

  /**
   * Last time looked for new Jobs in JobStore
   */
  private long lastJobStoreRefreshTimestamp = 0;

  /**
   * Erzeugt einen neuen Dispatcher
   * 
   * @param prefix
   * @param jobStore
   * @param parentLogger
   */
  public DispatcherImpl2(final String virtualHost, final JobStore jobStore)
  {
    super(virtualHost, jobStore);
  }

  // TODO rrk wird nciht benutzt
  public boolean checkJobToRun(TriggerJobDO job, Map<String, Scheduler> schedMap)
  {
    Scheduler sched = schedMap.get(job.getSchedulerName());
    if (sched == null) {
      return false;
    }
    if (sched.hasFreeJobSlots() == true) {
      return false;
    }
    return checkAndExecuteJob(sched, job);
  }

  public long checkJobsToRun()
  {

    checkJobStoreSchedulers();
    long now = System.currentTimeMillis();
    synchronized (reservedJobs) {
      for (Iterator<TriggerJobDO> it = reservedJobs.getJobsByNextFireTimeIterator(); it.hasNext() == true;) {
        TriggerJobDO job = it.next();
        if (job.getNextFireTime() == null) {
          GLog.note(GenomeLogCategory.Scheduler, "Reserved Job nextFireTime was null", new LogJobEventAttribute(job));
          reservedJobs.removeJob(it, job);
        }
        long nft = job.getNextFireTime().getTime();
        long dif = now - nft;
        if (nft > now) {
          return job.getNextFireTime().getTime();
        }
        Scheduler sched = schedulers.get(job.getSchedulerName());
        int nbt = sched.getNodeBindingTimeout() * 1000;
        boolean foreignJob = getVirtualHost().equals(job.getHostName()) == false;
        if (foreignJob == true && now < nft + nbt) {
          continue;
        }
        boolean execute = sched.executeJob(job, getJobStore());
        if (execute == true) {
          if (foreignJob == true) {
            /**
             * @logging
             * @reason Ein Job von einer anderen Node wurde gestartet
             * @action Keine. Ggf. Node ueberpruefen und erneut starten.
             */
            GLog.note(GenomeLogCategory.Scheduler, "started foreign job: " + dif + " ms; " + job.getPk());
          }
          reservedJobs.removeJob(it, job);

        } else {
          if (LOG.isDebugEnabled() == true) {
            LOG.debug("reserved job not executed: " + job.getPk());
          }
        }
      }
    }
    return -1;
  }

  private void checkJobsInDB()
  {
    long now = System.currentTimeMillis();
    if (lastJobStoreRefreshTimestamp + minNodeBindTime > now) {
      return;
    }
    lastJobStoreRefreshTimestamp = now;
    long lookAHead = minNodeBindTime;

    List<TriggerJobDO> nextJobs = getJobStore().getNextJobs(lookAHead);
    if (nextJobs != null && nextJobs.size() > 0 && GLog.isDebugEnabled() == true) {
      LOG.debug("Dispatcher got new jobs from store: " + nextJobs.size());
    }
    reservedJobs.setReservedJobs(nextJobs);
  }

  /**
   * Main loop of the dispatcher
   * 
   * @see java.lang.Runnable#run()
   */
  @Override
  public void run()
  {
    // ClassLoader contextClassLoader = Thread.currentThread().getContextClassLoader();
    // ThreadContextClassLoaderScope scope = new ThreadContextClassLoaderScope(new
    // CombinedClassLoader(DispatcherImpl.class.getClassLoader(),
    // GenomeApplicationListener.webClassLoader, contextClassLoader));
    //
    // ScopedLogContextAttribute threadContextScope = new ScopedLogContextAttribute(GenomeAttributeType.ThreadContext, HostUtils
    // .getRunContext());
    try {
      /**
       * @logging
       * @reason Chronos Dispatcher ist gestartet
       * @action Keine
       */
      GLog.note(GenomeLogCategory.Scheduler, "Dispatcher run started");
      int loopCount = 0;

      long nextJobTime = -1;
      while (Thread.interrupted() == false) {
        ++loopCount;
        // DynDaoManager.initForDeamonJobs(false);
        if (loopCount < 0) {
          loopCount = 0;
        }

        // GenomeDaoManager.get().getInterNodeCallDAO().stampRunning();

        long n = System.currentTimeMillis();

        try {
          GLog.trace(GenomeLogCategory.Scheduler, "Checking schedulers");

          checkJobsInDB();

          nextJobTime = checkJobsToRun();
        } catch (final Throwable ex) {
          /**
           * @logging
           * @reason Chronos Dispatcher hat einen Fehler entdeckt
           * @action Abhaengig von der Exception Entwickler kontaktieren
           */
          GLog.error(GenomeLogCategory.Scheduler, "Error while dispatching: " + ex, new LogExceptionAttribute(ex));
        }
        try {
          long timeout = minNodeBindTime;
          if (nextJobTime != -1 && nextJobTime - n < minNodeBindTime) {
            timeout = nextJobTime - n;
          }
          if (timeout < 0) {
            timeout = 0;
          }
          synchronized (this) {
            this.wait(timeout);
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
      // if (threadContextScope != null)
      // threadContextScope.restore();
      // scope.restore();
    }
  }

  /**
   * TODO rrk wird nicht benutzt Startet einen Job über seinen {@link Scheduler} wenn dieser freie Slots besitzt.
   * <p>
   * Wird vom Job ein {@link ServiceUnavailableException} geworfen, dann wird {@link Scheduler#pause(int)} mit
   * {@link Scheduler#getServiceRetryTime()} aufgerufen.
   * </p>
   * 
   * @param scheduler
   * @param job
   */
  private boolean checkAndExecuteJob(final Scheduler scheduler, final TriggerJobDO job)
  {
    // NOOP Codeif (scheduler.isAcceptable(job.getJobDefinition(),
    // scheduler, job.getTrigger()) == true) {
    // dispatch to scheduler
    try {
      if (GLog.isInfoEnabled() == true) {
        GLog.info(GenomeLogCategory.Scheduler, "Job wird gestartet: " + scheduler.getName() + "#" + job.getPk());
      }
      boolean jobExecuted = scheduler.executeJob(job, getJobStore());
      return jobExecuted;
    } catch (final ServiceUnavailableException ex) {
      // check if this will be logged
      scheduler.pause(scheduler.getServiceRetryTime());
    }
    // }
    return false;
  }

  /**
   * Gibt zu einem {@link SchedulerDO} die entspechende reinitialisierte {@link Scheduler} zurück, oder erzeugt diese
   * neu.
   * <p>
   * Ein neu angelegter Scheduler wird unmittelbar persisitiert und unter dem Namen inklusive Prefix abgespeichert.
   * </p>
   * 
   * @param schedulerDO
   * @return
   * @see #schedulers
   */
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
      final SchedulerDO schedulerDB = getJobStore().createOrGetScheduler(schedulerName);
      if (schedulerDB.getPk() != SchedulerDO.UNSAVED_SCHEDULER_ID) {
        schedulerDO.setPk(schedulerDB.getPk());
        if (GLog.isTraceEnabled() == true) {
          GLog.trace(GenomeLogCategory.Scheduler,
              "Reuse existing DB-Sheduler entrie. scheduler: " + schedulerName + "#" + schedulerDB.getPk());
        }

      } else {
        schedulerDO.setName(schedulerDB.getName());
        /**
         * @logging
         * @reason Chronos Ein noch nicht existierender Scheduler wird in die DB geschrieben.
         * @action Keine
         */
        GLog.note(GenomeLogCategory.Scheduler, "Create a new DB-Entry for scheduler: " + schedulerName);

        // im Zweifelsfall mit neuen Werten überschreiben, sonst nur neu
        // anlegen
        getJobStore().persist(schedulerDO);
      }
      // setzen der Id aus der Datenbank !
      scheduler.setSchedulerId(schedulerDO.getPk());
      // Halten mit Prefix
      schedulers.put(schedulerName, scheduler);
      // jobs.put(schedulerName, new ArrayList<TriggerJobDO>());
      return scheduler;
    } // synchronized end
  }

  /**
   * 
   * 
   * @see #submit(String, JobDefinition, Object, Trigger, boolean)
   * @param schedulerName
   * @param jobDefinition
   * @param arg
   * @param trigger
   */
  @Override
  public void submit(final String schedulerName, final JobDefinition jobDefinition, final Object arg,
      final Trigger trigger)
  {
    submit(schedulerName, (String) null, jobDefinition, arg, trigger, getVirtualHostName());
  }

  /**
   * 
   * 
   * @see #submit(String, JobDefinition, Object, Trigger, boolean)
   * @param schedulerName
   * @param jobDefinition
   * @param arg
   * @param trigger
   */
  @Override
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
  public synchronized long submit(final String schedulerName, String jobName, final JobDefinition jobDefinition,
      final Object arg,
      final Trigger trigger, String hostName)
  {
    if (hostName == null) {
      hostName = getVirtualHost();
    }

    final Scheduler scheduler = getScheduler(schedulerName);
    if (scheduler == null) {
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
    TriggerJobDO job = getJobStore().buildTriggerJob(scheduler, jobName, jobDefinition, arg, trigger, hostName,
        State.WAIT);

    boolean dispatcherAndSchedulerRunning = isRunning() && scheduler.isRunning();

    boolean isLocalHost = false;
    if (StringUtils.equals(hostName, getVirtualHostName()) == true) {
      isLocalHost = true;
    }

    boolean startJobNow = false;
    boolean addToLocalJobQueue = false;
    if (dispatcherAndSchedulerRunning == true) {
      if (isLocalHost == true) {
        Date now = new Date();
        Date nt = trigger.getNextFireTime(now);

        if (nt.getTime() - now.getTime() < 3) {
          startJobNow = true;
        } else {
          addToLocalJobQueue = true;
        }
      }
    }
    getJobStore().insertJob(job);
    if (startJobNow == true) {
      boolean started = scheduler.executeJob(job, getJobStore());
      if (started == false) {
        reservedJobs.addReservedJob(job);
        wakeup();
      }
    } else if (addToLocalJobQueue == true) {
      reservedJobs.addReservedJob(job);
      wakeup();
    }
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
}
