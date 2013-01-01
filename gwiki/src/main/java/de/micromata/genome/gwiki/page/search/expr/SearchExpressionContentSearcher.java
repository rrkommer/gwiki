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
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import de.micromata.genome.gwiki.model.GWikiAuthorization;
import de.micromata.genome.gwiki.model.GWikiLog;
import de.micromata.genome.gwiki.page.GWikiContext;
import de.micromata.genome.gwiki.page.search.ContentSearcher;
import de.micromata.genome.gwiki.page.search.IndexTextFilesContentSearcher;
import de.micromata.genome.gwiki.page.search.IndexTextFilesContentSearcher.SearchResultByRelevanceComparator;
import de.micromata.genome.gwiki.page.search.QueryResult;
import de.micromata.genome.gwiki.page.search.SearchQuery;
import de.micromata.genome.gwiki.page.search.SearchResult;

public class SearchExpressionContentSearcher implements ContentSearcher
{

  private SearchExpressionParser parser = new SearchExpressionParser();

  public Collection<String> getSearchMacros()
  {
    return parser.getCommandExpressions().keySet();
  }

  public void rebuildIndex(GWikiContext wikiContext, String pageId, boolean full)
  {
    Map<String, String> args = new HashMap<String, String>();
    args.put("pageId", pageId == null ? "" : pageId);
    args.put("full", Boolean.toString(full));
    wikiContext.getWikiWeb().getSchedulerProvider().execAsyncOne(wikiContext, SearchExpressionIndexerCallback.class, args);
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
    if (StringUtils.isEmpty(rt) == true)
      return;
    if (query.isHtmlSampleText() == true) {
      rt = SearchUtils.sampleToHtml(rt, words);
    }
    sr.setTextExerpt(rt);
  }

  public QueryResult search(GWikiContext ctx, SearchQuery query)
  {
    long startSearchTime = System.currentTimeMillis();
    GWikiAuthorization auth = ctx.getWikiWeb().getAuthorization();

    List<SearchResult> ret = new ArrayList<SearchResult>();
    for (SearchResult sr : query.getResults()) {
      if (auth.isAllowToView(ctx, sr.getElementInfo()) == true || auth.isAllowToEdit(ctx, sr.getElementInfo()) == true) {
        ret.add(sr);
      }
    }
    query.setResults(ret);

    if (StringUtils.isEmpty(query.getSearchExpression()) == true) {
      QueryResult qs = new QueryResult(ret, ret.size());
      qs.setTotalFoundItems(ret.size());
      qs.setSearchTime(System.currentTimeMillis() - startSearchTime);
      return qs;
    }

    SearchExpression se = parser.parse(StringUtils.defaultString(query.getSearchExpression()));
    String strsearch = se.toString();
    GWikiLog.info("Search; " + query.getSearchExpression() + ": " + strsearch);
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
    ctx.getWikiWeb().getLogging().addPerformance("WikiSearch.search", searchTime, 0);
    long startQuerySampleTime = now;
    int totalSize = ret.size();
    if (query.getSearchOffset() > 0 || query.getMaxCount() < totalSize) {
      int startIdx = query.getSearchOffset();
      int eidx = startIdx + (totalSize < query.getMaxCount() ? totalSize : query.getMaxCount());
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
    qr.setLookupWords(se.getLookupWords());
    qr.setTotalFoundItems(totalSize);
    qr.setSearchTime(searchTime);
    qr.setGetTextExamleTime(System.currentTimeMillis() - startQuerySampleTime);
    return qr;

  }

  public String reworkRawTextForPreview(String str)
  {
    str = StringUtils.replace(str, "\n", "<br/>\n");
    return str;
  }

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
