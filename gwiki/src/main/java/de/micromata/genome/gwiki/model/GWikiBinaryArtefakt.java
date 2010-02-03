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
 * An artefakt which store its data as binary.
 * 
 * @author roger@micromata.de
 * 
 */
public interface GWikiBinaryArtefakt<T extends Serializable> extends GWikiPersistArtefakt<T>
{
  byte[] getStorageData();

  void setStorageData(byte[] data);

  /**
   * 
   * @return true if the data should be archived.
   */
  boolean isNoArchiveData();
}
