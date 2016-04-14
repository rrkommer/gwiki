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

import de.micromata.genome.logging.LogAttribute;

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
