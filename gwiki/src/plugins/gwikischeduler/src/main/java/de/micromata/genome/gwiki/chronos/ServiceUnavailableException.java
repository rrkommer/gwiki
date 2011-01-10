/////////////////////////////////////////////////////////////////////////////
//
// $RCSfile: ServiceUnavailableException.java,v $
//
// Project   jchronos
//
// Author    Wolfgang Jung (w.jung@micromata.de)
// Created   03.01.2007
// Copyright Micromata 03.01.2007
//
// $Id: ServiceUnavailableException.java,v 1.3 2007-12-30 11:59:44 roger Exp $
// $Revision: 1.3 $
// $Date: 2007-12-30 11:59:44 $
//
/////////////////////////////////////////////////////////////////////////////
package de.micromata.genome.gwiki.chronos;

import de.micromata.genome.gwiki.chronos.logging.LogAttribute;

public class ServiceUnavailableException extends JobRetryException
{
  private static final long serialVersionUID = 6010861227600774448L;

  public ServiceUnavailableException()
  {
    super();
  }

  public ServiceUnavailableException(final String message)
  {
    super(message);
  }

  public ServiceUnavailableException(final String message, boolean silent)
  {
    super(message, silent);
  }

  public ServiceUnavailableException(final String message, final Throwable cause)
  {
    super(message, cause);
  }

  public ServiceUnavailableException(final String message, final Throwable cause, boolean silent)
  {
    super(message, cause, silent);
  }

  public ServiceUnavailableException(String message, LogAttribute... attrs)
  {
    super(message, attrs);
  }

  public ServiceUnavailableException(String message, Throwable cause, boolean captureLogContext, LogAttribute... attrs)
  {
    super(message, cause, captureLogContext, attrs);
  }

  public ServiceUnavailableException(Throwable cause)
  {
    super(cause);
  }

}
