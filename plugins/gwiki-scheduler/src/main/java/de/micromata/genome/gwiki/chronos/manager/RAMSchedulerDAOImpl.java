/////////////////////////////////////////////////////////////////////////////
//
// Project   Micromata Genome Core
//
// Author    roger@micromata.de
// Created   24.01.2008
// Copyright Micromata 24.01.2008
//
/////////////////////////////////////////////////////////////////////////////
package de.micromata.genome.gwiki.chronos.manager;

import de.micromata.genome.gwiki.chronos.spi.DispatcherImpl;
import de.micromata.genome.gwiki.chronos.spi.DispatcherImpl2;
import de.micromata.genome.gwiki.chronos.spi.ram.RamJobStore;

public class RAMSchedulerDAOImpl extends SchedulerBaseDAO
{

  public DispatcherImpl createDispatcher(String virtualHostName) throws Exception
  {
    RamJobStore store = new RamJobStore();

    DispatcherImpl dispatcher = new DispatcherImpl2(virtualHostName, store);
    return dispatcher;
  }

}
