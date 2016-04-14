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
