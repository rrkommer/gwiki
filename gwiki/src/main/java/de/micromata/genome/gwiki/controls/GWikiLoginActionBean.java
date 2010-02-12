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

package de.micromata.genome.gwiki.controls;

import org.apache.commons.lang.StringUtils;

import de.micromata.genome.gwiki.model.GWikiElementInfo;
import de.micromata.genome.gwiki.page.impl.actionbean.ActionBeanBase;

/**
 * ActionBean for standard login dialog.
 * 
 * @author Roger Rene Kommer (r.kommer@micromata.de)
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
