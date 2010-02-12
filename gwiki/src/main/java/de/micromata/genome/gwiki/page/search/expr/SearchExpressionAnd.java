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

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import de.micromata.genome.gwiki.page.GWikiContext;
import de.micromata.genome.gwiki.page.search.SearchQuery;
import de.micromata.genome.gwiki.page.search.SearchResult;

/**
 * Combine two SearchExpression with AND.
 * 
 * @author Roger Rene Kommer (r.kommer@micromata.de)
 * 
 */
public class SearchExpressionAnd extends SearchExpressionBinary
{

  public SearchExpressionAnd()
  {

  }

  public SearchExpressionAnd(SearchExpression left, SearchExpression right)
  {
    super(left, right);
  }

  public Collection<SearchResult> filter(GWikiContext ctx, SearchQuery query)
  {
    List<SearchExpression> list = new ArrayList<SearchExpression>();
    list.add(left);
    list.add(right);
    // Map<String, SearchResult> ret = new TreeMap<String, SearchResult>();

    Collection<SearchResult> src = left.filter(ctx, query);
    SearchQuery tq = new SearchQuery(query);
    tq.setResults(src);
    Collection<SearchResult> les = right.filter(ctx, tq);
    return les;
  }

  @Override
  public String toString()
  {
    return "and(" + left.toString() + ", " + right.toString() + ")";
  }

}
