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
// Created   09.01.2008
// Copyright Micromata 09.01.2008
//
/////////////////////////////////////////////////////////////////////////////
package de.micromata.genome.gwiki.chronos.manager;

import de.micromata.genome.gwiki.chronos.JobEvent;
import de.micromata.genome.gwiki.chronos.spi.JobEventImpl;
import de.micromata.genome.gwiki.chronos.spi.jdbc.TriggerJobDO;
import de.micromata.genome.logging.GenomeAttributeType;
import de.micromata.genome.logging.LogAttribute;

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
    if (event == null) {
      return "NullJobEvent";
    }
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
