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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import de.micromata.genome.gwiki.page.impl.actionbean.ActionBeanBase;
import de.micromata.genome.util.types.Pair;

/**
 * Base action bean for editing Elements.
 * 
 * @author roger@micromata.de
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
