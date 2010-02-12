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

import java.util.ArrayList;
import java.util.List;

import junit.framework.TestCase;
import de.micromata.genome.gwiki.utils.CommaListParser;

public class CommaListParserTest extends TestCase
{
  public void parse(String input, String... expected)
  {
    List<String> ret = CommaListParser.parseCommaList(input);
    List<String> expectedL = new ArrayList<String>();
    for (int i = 0; i < expected.length; ++i) {
      expectedL.add(expected[i]);
    }
    if (expectedL.equals(ret) == false) {
      fail("expected: " + expectedL + "; got: " + ret);
    }
  }

  public void testIt()
  {
    parse("\"a\" , ", "a", "");
    parse("  ");
    parse(",", "", "");
    parse("\"a\"", "a");
    parse("\"a,b\"", "a,b");
    parse("\"(Rechte{0,1})\"", "(Rechte{0,1})");
    parse(null);
    parse("");
    
    
    parse(" , ", "", "");
    parse("a, ", "a", "");
    
    parse("\" a\" , ", " a", "");
    parse("\" a\", b ", " a", "b");
    parse("a,b", "a", "b");

    parse("a", "a");
    parse("a, b", "a", "b");
    parse("a\\b, c", "a\\b", "c");
    parse(" a, b ", "a", "b");

    parse("\", a\", b ", ", a", "b");
    parse("\"\\\" a\", b ", "\" a", "b");
    parse("\"\\\" a\", b ", "\" a", "b");

  }
}
