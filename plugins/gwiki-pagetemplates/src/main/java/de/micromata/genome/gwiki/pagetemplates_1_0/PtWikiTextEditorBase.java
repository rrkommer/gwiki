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

import java.util.HashMap;
import java.util.Map;

import de.micromata.genome.gwiki.model.GWikiArtefakt;
import de.micromata.genome.gwiki.model.GWikiElement;
import de.micromata.genome.gwiki.page.GWikiContext;
import de.micromata.genome.gwiki.page.impl.GWikiWikiPageArtefakt;

/**
 * @author Roger Rene Kommer (r.kommer@micromata.de)
 * 
 */
public abstract class PtWikiTextEditorBase extends PtSectionEditorBase
{
  protected String wikiText;

  protected int startSec = -1;

  protected int endSec = -1;

  GWikiWikiPageArtefakt wikiArtefakt;

  /**
   * @param element
   * @param sectionName
   * @param editor
   */
  public PtWikiTextEditorBase(GWikiElement element, String sectionName, String editor)
  {
    super(element, sectionName, editor);
    extractContent();
  }

  public boolean render(final GWikiContext ctx)
  {
    renderWithParts(ctx);
    return true;
  }

  private GWikiWikiPageArtefakt getWiki()
  {
    Map<String, GWikiArtefakt< ? >> map = new HashMap<String, GWikiArtefakt< ? >>();
    element.collectParts(map);
    GWikiWikiPageArtefakt wiki = null;
    for (GWikiArtefakt< ? > art : map.values()) {
      if ((art instanceof GWikiWikiPageArtefakt) == true) {
        wiki = (GWikiWikiPageArtefakt) art;
        break;
      }
    }
    return wiki;
  }

  protected void updateSection(String newContent)
  {
    extractContent();
    if (endSec == -1) {
      return;
    }
    String nc = wikiText.substring(0, startSec) + newContent + wikiText.substring(endSec);
    wikiArtefakt.setStorageData(nc);
  }

  private void extractContent()
  {
    String start = "{pteditsection:name=" + sectionName;

    wikiArtefakt = getWiki();
    if (wikiArtefakt == null) {
      return;
    }
    wikiText = wikiArtefakt.getStorageData();
    int idx = wikiText.indexOf(start);
    if (idx == -1) {
      return;
    }
    idx += start.length();
    String sec = wikiText.substring(idx);
    int lidx = sec.indexOf('}');
    if (lidx == -1) {
      return;
    }
    startSec = idx + lidx + 1;
    sec = sec.substring(lidx);
    lidx = sec.indexOf("{pteditsection}");
    if (lidx == -1) {
      return;
    }
    endSec = startSec + lidx - 1;
    //
    // wiki.getCompiledObject().iterate(new GWikiSimpleFragmentVisitor() {
    //
    // public void begin(GWikiFragment fragment)
    // {
    // if ((fragment instanceof GWikiMacroFragment) == false) {
    // return;
    // }
    // GWikiMacroFragment mf = (GWikiMacroFragment) fragment;
    // if ((mf.getMacro() instanceof PtSectionMacroBean) == false) {
    // return;
    // }
    // PtSectionMacroBean secm = (PtSectionMacroBean) mf.getMacro();
    // if (mf.getAttrs().getChildFragment() == null) {
    // return;
    // }
    // StringBuilder sb = new StringBuilder();
    // mf.getAttrs().getChildFragment().getChildSouce(sb);
    // content = sb.toString();
    // }
    // });
  }

  protected String getEditContent()
  {
    if (endSec == -1) {
      return "";
    }
    return wikiText.substring(startSec, endSec);
  }

}