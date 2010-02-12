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
import de.micromata.genome.gwiki.model.GWikiTextArtefaktBase;
import de.micromata.genome.gwiki.page.GWikiContext;
import de.micromata.genome.util.xml.xmlbuilder.Xml;
import de.micromata.genome.util.xml.xmlbuilder.html.Html;

/**
 * Editor artefakt for editing gwiki text.
 * 
 * @author roger
 * 
 */
public class GWikiTextPageEditorArtefakt extends GWikiEditorArtefaktBase<String> implements GWikiEditorArtefakt<String>
{

  private static final long serialVersionUID = -1675589607133523091L;

  protected GWikiTextArtefaktBase< ? > textPage;

  public GWikiTextPageEditorArtefakt(GWikiElement elementToEdit, GWikiEditPageActionBean editBean, String partName,
      GWikiTextArtefaktBase< ? > textPage)
  {
    super(elementToEdit, editBean, partName);
    this.textPage = textPage;
  }

  public boolean renderWithParts(GWikiContext ctx)
  {
    String html = //
    Html.textarea(Xml.attrs("id", ctx.genHtmlId("gwikitextpe"), "rows", "40", "cols", "100", "name", partName + ".wikiText"), // 
        Xml.text(textPage.getStorageData())).toString();
    ctx.append(html);
    return true;
  }

  public void onSave(GWikiContext ctx)
  {
    String text = ctx.getRequestParameter(partName + ".wikiText");
    textPage.setStorageData(text);
  }

}
