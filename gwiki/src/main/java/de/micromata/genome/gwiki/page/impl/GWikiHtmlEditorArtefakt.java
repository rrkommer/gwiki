//
// Copyright (C) 2010-2016 Roger Rene Kommer & Micromata GmbH
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//  http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.
//

package de.micromata.genome.gwiki.page.impl;

import de.micromata.genome.gwiki.controls.GWikiEditPageActionBean;
import de.micromata.genome.gwiki.model.GWikiAuthorizationRights;
import de.micromata.genome.gwiki.model.GWikiElement;
import de.micromata.genome.gwiki.page.GWikiContext;
import de.micromata.genome.util.xml.xmlbuilder.Xml;
import de.micromata.genome.util.xml.xmlbuilder.html.Html;

/**
 * Editor for a HTML artefakt.
 * 
 * @deprecated this is broken, becuase tinyMCE doesn't work for core html. Using GWikiPlainHtmlEditorArtefakt instead.
 * 
 * @author Roger Rene Kommer (r.kommer@micromata.de)
 * 
 */
@Deprecated
public class GWikiHtmlEditorArtefakt extends GWikiEditorArtefaktBase<String> implements GWikiEditorArtefakt<String>
{

  private static final long serialVersionUID = -2557631751225594754L;

  private GWikiHtmlArtefakt wikiPage;

  public GWikiHtmlEditorArtefakt(GWikiElement elementToEdit, GWikiEditPageActionBean editBean, String partName,
      GWikiHtmlArtefakt wikiPage)
  {
    super(elementToEdit, editBean, partName);
    this.wikiPage = wikiPage;
  }

  @Override
  public void onSave(GWikiContext ctx)
  {
    ctx.getWikiWeb().getAuthorization().ensureAllowTo(ctx, GWikiAuthorizationRights.GWIKI_EDITHTML.name());
    String text = ctx.getRequestParameter(partName + ".htmlText");
    wikiPage.setStorageData(text);

  }

  @Override
  public void prepareHeader(GWikiContext wikiContext)
  {
    super.prepareHeader(wikiContext);
    wikiContext.getRequiredJs().add("/static/tiny_mce/tiny_mce_src.js");
    // wikiContext.getRequiredJs().add("/static/gwiki/textarea-0.1.js");
    wikiContext.getRequiredJs().add("/static/gwiki/gwiki-link-dialog-0.3.js");
    wikiContext.getRequiredJs().add("/static/gwiki/gwikiedit-wikiops-0.4.js");
    wikiContext.getRequiredJs().add("/static/gwiki/gwikiedit-toolbar-0.3.js");
    wikiContext.getRequiredJs().add("/static/gwiki/gwiki-wikitextarea-0.3.js");
    wikiContext.getRequiredJs().add("/static/gwiki/gwikiedit-frame-0.4.js");
    wikiContext.getRequiredJs().add("/static/gwiki/gwikiedit-0.3.js");
    wikiContext.getRequiredJs().add("/static/gwiki/gwiki-htmledit-0.3.js");
    wikiContext.getRequiredJs().add("/static/tiny_mce/plugins/gwiki/editor_plugin_src.js");
    wikiContext.getRequiredJs().add("/static/tiny_mce/plugins/gwikieditorlevel/editor_plugin_src.js");
    wikiContext.getRequiredCss().add("/static/gwikiedit/gwikiedit.css");

  }

  @Override
  public boolean renderWithParts(GWikiContext ctx)
  {
    // GWikiEditPageActionBean editBean = (GWikiEditPageActionBean) ctx.getRequestAttribute("form");
    // String wordEdit = "";
    //
    // if (editBean != null && editBean.getElementToEdit() != null) {
    // if (false) {
    // String wdavUrl = ctx.localUrl("/worddav/" + editBean.getElementToEdit().getElementInfo().getId() + partName + ".doc");
    // String defaultUrl = ctx.localUrl("/index");
    // wordEdit =
    // "<a onclick=\"if (window.ActiveXObject){var ed; try{ed = new ActiveXObject('SharePoint.OpenDocuments.1');}catch(err){window.alert('Unable to create an ActiveX object to open the document. This is most likely because of the security settings for your browser.');return false;}if (ed){ed.EditDocument('"
    // + wdavUrl
    // +
    // "', 'Word.Document');return false;}else{window.alert('Cannot instantiate the required ActiveX control to open the document. This is most likely because you do not have Office installed or you have an older version of Office.');return false;}}else if (window.URLLauncher){var wdFile = new URLLauncher();wdFile.open('/confluence/plugins/servlet/editinword/~roger/Confluence-WikiTest.doc');}else if(window.InstallTrigger){if(window.confirm('A plugin is required to use this feature. Would you like to download it?')){InstallTrigger.install({'WebDAV Launcher': '/confluence/download/resources/com.atlassian.confluence.extra.officeconnector:viewfile/webdavloader.xpi'});}}else{window.alert('Internet Explorer or Firefox is required to use this feature');}return false;\" href=\""
    // + defaultUrl
    // + "#\">In Word bearbeiten</a>";
    //
    // // wordEdit = Html.a(
    // // Xml.attrs("href", ctx.localUrl("/worddav/" + editBean.getElementToEdit().getElementInfo().getId() + partName + ".doc")),
    // // Xml.text("Mit Word editieren")).toString();
    // }
    // }
    String textareaid = partName + ".htmlText";

    String html = //
        Html.textarea(Xml.attrs("rows", "40", "cols", "120", "name", partName + ".htmlText", "id", textareaid), //
            Xml.text(wikiPage.getStorageData())).toString();
    String script = "<script type=\"text/javascript\">\n" + "$(document).ready(function(){\n"
        + "setTimeout(function() {\n"
        // + "alert('now timeout');\n"
        + "gwikiCreateHtmlPageEditor('"
        + textareaid
        + "');\n"
        + " }, 50);\n"
        + "});\n"
        + "</script>";
    // String link = "<a href=\"javascript:gwikiCreateHtmlPageEditor('" + textareaid + "');\">Load HTML Editor</a>";
    ctx.append(html);
    ctx.append(script);
    // ctx.append(wordEdit);
    // ctx.append(link);
    return true;
  }

}
