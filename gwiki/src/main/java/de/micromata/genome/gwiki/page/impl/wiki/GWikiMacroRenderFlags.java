/////////////////////////////////////////////////////////////////////////////
//
// Project   DHL-ParcelOnlinePostage
//
// Author    roger@micromata.de
// Created   03.01.2010
// Copyright Micromata 03.01.2010
//
/////////////////////////////////////////////////////////////////////////////
package de.micromata.genome.gwiki.page.impl.wiki;

/**
 * Source Render modes for macros.
 * 
 * @author Roger Rene Kommer (r.kommer@micromata.de)
 * 
 */
public enum GWikiMacroRenderFlags
{
  /**
   * If rendered as source add new line after start of macro body.
   */
  NewLineAfterStart(0x0001), //
  /**
   * if rendered as source insert new line before end of macro body.
   */
  NewLineBeforeEnd(0x0002), //
  /**
   * Wenn parsing macro body, remove white spaces before tokenize.
   */
  TrimTextContent(0x0010), //
  ;
  private int flag;

  private GWikiMacroRenderFlags(int flag)
  {
    this.flag = flag;
  }

  public static int combine(GWikiMacroRenderFlags... modes)
  {
    int flags = 0;
    for (GWikiMacroRenderFlags r : modes) {
      flags |= r.getFlag();
    }
    return flags;
  }

  public boolean isSet(int flags)
  {
    return (flags & flag) == flag;
  }

  public int getFlag()
  {
    return flag;
  }
}
