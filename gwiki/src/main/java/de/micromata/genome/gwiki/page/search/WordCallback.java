/////////////////////////////////////////////////////////////////////////////
//
// Project   DHL-ParcelOnlinePostage
//
// Author    roger@micromata.de
// Created   24.10.2009
// Copyright Micromata 24.10.2009
//
/////////////////////////////////////////////////////////////////////////////
package de.micromata.genome.gwiki.page.search;

/**
 * Will be called for index word.
 * 
 * @author Roger Rene Kommer (r.kommer@micromata.de)
 * 
 */
public interface WordCallback
{
  void callback(String word, int level);

  void pushLevel(int level);

  void popLevel();

}
