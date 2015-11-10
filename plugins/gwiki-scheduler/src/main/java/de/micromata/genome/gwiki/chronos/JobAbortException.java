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
// Project Genome Core
//
// Author    roger@micromata.de
// Created   09.03.2007
// Copyright Micromata 09.03.2007
//
/////////////////////////////////////////////////////////////////////////////
package de.micromata.genome.gwiki.chronos;

/**
 * Exception to abort the current running job.
 * 
 * @author roger
 * 
 */
public class JobAbortException extends JobControlException
{

  private static final long serialVersionUID = 1268094324172864470L;

  public JobAbortException()
  {
    super();
  }

  public JobAbortException(Throwable cause)
  {
    super(cause);
  }

  public JobAbortException(final String message, final Throwable cause)
  {
    super(message, cause);
  }

  public JobAbortException(final String message)
  {
    super(message);
  }
}
