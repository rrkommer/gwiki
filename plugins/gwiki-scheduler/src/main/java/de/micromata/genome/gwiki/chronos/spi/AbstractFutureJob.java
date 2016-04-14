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
// Project Genome Core
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
