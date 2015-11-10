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

package de.micromata.genome.gwiki.page.search.expr;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import de.micromata.genome.gwiki.page.GWikiContext;
import de.micromata.genome.gwiki.page.search.SearchQuery;
import de.micromata.genome.gwiki.page.search.SearchResult;
import de.micromata.genome.gwiki.page.search.SearchTextExtractor;

public class SearchExpressionTextContains extends SearchExpressionText implements SearchExpressionFieldSelektor
{

  public SearchExpressionTextContains(String text)
  {
    super(text);
  }

  public Collection<SearchResult> filter(GWikiContext ctx, SearchQuery query)
  {
    List<SearchResult> ret = new ArrayList<SearchResult>();
    SearchQuery sq = new SearchQuery(query);
    sq.setSearchExpression(text);
    if (query.getTextExtractor() == null) {
      for (SearchResult sr : query.getResults()) {
        SearchResult res = SearchUtils.findResult(ctx, sq, sr);
        if (res != null) {
          ret.add(res);
        }
      }
    } else {
      SearchTextExtractor tex = query.getTextExtractor();
      for (SearchResult sr : query.getResults()) {
        String comp = tex.getRawText(ctx, query, sr);
        if (comp == null) {
          continue;
        }
        if (StringUtils.containsIgnoreCase(comp, text) == true) {
          ret.add(sr);
        }
      }
    }
    return ret;
  }

  @Override
  public String toString()
  {
    return "contains(" + text + ")";
  }

  public String getField(SearchResult sr)
  {
    return text;
  }
}
