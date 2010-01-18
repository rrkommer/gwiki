/////////////////////////////////////////////////////////////////////////////
//
// Project   DHL-ParcelOnlinePostage
//
// Author    roger@micromata.de
// Created   26.11.2009
// Copyright Micromata 26.11.2009
//
/////////////////////////////////////////////////////////////////////////////
package de.micromata.genome.gwiki.page.impl.i18n;

import java.util.Enumeration;
import java.util.Locale;
import java.util.ResourceBundle;

import de.micromata.genome.gwiki.page.GWikiContext;

public class GWikiI18nResourcenBundle extends ResourceBundle
{
  // TODO gwiki never used.
  private Locale locale;

  private GWikiContext wikiContext;

  public GWikiI18nResourcenBundle(GWikiContext wikiContext, Locale locale)
  {
    this.locale = locale;
    this.wikiContext = wikiContext;
  }

  @Override
  public Enumeration<String> getKeys()
  {
    // TODO gwiki?
    // wikiContext.getI18nMaps();
    return null;
  }

  @Override
  protected Object handleGetObject(String key)
  {
    return wikiContext.getWikiWeb().getI18nProvider().translate(wikiContext, key);
  }

}
