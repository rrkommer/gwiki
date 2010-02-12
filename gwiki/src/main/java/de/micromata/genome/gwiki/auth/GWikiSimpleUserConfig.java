////////////////////////////////////////////////////////////////////////////
//
// Copyright (C) 2010 Micromata GmbH
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
// http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.
//
////////////////////////////////////////////////////////////////////////////

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
