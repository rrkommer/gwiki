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

package de.micromata.genome.gwiki.plugin.forum_1_0;

import de.micromata.genome.gwiki.controls.GWikiEditPageActionBean;
import de.micromata.genome.gwiki.model.GWikiArtefakt;
import de.micromata.genome.gwiki.model.GWikiElement;
import de.micromata.genome.gwiki.page.impl.GWikiWikiPageArtefakt;
import de.micromata.genome.gwiki.page.impl.GWikiWikiPageEditorArtefakt;

/**
 * @author Roger Rene Kommer (r.kommer@micromata.de)
 * 
 */
public class GWikiEditPostActionBean extends GWikiEditPageActionBean
{
  protected boolean quoteParent = false;

  protected String quoteWiki(GWikiElement pel, String text)
  {
    return "{quote}\n" + text + "\n{quote}\n";
  }

  protected void initQuotedContent()
  {
    GWikiElement pel = wikiContext.getWikiWeb().findElement(parentPageId);
    if (pel == null) {
      return;
    }
    GWikiArtefakt< ? > wa = pel.getPart("MainPage");
    if ((wa instanceof GWikiWikiPageArtefakt) == false) {
      return;
    }
    GWikiWikiPageArtefakt pw = (GWikiWikiPageArtefakt) wa;

    GWikiWikiPageEditorArtefakt pa = (GWikiWikiPageEditorArtefakt) editors.get("MainPage");
    if (pa == null) {
      return;
    }
    GWikiWikiPageArtefakt tw = (GWikiWikiPageArtefakt) pa.getElementToEdit().getPart("MainPage");
    String qs = quoteWiki(pel, pw.getStorageData());
    pa.setCompiledObject(qs);
    tw.setStorageData(qs);

  }

  @Override
  public Object onInit()
  {
    // setHideParts("Settings");
    Object ret = super.onInit();
    initQuotedContent();
    return ret;
  }

  public boolean isQuoteParent()
  {
    return quoteParent;
  }

  public void setQuoteParent(boolean quoteParent)
  {
    this.quoteParent = quoteParent;
  }
}
