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

public abstract class GWikiPersistArtefaktBase<T extends Serializable> extends GWikiArtefaktBase<T> implements GWikiPersistArtefakt<T>
{

  private static final long serialVersionUID = 6350903340208724036L;

  public String buildFileName(String pageId, String partName)
  {
    return pageId + partName + getFileSuffix();
  }

}
