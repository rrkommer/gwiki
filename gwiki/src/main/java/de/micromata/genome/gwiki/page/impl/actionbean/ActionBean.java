/////////////////////////////////////////////////////////////////////////////
//
// Project   DHL-ParcelOnlinePostage
//
// Author    roger@micromata.de
// Created   09.11.2009
// Copyright Micromata 09.11.2009
//
/////////////////////////////////////////////////////////////////////////////
package de.micromata.genome.gwiki.page.impl.actionbean;

import de.micromata.genome.gwiki.page.GWikiContext;

public interface ActionBean
{
  public GWikiContext getWikiContext();

  public void setWikiContext(GWikiContext wikiContext);

  public String getRequestPrefix();

  public Object onInit();
}
