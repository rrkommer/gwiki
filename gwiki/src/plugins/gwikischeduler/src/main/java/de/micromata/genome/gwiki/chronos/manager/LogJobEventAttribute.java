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

import de.micromata.genome.gwiki.chronos.JobEvent;
import de.micromata.genome.gwiki.chronos.logging.GenomeAttributeType;
import de.micromata.genome.gwiki.chronos.logging.LogAttribute;
import de.micromata.genome.gwiki.chronos.spi.JobEventImpl;
import de.micromata.genome.gwiki.chronos.spi.jdbc.TriggerJobDO;

public class LogJobEventAttribute extends LogAttribute
{

  private static final long serialVersionUID = -908728178801341463L;

  public LogJobEventAttribute(JobEvent event)
  {
    super(GenomeAttributeType.JobEvent, getEventString(event));
  }

  public LogJobEventAttribute(TriggerJobDO job)
  {
    super(GenomeAttributeType.JobEvent, getEventString(buildEvent(job)));
  }

  private static JobEvent buildEvent(TriggerJobDO job)
  {
    JobEvent event = new JobEventImpl(job, job.getJobDefinition(), null, job.getState(), null);
    return event;
  }

  private static String getEventString(final JobEvent event)
  {
    if (event == null)
      return "NullJobEvent";
    final StringBuilder sb = new StringBuilder();
    if (event.getScheduler() != null) {
      sb.append(" Scheduler=").append(event.getScheduler()).append("\n");
    }
    // if (event.getRunner() != null) {
    // sb.append(" Runner=").append(event.getRunner()).append("\n");
    // }
    if (event.getJob() != null) {
      sb.append(" Job=").append(event.getJob()).append("\n");
    }
    if (event.getJobDefinition() != null) {
      sb.append(" Definition=").append(event.getJobDefinition()).append("\n");
    }
    if (event.getJobResult() != null) {
      sb.append(" Result=").append(event.getJobResult()).append("\n");
    }
    if (event.getJobStatus() != null) {
      sb.append(" Status=").append(event.getJobStatus()).append("\n");
    }
    return sb.toString();
  }
}
