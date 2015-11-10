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

package de.micromata.genome.util.matcher.string;

/**
 * 
 * @author roger
 * 
 * @param <T>
 */
public abstract class StringPatternMatcherBase<T> extends StringMatcherBase<T>
{
  /**
   * 
   */
  private static final long serialVersionUID = -1169791355239547248L;

  protected String pattern;

  public StringPatternMatcherBase()
  {

  }

  public StringPatternMatcherBase(String pattern)
  {
    this.pattern = pattern;
  }

  public String getPattern()
  {
    return pattern;
  }

  public void setPattern(String pattern)
  {
    this.pattern = pattern;
  }

  public String toString()
  {
    return "<EXPR>.patternMatch(" + pattern + ")";
  }

}
