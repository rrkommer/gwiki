/////////////////////////////////////////////////////////////////////////////
//
// Project   DHL-ParcelOnlinePostage
//
// Author    roger@micromata.de
// Created   18.10.2009
// Copyright Micromata 18.10.2009
//
/////////////////////////////////////////////////////////////////////////////
package de.micromata.genome.gwiki.model;

import java.io.Serializable;
import java.util.Map;

import de.micromata.genome.gwiki.model.config.GWikiMetaTemplate;
import de.micromata.genome.gwiki.page.GWikiContext;
import de.micromata.genome.gwiki.page.impl.GWikiEditorArtefakt;

/**
 * All items (page, attachments, config, etc.) are a GWikiElement.
 * 
 * @author roger@micromata.de
 * 
 */
public interface GWikiElement extends Serializable
{
  /**
   * The head information of this element.
   * 
   * @return
   */
  GWikiElementInfo getElementInfo();

  /**
   * Setter for GWikiElementInfo
   * 
   * @param ei
   */
  void setElementInfo(GWikiElementInfo ei);

  /**
   * Get all parts/Artefakts of this elements.
   * 
   * @param map
   */
  void collectParts(Map<String, GWikiArtefakt< ? >> map);

  /**
   * Get the main artefakt.
   * 
   * @return null if not exists
   */
  GWikiArtefakt< ? > getMainPart();

  /**
   * 
   * @param partName
   * @return null if not exists
   */
  GWikiArtefakt< ? > getPart(String partName);

  /**
   * The GWikiMetaTemplate describes the type of this element.
   * 
   * @return
   */
  GWikiMetaTemplate getMetaTemplate();

  /**
   * Render/Serve this element. Basically has the same functionality of a servlet.
   * 
   * @param ctx
   */
  void serve(GWikiContext ctx);

  /**
   * Store part/artefakt information from editors into internal element structures.
   * 
   * @param ctx
   * @param editors
   */
  void saveParts(GWikiContext ctx, Map<String, GWikiEditorArtefakt< ? >> editors);
}
