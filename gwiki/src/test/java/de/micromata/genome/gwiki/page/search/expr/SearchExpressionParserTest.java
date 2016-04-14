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

package de.micromata.genome.gwiki.page.search.expr;

import java.util.regex.MatchResult;
import java.util.regex.Pattern;

import junit.framework.TestCase;

public class SearchExpressionParserTest extends TestCase
{
  public void testParseExerptTest()
  {
    Pattern pattern = Pattern.compile("(.*?)<\\^([0-9]+)>(.*?)(</\\^>)(.*)");
    // String text = "</^>x<^10>Asdf<^/>asdfasdf<^10>sddfdfdf<^/>asdfsdf";
    String text = "</^><^20> 1         Genome/OSGi</^><^1> 1.1        Einsatzgebiete Genome ist fuer ein weites Spektrum, vom Einsatz bei kleinen und mittleren Projekten im Bereich Desktop oder Webserver bis hin zu Servern im Clusterbetrieb, e";
    java.util.regex.Matcher rm = pattern.matcher(text);
    boolean matchFound = rm.find();
    MatchResult m = rm.toMatchResult();
    int gc = rm.groupCount();
    if (matchFound == true) {
      String lr = m.group(1);
      String zahl = m.group(2);
      String body = m.group(3);
      String rr = text.substring(m.end(4));
      for (int i = 0; i < gc; ++i) {
        String mp = m.group(i);
        // System.out.println("" + i + ": " + mp);
      }
    }
  }

  SearchExpressionParser parser = new SearchExpressionParser();

  public void testGrammar(String text, String expected)
  {
    SearchExpression expr = parser.parse(text);
    String str = expr.toString();
    assertEquals(expected, str);
  }

  public void testBasic()
  {
    // not allowed testGrammar("a not b", "orlist(contains(a), not(contains(b)))");
    testGrammar("a b", "orlist(contains(a), contains(b))");
    testGrammar("a and b", "and(contains(a), contains(b))");

    testGrammar("a", "contains(a)");

    testGrammar("a and not b", "and(contains(a), not(contains(b)))");
    testGrammar("a or not b", "or(contains(a), not(contains(b)))");
    testGrammar("not a", "not(contains(a))");
    testGrammar("parentpageid:pageId", "command(parentpageid:contains(pageId))");
    testGrammar("a and not (b or c)", "and(contains(a), not(or(contains(b), contains(c))))");

  }

  public void testAndContaining()
  {
    testGrammar("parentpageid:pop/PopHandbuecher and(produkt)", //
        "and(command(parentpageid:contains(pop/PopHandbuecher)), contains(produkt))");
  }

  public void testCompareOps()
  {
    testGrammar(
        "prop:TYPE != gwiki and prop:PAGEID like \"admin/templates/*\"",
        "and(compare(command(prop:contains(TYPE)) to !gwiki = <Expr>), compare(command(prop:contains(PAGEID)) to <EXPR>.startsWith(admin/templates/)))");

    testGrammar("prop:TITLE ~ \"Genome\"", "compare(command(prop:contains(TITLE)) to <EXPR>.containsIgnoreCase(Genome))");
    testGrammar("prop:TITLE like \"*Genome*\"", "compare(command(prop:contains(TITLE)) to <EXPR>.contains(Genome))");
    testGrammar("prop:TITLE like \"Genome*\"", "compare(command(prop:contains(TITLE)) to <EXPR>.startsWith(Genome))");
    testGrammar("prop:TITLE like \"~Genome [sS].*\"", "compare(command(prop:contains(TITLE)) to <EXPR>.regexp(Genome [sS].*))");
  }

  public void testIt()
  {
    SearchExpressionParser parser = new SearchExpressionParser();
    String command = "prop:TITLE like \"~Genome [sS].*\"";
    SearchExpression expr = parser.parse(command);
    String s = expr.toString();
    System.out.println(s);
  }

  public void testOrderBy()
  {
    // testGrammar("a order by relevance", "contains(a) order by relevance asc");
    // testGrammar("a order by relevance desc", "contains(a) order by relevance desc");
    testGrammar("a order by relevance desc, prop:TITLE asc", "contains(a) order by relevance desc, command(prop:contains(TITLE)) asc");
  }
}
