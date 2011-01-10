/////////////////////////////////////////////////////////////////////////////
//
// $RCSfile: ResultDO.java,v $
//
// Project   chronos
//
// Author    Wolfgang Jung (w.jung@micromata.de)
// Created   15.01.2007
// Copyright Micromata 15.01.2007
//
// $Id: ResultDO.java,v 1.8 2007/03/16 13:34:18 noodles Exp $
// $Revision: 1.8 $
// $Date: 2007/03/16 13:34:18 $
//
/////////////////////////////////////////////////////////////////////////////
package de.micromata.genome.gwiki.chronos.spi.jdbc;

import java.util.Date;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.builder.ToStringBuilder;

import de.micromata.genome.dao.db.StdRecordDO;
import de.micromata.genome.gwiki.chronos.State;

public class JobResultDO extends StdRecordDO
{
  /**
   * 
   */
  private static final long serialVersionUID = -7696253063378461680L;

  // private long id;

  private long jobPk;

  private State state;

  private long duration;

  private Date executionStart;

  private int retryCount;

  // private boolean active = true;

  private String hostName;

  private String vm;

  private Object resultObject;

  private String resultString;

  // public long getId()
  // {
  // return id;
  // }

  // public void setId(long id)
  // {
  // this.id = id;
  // }

  public long getJobPk()
  {
    return jobPk;
  }

  public void setJobPk(long jobId)
  {
    this.jobPk = jobId;
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
    return state == null ? "UNDEFINED" : state.name();
  }

  public void setStateString(String state)
  {
    this.state = State.valueOf(state);
  }

  public Object getResultObject()
  {
    if (resultObject == null) {
      // resultObject = SerializationUtil.deserialize(resultString, null);
    }
    return resultObject;
  }

  public void setResultObject(Object resultObject)
  {
    this.resultString = null;
    this.resultObject = resultObject;
  }

  public String getResultString()
  {
    if (resultString != null)
      return resultString;
    // resultString = SerializationUtil.serialize(resultObject);
    return resultString;
  }

  public String getResultStringForDB()
  {
    return StringUtils.substring(getResultString(), 0, 1290);
  }

  public void setResultString(String resultString)
  {
    this.resultString = resultString;
    this.resultObject = null;
  }

  public long getDuration()
  {
    return duration;
  }

  public void setDuration(long duration)
  {
    this.duration = duration;
  }

  public Date getExecutionStart()
  {
    return executionStart;
  }

  public void setExecutionStart(Date executionStart)
  {
    this.executionStart = executionStart;
  }

  public String getHostName()
  {
    return hostName;
  }

  public void setHostName(String hostName)
  {
    this.hostName = hostName;
  }

  public String getVm()
  {
    return vm;
  }

  public void setVm(String vm)
  {
    this.vm = vm;
  }

  public int getRetryCount()
  {
    return retryCount;
  }

  public void setRetryCount(int retryCount)
  {
    this.retryCount = retryCount;
  }

  @Override
  public String toString()
  {
    final ToStringBuilder sb = new ToStringBuilder(this);

    sb.append("id", getPk()).append("jobId", jobPk).append("state", state).append("duration", duration).append("retryCount", retryCount)
        .append("hostName", hostName).append("resultString", getResultString()).append("createdAt", getCreatedAtString()).append(
            "modifiedAt", getModifiedAtString());
    return sb.toString();
  }
}
