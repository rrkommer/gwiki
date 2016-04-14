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

package de.micromata.genome.gwiki.auth;

import de.micromata.genome.gwiki.model.GWikiAuthorizationExt;
import de.micromata.genome.gwiki.page.GWikiContext;
import de.micromata.genome.gwiki.umgmt.GWikiUserServeElementFilterEvent;
import de.micromata.genome.gwiki.utils.StringUtils;
import de.micromata.genome.util.matcher.CommonMatchers;
import de.micromata.genome.util.runtime.CallableX;
import de.micromata.genome.util.runtime.LocalSettings;

/**
 * Authentificate against local settings.
 *
 * @author Roger Rene Kommer (r.kommer.extern@micromata.de)
 */
public class GWikiSysUserAuthorization extends GWikiAuthorizationExtWrapper
{
  public static final String LS_GWIKI_SYS_USER = "gwiki.sys.user";
  public static final String LS_GWIKI_SYS_PASSWORDHASH = "gwiki.sys.passwordhash";

  public GWikiSysUserAuthorization()
  {
    super();
  }

  public GWikiSysUserAuthorization(GWikiAuthorizationExt parentExt)
  {
    super(parentExt);
  }

  @Override
  public boolean hasUser(GWikiContext wikiContext, String userName)
  {
    if (super.hasUser(wikiContext, userName) == true) {
      return true;
    }
    String sysuser = LocalSettings.get().get(LS_GWIKI_SYS_USER);
    String hash = LocalSettings.get().get(LS_GWIKI_SYS_PASSWORDHASH);
    if (StringUtils.isNotBlank(sysuser) == true && StringUtils.isNotBlank(hash) == true) {
      if (sysuser.equals(userName) == true) {
        return true;
      }
    }
    return false;
  }

  protected GWikiSimpleUser createSystemUser(GWikiContext ctx, String userName)
  {
    GWikiSimpleUser user = new GWikiSimpleUser();
    user.setUser(userName);
    String email = LocalSettings.get().get("gwiki.public.email");
    user.setEmail(email);
    user.setRightsMatcher(CommonMatchers.always());
    return user;
  }

  @Override
  public boolean login(GWikiContext ctx, String user, String password)
  {
    if (super.hasUser(ctx, user) == true) {
      return super.login(ctx, user, password);
    }
    String sysuser = LocalSettings.get().get(LS_GWIKI_SYS_USER);
    String hash = LocalSettings.get().get(LS_GWIKI_SYS_PASSWORDHASH);
    if (StringUtils.isNotBlank(sysuser) == true && StringUtils.isNotBlank(hash) == true) {
      if (PasswordUtils.checkPassword(password, hash) == true) {
        GWikiSimpleUser su = createSystemUser(ctx, user);
        return afterLogin(ctx, su);
      }
    }
    return super.login(ctx, user, password);
  }

  @Override
  public <T> T runAsUser(String user, GWikiContext wikiContext, CallableX<T, RuntimeException> callback)
  {
    if (super.hasUser(wikiContext, user) == true) {
      return super.runAsUser(user, wikiContext, callback);
    }
    String sysuser = LocalSettings.get().get(LS_GWIKI_SYS_USER);
    if (StringUtils.isNotBlank(sysuser) == true && StringUtils.equals(user, sysuser) == true) {
      GWikiSimpleUser pu = GWikiUserServeElementFilterEvent.getUser();
      GWikiSimpleUser su = createSystemUser(wikiContext, user);
      try {
        GWikiUserServeElementFilterEvent.setUser(su);
        return callback.call();
      } finally {
        GWikiUserServeElementFilterEvent.setUser(pu);
      }
    }
    return super.runAsUser(user, wikiContext, callback);

  }

}
