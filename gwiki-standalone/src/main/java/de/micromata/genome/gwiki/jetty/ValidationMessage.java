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

package de.micromata.genome.gwiki.jetty;

import org.apache.log4j.Level;

/**
 * 
 * @author Roger Rene Kommer (r.kommer.extern@micromata.de)
 *
 */
public class ValidationMessage
{
  private Level level = Level.ERROR;
  private String message;
  private String property;

  public ValidationMessage()
  {

  }

  public ValidationMessage(String message)
  {
    this.message = message;
  }

  public ValidationMessage(Level level, String property, String message)
  {
    this.level = level;
    this.message = message;
    this.property = property;
  }

  public String getMessage()
  {
    return message;
  }

  public void setMessage(String message)
  {
    this.message = message;
  }

  public Level getLevel()
  {
    return level;
  }

  public void setLevel(Level type)
  {
    this.level = type;
  }

  public String getProperty()
  {
    return property;
  }

  public void setProperty(String property)
  {
    this.property = property;
  }

}
