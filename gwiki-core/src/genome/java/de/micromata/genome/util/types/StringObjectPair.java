/////////////////////////////////////////////////////////////////////////////
//
// Project   DHL-ParcelOnlinePostage
//
// Author    roger@micromata.de
// Created   11.07.2006
// Copyright Micromata 11.07.2006
//
/////////////////////////////////////////////////////////////////////////////
package de.micromata.genome.util.types;

/**
 * @see Pair
 * @author roger@micromata.de
 * 
 */
public class StringObjectPair extends Pair<String, Object>
{
  private static final long serialVersionUID = 8490052926203941134L;

  public StringObjectPair()
  {
    super();
  }

  public StringObjectPair(String key, Object value)
  {
    super(key, value);
  }
}
