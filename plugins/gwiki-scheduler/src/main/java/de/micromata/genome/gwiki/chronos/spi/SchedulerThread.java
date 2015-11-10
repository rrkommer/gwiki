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
// Project   Micromata Genome Core
//
// Author    roger@micromata.de
// Created   05.02.2008
// Copyright Micromata 05.02.2008
//
/////////////////////////////////////////////////////////////////////////////
package de.micromata.genome.gwiki.chronos.spi;

public class SchedulerThread extends Thread
{
  private long jobId;

  public SchedulerThread()
  {

  }

  public SchedulerThread(Runnable target, String name)
  {
    super(target, name);
  }

  public SchedulerThread(Runnable target)
  {
    super(target);
  }

  public SchedulerThread(String name)
  {
    super(name);
  }

  public SchedulerThread(ThreadGroup group, Runnable target, String name, long stackSize)
  {
    super(group, target, name, stackSize);
  }

  public SchedulerThread(ThreadGroup group, Runnable target, String name)
  {
    super(group, target, name);
  }

  public SchedulerThread(ThreadGroup group, Runnable target)
  {
    super(group, target);
  }

  public SchedulerThread(ThreadGroup group, String name)
  {
    super(group, name);
  }

  @Override
  public String toString()
  {
    return super.toString() + "; jobId: " + jobId;
  }

  public long getJobId()
  {
    return jobId;
  }

  public void setJobId(long jobId)
  {
    this.jobId = jobId;
  }

}
