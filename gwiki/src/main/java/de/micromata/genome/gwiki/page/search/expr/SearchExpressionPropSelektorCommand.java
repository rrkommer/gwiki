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

import java.util.Collection;
import java.util.List;

import de.micromata.genome.gwiki.model.GWikiPropKeys;
import de.micromata.genome.gwiki.page.GWikiContext;
import de.micromata.genome.gwiki.page.search.SearchQuery;
import de.micromata.genome.gwiki.page.search.SearchResult;

public class SearchExpressionPropSelektorCommand extends SearchExpressionCommand implements SearchExpressionFieldSelektor
{
  private String key;

  public SearchExpressionPropSelektorCommand(String command, SearchExpression nested)
  {
    super(command, nested);
    this.key = nested.getLookupWords().get(0);
  }

  public String getField(SearchResult sr)
  {
    if (GWikiPropKeys.PAGEID.equals(key) == true) {
      return sr.getElementInfo().getId();
    } else if (GWikiPropKeys.TYPE.equals(key) == true) {
      return sr.getElementInfo().getType();
    }
    return sr.getElementInfo().getProps().getStringValue(key);
  }

  public String getKey()
  {
    return key;
  }

  public void setKey(String key)
  {
    this.key = key;
  }

  public Collection<SearchResult> filter(GWikiContext ctx, SearchQuery query)
  {
    // TODO Auto-generated method stub
    return null;
  }

  public List<String> getLookupWords()
  {
    // TODO Auto-generated method stub
    return null;
  }

}
