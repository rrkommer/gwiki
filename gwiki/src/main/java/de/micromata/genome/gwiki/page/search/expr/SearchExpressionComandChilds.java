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

import org.apache.commons.lang3.StringUtils;

import de.micromata.genome.gwiki.model.GWikiElementInfo;
import de.micromata.genome.gwiki.page.GWikiContext;
import de.micromata.genome.gwiki.page.search.SearchQuery;
import de.micromata.genome.gwiki.page.search.SearchResult;

/**
 * A command with a SearchExpression.
 * 
 * @author Roger Rene Kommer (r.kommer@micromata.de)
 * 
 */
public class SearchExpressionComandChilds extends SearchExpressionCommand
{
  public SearchExpressionComandChilds(String command, SearchExpression nested)
  {
    super(command, nested);
  }

  protected boolean isChildOf(GWikiContext ctx, GWikiElementInfo parent, GWikiElementInfo child)
  {
    if (StringUtils.equals(child.getParentId(), parent.getId()) == true) {
      return true;
    }
    if (StringUtils.isEmpty(child.getParentId()) == true) {
      return false;
    }
    GWikiElementInfo el = ctx.getWikiWeb().findElementInfo(child.getParentId());
    if (el == null)
      return false;
    return isChildOf(ctx, parent, el);
  }

  public Collection<SearchResult> filter(GWikiContext ctx, SearchQuery query)
  {
    String text = ((SearchExpressionText) nested).getText();
    List<SearchResult> ret = new ArrayList<SearchResult>();
    GWikiElementInfo el = ctx.getWikiWeb().findElementInfo(text);
    if (el == null) {
      ctx.addSimpleValidationError("Select page id is not valid: " + text);
      return ret;
    }
    for (SearchResult s : query.getResults()) {
      if (isChildOf(ctx, el, s.getElementInfo()) == true) {
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
