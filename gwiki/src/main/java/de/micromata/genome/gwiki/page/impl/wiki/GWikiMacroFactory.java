/////////////////////////////////////////////////////////////////////////////
//
// Project   DHL-ParcelOnlinePostage
//
// Author    roger@micromata.de
// Created   09.11.2009
// Copyright Micromata 09.11.2009
//
/////////////////////////////////////////////////////////////////////////////
package de.micromata.genome.gwiki.page.impl.wiki;

public interface GWikiMacroFactory
{
  GWikiMacro createInstance();

  boolean hasBody();

  boolean evalBody();

  /**
   * Can be transformed for rich text edit and back.
   * 
   * @return
   */
  boolean isRteMacro();
}
