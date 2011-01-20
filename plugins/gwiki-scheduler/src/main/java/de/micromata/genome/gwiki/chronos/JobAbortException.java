/////////////////////////////////////////////////////////////////////////////
//
// Project   DHL-ParcelOnlinePostage
//
// Author    roger@micromata.de
// Created   09.03.2007
// Copyright Micromata 09.03.2007
//
/////////////////////////////////////////////////////////////////////////////
package de.micromata.genome.gwiki.chronos;

/**
 * Exception to abort the current running job.
 * 
 * @author roger
 * 
 */
public class JobAbortException extends JobControlException
{

  private static final long serialVersionUID = 1268094324172864470L;

  public JobAbortException()
  {
    super();
  }

  public JobAbortException(Throwable cause)
  {
    super(cause);
  }

  public JobAbortException(final String message, final Throwable cause)
  {
    super(message, cause);
  }

  public JobAbortException(final String message)
  {
    super(message);
  }
}
