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
import de.micromata.genome.gwiki.model.AuthorizationFailedException;
import de.micromata.genome.gwiki.model.GWikiElement;
import de.micromata.genome.gwiki.page.GWikiContext;
import de.micromata.genome.gwiki.utils.ThrowableUtils;
import de.micromata.genome.util.xml.xmlbuilder.Xml;
import de.micromata.genome.util.xml.xmlbuilder.html.Html;

public class GWikiWikiPageEditorArtefakt extends GWikiTextPageEditorArtefakt
{

  private static final long serialVersionUID = -3208103086581392210L;

  private GWikiWikiPageArtefakt wikiPage;

  public GWikiWikiPageEditorArtefakt(GWikiElement elementToEdit, GWikiEditPageActionBean editBean, String partName,
      GWikiWikiPageArtefakt wikiPage)
  {
    super(elementToEdit, editBean, partName, wikiPage);
    this.wikiPage = wikiPage;
  }

  @Override
  public void prepareHeader(GWikiContext wikiContext)
  {
    super.prepareHeader(wikiContext);
    wikiContext.getRequiredJs().add("/static/tiny_mce/tiny_mce_src.js");
    wikiContext.getRequiredJs().add("/static/gwiki/textarea-0.1.js");
    wikiContext.getRequiredJs().add("/static/gwiki/gwiki-link-dialog-0.3.js");
    wikiContext.getRequiredJs().add("/static/gwiki/gwikiedit-wikiops-0.3.js");
    wikiContext.getRequiredJs().add("/static/gwiki/gwikiedit-toolbar-0.3.js");
    wikiContext.getRequiredJs().add("/static/gwiki/gwiki-wikitextarea-0.3.js");
    wikiContext.getRequiredJs().add("/static/gwiki/gwikiedit-frame-0.3.js");
    wikiContext.getRequiredJs().add("/static/gwiki/gwikiedit-0.3.js");
    wikiContext.getRequiredJs().add("/static/gwiki/gwiki-htmledit-0.3.js");
    wikiContext.getRequiredJs().add("/static/tiny_mce/plugins/gwiki/editor_plugin_src.js");
  }

  public boolean renderWithParts(GWikiContext ctx)
  {
    String thisPageId = null;
    if (editBean.isNewPage() == false) {
      thisPageId = editBean.getPageId();
    }
    String pn = partName;
    String html = //
    Html.textarea(
        Xml.attrs("id", "textarea" + partName, "class", "wikiEditorTextArea", "rows", "40", "cols", "100", "name", partName + ".wikiText", "style",
            "width:100%;height:100%"), // 
        Xml.text(textPage.getStorageData())).toString();
    String commands = // "<span class=\"mceEditor defaultSkin\">"
    "<table class=\"gwikiToolPanel\" cellspacing=\"0\" cellpadding=\"0\" width=\"100%\">"
        + "<tr>"
        + "<td style=\"position: relative;\" >"
        + "<table class=\"gwikiToolBar\" cellspacing=\"0\" cellpadding=\"0\" align=\"\" ><tr><td>"
        + "<td style=\"position: relative;\"><a title=\"Save\" class=\"mceButton mceButtonEnabled\" href=\"javascript:\" onmousedown=\"return false;\" onclick=\"return false;\">"
        + "<span class=\"gwikiToolbarIcon gwikiIcon_save\" onclick=\"gwikiEditSave()\"/>"
        + "</a></td>"
        + "<td style=\"position: relative;\"><a title=\"Cancel\" class=\"mceButton mceButtonEnabled\" href=\"javascript:\" onmousedown=\"return false;\" onclick=\"return false;\">"
        + "<span class=\"gwikiToolbarIcon gwikiIcon_cancel\" onclick=\"gwikiEditCancel()\"/>"
        + "</a></td>"
        + "<td style=\"position: relative;\"><a class=\"mceButton mceButtonEnabled\" title=\"Fullscreen\" href=\"javascript:\" onmousedown=\"return false;\" onclick=\"return false;\">"
        + "<span class=\"gwikiToolbarIcon gwikiIcon_fullscreen\" onclick=\"gwikiFullscreen('gwikiWikiEditorFrame')\"/>"
        + "</a></td>"
        + "</td><td style=\"position: relative;\">"
        + "<a class=\"mceButton mceButtonEnabled\" title=\"GWiki Help\" href=\"javascript:\" onmousedown=\"return false;\" onclick=\"return false;\">"
        + "<span class=\"gwikiToolbarIcon gwikiIcon_help\" onclick=\"gwikiHelp()\"/>"
        + "</a></td>"
        + "<td>&nbsp;</td>"
        + "</tr></table>"
        + "</td><td>&nbsp;</td></tr></table>";
    String tabs = "<div id=\"gwikiWikiEditorFrame"
        + pn
        + "\">"
        + commands
        + "<div id='gwikiwktabs"
        + pn
        + "'>"
        + "<ul><li><a href='#WikiEdit" + pn + "'><span>Wiki</span></a></li><li>"
        + "<a href='#WikiRte"
        + pn
        + "'><span>Rich Text</span></a></li><li><a href='#WikiPreview" + pn + "'><span>Preview</span></a></li></ul>"
        + "<div id='WikiEdit" + pn + "'>"
        + html
        + "</div>"
        + "<div id='WikiRte" + pn + "'></div>"
        + "<div id='WikiPreview" + pn + "' style=\"width: 100%; height: 100%; overflow: scroll;\">" // overflow: scroll;
        + "</div>"
        + "</div>"
        + "</div>";
    ctx.append(tabs);

    ctx.append("<script type=\"text/javascript\">\n", "jQuery(document).ready(function(){\n" 
        + " jQuery(\"#textarea" + pn + "\").gwikiedit({\n",
        "linkAutoCompleteUrl: '", ctx.localUrl("edit/PageSuggestions"), "', partName: '", partName, "' ");
    if (thisPageId != null) {
      ctx.append(", parentPageId: '", thisPageId, "'");
    }
    ctx.append("});\n" + "  gwikicreateEditTab('" + partName + "'); } );\n" + "</script>");
    return true;
  }

  public void onSave(GWikiContext ctx)
  {
    super.onSave(ctx);
    try {
      wikiPage.compileFragements(ctx);
      wikiPage.getCompiledObject().ensureRight(ctx);
    } catch (AuthorizationFailedException ex) {
      ctx.addSimpleValidationError(ex.getMessage());
    } catch (Exception ex) {
      String st = ThrowableUtils.getExceptionStacktraceForHtml(ex);
      ctx.addSimpleValidationError("Kann Wiki Seite nicht kompilieren: " + ex.getMessage() + "\n" + st);
    }

  }
}
