/////////////////////////////////////////////////////////////////////////////
//
// Project   DHL-ParcelOnlinePostage
//
// Author    jensi@micromata.de
// Created   04.08.2009
// Copyright Micromata 04.08.2009
//
/////////////////////////////////////////////////////////////////////////////
package de.micromata.genome.util.types;

import java.util.Comparator;

public class NameComparator implements Comparator<Name>
{
  private static NameComparator instance = new NameComparator();
  
  public static NameComparator getInstance()
  {
    return instance;
  }

  public int compare(Name o1, Name o2)
  {
    return o1.name().compareTo(o2.name());
  }

}
