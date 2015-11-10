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
package de.micromata.genome.gwiki.chronos.logging;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * Runtime Exception which can transport LogAttributes to catcher of this exception
 * 
 * @author roger@micromata.de
 * 
 */
public class LogAttributeRuntimeException extends RuntimeException // implements WithLogAttributes
{

  private static final long serialVersionUID = 3169674194091876222L;

  private Map<String, LogAttribute> logAttributeMap = new HashMap<String, LogAttribute>();

  public LogAttributeRuntimeException()
  {

  }

  public LogAttributeRuntimeException(String message)
  {
    this(message, null, true);
  }

  public LogAttributeRuntimeException(String message, boolean captureLogContext)
  {
    this(message, null, captureLogContext);
  }

  public LogAttributeRuntimeException(String message, Throwable cause)
  {
    this(message, cause, true);
  }

  public LogAttributeRuntimeException(String message, LogAttribute... attrs)
  {
    this(message, null, true, attrs);
  }

  public LogAttributeRuntimeException(Throwable cause)
  {
    this(null, cause, true);
  }

  public LogAttributeRuntimeException(String message, Throwable cause, boolean captureLogContext, LogAttribute... attrs)
  {
    super(message, cause);
    for (LogAttribute la : attrs) {
      if (logAttributeMap.containsKey(la.getType().name()) == false) {
        logAttributeMap.put(la.getType().name(), la);
      }
    }
    // if (cause instanceof WithLogAttributes) {
    // for (LogAttribute la : ((WithLogAttributes) cause).getLogAttributes()) {
    // if (logAttributeMap.containsKey(la.getType().name()) == false) {
    // logAttributeMap.put(la.getType().name(), la);
    // }
    // }
    // }
    // if (captureLogContext == true) {
    // for (Map.Entry<LogAttributeType, LogAttribute> me : LoggingContext.getEnsureContext().getAttributes().entrySet()) {
    // String k = me.getKey().name();
    // if (logAttributeMap.containsKey(k) == false) {
    // logAttributeMap.put(k, me.getValue());
    // }
    // }
    // }
  }

  public Collection<LogAttribute> getLogAttributes()
  {
    return logAttributeMap.values();
  }

  public Map<String, LogAttribute> getLogAttributeMap()
  {
    return logAttributeMap;
  }

  public void setLogAttributeMap(Map<String, LogAttribute> logAttributeMap)
  {
    this.logAttributeMap = logAttributeMap;
  }

}
