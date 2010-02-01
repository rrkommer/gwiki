/////////////////////////////////////////////////////////////////////////////
//
// Project   DHL-ParcelOnlinePostage
//
// Author    roger@micromata.de
// Created   12.12.2009
// Copyright Micromata 12.12.2009
//
/////////////////////////////////////////////////////////////////////////////
package de.micromata.genome.util.runtime;

public interface CallableX1<V, ARG1, EX extends Throwable>
{
  public V call(ARG1 arg1) throws EX;
}
