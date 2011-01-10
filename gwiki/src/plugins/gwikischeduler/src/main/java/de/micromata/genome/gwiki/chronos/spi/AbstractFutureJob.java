/////////////////////////////////////////////////////////////////////////////
//
// Project   DHL-ParcelOnlinePostage
//
// Author    worker@micromata.de
// Created   20.02.2007
// Copyright Micromata 20.02.2007
//
/////////////////////////////////////////////////////////////////////////////
package de.micromata.genome.gwiki.chronos.spi;

import de.micromata.genome.gwiki.chronos.FutureJob;
import de.micromata.genome.gwiki.chronos.spi.jdbc.TriggerJobDO;

/**
 * Hält zusätzlich den {@link TriggerJobDO}.
 * 
 * @author H.Spiewok@micromata.de
 * 
 */
public abstract class AbstractFutureJob implements FutureJob
{

  private TriggerJobDO triggerJobDO;

  public long getWaitTime()
  {
    if (triggerJobDO != null && triggerJobDO.getFireTime() != null)
      return System.currentTimeMillis() - triggerJobDO.getFireTime().getTime();
    return 0;
  }

  /**
   * @see de.micromata.genome.gwiki.chronos.FutureJob#call(java.lang.Object)
   */
  public abstract Object call(Object argument) throws Exception;

  public TriggerJobDO getTriggerJobDO()
  {
    return triggerJobDO;
  }

  public void setTriggerJobDO(final TriggerJobDO triggerJobDO)
  {
    this.triggerJobDO = triggerJobDO;
  }

}
