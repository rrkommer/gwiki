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

import de.micromata.genome.logging.LogAttribute;

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
