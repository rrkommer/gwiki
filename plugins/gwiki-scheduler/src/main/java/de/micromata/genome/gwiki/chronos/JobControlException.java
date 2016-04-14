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

package de.micromata.genome.gwiki.chronos;

import de.micromata.genome.logging.LogAttribute;
import de.micromata.genome.logging.LogAttributeRuntimeException;

/**
 * Base class for Exception thrown by jobs
 * 
 * @author roger
 * 
 */
public class JobControlException extends LogAttributeRuntimeException
{

  private static final long serialVersionUID = -5339065645182169281L;

  /**
   * Wenn silent == true, wird vom jchronos die exception nicht mehr geloggt
   */
  private boolean silent = false;

  public JobControlException()
  {
    super();
  }

  public JobControlException(String message, boolean captureLogContext)
  {
    super(message, captureLogContext);
  }

  public JobControlException(String message, LogAttribute... attrs)
  {
    super(message, attrs);
  }

  public JobControlException(String message, Throwable cause, boolean captureLogContext, LogAttribute... attrs)
  {
    super(message, cause, captureLogContext, attrs);
  }

  public JobControlException(String message, Throwable cause)
  {
    super(message, cause);
  }

  public JobControlException(String message)
  {
    super(message);
  }

  public JobControlException(Throwable cause)
  {
    super(cause);
  }

  public boolean isSilent()
  {
    return silent;
  }

  public void setSilent(boolean silent)
  {
    this.silent = silent;
  }

}
