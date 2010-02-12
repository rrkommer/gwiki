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

/**
 * Base implementation of the SearchTextExtractor interface.
 * 
 * @author roger
 * 
 */
public abstract class SearchTextExtractorBase implements SearchTextExtractor
{
  private int weight = 1;

  public SearchTextExtractorBase()
  {

  }

  public SearchTextExtractorBase(int weight)
  {
    this.weight = weight;
  }

  public int getWeight()
  {
    return weight;
  }

  public void setWeight(int weight)
  {
    this.weight = weight;
  }
}
