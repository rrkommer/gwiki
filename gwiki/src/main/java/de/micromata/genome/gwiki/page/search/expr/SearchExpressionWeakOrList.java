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

package de.micromata.genome.gwiki.page.search.expr;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import de.micromata.genome.gwiki.page.GWikiContext;
import de.micromata.genome.gwiki.page.search.SearchQuery;
import de.micromata.genome.gwiki.page.search.SearchResult;

public class SearchExpressionWeakOrList extends SearchExpressionList
{

  public SearchExpressionWeakOrList()
  {

  }

  public SearchExpressionWeakOrList(List<SearchExpression> list)
  {
    super(list);
  }

  public Collection<SearchResult> filter(GWikiContext ctx, SearchQuery query)
  {
    Map<String, SearchResult> ret = new TreeMap<String, SearchResult>();
    for (SearchExpression se : list) {
      Collection<SearchResult> src = se.filter(ctx, query);
      if (src == null) {
        return ret.values();
      }
      for (SearchResult sr : src) {
        SearchResult or = ret.get(sr.getPageId());
        if (or != null) {
          or.setRelevance(or.getRelevance() + sr.getRelevance() + 50);
        } else {
          ret.put(sr.getPageId(), sr);
        }
      }
    }
    return ret.values();
  }

  @Override
  public String toString()
  {
    StringBuilder sb = new StringBuilder();
    sb.append("orlist(");
    boolean first = true;
    for (SearchExpression se : list) {
      if (first == true) {
        first = false;
      } else {
        sb.append(", ");
      }
      sb.append(se.toString());
    }
    sb.append(")");
    return sb.toString();
  }
}
