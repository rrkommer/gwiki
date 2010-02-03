/////////////////////////////////////////////////////////////////////////////
//
// Project   DHL-ParcelOnlinePostage
//
// Author    roger@micromata.de
// Created   26.12.2009
// Copyright Micromata 26.12.2009
//
/////////////////////////////////////////////////////////////////////////////
package de.micromata.genome.gwiki.page.impl.wiki;

import org.apache.commons.lang.StringEscapeUtils;

import de.micromata.genome.gwiki.model.AuthorizationFailedException;
import de.micromata.genome.gwiki.page.GWikiContext;

public abstract class GWikiMacroBase implements GWikiMacro
{
  private int renderModes = 0;

  public boolean hasBody()
  {
    return this instanceof GWikiBodyMacro;
  }

  public boolean evalBody()
  {
    return this instanceof GWikiBodyEvalMacro;
  }

  public void ensureRight(MacroAttributes attrs, GWikiContext ctx) throws AuthorizationFailedException
  {

  }

  static public void renderErrorMessage(GWikiContext ctx, String message, MacroAttributes attrs)
  {
    ctx.append("<span style=\"color=red\">").append(StringEscapeUtils.escapeHtml(message)).append("</span>");
  }

  public int getRenderModes()
  {
    return renderModes;
  }

  public void setRenderModes(int renderModes)
  {
    this.renderModes = renderModes;
  }
}
