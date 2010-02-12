/////////////////////////////////////////////////////////////////////////////
//
// Project   DHL-ParcelOnlinePostage
//
// Author    roger@micromata.de
// Created   24.10.2009
// Copyright Micromata 24.10.2009
//
/////////////////////////////////////////////////////////////////////////////
package de.micromata.genome.gwiki.page.impl;

import de.micromata.genome.gwiki.controls.GWikiEditPageActionBean;
import de.micromata.genome.gwiki.model.GWikiElement;
import de.micromata.genome.gwiki.model.GWikiExecutableArtefakt;
import de.micromata.genome.gwiki.model.GWikiTextArtefaktBase;
import de.micromata.genome.gwiki.page.GWikiContext;
import de.micromata.genome.gwiki.page.search.GWikiIndexedArtefakt;
import de.micromata.genome.gwiki.utils.AppendableI;

/**
 * Artefakt for HTML content.
 * 
 * @author roger@micromata.de
 * 
 */
public class GWikiHtmlArtefakt extends GWikiTextArtefaktBase<String> implements GWikiExecutableArtefakt<String>, GWikiIndexedArtefakt,
    GWikiEditableArtefakt
{
  private static final long serialVersionUID = 8904411739515655880L;

  public String getFileSuffix()
  {
    return ".html";
  }

  @Override
  public boolean renderWithParts(GWikiContext ctx)
  {
    ctx.append(getStorageData());
    return true;

  }

  public void getPreview(GWikiContext ctx, AppendableI sb)
  {
    sb.append(getStorageData());
  }

  public GWikiEditorArtefakt< ? > getEditor(GWikiElement elementToEdit, GWikiEditPageActionBean bean, String partKey)
  {
    return new GWikiHtmlEditorArtefakt(elementToEdit, bean, partKey, this);
  }

}
