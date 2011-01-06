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

package de.micromata.genome.gwiki.controls;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import de.micromata.genome.gwiki.model.GWikiElementInfo;
import de.micromata.genome.gwiki.page.impl.actionbean.ActionBeanBase;
import de.micromata.genome.gwiki.page.search.QueryResult;
import de.micromata.genome.gwiki.page.search.SearchQuery;
import de.micromata.genome.gwiki.page.search.SearchResult;
import de.micromata.genome.gwiki.utils.WebUtils;
import de.micromata.genome.util.matcher.InvalidMatcherGrammar;

/**
 * ActionBean for searching.
 * 
 * @author Roger Rene Kommer (r.kommer@micromata.de)
 * 
 */
public class GWikiSearchActionBean extends ActionBeanBase
{
  /**
   * Set as hidden parameter with prefix.
   */
  private String searchPrefix;

  private String searchExpression;

  private List<SearchResult> foundPages;

  private String searchMessage = "";

  private String backUrl;

  private int searchOffset;

  private int pageSize = 10;

  private int totalFound = 0;

  private String pageUrlArgs = "";

  protected void initFromParams()
  {
    StringBuilder sb = new StringBuilder();
    for (String cmd : wikiContext.getWikiWeb().getContentSearcher().getSearchMacros()) {
      String val = wikiContext.getRequestParameter(cmd);
      if (StringUtils.isBlank(val) == true) {
        continue;
      }
      if (sb.length() > 0) {
        sb.append(" ");
      }
      sb.append(cmd).append(":").append(val);
    }
    if (StringUtils.isBlank(searchExpression) == true) {
      if (StringUtils.isNotBlank(wikiContext.getRequestParameter("se")) == true) {
        searchExpression = wikiContext.getRequestParameter("se");
      }
    }

    if (StringUtils.isBlank(searchExpression) == true) {
      searchExpression = sb.toString();
    } else if (sb.toString().length() == 0) {
      ;
    } else {
      searchPrefix = sb.toString();
      // searchExpression = sb.toString() + " and(" + searchExpression + ")";
    }
  }

  protected String buildSearchExpression()
  {
    if (StringUtils.isBlank(searchPrefix) == true) {
      return searchExpression;
    } else if (StringUtils.isBlank(searchExpression) == true) {
      return searchPrefix;
    } else {
      return searchPrefix + " and(" + searchExpression + ")";
    }
  }

  public Object onInit()
  {
    initFromParams();
    return null;
  }

  public Object onSearch()
  {
    initFromParams();
    String se = buildSearchExpression();
    if (StringUtils.isBlank(se) == true) {
      wikiContext.addValidationError("gwiki.page.edit.Search.message.nosearchexpression");
      return null;
    }
    Iterable<GWikiElementInfo> webInfos = wikiContext.getWikiWeb().getElementInfos();
    List<SearchResult> sr = new ArrayList<SearchResult>(wikiContext.getWikiWeb().getElementInfoCount());
    for (GWikiElementInfo wi : webInfos) {
      sr.add(new SearchResult(wi));
    }
    SearchQuery query = new SearchQuery(se, true, sr);
    query.setSearchOffset(searchOffset);
    query.setMaxCount(pageSize);
    try {
      QueryResult qr = wikiContext.getWikiWeb().getContentSearcher().search(wikiContext, query);
      if (qr.getLookupWords().isEmpty() == false) {
        pageUrlArgs = "?_gwhiwords=" + WebUtils.encodeUrlParam(StringUtils.join(qr.getLookupWords(), ","));
      }
      foundPages = qr.getResults();
      totalFound = qr.getTotalFoundItems();

      searchMessage = translate("gwiki.page.edit.Search.message.pagefound", qr.getTotalFoundItems(), wikiContext.getWikiWeb()
          .getElementInfoCount(), (qr.getSearchTime()), searchOffset, (searchOffset + (pageSize < foundPages.size() ? pageSize : foundPages
          .size())));

    } catch (InvalidMatcherGrammar ex) {
      wikiContext.addSimpleValidationError(ex.getMessage());
    }
    return null;
  }

  public String getSearchExpression()
  {
    return searchExpression;
  }

  public void setSearchExpression(String searchExpression)
  {
    this.searchExpression = searchExpression;
  }

  public List<SearchResult> getFoundPages()
  {
    return foundPages;
  }

  public void setFoundPages(List<SearchResult> foundPages)
  {
    this.foundPages = foundPages;
  }

  public String getSearchMessage()
  {
    return searchMessage;
  }

  public void setSearchMessage(String searchMessage)
  {
    this.searchMessage = searchMessage;
  }

  public String getSearchPrefix()
  {
    return searchPrefix;
  }

  public void setSearchPrefix(String searchPrefix)
  {
    this.searchPrefix = searchPrefix;
  }

  public String getBackUrl()
  {
    return backUrl;
  }

  public void setBackUrl(String backUrl)
  {
    this.backUrl = backUrl;
  }

  public int getSearchOffset()
  {
    return searchOffset;
  }

  public void setSearchOffset(int searchOffset)
  {
    this.searchOffset = searchOffset;
  }

  public int getPageSize()
  {
    return pageSize;
  }

  public void setPageSize(int pageSize)
  {
    this.pageSize = pageSize;
  }

  public int getTotalFound()
  {
    return totalFound;
  }

  public void setTotalFound(int totalFound)
  {
    this.totalFound = totalFound;
  }

  public String getPageUrlArgs()
  {
    return pageUrlArgs;
  }

  public void setPageUrlArgs(String lookupWords)
  {
    this.pageUrlArgs = lookupWords;
  }

}
