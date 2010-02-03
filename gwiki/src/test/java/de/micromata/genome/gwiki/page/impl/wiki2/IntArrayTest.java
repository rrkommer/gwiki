/////////////////////////////////////////////////////////////////////////////
//
// Project   DHL-ParcelOnlinePostage
//
// Author    roger@micromata.de
// Created   26.12.2009
// Copyright Micromata 26.12.2009
//
/////////////////////////////////////////////////////////////////////////////
package de.micromata.genome.gwiki.page.impl.wiki2;

import de.micromata.genome.gwiki.utils.IntArray;
import junit.framework.TestCase;

public class IntArrayTest extends TestCase
{
  public void testOverflow()
  {
    IntArray ia = new IntArray(10);
    for (int i = 0; i < 20; ++i) {
      ia.add(i);
    }
  }
}
