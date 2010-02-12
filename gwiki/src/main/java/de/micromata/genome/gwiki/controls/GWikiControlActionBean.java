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

import de.micromata.genome.gwiki.auth.GWikiSimpleUserAuthorization;
import de.micromata.genome.gwiki.auth.GWikiSimpleUser;
import de.micromata.genome.gwiki.page.impl.actionbean.ActionBeanBase;

/**
 * Implementation now in WikiControlTemplate.groovy
 * 
 * @author Roger Rene Kommer (r.kommer@micromata.de)
 * 
 */
@Deprecated
public class GWikiControlActionBean extends ActionBeanBase
{
  private GWikiSimpleUser singleUser = new GWikiSimpleUser();

  private String authRightsRule;

  private void initialize()
  {
    singleUser = new GWikiSimpleUser(GWikiSimpleUserAuthorization.getSingleUser(wikiContext));
    authRightsRule = singleUser.getRightsMatcherRule();
  }

  public Object onInit()
  {
    initialize();
    return null;
  }

  public Object onReloadWeb()
  {
    wikiContext.getWikiWeb().reloadWeb();
    return null;
  }

  public Object onSetRights()
  {
    GWikiSimpleUserAuthorization auth = new GWikiSimpleUserAuthorization();

    try {
      singleUser.setRightsMatcherRule(authRightsRule);
    } catch (Exception ex) {
      wikiContext.addSimpleValidationError("Fehler in der RightRule: " + ex.getMessage());
      return null;
    }
    auth.setSingleUser(wikiContext, singleUser);
    wikiContext.getWikiWeb().getDaoContext().setAuthorization(auth);
    return null;
  }

  public String getAuthRightsRule()
  {
    return authRightsRule;
  }

  public void setAuthRightsRule(String authRightsRule)
  {
    this.authRightsRule = authRightsRule;
  }

  public GWikiSimpleUser getSingleUser()
  {
    return singleUser;
  }

  public void setSingleUser(GWikiSimpleUser user)
  {
    this.singleUser = user;
  }

}
