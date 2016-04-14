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

// ///////////////////////////////////////////////////////////////////////////
//
// $RCSfile: State.java,v $
//
// Project jchronos
//
// Author Roger Rene Kommer, Wolfgang Jung (w.jung@micromata.de)
// Created 17.01.2007
// Copyright Micromata 17.01.2007
//
// $Id: State.java,v 1.3 2007/03/11 18:05:26 roger Exp $
// $Revision: 1.3 $
// $Date: 2007/03/11 18:05:26 $
//
// ///////////////////////////////////////////////////////////////////////////
package de.micromata.genome.gwiki.chronos;

public enum State
{
  /**
   * Job is waiting for next execution
   */
  WAIT,
  /**
   * Job is prepared to run
   */
  SCHEDULED,
  /**
   * Job is running
   */
  RUN,
  /**
   * Job is stopped
   */
  STOP,
  /**
   * Job is finished
   */
  FINISHED,
  /**
   * Job is in Retry
   */
  RETRY,
  /**
   * Job Wurde geschlossen.
   */
  CLOSED;
  public static State fromString(String s)
  {
    if (s == null)
      return null;
    for (State st : values()) {
      if (st.name().equals(s) == true)
        return st;
    }
    return null;
  }
}