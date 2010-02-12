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
import java.util.Collections;
import java.util.List;

import de.micromata.genome.gwiki.page.GWikiContext;
import de.micromata.genome.gwiki.page.search.SearchQuery;
import de.micromata.genome.gwiki.page.search.SearchResult;
import de.micromata.genome.gwiki.page.search.SearchTextExtractorBase;

/**
 * SearchExpression look at the pageId.
 * 
 * @author Roger Rene Kommer (r.kommer@micromata.de)
 * 
 */
public class SearchExpressionComandPageIdMatcher extends SearchExpressionCommand
{
  public SearchExpressionComandPageIdMatcher(String command, SearchExpression nested)
  {
    super(command, nested);
  }

  public Collection<SearchResult> filter(GWikiContext ctx, SearchQuery query)
  {
    // List<SearchResult> ret = new ArrayList<SearchResult>();
    SearchQuery nq = new SearchQuery(query);
    nq.setTextExtractor(new SearchTextExtractorBase(1) {

      public String getRawText(GWikiContext ctx, SearchQuery query, SearchResult sr)
      {
        return sr.getPageId();
      }
    });
    Collection<SearchResult> fsr = nested.filter(ctx, nq);
    return fsr;
  }

  public List<String> getLookupWords()
  {
    return Collections.emptyList();
  }

}
