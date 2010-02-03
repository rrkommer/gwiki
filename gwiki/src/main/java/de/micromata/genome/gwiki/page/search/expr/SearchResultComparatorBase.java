/////////////////////////////////////////////////////////////////////////////
//
// Project   DHL-ParcelOnlinePostage
//
// Author    roger@micromata.de
// Created   10.11.2009
// Copyright Micromata 10.11.2009
//
/////////////////////////////////////////////////////////////////////////////
package de.micromata.genome.gwiki.page.search.expr;

import de.micromata.genome.gwiki.page.search.SearchResult;

public abstract class SearchResultComparatorBase implements SearchResultComparator
{
  protected SearchResultComparator nextComparator;

  protected boolean desc = false;

  public SearchResultComparatorBase()
  {

  }

  public SearchResultComparatorBase(SearchResultComparator nextComparator, boolean desc)
  {
    this.nextComparator = nextComparator;
    this.desc = desc;
  }

  public abstract int compareThis(SearchResult o1, SearchResult o2);

  public int compare(SearchResult o1, SearchResult o2)
  {
    int res = compareThis(o1, o2);
    if (res != 0) {
      if (desc == true) {
        return res < 0 ? 1 : -1;
      }
      return res;
    }
    if (nextComparator != null) {
      return nextComparator.compare(o1, o2);
    }
    return 0;
  }

  public SearchResultComparator getNextComparator()
  {
    return nextComparator;
  }

  public void setNextComparator(SearchResultComparator nextComparator)
  {
    this.nextComparator = nextComparator;
  }

  public boolean isDesc()
  {
    return desc;
  }

  public void setDesc(boolean desc)
  {
    this.desc = desc;
  }
}
