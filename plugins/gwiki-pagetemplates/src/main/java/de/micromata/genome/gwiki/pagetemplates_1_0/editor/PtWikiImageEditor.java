////////////////////////////////////////////////////////////////////////////
// 
// Copyright (C) 2010 Micromata GmbH
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
package de.micromata.genome.gwiki.pagetemplates_1_0.editor;

import org.apache.commons.lang.StringEscapeUtils;

import de.micromata.genome.gwiki.model.GWikiElement;
import de.micromata.genome.gwiki.page.GWikiContext;
import de.micromata.genome.gwiki.page.RenderModes;
import de.micromata.genome.gwiki.page.impl.wiki.parser.WikiParserUtils;

/**
 * @author Christian Claus (c.claus@micromata.de)
 */
public class PtWikiImageEditor extends PtWikiTextEditorBase
{

  private static final long serialVersionUID = 5901053792188232570L;

  public PtWikiImageEditor(GWikiElement element, String sectionName, String editor)
  {
    super(element, sectionName, editor);
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.micromata.genome.gwiki.model.GWikiArtefaktBase#renderWithParts(de.micromata.genome.gwiki.page.GWikiContext)
   */
  @Override
  public boolean renderWithParts(GWikiContext ctx)
  {
      String content = StringEscapeUtils.escapeHtml(getEditContent());
      String img = WikiParserUtils.wiki2html(content, RenderModes.combine(RenderModes.GlobalImageLinks, RenderModes.LocalImageLinks));
      
      ctx.append(img);
      
      final String discover = ctx.getTranslated("gwiki.editor.image.discover");
      
      ctx.append(discover);
      ctx.append("<input");
      renderAttr(ctx, "type", "file");
      renderAttr(ctx, "size", "50");
      renderAttr(ctx, "accept", "image/*");
      renderAttr(ctx, "style", "margin:10px 0 25px 10px;");
      ctx.append("/>");
    
    return true;
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.micromata.genome.gwiki.page.impl.GWikiEditorArtefakt#onSave(de.micromata.genome.gwiki.page.GWikiContext)
   */
  public void onSave(GWikiContext ctx)
  {
    // TODO: GWikiUploadAttachmentActionBean does all, we want. reuse! (next week) :)

  }

  /*
   * (non-Javadoc)
   * 
   * @see de.micromata.genome.gwiki.page.impl.GWikiEditorArtefakt#getTabTitle()
   */
  public String getTabTitle()
  {

    return "";
  }

}
