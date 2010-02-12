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

import java.io.PrintWriter;
import java.io.StringWriter;

import de.micromata.genome.gwiki.controls.GWikiEditPageActionBean;
import de.micromata.genome.gwiki.model.GWikiElement;
import de.micromata.genome.gwiki.page.GWikiContext;

/**
 * Editor for a GSPT/Jsp page.
 * 
 * @author roger
 * 
 */
public class GWikiJspPageEditorArtefakt extends GWikiCodePageEditorArtefakt
{

  private static final long serialVersionUID = 433557872072235804L;

  private GWikiJspTemplateArtefakt jspPage;

  public GWikiJspPageEditorArtefakt(GWikiElement elementToEdit, GWikiEditPageActionBean editBean, String partName,
      GWikiJspTemplateArtefakt textPage)
  {
    super(elementToEdit, editBean, partName, textPage);
    this.jspPage = textPage;
  }

  public static String getCompileError(Throwable ex)
  {
    StringWriter sout = new StringWriter();
    sout.append(ex.getMessage() + "\n");
    PrintWriter pout = new PrintWriter(sout);
    ex.printStackTrace(pout);
    return sout.getBuffer().toString();
  }

  @Override
  public void onSave(GWikiContext ctx)
  {
    super.onSave(ctx);
    jspPage.setCompiledObject(null);
    if (ctx.hasValidationErrors() == true)
      return;
    try {
      jspPage.compile(ctx);
    } catch (Throwable ex) {
      ctx.addSimpleValidationError("Failure to compile JSP: " + getCompileError(ex));
    }
  }

  @Override
  protected String getCodeType()
  {
    return "html";
  }

}
