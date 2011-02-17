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

import de.micromata.genome.gwiki.model.GWikiElement;
import de.micromata.genome.gwiki.page.GWikiContext;

/**
 * @author Christian Claus (c.claus@micromata.de)
 */
public class PtWikiLinkEditor extends PtWikiTextEditorBase
{

  private static final long serialVersionUID = 5901053792188232570L;

  public PtWikiLinkEditor(GWikiElement element, String sectionName, String editor)
  {
    super(element, sectionName, editor);
  }
  
  @Override
  public void prepareHeader(GWikiContext wikiContext) 
  {
    super.prepareHeader(wikiContext);
    
    wikiContext.getRequiredJs().add("/static/js/jquery.jstree.js");
  }
  
  private void renderTableRow(GWikiContext ctx, String... cols) 
  {
    ctx.append("<tr>");
    
    for (String col : cols) 
    {
      ctx.append("<td>");
      ctx.append(col);
      ctx.append("</td>");
    }
    
    ctx.append("</tr>");
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.micromata.genome.gwiki.model.GWikiArtefaktBase#renderWithParts(de.micromata.genome.gwiki.page.GWikiContext)
   */
  @Override
  public boolean renderWithParts(GWikiContext ctx)
  {
    ctx.append("<p>" + ctx.getTranslated("gwiki.editor.hyperlink.headline") + "</p>");
    
    ctx.append("<table>");
    
    String input  = "<input name='external' style='width:400px' value='http://'/>";
    String choose = "<input name='' type='button' value='choose' />";
    
    renderTableRow(ctx, ctx.getTranslated("gwiki.editor.hyperlink.address"), input, choose);
    
    input = "<input name='nicename' width='200px'/>";
    
    renderTableRow(ctx, ctx.getTranslated("gwiki.editor.hyperlink.nicename"), input, "");
    
    ctx.append("</table>");
    
    return true;
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.micromata.genome.gwiki.page.impl.GWikiEditorArtefakt#onSave(de.micromata.genome.gwiki.page.GWikiContext)
   */
  public void onSave(GWikiContext ctx)
  {
    String newContent = ctx.getRequestParameter(sectionName);
    updateSection(newContent);
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
