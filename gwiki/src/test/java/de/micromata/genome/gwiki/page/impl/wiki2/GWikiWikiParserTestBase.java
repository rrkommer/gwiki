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

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import junit.framework.TestCase;
import de.micromata.genome.gwiki.page.GWikiStandaloneContext;
import de.micromata.genome.gwiki.page.impl.wiki.GWikiMacroFactory;
import de.micromata.genome.gwiki.page.impl.wiki.fragment.GWikiFragment;
import de.micromata.genome.gwiki.page.impl.wiki.parser.GWikiWikiParser;
import de.micromata.genome.gwiki.page.impl.wiki.parser.GWikiWikiParserContext;

public class GWikiWikiParserTestBase extends TestCase
{

  protected List<GWikiFragment> parseText(String wikiText, String macroName, GWikiMacroFactory macroFactorie)
  {
    Map<String, GWikiMacroFactory> macroFactories = new HashMap<String, GWikiMacroFactory>();
    macroFactories.put(macroName, macroFactorie);
    return parseText(wikiText, macroFactories);
  }

  protected List<GWikiFragment> parseText(String wikiText, Map<String, GWikiMacroFactory> macroFactories)
  {
    GWikiWikiParserContext ctx = new GWikiWikiParserContext();
    ctx.getMacroFactories().putAll(macroFactories);
    GWikiWikiParser parser = new GWikiWikiParser();
    parser.parseFrags(wikiText, ctx);
    List<GWikiFragment> frags = ctx.popFragList();
    return frags;
  }

  protected List<GWikiFragment> parseText(String wikiText)
  {
    Map<String, GWikiMacroFactory> macroFactories = Collections.emptyMap();
    return parseText(wikiText, macroFactories);
  }

  protected String wiki2html(String wikiText)
  {
    List<GWikiFragment> fr = parseText(wikiText);
    GWikiStandaloneContext ctx = new GWikiStandaloneContext();
    for (GWikiFragment f : fr) {
      f.render(ctx);
    }
    return ctx.getJspWriter().getString();
  }

  protected void w2htest(String wiki, String html)
  {
    String genHtml = wiki2html(wiki);
    if (html.equals(genHtml) == false) {
      System.out.println("exp:[" + html + "]\nrec:[" + genHtml + "]");
    }
    assertEquals(html, genHtml);
  }
}
