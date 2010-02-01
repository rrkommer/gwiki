/////////////////////////////////////////////////////////////////////////////
//
// Project   DHL-ParcelOnlinePostage
//
// Author    roger@micromata.de
// Created   24.01.2009
// Copyright Micromata 24.01.2009
//
/////////////////////////////////////////////////////////////////////////////
package de.micromata.genome.util.bean;

/**
 * Simply wrapper to set a property in a bean
 * 
 * @author roger@micromata.de
 * 
 */
public interface AttrSetter<BEAN, VAL>
{
  void set(BEAN bean, VAL value);
}
