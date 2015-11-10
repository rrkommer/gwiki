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

package de.micromata.genome.util.matcher;

/**
 * Just a grouped matcher
 * 
 * @author roger@micromata.de
 * 
 */
public class GroupMatcher<T> extends MatcherBase<T>
{
  /**
   * 
   */
  private static final long serialVersionUID = -3472867724633767575L;

  private Matcher<T> groupedMatcher;

  public GroupMatcher()
  {

  }

  public GroupMatcher(Matcher<T> groupedMatcher)
  {
    this.groupedMatcher = groupedMatcher;
  }

  public boolean match(T object)
  {
    return groupedMatcher.match(object);
  }

  public String toString()
  {
    return "(" + groupedMatcher.toString() + ")";
  }
}
