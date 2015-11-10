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

public class StringToken extends TokenBase
{
  private String pattern;

  public StringToken(int tokenType, String pattern)
  {
    super(tokenType);
    this.pattern = pattern;
  }

  public StringToken()
  {

  }

  public TokenResult consume(String text, char escapeChar)
  {
    if (text.startsWith(pattern) == true)
      return new StringTokenResult(this);

    return null;
  }

  public boolean match(String text)
  {
    return text.startsWith(pattern) == true;
  }

  public String getPattern()
  {
    return pattern;
  }

  public void setPattern(String pattern)
  {
    this.pattern = pattern;
  }
}
