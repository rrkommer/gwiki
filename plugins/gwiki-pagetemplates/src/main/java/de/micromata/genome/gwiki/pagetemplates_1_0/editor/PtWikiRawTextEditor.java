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

import static de.micromata.genome.util.xml.xmlbuilder.Xml.attrs;
import static de.micromata.genome.util.xml.xmlbuilder.Xml.text;
import static de.micromata.genome.util.xml.xmlbuilder.html.Html.textarea;

import org.apache.commons.lang.StringEscapeUtils;

import de.micromata.genome.gwiki.model.GWikiElement;
import de.micromata.genome.gwiki.page.GWikiContext;
import de.micromata.genome.gwiki.utils.html.Html2WikiFilter;
import de.micromata.genome.util.xml.xmlbuilder.XmlElement;

/**
 * @author Roger Rene Kommer (r.kommer@micromata.de)
 */
public class PtWikiRawTextEditor extends PtWikiTextEditorBase
{

  private static final long serialVersionUID = 5901053792188232570L;

  private boolean allowWikiSyntax = false;

  public PtWikiRawTextEditor(GWikiElement element, String sectionName, String editor, String hint, boolean allowWikiSyntax)
  {
    super(element, sectionName, editor, hint);
    this.allowWikiSyntax = allowWikiSyntax;
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.micromata.genome.gwiki.model.GWikiArtefaktBase#renderWithParts(de.micromata.genome.gwiki.page.GWikiContext)
   */
  @Override
  public boolean renderWithParts(final GWikiContext ctx)
  {
    String content = StringEscapeUtils.escapeHtml(getEditContent());
    content = Html2WikiFilter.unescapeWiki(content);

    XmlElement textarea = textarea( //
        attrs("name", sectionName, //
            "cols", "120", //
            "rows", "40")) //
        // "onchange", "javascript:gwikiEditorContentChanged = true")) //
        .nest(text(content));

    ctx.append(textarea.toString());

    return true;
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.micromata.genome.gwiki.page.impl.GWikiEditorArtefakt#onSave(de.micromata.genome.gwiki.page.GWikiContext)
   */
  public void onSave(final GWikiContext ctx)
  {
    String newContent = ctx.getRequestParameter(sectionName);

    if (!allowWikiSyntax) {
      newContent = Html2WikiFilter.escapeWiki(newContent);
    }

    updateSection(ctx, newContent);

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

  /*
   * (non-Javadoc)
   * 
   * @see
   * de.micromata.genome.gwiki.pagetemplates_1_0.editor.GWikiSectionEditorArtefakt#onDelete(de.micromata.genome.gwiki.page.GWikiContext)
   */
  public void onDelete(GWikiContext ctx)
  {
    // TODO Auto-generated method stub

  }

}
