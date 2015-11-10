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
// Created   14.11.2009
// Copyright Micromata 14.11.2009
//
/////////////////////////////////////////////////////////////////////////////
package de.micromata.genome.util.matcher;

/**
 * matcher.match(object) returns true if matcher.object > arg
 * 
 * @author roger@micromata.de
 * 
 */
public class MoreThanMatcher<T extends Comparable<T>> extends ComparatorMatcherBase<T>
{

  private static final long serialVersionUID = 4511221781327480260L;

  public MoreThanMatcher()
  {
  }

  public MoreThanMatcher(T other)
  {
    super(other);
  }

  public boolean match(T object)
  {
    if (other == null || object == null)
      return false;
    return other.compareTo(object) < 0;
  }

  public String toString()
  {
    return "(" + other.toString() + " > EXPR)";
  }

}
