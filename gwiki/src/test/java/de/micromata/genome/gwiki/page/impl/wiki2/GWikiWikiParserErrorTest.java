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

import java.util.HashMap;
import java.util.Map;

import de.micromata.genome.gwiki.page.impl.wiki.GWikiMacroClassFactory;
import de.micromata.genome.gwiki.page.impl.wiki.GWikiMacroFactory;
import de.micromata.genome.gwiki.page.impl.wiki.macros.GWikiCodeMacro;
import de.micromata.genome.gwiki.page.impl.wiki.macros.html.GWikiHtmlDivMacro;

/**
 * @author Roger Rene Kommer (r.kommer@micromata.de)
 * 
 */
public class GWikiWikiParserErrorTest extends GWikiWikiParserTestBase
{
  public void testEvaledBodyTest()
  {
    Map<String, GWikiMacroFactory> macroFactories = new HashMap<String, GWikiMacroFactory>();
    macroFactories.put("div", new GWikiMacroClassFactory(GWikiHtmlDivMacro.class));

    String wiki = "y\n\nx{div}a\n";
    String html = "<p>y</p>\n<p>x<color=\"red\">Missing macro end for  div; {div}\n{div}\n</color>a</p>\n";
    w2htest(wiki, html, macroFactories);
  }

  public void testNonEvaledBodyTest()
  {
    Map<String, GWikiMacroFactory> macroFactories = new HashMap<String, GWikiMacroFactory>();
    macroFactories.put("code", new GWikiMacroClassFactory(GWikiCodeMacro.class));

    String wiki = "y\n\nx{code}a\n";
    String html = "<p>y</p>\n<p>x<color=\"red\">Missing macro end for  code</color>a</p>\n";
    w2htest(wiki, html, macroFactories);
  }
}
