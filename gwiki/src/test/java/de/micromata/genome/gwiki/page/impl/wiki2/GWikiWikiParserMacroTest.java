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

package de.micromata.genome.gwiki.page.impl.wiki2;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.micromata.genome.gwiki.page.GWikiStandaloneContext;
import de.micromata.genome.gwiki.page.impl.wiki.GWikiMacroClassFactory;
import de.micromata.genome.gwiki.page.impl.wiki.GWikiMacroFactory;
import de.micromata.genome.gwiki.page.impl.wiki.GWikiMacroFragment;
import de.micromata.genome.gwiki.page.impl.wiki.MacroAttributes;
import de.micromata.genome.gwiki.page.impl.wiki.fragment.GWikiFragment;
import de.micromata.genome.gwiki.page.impl.wiki.fragment.GWikiFragmentP;
import de.micromata.genome.gwiki.page.impl.wiki.fragment.GWikiFragmentParseError;
import de.micromata.genome.gwiki.page.impl.wiki.fragment.GWikiFragmentText;
import de.micromata.genome.gwiki.page.impl.wiki.fragment.GWikiFragmentUnsecureHtml;
import de.micromata.genome.gwiki.page.impl.wiki.macros.GWikiHtmlBodyMacro;
import de.micromata.genome.gwiki.page.impl.wiki.macros.GWikiHtmlBodyTagMacro;
import de.micromata.genome.gwiki.page.impl.wiki.macros.GWikiHtmlTagMacro;
import de.micromata.genome.gwiki.page.impl.wiki.macros.GWikiTocMacro;
import de.micromata.genome.gwiki.page.impl.wiki.macros.html.GWikiHtmlSpanMacro;

public class GWikiWikiParserMacroTest extends GWikiWikiParserTestBase
{
  @Override
  protected List<GWikiFragment> unwrapP(List<GWikiFragment> frags)
  {
    if (frags.get(0) instanceof GWikiFragmentP) {
      return ((GWikiFragmentP) frags.get(0)).getChilds();
    }
    return frags;
  }

  public void testPlainBodyWithoutEnd()
  {
    List<GWikiFragment> frags = parseText("{html}asdf", "html", new GWikiMacroClassFactory(GWikiHtmlBodyMacro.class));
    frags = unwrapP(frags);
    assertEquals(3, frags.size());
    assertTrue(frags.get(0) instanceof GWikiFragmentParseError);
  }

  public void testMacrosWithQuoting()
  {
    Map<String, GWikiMacroFactory> macroFactories = new HashMap<String, GWikiMacroFactory>();
    macroFactories.put("x", new GWikiMacroClassFactory(GWikiHtmlBodyTagMacro.class));
    List<GWikiFragment> frags = parseText("{x:a=\"}\"|b=\"x\"}a{x}", macroFactories);
    frags = unwrapP(frags);
    assertEquals(1, frags.size());
    assertTrue(frags.get(0) instanceof GWikiMacroFragment);
    GWikiMacroFragment macrofrag = (GWikiMacroFragment) frags.get(0);
    assertEquals("}", macrofrag.getAttrs().getArgs().getStringValue("a"));
    assertEquals("x", macrofrag.getAttrs().getArgs().getStringValue("b"));

  }

  public void testNestedMacros3()
  {
    Map<String, GWikiMacroFactory> macroFactories = new HashMap<String, GWikiMacroFactory>();
    macroFactories.put("x", new GWikiMacroClassFactory(GWikiHtmlBodyTagMacro.class));
    List<GWikiFragment> frags = parseText("{x}a{x}b{x}c{x}", macroFactories);
    frags = unwrapP(frags);
    assertEquals(3, frags.size());
    assertTrue(frags.get(0) instanceof GWikiMacroFragment);
    assertTrue(frags.get(2) instanceof GWikiMacroFragment);
  }

  public void testNestedMacros2()
  {
    Map<String, GWikiMacroFactory> macroFactories = new HashMap<String, GWikiMacroFactory>();
    macroFactories.put("span", new GWikiMacroClassFactory(GWikiHtmlBodyTagMacro.class));
    List<GWikiFragment> frags = parseText("{span:x=y}{span:x=z}asdf{span}{span}", macroFactories);
    frags = unwrapP(frags);
    assertEquals(1, frags.size());
    assertTrue(frags.get(0) instanceof GWikiMacroFragment);
    GWikiMacroFragment mf = (GWikiMacroFragment) frags.get(0);
    assertTrue(mf.getMacro() instanceof GWikiHtmlTagMacro);
    assertEquals(1, mf.getChilds().size());
    frags = mf.getChilds();
    assertTrue(frags.get(0) instanceof GWikiMacroFragment);
    assertEquals(1, mf.getChilds().size());
  }

  public void testEvalMacro()
  {
    List<GWikiFragment> frags = parseText("{span}asdf{span}", "span",
        new GWikiMacroClassFactory(GWikiHtmlSpanMacro.class));
    frags = unwrapP(frags);
    assertEquals(1, frags.size());
    assertTrue(frags.get(0) instanceof GWikiMacroFragment);
    GWikiMacroFragment mf = (GWikiMacroFragment) frags.get(0);
    assertTrue(mf.getMacro() instanceof GWikiHtmlTagMacro);
    assertEquals(1, mf.getChilds().size());
    assertEquals("asdf", ((GWikiFragmentText) mf.getChilds().get(0)).getSource());
  }

  public void testEvalMacro2()
  {
    List<GWikiFragment> frags = parseText("{span}h1.Titel\nText{span}", "span", new GWikiMacroClassFactory(
        GWikiHtmlSpanMacro.class));
    frags = unwrapP(frags);
    assertEquals(1, frags.size());
    assertTrue(frags.get(0) instanceof GWikiMacroFragment);
    GWikiMacroFragment mf = (GWikiMacroFragment) frags.get(0);
    assertTrue(mf.getMacro() instanceof GWikiHtmlTagMacro);
    assertEquals(2, mf.getChilds().size());
    assertEquals("Text", ((GWikiFragmentText) mf.getChilds().get(1)).getSource());
  }

  public void testPlainMacroHeadMissingTerm()
  {
    List<GWikiFragment> frags = parseText("{html asdf", "html", new GWikiMacroClassFactory(GWikiHtmlBodyMacro.class));
    frags = unwrapP(frags);
    assertEquals(1, frags.size());
    assertTrue(frags.get(0) instanceof GWikiFragmentText);
    assertEquals("{html asdf", ((GWikiFragmentText) frags.get(0)).getSource());
  }

  public void testPlainEmptyBody()
  {
    List<GWikiFragment> frags = parseText("{html}{html}", "html", new GWikiMacroClassFactory(GWikiHtmlBodyMacro.class));
    frags = unwrapP(frags);
    assertEquals(1, frags.size());
    assertTrue(frags.get(0) instanceof GWikiMacroFragment);
    GWikiMacroFragment mf = (GWikiMacroFragment) frags.get(0);
    frags = mf.getChilds();
    assertTrue(frags.get(0) instanceof GWikiFragmentUnsecureHtml);
    GWikiFragmentUnsecureHtml l = (GWikiFragmentUnsecureHtml) frags.get(0);
    assertEquals("", l.getHtml());
  }

  protected String renderToString(GWikiFragment frag)
  {
    GWikiStandaloneContext ctx = new GWikiStandaloneContext();
    frag.render(ctx);
    return ctx.getOutString();
  }

  public void testPlainBody()
  {
    List<GWikiFragment> frags = parseText("{html}[]{{html}", "html",
        new GWikiMacroClassFactory(GWikiHtmlBodyMacro.class));
    frags = unwrapP(frags);
    assertEquals(1, frags.size());
    GWikiMacroFragment mf = (GWikiMacroFragment) frags.get(0);
    String s = renderToString(mf);
    assertEquals("[]{", s);
  }

  public void testKeyValueArgs2()
  {
    List<GWikiFragment> frags = parseText("{toc:minLevel=1|maxLevel=2}", "toc",
        new GWikiMacroClassFactory(GWikiTocMacro.class));
    frags = unwrapP(frags);
    assertEquals(1, frags.size());
    assertTrue(frags.get(0) instanceof GWikiMacroFragment);
    GWikiMacroFragment l = (GWikiMacroFragment) frags.get(0);
    assertTrue(l.getMacro() instanceof GWikiTocMacro);
    assertEquals("1", l.getAttrs().getArgs().getStringValue("minLevel"));
    assertEquals("2", l.getAttrs().getArgs().getStringValue("maxLevel"));
  }

  public void testKeyValueArgs()
  {
    List<GWikiFragment> frags = parseText("{toc:minLevel=1}", "toc", new GWikiMacroClassFactory(GWikiTocMacro.class));
    frags = unwrapP(frags);
    assertEquals(1, frags.size());
    assertTrue(frags.get(0) instanceof GWikiMacroFragment);
    GWikiMacroFragment l = (GWikiMacroFragment) frags.get(0);
    assertTrue(l.getMacro() instanceof GWikiTocMacro);
    assertEquals("1", l.getAttrs().getArgs().getStringValue("minLevel"));
  }

  public void testSimpleArgs()
  {
    List<GWikiFragment> frags = parseText("{toc:includeAll}", "toc", new GWikiMacroClassFactory(GWikiTocMacro.class));
    frags = unwrapP(frags);
    assertEquals(1, frags.size());
    assertTrue(frags.get(0) instanceof GWikiMacroFragment);
    GWikiMacroFragment l = (GWikiMacroFragment) frags.get(0);
    assertTrue(l.getMacro() instanceof GWikiTocMacro);
    assertEquals("includeAll", l.getAttrs().getArgs().getStringValue(MacroAttributes.DEFAULT_VALUE_KEY));
  }

  public void testSimpleMacro()
  {
    List<GWikiFragment> frags = parseText("{toc}", "toc", new GWikiMacroClassFactory(GWikiTocMacro.class));
    frags = unwrapP(frags);
    assertEquals(1, frags.size());
    assertTrue(frags.get(0) instanceof GWikiMacroFragment);
    GWikiMacroFragment l = (GWikiMacroFragment) frags.get(0);
    assertTrue(l.getMacro() instanceof GWikiTocMacro);
  }

}
