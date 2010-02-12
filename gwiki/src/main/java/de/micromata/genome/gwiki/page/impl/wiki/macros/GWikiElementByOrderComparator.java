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
 * Comparator to compare to GWiki Element info by order property value.
 * 
 * @author Roger Rene Kommer (r.kommer@micromata.de)
 * 
 */
public class GWikiElementByOrderComparator extends GWikiElementComparatorBase
{
  public GWikiElementByOrderComparator()
  {
  }

  public GWikiElementByOrderComparator(Comparator<GWikiElementInfo> parentComparator)
  {
    super(parentComparator);
  }

  public int compare(GWikiElementInfo o1, GWikiElementInfo o2)
  {
    int i0 = o1.getOrder();
    int i1 = o2.getOrder();
    if (i1 == i0)
      return compareParent(o1, o2);
    if (i1 == -1) {
      return -1;
    }
    if (i0 == -1)
      return 1;
    if (i1 < i0)
      return 1;
    if (i1 > i0)
      return -1;
    return compareParent(o1, o2);
  }

}
