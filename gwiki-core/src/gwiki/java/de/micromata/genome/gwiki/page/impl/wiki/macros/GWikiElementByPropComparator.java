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

public class GWikiElementByPropComparator extends GWikiElementComparatorBase
{
  private String propName;

  private String defaultValue;

  public GWikiElementByPropComparator()
  {

  }

  public GWikiElementByPropComparator(String propName)
  {
    this.propName = propName;
  }

  public GWikiElementByPropComparator(String propName, String defaultValue)
  {
    this.propName = propName;
    this.defaultValue = defaultValue;
  }

  public GWikiElementByPropComparator(String propName, Comparator<GWikiElementInfo> parentComparator)
  {
    super(parentComparator);
    this.propName = propName;
  }

  public int compare(GWikiElementInfo o1, GWikiElementInfo o2)
  {
    String s1 = o1.getProps().getStringValue(propName, defaultValue);
    String s2 = o2.getProps().getStringValue(propName, defaultValue);
    if (s1 == s2)
      return compareParent(o1, o2);
    if (s1 == null)
      return 1;
    if (s2 == null)
      return -1;
    int ret = s1.compareTo(s2);
    if (ret != 0)
      return ret;
    return compareParent(o1, o2);
  }

  public String getPropName()
  {
    return propName;
  }

  public void setPropName(String propName)
  {
    this.propName = propName;
  }

}
