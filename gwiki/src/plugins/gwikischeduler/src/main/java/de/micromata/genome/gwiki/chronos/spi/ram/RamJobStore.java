/////////////////////////////////////////////////////////////////////////////
//
// Project   Micromata Genome Core
//
// Author    roger@micromata.de
// Created   21.01.2008
// Copyright Micromata 21.01.2008
//
/////////////////////////////////////////////////////////////////////////////
package de.micromata.genome.gwiki.chronos.spi.ram;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections15.ListUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.Validate;

import de.micromata.genome.gwiki.chronos.Scheduler;
import de.micromata.genome.gwiki.chronos.State;
import de.micromata.genome.gwiki.chronos.Trigger;
import de.micromata.genome.gwiki.chronos.logging.GLog;
import de.micromata.genome.gwiki.chronos.logging.GenomeLogCategory;
import de.micromata.genome.gwiki.chronos.spi.AbstractJobStore;
import de.micromata.genome.gwiki.chronos.spi.jdbc.JobResultDO;
import de.micromata.genome.gwiki.chronos.spi.jdbc.SchedulerDO;
import de.micromata.genome.gwiki.chronos.spi.jdbc.SchedulerDisplayDO;
import de.micromata.genome.gwiki.chronos.spi.jdbc.TriggerJobDO;
import de.micromata.genome.gwiki.chronos.spi.jdbc.TriggerJobDisplayDO;

/**
 * Abbildung der Jobs im Memory
 * 
 * TODO genome chronos synchronized ggf nicht immer auf this (skalliert schlecht)
 * 
 * TODO genome chronos WAIT Jobs in eigener sortieren Liste halten
 * 
 * @author roger@micromata.de
 * 
 */
public class RamJobStore extends AbstractJobStore
{
  protected Map<String, SchedulerDO> schedulers = new HashMap<String, SchedulerDO>();

  protected final Map<String, Map<Long, TriggerJobDO>> allJobs = new HashMap<String, Map<Long, TriggerJobDO>>();

  protected final Map<Long, List<JobResultDO>> jobResults = new HashMap<Long, List<JobResultDO>>();

  protected long nextJobId = 0;

  protected long nextSchedulerId = 0;

  protected long nextResultId = 0;

  public SchedulerDO createOrGetScheduler(String schedulerName)
  {
    synchronized (this) {
      SchedulerDO ret = schedulers.get(schedulerName);
      if (ret != null)
        return ret;
      ret = new SchedulerDO();
      ret.setName(schedulerName);
      ret.setPk(++nextSchedulerId);
      schedulers.put(schedulerName, ret);
      return ret;
    }
  }

  @SuppressWarnings("unchecked")
  public List<TriggerJobDO> getJobs(Scheduler scheduler, Date fromDate, Date untilDate, State state)
  {
    final Map<Long, TriggerJobDO> tjobs;
    synchronized (allJobs) {
      tjobs = allJobs.get(scheduler.getName());
    }
    if (tjobs == null)
      return ListUtils.EMPTY_LIST;
    List<TriggerJobDO> ret = new ArrayList<TriggerJobDO>();
    synchronized (tjobs) {
      ret.addAll(tjobs.values());
    }
    return ret; // TODO genome chronos filter
    // Date now = new Date();
    // for (final TriggerJobDO job : allJobs) {
    // Date fire = job.getNextFireTime(now);
    // if (fire == null)
    // continue;
    //
    // }
  }

  public List<TriggerJobDO> getNextJobs(Scheduler scheduler, boolean foreignJobs)
  {
    final Map<Long, TriggerJobDO> tjobs;
    synchronized (allJobs) {
      tjobs = allJobs.get(scheduler.getName());
    }

    if (tjobs == null || tjobs.isEmpty() == true)
      return ListUtils.EMPTY_LIST;

    final ArrayList<TriggerJobDO> jobsToStart = new ArrayList<TriggerJobDO>();
    Date now = new Date();
    boolean isDebugEnabled = GLog.isDebugEnabled();
    synchronized (tjobs) {
      for (final TriggerJobDO job : tjobs.values()) {
        if (job.getState() != State.WAIT)
          continue;
        final Trigger trigger = job.getTrigger();
        final Date nextFireTime = trigger.getNextFireTime(now);
        if (nextFireTime != null && nextFireTime.before(now)) {
          if (isDebugEnabled == true)
            GLog.debug(GenomeLogCategory.Scheduler, "Found trigger: " + trigger);
          jobsToStart.add(job);
        }
      }
    }
    return jobsToStart;
  }

  public List<TriggerJobDO> getNextJobs(long lookForward)
  {
    boolean isDebugEnabled = GLog.isDebugEnabled();
    final ArrayList<TriggerJobDO> jobsToStart = new ArrayList<TriggerJobDO>();
    Date vnow = new Date(System.currentTimeMillis() + lookForward);
    synchronized (allJobs) {
      for (Map<Long, TriggerJobDO> tjobs : allJobs.values()) {
        synchronized (tjobs) {
          for (final TriggerJobDO job : tjobs.values()) {
            if (job.getState() != State.WAIT)
              continue;
            final Trigger trigger = job.getTrigger();
            final Date nextFireTime = job.getNextFireTime();
            if (nextFireTime != null && nextFireTime.before(vnow)) {
              if (isDebugEnabled == true)
                GLog.debug(GenomeLogCategory.Scheduler, "Found trigger: " + trigger);
              jobsToStart.add(job);
            }
          }
        }
      }
    }
    return jobsToStart;
  }

  public String getPrefix()
  {
    return null;
  }

  @SuppressWarnings("unchecked")
  public synchronized List<JobResultDO> getResults(TriggerJobDO job, int maxResults)
  {

    List<JobResultDO> tl;
    synchronized (jobResults) {
      tl = jobResults.get(job);
    }

    if (tl == null)
      return ListUtils.EMPTY_LIST;

    if (maxResults >= tl.size())
      return tl;
    List<JobResultDO> ret = new ArrayList<JobResultDO>(maxResults);
    synchronized (tl) {
      for (int i = 0; i < maxResults; ++i) {
        ret.add(tl.get(i));
      }
    }
    return ret;
  }

  public List<SchedulerDO> getSchedulers()
  {
    List<SchedulerDO> ret = new ArrayList<SchedulerDO>();
    synchronized (schedulers) {
      ret.addAll(schedulers.values());
    }
    return ret;
  }

  public void withinTransaction(final Runnable runnable)
  {
    runnable.run();
  }

  public void jobRemove(TriggerJobDO job, JobResultDO jobResult, Scheduler scheduler)
  {
    if (GLog.isTraceEnabled() == true)
      GLog.trace(GenomeLogCategory.Scheduler, "jobRemove: " + job.getPk() + "; " + job.toString());
    Map<Long, TriggerJobDO> schedJobs;
    synchronized (allJobs) {
      schedJobs = allJobs.get(scheduler.getName());
    }
    TriggerJobDO removed;
    synchronized (schedJobs) {
      removed = schedJobs.remove(job.getPk());
    }
    if (removed == null) {
      GLog.warn(GenomeLogCategory.Scheduler, "Job with ID cannot be removed: " + job.getPk());
    }
    synchronized (jobResults) {
      jobResults.remove(job.getPk());
    }

  }

  public void jobResultRemove(TriggerJobDO job, JobResultDO jobResult, Scheduler scheduler)
  {

    List<JobResultDO> resList;
    synchronized (jobResults) {
      resList = jobResults.get(job.getPk());
    }
    if (resList == null)
      return;
    synchronized (resList) {
      resList.remove(jobResult);
    }
  }

  public void persist(SchedulerDO scheduler)
  {
    synchronized (schedulers) {
      schedulers.put(scheduler.getName(), scheduler);
      if (scheduler.getPk() == null || scheduler.getPk() == SchedulerDO.UNSAVED_SCHEDULER_ID)
        scheduler.setPk(nextSchedulerId++);

    }

  }

  public TriggerJobDO reserveJob(TriggerJobDO job)
  {
    job.setState(State.SCHEDULED);
    updateJob(job);
    return job;
  }

  public void setPrefix(String prefix)
  {
  }

  public void shutdown() throws InterruptedException
  {

  }

  public synchronized long getNextJobId()
  {
    return ++nextJobId;
  }

  public synchronized long getNextJobResultId()
  {
    return ++nextResultId;
  }

  public synchronized long getNextSchedulerId()
  {
    return ++nextSchedulerId;
  }

  public void insertJob(TriggerJobDO job)
  {
    Validate.notNull(job);
    Validate.notNull(job.getSchedulerName());
    Map<Long, TriggerJobDO> list;
    synchronized (allJobs) {
      list = allJobs.get(job.getSchedulerName());

      if (list == null) {
        list = new HashMap<Long, TriggerJobDO>();
        allJobs.put(job.getSchedulerName(), list);
      }
    }
    if (GLog.isTraceEnabled() == true)
      GLog.trace(GenomeLogCategory.Scheduler, "insertJob: " + job.getPk());
    synchronized (list) {
      list.put(job.getPk(), job);
    }

  }

  public void insertResult(JobResultDO result)
  {
    List<JobResultDO> l;
    synchronized (jobResults) {
      l = jobResults.get(result.getJobPk());
      if (l == null) {
        l = new ArrayList<JobResultDO>();
        jobResults.put(result.getJobPk(), l);
      }
    }
    synchronized (l) {
      l.add(result);
    }
  }

  public void updateJob(TriggerJobDO job)
  {
    Validate.notNull(job);
    Validate.notNull(job.getSchedulerName());
    if (GLog.isTraceEnabled() == true)
      GLog.trace(GenomeLogCategory.Scheduler, "updateJob: " + job.getPk() + "; " + job.toString());

    Map<Long, TriggerJobDO> c;
    synchronized (allJobs) {
      c = allJobs.get(job.getSchedulerName());
    }
    if (c == null) {
      // TODO genome sched warn
      return;
    }
    synchronized (c) {
      c.put(job.getPk(), job);
    }
  }

  // TODO genome chronos synchronize
  public TriggerJobDO getAdminJobByPk(long pk)
  {
    for (Map<Long, TriggerJobDO> m : allJobs.values()) {
      if (m.containsKey(pk) == true)
        return m.get(pk);
    }
    return null;
  }

  public List<TriggerJobDisplayDO> getAdminJobs(String hostName, String name, String state, String schedulerName, int resultCount)
  {
    List<TriggerJobDisplayDO> ret = new ArrayList<TriggerJobDisplayDO>();
    for (Map.Entry<String, Map<Long, TriggerJobDO>> m : allJobs.entrySet()) {
      if (StringUtils.isNotEmpty(schedulerName) == true && StringUtils.equals(schedulerName, m.getKey()) == false)
        continue;
      for (Map.Entry<Long, TriggerJobDO> e : m.getValue().entrySet()) {
        if (StringUtils.isNotEmpty(hostName) && StringUtils.equals(hostName, e.getValue().getHostName()) == false)
          continue;
        if (StringUtils.isNotEmpty(state) && StringUtils.equals(state, e.getValue().getState().name()) == false)
          continue;
        TriggerJobDisplayDO tjd = new TriggerJobDisplayDO(e.getValue());
        ret.add(tjd);
        // TODO genome/RamJobStore Results
      }

    }
    return ret;
  }

  public List<SchedulerDisplayDO> getAdminSchedulers()
  {
    List<SchedulerDisplayDO> ret = new ArrayList<SchedulerDisplayDO>();
    for (SchedulerDO s : schedulers.values()) {
      ret.add(new SchedulerDisplayDO(s));
    }
    return ret;
  }

  public TriggerJobDO getJobByPk(long pk)
  {
    return getAdminJobByPk(pk);
  }

  public List<JobResultDO> getResultsForJob(long jobId)
  {
    return jobResults.get(jobId);
  }

  public int setJobState(long pk, String newState, String oldState)
  {
    for (Map.Entry<String, Map<Long, TriggerJobDO>> m : allJobs.entrySet()) {
      if (m.getValue().containsKey(pk) == true) {
        TriggerJobDO tj = m.getValue().get(pk);
        if (StringUtils.equals(oldState, tj.getState().name()) == true) {
          tj.setState(State.valueOf(newState));
          return 1;
        }
        return 0;
      }
    }
    return 0;
  }

  public long getJobCount(State state)
  {
    long result = 0;
    if (state == null) {
      for (Map<Long, TriggerJobDO> v : allJobs.values()) {
        result += v.size();
      }
    } else {
      for (Map<Long, TriggerJobDO> v : allJobs.values()) {
        for (TriggerJobDO tj : v.values()) {
          if (tj.getState() == state)
            ++result;
        }
      }
    }
    return result;
  }

  public long getJobResultCount(State state)
  {
    long result = 0;
    for (List<JobResultDO> lr : jobResults.values()) {
      if (state == null)
        result += lr.size();
      else {
        for (JobResultDO jr : lr) {
          if (jr.getState() == state)
            ++result;
        }
      }
    }
    return result;
  }

  public JobResultDO getResultByPk(long resultId)
  {
    for (List<JobResultDO> lr : jobResults.values()) {
      for (JobResultDO rs : lr) {
        if (rs.getPk() == resultId)
          return rs;
      }
    }
    // TODO genome throw ex
    return null;
  }

  public void deleteScheduler(Long pk)
  {
    // TODO lado implement
  }

  public List<String> getJobNames()
  {
    List<String> jobNames = new ArrayList<String>();
    for (Map<Long, TriggerJobDO> m : allJobs.values()) {
      for (TriggerJobDO j : m.values()) {
        jobNames.add(j.getJobName());
      }
    }

    return jobNames;
  }

}
