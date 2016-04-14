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

package de.micromata.genome.gwiki.page.search.expr;

import junit.framework.TestCase;

/**
 * @author roger
 * 
 */
public class SearchUtilsTest extends TestCase
{
  protected void testExpression(String expr)
  {
    System.out.println(expr);
    SearchExpressionParser parser = new SearchExpressionParser();
    SearchExpression sexpr = parser.parse(expr);
    String str = sexpr.toString();
    System.out.println(expr + ": " + str);

  }

  public void testPureQuoted()
  {
    testExpression("\"a\\\"b\"");
    testExpression("\"");
    testExpression("\"a\"");

  }

  public void testQuotedQuote()
  {
    String ret;
    ret = SearchUtils.createLinkExpression("\"a\\\"x\"", false, null);
    testExpression(ret);
  }

  public void testSingle()
  {
    String ret = SearchUtils.createLinkExpression("A", false, null);
    testExpression(ret);

    ret = SearchUtils.createLinkExpression("A ", false, null);
    testExpression(ret);

    ret = SearchUtils.createLinkExpression("A B", false, null);
    testExpression(ret);

    ret = SearchUtils.createLinkExpression("A B C", false, null);
    testExpression(ret);
  }

  public void testQuoted()
  {
    String ret = SearchUtils.createLinkExpression("\"", false, null);
    testExpression(ret);

    ret = SearchUtils.createLinkExpression("\"a\\\"x\"", false, null);
    testExpression(ret);

  }

}
