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

package de.micromata.genome.gwiki.page.impl.wiki2;

public class GWikiWikiParserTableTest extends GWikiWikiParserTestBase
{
  public void testTableWithLink()
  {
    w2htest(
        "|[http://www.mydomain.de]|\n",
        "<table class=\"gwikiTable\"><tbody><tr>\n<td class=\"gwikitd\"><a href=\"http://www.mydomain.de\" title='http://www.mydomain.de' class=\"gwikiGlobalLink\">http://www.mydomain.de</a></td>\n</tr></tbody></table>");

    w2htest(
        "|a|[http://www.mydomain.de]|b|\n",
        "<table class=\"gwikiTable\"><tbody><tr>\n<td class=\"gwikitd\">a</td><td class=\"gwikitd\"><a href=\"http://www.mydomain.de\" title='http://www.mydomain.de' class=\"gwikiGlobalLink\">http://www.mydomain.de</a></td><td class=\"gwikitd\">b</td>\n</tr></tbody></table>");
  }

  public void testTableWithSpace()
  {
    w2htest("|col A1| \n", "<table class=\"gwikiTable\"><tbody><tr>\n<td class=\"gwikitd\">col A1</td>\n</tr></tbody></table>");
  }

  public void testTableWithUnTabledEnd()
  {
    w2htest("|col A1|x\n",
        "<table class=\"gwikiTable\"><tbody><tr>\n<td class=\"gwikitd\">col A1</td><td class=\"gwikitd\">x</td>\n</tr></tbody></table>");
  }

  public void testTableNested()
  {
    // w2htest("||K1||", "<table class=\"gwikiTable\"><tbody><tr>\n<th class=\"gwikith\">K1</th>\n</tr></tbody></table>");
    w2htest("||* K1||",
        "<table class=\"gwikiTable\"><tbody><tr>\n<th class=\"gwikith\"><ul class=\"star\"><li>K1</li></ul></th>\n</tr></tbody></table>");
  }

  public void testTableFail1()
  {
    w2htest("||K1", "<p>||K1</p>\n");
  }

  public void testTable4()
  {
    w2htest(
        "||K1||K2||\n|Z1\\\\\nY|Z2|\n", //
        "<table class=\"gwikiTable\"><tbody><tr>\n<th class=\"gwikith\">K1</th><th class=\"gwikith\">K2</th>\n</tr><tr>\n<td class=\"gwikitd\">Z1<br/>\nY</td><td class=\"gwikitd\">Z2</td>\n</tr></tbody></table>");
  }

  public void testTable3a()
  {
    w2htest(
        "|h1. Z1|Z2|\n", //
        "<table class=\"gwikiTable\"><tbody><tr>\n<td class=\"gwikitd\"><h1><a name=\"Z1\" target=\"_top\"></a>Z1</h1>\n</td><td class=\"gwikitd\">Z2</td>\n</tr></tbody></table>");
  }

  public void testTable3()
  {
    w2htest(
        "werden.\n||K1||K2||\n|h1. Z1|Z2|\n", //
        "<p>werden.</p>\n<table class=\"gwikiTable\"><tbody><tr>\n<th class=\"gwikith\">K1</th><th class=\"gwikith\">K2</th>\n</tr><tr>\n<td class=\"gwikitd\"><h1><a name=\"Z1\" target=\"_top\"></a>Z1</h1>\n</td><td class=\"gwikitd\">Z2</td>\n</tr></tbody></table>");
  }

  public void testTableWithHeading()
  {
    w2htest(
        "||K1||K2||\n|h1. Z1|Z2|\n", //
        "<table class=\"gwikiTable\"><tbody><tr>\n<th class=\"gwikith\">K1</th><th class=\"gwikith\">K2</th>\n</tr><tr>\n<td class=\"gwikitd\"><h1><a name=\"Z1\" target=\"_top\"></a>Z1</h1>\n</td><td class=\"gwikitd\">Z2</td>\n</tr></tbody></table>");
  }

  public void testSimpleTable()
  {
    w2htest(
        "||K1||K2||\n|Z1|Z2|\n", //
        "<table class=\"gwikiTable\"><tbody><tr>\n<th class=\"gwikith\">K1</th><th class=\"gwikith\">K2</th>\n</tr><tr>\n<td class=\"gwikitd\">Z1</td><td class=\"gwikitd\">Z2</td>\n</tr></tbody></table>");
  }

}
