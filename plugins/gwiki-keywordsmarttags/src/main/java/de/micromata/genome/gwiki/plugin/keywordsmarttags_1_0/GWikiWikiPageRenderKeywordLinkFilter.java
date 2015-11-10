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

package de.micromata.genome.gwiki.plugin.keywordsmarttags_1_0;

import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import de.micromata.genome.gwiki.model.GWikiElementInfo;
import de.micromata.genome.gwiki.model.filter.GWikiFilterChain;
import de.micromata.genome.gwiki.model.filter.GWikiWikiPageCompileFilter;
import de.micromata.genome.gwiki.model.filter.GWikiWikiPageCompileFilterEvent;
import de.micromata.genome.gwiki.page.RenderModes;
import de.micromata.genome.gwiki.page.impl.GWikiContent;
import de.micromata.genome.gwiki.page.impl.GWikiWikiPageBaseArtefakt;
import de.micromata.genome.util.types.Pair;

/**
 * 
 * @author Roger Rene Kommer (r.kommer@micromata.de)
 * 
 */
public class GWikiWikiPageRenderKeywordLinkFilter implements GWikiWikiPageCompileFilter
{
  public Void filter(GWikiFilterChain<Void, GWikiWikiPageCompileFilterEvent, GWikiWikiPageCompileFilter> chain,
      GWikiWikiPageCompileFilterEvent event)
  {
    chain.nextFilter(event);
    int renderMode = event.getWikiContext().getRenderMode();
    if (RenderModes.NoLinks.isSet(renderMode) == true
        || RenderModes.ForText.isSet(renderMode) == true
        || RenderModes.ForRichTextEdit.isSet(renderMode) == true) {
      return null;
    }
    GWikiWikiPageBaseArtefakt a = event.getWikiPageArtefakt();
    GWikiContent content = a.getCompiledObject();
    GWikiKeywordLoadElementInfosFilter fe = GWikiKeywordLoadElementInfosFilter.getInstance();
    if (fe != null && content != null) {
      if (event.getElement() == null) {
        return null;
      }
      String space = event.getElement().getElementInfo().getWikiSpace(event.getWikiContext());
      Map<String, Pair<Pattern, List<GWikiElementInfo>>> spaceKeyWords = fe.getKeywords(event.getWikiContext()).get(space);
      if (spaceKeyWords != null) {
        content.iterate(new KeyWordReplaceVisitor(spaceKeyWords));
      }
    }
    return null;
  }

}
