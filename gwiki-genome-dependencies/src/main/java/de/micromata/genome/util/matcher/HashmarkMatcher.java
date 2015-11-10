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
// Author    jens@micromata.de
// Created   31.12.2008
// Copyright Micromata 31.12.2008
//
/////////////////////////////////////////////////////////////////////////////
package de.micromata.genome.util.matcher;

/**
 * matches nothing, but is used to identify inactive derivation of e.g. rights/roles that can be selected for sub-matchers in a hierarchy.
 * In that case the following mappong is used: MatchPositive -> match in sub-matcher if not -'ed there MatchNegative or NoMatch -> match in
 * sub-matcher only if +'ed there
 * 
 * @author jens@micromata.de
 */
public class HashmarkMatcher<T> extends NoneMatcher<T>
{
  /**
   * 
   */
  private static final long serialVersionUID = -5434145883323585312L;

  protected Matcher<T> enclosedMatcher;

  public HashmarkMatcher(Matcher<T> enclosedMatcher)
  {
    this.enclosedMatcher = enclosedMatcher;
  }

  public Matcher<T> getEnclosedMatcher()
  {
    return enclosedMatcher;
  }

  public boolean match(T object)
  {
    return false;
  }
}
