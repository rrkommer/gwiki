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

package de.micromata.genome.gwiki.scheduler_1_0.gui;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import de.micromata.genome.chronos.ChronosServiceManager;
import de.micromata.genome.chronos.manager.SchedulerDAO;
import de.micromata.genome.chronos.spi.jdbc.TriggerJobDisplayDO;
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

  private String filterJobDefinition;

  private String newState;

  private String oldState;

  private long selectedJob = -1;

  private String selectedState = null;

  private List<TriggerJobDisplayDO> jobs = new ArrayList<TriggerJobDisplayDO>();

  private void initJobList()
  {
    SchedulerDAO scheddao = ChronosServiceManager.get().getSchedulerDAO();

    jobs = scheddao.getAdminJobs(null, filterJobName, filterState, filterScheduler, 10000);
    TriggerJobDisplayDO tdj = null;
  }

  @Override
  public Object onInit()
  {
    initJobList();
    return super.onInit();
  }

  public Object onSetJobState()
  {
    if (selectedJob == -1) {
      wikiContext.addSimpleValidationError("No Job selected");
      return null;
    }
    if (StringUtils.isBlank(newState) == true) {
      return null;
    }
    SchedulerDAO scheddao = ChronosServiceManager.get().getSchedulerDAO();
    if (scheddao.setJobState(selectedJob, newState, oldState) == 0) {
      wikiContext.addSimpleValidationError("Could not update Job");
    }

    initJobList();
    return super.onInit();
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

  public String getFilterJobDefinition()
  {
    return filterJobDefinition;
  }

  public void setFilterJobDefinition(String filterJobDefinition)
  {
    this.filterJobDefinition = filterJobDefinition;
  }

  public String getNewState()
  {
    return newState;
  }

  public void setNewState(String newState)
  {
    this.newState = newState;
  }

  public String getOldState()
  {
    return oldState;
  }

  public void setOldState(String oldState)
  {
    this.oldState = oldState;
  }

}
