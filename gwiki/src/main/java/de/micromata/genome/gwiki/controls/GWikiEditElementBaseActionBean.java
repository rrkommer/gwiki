/////////////////////////////////////////////////////////////////////////////
//
// Project   DHL-ParcelOnlinePostage
//
// Author    roger@micromata.de
// Created   22.10.2009
// Copyright Micromata 22.10.2009
//
/////////////////////////////////////////////////////////////////////////////
package de.micromata.genome.gwiki.controls;

import de.micromata.genome.gwiki.page.impl.actionbean.ActionBeanBase;

/**
 * Base action bean for editing Elements.
 * 
 * @author Roger Rene Kommer (r.kommer@micromata.de)
 * 
 */
public class GWikiEditElementBaseActionBean extends ActionBeanBase
{
  protected boolean newPage = false;

  protected String parentPageId;

  protected String pageId;

  public boolean isNewPage()
  {
    return newPage;
  }

  public void setNewPage(boolean newPage)
  {
    this.newPage = newPage;
  }

  public String getParentPageId()
  {
    return parentPageId;
  }

  public void setParentPageId(String parentPageId)
  {
    this.parentPageId = parentPageId;
  }

  public String getPageId()
  {
    return pageId;
  }

  public void setPageId(String pageId)
  {
    this.pageId = pageId;
  }


}
