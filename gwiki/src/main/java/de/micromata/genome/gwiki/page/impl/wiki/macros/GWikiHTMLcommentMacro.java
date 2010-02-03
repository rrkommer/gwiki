/////////////////////////////////////////////////////////////////////////////
//
// Project   DHL-ParcelOnlinePostage
//
// Author    roger@micromata.de
// Created   09.01.2010
// Copyright Micromata 09.01.2010
//
/////////////////////////////////////////////////////////////////////////////
package de.micromata.genome.gwiki.page.impl.wiki.macros;

import org.apache.commons.lang.StringUtils;

import de.micromata.genome.gwiki.page.GWikiContext;
import de.micromata.genome.gwiki.page.impl.wiki.GWikiBodyMacro;
import de.micromata.genome.gwiki.page.impl.wiki.GWikiMacroBase;
import de.micromata.genome.gwiki.page.impl.wiki.GWikiRuntimeMacro;
import de.micromata.genome.gwiki.page.impl.wiki.MacroAttributes;

/**
 * Renders nothing (if default hidden is set) or a Html-Comment.
 * 
 * @author roger@micromata.de
 * 
 */
public class GWikiHTMLcommentMacro extends GWikiMacroBase implements GWikiBodyMacro, GWikiRuntimeMacro
{

  private static final long serialVersionUID = -5609535631029285273L;

  public boolean render(MacroAttributes attrs, GWikiContext ctx)
  {
    boolean hidden = StringUtils.equals(attrs.getDefaultValue(), "hidden");
    if (hidden == true) {
      return true;
    }
    ctx.append("<!-- ");
    ctx.append(attrs.getBody());
    ctx.append("--!>");
    return true;
  }

}
