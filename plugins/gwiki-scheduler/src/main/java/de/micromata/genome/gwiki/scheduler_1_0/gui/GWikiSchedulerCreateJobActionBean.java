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

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.apache.commons.lang3.StringUtils;

import de.micromata.genome.chronos.ChronosServiceManager;
import de.micromata.genome.chronos.Scheduler;
import de.micromata.genome.chronos.manager.SchedulerDAO;
import de.micromata.genome.chronos.spi.jdbc.SchedulerDO;
import de.micromata.genome.chronos.spi.jdbc.TriggerJobDO;
import de.micromata.genome.gwiki.model.logging.GWikiLog;
import de.micromata.genome.gwiki.page.impl.actionbean.ActionBeanBase;
import de.micromata.genome.gwiki.scheduler_1_0.api.GWikiScheduler;
import de.micromata.genome.util.text.PipeValueList;

/**
 * @author Roger Rene Kommer (r.kommer@micromata.de)
 * 
 */
public class GWikiSchedulerCreateJobActionBean extends ActionBeanBase
{
  private Map<String, String> schedulers = new TreeMap<String, String>();

  private String scheduler;

  private String jobDefinition;

  private String jobArguments;

  private String triggerDefinition;

  private long cloneJobId = -1;

  private void init()
  {
    List<SchedulerDO> scheds = ChronosServiceManager.get().getSchedulerDAO().getSchedulers();
    for (SchedulerDO sched : scheds) {
      schedulers.put(sched.getName(), sched.getName());
    }
  }

  private void cloneJob(long jobId)
  {
    SchedulerDAO scheddao = ChronosServiceManager.get().getSchedulerDAO();
    TriggerJobDO tj = scheddao.getAdminJobByPk(jobId);
    Scheduler sched = scheddao.getSchedulerByPk(tj.getScheduler());
    if (tj != null) {
      scheduler = sched.getName();
      jobDefinition = tj.getJobDefinitionString();
      jobArguments = tj.getJobArgumentString();
      triggerDefinition = tj.getTriggerDefinition();
    }
  }

  @Override
  public Object onInit()
  {
    init();
    if (cloneJobId != -1) {
      cloneJob(cloneJobId);
    }
    return null;
  }

  public Object onCreate()
  {
    init();
    if (StringUtils.isBlank(scheduler) == true) {
      wikiContext.addSimpleValidationError("No scheduler selected");
    } else {

    }
    if (StringUtils.isBlank(jobDefinition) == true) {
      wikiContext.addSimpleValidationError("No valid job definition");
    } else {

    }
    if (StringUtils.isBlank(triggerDefinition) == true) {
      wikiContext.addSimpleValidationError("No valid trigger definition");
    } else {

    }
    Map<String, String> args = new HashMap<String, String>();
    if (StringUtils.isNotBlank(jobArguments) == true) {
      args = PipeValueList.decode(jobArguments);
    }
    if (wikiContext.hasValidationErrors() == true) {
      return null;
    }
    try {
      GWikiScheduler.submitJob(scheduler, jobDefinition, triggerDefinition, args);
      wikiContext.addSimpleValidationError("Job submitted");
    } catch (Exception ex) {
      wikiContext.addSimpleValidationError("Failed to submit job: " + ex.getMessage());
      GWikiLog.warn("Failed to submit job: " + ex.getMessage(), ex);
    }
    return null;
  }

  public String getScheduler()
  {
    return scheduler;
  }

  public void setScheduler(String scheduler)
  {
    this.scheduler = scheduler;
  }

  public String getJobDefinition()
  {
    return jobDefinition;
  }

  public void setJobDefinition(String jobDefinition)
  {
    this.jobDefinition = jobDefinition;
  }

  public String getJobArguments()
  {
    return jobArguments;
  }

  public void setJobArguments(String jobArguments)
  {
    this.jobArguments = jobArguments;
  }

  public String getTriggerDefinition()
  {
    return triggerDefinition;
  }

  public void setTriggerDefinition(String triggerDefinition)
  {
    this.triggerDefinition = triggerDefinition;
  }

  public Map<String, String> getSchedulers()
  {
    return schedulers;
  }

  public void setSchedulers(Map<String, String> schedulers)
  {
    this.schedulers = schedulers;
  }

  public long getCloneJobId()
  {
    return cloneJobId;
  }

  public void setCloneJobId(long cloneJobId)
  {
    this.cloneJobId = cloneJobId;
  }

}
