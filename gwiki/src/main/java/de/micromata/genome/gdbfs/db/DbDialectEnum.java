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

public enum DbDialectEnum implements DbDialect
{
  /**
   * Probably also work with Oracle 9 - 11
   */
  Oracle10(Flags.combine(Flags.SupportSequencer)), //
  ;
  private int flags;

  private DbDialectEnum(int flags)
  {
    this.flags = flags;

  }

  public boolean supports(Flags flag)
  {
    return (this.flags & flag.getFlags()) == flag.getFlags();
  }
}
