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
import de.micromata.genome.util.xml.xmlbuilder.Xml;
import de.micromata.genome.util.xml.xmlbuilder.html.Html;

public class GWikiScriptControlerEditorArtefakt extends GWikiTextPageEditorArtefakt
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
  public boolean renderWithParts(GWikiContext ctx)
  {
    String html;
    boolean withCodePress = false;
    boolean withEditArea = false;
    String texId = ctx.genHtmlId("gwikijsppe");
    if (withCodePress == true) {
      html = Html.textarea(
          Xml.attrs("id", texId, "rows", "40", "cols", "120", "name", partName + ".wikiText", "class", "codepress java linenumbers-on"), //
          Xml.text(groovyPage.getStorageData())).toString();
      
    } else if (withEditArea == true) {
      html = Html.textarea(Xml.attrs("id", texId, "rows", "40", "cols", "120", "name", partName + ".wikiText", "class", "tabindent"), //
          Xml.text(groovyPage.getStorageData())).toString();
      String sin = "<script language=\"javascript\" type=\"text/javascript\">\n"
          + "editAreaLoader.init({\n"
          + "id : \""
          + texId
          + "\"\n"
          + ",syntax: \"java\"\n"
          + ",start_highlight: true\n"
          + ",replace_tab_by_spaces: 2\n"
          + "});\n"
          + "</script>";
      html += sin;
    } else {
      html = Html.textarea(Xml.attrs("id", texId, "rows", "40", "cols", "120", "name", partName + ".wikiText", "class", "tabindent"), //
          Xml.text(groovyPage.getStorageData())).toString();
    }
    ctx.append(html);
    return true;
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
}