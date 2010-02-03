/////////////////////////////////////////////////////////////////////////////
//
// Project   DHL-ParcelOnlinePostage
//
// Author    roger@micromata.de
// Created   01.01.2010
// Copyright Micromata 01.01.2010
//
/////////////////////////////////////////////////////////////////////////////
package de.micromata.genome.gwiki.page.impl.wiki;

import de.micromata.genome.gwiki.utils.html.Html2WikiTransformInfo;

/**
 * Marker interface that this macro can be interpreted in Rich Text Editor.
 * 
 * @author roger@micromata.de
 * 
 */
public interface GWikiMacroRte extends GWikiRuntimeMacro
{
  /**
   * 
   * @return may return null if no transform info is needed
   */
  Html2WikiTransformInfo getTransformInfo();
}
