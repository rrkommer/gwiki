/////////////////////////////////////////////////////////////////////////////
//
// Project   DHL-ParcelOnlinePostage
//
// Author    roger@micromata.de
// Created   25.10.2009
// Copyright Micromata 25.10.2009
//
/////////////////////////////////////////////////////////////////////////////
package de.micromata.genome.gwiki.model;

import java.io.Serializable;

/**
 * Artefakt that can be persist.
 * 
 * @author roger@micromata.de
 * 
 */
public interface GWikiPersistArtefakt<T extends Serializable> extends GWikiArtefakt<T>
{
  /**
   * Build a file name for this element (pageId), partName and artefakt type.
   * 
   * @param pageId
   * @param partName
   * @return
   */
  String buildFileName(String pageId, String partName);

  String getFileSuffix();
}
