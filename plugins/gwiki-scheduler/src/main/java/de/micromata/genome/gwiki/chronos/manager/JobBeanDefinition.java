/////////////////////////////////////////////////////////////////////////////
//
// Project   Micromata Genome Core
//
// Author    roger@micromata.de
// Created   09.01.2008
// Copyright Micromata 09.01.2008
//
/////////////////////////////////////////////////////////////////////////////
package de.micromata.genome.gwiki.chronos.manager;

import de.micromata.genome.gwiki.chronos.FutureJob;
import de.micromata.genome.gwiki.chronos.JobDefinition;
import de.micromata.genome.gwiki.chronos.spi.jdbc.Stringifiable;

public class JobBeanDefinition implements JobDefinition, Stringifiable
{
  private String beanName;

  private String jobName;

  private JobDefinition jobDefinition;

  private String schedulerName;

  private String triggerDefinition;

  public FutureJob getInstance()
  {
    return jobDefinition.getInstance();
  }

  public String asString()
  {
    return JobBeanDefinition.class.getName() + ":" + beanName;
  }

  public JobDefinition getJobDefinition()
  {
    return jobDefinition;
  }

  public void setJobDefinition(JobDefinition jobDefinition)
  {
    this.jobDefinition = jobDefinition;
  }

  public String getBeanName()
  {
    return beanName;
  }

  public void setBeanName(String beanName)
  {
    this.beanName = beanName;
  }

  public String getSchedulerName()
  {
    return schedulerName;
  }

  public void setSchedulerName(String schedulerName)
  {
    this.schedulerName = schedulerName;
  }

  public String getTriggerDefinition()
  {
    return triggerDefinition;
  }

  public void setTriggerDefinition(String triggerDefinition)
  {
    this.triggerDefinition = triggerDefinition;
  }

  public String getJobName()
  {
    return jobName;
  }

  public void setJobName(String jobName)
  {
    this.jobName = jobName;
  }

}
