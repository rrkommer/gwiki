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

package de.micromata.genome.gwiki.chronos.spi;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.apache.commons.lang.Validate;

import de.micromata.genome.gwiki.chronos.spi.jdbc.TriggerJobDO;

/**
 * Jobs which start in soon future. Used by the Dispatcher implementation
 * 
 * Problem: Job, which has a very high nodebinding timeout are kept in this cache for this long time.
 * 
 * @author roger
 * 
 */
public class ReservedJobs
{
  private Set<TriggerJobDO> jobsByStarted = new TreeSet<TriggerJobDO>(new Comparator<TriggerJobDO>() {

    public int compare(TriggerJobDO o1, TriggerJobDO o2)
    {
      long t1 = o1.getNextFireTime().getTime();
      long t2 = o2.getNextFireTime().getTime();
      if (t1 > t2)
        return 1;
      if (t1 < t2)
        return -1;
      return 0;
    }
  });

  private Map<Long, TriggerJobDO> jobsByPk = new HashMap<Long, TriggerJobDO>();

  // private Date minModifiedAt;

  public synchronized void addReservedJob(TriggerJobDO job)
  {
    Validate.notNull(job.getPk(), "job.getPk() is not set in addReservedJob");
    if (jobsByPk.containsKey(job.getPk()) == true)
      return;
    Validate.notNull(job.getSchedulerName(), "job.getSchedulerName() is not set in addReservedJob");
    jobsByPk.put(job.getPk(), job);
    jobsByStarted.add(job);
    // // modifiedAt is null, if job is directly added from local host
    // if (job.getModifiedAt() != null)
    // minModifiedAt = DateUtils.max(minModifiedAt, job.getModifiedAt());
  }

  // private void determineMinModifiedAt()
  // {
  // Date minModifiedAt = null;
  // for (TriggerJobDO job : jobsByStarted) {
  // if (job.getModifiedAt() != null)
  // minModifiedAt = DateUtils.max(minModifiedAt, job.getModifiedAt());
  // }
  // }

  public Iterator<TriggerJobDO> getJobsByNextFireTimeIterator()
  {
    return jobsByStarted.iterator();
  }

  public void removeJob(Iterator<TriggerJobDO> it, TriggerJobDO job)
  {
    jobsByPk.remove(job.getPk());
    it.remove();
    // if (job.getModifiedAt() != null && job.getModifiedAt().equals(minModifiedAt) == true) {
    // determineMinModifiedAt();
    // }
  }

  public synchronized void setReservedJobs(List<TriggerJobDO> jobs)
  {
    jobsByPk.clear();
    jobsByStarted.clear();
    jobsByStarted.addAll(jobs);
    for (TriggerJobDO job : jobs) {
      Validate.notNull(job.getSchedulerName(), "job.getSchedulerName() is not set in addReservedJob");
      Validate.notNull(job.getPk(), "job.getPk() is not set in addReservedJob");
      jobsByPk.put(job.getPk(), job);
    }
  }

  // public synchronized void addReservedJob(List<TriggerJobDO> jobs)
  // {
  // if (jobs == null)
  // return;
  // for (TriggerJobDO job : jobs) {
  // addReservedJob(job);
  // }
  // }

  public Set<TriggerJobDO> getJobsByStarted()
  {
    return jobsByStarted;
  }

  public void setJobsByStarted(Set<TriggerJobDO> jobsByStarted)
  {
    this.jobsByStarted = jobsByStarted;
  }

  // public Date getMinModifiedAt()
  // {
  // return minModifiedAt;
  // }
  //
  // public void setMinModifiedAt(Date minModifiedAt)
  // {
  // this.minModifiedAt = minModifiedAt;
  // }
}
