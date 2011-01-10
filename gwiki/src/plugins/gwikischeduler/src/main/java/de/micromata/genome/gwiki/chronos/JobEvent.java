/////////////////////////////////////////////////////////////////////////////
//
// $RCSfile: JobEvent.java,v $
//
// Project   jchronos
//
// Author    Wolfgang Jung (w.jung@micromata.de)
// Created   02.01.2007
// Copyright Micromata 02.01.2007
//
// $Id: JobEvent.java,v 1.4 2007/02/25 13:38:59 roger Exp $
// $Revision: 1.4 $
// $Date: 2007/02/25 13:38:59 $
//
/////////////////////////////////////////////////////////////////////////////
package de.micromata.genome.gwiki.chronos;

import de.micromata.genome.gwiki.chronos.spi.jdbc.JobResultDO;
import de.micromata.genome.gwiki.chronos.spi.jdbc.TriggerJobDO;

public interface JobEvent
{

  public Scheduler getScheduler();

  public TriggerJobDO getJob();

  public JobDefinition getJobDefinition();

  public JobResultDO getJobResult();

  public State getJobStatus();
}
