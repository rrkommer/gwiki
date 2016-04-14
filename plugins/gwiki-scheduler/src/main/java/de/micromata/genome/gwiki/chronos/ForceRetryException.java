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
// Project Genome Core
//
// Author    roger@micromata.de
// Created   14.03.2007
// Copyright Micromata 14.03.2007
//
/////////////////////////////////////////////////////////////////////////////
package de.micromata.genome.gwiki.chronos;

import de.micromata.genome.logging.LogAttribute;

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
