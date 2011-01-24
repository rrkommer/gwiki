////////////////////////////////////////////////////////////////////////////
// 
// Copyright (C) 2010 Micromata GmbH
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
package de.micromata.genome.gwiki.scheduler_1_0.gui;

import java.util.ArrayList;
import java.util.List;

import de.micromata.genome.gwiki.chronos.JobStore;
import de.micromata.genome.gwiki.chronos.StaticDaoManager;
import de.micromata.genome.gwiki.chronos.spi.jdbc.TriggerJobDisplayDO;
import de.micromata.genome.gwiki.page.impl.actionbean.ActionBeanBase;

/**
 * @author Roger Rene Kommer (r.kommer@micromata.de)
 * 
 */
public class GWikiSchedulerJobsActionBean extends ActionBeanBase
{
  private String filterState;

  private String filterScheduler;

  private String filterJobName;

  private long selectedJob = -1;

  private String selectedState = null;

  private List<TriggerJobDisplayDO> jobs = new ArrayList<TriggerJobDisplayDO>();

  private void initJobList()
  {
    JobStore js = StaticDaoManager.get().getJobStore();
    jobs = js.getAdminJobs(null, filterJobName, filterState, filterScheduler, 10000);
  }

  @Override
  public Object onInit()
  {
    initJobList();
    return super.onInit();
  }

  public Object onSetState()
  {
    return null;
  }

  public long getSelectedJob()
  {
    return selectedJob;
  }

  public void setSelectedJob(long selectedJob)
  {
    this.selectedJob = selectedJob;
  }

  public String getSelectedState()
  {
    return selectedState;
  }

  public void setSelectedState(String selectedState)
  {
    this.selectedState = selectedState;
  }

  public String getFilterState()
  {
    return filterState;
  }

  public void setFilterState(String filterState)
  {
    this.filterState = filterState;
  }

  public String getFilterScheduler()
  {
    return filterScheduler;
  }

  public void setFilterScheduler(String filterScheduler)
  {
    this.filterScheduler = filterScheduler;
  }

  public String getFilterJobName()
  {
    return filterJobName;
  }

  public void setFilterJobName(String filterJobName)
  {
    this.filterJobName = filterJobName;
  }

  public List<TriggerJobDisplayDO> getJobs()
  {
    return jobs;
  }

  public void setJobs(List<TriggerJobDisplayDO> jobs)
  {
    this.jobs = jobs;
  }

}
