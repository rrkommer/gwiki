/////////////////////////////////////////////////////////////////////////////
//
// Project   DHL-ParcelOnlinePostage
//
// Author    roger@micromata.de
// Created   25.11.2009
// Copyright Micromata 25.11.2009
//
/////////////////////////////////////////////////////////////////////////////
package de.micromata.genome.gwiki.model;

import de.micromata.genome.gwiki.page.GWikiContext;

/**
 * Provides interationalization.
 * 
 * @author Roger Rene Kommer (r.kommer@micromata.de)
 * 
 */
public interface GWikiI18nProvider
{
  public void addTranslationElement(GWikiContext ctx, String pageId);

  public String translate(GWikiContext ctx, String key);

  /**
   * key kann contains I${} values, which will replaced with text.
   * 
   * @param ctx
   * @param key
   * @return
   */
  public String translateProp(GWikiContext ctx, String key);

  public String translate(GWikiContext ctx, String key, String defaultValue);

  public String translate(GWikiContext ctx, String key, String defaultValue, Object... args);

}
