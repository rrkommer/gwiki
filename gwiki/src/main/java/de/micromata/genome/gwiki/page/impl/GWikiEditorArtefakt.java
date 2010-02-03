/////////////////////////////////////////////////////////////////////////////
//
// Project   DHL-ParcelOnlinePostage
//
// Author    roger@micromata.de
// Created   25.10.2009
// Copyright Micromata 25.10.2009
//
/////////////////////////////////////////////////////////////////////////////
package de.micromata.genome.gwiki.page.impl;

import java.io.Serializable;

import de.micromata.genome.gwiki.model.GWikiExecutableArtefakt;
import de.micromata.genome.gwiki.page.GWikiContext;

/**
 * Editor for a artefakt.
 * 
 * The render method should render a editor.
 * 
 * @author roger@micromata.de
 * 
 */
public interface GWikiEditorArtefakt<T extends Serializable> extends GWikiExecutableArtefakt<T>
{
  public void onSave(GWikiContext ctx);

  public String getTabTitle();
}
