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

import de.micromata.genome.gwiki.controls.GWikiEditPageActionBean;
import de.micromata.genome.gwiki.model.GWikiElement;

/**
 * The artefakt
 * 
 * @author roger@micromata.de
 * 
 */
public interface GWikiEditableArtefakt
{
  // TODO hier auch noch Parent element uebergeben, so dass der Part ggf. auf Props zugreifen kann.

  GWikiEditorArtefakt< ? > getEditor(GWikiElement elementToEdit, GWikiEditPageActionBean editBean, String partKey);
}
