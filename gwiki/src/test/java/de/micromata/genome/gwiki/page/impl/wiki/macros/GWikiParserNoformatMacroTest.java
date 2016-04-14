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

package de.micromata.genome.gwiki.page.impl.wiki.macros;

import java.util.HashMap;
import java.util.Map;

import de.micromata.genome.gwiki.page.impl.wiki.GWikiMacroClassFactory;
import de.micromata.genome.gwiki.page.impl.wiki.GWikiMacroFactory;
import de.micromata.genome.gwiki.page.impl.wiki.macros.html.GWikiHtmlDivMacro;
import de.micromata.genome.gwiki.page.impl.wiki2.GWikiWikiParserTestBase;

/**
 * @author Roger Rene Kommer (r.kommer@micromata.de)
 * 
 */
public class GWikiParserNoformatMacroTest extends GWikiWikiParserTestBase
{
  Map<String, GWikiMacroFactory> macroFactories = new HashMap<String, GWikiMacroFactory>();

  public GWikiParserNoformatMacroTest()
  {
    macroFactories.put("noformat", new GWikiMacroClassFactory(GWikiNoFormatBodyMacro.class));
    macroFactories.put("div", new GWikiMacroClassFactory(GWikiHtmlDivMacro.class));
  }

  public void testPut()
  {
    w2htest("{noformat}<{noformat}", "<p>&lt;</p>\n", macroFactories);
  }

  public void testNested()
  {
    //parseDumpWiki("{div}a{noformat}b{noformat}{div}", macroFactories);
    w2htest("{div}a{noformat}b{noformat}{div}", "<div><p>ab</p>\n</div>",
        macroFactories);
  }
}
