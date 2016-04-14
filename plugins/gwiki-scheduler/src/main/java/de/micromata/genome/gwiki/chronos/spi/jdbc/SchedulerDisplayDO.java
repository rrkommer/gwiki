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

/////////////////////////////////////////////////////////////////////////////
//
// Project Genome Core
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
  {// TODO rrk dieser Konstruktro hat gefehlt. Ist das korrekt ?
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
