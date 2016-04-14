//
// Copyright (C) 2010-2016 Roger Rene Kommer & Micromata GmbH
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//  http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.
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

////////////////////////////////////////////////////////////////////////////


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

////////////////////////////////////////////////////////////////////////////


package de.micromata.genome.gwiki;

import java.util.regex.Pattern;

import junit.framework.TestCase;

import com.uwyn.jhighlight.tools.StringUtils;

public class KeywordMatcherTest extends TestCase
{
  // public String tranformToRegExp(String matcher)
  // {
  // StringBuilder sb = new StringBuilder();
  // List<String> tks = Converter.parseStringTokens(matcher, "()|", true);
  // for (String tk : tks) {
  // char c = tk.charAt(0);
  // switch (c) {
  // case '(':
  // sb.append("(");
  // break;
  // case ')':
  // sb.append("){0,1}");
  // break;
  // case '|':
  // sb.append("|");
  // break;
  // default:
  // sb.append(tk);
  // break;
  // }
  // }
  // return sb.toString();
  // }

  // boolean match(String matcher, String text)
  // {
  // String regex = "spring(((e){0,1}?)((n){0,1}|(r){0,1})){0,1}";
  // Pattern p = Pattern.compile(regex);
  // Matcher m = p.matcher(text);
  // boolean found = m.matches();
  // return found;
  // }
  //
  // boolean match(String matcher, String text)
  // {
  //
  // List<String> tks = Converter.parseStringTokens(matcher, "()|", true);
  // for (String tk : tks) {
  // char c = tk.charAt(0);
  // switch (c) {
  // case '(':
  //
  // break;
  // case ')':
  // sb.append("){0,1}");
  // break;
  // case '|':
  // sb.append("|");
  // break;
  // default:
  // sb.append(tk);
  // break;
  // }
  // }
  // return true;
  // }

  public boolean match(String matcher, String text)
  {
    String rm = StringUtils.replace(matcher, ")", "){0,1}");
    return Pattern.compile(rm).matcher(text).matches();
  }

  public void testMatch()
  {
    // spring((e)((n)|(r)))
    // spring(((e){0,1}?))((n){0,1}|(r){0,1}){0,1}
    String matcher = "spring(e(n|r))";
    // assertTrue(match(matcher, "spring"));
    // assertTrue(match(matcher, "springe"));
    assertTrue(match(matcher, "springen"));
    assertTrue(match(matcher, "springer"));
    assertFalse(match(matcher, "springex"));
    assertFalse(match(matcher, "springnr"));
    assertFalse(match(matcher, "springr"));

    matcher = "spring(e(nd|r))";
    assertTrue(match(matcher, "springer"));
    assertTrue(match(matcher, "springend"));
  }
}
