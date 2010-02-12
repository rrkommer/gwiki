/////////////////////////////////////////////////////////////////////////////
//
// Project   DHL-ParcelOnlinePostage
//
// Author    roger@micromata.de
// Created   23.12.2009
// Copyright Micromata 23.12.2009
//
/////////////////////////////////////////////////////////////////////////////
package de.micromata.genome.gwiki.page.impl.wiki.macros;

import org.apache.commons.lang.StringEscapeUtils;

import de.micromata.genome.gwiki.model.AuthorizationFailedException;
import de.micromata.genome.gwiki.page.GWikiContext;
import de.micromata.genome.gwiki.page.impl.wiki.GWikiRuntimeMacro;
import de.micromata.genome.gwiki.page.impl.wiki.MacroAttributes;

/**
 * Macro place holder for unknown macro.
 * 
 * @author Roger Rene Kommer (r.kommer@micromata.de)
 * 
 */
public class GWikiMacroUnknown implements GWikiRuntimeMacro
{

  private static final long serialVersionUID = -1990609591192712242L;

  private String message;

  public GWikiMacroUnknown()
  {
  }

  public GWikiMacroUnknown(String message)
  {
    this.message = message;
  }

  public void ensureRight(MacroAttributes attrs, GWikiContext ctx) throws AuthorizationFailedException
  {
  }

  public boolean evalBody()
  {
    return false;
  }

  public boolean hasBody()
  {
    return false;
  }

  public boolean render(MacroAttributes attrs, GWikiContext ctx)
  {
    String msg = message;
    if (msg == null) {
      msg = "Unkown macro: " + StringEscapeUtils.escapeHtml(attrs.getCmd());
    }
    ctx.append("<font color=\"red\">").append(StringEscapeUtils.escapeHtml(msg)).append("</font>");
    return true;
  }

  public int getRenderModes()
  {
    return 0;
  }

}
