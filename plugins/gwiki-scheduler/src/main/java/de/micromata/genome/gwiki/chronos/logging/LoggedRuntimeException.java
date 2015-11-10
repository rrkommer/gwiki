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
// Created   03.07.2006
// Copyright Micromata 03.07.2006
//
/////////////////////////////////////////////////////////////////////////////
package de.micromata.genome.gwiki.chronos.logging;

/**
 * Exception, die strukturiert geloggt wird
 * 
 * @author roger
 */
public class LoggedRuntimeException extends LogAttributeRuntimeException
{
  private static final long serialVersionUID = -8526594930837970168L;

  public LoggedRuntimeException(LogLevel loglevel, LogCategory category, String message, LogAttribute... attributes)
  {
    super(message, null, true, attributes);
    writeLogMsg(loglevel, category, message, null, attributes);
  }

  public LoggedRuntimeException(Throwable cause, LogLevel loglevel, LogCategory category, String message, LogAttribute... attributes)
  {
    super(message, cause, true, attributes);
    writeLogMsg(loglevel, category, message, cause, attributes);
  }

  private void writeLogMsg(LogLevel log, LogCategory category, String message, Throwable cause, LogAttribute[] attributes)
  {
    // if (cause != null) {
    // attributes = (LogAttribute[]) ArrayUtils.add(attributes, new LogExceptionAttribute(cause));
    // }
    GLog.doLog(log, category, message, attributes);
  }
}
