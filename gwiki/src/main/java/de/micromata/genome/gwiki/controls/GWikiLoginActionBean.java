/////////////////////////////////////////////////////////////////////////////
//
// Project   DHL-ParcelOnlinePostage
//
// Author    roger@micromata.de
// Created   06.11.2009
// Copyright Micromata 06.11.2009
//
/////////////////////////////////////////////////////////////////////////////
package de.micromata.genome.gwiki.controls;

import org.apache.commons.lang.StringUtils;

import de.micromata.genome.gwiki.model.GWikiElementInfo;
import de.micromata.genome.gwiki.page.impl.actionbean.ActionBeanBase;

/**
 * ActionBean for standard login dialog.
 * 
 * @author roger@micromata.de
 * 
 */
public class GWikiLoginActionBean extends ActionBeanBase
{
  private String pageId;

  private String user;

  private String password;

  public Object onInit()
  {
    return null;
  }

  public Object onLogin()
  {
    if (StringUtils.isBlank(user) == true || StringUtils.isBlank(password) == true) {
      wikiContext.addSimpleValidationError("Sowohl Nutzer als auch Passwort sind erforderlich");
      return null;
    }

    boolean success = wikiContext.getWikiWeb().getAuthorization().login(wikiContext, StringUtils.trim(user), StringUtils.trim(password));
    if (success == false) {
      wikiContext.addSimpleValidationError("Unbekannter Nutzer oder Passwort");
      return null;
    }
    if (StringUtils.isBlank(pageId) == false) {
      GWikiElementInfo ei = wikiContext.getWikiWeb().findElementInfo(pageId);
      if (ei != null) {
        return ei;
      }
    }
    return wikiContext.getWikiWeb().getHomeElement(wikiContext);
  }

  public Object onLogout()
  {
    wikiContext.getWikiWeb().getAuthorization().logout(wikiContext);
    return null;
  }

  public String getUser()
  {
    return user;
  }

  public void setUser(String user)
  {
    this.user = user;
  }

  public String getPassword()
  {
    return password;
  }

  public void setPassword(String password)
  {
    this.password = password;
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
