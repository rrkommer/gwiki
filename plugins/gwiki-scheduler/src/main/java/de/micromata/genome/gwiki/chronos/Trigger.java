/////////////////////////////////////////////////////////////////////////////
//
// $RCSfile: Trigger.java,v $
//
// Project   chronos
//
// Author    Wolfgang Jung (w.jung@micromata.de)
// Created   19.12.2006
// Copyright Micromata 19.12.2006
//
// $Id: Trigger.java,v 1.5 2007/03/09 07:25:10 roger Exp $
// $Revision: 1.5 $
// $Date: 2007/03/09 07:25:10 $
//
/////////////////////////////////////////////////////////////////////////////
package de.micromata.genome.gwiki.chronos;

import java.util.Date;

public interface Trigger
{
  public Date getNextFireTime(Date now);

  public void setNextFireTime(Date nextFireTime);

  /**
   * Vorbereitung auf den nächsten Auslösezeitpunkt.
   * 
   * @param scheduler
   * @param cause
   * @param logging
   * @return nextFireTime null wenn nicht mehr ausgefuehrt werden soll
   */

  public Date updateAfterRun(Scheduler scheduler, JobCompletion cause);

  public String getTriggerDefinition();

}
