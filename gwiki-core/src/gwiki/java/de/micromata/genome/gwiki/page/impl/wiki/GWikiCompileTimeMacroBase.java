/////////////////////////////////////////////////////////////////////////////
//
// Project   DHL-ParcelOnlinePostage
//
// Author    roger@micromata.de
// Created   02.02.2010
// Copyright Micromata 02.02.2010
//
/////////////////////////////////////////////////////////////////////////////
package de.micromata.genome.gwiki.page.impl.wiki;

import org.apache.commons.lang.StringUtils;

import de.micromata.genome.gwiki.page.GWikiContext;

public abstract class GWikiCompileTimeMacroBase extends GWikiMacroBase implements GWikiCompileTimeMacro
{

  private static final long serialVersionUID = -8053026294841163346L;

  public boolean render(MacroAttributes attrs, GWikiContext ctx)
  {
    if (attrs.getChildFragment() != null) {
      attrs.getChildFragment().render(ctx);
    } else if (StringUtils.isNotEmpty(attrs.getBody()) == true) {
      ctx.append(attrs.getBody());
    }
    return true;
  }

}
