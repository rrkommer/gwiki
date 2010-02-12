/////////////////////////////////////////////////////////////////////////////
//
// Project   DHL-ParcelOnlinePostage
//
// Author    roger@micromata.de
// Created   06.11.2009
// Copyright Micromata 06.11.2009
//
/////////////////////////////////////////////////////////////////////////////
package de.micromata.genome.gwiki.page.impl.wiki.macros;

import java.util.Comparator;

import de.micromata.genome.gwiki.model.GWikiElementInfo;

/**
 * Base implementation for comparing GWikiElementInfo.
 * 
 * @author roger
 * 
 */
public abstract class GWikiElementComparatorBase implements Comparator<GWikiElementInfo>
{
  protected Comparator<GWikiElementInfo> parentComparator;

  public GWikiElementComparatorBase()
  {

  }

  public GWikiElementComparatorBase(Comparator<GWikiElementInfo> parentComparator)
  {
    this.parentComparator = parentComparator;
  }

  public int compareParent(GWikiElementInfo o1, GWikiElementInfo o2)
  {
    if (parentComparator == null) {
      return 0;
    }
    return parentComparator.compare(o1, o2);
  }

  public Comparator<GWikiElementInfo> getParentComparator()
  {
    return parentComparator;
  }

  public void setParentComparator(Comparator<GWikiElementInfo> parentComparator)
  {
    this.parentComparator = parentComparator;
  }

}
