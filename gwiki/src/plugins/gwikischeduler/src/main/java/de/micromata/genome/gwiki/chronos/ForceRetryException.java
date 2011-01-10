/////////////////////////////////////////////////////////////////////////////
//
// Project   DHL-ParcelOnlinePostage
//
// Author    roger@micromata.de
// Created   14.03.2007
// Copyright Micromata 14.03.2007
//
/////////////////////////////////////////////////////////////////////////////
package de.micromata.genome.gwiki.chronos;

import de.micromata.genome.gwiki.chronos.logging.LogAttribute;

/**
 * Ein Retry wird erzwungen.
 * 
 * @author roger@micromata.de
 * 
 */
public class ForceRetryException extends JobRetryException
{

  private static final long serialVersionUID = 1388936786323502747L;

  public ForceRetryException()
  {
    super();
  }


  public ForceRetryException(String message, Throwable cause)
  {
    super(message, cause);
  }

  public ForceRetryException(String message, Throwable cause, boolean silent)
  {
    super(message, cause, silent);
  }

  public ForceRetryException(boolean silent)
  {
    super("", silent);
  }

  public ForceRetryException(String message, boolean silent)
  {
    super(message, silent);
  }

  public ForceRetryException(String message, LogAttribute... attrs)
  {
    super(message, attrs);
  }

  public ForceRetryException(String message, Throwable cause, boolean captureLogContext, LogAttribute... attrs)
  {
    super(message, cause, captureLogContext, attrs);
  }

  public ForceRetryException(String message)
  {
    super(message);

  }

  public ForceRetryException(Throwable cause)
  {
    super(cause);

  }

}
