/////////////////////////////////////////////////////////////////////////////
//
// $RCSfile: FutureJob.java,v $
//
// Project   chronos
//
// Author    Wolfgang Jung (w.jung@micromata.de)
// Created   19.12.2006
// Copyright Micromata 19.12.2006
//
// $Id: FutureJob.java,v 1.3 2007/02/20 14:06:21 hx Exp $
// $Revision: 1.3 $
// $Date: 2007/02/20 14:06:21 $
//
/////////////////////////////////////////////////////////////////////////////
package de.micromata.genome.gwiki.chronos;

import de.micromata.genome.gwiki.chronos.spi.jdbc.TriggerJobDO;

/**
 * Interface des eigentliches Runtime-Jobs.
 * 
 */
public interface FutureJob
{
  public Object call(Object argument) throws Exception;

  public void setTriggerJobDO(TriggerJobDO triggerJobDO);

}
