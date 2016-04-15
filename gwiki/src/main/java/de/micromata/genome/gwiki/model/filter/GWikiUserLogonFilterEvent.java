//
// Copyright (C) 2010-2016 Roger Rene Kommer & Micromata GmbH
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//  http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.
//

package de.micromata.genome.gwiki.model.filter;

import de.micromata.genome.gwiki.auth.GWikiSimpleUser;
import de.micromata.genome.gwiki.page.GWikiContext;

/**
 * The Class GWikiUserLogonFilterEvent.
 *
 * @author Roger Rene Kommer (r.kommer@micromata.de)
 */
public class GWikiUserLogonFilterEvent extends GWikiFilterEvent
{

  /**
   * The Enum LoginState.
   */
  public static enum LoginState
  {

    /**
     * The Login.
     */
    Login,
    /**
     * The Logout.
     */
    //
    Logout //
    ;
  }

  /**
   * Status of login.
   */
  private LoginState loginState;

  /**
   * The user.
   */
  private GWikiSimpleUser user;

  /**
   * Set by filter to true if process should be aborted.
   */
  private boolean abort = false;

  /**
   * Instantiates a new g wiki user logon filter event.
   *
   * @param wikiContext the wiki context
   * @param loginState the login state
   * @param user the user
   */
  public GWikiUserLogonFilterEvent(GWikiContext wikiContext, LoginState loginState, GWikiSimpleUser user)
  {
    super(wikiContext);
    this.loginState = loginState;
    this.user = user;
  }

  public LoginState getLoginState()
  {
    return loginState;
  }

  public void setLoginState(LoginState loginState)
  {
    this.loginState = loginState;
  }

  public GWikiSimpleUser getUser()
  {
    return user;
  }

  public void setUser(GWikiSimpleUser user)
  {
    this.user = user;
  }

  public boolean isAbort()
  {
    return abort;
  }

  public void setAbort(boolean abort)
  {
    this.abort = abort;
  }

}
