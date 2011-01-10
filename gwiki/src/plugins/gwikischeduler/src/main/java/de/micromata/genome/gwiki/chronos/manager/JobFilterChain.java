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
