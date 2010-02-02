/////////////////////////////////////////////////////////////////////////////
//
// Project   DHL-ParcelOnlinePostage
//
// Author    roger@micromata.de
// Created   22.12.2009
// Copyright Micromata 22.12.2009
//
/////////////////////////////////////////////////////////////////////////////
package de.micromata.genome.gwiki.page.impl.wiki2;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.micromata.genome.gwiki.page.impl.wiki.GWikiMacroClassFactory;
import de.micromata.genome.gwiki.page.impl.wiki.GWikiMacroFactory;
import de.micromata.genome.gwiki.page.impl.wiki.GWikiMacroFragment;
import de.micromata.genome.gwiki.page.impl.wiki.MacroAttributes;
import de.micromata.genome.gwiki.page.impl.wiki.fragment.GWikiFragment;
import de.micromata.genome.gwiki.page.impl.wiki.fragment.GWikiFragmentParseError;
import de.micromata.genome.gwiki.page.impl.wiki.fragment.GWikiFragmentText;
import de.micromata.genome.gwiki.page.impl.wiki.fragment.GWikiFragmentUnsecureHtml;
import de.micromata.genome.gwiki.page.impl.wiki.macros.GWikiHtmlBodyMacro;
import de.micromata.genome.gwiki.page.impl.wiki.macros.GWikiHtmlBodyTagMacro;
import de.micromata.genome.gwiki.page.impl.wiki.macros.GWikiHtmlTagMacro;
import de.micromata.genome.gwiki.page.impl.wiki.macros.GWikiTocMacro;

public class GWikiWikiParserMacroTest extends GWikiWikiParserTestBase
{
  public void testMacrosWithQuoting()
  {
    Map<String, GWikiMacroFactory> macroFactories = new HashMap<String, GWikiMacroFactory>();
    macroFactories.put("x", new GWikiMacroClassFactory(GWikiHtmlBodyTagMacro.class));
    List<GWikiFragment> frags = parseText("{x:a=\"}\"|b=\"x\"}a{x}", macroFactories);
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
    assertEquals(3, frags.size());
    assertTrue(frags.get(0) instanceof GWikiMacroFragment);
    assertTrue(frags.get(2) instanceof GWikiMacroFragment);
  }

  public void testNestedMacros2()
  {
    Map<String, GWikiMacroFactory> macroFactories = new HashMap<String, GWikiMacroFactory>();
    macroFactories.put("center", new GWikiMacroClassFactory(GWikiHtmlBodyTagMacro.class));
    macroFactories.put("span", new GWikiMacroClassFactory(GWikiHtmlBodyTagMacro.class));
    List<GWikiFragment> frags = parseText("{span:x=y}{span:x=z}asdf{span}{span}", macroFactories);
    assertEquals(1, frags.size());
    assertTrue(frags.get(0) instanceof GWikiMacroFragment);
    GWikiMacroFragment mf = (GWikiMacroFragment) frags.get(0);
    assertTrue(mf.getMacro() instanceof GWikiHtmlTagMacro);
    assertEquals(1, mf.getChilds().size());
    frags = mf.getChilds();
    assertTrue(frags.get(0) instanceof GWikiMacroFragment);
    assertEquals(1, mf.getChilds().size());
  }

  public void testNestedMacros()
  {
    Map<String, GWikiMacroFactory> macroFactories = new HashMap<String, GWikiMacroFactory>();
    macroFactories.put("center", new GWikiMacroClassFactory(GWikiHtmlBodyTagMacro.class));
    macroFactories.put("span", new GWikiMacroClassFactory(GWikiHtmlBodyTagMacro.class));
    List<GWikiFragment> frags = parseText("{center}{span}asdf{span}{center}", macroFactories);
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
    List<GWikiFragment> frags = parseText("{center}asdf{center}", "center", new GWikiMacroClassFactory(GWikiHtmlBodyTagMacro.class));
    assertEquals(1, frags.size());
    assertTrue(frags.get(0) instanceof GWikiMacroFragment);
    GWikiMacroFragment mf = (GWikiMacroFragment) frags.get(0);
    assertTrue(mf.getMacro() instanceof GWikiHtmlTagMacro);
    assertEquals(1, mf.getChilds().size());
    assertEquals("asdf", ((GWikiFragmentText) mf.getChilds().get(0)).getSource());
  }

  public void testEvalMacro2()
  {
    List<GWikiFragment> frags = parseText("{center}h1.Titel\nText{center}", "center", new GWikiMacroClassFactory(
        GWikiHtmlBodyTagMacro.class));
    assertEquals(1, frags.size());
    assertTrue(frags.get(0) instanceof GWikiMacroFragment);
    GWikiMacroFragment mf = (GWikiMacroFragment) frags.get(0);
    assertTrue(mf.getMacro() instanceof GWikiHtmlTagMacro);
    assertEquals(2, mf.getChilds().size());
    assertEquals("\nText", ((GWikiFragmentText) mf.getChilds().get(1)).getSource());
  }

  public void testPlainMacroHeadMissingTerm()
  {
    List<GWikiFragment> frags = parseText("{html asdf", "html", new GWikiMacroClassFactory(GWikiHtmlBodyMacro.class));
    assertEquals(1, frags.size());
    assertTrue(frags.get(0) instanceof GWikiFragmentText);
    assertEquals("{html asdf", ((GWikiFragmentText) frags.get(0)).getSource());
  }

  public void testPlainBodyWithoutEnd()
  {
    List<GWikiFragment> frags = parseText("{html}asdf", "html", new GWikiMacroClassFactory(GWikiHtmlBodyMacro.class));
    assertEquals(2, frags.size());
    assertTrue(frags.get(0) instanceof GWikiFragmentParseError);
  }

  public void testPlainEmptyBody()
  {
    List<GWikiFragment> frags = parseText("{html}{html}", "html", new GWikiMacroClassFactory(GWikiHtmlBodyMacro.class));
    assertEquals(1, frags.size());
    assertTrue(frags.get(0) instanceof GWikiMacroFragment);
    GWikiMacroFragment mf = (GWikiMacroFragment) frags.get(0);
    frags = mf.getChilds();
    assertTrue(frags.get(0) instanceof GWikiFragmentUnsecureHtml);
    GWikiFragmentUnsecureHtml l = (GWikiFragmentUnsecureHtml) frags.get(0);
    assertEquals("", l.getHtml());
  }

  public void testPlainBody()
  {
    List<GWikiFragment> frags = parseText("{html}[]{{html}", "html", new GWikiMacroClassFactory(GWikiHtmlBodyMacro.class));
    assertEquals(1, frags.size());
    assertTrue(frags.get(0) instanceof GWikiMacroFragment);
    frags = ((GWikiMacroFragment) frags.get(0)).getChilds();
    assertTrue(frags.get(0) instanceof GWikiFragmentUnsecureHtml);
    GWikiFragmentUnsecureHtml l = (GWikiFragmentUnsecureHtml) frags.get(0);
    assertEquals("[]{", l.getHtml());
  }

  public void testKeyValueArgs2()
  {
    List<GWikiFragment> frags = parseText("{toc:minLevel=1|maxLevel=2}", "toc", new GWikiMacroClassFactory(GWikiTocMacro.class));
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
    assertEquals(1, frags.size());
    assertTrue(frags.get(0) instanceof GWikiMacroFragment);
    GWikiMacroFragment l = (GWikiMacroFragment) frags.get(0);
    assertTrue(l.getMacro() instanceof GWikiTocMacro);
    assertEquals("1", l.getAttrs().getArgs().getStringValue("minLevel"));
  }

  public void testSimpleArgs()
  {
    List<GWikiFragment> frags = parseText("{toc:includeAll}", "toc", new GWikiMacroClassFactory(GWikiTocMacro.class));
    assertEquals(1, frags.size());
    assertTrue(frags.get(0) instanceof GWikiMacroFragment);
    GWikiMacroFragment l = (GWikiMacroFragment) frags.get(0);
    assertTrue(l.getMacro() instanceof GWikiTocMacro);
    assertEquals("includeAll", l.getAttrs().getArgs().getStringValue(MacroAttributes.DEFAULT_VALUE_KEY));
  }

  public void testSimpleMacro()
  {
    List<GWikiFragment> frags = parseText("{toc}", "toc", new GWikiMacroClassFactory(GWikiTocMacro.class));
    assertEquals(1, frags.size());
    assertTrue(frags.get(0) instanceof GWikiMacroFragment);
    GWikiMacroFragment l = (GWikiMacroFragment) frags.get(0);
    assertTrue(l.getMacro() instanceof GWikiTocMacro);
  }

}
