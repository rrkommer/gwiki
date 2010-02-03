/////////////////////////////////////////////////////////////////////////////
//
// Project   DHL-ParcelOnlinePostage
//
// Author    roger@micromata.de
// Created   01.12.2009
// Copyright Micromata 01.12.2009
//
/////////////////////////////////////////////////////////////////////////////
package de.micromata.genome.gwiki.model;

import java.io.Serializable;

import de.micromata.genome.gwiki.page.GWikiContext;

public interface GWikiSessionProvider
{
  Object getSessionAttribute(GWikiContext wikiContext, String key);

  void setSessionAttribute(GWikiContext wikiContext, String key, Serializable object);

  void removeSessionAttribute(GWikiContext wikiContext, String key);

  void clearSessionAttributes(GWikiContext wikiContext);
}
