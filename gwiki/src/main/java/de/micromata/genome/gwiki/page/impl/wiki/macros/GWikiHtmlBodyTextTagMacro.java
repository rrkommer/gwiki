/////////////////////////////////////////////////////////////////////////////
//
// Project   DHL-ParcelOnlinePostage
//
// Author    roger@micromata.de
// Created   06.01.2010
// Copyright Micromata 06.01.2010
//
/////////////////////////////////////////////////////////////////////////////
package de.micromata.genome.gwiki.page.impl.wiki.macros;

import de.micromata.genome.gwiki.page.impl.wiki.GWikiBodyMacro;
import de.micromata.genome.gwiki.page.impl.wiki.GWikiMacroRte;
import de.micromata.genome.gwiki.utils.html.Html2WikiTransformInfo;

/**
 * Html body tag, where body will not interpreted, but outputed as text.
 * 
 * @author Roger Rene Kommer (r.kommer@micromata.de)
 * 
 */
public class GWikiHtmlBodyTextTagMacro extends GWikiHtmlTagMacro implements GWikiBodyMacro, GWikiMacroRte
{

  private static final long serialVersionUID = 1251867687172353344L;

  public GWikiHtmlBodyTextTagMacro()
  {

  }

  public GWikiHtmlBodyTextTagMacro(int renderModes)
  {
    setRenderModes(renderModes);
  }

  public Html2WikiTransformInfo getTransformInfo()
  {
    // no transform needed
    return null;
  }
}
