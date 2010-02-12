////////////////////////////////////////////////////////////////////////////

// Copyright (C) 2010 Micromata GmbH
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


// Copyright (C) 2010 Micromata GmbH
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


package de.micromata.genome.gwiki.page.impl.wiki;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import junit.framework.TestCase;

public class KeywordReplacerTest extends TestCase
{
  void testMatcher(String pattern, String text, String expected)
  {
    Pattern p = Pattern.compile(pattern);
    Matcher m = p.matcher(text);
    boolean found = m.find();
    if (found == false && expected == null) {
      return;
    }
    if (expected == null) {
      fail("Pattern '" + pattern + "' with Text '" + text + "' should NOT matched");
    }
    if (found == false) {
      fail("Pattern '" + pattern + "' with Text '" + text + "' should matched with: " + expected);
    }
    if (expected.equals(m.group(0)) == false) {
      fail("Pattern '" + pattern + "' with Text '" + text + "' should matched with: '" + expected + "' but got: '" + m.group(0) + "'");
    }
  }

  public void testIt()
  {
    testMatcher("((?:Rechte)|(?:Recht))", "Recht.", "Recht");
    testMatcher("((?:Rechte)|(?:Recht))", "Rechte.", "Rechte");
    testMatcher("(Rechte{0,1})", " Recht.", "Recht");
    testMatcher("(Rechte{0,1})", " Rechte.", "Rechte");
  }
}
