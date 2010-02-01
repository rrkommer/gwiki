/////////////////////////////////////////////////////////////////////////////
//
// Project   DHL-ParcelOnlinePostage
//
// Author    roger@micromata.de
// Created   25.01.2009
// Copyright Micromata 25.01.2009
//
/////////////////////////////////////////////////////////////////////////////
package de.micromata.genome.util.bean;

public interface NamedAttrSetter<BEAN, VAL> extends AttrSetter<BEAN, VAL>
{
  /**
   * the property name
   * 
   * @return
   */
  String getName();

}
