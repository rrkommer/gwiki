/////////////////////////////////////////////////////////////////////////////
//
// Project   DHL-ParcelOnlinePostage
//
// Author    roger@micromata.de
// Created   17.11.2009
// Copyright Micromata 17.11.2009
//
/////////////////////////////////////////////////////////////////////////////
package de.micromata.genome.gwiki.model.filter;

import java.util.ArrayList;
import java.util.List;

import de.micromata.genome.gwiki.model.GWikiLog;

/**
 * Chain implementation to dispatch events.
 * 
 * @author Roger Rene Kommer (r.kommer@micromata.de)
 * 
 */
public class GWikiFilterChain<R, E extends GWikiFilterEvent, F extends GWikiFilter<R, E, F>>
{
  private int curIndex = 0;

  private F target;

  private List<F> filterList = new ArrayList<F>();

  private boolean exceptionSafe = false;

  public GWikiFilterChain(GWikiFilters filters, F target, List<F> filterList)
  {
    this.target = target;
    this.filterList = filterList;
  }

  public R nextFilter(E event)
  {
    do {
      if (curIndex >= filterList.size()) {
        return target.filter(this, event);
      }
      if (exceptionSafe == false) {
        return filterList.get(curIndex++).filter(this, event);
      }
      try {
        return filterList.get(curIndex++).filter(this, event);
      } catch (Exception ex) {
        GWikiLog.error("Failure executed filter: " + ex.getMessage(), ex);
        continue;
      }
    } while (true);
  }

  public boolean isExceptionSafe()
  {
    return exceptionSafe;
  }

  public void setExceptionSafe(boolean exceptionSafe)
  {
    this.exceptionSafe = exceptionSafe;
  }
}
