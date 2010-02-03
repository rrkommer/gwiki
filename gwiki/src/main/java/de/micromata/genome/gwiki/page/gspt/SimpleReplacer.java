/////////////////////////////////////////////////////////////////////////////
//
// Project   DHL-ParcelOnlinePostage
//
// Author    roger@micromata.de
// Created   18.11.2006
// Copyright Micromata 18.11.2006
//
/////////////////////////////////////////////////////////////////////////////
package de.micromata.genome.gwiki.page.gspt;

import java.util.Map;

public class SimpleReplacer extends ReplacerBase
{
  String start;

  String replace;

  public SimpleReplacer(String start, String replace)
  {
    this.start = start;
    this.replace = replace;
  }

  public String getEnd()
  {
    return "";
  }

  public String getStart()
  {
    return start;
  }

  public String replace(ReplacerContext ctx, Map<String, String> attr, boolean isClosed)
  {
    return replace;
  }

  public String getReplace()
  {
    return replace;
  }

}
