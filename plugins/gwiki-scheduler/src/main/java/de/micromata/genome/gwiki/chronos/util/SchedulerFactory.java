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

/////////////////////////////////////////////////////////////////////////////
//
// $RCSfile: SchedulerFactory.java,v $
//
// Project   chronos
//
// Author    Roger Rene Kommer, Wolfgang Jung (w.jung@micromata.de)
// Created   26.12.2006
// Copyright Micromata 26.12.2006
//
// $Id: SchedulerFactory.java,v 1.8 2007-12-28 10:35:53 roger Exp $
// $Revision: 1.8 $
// $Date: 2007-12-28 10:35:53 $
//
/////////////////////////////////////////////////////////////////////////////
package de.micromata.genome.gwiki.chronos.util;

import de.micromata.genome.gwiki.chronos.JobStore;
import de.micromata.genome.gwiki.chronos.Scheduler;
import de.micromata.genome.gwiki.chronos.spi.Dispatcher;
import de.micromata.genome.gwiki.chronos.spi.jdbc.SchedulerDO;

public class SchedulerFactory
{
  private Dispatcher dispatcher;

  private String schedulerName;

  private boolean startOnCreate = true;

  private int startupTimeout;

  private int jobRetryTime = 60;

  private int jobMaxRetryCount = 0;

  private int serviceRetryTime = 60;

  private int threadPoolSize = 2;

  private int nodeBindingTimeout = 0;

  public void setSchedulerName(final String schedulerName)
  {
    this.schedulerName = schedulerName;
  }

  public void setStartOnCreate(final boolean startOnCreate)
  {
    this.startOnCreate = startOnCreate;
  }

  public void setStartupTimeout(final int startupTimeoutInSeconds)
  {
    this.startupTimeout = startupTimeoutInSeconds;
  }

  public void setServiceRetryTime(final int serviceRetryTimeInSeconds)
  {
    this.serviceRetryTime = serviceRetryTimeInSeconds;
  }

  public void setJobRetryTime(final int jobRetryTimeInSeconds)
  {
    this.jobRetryTime = jobRetryTimeInSeconds;
  }

  public void setThreadPoolSize(int threadPoolSize)
  {
    this.threadPoolSize = threadPoolSize;
  }

  public Scheduler create(JobStore jobStore)
  {
    final SchedulerDO scheduler = new SchedulerDO();
    scheduler.setName(schedulerName);
    scheduler.setServiceRetryTime(serviceRetryTime);
    scheduler.setJobRetryTime(jobRetryTime);
    scheduler.setJobMaxRetryCount(jobMaxRetryCount);
    scheduler.setThreadPoolSize(threadPoolSize);
    scheduler.setNodeBindingTimeout(nodeBindingTimeout);
    jobStore.persist(scheduler);
    final Scheduler schedulerL = dispatcher.createOrGetScheduler(scheduler);

    schedulerL.resume();

    if (startOnCreate == false) {
      schedulerL.pause(startupTimeout);
    }

    return schedulerL;
  }

  public void setDispatcher(Dispatcher dispatcher)
  {
    this.dispatcher = dispatcher;
  }

  public int getNodeBindingTimeout()
  {
    return nodeBindingTimeout;
  }

  public void setNodeBindingTimeout(int nodeBindingTimeout)
  {
    this.nodeBindingTimeout = nodeBindingTimeout;
  }

  public int getJobRetryTime()
  {
    return jobRetryTime;
  }

  public String getSchedulerName()
  {
    return schedulerName;
  }

  public int getServiceRetryTime()
  {
    return serviceRetryTime;
  }

  public boolean isStartOnCreate()
  {
    return startOnCreate;
  }

  public int getStartupTimeout()
  {
    return startupTimeout;
  }

  public int getThreadPoolSize()
  {
    return threadPoolSize;
  }

  public int getJobMaxRetryCount()
  {
    return jobMaxRetryCount;
  }

  public void setJobMaxRetryCount(int jobMaxRetryCount)
  {
    this.jobMaxRetryCount = jobMaxRetryCount;
  }

}
