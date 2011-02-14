/////////////////////////////////////////////////////////////////////////////
//
// Project   Micromata Genome Core
//
// Author    roger@micromata.de
// Created   09.01.2008
// Copyright Micromata 09.01.2008
//
/////////////////////////////////////////////////////////////////////////////
package de.micromata.genome.gwiki.chronos.manager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.InitializingBean;

import de.micromata.genome.gwiki.chronos.JobDefinition;
import de.micromata.genome.gwiki.chronos.JobStore;
import de.micromata.genome.gwiki.chronos.Scheduler;
import de.micromata.genome.gwiki.chronos.StaticDaoManager;
import de.micromata.genome.gwiki.chronos.Trigger;
import de.micromata.genome.gwiki.chronos.logging.GLog;
import de.micromata.genome.gwiki.chronos.logging.GenomeLogCategory;
import de.micromata.genome.gwiki.chronos.logging.LogLevel;
import de.micromata.genome.gwiki.chronos.logging.LoggedRuntimeException;
import de.micromata.genome.gwiki.chronos.spi.Dispatcher;
import de.micromata.genome.gwiki.chronos.spi.DispatcherImpl;
import de.micromata.genome.gwiki.chronos.spi.jdbc.SchedulerDO;
import de.micromata.genome.gwiki.chronos.spi.jdbc.TriggerJobDO;
import de.micromata.genome.gwiki.chronos.util.CronTrigger;
import de.micromata.genome.gwiki.chronos.util.DelayTrigger;
import de.micromata.genome.gwiki.chronos.util.FixedTrigger;
import de.micromata.genome.gwiki.chronos.util.SchedulerFactory;
import de.micromata.genome.util.text.PipeValueList;
import de.micromata.genome.util.types.TimeInMillis;

/**
 * Service-Klasse zum vereinfachten Handling mit Schedulern und Jobs.
 * <p>
 * Im Augenblick wird hier nur ein {@link DispatcherImpl} unterstützt.
 * </p>
 * 
 * 
 * TODO genome konfigurierbar ueber DaoManager machen
 * 
 * @author roger
 */
public class SchedulerManager implements InitializingBean
{
  /**
   * Must be static
   */
  private static Dispatcher dispatcher;

  /**
   * predefined Jobs
   */
  private Map<String, JobBeanDefinition> jobs = new HashMap<String, JobBeanDefinition>(0);

  private String virtualHostName = null;

  private long minRefreshInMillis = 10L;

  private long startRefreshInMillis = 250L;

  private long maxRefreshInMillis = 4000L;

  /**
   * Minimale Nodebindtimeout (Dispatcher2)
   */
  private long minNodeBindTime = TimeInMillis.MINUTE;

  private List<SchedulerFactory> scheduleFactories = new ArrayList<SchedulerFactory>();

  private Map<String, List<JobRunnerFilter>> schedulerFilter = new HashMap<String, List<JobRunnerFilter>>();

  private List<JobRunnerFilter> globalFilter = new ArrayList<JobRunnerFilter>();

  private Map<String, List<JobRunnerFilter>> mergedFilter = null;

  public void init()
  {
    if (dispatcher != null) {
      dispatcher.setMinNodeBindTime(minNodeBindTime);
      return;
    }

    try {
      dispatcher = StaticDaoManager.get().getSchedulerDAO().createDispatcher(virtualHostName);

      dispatcher.startup();

    } catch (Exception ex) {
      /**
       * @logging
       * @reason Ein Fehler beim Initialisieren des Schedulers ist aufgetreten
       * @action Datenbankverbindung ueberpruefen
       */
      throw new LoggedRuntimeException(ex, LogLevel.Fatal, GenomeLogCategory.Scheduler, "Cannot initialize JobStore");
    }
  }

  /**
   * Instance des SchedulerManager
   * 
   * @return Kann null zurückgeben, wenn der Scheduler in ContextPop nicht definiert ist!
   */
  public static SchedulerManager get()
  {
    SchedulerManager manager = StaticDaoManager.get().getSchedulerManager();
    // GenomeDaoManager gdm = GenomeDaoManager.get();
    // DynBeanConfig dbc = (DynBeanConfig) gdm.getTimependingDAO().getVersionResource(null, "CONTEXT_CHRONOS", false);
    // if (dbc == null)
    // return null;
    // SchedulerManager manager = (SchedulerManager) dbc.getBean("schedulerManager");
    manager.init();
    return manager;
  }

  public void afterPropertiesSet() throws Exception
  {

    if (scheduleFactories == null) {
      /**
       * @logging
       * @reason In der ContextPop sind keine Scheduler definiert.
       * @action Techadmin
       */
      GLog.error(GenomeLogCategory.Scheduler, "No SchedulerFactories configured in ContextPop"); // TODO anpassen
      return;
    }
    if (jobs == null) {
      /**
       * @logging
       * @reason In der ContextPop sind keine Jobs verwendet.
       * @action Techadmin
       */
      GLog.error(GenomeLogCategory.Scheduler, "No Jobs configured in ContextPop"); // TODO anpassen
      return;
    }
    for (Map.Entry<String, JobBeanDefinition> me : jobs.entrySet()) {
      me.getValue().setBeanName(me.getKey());
    }
    init();
    for (SchedulerFactory sf : scheduleFactories) {
      sf.setDispatcher(this.getDispatcher());
      if (dispatcher.getScheduler(sf.getSchedulerName()) == null)
        sf.create(this.getDispatcher().getJobStore());
    }

  }

  public synchronized List<JobRunnerFilter> getFilters(String schedulerName)
  {
    if (mergedFilter != null && mergedFilter.get(schedulerName) != null)
      return mergedFilter.get(schedulerName);
    if (mergedFilter == null) {
      mergedFilter = new HashMap<String, List<JobRunnerFilter>>();
    }
    List<JobRunnerFilter> res = new ArrayList<JobRunnerFilter>();
    res.addAll(globalFilter);
    List<JobRunnerFilter> l = schedulerFilter.get(schedulerName);
    if (l != null)
      res.addAll(l);
    Collections.sort(res, new Comparator<JobRunnerFilter>() {

      public int compare(JobRunnerFilter o1, JobRunnerFilter o2)
      {
        return o1.getPriority() - o2.getPriority();
      }
    });
    mergedFilter.put(schedulerName, res);
    return res;
  }

  /**
   * Erzeugt den Scheduler, wenn noch nicht da ist.
   */
  public Scheduler getScheduler(String name)
  {
    return getScheduler(name, true);
  }

  public Scheduler getScheduler(final String name, final boolean createByDefault)
  {

    Scheduler sched = dispatcher.getScheduler(name);

    if (sched != null) {
      return sched;
    }

    if (createByDefault == false) {
      return null;
    }
    // we have to create a new one
    SchedulerDO schedulerDO = new SchedulerDO();
    schedulerDO.setName(name);
    schedulerDO.setThreadPoolSize(1);
    schedulerDO.setServiceRetryTime(60); // TODO not hardcoded
    schedulerDO.setJobRetryTime(30);
    schedulerDO.setNodeBindingTimeout(0);
    sched = dispatcher.createOrGetScheduler(schedulerDO);
    return sched;
  }

  /**
   * @param name Standard Job name
   * @return den Job. Falls Job nicht gefunden werden kann, wird eine LoggedRuntimeException geworfen
   */
  public JobBeanDefinition getJobDefinition(String name)
  {
    if (jobs == null) {
      return null;
    }
    JobBeanDefinition jdef = jobs.get(name);
    if (jdef == null) {
      /**
       * @logging
       * @reason Der angegebene Job kann nicht gefunden werden
       * @action Techadmin kontaktieren, Konfiguration ueberpruefen
       */
      throw new LoggedRuntimeException(LogLevel.Fatal, GenomeLogCategory.Scheduler, "Standard Scheduler Job cannot be found: " + name);
    }
    return jdef;
  }

  /**
   * Delegate zum internen {@link SchedulerManager#dispatcher#submit(String, JobDefinition, Object, Trigger)}.
   */
  public long submit(final String schedulerName, final JobDefinition jobDefinition, final Object arg, final Trigger trigger)
  {
    return submit(schedulerName, null, jobDefinition, arg, trigger);
  }

  /**
   * Delegate zum internen {@link SchedulerManager#dispatcher#submit(String, JobDefinition, Object, Trigger)}.
   */
  public long submit(final String schedulerName, String jobName, final JobDefinition jobDefinition, final Object arg, final Trigger trigger)
  {
    return dispatcher.submit(schedulerName, jobName, jobDefinition, arg, trigger, null);
  }

  public long submit(final String schedulerName, final JobDefinition jobDefinition, final Object arg, final Trigger trigger, String hostName)
  {
    return submit(schedulerName, null, jobDefinition, arg, trigger, hostName);
  }

  public long submit(final String schedulerName, String jobName, final JobDefinition jobDefinition, final Object arg,
      final Trigger trigger, String hostName)
  {
    return dispatcher.submit(schedulerName, jobDefinition, arg, trigger, hostName);
  }

  /**
   * Starts job on each known and running node.
   * 
   * This uses InterNodeCallDAO.getRunningNodes() to get running nodes.
   * 
   * Delegate zum internen {@link SchedulerManager#dispatcher#submit(String, JobDefinition, Object, Trigger, String)}.
   * 
   * @param schedulerName The scheduler must have a high node bind timeout.
   * @param jobDefinition
   * @param arg
   * @param trigger
   */
  public void submitOnEachNode(final String schedulerName, final JobDefinition jobDefinition, final Object arg, final Trigger trigger)
  {
    submitOnEachNode(schedulerName, null, jobDefinition, arg, trigger);

  }

  /**
   * Starts job on each known and running node.
   * 
   * This uses InterNodeCallDAO.getRunningNodes() to get running nodes.
   * 
   * Delegate zum internen {@link SchedulerManager#dispatcher#submit(String, JobDefinition, Object, Trigger, String)}.
   * 
   * @param schedulerName The scheduler must have a high node bind timeout.
   * @param jobDefinition
   * @param arg
   * @param trigger
   */
  public void submitOnEachNode(final String schedulerName, String jobName, final JobDefinition jobDefinition, final Object arg,
      final Trigger trigger)
  {
    // List<Pair<String, Date>> nodeList = GenomeDaoManager.get().getInterNodeCallDAO().getRunningNodes();
    // for (Pair<String, Date> nn : nodeList) {
    // dispatcher.submit(schedulerName, jobName, jobDefinition, arg, trigger, nn.getFirst());
    // }
    throw new UnsupportedOperationException();
  }

  public void submitStdAdminJob(String jobId, final Map<String, String> args)
  {
    JobBeanDefinition jdef = getJobDefinition(jobId);
    Trigger trigger = TriggerJobDO.parseTrigger(jdef.getTriggerDefinition());
    String sarg = PipeValueList.encode(args);
    submit(jdef.getSchedulerName(), jdef.getJobName(), jdef.getJobDefinition(), sarg, trigger);
  }

  public long submitStdJob(String jobId, final Object arg)
  {
    JobBeanDefinition jdef = getJobDefinition(jobId);
    Trigger trigger = TriggerJobDO.parseTrigger(jdef.getTriggerDefinition());
    return submit(jdef.getSchedulerName(), jdef.getJobName(), jdef.getJobDefinition(), arg, trigger);
  }

  /**
   * Delegate zu {@link SchedulerManager#dispatcher#persist(SchedulerImpl)} .
   * 
   * @param scheduler
   */
  public void persist(SchedulerDO scheduler)
  {
    dispatcher.persist(scheduler);
  }

  /**
   * Delegate zu {@link SchedulerManager#dispatcher#avoidNewJobs(String)} .
   * 
   * @param scheduler
   */
  public void denyNewJobs(final String name)
  {
    dispatcher.denyNewJobs(name);
  }

  /**
   * Delegate zu {@link SchedulerManager#dispatcher#setJobCount(int, String)}.
   * 
   * @param scheduler
   */
  public void setJobCount(final int size, final String schedulerName)
  {
    dispatcher.setJobCount(size, schedulerName);
  }

  // TODO (Rx) rrk wird das ueberhaupt noch verwendet
  public List<SchedulerFactory> getScheduleFactories()
  {
    return scheduleFactories;
  }

  public void setScheduleFactories(List<SchedulerFactory> scheduleFactories)
  {
    this.scheduleFactories = scheduleFactories;
    for (SchedulerFactory fac : scheduleFactories) {
      fac.setDispatcher(dispatcher);
      fac.setStartOnCreate(true);
    }
  }

  public static Trigger createTriggerDefinition(final String definition)
  {
    if (definition.startsWith("+")) {
      return new DelayTrigger(definition);
    } else if (definition.startsWith("!") == true) {
      return new FixedTrigger(definition);
    } else {
      return new CronTrigger(definition);
    }
  }

  public JobStore getJobStore()
  {
    return dispatcher.getJobStore();
  }

  public Dispatcher getDispatcher()
  {
    return dispatcher;
  }

  public void setJobs(Map<String, JobBeanDefinition> jobs)
  {
    this.jobs = jobs;
  }

  /**
   * Delegiert zu {@link DispatcherImpl#shutdown()}.
   * 
   * @throws InterruptedException
   */
  public void shutdown() throws InterruptedException
  {
    dispatcher.shutdown();
  }

  public void startup()
  {
    dispatcher.startup();
  }

  public String getVirtualHostName()
  {
    return virtualHostName;
  }

  public void setVirtualHostName(String virtualHostName)
  {
    this.virtualHostName = virtualHostName;
  }

  public long getMinNodeBindTime()
  {
    return minNodeBindTime;
  }

  public void setMinNodeBindTime(long minNodeBindTime)
  {
    this.minNodeBindTime = minNodeBindTime;
    if (dispatcher != null)
      dispatcher.setMinNodeBindTime(minNodeBindTime);
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
}
