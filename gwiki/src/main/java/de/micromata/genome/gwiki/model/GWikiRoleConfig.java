////////////////////////////////////////////////////////////////////////////
// 
// Copyright (C) 2010-2013 Micromata GmbH / Roger Rene Kommer
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
package de.micromata.genome.gwiki.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.InitializingBean;

import de.micromata.genome.util.matcher.BooleanListRulesFactory;

/**
 * @author Roger Rene Kommer (r.kommer@micromata.de)
 * 
 */
public class GWikiRoleConfig implements InitializingBean, Serializable
{

  private static final long serialVersionUID = 2957213892492786768L;

  /**
   * set via bean.
   */
  private List<GWikiRight> rights = new ArrayList<GWikiRight>();

  /**
   * Set implizit via afterPropertiesSet()
   */
  private Map<String, GWikiRight> roles = new HashMap<String, GWikiRight>();

  /*
   * (non-Javadoc)
   * 
   * @see org.springframework.beans.factory.InitializingBean#afterPropertiesSet()
   */
  public void afterPropertiesSet() throws Exception
  {
    BooleanListRulesFactory<String> fac = new BooleanListRulesFactory<String>();
    for (GWikiRight r : rights) {
      if (roles.containsKey(r.getName()) == true) {
        throw new Exception("Duplicated Right: " + r.getName() + " in RoleConfig");
      }
      if (StringUtils.isNotBlank(r.getDefinition()) == true) {
        r.setDefinitionRule(fac.createMatcher(r.getDefinition()));
      }
      roles.put(r.getName(), r);
    }
  }

  public void addRight(GWikiRight right)
  {
    if (roles.containsKey(right.getName()) == true) {
      return;
    }
    if (StringUtils.isNotEmpty(right.getDefinition()) == true && right.getDefinitionRule() == null) {
      BooleanListRulesFactory<String> fac = new BooleanListRulesFactory<String>();
      right.setDefinitionRule(fac.createMatcher(right.getDefinition()));
    }
    roles.put(right.getName(), right);
    rights.add(right);
  }

  public void removeRight(GWikiRight right)
  {
    roles.remove(right.getName());
  }

  public List<GWikiRight> getRights()
  {
    return rights;
  }

  public void setRights(List<GWikiRight> rights)
  {
    this.rights = rights;
  }

  public Map<String, GWikiRight> getRoles()
  {
    return roles;
  }

  public void setRoles(Map<String, GWikiRight> roles)
  {
    this.roles = roles;
  }

}
