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

import de.micromata.genome.gwiki.controls.GWikiEditPageActionBean;
import de.micromata.genome.gwiki.model.GWikiElement;
import de.micromata.genome.gwiki.model.GWikiTextArtefaktBase;
import de.micromata.genome.gwiki.page.GWikiContext;
import de.micromata.genome.util.xml.xmlbuilder.Xml;
import de.micromata.genome.util.xml.xmlbuilder.html.Html;

/**
 * Editor artefakt for editing gwiki text.
 * 
 * @author Roger Rene Kommer (r.kommer@micromata.de)
 * 
 */
public class GWikiTextPageEditorArtefakt extends GWikiEditorArtefaktBase<String> implements GWikiEditorArtefakt<String>
{

  private static final long serialVersionUID = -1675589607133523091L;

  protected GWikiTextArtefaktBase<?> textPage;

  public GWikiTextPageEditorArtefakt(GWikiElement elementToEdit, GWikiEditPageActionBean editBean, String partName,
      GWikiTextArtefaktBase<?> textPage)
  {
    super(elementToEdit, editBean, partName);
    this.textPage = textPage;
  }

  @Override
  public boolean renderWithParts(GWikiContext ctx)
  {
    String html = //
        Html.textarea(
            Xml.attrs("id", ctx.genHtmlId("gwikitextpe"), "rows", "40", "cols", "100", "name", partName + ".wikiText"), // 
            Xml.text(textPage.getStorageData())).toString();
    ctx.append(html);
    return true;
  }

  @Override
  public void onSave(GWikiContext ctx)
  {
    String text = ctx.getRequestParameter(partName + ".wikiText");
    textPage.setStorageData(text);
  }

}
