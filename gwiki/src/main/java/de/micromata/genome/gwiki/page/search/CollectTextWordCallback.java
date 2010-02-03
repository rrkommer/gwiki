/////////////////////////////////////////////////////////////////////////////
//
// Project   DHL-ParcelOnlinePostage
//
// Author    roger@micromata.de
// Created   27.10.2009
// Copyright Micromata 27.10.2009
//
/////////////////////////////////////////////////////////////////////////////
package de.micromata.genome.gwiki.page.search;


public class CollectTextWordCallback  extends WordCallbackBase
{
  private StringBuilder buffer = new StringBuilder();

  private int lastLevel = 1;

  private boolean needFinalClose = false;

  public void callback(String word, int level)
  {
    int ll = level + getLevelOffset();
    if (lastLevel == ll) {
      buffer.append(word);
    } else {
      if (buffer.length() > 0) {
        buffer.append("</^>");
      }
      buffer.append("<^").append(ll).append(">").append(word);
      needFinalClose = true;
      lastLevel = ll;
    }
  }

  public StringBuilder getBuffer()
  {
    if (needFinalClose == true) {
      buffer.append("</^>");
      needFinalClose = false;
    }
    return buffer;
  }

  public void setBuffer(StringBuilder buffer)
  {
    this.buffer = buffer;
  }

  public int getLastLevel()
  {
    return lastLevel;
  }

  public void setLastLevel(int lastLevel)
  {
    this.lastLevel = lastLevel;
  }
}
