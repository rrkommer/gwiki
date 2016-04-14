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

package de.micromata.genome.gwiki.page.impl.wiki2;

/**
 * @author Roger Rene Kommer (r.kommer@micromata.de)
 * 
 */
public class GWikiWikiParserPTest extends GWikiWikiParserTestBase
{
  // public void testOneLine()
  // {
  // w2htest("a.", "<p>a.</p>");
  // }
  public void testPanBr2()
  {

    w2htest("a\nb\n\nc\nd\n\ne\nf", "<p>a<br/>\nb</p>\n<p>c<br/>\nd</p>\n<p>e<br/>\nf</p>\n");
    // geht
    w2htest("a\nb\n\nc\nd\n\ne", "<p>a<br/>\nb</p>\n<p>c<br/>\nd</p>\n<p>e</p>\n");
  }

  public void testPandBr()
  {
    w2htest("T1\nT2\n\nT3\n", "<p>T1<br/>\nT2</p>\n<p>T3</p>\n");
  }

  public void test2LineAfterHeading()
  {
    w2htest("h1. U\n\nb", "<h1><a name=\"U\" target=\"_top\"></a>U</h1>\n<p>b</p>\n");
  }

  public void test1LineAfterHeading()
  {
    w2htest("h1. U\nb", "<h1><a name=\"U\" target=\"_top\"></a>U</h1>\n<p>b</p>\n");
  }

}
