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
 * Same as java.lang.appendable, but no IOException.
 * 
 * @author Roger Rene Kommer (r.kommer@micromata.de)
 * 
 */
public interface AppendableI extends java.lang.Appendable
{
  AppendableI append(String s);

  AppendableI append(CharSequence csq);

  AppendableI append(CharSequence csq, int start, int end);

  AppendableI append(char c);

  AppendableI append(Object... values);
}
