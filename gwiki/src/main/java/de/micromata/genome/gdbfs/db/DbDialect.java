/////////////////////////////////////////////////////////////////////////////
//
// Project   DHL-ParcelOnlinePostage
//
// Author    roger@micromata.de
// Created   12.01.2010
// Copyright Micromata 12.01.2010
//
/////////////////////////////////////////////////////////////////////////////
package de.micromata.genome.gdbfs.db;

public interface DbDialect
{
  public static enum Flags
  {
    SupportSequencer(0x0001), //
    ;
    private Flags(int flags)
    {
      this.flags = flags;
    }

    private int flags;

    public int getFlags()
    {
      return flags;
    }

    public static int combine(Flags... flags)
    {
      int ret = 0;
      for (Flags fl : flags) {
        ret |= fl.getFlags();
      }
      return ret;
    }
  }

  public boolean supports(Flags flag);

  public String name();

}
