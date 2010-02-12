/////////////////////////////////////////////////////////////////////////////
//
// Project   DHL-ParcelOnlinePostage
//
// Author    roger@micromata.de
// Created   21.10.2009
// Copyright Micromata 21.10.2009
//
/////////////////////////////////////////////////////////////////////////////
package de.micromata.genome.gwiki.page.impl;

import java.io.Serializable;

import de.micromata.genome.gwiki.controls.GWikiEditPageActionBean;
import de.micromata.genome.gwiki.model.GWikiElement;
import de.micromata.genome.gwiki.model.GWikiExecutableArtefakt;
import de.micromata.genome.gwiki.model.GWikiTextArtefaktBase;
import de.micromata.genome.gwiki.page.GWikiContext;

/**
 * Artefact for a Jsp (GSPT) artefact.
 * 
 * @author Roger Rene Kommer (r.kommer@micromata.de)
 * 
 */
public class GWikiJspTemplateArtefakt extends GWikiTextArtefaktBase<Serializable> implements GWikiExecutableArtefakt<Serializable>,
    GWikiEditableArtefakt
{

  private static final long serialVersionUID = -699812098888363361L;

  public String getFileSuffix()
  {
    return ".gspt";
  }

  protected Serializable compile(GWikiContext ctx)
  {
    if (getCompiledObject() != null)
      return getCompiledObject();
    Serializable s = ctx.getWikiWeb().getJspProcessor().compile(ctx, getStorageData());
    setCompiledObject(s);
    return s;
  }

  public boolean renderWithParts(final GWikiContext ctx)
  {
    Serializable template = compile(ctx);
    ctx.getWikiWeb().getJspProcessor().renderTemplate(ctx, template);
    return true;
  }

  public GWikiEditorArtefakt getEditor(GWikiElement elementToEdit, GWikiEditPageActionBean bean, String partKey)
  {
    return new GWikiJspPageEditorArtefakt(elementToEdit, bean, partKey, this);
  }

}
