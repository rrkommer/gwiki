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
import de.micromata.genome.gwiki.page.impl.wiki.macros.GWikiCodeMacro;
import de.micromata.genome.gwiki.page.impl.wiki.macros.GWikiHtmlBodyDivTagMacro;
import de.micromata.genome.gwiki.page.impl.wiki.macros.GWikiPageIntroMacroBean;
import de.micromata.genome.gwiki.plugin.s5slideshow_1_0.GWikiSlideMacro;

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
    macroFactories.put("center", new GWikiMacroClassFactory(GWikiHtmlBodyDivTagMacro.class));
    macroFactories.put("code", new GWikiMacroClassFactory(GWikiCodeMacro.class));
    macroFactories.put("pageintro", new GWikiMacroClassFactory(GWikiPageIntroMacroBean.class));
  }

  public void testPageIntroMacros4()
  {
    w2htest(
        "{pageintro}- a {{m}} b\n- c{pageintro}\n",
        "<div class=\"wikiPageIntro\"><ul class=\"minus\" type=\"square\"><li>a <span style=\"font-family:monospace\">m</span> b</li><li>c</li></ul></div>",
        macroFactories);
  }

  public void testPageIntroMacros2()
  {
    w2htest("{pageintro}P{pageintro}\n\nT", "<div class=\"wikiPageIntro\"><p>P</p>\n</div><p>T</p>\n", macroFactories);
  }

  public void testPageMacros3()
  {
    String t = "{slide:title=T}\nA\n\n{center}x{center}\n{slide}";
    w2htest(t, "<h1>T</h1>\n<p>A</p>\n<center><p>x</p>\n</center>", macroFactories);
  }

  public void testPageIntroMacros1()
  {
    w2htest("{pageintro}P{pageintro}\nT\n", "<div class=\"wikiPageIntro\"><p>P</p>\n</div><p>T</p>\n", macroFactories);
  }

  public void testPageCodeMacros1()
  {
    w2htest(
        "a\n{code}asdf{code}\ne\n",
        "<p>a</p>\n<div class=\"preformatted panel\" style=\"border-width: 1px;\"><div class=\"preformattedContent panelContent\">\n\n<span class=\"java_plain\">asdf</span><br />\n</div></div><p>e</p>\n",
        macroFactories);
  }

  public void testPageMacros1()
  {

    w2htest("{slide:title=asdf}\na\n\nb{slide}", "<h1>asdf</h1>\n<p>a</p>\n<p>b</p>\n", macroFactories);
  }

  public void testPageMacros2()
  {
    w2htest("{slide:title=asdf}\na\nb\n\nc\n{slide}", "<h1>asdf</h1>\n<p>a<br/>\nb</p>\n<p>c</p>\n", macroFactories);
  }

  public void testPageMacros4()
  {
    String t = "{slide:title=T}\n- a\n- b\n{slide}\n";
    w2htest(t, "<h1>T</h1>\n<ul class=\"minus\" type=\"square\"><li>a</li><li>b</li></ul>", macroFactories);
  }

}
