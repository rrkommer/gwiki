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
// Created   04.07.2009
// Copyright Micromata 04.07.2009
//
/////////////////////////////////////////////////////////////////////////////
package de.micromata.genome.util.matcher.string;

import de.micromata.genome.util.matcher.Matcher;

/**
 * Matcher builder for string based matcher.
 * 
 * @author roger@micromata.de
 * 
 */
public class StringMatcherBuilder
{
  public static Matcher<String> contains(String text)
  {
    return new ContainsMatcher<String>(text);
  }

  public static Matcher<String> startsWith(String text)
  {
    return new StartWithMatcher<String>(text);
  }

  public static Matcher<String> endsWith(String text)
  {
    return new EndsWithMatcher<String>(text);
  }

  public static Matcher<String> wildcart(String text)
  {
    return new WildcardMatcher<String>(text);
  }

  public static Matcher<String> regexp(String text)
  {
    return new RegExpMatcher<String>(text);
  }
}
