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

package de.micromata.genome.gwiki.page.search;

import java.util.Collections;
import java.util.List;

/**
 * A result bean for a search operation.
 * 
 * @author Roger Rene Kommer (r.kommer@micromata.de)
 * 
 */
public class QueryResult
{
  private List<SearchResult> results;

  private int foundItems;

  /**
   * All found items.
   */
  private int totalFoundItems;

  private long searchTime;

  private long getTextExamleTime;

  private List<String> lookupWords = Collections.emptyList();

  public QueryResult()
  {

  }

  public QueryResult(List<SearchResult> results, int foundItems)
  {
    this.results = results;
    this.foundItems = foundItems;
  }

  public List<SearchResult> getResults()
  {
    return results;
  }

  public void setResults(List<SearchResult> results)
  {
    this.results = results;
  }

  public int getFoundItems()
  {
    return foundItems;
  }

  public void setFoundItems(int foundItems)
  {
    this.foundItems = foundItems;
  }

  public long getSearchTime()
  {
    return searchTime;
  }

  public void setSearchTime(long searchTime)
  {
    this.searchTime = searchTime;
  }

  public long getGetTextExamleTime()
  {
    return getTextExamleTime;
  }

  public void setGetTextExamleTime(long getTextExamleTime)
  {
    this.getTextExamleTime = getTextExamleTime;
  }

  public int getTotalFoundItems()
  {
    return totalFoundItems;
  }

  public void setTotalFoundItems(int totalFoundItems)
  {
    this.totalFoundItems = totalFoundItems;
  }

  public List<String> getLookupWords()
  {
    return lookupWords;
  }

  public void setLookupWords(List<String> lookupWords)
  {
    this.lookupWords = lookupWords;
  }

}
