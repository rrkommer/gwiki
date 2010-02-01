/////////////////////////////////////////////////////////////////////////////
//
// Project   DHL-ParcelOnlinePostage
//
// Author    roger@micromata.de
// Created   19.10.2009
// Copyright Micromata 19.10.2009
//
/////////////////////////////////////////////////////////////////////////////
package de.micromata.genome.gwiki.page.impl.actionbean;

import de.micromata.genome.gwiki.page.GWikiContext;

public class ActionBeanBase implements ActionBean
{
  protected GWikiContext wikiContext;

  /**
   * for private forms.
   * 
   * @return
   */
  public String getRequestPrefix()
  {
    return "";
  }

  public Object onInit()
  {
    return null;
  }

  public GWikiContext getWikiContext()
  {
    return wikiContext;
  }

  public void setWikiContext(GWikiContext wikiContext)
  {
    this.wikiContext = wikiContext;
  }

  public Object noForward()
  {
    return NoForward.class;
  }

  protected String getReqParam(String key)
  {
    return wikiContext.getRequestParameter(key);
  }
}
