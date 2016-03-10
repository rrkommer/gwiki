////////////////////////////////////////////////////////////////////////////
//
// Copyright (C) 2010-2013 Micromata GmbH / Roger Rene Kommer
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
// http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.
//
////////////////////////////////////////////////////////////////////////////

package de.micromata.genome.gwiki.page.impl;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.StringUtils;

import de.micromata.genome.gwiki.controls.GWikiEditPageActionBean;
import de.micromata.genome.gwiki.model.AuthorizationFailedException;
import de.micromata.genome.gwiki.model.GWikiElement;
import de.micromata.genome.gwiki.page.GWikiContext;
import de.micromata.genome.gwiki.page.impl.wiki.fragment.GWikiFragment;
import de.micromata.genome.gwiki.page.impl.wiki.fragment.GWikiFragmentParseError;
import de.micromata.genome.gwiki.page.impl.wiki.fragment.GWikiFragmentVisitor;
import de.micromata.genome.gwiki.page.impl.wiki.fragment.GWikiSimpleFragmentVisitor;
import de.micromata.genome.gwiki.page.impl.wiki.parser.WeditWikiUtils;
import de.micromata.genome.gwiki.utils.ThrowableUtils;
import de.micromata.genome.util.xml.xmlbuilder.Xml;
import de.micromata.genome.util.xml.xmlbuilder.html.Html;

/**
 * Editor artefakt for editing gwiki wiki text.
 * 
 * @author Roger Rene Kommer (r.kommer@micromata.de)
 * 
 */
public class GWikiWikiPageEditorArtefakt2 extends GWikiTextPageEditorArtefakt
{
  private static boolean useDivEditor = false;
  private static boolean useHtmlImageInserter = true;
  private static final long serialVersionUID = -3208103086581392210L;

  private GWikiWikiPageBaseArtefakt wikiPage;

  public GWikiWikiPageEditorArtefakt2(GWikiElement elementToEdit, GWikiEditPageActionBean editBean, String partName,
      GWikiWikiPageBaseArtefakt wikiPage)
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
    wikiContext.getRequiredJs().add("/static/gwiki/gwikiedit-wikiops-0.4.js");
    wikiContext.getRequiredJs().add("/static/gwiki/gwikiedit-toolbar-0.3.js");

    wikiContext.getRequiredJs().add("/static/gwiki/gwikiedit-wikiops-0.4.js");

    wikiContext.getRequiredJs().add("/static/wedit/weditclipboard.js");
    wikiContext.getRequiredJs().add("/static/wedit/weditfocus.js");
    wikiContext.getRequiredJs().add("/static/wedit/weditdnd.js");
    wikiContext.getRequiredJs().add("/static/wedit/weditdl.js");
    wikiContext.getRequiredJs().add("/static/wedit/weditops.js");
    wikiContext.getRequiredJs().add("/static/wedit/weditnewimagedlg.js");
    wikiContext.getRequiredJs().add("/static/wedit/weditautocomplete.js");
    wikiContext.getRequiredJs().add("/static/wedit/weditkeyhandler.js");
    wikiContext.getRequiredJs().add("/static/wedit/weditobj.js");
    wikiContext.getRequiredJs().add("/static/gwedit/gweditautocomplete.js");

    //    wikiContext.getRequiredJs().add("/static/gwiki/gwiki-wikitextarea-0.4.js");

    wikiContext.getRequiredJs().add("/static/gwiki/gwikiedit-frame-0.4.js");
    wikiContext.getRequiredJs().add("/static/gwiki/gwikiedit-0.3.js");
    wikiContext.getRequiredJs().add("/static/gwiki/gwiki-htmledit-0.3.js");
    wikiContext.getRequiredJs().add("/static/tiny_mce/plugins/gwiki/editor_plugin_src.js");
    wikiContext.getRequiredJs().add("/static/tiny_mce/plugins/gwikieditorlevel/editor_plugin_src.js");
    wikiContext.getRequiredCss().add("/static/gwikiedit/gwikiedit.css");
    wikiContext.getRequiredCss().add("/static/wedit/wautocmp.css");
  }

  @Override
  public boolean renderWithParts(GWikiContext ctx)
  {
    String thisPageId = null;
    if (editBean.isNewPage() == false) {
      thisPageId = editBean.getPageId();
    }
    String pn = partName;
    String html;

    String text = textPage.getStorageData();
    text = StringEscapeUtils.escapeXml(text);
    text = StringUtils.replace(text, "\n", "<br/>\n");
    // TODO RK partname is not used
    html = "";
    //html += Html.input("id", "inplaceautocomplete", "type", "text", "style", "display: none").toString();

    String weditoptions = "{ " +
        "linkAutoCompleteUrl: '" + ctx.localUrl("edit/PageSuggestions") + "',"
        + " partName: '" + partName + "' ,"
        + " autocompletegetitemshandler: gwedit_autocomplete_entries ";
    if (thisPageId != null) {
      weditoptions += ", parentPageId: '" + thisPageId + "'";
    }
    weditoptions += "}";

    html += Html.div(
        Xml.attrs("id", "weditordiv" + partName, "class", "wikiEditorTextArea", "contenteditable", "true",
            "style", "width:100%;height:100%"),
        Xml.code(WeditWikiUtils.wikiToWedit(text))).toString();
    html += Html.textarea(
        Xml.attrs("id", "textarea" + partName, "class", "wikiEditorTextArea", "name", partName + ".wikiText",
            "style", "display: none"), //
        Xml.text(textPage.getStorageData())).toString();
    String tabs = "<div id=\"gwikiWikiEditorFrame"
        + pn
        + "\" style=\"width: 100%; height: 100%\">"
        + "<div id='gwikiwktabs"
        + pn
        + "'>"
        + "<ul><li><a href='#WikiEdit"
        + pn
        + "'><span>Wiki</span></a></li><li>"
        + "<a href='#WikiRte"
        + pn
        + "'><span>Rich Text</span></a></li><li><a href='#WikiPreview"
        + pn
        + "'><span>Preview</span></a></li></ul>"
        + "<div id='WikiEdit"
        + pn
        + "'>"
        + html
        + "</div>"
        + "<div id='WikiRte"
        + pn
        + "'></div>"
        + "<div id='WikiPreview"
        + pn
        + "' style=\"width: 100%; height: 100%; overflow: scroll;\">" // overflow: scroll;
        + "</div>"
        + "</div>"
        + "</div>";
    ctx.append(tabs);

    ctx.append("<script type=\"text/javascript\">\n");
    ctx.append("jQuery(document).ready(function(){\n");
    ctx.append(" jQuery('#weditordiv"
        + pn
        + "').wedit(", weditoptions, ");\n");
    ctx.append(" gwikicreateEditTab('" + partName + "'); } );\n");
    ctx.append("saveHandlers.push(function(){\n")//
        .append("  gwikiRestoreFromRte(gwikiCurrentPart);\n ")//
        .append("  gwikiUnsetContentChanged();\n")//
        .append("});\n");

    ctx.append("</script>");
    return true;
  }

  @Override
  public void onSave(GWikiContext ctx)
  {
    super.onSave(ctx);
    try {
      wikiPage.compileFragements(ctx);
      wikiPage.getCompiledObject().ensureRight(ctx);
      final List<GWikiFragmentParseError> errors = new ArrayList<GWikiFragmentParseError>();
      GWikiFragmentVisitor findCompileErrorVisitor = new GWikiSimpleFragmentVisitor()
      {

        @Override
        public void begin(GWikiFragment fragment)
        {
          if (fragment instanceof GWikiFragmentParseError) {
            errors.add((GWikiFragmentParseError) fragment);
          }
        }
      };
      wikiPage.getCompiledObject().iterate(findCompileErrorVisitor);
      if (errors.isEmpty() == false) {
        StringBuilder sb = new StringBuilder();
        sb.append(ctx.getTranslated("gwiki.edit.EditPage.validate.syntax.error")).append(" \n");
        for (GWikiFragmentParseError e : errors) {
          sb.append(e.getText()).append(ctx.getTranslated("gwiki.edit.EditPage.validate.syntax.atline"));
          sb.append(" ").append(e.getLineNo()).append("\n");
        }
        ctx.addSimpleValidationError(sb.toString());

      }
    } catch (AuthorizationFailedException ex) {
      ctx.addSimpleValidationError(ex.getMessage());
    } catch (Exception ex) {
      String st = ThrowableUtils.getExceptionStacktraceForHtml(ex);
      ctx.addSimpleValidationError(
          ctx.getTranslated("gwiki.edit.EditPage.validate.compile.error") + " " + ex.getMessage() + "\n" + st);
    }

  }
}
