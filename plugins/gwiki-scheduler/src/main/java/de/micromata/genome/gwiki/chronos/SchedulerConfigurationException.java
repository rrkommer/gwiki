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
