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
// Project   Micromata Genome Core
//
// Author    roger@micromata.de
// Created   20.01.2008
// Copyright Micromata 20.01.2008
//
/////////////////////////////////////////////////////////////////////////////
package de.micromata.genome.gwiki.chronos.spi;

import java.util.Date;

import de.micromata.genome.gwiki.chronos.JobDefinition;
import de.micromata.genome.gwiki.chronos.JobStore;
import de.micromata.genome.gwiki.chronos.Trigger;

/**
 * NOT PRODUCTIVE currently
 * 
 * TODO Ein Job mit +1 sofort starten, d.h. also schon als scheduled in die DB schreiben
 * 
 * @author roger@micromata.de
 * 
 */
public class LazyDispatcher extends DispatcherImpl
{
  private long nextFireTime = -1;

  public LazyDispatcher(String prefix, JobStore jobStore)
  {
    super(prefix, jobStore);
  }

  @Override
  public long submit(String schedulerName, JobDefinition jobDefinition, Object arg, Trigger trigger, String hostName)
  {
    long jobPk = super.submit(schedulerName, jobDefinition, arg, trigger, hostName);
    Date now = new Date();
    Date nft = trigger.getNextFireTime(now);
    if (nextFireTime == -1 || nft.getTime() < nextFireTime) {
      nextFireTime = nft.getTime();
    }
    return jobPk;
  }

  @Override
  public void run()
  {
    // TODO Auto-generated method stub
    super.run();
  }

  protected boolean checkScheduler(boolean foreignJobs)
  {
    // TODO genome select ueber alle Scheduler. Problem, wenn Scheduler sleeped
    return super.checkScheduler(foreignJobs);
  }

}
