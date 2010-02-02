/////////////////////////////////////////////////////////////////////////////
//
// Project   DHL-ParcelOnlinePostage
//
// Author    roger@micromata.de
// Created   25.12.2009
// Copyright Micromata 25.12.2009
//
/////////////////////////////////////////////////////////////////////////////
package de.micromata.genome.gwiki.page.impl.wiki.macros;

import java.util.ArrayList;
import java.util.Collection;

import de.micromata.genome.gwiki.page.GWikiContext;
import de.micromata.genome.gwiki.page.impl.wiki.GWikiBodyMacro;
import de.micromata.genome.gwiki.page.impl.wiki.GWikiCompileTimeMacro;
import de.micromata.genome.gwiki.page.impl.wiki.GWikiMacroBean;
import de.micromata.genome.gwiki.page.impl.wiki.GWikiMacroFragment;
import de.micromata.genome.gwiki.page.impl.wiki.MacroAttributes;
import de.micromata.genome.gwiki.page.impl.wiki.fragment.GWikiFragment;
import de.micromata.genome.gwiki.page.impl.wiki.fragment.GWikiFragmentText;
import de.micromata.genome.gwiki.page.impl.wiki.parser.GWikiWikiParserContext;
import de.micromata.genome.gwiki.page.impl.wiki.parser.GWikiWikiTokens;

public class GWikiNoFormatBodyMacro extends GWikiMacroBean implements GWikiBodyMacro, GWikiCompileTimeMacro
{
  private static final long serialVersionUID = 335691916315972801L;

  @Override
  public boolean renderImpl(GWikiContext ctx, MacroAttributes attrs)
  {
    // because compile time
    return false;
  }

  public Collection<GWikiFragment> getFragments(GWikiMacroFragment macroFrag, GWikiWikiTokens tks, GWikiWikiParserContext ctx)
  {
    Collection<GWikiFragment> frags = new ArrayList<GWikiFragment>();
    GWikiHtmlBodyTagMacro tagMacro = new GWikiHtmlBodyTagMacro();
    GWikiMacroFragment preFrag = new GWikiMacroFragment(tagMacro, new MacroAttributes("pre:style=border=1;"));
    preFrag.addChild(new GWikiFragmentText(macroFrag.getAttrs().getBody()));
    macroFrag.addChild(preFrag);
    frags.add(macroFrag);
    // frags.add(preFrag);
    return frags;
  }
}
