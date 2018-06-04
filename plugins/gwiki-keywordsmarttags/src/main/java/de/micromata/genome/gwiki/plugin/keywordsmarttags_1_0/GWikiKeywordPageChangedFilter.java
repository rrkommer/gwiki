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

package de.micromata.genome.gwiki.plugin.keywordsmarttags_1_0;

import org.apache.commons.lang3.StringUtils;

import de.micromata.genome.gwiki.model.GWikiPropKeys;
import de.micromata.genome.gwiki.model.filter.GWikiFilterChain;
import de.micromata.genome.gwiki.model.filter.GWikiPageChangedFilter;
import de.micromata.genome.gwiki.model.filter.GWikiPageChangedFilterEvent;
import de.micromata.genome.gwiki.page.impl.GWikiWikiPageArtefakt;

public class GWikiKeywordPageChangedFilter implements GWikiPageChangedFilter
{
  public Void filter(GWikiFilterChain<Void, GWikiPageChangedFilterEvent, GWikiPageChangedFilter> chain, GWikiPageChangedFilterEvent event)
  {
    GWikiKeywordLoadElementInfosFilter chf = GWikiKeywordLoadElementInfosFilter.getInstance();
    if (chf == null) {
      return chain.nextFilter(event);
    }

    if (event.getOldInfo() != null && event.getNewInfo() != null) {
      String oKeyWords = event.getOldInfo().getProps().getStringValue(GWikiPropKeys.KEYWORDS);
      String nKeyWords = event.getNewInfo().getProps().getStringValue(GWikiPropKeys.KEYWORDS);
      if (StringUtils.equals(oKeyWords, nKeyWords) == true) {
        return chain.nextFilter(event);
      }

    } else if (event.getOldInfo() != null) {
      String oKeyWords = event.getOldInfo().getProps().getStringValue(GWikiPropKeys.KEYWORDS);
      if (StringUtils.isEmpty(oKeyWords) == false) {
      } else {
        return chain.nextFilter(event);
      }
    } else if (event.getNewInfo() != null) {
      String nKeyWords = event.getNewInfo().getProps().getStringValue(GWikiPropKeys.KEYWORDS);
      if (StringUtils.isEmpty(nKeyWords) == false) {
      } else {
        return chain.nextFilter(event);
      }
    }
    chf.clearKeywords();
    event.getWikiWeb().getPageCache().clearCompiledFragments(GWikiWikiPageArtefakt.class);
    return chain.nextFilter(event);
  }
}
