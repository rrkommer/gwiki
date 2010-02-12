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

import de.micromata.genome.gwiki.page.GWikiContext;

/**
 * Base implementation for an artefakt with text as storage.
 * 
 * @author roger
 * 
 * @param <T>
 */
public abstract class GWikiTextArtefaktBase<T extends Serializable> extends GWikiPersistArtefaktBase<T> implements GWikiTextArtefakt<T>
{
  private static final long serialVersionUID = 8433380526307298393L;

  private String storageData;

  @Override
  public boolean renderWithParts(GWikiContext ctx)
  {
    ctx.append(getStorageData());
    return true;

  }

  public String getStorageData()
  {
    return storageData;
  }

  public void setStorageData(String storageData)
  {
    this.storageData = storageData;
  }

  public boolean isNoArchiveData()
  {
    return false;
  }

}
