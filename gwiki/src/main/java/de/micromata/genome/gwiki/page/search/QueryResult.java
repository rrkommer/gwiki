/////////////////////////////////////////////////////////////////////////////
//
// Project   DHL-ParcelOnlinePostage
//
// Author    roger@micromata.de
// Created   03.11.2009
// Copyright Micromata 03.11.2009
//
/////////////////////////////////////////////////////////////////////////////
package de.micromata.genome.gwiki.page.search;

import java.util.Collections;
import java.util.List;

/**
 * A result bean for a search operation.
 * 
 * @author roger
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
