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
 * Matches if pattern is equals to token
 * 
 * @author roger
 * 
 */
public class EqualsMatcher<T> extends MatcherBase<T>
{

  private static final long serialVersionUID = -531763040829078392L;

  private T other;

  public EqualsMatcher()
  {

  }

  public EqualsMatcher(T other)
  {
    this.other = other;
  }

  public boolean match(T token)
  {
    if (token == null && other == null) {
      return true;
    }
    if (token == null && other != null)
      return false;
    if (token != null && other == null)
      return false;
    return token.equals(other);
  }

  public T getOther()
  {
    return other;
  }

  public void setOther(T other)
  {
    this.other = other;
  }

  public String toString()
  {
    return other.toString() + " = <Expr>";
  }
}
