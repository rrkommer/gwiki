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
package de.micromata.genome.gwiki.pagetemplates_1_0;

import org.apache.commons.lang.StringEscapeUtils;

import de.micromata.genome.gwiki.model.GWikiElement;
import de.micromata.genome.gwiki.page.GWikiContext;

/**
 * @author Roger Rene Kommer (r.kommer@micromata.de)
 * 
 */
public class PtWikiRawTextEditor extends PtWikiTextEditorBase
{

  private static final long serialVersionUID = 5901053792188232570L;

  public PtWikiRawTextEditor(GWikiElement element, String sectionName, String editor)
  {
    super(element, sectionName, editor);
  }

  private void renderAttr(GWikiContext ctx, String key, String value)
  {
    ctx.append(" " + key + "=\"").append(StringEscapeUtils.escapeHtml(value)).append("\"");
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.micromata.genome.gwiki.model.GWikiArtefaktBase#renderWithParts(de.micromata.genome.gwiki.page.GWikiContext)
   */
  @Override
  public boolean renderWithParts(GWikiContext ctx)
  {
    ctx.append("<textarea name=\"" + sectionName + "\"");
    renderAttr(ctx, "name", sectionName);
    renderAttr(ctx, "cols", "120");
    renderAttr(ctx, "rows", "40");
    ctx.append(">" + StringEscapeUtils.escapeHtml(getEditContent()) + "</textarea>");
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
