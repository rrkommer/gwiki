/////////////////////////////////////////////////////////////////////////////
//
// Project   Micromata Genome Core
//
// Author    roger@micromata.de
// Created   24.01.2008
// Copyright Micromata 24.01.2008
//
/////////////////////////////////////////////////////////////////////////////
package de.micromata.genome.gwiki.scheduler_1_0.chronos.spi;

import de.micromata.genome.gwiki.chronos.manager.SchedulerBaseDAO;
import de.micromata.genome.gwiki.chronos.spi.DispatcherImpl;

public class GWikiSchedulerDAOImpl extends SchedulerBaseDAO
{

  public DispatcherImpl createDispatcher(String virtualHostName) throws Exception
  {
    GWikiSchedElementJobStore store = new GWikiSchedElementJobStore();

    DispatcherImpl dispatcher = new GWikiSchedDispatcherImpl(virtualHostName, store);
    return dispatcher;
  }

}
