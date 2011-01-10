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
