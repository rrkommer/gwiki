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

import groovy.lang.GroovyShell;
import groovy.lang.Script;

/**
 * 
 * @author roger
 * 
 * @param <T>
 */
public class GroovyMatcherFactory<T> implements MatcherFactory<T>
{

  public Matcher<T> createMatcher(String pattern)
  {
    GroovyShell gs = new GroovyShell();
    Script script = gs.parse(pattern);
    return new GroovyMatcher<T>(pattern, script.getClass());
  }

  public String getRuleString(Matcher<T> matcher)
  {
    if ((matcher instanceof GroovyMatcher) == false)
      return "<unknown>";
    return "${" + ((GroovyMatcher<T>) matcher).getSource() + "}";
  }

}
