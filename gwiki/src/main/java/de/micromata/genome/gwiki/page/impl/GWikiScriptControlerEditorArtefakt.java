/////////////////////////////////////////////////////////////////////////////
//
// Project   DHL-ParcelOnlinePostage
//
// Author    roger@micromata.de
// Created   26.10.2009
// Copyright Micromata 26.10.2009
//
/////////////////////////////////////////////////////////////////////////////
package de.micromata.genome.gwiki.page.impl;

import de.micromata.genome.gwiki.controls.GWikiEditPageActionBean;
import de.micromata.genome.gwiki.model.GWikiElement;
import de.micromata.genome.gwiki.page.GWikiContext;

/**
 * A editor artefakt for groovy controler action beans.
 * 
 * @author Roger Rene Kommer (r.kommer@micromata.de)
 * 
 */
public class GWikiScriptControlerEditorArtefakt extends GWikiCodePageEditorArtefakt
{
  private static final long serialVersionUID = -4234425256998011853L;

  private GWikiScriptControlerArtefakt groovyPage;

  public GWikiScriptControlerEditorArtefakt(GWikiElement elementToEdit, GWikiEditPageActionBean editBean, String partName,
      GWikiScriptControlerArtefakt textPage)
  {
    super(elementToEdit, editBean, partName, textPage);
    this.groovyPage = textPage;
  }

  @Override
  public void onSave(GWikiContext ctx)
  {
    super.onSave(ctx);
    groovyPage.setCompiledObject(null);
    if (ctx.hasValidationErrors() == true)
      return;
    try {
      groovyPage.getActionBeanClass(ctx);
    } catch (Throwable ex) {
      ctx.addSimpleValidationError("Failure to compile Groovy Action: " + GWikiJspPageEditorArtefakt.getCompileError(ex));
    }
  }

  @Override
  protected String getCodeType()
  {
    return "java";
  }
}