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
// Created   12.01.2009
// Copyright Micromata 12.01.2009
//
/////////////////////////////////////////////////////////////////////////////
package de.micromata.genome.util.runtime;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections15.ListUtils;

import de.micromata.genome.util.matcher.Matcher;

/**
 * Utils around Threads.
 * 
 * @author roger@micromata.de
 * 
 */
public class ThreadUtils
{
  /**
   * Find Thread with given Thread.getId().
   * 
   * @param tid a thread id
   * @return the Thread found or null if thread was not able to found
   */
  public static Thread findThreadById(long tid)
  {
    Map<Thread, StackTraceElement[]> allStacks = Thread.getAllStackTraces();
    for (Map.Entry<Thread, StackTraceElement[]> me : allStacks.entrySet()) {
      if (me.getKey().getId() == tid) {
        return me.getKey();
      }
    }
    return null;
  }

  /**
   * Find a list of thread where the thread name match to given matcher
   * 
   * @param matcher a valid matcher. if null returns an empty list
   * @return always not null. In case of nothing empty list.
   */
  @SuppressWarnings("unchecked")
  public static List<Thread> findThreadsByNameMatcher(Matcher<String> matcher)
  {
    if (matcher == null)
      return ListUtils.EMPTY_LIST;
    Map<Thread, StackTraceElement[]> allStacks = Thread.getAllStackTraces();
    List<Thread> ret = new ArrayList<Thread>();
    for (Thread t : allStacks.keySet()) {
      if (matcher.match(t.getName()) == true)
        ret.add(t);
    }
    return ret;
  }
}
