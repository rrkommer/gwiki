/////////////////////////////////////////////////////////////////////////////
//
// Project   DHL-ParcelOnlinePostage
//
// Author    roger@micromata.de
// Created   29.12.2009
// Copyright Micromata 29.12.2009
//
/////////////////////////////////////////////////////////////////////////////
package de.micromata.genome.gwiki.utils.html;

/**
 * Enumeration used by the html 2 wiki filter
 * 
 * @author Roger Rene Kommer (r.kommer@micromata.de)
 * 
 */
public enum Html2WikiElements implements Html2WikiElement
{
  Li("- ", "\n"), //
  LiStar("* ", "\n"), //
  LiNum("# ", "\n"), //
  B("*", "*"), //
  ;
  private String start;

  private String end;

  private Html2WikiElements(String start, String end)
  {
    this.start = start;
    this.end = end;
  }

  public String getEnd()
  {
    return end;
  }

  public String getStart()
  {
    return start;
  }

}
