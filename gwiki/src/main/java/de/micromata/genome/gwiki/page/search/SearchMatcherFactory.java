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

package de.micromata.genome.gwiki.page.search;

import java.util.List;

import de.micromata.genome.util.matcher.Matcher;
import de.micromata.genome.util.matcher.MatcherFactory;
import de.micromata.genome.util.matcher.OrMatcher;
import de.micromata.genome.util.matcher.string.ContainsMatcher;
import de.micromata.genome.util.types.Converter;

/**
 * Factory to create matchers.
 * 
 * @author Roger Rene Kommer (r.kommer@micromata.de)
 * 
 */
public class SearchMatcherFactory implements MatcherFactory<String>
{

  public Matcher<String> createMatcher(String pattern)
  {
    List<String> parts = Converter.parseStringTokens(pattern, " ", false);

    Matcher<String> lm = null;// new OrMatcher<String>();

    for (String p : parts) {
      if (lm == null) {
        lm = new ContainsMatcher<String>(p);
      } else {
        lm = new OrMatcher<String>(lm, new ContainsMatcher<String>(p));
      }
    }
    return lm;
  }

  public String getRuleString(Matcher<String> matcher)
  {
    return "not supported";
  }

}
