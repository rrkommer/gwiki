/////////////////////////////////////////////////////////////////////////////
//
// Project   DHL-ParcelOnlinePostage
//
// Author    roger@micromata.de
// Created   02.12.2009
// Copyright Micromata 02.12.2009
//
/////////////////////////////////////////////////////////////////////////////
package de.micromata.genome.gwiki.page.search;

import org.apache.commons.collections15.ArrayStack;
import org.apache.commons.collections15.Buffer;

public abstract class WordCallbackBase implements WordCallback
{
  protected Buffer<Integer> offsetLevel = new ArrayStack<Integer>();

  protected int getLevelOffset()
  {
    if (offsetLevel.isEmpty() == true) {
      return 0;
    }
    int ret = 0;
    for (Integer i : offsetLevel) {
      ret += i;
    }
    return ret;
  }

  public void popLevel()
  {
    offsetLevel.remove();
  }

  public void pushLevel(int level)
  {
    offsetLevel.add(level);
  }

}
