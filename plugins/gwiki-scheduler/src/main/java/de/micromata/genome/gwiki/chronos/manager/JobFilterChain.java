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
// Created   29.03.2008
// Copyright Micromata 29.03.2008
//
/////////////////////////////////////////////////////////////////////////////
package de.micromata.genome.gwiki.chronos.manager;

import java.util.ArrayList;
import java.util.List;

import de.micromata.genome.gwiki.chronos.spi.JobRunner;

public class JobFilterChain
{

  private JobRunner jobRunner;

  private SchedulerDAO schedulerDAO;

  private List<JobRunnerFilter> filters = new ArrayList<JobRunnerFilter>();

  private int index;

  public Object doFilter() throws Exception
  {
    if (index >= filters.size()) {
      return schedulerDAO.invokeJob(jobRunner);
    }
    ++index;
    return filters.get(index - 1).filter(this);
  }

  public JobRunner getJobRunner()
  {
    return jobRunner;
  }

  public void setJobRunner(JobRunner jobRunner)
  {
    this.jobRunner = jobRunner;
  }

  public SchedulerDAO getSchedulerDAO()
  {
    return schedulerDAO;
  }

  public void setSchedulerDAO(SchedulerDAO schedulerDAO)
  {
    this.schedulerDAO = schedulerDAO;
  }

  public List<JobRunnerFilter> getFilters()
  {
    return filters;
  }

  public void setFilters(List<JobRunnerFilter> filters)
  {
    this.filters = filters;
  }

  public int getIndex()
  {
    return index;
  }

  public void setIndex(int index)
  {
    this.index = index;
  }
}
