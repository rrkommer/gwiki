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
import de.micromata.genome.util.xml.xmlbuilder.Xml;
import de.micromata.genome.util.xml.xmlbuilder.html.Html;

public class GWikiJspPageEditorArtefakt extends GWikiTextPageEditorArtefakt
{
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
  public boolean renderWithParts(GWikiContext ctx)
  {
    boolean withCodePress = false;
    if (withCodePress == false) {
      return super.renderWithParts(ctx);
    }
    String html = Html.textarea(
        Xml.attrs("id", ctx.genHtmlId("gwikijsppe"), "rows", "40", "cols", "120", "name", partName + ".wikiText", "class",
            "codepress html  linenumbers-off"), //
        Xml.text(jspPage.getStorageData())).toString();
    ctx.append(html);
    return true;
  }
}
