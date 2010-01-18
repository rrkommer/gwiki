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
 * Interface to retrieve a property from a bean.
 * 
 * @author roger@micromata.de
 * 
 */
public interface AttrGetter<BEAN, VAL>
{
  VAL get(BEAN bean);
}
