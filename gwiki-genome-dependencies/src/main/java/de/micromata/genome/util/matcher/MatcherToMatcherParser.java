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

import java.util.List;

import de.micromata.genome.util.text.TextSplitterUtils;
import de.micromata.genome.util.types.Pair;

/**
 * 
 * @author roger
 * 
 */
public class MatcherToMatcherParser
{
  static public <LT, RT> void parse(String text, MatcherFactory<LT> left, MatcherFactory<RT> right, String divider,
      Pair<Matcher<LT>, Matcher<RT>> retVal)
  {
    List<String> parts = TextSplitterUtils.parseStringTokens(text, new String[] { divider, "\\"}, '\\', false, true);
    if (parts.size() != 2) {
      throw new RuntimeException("Expect " + divider + " in rule string. rule: " + text);
    }
    Matcher<LT> leftM = left.createMatcher(parts.get(0));
    Matcher<RT> rightM = right.createMatcher(parts.get(1));
    retVal.setFirst(leftM);
    retVal.setSecond(rightM);

  }

}
