////////////////////////////////////////////////////////////////////////////
// 
// Copyright (C) 2010-2013 Micromata GmbH / Roger Rene Kommer
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
// http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.
// 
////////////////////////////////////////////////////////////////////////////

/////////////////////////////////////////////////////////////////////////////
//
// Project   Micromata Genome Core
//
// Author    roger@micromata.de
// Created   29.03.2008
// Copyright Micromata 29.03.2008
//
/////////////////////////////////////////////////////////////////////////////
package de.micromata.genome.gwiki.chronos;

import de.micromata.genome.gwiki.chronos.logging.LogAttribute;

/**
 * If the trigger is a chron expression, abort this job run with an error, but restart the job for the next regular time.
 * 
 * @author roger@micromata.de
 * 
 */
public class RetryNextRunException extends JobRetryException
{

  private static final long serialVersionUID = 6366441836855430698L;

  public RetryNextRunException()
  {
  }

  public RetryNextRunException(String message, boolean silent)
  {
    super(message, silent);
  }

  public RetryNextRunException(String message, Throwable cause, boolean silent)
  {
    super(message, cause, silent);
  }

  public RetryNextRunException(String message, Throwable cause)
  {
    super(message, cause);
  }

  public RetryNextRunException(String message)
  {
    super(message);
  }

  public RetryNextRunException(String message, LogAttribute... attrs)
  {
    super(message, attrs);
  }

  public RetryNextRunException(String message, Throwable cause, boolean captureLogContext, LogAttribute... attrs)
  {
    super(message, cause, captureLogContext, attrs);
  }

  public RetryNextRunException(Throwable cause)
  {
    super(cause);
  }

}
