/////////////////////////////////////////////////////////////////////////////
//
// $RCSfile: JobRetryException.java,v $
//
// Project   jchronos
//
// Author    Roger Kommer, Wolfgang Jung (w.jung@micromata.de)
// Created   03.01.2007
// Copyright Micromata 03.01.2007
//
//
/////////////////////////////////////////////////////////////////////////////
package de.micromata.genome.gwiki.chronos;

import de.micromata.genome.gwiki.chronos.logging.LogAttribute;

public class JobRetryException extends JobControlException
{

  /**
   * serialVersionUID
   */
  private static final long serialVersionUID = -2814203354464893048L;

  public JobRetryException(final String message)
  {
    super(message);
  }

  public JobRetryException(final String message, final Throwable cause)
  {
    super(message, cause);
  }

  public JobRetryException()
  {
    super();
  }

  public JobRetryException(String message, boolean captureLogContext)
  {
    super(message, captureLogContext);

  }

  public JobRetryException(String message, LogAttribute... attrs)
  {
    super(message, attrs);

  }

  public JobRetryException(String message, Throwable cause, boolean captureLogContext, LogAttribute... attrs)
  {
    super(message, cause, captureLogContext, attrs);

  }

  public JobRetryException(Throwable cause)
  {
    super(cause);

  }

}
