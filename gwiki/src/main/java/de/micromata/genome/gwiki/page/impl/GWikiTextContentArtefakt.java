/////////////////////////////////////////////////////////////////////////////
//
// Project   DHL-ParcelOnlinePostage
//
// Author    roger@micromata.de
// Created   27.10.2009
// Copyright Micromata 27.10.2009
//
/////////////////////////////////////////////////////////////////////////////
package de.micromata.genome.gwiki.page.impl;

import de.micromata.genome.gwiki.controls.GWikiEditPageActionBean;
import de.micromata.genome.gwiki.model.GWikiElement;
import de.micromata.genome.gwiki.model.GWikiTextArtefaktBase;
import de.micromata.genome.gwiki.page.GWikiContext;

public class GWikiTextContentArtefakt extends GWikiTextArtefaktBase<String> implements GWikiEditableArtefakt
// , GWikiIndexedArtefakt by default kein index
{

  public java.lang.String getFileSuffix()
  {
    return ".txt";
  }

  public GWikiEditorArtefakt getEditor(GWikiElement elementToEdit, GWikiEditPageActionBean bean, String partKey)
  {
    return new GWikiTextPageEditorArtefakt(elementToEdit, bean, partKey, this);

  }

  @Override
  public boolean renderWithParts(GWikiContext ctx)
  {
    return true;
  }

}
