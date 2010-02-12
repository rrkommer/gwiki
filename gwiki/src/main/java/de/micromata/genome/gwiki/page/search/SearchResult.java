/////////////////////////////////////////////////////////////////////////////
//
// Project   DHL-ParcelOnlinePostage
//
// Author    roger@micromata.de
// Created   27.10.2009
// Copyright Micromata 27.10.2009
//
/////////////////////////////////////////////////////////////////////////////
package de.micromata.genome.gwiki.page.search;

import de.micromata.genome.gwiki.model.GWikiElementInfo;

/**
 * Internal class to hold a search result element.
 * 
 * @author roger
 * 
 */
public class SearchResult
{
  private GWikiElementInfo elementInfo;

  private int relevance = 1;

  private String textExerpt = "";

  public SearchResult(GWikiElementInfo elementInfo)
  {
    this.elementInfo = elementInfo;
  }

  public SearchResult(SearchResult other)
  {
    this.elementInfo = other.elementInfo;
    this.relevance = other.relevance;
    this.textExerpt = other.textExerpt;
  }

  public SearchResult(SearchResult other, int relevance)
  {
    this(other);
    this.relevance = relevance;
  }

  public String getPageId()
  {
    return elementInfo.getId();
  }

  public int getRelevance()
  {
    return relevance;
  }

  public void setRelevance(int relevance)
  {
    this.relevance = relevance;
  }

  public String getTextExerpt()
  {
    return textExerpt;
  }

  public void setTextExerpt(String textExerpt)
  {
    this.textExerpt = textExerpt;
  }

  public GWikiElementInfo getElementInfo()
  {
    return elementInfo;
  }

  public void setElementInfo(GWikiElementInfo elementInfo)
  {
    this.elementInfo = elementInfo;
  }
}
