/////////////////////////////////////////////////////////////////////////////
//
// Project   DHL-ParcelOnlinePostage
//
// Author    roger@micromata.de
// Created   21.10.2009
// Copyright Micromata 21.10.2009
//
/////////////////////////////////////////////////////////////////////////////
package de.micromata.genome.gwiki.model;

import java.io.Serializable;

/**
 * Artefakt with has a string as storage.
 * 
 * @author roger
 * 
 * @param <T> compiled version of the artefakt.
 */
public interface GWikiTextArtefakt<T extends Serializable> extends GWikiArtefakt<T>
{
  String getStorageData();

  void setStorageData(String data);

  /**
   * 
   * @return true if the data should be archived.
   */
  boolean isNoArchiveData();

}
