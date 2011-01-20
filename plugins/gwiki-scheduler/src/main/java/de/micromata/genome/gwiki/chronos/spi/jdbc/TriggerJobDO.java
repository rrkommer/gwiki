/////////////////////////////////////////////////////////////////////////////
//
// $RCSfile: TriggerJobDO.java,v $
//
// Project   genome
//
// Author    Wolfgang Jung (w.jung@micromata.de)
// Created   09.01.2007
// Copyright Micromata 09.01.2007
//
// $Id: TriggerJobDO.java,v 1.17 2007-12-16 07:29:11 roger Exp $
// $Revision: 1.17 $
// $Date: 2007-12-16 07:29:11 $
//
/////////////////////////////////////////////////////////////////////////////
package de.micromata.genome.gwiki.chronos.spi.jdbc;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.builder.ToStringBuilder;

import de.micromata.genome.dao.db.StdRecordDO;
import de.micromata.genome.gwiki.chronos.FutureJob;
import de.micromata.genome.gwiki.chronos.JobDefinition;
import de.micromata.genome.gwiki.chronos.JobStore;
import de.micromata.genome.gwiki.chronos.State;
import de.micromata.genome.gwiki.chronos.Trigger;
import de.micromata.genome.gwiki.chronos.util.CronTrigger;
import de.micromata.genome.gwiki.chronos.util.DelayTrigger;
import de.micromata.genome.util.types.Converter;

/**
 * Datenbank-Sicht auf einen Job mit dem entsprechenden Auslöser(Trigger).
 * 
 */
public class TriggerJobDO extends StdRecordDO // implements /* Trigger,
// */Job
{

  private static final long serialVersionUID = 593615016974976998L;

  private JobStore jobStore;

  private String jobDefinitionString;

  private JobDefinition jobDefinitionObject;

  private Trigger triggerDefintionObject;

  private String triggerDefintionString;

  private String jobName;

  public String getJobName()
  {
    return jobName;
  }

  public void setJobName(String jobName)
  {
    this.jobName = jobName;
  }

  private String jobArgumentString;

  private Object jobArgumentObject;

  private Date nextFireTime;

  private Date lastScheduledTime;

  private int retryCount = 0;

  private long scheduler;

  private Long currentResultPk = null;

  private JobResultDO result = null;

  private State state;

  private String hostName = StringUtils.EMPTY;

  private transient Map<String, Object> attributes = new HashMap<String, Object>();

  /**
   * Name des Schedulers, wird nur bei views gesetzt
   */
  private transient String schedulerName;

  public TriggerJobDO()
  {

  }

  public TriggerJobDO(TriggerJobDO other)
  {
    this.jobStore = other.jobStore;
    this.jobDefinitionString = other.jobDefinitionString;
    this.jobName = other.jobName;
    this.jobDefinitionObject = other.jobDefinitionObject;
    this.triggerDefintionObject = other.triggerDefintionObject;
    this.triggerDefintionString = other.triggerDefintionString;
    this.pk = other.pk;
    this.jobArgumentString = other.jobArgumentString;
    this.jobArgumentObject = other.jobArgumentObject;
    this.nextFireTime = other.nextFireTime;
    this.lastScheduledTime = other.lastScheduledTime;
    this.retryCount = other.retryCount;
    this.scheduler = other.scheduler;
    this.currentResultPk = other.currentResultPk;
    this.result = other.result;
    this.state = other.state;
    this.hostName = other.hostName;
  }

  @Override
  public String toString()
  {
    final ToStringBuilder sb = new ToStringBuilder(this);
    sb.append("pk", pk);
    if (jobName != null) {
      sb.append("jobName", jobName);
    }
    sb.append("schedulerId", scheduler);
    sb.append("state", state);
    sb.append("trigger", getTriggerDefinition());
    sb.append("jobDefinition", getJobArgumentString());
    return sb.toString();
  }

  public Object getAttribute(String name)
  {
    return attributes.get(name);
  }

  public Object setAttribute(String name, Object value)
  {
    return attributes.put(name, value);
  }

  public Object removeAttribute(String name)
  {
    return attributes.remove(name);
  }

  public void increaseRetryCount()
  {
    retryCount = retryCount + 1;
  }

  public void setJobArguments(final Object jobArguments)
  {
    jobArgumentObject = jobArguments;
    jobArgumentString = null;
  }

  public void setArgumentDefinitionString(final String argDef)
  {
    jobArgumentString = argDef;
    jobArgumentObject = null;
  }

  public String getArgumentDefinitionString()
  {
    if (jobArgumentString != null)
      return jobArgumentString;
    jobArgumentString = SerializationUtil.serializeJobArguments(jobArgumentObject);
    return jobArgumentString;

  }

  public void setJobDefinition(final JobDefinition jobDefinition)
  {
    this.jobDefinitionObject = jobDefinition;
    this.jobDefinitionString = null;
  }

  public void setJobDefinitionString(final String jobDef)
  {
    jobDefinitionString = jobDef;
    jobDefinitionObject = null;
    // jobDefinition = JdbcJobStore.deserialize(jobDef,
    // JobDefinition.class);
  }

  public String getJobDefinitionString()
  {
    if (jobDefinitionString != null)
      return jobDefinitionString;
    jobDefinitionString = SerializationUtil.serializeJobDefinition(jobDefinitionObject);
    return jobDefinitionString;
  }

  public void setJobStore(final JobStore jobStore)
  {
    this.jobStore = jobStore;
  }

  public void setTrigger(final Trigger trigger)
  {
    this.triggerDefintionObject = trigger;
    this.triggerDefintionString = null;
  }

/**
   * An Hand des übergebenen String werden verschiedene Definitionen kreiert.
   * <p>
   * <ul>
   * <li>'+' {@link DelayTrigger}</li>
   * <li>'<' Deserialisieren des Triggers mittels {@link JdbcJobStore#deserialize(String, Class)}</li>
   * <li>Sonst wird ein {@link CronTrigger} angelegt.</li>
   * </ul>
   * </p>
   * 
   * @param definition
   */
  public static Trigger parseTrigger(final String definition)
  {
    if (definition.startsWith("+")) {
      return new DelayTrigger(definition);

      // TODO parse ! with fixed date
      // } else if (definition.startsWith("<")) {
      // return SerializationUtil.deserialize(definition, Trigger.class);
    } else {
      return new CronTrigger(definition);
    }
  }

  public void setTriggerDefinition(final String definition)
  {
    triggerDefintionString = definition;
    triggerDefintionObject = null;
  }

  public Trigger getTrigger()
  {
    if (triggerDefintionObject != null)
      return triggerDefintionObject;
    if (triggerDefintionString == null)
      return null;
    triggerDefintionObject = parseTrigger(triggerDefintionString);
    return triggerDefintionObject;
  }

  public String getTriggerDefinition()
  {
    if (triggerDefintionString != null)
      return triggerDefintionString;
    if (triggerDefintionObject == null)
      return null;
    triggerDefintionString = triggerDefintionObject.getTriggerDefinition();
    return triggerDefintionString;
  }

  /**
   * Hier wird das eigentliche Runtime-Objekt erzeugt.
   * <p>
   * <code>This</code> wird als {@link TriggerJobDO} gesetzt.
   * </p>
   * 
   * @see de.micromata.genome.jchronos.Job#getExecutor()
   */
  public FutureJob getExecutor()
  {
    final FutureJob job = getJobDefinition().getInstance();
    job.setTriggerJobDO(this);
    return job;
  }

  public Object getJobArguments()
  {
    if (jobArgumentObject != null)
      return jobArgumentObject;
    jobArgumentObject = SerializationUtil.deserializeJobArguments(jobArgumentString);
    return jobArgumentObject;
  }

  public String getJobArgumentString()
  {
    if (jobArgumentString != null)
      return jobArgumentString;
    jobArgumentString = SerializationUtil.serializeJobArguments(jobArgumentObject);
    return jobArgumentString;
  }

  public JobDefinition getJobDefinition()
  {
    if (jobDefinitionObject != null)
      return jobDefinitionObject;
    jobDefinitionObject = SerializationUtil.deserializeJobDefinition(jobDefinitionString);
    return jobDefinitionObject;
  }

  public Date getNextFireTime(final Date now)
  {
    nextFireTime = getTrigger().getNextFireTime(now);
    return nextFireTime == null ? null : new Date(nextFireTime.getTime());
  }

  public void setFireTime(final Date nextFireTime)
  {
    this.nextFireTime = nextFireTime == null ? null : new Date(nextFireTime.getTime());
  }

  public Date getFireTime()
  {
    return nextFireTime == null ? null : new Date(nextFireTime.getTime());
  }

  public String getFireTimeString()
  {
    if (nextFireTime == null)
      return null;
    return Converter.formatByIsoTimestampFormat(nextFireTime);
  }

  public Date getLastScheduledTime()
  {
    return lastScheduledTime == null ? null : new Date(lastScheduledTime.getTime());
  }

  public void setLastScheduledTime(final Date lastScheduledTime)
  {
    this.lastScheduledTime = lastScheduledTime == null ? null : new Date(lastScheduledTime.getTime());
  }

  public String getLastScheduleTimeString()
  {
    if (lastScheduledTime == null)
      return null;
    return Converter.formatByIsoTimestampFormat(lastScheduledTime);
  }

  public long getScheduler()
  {
    return scheduler;
  }

  public void setScheduler(final long scheduler)
  {
    this.scheduler = scheduler;
  }

  public long getResultId()
  {
    if (result == null) {
      return -1L;
    }
    return result.getPk();
  }

  public JobResultDO getResult()
  {
    return result;
  }

  public void setResult(JobResultDO result)
  {
    this.result = result;
  }

  public State getState()
  {
    return state;
  }

  public void setState(State state)
  {
    this.state = state;
  }

  public String getStateString()
  {
    return state.name();
  }

  public void setStateString(String state)
  {
    this.state = State.valueOf(state);
  }

  public String getHostName()
  {
    return hostName;
  }

  public void setHostName(String hostName)
  {
    this.hostName = hostName;
  }

  public String getSchedulerName()
  {
    return schedulerName;
  }

  public void setSchedulerName(String schedulerName)
  {
    this.schedulerName = schedulerName;
  }

  public boolean hasFailureResult()
  {
    return currentResultPk != null;
  }

  public Long getCurrentResultPk()
  {
    return currentResultPk;
  }

  public void setCurrentResultPk(Long resultPk)
  {

    this.currentResultPk = resultPk;

  }

  public JobStore getJobStore()
  {
    return jobStore;
  }

  public int getRetryCount()
  {
    return retryCount;
  }

  public void setRetryCount(int retryCount)
  {
    this.retryCount = retryCount;
  }

  public JobDefinition getJobDefinitionObject()
  {
    return jobDefinitionObject;
  }

  public void setJobDefinitionObject(JobDefinition jobDefinitionObject)
  {
    this.jobDefinitionObject = jobDefinitionObject;
  }

  public Trigger getTriggerDefintionObject()
  {
    return triggerDefintionObject;
  }

  public void setTriggerDefintionObject(Trigger triggerDefintionObject)
  {
    this.triggerDefintionObject = triggerDefintionObject;
  }

  public String getTriggerDefintionString()
  {
    return triggerDefintionString;
  }

  public void setTriggerDefintionString(String triggerDefintionString)
  {
    this.triggerDefintionString = triggerDefintionString;
  }

  public Object getJobArgumentObject()
  {
    return jobArgumentObject;
  }

  public void setJobArgumentObject(Object jobArgumentObject)
  {
    this.jobArgumentObject = jobArgumentObject;
  }

  public Date getNextFireTime()
  {
    return nextFireTime;
  }

  public void setNextFireTime(Date nextFireTime)
  {
    this.nextFireTime = nextFireTime;
  }

  public Map<String, Object> getAttributes()
  {
    return attributes;
  }

  public void setAttributes(Map<String, Object> attributes)
  {
    this.attributes = attributes;
  }

  public void setJobArgumentString(String jobArgumentString)
  {
    this.jobArgumentString = jobArgumentString;
  }

}
