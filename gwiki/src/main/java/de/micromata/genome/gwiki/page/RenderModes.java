/////////////////////////////////////////////////////////////////////////////
//
// Project   DHL-ParcelOnlinePostage
//
// Author    roger@micromata.de
// Created   06.12.2009
// Copyright Micromata 06.12.2009
//
/////////////////////////////////////////////////////////////////////////////
package de.micromata.genome.gwiki.page;

/**
 * Flags for rendering.
 * 
 * @author Roger Rene Kommer (r.kommer@micromata.de)
 * 
 */
public enum RenderModes
{
  Full(0x0), //

  NoLinks(0x00001), //
  /**
   * Render global links using the public url setting in GwikiGlobalConfig.
   */
  GlobalLinks(0x00002), //

  NoImages(0x00004), //
  GlobalImageLinks(0x00008), //
  /**
   * as local links
   */
  LocalImageLinks(0x00010), //

  NoToc(0x00020), //
  /**
   * render in memory.
   */
  InMem(0x0100), //
  /**
   * no page decoration with navication, etc.
   */
  NoPageDecoration(0x0200), //

  ForText(0x1000), //
  ForRichTextEdit(0x2000), //
  ForIndex(combine(NoLinks, NoToc, InMem, ForText, NoPageDecoration)), //
  ;
  private int flag;

  private RenderModes(int flag)
  {
    this.flag = flag;
  }

  public static int combine(RenderModes... modes)
  {
    int flags = 0;
    for (RenderModes r : modes) {
      flags |= r.getFlag();
    }
    return flags;
  }

  /**
   * same as valueOf but returns null.
   * 
   * @param mode
   * @return
   */
  public static RenderModes getRenderMode(String mode)
  {
    for (RenderModes rm : values()) {
      if (rm.name().equals(mode) == true) {
        return rm;
      }
    }
    return null;
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
