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
package de.micromata.genome.gwiki.page.impl;

import java.util.Date;

import org.apache.commons.lang.StringUtils;

import de.micromata.genome.gwiki.controls.GWikiEditPageActionBean;
import de.micromata.genome.gwiki.model.GWikiElement;
import de.micromata.genome.gwiki.model.GWikiPropKeys;
import de.micromata.genome.gwiki.model.GWikiProps;
import de.micromata.genome.gwiki.model.GWikiTextArtefaktBase;
import de.micromata.genome.gwiki.page.GWikiContext;
import de.micromata.genome.util.xml.xmlbuilder.Xml;
import de.micromata.genome.util.xml.xmlbuilder.html.Html;

/**
 * @author Roger Rene Kommer (r.kommer@micromata.de)
 * 
 */
public class GWikiChangeCommentEditorArtefakt extends GWikiTextPageEditorArtefakt
{

  private static final long serialVersionUID = 2704758581225549461L;

  /**
   * @param elementToEdit
   * @param editBean
   * @param partName
   * @param textPage
   */
  public GWikiChangeCommentEditorArtefakt(GWikiElement elementToEdit, GWikiEditPageActionBean editBean, String partName,
      GWikiTextArtefaktBase< ? > textPage)
  {
    super(elementToEdit, editBean, partName, textPage);
  }

  @Override
  public void onSave(GWikiContext ctx)
  {
    String text = ctx.getRequestParameter(partName + ".wikiText");
    String oldText = textPage.getStorageData();
    String ntext;
    String uname = ctx.getWikiWeb().getAuthorization().getCurrentUserName(ctx);
    int prevVersion = elementToEdit.getElementInfo().getProps().getIntValue(GWikiPropKeys.VERSION, 0);
    if (StringUtils.isNotBlank(text) == true) {
      Date now = new Date();
      ntext = "{changecomment:modifiedBy="
          + uname
          + "|modifiedAt="
          + GWikiProps.date2string(now)
          + "|version="
          + (prevVersion + 1)
          + "}\n"
          + text
          + "\n{changecomment}\n"
          + StringUtils.defaultString(oldText);
    } else {
      ntext = oldText;
    }
    ntext = StringUtils.trimToNull(ntext);
    textPage.setStorageData(ntext);
  }

  @Override
  public boolean renderWithParts(GWikiContext ctx)
  {
    String html = //
    Html.div(
        Xml.attrs("style", "width:100%"),
        Html.p(Xml.text(ctx.getWikiWeb().getI18nProvider().translate(ctx, "gwiki.edit.EditPage.ChangeComment.intro.text")), //
            Html.br(), //
            Html.textarea(Xml.attrs("id", ctx.genHtmlId("gwikitextpe"), "style", "width:100%; padding:0", "rows", "5", "cols", "100",
                "name", partName + ".wikiText"), //
                Xml.text("")))).toString();
    ctx.append(html);
    return true;
  }

}
