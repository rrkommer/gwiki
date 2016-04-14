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

package de.micromata.genome.gwiki.page.impl.wiki;

import java.util.Map;

import org.junit.Test;

import de.micromata.genome.gwiki.page.impl.wiki.macros.registry.GWikiBuildinProviderServiceImpl;
import de.micromata.genome.gwiki.page.impl.wiki2.GWikiWikiParserTestBase;

public class WikiParsePlayzoneTest extends GWikiWikiParserTestBase
{
  Map<String, GWikiMacroFactory> macroFactories = new GWikiBuildinProviderServiceImpl().getMacros();

  //  @Test
  public void xtestParsePs()
  {

    String wiki = "h2. Title\nFirst\n\nSecond\nThird";
    String genHtml = wiki2html(wiki, macroFactories);
    System.out.println("W2H: " + wiki + "\nHTML:\n" + genHtml);
  }

  //  @Test
  public void testParseDivs()
  {
    String wiki = "h2. Title\n{div}\nFirst\nSecond{div}";
    String genHtml = wiki2html(wiki, macroFactories);
    System.out.println("W2H: " + wiki + "\nHTML:\n" + genHtml);
    parseDumpWiki(wiki, macroFactories);
  }

  @Test
  public void testParseOnClick()
  {
    String wiki = "{fieldset:class=gwikiExpandableFieldSet}\r\n" +
        "{legend:onclick=$(this).next('div').toggle();}X{legend}Y{fieldset}";

    String genHtml = wiki2html(wiki, macroFactories);
    System.out.println("W2H: " + wiki + "\nHTML:\n" + genHtml);
    parseDumpWiki(wiki, macroFactories);
  }
}
