/////////////////////////////////////////////////////////////////////////////
//
// Project   DHL-ParcelOnlinePostage
//
// Author    roger@micromata.de
// Created   02.12.2009
// Copyright Micromata 02.12.2009
//
/////////////////////////////////////////////////////////////////////////////
package de.micromata.genome.gwiki.utils;

/**
 * 
 * @author roger TODO gwiki minor may move to other class.
 */
public class AppendableUtils
{
  public static AppendableI create(final StringBuilder sb)
  {
    return new AbstractAppendable() {
      public AppendableI append(String s)
      {
        sb.append(s);
        return this;
      }
    };
  }
}
