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

package de.micromata.genome.gwiki.pagetemplates_1_0.editor;

import de.micromata.genome.gwiki.model.GWikiElement;
import de.micromata.genome.gwiki.page.GWikiContext;
import de.micromata.genome.gwiki.page.GWikiStandaloneContext;
import de.micromata.genome.gwiki.page.RenderModes;
import de.micromata.genome.gwiki.page.impl.GWikiContent;
import de.micromata.genome.gwiki.page.impl.wiki.parser.GWikiWikiParser;
import de.micromata.genome.gwiki.utils.html.Html2WikiFilter;
import de.micromata.genome.util.xml.xmlbuilder.Xml;
import de.micromata.genome.util.xml.xmlbuilder.html.Html;

/**
 * @author Christian Claus
 * 
 */
public class PtWikiRichTextEditor extends PtWikiTextEditorBase // implements GWikiEditorArtefakt
{

  private static final long serialVersionUID = 4699544884150199528L;

  @Override
  public void prepareHeader(final GWikiContext wikiContext)
  {
    super.prepareHeader(wikiContext);

    wikiContext.getRequiredJs().add("/static/gwiki/textarea-0.1.js");
    wikiContext.getRequiredJs().add("/static/gwiki/gwiki-wikitextarea-1.2.js");
    wikiContext.getRequiredJs().add("/static/tinymce/tinymce.js");
    wikiContext.getRequiredJs().add("/static/wedit/tweditutils.js");
    wikiContext.getRequiredJs().add("/static/wedit/tweditac.js");
    wikiContext.getRequiredJs().add("/static/wedit/ClipData.js");
    wikiContext.getRequiredJs().add("/static/wedit/twedit-attach-dialog.js");
    wikiContext.getRequiredJs().add("/static/wedit/tweditclipboard.js");
    wikiContext.getRequiredJs().add("/static/wedit/twedit-link-manip.js");
    wikiContext.getRequiredJs().add("/static/wedit/twedit.js");
    wikiContext.getRequiredJs().add("/static/wedit/tweditplugin.js");
    wikiContext.getRequiredJs().add("/static/wedit/weditexttoolbarplugin.js");

    wikiContext.getRequiredJs().add("/static/wedit/gwiki-link-dialog.js");
    wikiContext.getRequiredJs().add("/static/wedit/twedittabs.js");
    wikiContext.getRequiredJs().add("/static/wedit/gweditautocomplete.js");
    wikiContext.getRequiredJs().add("/static/wedit/tweditmacrodlg.js");
    wikiContext.getRequiredJs().add("/static/gwiki/gwikiedit-0.4.js");
    wikiContext.getRequiredCss().add("/static/gwikiedit/gwikiedit.css");
  }

  /**
   * @param element
   * @param sectionName
   * @param editor
   * @param hint
   */
  public PtWikiRichTextEditor(GWikiElement element, String sectionName, String editor, String hint)
  {
    super(element, sectionName, editor, hint);
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.micromata.genome.gwiki.page.impl.GWikiEditorArtefakt#onSave(de.micromata.genome.gwiki.page.GWikiContext)
   */
  @Override
  public void onSave(final GWikiContext ctx)
  {
    String htmlCode = ctx.getRequestParameter(sectionName + ".htmlText");
    String result = Html2WikiFilter.html2Wiki(htmlCode);

    updateSection(ctx, result);
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.micromata.genome.gwiki.page.impl.GWikiEditorArtefakt#getTabTitle()
   */
  @Override
  public String getTabTitle()
  {
    return "";
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.micromata.genome.gwiki.model.GWikiArtefaktBase#renderWithParts(de.micromata.genome.gwiki.page.GWikiContext)
   */
  @Override
  public boolean renderWithParts(final GWikiContext ctx)
  {
    String textareaId = sectionName + ".htmlText";

    GWikiWikiParser wkparse = new GWikiWikiParser();
    GWikiContent content = wkparse.parse(ctx, getEditContent());
    GWikiStandaloneContext sctx = GWikiStandaloneContext.create();
    sctx.setRenderMode(RenderModes.combine(RenderModes.ForRichTextEdit, RenderModes.InMem));
    content.render(sctx);
    sctx.flush();

    String result = sctx.getOutString();

    String html = Html.textarea( //
        Xml.attrs("rows", "40", //
            "cols", "120", //
            "name", textareaId, //
            "id", textareaId), //
        Xml.text(result)).toString();

    String script = "<script type=\"text/javascript\">\n"
        + "$(document).ready(function(){\n"
        + "setTimeout(function() {\n"
        + "twedit_create('"
        + textareaId
        + "');\n"
        + " }, 50);\n"
        + "});\n"
        + "</script>";

    ctx.append(html);
    ctx.append(script);

    return true;
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * de.micromata.genome.gwiki.pagetemplates_1_0.editor.GWikiSectionEditorArtefakt#onDelete(de.micromata.genome.gwiki.
   * page.GWikiContext)
   */
  @Override
  public void onDelete(GWikiContext ctx)
  {
    // TODO Auto-generated method stub

  }

}
