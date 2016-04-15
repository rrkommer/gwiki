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

import java.util.Collection;

import de.micromata.genome.gwiki.page.GWikiContext;

/**
 * Trivial grep'er.
 *
 * @author Roger Rene Kommer (r.kommer@micromata.de)
 */
public interface ContentSearcher
{

  /**
   * Start rebuilding index.
   *
   * @param wikiContext the wiki context
   * @param pageId may null if all pages should be reindexed
   * @param full if false indexer reindex only changed documents.
   */
  public void rebuildIndex(GWikiContext wikiContext, String pageId, boolean full);

  /**
   * return a collection of searchmacros, like parentpageid:id, etc.
   *
   * @return the search macros
   */
  Collection<String> getSearchMacros();

  /**
   * Search.
   *
   * @param ctx the ctx
   * @param query the query
   * @return pageId's
   */
  QueryResult search(GWikiContext ctx, SearchQuery query);

  /**
   * Give a HTML fragment for the preview of given pageId.
   *
   * @param ctx the ctx
   * @param pageId the page id
   * @return null if no preview
   */
  String getHtmlPreview(GWikiContext ctx, String pageId);

}
