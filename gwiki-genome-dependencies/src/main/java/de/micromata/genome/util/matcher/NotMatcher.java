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
 * Matches if nested matcher does not matches
 * 
 * @author roger
 * 
 */
public class NotMatcher<T> extends MatcherBase<T>
{

  private static final long serialVersionUID = 4329354733404236006L;

  private Matcher<T> nested;

  public NotMatcher()
  {

  }

  public NotMatcher(Matcher<T> nested)
  {
    this.nested = nested;
  }

  public boolean match(T token)
  {
    return nested.match(token) == false;
  }

  public Matcher<T> getNested()
  {
    return nested;
  }

  public void setNested(Matcher<T> nested)
  {
    this.nested = nested;
  }

  public String toString()
  {
    return "!" + nested.toString();
  }

}
