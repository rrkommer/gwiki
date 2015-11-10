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

package de.micromata.genome.gwiki.controls;

import de.micromata.genome.gwiki.auth.GWikiSimpleUser;
import de.micromata.genome.gwiki.auth.GWikiSimpleUserAuthorization;
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
    // singleUser = new GWikiSimpleUser(GWikiSimpleUserAuthorization.getSingleUser(wikiContext));
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
      wikiContext.addSimpleValidationError(translate("gwiki.controls.wikicontrol.rightrule.error", ex.getMessage()));
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
