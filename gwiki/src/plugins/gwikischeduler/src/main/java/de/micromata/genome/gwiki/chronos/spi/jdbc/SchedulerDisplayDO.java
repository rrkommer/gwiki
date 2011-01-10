/////////////////////////////////////////////////////////////////////////////
//
// Project   DHL-ParcelOnlinePostage
//
// Author    roger@micromata.de
// Created   19.02.2007
// Copyright Micromata 19.02.2007
//
/////////////////////////////////////////////////////////////////////////////
package de.micromata.genome.gwiki.chronos.spi.jdbc;

public class SchedulerDisplayDO extends SchedulerDO
{

  private static final long serialVersionUID = 5924549593570479121L;

  private int poolSize;

  private int poolActive;

  private long poolCompleted;

  private int poolWaiting;

  private long poolTaskCount;

  public SchedulerDisplayDO()
  {//TODO rrk dieser Konstruktro hat gefehlt. Ist das korrekt ?
    super();
  }

  public SchedulerDisplayDO(SchedulerDO sched)
  {
    super(sched);
  }

  public int getPoolActive()
  {
    return poolActive;
  }

  public void setPoolActive(int poolActive)
  {
    this.poolActive = poolActive;
  }

  public long getPoolCompleted()
  {
    return poolCompleted;
  }

  public void setPoolCompleted(long poolCompleted)
  {
    this.poolCompleted = poolCompleted;
  }

  public int getPoolSize()
  {
    return poolSize;
  }

  public void setPoolSize(int poolSize)
  {
    this.poolSize = poolSize;
  }

  public long getPoolTaskCount()
  {
    return poolTaskCount;
  }

  public void setPoolTaskCount(long poolTaskCount)
  {
    this.poolTaskCount = poolTaskCount;
  }

  public int getPoolWaiting()
  {
    return poolWaiting;
  }

  public void setPoolWaiting(int poolWaiting)
  {
    this.poolWaiting = poolWaiting;
  }
}
