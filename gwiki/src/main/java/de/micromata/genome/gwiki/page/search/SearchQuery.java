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

package de.micromata.genome.gwiki.page.search;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import de.micromata.genome.gwiki.model.GWikiElement;
import de.micromata.genome.gwiki.model.GWikiElementInfo;
import de.micromata.genome.gwiki.model.GWikiWeb;
import de.micromata.genome.gwiki.page.GWikiContext;

/**
 * Bean to hold the query.
 * 
 * @author Roger Rene Kommer (r.kommer@micromata.de)
 * 
 */
public class SearchQuery
{
  private String searchExpression;

  private boolean withSampleText = false;

  /**
   * sample text as HTML
   */
  private boolean htmlSampleText = true;

  private int searchOffset = 0;

  private int maxCount = 20;

  private Collection<SearchResult> results;

  /**
   * if null, uses the normal content search. otherwise collects from this interface.
   */
  private SearchTextExtractor textExtractor;

  /**
   * Will be used internal.
   */
  private GlobalWordIndexTextArtefakt globalIndex;

  private boolean fallBackIfNoIndex = true;

  private boolean findUnindexed = false;

  public SearchQuery()
  {

  }

  /**
   * TODO look callstack
   * 
   * @param searchExpression
   * @param elems
   */
  @Deprecated
  public SearchQuery(String searchExpression, Map<String, GWikiElementInfo> elems)
  {
    this.searchExpression = searchExpression;
    List<SearchResult> srl = new ArrayList<SearchResult>(elems.size());
    for (GWikiElementInfo wi : elems.values()) {
      srl.add(new SearchResult(wi));
    }
    this.results = srl;
  }

  public SearchQuery(String searchExpression, GWikiWeb wikiWeb)
  {
    this(searchExpression, wikiWeb.getElementInfos(), wikiWeb.getElementInfoCount());
  }

  public SearchQuery(String searchExpression, Iterable<GWikiElementInfo> elems, int elemCount)
  {
    this.searchExpression = searchExpression;
    List<SearchResult> srl = new ArrayList<SearchResult>(elemCount);
    for (GWikiElementInfo wi : elems) {
      srl.add(new SearchResult(wi));
    }
    this.results = srl;
  }

  public SearchQuery(String searchExpression, Collection<SearchResult> results)
  {
    this(searchExpression, false, results);
  }

  public SearchQuery(String searchExpression, boolean withSampleText, Collection<SearchResult> results)
  {
    this.searchExpression = searchExpression;
    this.withSampleText = withSampleText;
    this.results = results;
  }

  public SearchQuery(SearchQuery other)
  {
    this.searchExpression = other.searchExpression;
    this.withSampleText = other.withSampleText;
    this.maxCount = other.maxCount;
    this.results = other.results;
    this.globalIndex = other.globalIndex;
  }

  public String getSearchExpression()
  {
    return searchExpression;
  }

  public void setSearchExpression(String searchExpression)
  {
    this.searchExpression = searchExpression;
  }

  public boolean isWithSampleText()
  {
    return withSampleText;
  }

  public void setWithSampleText(boolean withSampleText)
  {
    this.withSampleText = withSampleText;
  }

  public Collection<SearchResult> getResults()
  {
    return results;
  }

  public void setResults(Collection<SearchResult> results)
  {
    this.results = results;
  }

  public int getMaxCount()
  {
    return maxCount;
  }

  public void setMaxCount(int maxCount)
  {
    this.maxCount = maxCount;
  }

  public SearchTextExtractor getTextExtractor()
  {
    return textExtractor;
  }

  public void setTextExtractor(SearchTextExtractor textExtractor)
  {
    this.textExtractor = textExtractor;
  }

  public boolean isHtmlSampleText()
  {
    return htmlSampleText;
  }

  public void setHtmlSampleText(boolean htmlSampleText)
  {
    this.htmlSampleText = htmlSampleText;
  }

  public int getSearchOffset()
  {
    return searchOffset;
  }

  public void setSearchOffset(int searchOffset)
  {
    this.searchOffset = searchOffset;
  }

  public GlobalWordIndexTextArtefakt getGlobalIndex(GWikiContext wikiContext)
  {
    if (globalIndex != null) {
      return globalIndex;
    }
    GWikiElement gi = wikiContext.getWikiWeb().findElement(GlobalIndexFile.GLOBAL_INDEX_PAGEID);
    if (gi == null) {
      return null;
    }
    GlobalWordIndexTextArtefakt art = (GlobalWordIndexTextArtefakt) gi.getMainPart();
    globalIndex = art;
    return globalIndex;
  }

  public GlobalWordIndexTextArtefakt getGlobalIndex()
  {
    return globalIndex;
  }

  public void setGlobalIndex(GlobalWordIndexTextArtefakt globalIndex)
  {
    this.globalIndex = globalIndex;
  }

  public boolean isFallBackIfNoIndex()
  {
    return fallBackIfNoIndex;
  }

  public void setFallBackIfNoIndex(boolean fallBackIfNoIndex)
  {
    this.fallBackIfNoIndex = fallBackIfNoIndex;
  }

  public boolean isFindUnindexed()
  {
    return findUnindexed;
  }

  public void setFindUnindexed(boolean findUnindexed)
  {
    this.findUnindexed = findUnindexed;
  }

}
