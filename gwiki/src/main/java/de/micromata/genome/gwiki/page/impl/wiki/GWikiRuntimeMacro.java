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

import java.io.Serializable;

import de.micromata.genome.gwiki.page.GWikiContext;

public interface GWikiRuntimeMacro extends GWikiMacro, Serializable
{
  public boolean render(MacroAttributes attrs, final GWikiContext ctx);

}
