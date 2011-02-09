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
package de.micromata.genome.gwiki.pagelifecycle_1_0.action;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.commons.collections15.CollectionUtils;
import org.apache.commons.collections15.Transformer;
import org.apache.commons.lang.StringUtils;

import de.micromata.genome.gwiki.controls.GWikiPageListActionBean;
import de.micromata.genome.gwiki.model.GWikiElementInfo;
import de.micromata.genome.gwiki.model.GWikiPropKeys;
import de.micromata.genome.gwiki.model.matcher.GWikiPageIdMatcher;
import de.micromata.genome.gwiki.page.search.QueryResult;
import de.micromata.genome.gwiki.page.search.SearchResult;
import de.micromata.genome.util.matcher.MatcherBase;
import de.micromata.genome.util.runtime.CallableX;

/**
 * @author Stefan Stuetzer (s.stuetzer@micromata.com)
 */
public class PlcDashboardAction extends GWikiPageListActionBean
{
  
  public PlcDashboardAction()
  {
    super();
    fixedFilterExpression = "prop:PAGEID like \"*\"";
  }
  
  /* (non-Javadoc)
   * @see de.micromata.genome.gwiki.controls.GWikiPageListActionBean#onFilter()
   */
  @Override
  public Object onFilter()
  {
    List<SearchResult> res = new ArrayList<SearchResult>();
    List<GWikiElementInfo> elems = wikiContext.runInTenantContext("_DRAFT", getWikiSelector(), new CallableX<List<GWikiElementInfo>, RuntimeException>() {

      public List<GWikiElementInfo> call() throws RuntimeException
      {
        List<GWikiElementInfo> pageInfos = wikiContext.getElementFinder().getPageInfos(new MatcherBase<GWikiElementInfo>() {
          private static final long serialVersionUID = -6020166500681070082L;
          
          public boolean match(GWikiElementInfo ei)
          {
            String tid = ei.getProps().getStringValue(GWikiPropKeys.TENANT_ID);
            return StringUtils.equals("_DRAFT", tid);
          }
        });
        return pageInfos;
      }
    });

    for (GWikiElementInfo pageInfo : elems) {
      res.add(new SearchResult(pageInfo));
    }
    
    QueryResult qr = new QueryResult(res, res.size());
    writeXmlResult(qr);
    
    return noForward();
  }

  /* (non-Javadoc)
   * @see de.micromata.genome.gwiki.controls.GWikiPageListActionBean#renderField(java.lang.String, de.micromata.genome.gwiki.model.GWikiElementInfo)
   */
  @Override
  public String renderField(String fieldName, GWikiElementInfo elementInfo)
  {
    return super.renderField(fieldName, elementInfo);
  }
}
