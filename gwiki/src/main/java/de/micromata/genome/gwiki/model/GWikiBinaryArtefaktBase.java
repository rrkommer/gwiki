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
import java.util.List;
import java.util.Map;

/**
 * Standard GWikiBinaryArtefakt base implementation
 * 
 * @author roger@micromata.de
 * 
 */
public abstract class GWikiBinaryArtefaktBase<T extends Serializable> implements GWikiBinaryArtefakt<T>, GWikiPersistArtefakt<T>
{

  private static final long serialVersionUID = -2412235746170911693L;

  private byte[] storageData;

  public boolean isNoArchiveData()
  {
    return false;
  }

  public byte[] getStorageData()
  {
    return storageData;
  }

  public void setStorageData(byte[] data)
  {
    this.storageData = data;
  }

  public void collectArtefakts(List<GWikiArtefakt< ? >> al)
  {

  }

  public void collectParts(Map<String, GWikiArtefakt< ? >> map)
  {

  }

}
