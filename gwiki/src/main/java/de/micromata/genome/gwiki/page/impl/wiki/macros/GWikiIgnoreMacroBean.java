/////////////////////////////////////////////////////////////////////////////
//
// Project   DHL-ParcelOnlinePostage
//
// Author    roger@micromata.de
// Created   21.11.2009
// Copyright Micromata 21.11.2009
//
/////////////////////////////////////////////////////////////////////////////
package de.micromata.genome.gwiki.page.impl.wiki.macros;

import de.micromata.genome.gwiki.page.GWikiContext;
import de.micromata.genome.gwiki.page.impl.wiki.GWikiMacroBean;
import de.micromata.genome.gwiki.page.impl.wiki.MacroAttributes;

/**
 * Just ignore the macros.
 * 
 * @author roger@micromata.de
 * 
 */
public class GWikiIgnoreMacroBean extends GWikiMacroBean
{

  private static final long serialVersionUID = -8799593853026031300L;

  @Override
  public boolean renderImpl(GWikiContext ctx, MacroAttributes attrs)
  {
    return true;
  }

}
