package de.micromata.genome.gwiki.page.impl;

import de.micromata.genome.gwiki.controls.GWikiEditPageActionBean;
import de.micromata.genome.gwiki.model.GWikiElement;
import de.micromata.genome.gwiki.model.GWikiTextArtefaktBase;
import de.micromata.genome.gwiki.page.GWikiContext;
import de.micromata.genome.util.xml.xmlbuilder.Xml;
import de.micromata.genome.util.xml.xmlbuilder.html.Html;

/**
 * Base editor artefact for editing code.
 * 
 * @author roger
 * 
 */
public abstract class GWikiCodePageEditorArtefakt extends GWikiTextPageEditorArtefakt
{

  private static final long serialVersionUID = 1489327680460828556L;

  public GWikiCodePageEditorArtefakt(GWikiElement elementToEdit, GWikiEditPageActionBean editBean, String partName,
      GWikiTextArtefaktBase< ? > textPage)
  {
    super(elementToEdit, editBean, partName, textPage);
  }

  protected abstract String getCodeType();

  protected String getCode()
  {
    return textPage.getStorageData();
  }

  @Override
  public boolean renderWithParts(GWikiContext ctx)
  {
    String textId = ctx.genHtmlId("gwikijsppe");

    String html = Html.textarea(Xml.attrs("id", textId, "rows", "40", "cols", "120", //
        "name", partName + ".wikiText", //
        "class", "tabindent", //
        "style", "min-height:500px;height: 500px"), //
        Xml.text(getCode())).toString();
    String sin = "<script language=\"javascript\" type=\"text/javascript\">\n" + "editAreaLoader.init({\n" + "id : \""
        + textId
        + "\"\n"
        + ",syntax: \""
        + getCodeType()
        + "\"\n"
        + ",start_highlight: true\n"
        + ",replace_tab_by_spaces: 2\n"
        + "});\n"
        + "saveHandlers.push(function(){ "
        + "editAreaLoader.toggle('"
        + textId
        + "');});\n"
        + "function "
        + partName
        + "Activate(partName){\n"
        + "gwikiActivateCodeEditor('"
        + textId
        + "', partName, '"
        + getCodeType()
        + "');\n"
        + "}\n"
        + "function "
        + partName
        + "Deactivate(partName){\n"
        + "gwikiDeActivateCodeEditor('"
        + textId
        + "', partName);\n"
        + "}\n"
        + "</script>\n";

    html += sin;
    ctx.append(html);

    return true;
  }

  @Override
  public void prepareHeader(GWikiContext wikiContext)
  {
    super.prepareHeader(wikiContext);
    wikiContext.getRequiredJs().add("/static/edit_area/edit_area_full.js");
    wikiContext.getRequiredJs().add("/static/gwiki/gwiki-codeedit-0.3.js");
  }

}
