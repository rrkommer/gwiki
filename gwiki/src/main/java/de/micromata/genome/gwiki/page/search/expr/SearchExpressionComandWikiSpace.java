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
import java.util.Collections;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import de.micromata.genome.gwiki.page.GWikiContext;
import de.micromata.genome.gwiki.page.search.SearchQuery;
import de.micromata.genome.gwiki.page.search.SearchResult;

/**
 * Search expression matches to WikiSpace.
 * 
 * @author Roger Rene Kommer (r.kommer@micromata.de)
 * 
 */
public class SearchExpressionComandWikiSpace extends SearchExpressionCommand
{
  public SearchExpressionComandWikiSpace(String command, SearchExpression nested)
  {
    super(command, nested);
  }

  public Collection<SearchResult> filter(GWikiContext ctx, SearchQuery query)
  {
    String text = ((SearchExpressionText) nested).getText();
    List<SearchResult> ret = new ArrayList<SearchResult>();
    for (SearchResult s : query.getResults()) {
      if (StringUtils.equals(s.getElementInfo().getWikiSpace(ctx), text) == true) {
        ret.add(s);
      }
    }
    return ret;
  }

  public List<String> getLookupWords()
  {
    return Collections.emptyList();
  }

}
