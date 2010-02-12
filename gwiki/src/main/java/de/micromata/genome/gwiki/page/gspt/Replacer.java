/////////////////////////////////////////////////////////////////////////////
//
// Project   DHL-ParcelOnlinePostage
//
// Author    roger@micromata.de
// Created   18.11.2006
// Copyright Micromata 18.11.2006
//
/////////////////////////////////////////////////////////////////////////////
package de.micromata.genome.gwiki.page.gspt;

import java.util.Map;

/**
 * Implements a replacer to parse gspts.
 */
public interface Replacer
{
  /**
   * @param attr Attribute
   * @param isClosed wenn end == ">" && isClosed == true, dann "/>"
   */
  public String replace(ReplacerContext ctx, Map<String, String> attr, boolean isClosed);

  public String getStart();

  public String getEnd();

}
