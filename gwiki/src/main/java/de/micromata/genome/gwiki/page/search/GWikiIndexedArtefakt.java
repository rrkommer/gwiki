/////////////////////////////////////////////////////////////////////////////
//
// Project   DHL-ParcelOnlinePostage
//
// Author    roger@micromata.de
// Created   24.10.2009
// Copyright Micromata 24.10.2009
//
/////////////////////////////////////////////////////////////////////////////
package de.micromata.genome.gwiki.page.search;

import de.micromata.genome.gwiki.page.GWikiContext;
import de.micromata.genome.gwiki.utils.AppendableI;

/**
 * A GWikiArtefakt which provides index information should implement this.
 * 
 * @author roger@micromata.de
 * 
 */
public interface GWikiIndexedArtefakt
{
  void getPreview(GWikiContext ctx, AppendableI sb);
}
