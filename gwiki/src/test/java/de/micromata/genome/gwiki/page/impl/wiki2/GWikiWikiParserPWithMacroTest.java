////////////////////////////////////////////////////////////////////////////
// 
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
// 
////////////////////////////////////////////////////////////////////////////
package de.micromata.genome.gwiki.page.impl.wiki2;

import java.util.HashMap;
import java.util.Map;

import de.micromata.genome.gwiki.page.impl.wiki.GWikiMacroClassFactory;
import de.micromata.genome.gwiki.page.impl.wiki.GWikiMacroFactory;
import de.micromata.genome.gwiki.page.impl.wiki.GWikiMacroRenderFlags;
import de.micromata.genome.gwiki.page.impl.wiki.macros.GWikiHtmlBodyTagMacro;
import de.micromata.genome.gwiki.page.impl.wiki.slideshow.GWikiSlideMacro;

/**
 * @author Roger Rene Kommer (r.kommer@micromata.de)
 * 
 */
public class GWikiWikiParserPWithMacroTest extends GWikiWikiParserTestBase
{
  Map<String, GWikiMacroFactory> macroFactories = new HashMap<String, GWikiMacroFactory>();

  public GWikiWikiParserPWithMacroTest()
  {
    macroFactories.put("slide", new GWikiMacroClassFactory(GWikiSlideMacro.class, GWikiMacroRenderFlags.combine(
        GWikiMacroRenderFlags.TrimTextContent, GWikiMacroRenderFlags.ContainsTextBlock, GWikiMacroRenderFlags.NoWrapWithP)));
    macroFactories.put("center", new GWikiMacroClassFactory(GWikiHtmlBodyTagMacro.class));
  }

  public void testPageMacros1()
  {

    w2htest("{slide:title=asdf}\na\n\nb{slide}", "<h1>asdf</h1>\n<p>a</p>\n<p>b</p>\n", macroFactories);
  }

  public void testPageMacros2()
  {
    w2htest("{slide:title=asdf}\na\nb\n\nc\n{slide}", "<h1>asdf</h1>\n<p>a<br/>\nb</p>\n<p>c</p>\n", macroFactories);
  }

  public void testPageMacros3()
  {
    String t = "{slide:title=T}\nA\n\n{center}x{center}\n{slide}";
    w2htest(t, "<h1>T</h1>\n<p>A</p>\n<p><center>x</center></p>\n", macroFactories);
  }

  public void testPageMacros4()
  {
    String t = "{slide:title=T}\n- a\n- b\n{slide}\n";
    w2htest(t, "<h1>T</h1>\n<ul class=\"minus\" type=\"square\"><li>a</li><li>b</li></ul>", macroFactories);
  }

}
