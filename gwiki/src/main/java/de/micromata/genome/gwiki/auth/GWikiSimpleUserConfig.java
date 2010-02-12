/////////////////////////////////////////////////////////////////////////////
//
// Project   DHL-ParcelOnlinePostage
//
// Author    roger@micromata.de
// Created   08.11.2009
// Copyright Micromata 08.11.2009
//
/////////////////////////////////////////////////////////////////////////////
package de.micromata.genome.gwiki.auth;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.InitializingBean;

/**
 * Spring context bean holding GWikiSimpleUser.
 * 
 * @author Roger Rene Kommer (r.kommer@micromata.de)
 * 
 */
public class GWikiSimpleUserConfig implements InitializingBean, Serializable
{

  private static final long serialVersionUID = 4086622875434519609L;

  Map<String, GWikiSimpleUser> users = new HashMap<String, GWikiSimpleUser>();

  public Map<String, GWikiSimpleUser> getUsers()
  {
    return users;
  }

  public void setUsers(Map<String, GWikiSimpleUser> users)
  {
    this.users = users;
  }

  public GWikiSimpleUser getUser(String name)
  {
    return users.get(name);
  }

  public void afterPropertiesSet() throws Exception
  {
    for (Map.Entry<String, GWikiSimpleUser> me : users.entrySet()) {
      me.getValue().setUser(me.getKey());
    }

  }

}
