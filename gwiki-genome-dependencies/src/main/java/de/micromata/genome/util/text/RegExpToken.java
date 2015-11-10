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

package de.micromata.genome.util.text;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 
 * @author roger
 * 
 */
public class RegExpToken extends TokenBase
{
  private Pattern pattern;

  public RegExpToken(int tokenType, String patternString)
  {
    super(tokenType);
    pattern = Pattern.compile(patternString);
  }

  public boolean match(String text)
  {
    Matcher m = pattern.matcher(text);

    boolean doMatch = m.matches() == true;
    return doMatch;
  }

  public TokenResult consume(String text, char escapeChar)
  {
    Matcher matcher = pattern.matcher(text);
    Matcher mr = matcher;// .toMatchResult();
    mr.find();
    if (mr.groupCount() < 2)
      return null;
    String matched = mr.group(1);

    return new RegExpTokenResult(this, matcher, matched);
  }

  public String toString()
  {
    return pattern.toString();
  }

  public Pattern getPattern()
  {
    return pattern;
  }

  public void setPattern(Pattern pattern)
  {
    this.pattern = pattern;
  }

}
