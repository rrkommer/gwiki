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
package de.micromata.genome.gwiki.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.exception.ExceptionUtils;

import de.micromata.genome.util.types.Pair;

/**
 * @author stefans
 *
 */
public class GWikiLogEntry
{
  private Date date;
  
  private String message;
  
  private Object[] params;
  
  private GWikiLogLevel logLevel;
  
  private Throwable ex;

  /**
   * @param date
   * @param message
   * @param params
   * @param logLevel
   * @param ex
   */
  public GWikiLogEntry(GWikiLogLevel logLevel, String message, Object[] params, Throwable ex)
  {
    super();
    this.date = new Date();
    this.message = message;
    this.setParams(params);
    this.logLevel = logLevel;
    this.ex = ex;
  }

  /**
   * @param date
   * @param message
   * @param params
   * @param logLevel
   * @param ex
   */
  public GWikiLogEntry(GWikiLogLevel logLevel, String message, Object[] params)
  {
    this(logLevel, message, params, null);
  }

  /**
   * @param date
   * @param message
   * @param params
   * @param logLevel
   * @param ex
   */
  public GWikiLogEntry(GWikiLogLevel logLevel, String message)
  {
    this(logLevel, message, null);
  }
  
  /**
   * @return the date
   */
  public Date getDate()
  {
    return date;
  }

  /**
   * @param date the date to set
   */
  public void setDate(Date date)
  {
    this.date = date;
  }

  /**
   * @return the message
   */
  public String getMessage()
  {
    return message;
  }

  /**
   * @param message the message to set
   */
  public void setMessage(String message)
  {
    this.message = message;
  }

  /**
   * @return the logLevel
   */
  public GWikiLogLevel getLogLevel()
  {
    return logLevel;
  }

  /**
   * @param logLevel the logLevel to set
   */
  public void setLogLevel(GWikiLogLevel logLevel)
  {
    this.logLevel = logLevel;
  }

  /**
   * @param ex the ex to set
   */
  public void setEx(Throwable ex)
  {
    this.ex = ex;
  }

  /**
   * @return the ex
   */
  public Throwable getEx()
  {
    return ex;
  }

  /**
   * @param params the params to set
   */
  public void setParams(Object[] params)
  {
    this.params = params;
  }

  /**
   * @return the params
   */
  public Object[] getParams()
  {
    return params;
  }

  public List<Pair<String,String>> getParamMap()
  {
    List<Pair<String,String>> paramMap = new ArrayList<Pair<String,String>>();
    if (params != null && params.length > 0) {
      for (int i = 0; i < params.length; i+=2) {
        Object val = params[i+1];
        
        String key;
        if (params[i] instanceof GLogAttributeName) {
          key = ((GLogAttributeName) params[i]).name();
        } else {
          key = ObjectUtils.toString(params[i]);
        }
        
        paramMap.add(new Pair<String, String>(key, ObjectUtils.toString(val)));
      }
    }
    return paramMap;
  }
  
  public String getStacktrace() {
    if (ex == null) {
      return "";    
     }
    return ExceptionUtils.getStackTrace(ex);
  }
}
