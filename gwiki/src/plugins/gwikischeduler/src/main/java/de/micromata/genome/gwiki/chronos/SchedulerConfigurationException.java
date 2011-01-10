/////////////////////////////////////////////////////////////////////////////
//
// $RCSfile: SchedulerConfigurationException.java,v $
//
// Project   chronos
//
// Author    Wolfgang Jung (w.jung@micromata.de)
// Created   26.12.2006
// Copyright Micromata 26.12.2006
//
// $Id: SchedulerConfigurationException.java,v 1.1 2007/02/09 09:57:15 roger Exp $
// $Revision: 1.1 $
// $Date: 2007/02/09 09:57:15 $
//
/////////////////////////////////////////////////////////////////////////////
package de.micromata.genome.gwiki.chronos;

public class SchedulerConfigurationException extends SchedulerException
{

  /**
   * 
   */
  private static final long serialVersionUID = 9029534181438687101L;

  /**
   * @param message
   * @param cause
   */
  public SchedulerConfigurationException(final String message, final Throwable cause)
  {
    super(message, cause);
  }

  /**
   * @param message
   */
  public SchedulerConfigurationException(final String message)
  {
    super(message);
  }

  /**
   * @param cause
   */
  public SchedulerConfigurationException(final Throwable cause)
  {
    super(cause);
  }

}
