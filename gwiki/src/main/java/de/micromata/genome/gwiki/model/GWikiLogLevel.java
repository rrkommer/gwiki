////////////////////////////////////////////////////////////////////////////
// 
// Copyright (C) 2010 Micromata GmbH
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

/**
 * LogLevel
 * 
 * @author Stefan Stuetzer/Roger Kommer
 */
public enum GWikiLogLevel
{
  /**
   * Debugging only
   */
  DEBUG(0),
  /**
   * Unimportant information
   */
  INFO(1),
  /**
   * Important information
   */
  NOTE(2),
  /**
   * Warning, wrong input
   */
  WARN(3),
  /**
   * Maybe programming error
   */
  ERROR(4),
  /**
   * Fatal resource situation.
   */
  FATAL(5);

  private int priority;

  GWikiLogLevel(int priority)
  {
    this.priority = priority;
  }

  /**
   * @return the priority
   */
  public int getPriority()
  {
    return priority;
  }

}
