/////////////////////////////////////////////////////////////////////////////
//
// Project   DHL-ParcelOnlinePostage
//
// Author    roger@micromata.de
// Created   01.11.2009
// Copyright Micromata 01.11.2009
//
/////////////////////////////////////////////////////////////////////////////
package de.micromata.genome.gwiki.page.impl.wiki;

import de.micromata.genome.gwiki.model.AuthorizationFailedException;
import de.micromata.genome.gwiki.page.GWikiContext;

/**
 * Macros will be created for each instanceof of a macro node in the document.
 * 
 * TODO make runtime macro own interface.
 * 
 * @author roger@micromata.de
 * 
 */
public interface GWikiMacro
{
  public boolean hasBody();

  public boolean evalBody();

  public void ensureRight(MacroAttributes attrs, GWikiContext ctx) throws AuthorizationFailedException;

  /**
   * combination of GWikiMacroRenderFlags
   * 
   * @return
   */
  public int getRenderModes();

}
