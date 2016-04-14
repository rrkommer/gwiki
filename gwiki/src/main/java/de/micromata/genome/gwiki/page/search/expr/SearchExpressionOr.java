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

package de.micromata.genome.gwiki.page.search.expr;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import de.micromata.genome.gwiki.page.GWikiContext;
import de.micromata.genome.gwiki.page.search.SearchQuery;
import de.micromata.genome.gwiki.page.search.SearchResult;

public class SearchExpressionOr extends SearchExpressionBinary
{

  public SearchExpressionOr()
  {

  }

  public SearchExpressionOr(SearchExpression left, SearchExpression right)
  {
    super(left, right);
  }

  public Collection<SearchResult> filter(GWikiContext ctx, SearchQuery query)
  {
    List<SearchExpression> list = new ArrayList<SearchExpression>();
    list.add(left);
    list.add(right);
    Map<String, SearchResult> ret = new TreeMap<String, SearchResult>();
    for (SearchExpression se : list) {
      Collection<SearchResult> src = se.filter(ctx, query);
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
    return "or(" + left.toString() + ", " + right.toString() + ")";
  }
}
