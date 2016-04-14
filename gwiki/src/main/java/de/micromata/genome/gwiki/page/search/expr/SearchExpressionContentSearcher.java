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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import de.micromata.genome.gwiki.model.GWikiAuthorization;
import de.micromata.genome.gwiki.model.logging.GWikiLogCategory;
import de.micromata.genome.gwiki.page.GWikiContext;
import de.micromata.genome.gwiki.page.search.ContentSearcher;
import de.micromata.genome.gwiki.page.search.IndexTextFilesContentSearcher;
import de.micromata.genome.gwiki.page.search.IndexTextFilesContentSearcher.SearchResultByRelevanceComparator;
import de.micromata.genome.gwiki.page.search.QueryResult;
import de.micromata.genome.gwiki.page.search.SearchQuery;
import de.micromata.genome.gwiki.page.search.SearchResult;
import de.micromata.genome.logging.GLog;
import de.micromata.genome.logging.LoggingServiceManager;

public class SearchExpressionContentSearcher implements ContentSearcher
{

  private SearchExpressionParser parser = new SearchExpressionParser();

  @Override
  public Collection<String> getSearchMacros()
  {
    return parser.getCommandExpressions().keySet();
  }

  @Override
  public void rebuildIndex(GWikiContext wikiContext, String pageId, boolean full)
  {
    Map<String, String> args = new HashMap<String, String>();
    args.put("pageId", pageId == null ? "" : pageId);
    args.put("full", Boolean.toString(full));
    wikiContext.getWikiWeb().getSchedulerProvider().execAsyncOne(wikiContext, SearchExpressionIndexerCallback.class,
        args);
  }

  protected void querySampleText(GWikiContext ctx, SearchExpression se, SearchQuery query, SearchResult sr)
  {
    List<String> words = se.getLookupWords();
    String rt;
    if (query.getTextExtractor() != null) {
      rt = query.getTextExtractor().getRawText(ctx, query, sr);
      if (StringUtils.isEmpty(rt) == true) {
        return;
      }
    } else {
      rt = SearchUtils.getTextSample(ctx, sr, words, sr.getPageId());
    }
    if (StringUtils.isEmpty(rt) == true) {
      return;
    }
    if (query.isHtmlSampleText() == true) {
      rt = SearchUtils.sampleToHtml(rt, words);
    }
    sr.setTextExerpt(rt);
  }

  private List<SearchResult> getSearchBase(GWikiContext ctx, SearchQuery query)
  {
    GWikiAuthorization auth = ctx.getWikiWeb().getAuthorization();
    List<SearchResult> ret = new ArrayList<SearchResult>();
    for (SearchResult sr : query.getResults()) {
      if (query.isFindUnindexed() == false && sr.getElementInfo().isIndexed() == false) {
        continue;
      }
      if (auth.isAllowToView(ctx, sr.getElementInfo()) == true
          || auth.isAllowToEdit(ctx, sr.getElementInfo()) == true) {
        ret.add(sr);
      }
    }
    return ret;
  }

  @Override
  public QueryResult search(GWikiContext ctx, SearchQuery query)
  {
    long startSearchTime = System.currentTimeMillis();

    List<SearchResult> ret = getSearchBase(ctx, query);
    int totalItems = ret.size();
    query.setResults(ret);

    if (StringUtils.isEmpty(query.getSearchExpression()) == true) {
      QueryResult qs = new QueryResult(ret, ret.size());
      qs.setTotalItems(totalItems);
      qs.setTotalFoundItems(ret.size());
      qs.setSearchTime(System.currentTimeMillis() - startSearchTime);
      return qs;
    }

    SearchExpression se = parser.parse(StringUtils.defaultString(query.getSearchExpression()));
    String strsearch = se.toString();
    GLog.info(GWikiLogCategory.Wiki, "Search; " + query.getSearchExpression() + ": " + strsearch);
    startSearchTime = System.currentTimeMillis();

    Collection<SearchResult> res = se.filter(ctx, query);
    if (res instanceof List) {
      ret = (List<SearchResult>) res;
    } else {
      ret = new ArrayList<SearchResult>();
      ret.addAll(res);
    }

    if ((se instanceof SearchExpressionOrderBy) == false) {
      Collections.sort(ret, new SearchResultByRelevanceComparator());
    }
    long now = System.currentTimeMillis();
    long searchTime = now - startSearchTime;
    LoggingServiceManager.get().getStatsDAO().addPerformance(GWikiLogCategory.Wiki, "WikiSearch.search", searchTime, 0);
    long startQuerySampleTime = now;
    int totalFound = ret.size();
    if (query.getSearchOffset() > 0 || query.getMaxCount() < totalFound) {
      int startIdx = query.getSearchOffset();
      int eidx = startIdx + (totalFound < query.getMaxCount() ? totalFound : query.getMaxCount());
      eidx = eidx < ret.size() ? eidx : ret.size();
      if (startIdx < eidx) {
        ret = ret.subList(startIdx, eidx);
      } else {
        ret = new ArrayList<SearchResult>(0);
      }
    }

    if (query.isWithSampleText() == true) {
      for (int i = 0; i < ret.size() && i < query.getMaxCount(); ++i) {
        querySampleText(ctx, se, query, ret.get(i));
      }
    }
    QueryResult qr = new QueryResult(ret, ret.size());
    qr.setTotalItems(totalItems);
    qr.setLookupWords(se.getLookupWords());
    qr.setTotalFoundItems(totalFound);
    qr.setSearchTime(searchTime);
    qr.setGetTextExamleTime(System.currentTimeMillis() - startQuerySampleTime);
    return qr;

  }

  public String reworkRawTextForPreview(String str)
  {
    str = StringUtils.replace(str, "\n", "<br/>\n");
    return str;
  }

  @Override
  public String getHtmlPreview(GWikiContext ctx, String pageId)
  {
    return IndexTextFilesContentSearcher.getHtmlPreviewS(ctx, pageId);
  }

  public SearchExpressionParser getParser()
  {
    return parser;
  }

  public void setParser(SearchExpressionParser parser)
  {
    this.parser = parser;
  }

}
