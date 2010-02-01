/////////////////////////////////////////////////////////////////////////////
//
// Project   DHL-ParcelOnlinePostage
//
// Author    roger@micromata.de
// Created   11.12.2009
// Copyright Micromata 11.12.2009
//
/////////////////////////////////////////////////////////////////////////////
package de.micromata.genome.gwiki.page.impl.wiki.macros;

import java.util.Comparator;

import de.micromata.genome.gwiki.model.GWikiElementInfo;

public class GWikiElementByIntPropComparator extends GWikiElementComparatorBase
{
  private String propName;

  private int defaultValue;

  public GWikiElementByIntPropComparator()
  {

  }

  public GWikiElementByIntPropComparator(String propName)
  {
    this(propName, 0);
  }

  public GWikiElementByIntPropComparator(String propName, int defaultValue)
  {
    this.propName = propName;
    this.defaultValue = defaultValue;
  }

  public GWikiElementByIntPropComparator(String propName, int defaultValue, Comparator<GWikiElementInfo> parentComparator)
  {
    super(parentComparator);
    this.propName = propName;
    this.defaultValue = defaultValue;
  }

  public int compare(GWikiElementInfo o1, GWikiElementInfo o2)
  {
    int s1 = o1.getProps().getIntValue(propName, defaultValue);
    int s2 = o2.getProps().getIntValue(propName, defaultValue);
    if (s1 == s2)
      return compareParent(o1, o2);
    if (s1 < s2) {
      return -1;
    }
    return 1;
  }

  public String getPropName()
  {
    return propName;
  }

  public void setPropName(String propName)
  {
    this.propName = propName;
  }

  public int getDefaultValue()
  {
    return defaultValue;
  }

  public void setDefaultValue(int defaultValue)
  {
    this.defaultValue = defaultValue;
  }

}
