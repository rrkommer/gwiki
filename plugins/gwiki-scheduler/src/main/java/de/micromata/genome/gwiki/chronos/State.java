// ///////////////////////////////////////////////////////////////////////////
//
// $RCSfile: State.java,v $
//
// Project jchronos
//
// Author Wolfgang Jung (w.jung@micromata.de)
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