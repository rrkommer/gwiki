/////////////////////////////////////////////////////////////////////////////
//
// Project   DHL-ParcelOnlinePostage
//
// Author    roger@micromata.de
// Created   26.12.2009
// Copyright Micromata 26.12.2009
//
/////////////////////////////////////////////////////////////////////////////
package de.micromata.genome.gwiki.page.impl.wiki.macros;

import de.micromata.genome.gwiki.page.impl.wiki.GWikiBodyEvalMacro;
import de.micromata.genome.gwiki.page.impl.wiki.GWikiMacroClassFactory;
import de.micromata.genome.gwiki.page.impl.wiki.GWikiMacroFactory;
import de.micromata.genome.gwiki.page.impl.wiki.GWikiMacroRenderFlags;
import de.micromata.genome.gwiki.page.impl.wiki.GWikiMacroRte;
import de.micromata.genome.gwiki.utils.html.Html2WikiTransformInfo;

/**
 * HTML tag macro with a evaluated body.
 * 
 * @author Roger Rene Kommer (r.kommer@micromata.de)
 * 
 */
public class GWikiHtmlBodyTagMacro extends GWikiHtmlTagMacro implements GWikiBodyEvalMacro, GWikiMacroRte
{

  private static final long serialVersionUID = 9204139467653157793L;

  public static GWikiMacroFactory standardBody()
  {
    return new GWikiMacroClassFactory(GWikiHtmlBodyTagMacro.class, //
        GWikiMacroRenderFlags.combine(GWikiMacroRenderFlags.NewLineAfterStart, GWikiMacroRenderFlags.NewLineBeforeEnd,
            GWikiMacroRenderFlags.TrimTextContent));
  }

  public static GWikiMacroFactory table()
  {
    return standardBody();
  }

  public static GWikiMacroFactory tr()
  {
    return standardBody();
  }

  public static GWikiMacroFactory td()
  {
    return standardBody();
  }

  public static GWikiMacroFactory div()
  {
    return standardBody();
  }

  public GWikiHtmlBodyTagMacro()
  {

  }

  public GWikiHtmlBodyTagMacro(int renderModes)
  {
    setRenderModes(renderModes);
  }

  public Html2WikiTransformInfo getTransformInfo()
  {
    // no transform needed
    return null;
  }
}
