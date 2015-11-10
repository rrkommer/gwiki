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

package de.micromata.genome.gwiki.chronos.spi;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import de.micromata.genome.gwiki.chronos.JobDefinition;
import de.micromata.genome.gwiki.chronos.JobStore;
import de.micromata.genome.gwiki.chronos.State;
import de.micromata.genome.gwiki.chronos.Trigger;
import de.micromata.genome.gwiki.chronos.spi.jdbc.SchedulerDO;
import de.micromata.genome.gwiki.chronos.spi.jdbc.TriggerJobDO;

public class SchedulerImpl2 extends SchedulerImpl
{
  public static int minNodeBindTime = 60000; // 1 Minute

  public List<TriggerJobDO> scheduledJobs = new ArrayList<TriggerJobDO>();

  public SchedulerImpl2()
  {

  }

  public SchedulerImpl2(SchedulerDO schedulerDO, DispatcherInternal dispatcher)
  {
    super(schedulerDO, dispatcher);
  }

  protected synchronized void addScheduledJob(TriggerJobDO job)
  {
    scheduledJobs.add(job);
    Collections.sort(scheduledJobs, new Comparator<TriggerJobDO>() {

      public int compare(TriggerJobDO o1, TriggerJobDO o2)
      {
        long t1 = o1.getNextFireTime().getTime();
        long t2 = o2.getNextFireTime().getTime();
        if (t1 < t2)
          return -1;
        if (t1 > t2)
          return 1;
        return 0;
      }
    });
  }

  public boolean startJob(TriggerJobDO job)
  {
    if (hasFreeJobSlots() == false || getThreadPoolSize() == 0)
      return false;
    JobStore jobStore = getDispatcher().getJobStore();
    int toggled = jobStore.setJobState(job.getPk(), State.RUN.name(), job.getState().name());
    if (toggled == 0)
      return false;
    adjustPoolSize();
    executeJobNow(job, jobStore);
    return true;
  }

  public boolean runSheduledJobs()
  {
    long now = System.currentTimeMillis();
    JobStore jobStore = getDispatcher().getJobStore();
    boolean runAJob = false;
    for (int i = 0; i < scheduledJobs.size(); ++i) {
      TriggerJobDO job = scheduledJobs.get(i);
      long jr = job.getNextFireTime().getTime();
      if (jr > now)
        break;
      if (hasFreeJobSlots() == false || getThreadPoolSize() == 0)
        break;
      int toggled = jobStore.setJobState(job.getPk(), State.RUN.name(), job.getState().name());
      if (toggled == 0) {
        scheduledJobs.remove(i);
        --i;
        continue;
      }
      adjustPoolSize();
      executeJobNow(job, jobStore);
      runAJob = true;
      scheduledJobs.remove(i);
      --i;
    }
    return runAJob;
  }

  public TriggerJobDO submit(final JobDefinition jobDefinition, final Object info, final Trigger trigger)
  {
    JobStore jobStore = getDispatcher().getJobStore();
    if (hasFreeJobSlots() == true && getThreadPoolSize() != 0) {
      Date now = new Date();
      Date fireTime = trigger.getNextFireTime(now);
      long dif = fireTime.getTime() - now.getTime();
      if (fireTime.getTime() - now.getTime() < minNodeBindTime) {
        State state = State.WAIT;
        if (dif < 3)
          state = State.RUN;
        TriggerJobDO job = jobStore.submit(this, jobDefinition, info, trigger, getVirtualHostName(), state);
        if (state == State.RUN) {
          nextRuntime = System.currentTimeMillis();
          adjustPoolSize();
          executeJobNow(job, jobStore);
        } else {
          addScheduledJob(job);
        }
        return job;
      }
    }
    return getDispatcher().getJobStore().submit(this, jobDefinition, info, trigger, getVirtualHostName(), State.WAIT);

  }

  public TriggerJobDO submit(final JobDefinition jobDefinition, final Object info, final Trigger trigger, String hostName)
  {
    return getDispatcher().getJobStore().submit(this, jobDefinition, info, trigger, hostName, State.WAIT);
  }
}
