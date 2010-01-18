/////////////////////////////////////////////////////////////////////////////
//
// Project   DHL-ParcelOnlinePostage
//
// Author    roger@micromata.de
// Created   08.01.2010
// Copyright Micromata 08.01.2010
//
/////////////////////////////////////////////////////////////////////////////
package de.micromata.genome.gwiki.utils;

public class PropDiffLine extends DiffLine
{
  private String key;

  public PropDiffLine(DiffType diffType, String key, String left, int li, String right, int ri)
  {
    super(diffType, left, li, right, ri);
    this.key = key;
  }

  public String getKey()
  {
    return key;
  }

  public void setKey(String key)
  {
    this.key = key;
  }

}
