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

import java.security.SecureRandom;
import java.util.Locale;
import java.util.Random;

import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;

import de.micromata.genome.gwiki.model.AuthorizationFailedException;
import de.micromata.genome.gwiki.model.GWikiAuthorization;
import de.micromata.genome.gwiki.model.GWikiAuthorizationRights;
import de.micromata.genome.gwiki.model.GWikiElementInfo;
import de.micromata.genome.gwiki.model.GWikiPropKeys;
import de.micromata.genome.gwiki.model.logging.GWikiLogCategory;
import de.micromata.genome.gwiki.page.GWikiContext;
import de.micromata.genome.gwiki.umgmt.GWikiUserServeElementFilterEvent;
import de.micromata.genome.logging.GLog;
import de.micromata.genome.logging.GenomeAttributeType;
import de.micromata.genome.logging.LogAttribute;
import de.micromata.genome.util.runtime.CallableX;
import de.micromata.genome.util.types.Pair;

/**
 * Implementation of common methods of GWikiAuthorization.
 * 
 * @author Roger Rene Kommer (r.kommer@micromata.de)
 * 
 */
public abstract class GWikiAuthorizationBase implements GWikiAuthorization, GWikiPropKeys
{
  public static final String COOKIE_STAY_LOGIN_TOKEN = "gwikistaylogintk";
  private boolean generalPublicEdit = false;

  private boolean generalPublicView = false;

  @Override
  public boolean afterLogin(GWikiContext ctx, GWikiSimpleUser su)
  {
    GWikiSimpleUserAuthorization.setSingleUser(ctx, su);
    GWikiUserServeElementFilterEvent.setUser(su);
    if (ctx.getWikiWeb().getFilter().onLogin(ctx, su) == false) {
      GWikiUserServeElementFilterEvent.setUser(null);
      return false;
    }
    GLog.note(GWikiLogCategory.Wiki, "User logged in: " + su.getUser());
    return true;
  }

  @Override
  public void setUserProp(GWikiContext ctx, String key, String value, UserPropStorage storage)
  {

    switch (storage) {
      case Client:
        setUserPropInCookie(ctx, key, value);
        break;
      case Server:
        break;
      case Transient:
        break;

    }
  }

  public boolean canStoreCookie(GWikiContext ctx)
  {
    if (ctx.getWikiWeb().getAuthorization().isCurrentAnonUser(ctx) == false) {
      return true;
    }
    if (ctx.getWikiWeb().getWikiConfig().allowAnonCookies() == true) {
      return true;
    }
    if ("true".equals(ctx.getCookie("gwikiAllowStoreCookies", "false")) == true) {
      return true;
    }
    return false;
  }

  protected void setUserPropInCookie(GWikiContext ctx, String key, String value)
  {
    if (canStoreCookie(ctx) == false) {
      return;
    }

    ctx.setCookie(key, value);
  }

  @Override
  public void createAuthenticationCookie(GWikiContext ctx, String user, String password)
  {
    Pair<String, String> token = createAuthToken(ctx, user, password);
    setUserProp(ctx, COOKIE_STAY_LOGIN_TOKEN, token.getSecond(), UserPropStorage.Server);
    setUserProp(ctx, COOKIE_STAY_LOGIN_TOKEN, token.getFirst(), UserPropStorage.Client);
  }

  @Override
  public void clearAuthenticationCookie(GWikiContext ctx, String user)
  {
    setUserProp(ctx, COOKIE_STAY_LOGIN_TOKEN, "", UserPropStorage.Server);
    setUserProp(ctx, COOKIE_STAY_LOGIN_TOKEN, "", UserPropStorage.Client);
  }

  protected GWikiSimpleUser findUserByAuthenticationToken(GWikiContext ctx)
  {
    String tk = ctx.getCookie(COOKIE_STAY_LOGIN_TOKEN, null);
    if (StringUtils.isBlank(tk) == true) {
      return null;
    }
    String[] tks = StringUtils.split(tk, ':');
    if (tks.length < 2) {
      return null;
    }
    String userName = tks[0];
    GWikiSimpleUser user = findUser(ctx, userName);
    if (user == null) {
      return null;
    }
    String st = user.getProps().get(COOKIE_STAY_LOGIN_TOKEN);
    if (StringUtils.equals(tks[1], st) == true) {
      GLog.note(GWikiLogCategory.Wiki, "User autologin from cookie: " + userName,
          new LogAttribute(GenomeAttributeType.AdminUserName, userName));
      return user;
    }
    return null;
  }

  public GWikiSimpleUser findUser(GWikiContext wikiContext, String userName)
  {
    return null;
  }

  private Pair<String, String> createAuthToken(GWikiContext ctx, String user, String password)
  {
    int tklength = 16;
    Random r = new SecureRandom();
    String internalToken = RandomStringUtils.random(tklength, 32, 127, true, true, null, r);
    return Pair.make(user + ":" + internalToken, internalToken);

  }

  public void ensureAllowTo(GWikiContext ctx, String right, GWikiElementInfo el)
  {
    if (isAllowTo(ctx, right) == true) {
      return;
    }
    AuthorizationFailedException.failRight(ctx, right, el);
  }

  @Override
  public void ensureAllowTo(GWikiContext ctx, String right)
  {
    if (isAllowTo(ctx, right) == true) {
      return;
    }
    AuthorizationFailedException.failRight(ctx, right);
  }

  // TODO gwiki noch nicht richtig
  @Override
  public boolean isAllowToCreate(GWikiContext ctx, GWikiElementInfo ei)
  {
    if (generalPublicEdit == true) {
      return true;
    }
    if (isAllowToEdit(ctx, ei) == false) {
      return false;
    }

    if (ei.getMetaTemplate() != null && ei.getMetaTemplate().getRequiredEditRight() != null) {
      if (isAllowTo(ctx, ei.getMetaTemplate().getRequiredEditRight()) == false) {
        return false;
      }
    }
    if (isAllowTo(ctx, GWikiAuthorizationRights.GWIKI_CREATEPAGES.name()) == false) {
      return false;
    }
    // TODO gwiki ch)eck if parent has right to edit.
    return true;
  }

  public boolean getEditRightFromTemplate(GWikiContext ctx, GWikiElementInfo ei)
  {
    if (ei.getMetaTemplate() != null && ei.getMetaTemplate().getRequiredEditRight() != null) {
      if (isAllowTo(ctx, ei.getMetaTemplate().getRequiredEditRight()) == false) {
        return false;
      }
    }
    return true;
  }

  @Override
  public boolean isAllowToEdit(GWikiContext ctx, GWikiElementInfo ei)
  {
    if (generalPublicEdit == true) {
      return true;
    }
    Boolean cp = checkPageSpecificRight(ctx, AUTH_EDIT, ei);
    if (cp != null) {
      return (cp) && getEditRightFromTemplate(ctx, ei) == true;
    }

    boolean hasEditRight = isAllowTo(ctx, GWikiAuthorizationRights.GWIKI_EDITPAGES.name());
    if (hasEditRight == false) {
      return false;
    }
    return getEditRightFromTemplate(ctx, ei);

  }

  protected String getMetaTemplateRight(GWikiContext ctx, GWikiElementInfo ei, String pageRight)
  {
    if (ei.getMetaTemplate() == null) {
      return null;
    }
    if (GWikiPropKeys.AUTH_VIEW.equals(pageRight) == true) {
      return ei.getMetaTemplate().getRequiredViewRight();
    } else if (GWikiPropKeys.AUTH_EDIT.equals(pageRight) == true) {
      return ei.getMetaTemplate().getRequiredEditRight();
    } else {
      return null;
    }
  }

  @Override
  public String getEffectiveRight(GWikiContext ctx, GWikiElementInfo ei, String pageRight)
  {
    if (generalPublicView == true) {
      return GWikiAuthorizationRights.GWIKI_PUBLIC.name();
    }
    String r = ei.getProps().getStringValue(pageRight);
    if (StringUtils.isNotEmpty(r) == true) {
      return r;
    }
    r = getMetaTemplateRight(ctx, ei, pageRight);
    if (StringUtils.isNotEmpty(r) == true) {
      return r;
    }
    GWikiElementInfo pi = ei.getParent(ctx);
    if (pi != null) {
      return getEffectiveRight(ctx, pi, pageRight);
    }
    if (GWikiPropKeys.AUTH_VIEW.equals(pageRight) == true) {
      return GWikiAuthorizationRights.GWIKI_VIEWPAGES.name();
    } else if (GWikiPropKeys.AUTH_EDIT.equals(pageRight) == true) {
      return GWikiAuthorizationRights.GWIKI_EDITPAGES.name();
    } else {
      return "";
    }

  }

  @Override
  public boolean isAllowToView(GWikiContext ctx, GWikiElementInfo ei)
  {
    if (generalPublicView == true) {
      return true;
    }

    if (ei.isViewable() == false) {
      return false;
    }
    Boolean pc = checkPageSpecificRight(ctx, AUTH_VIEW, ei);
    if (pc != null) {
      return pc;
    }
    if (isAllowTo(ctx, GWikiAuthorizationRights.GWIKI_VIEWPAGES.name()) == false) {
      return false;
    }

    if (ei.getMetaTemplate() != null && ei.getMetaTemplate().getRequiredViewRight() != null) {
      if (isAllowTo(ctx, ei.getMetaTemplate().getRequiredViewRight()) == false) {
        return false;
      }
    }
    return true;
  }

  protected Boolean checkPageSpecificRight(GWikiContext ctx, String propKey, GWikiElementInfo ei)
  {
    String r = ei.getProps().getStringValue(propKey);
    if (StringUtils.isEmpty(r) == true) {
      if (ei.getParentId() != null) {
        GWikiElementInfo pei = ctx.getWikiWeb().findElementInfo(ei.getParentId());
        if (pei != null && pei != ei) {
          return checkPageSpecificRight(ctx, propKey, pei);
        }
      }
      return null;
    }

    if (r.equals(GWikiAuthorizationRights.GWIKI_PRIVATE.name()) == true) {
      if (isAllowTo(ctx, GWikiAuthorizationRights.GWIKI_ADMIN.name()) == true) {
        return true;
      }
      if (StringUtils.equals(getCurrentUserName(ctx), ei.getCreatedBy()) == true) {
        return true;
      }
      return false;
    } else if (r.equals(GWikiAuthorizationRights.GWIKI_PUBLIC.name()) == true) {
      return true;
    } else if (r.equals(GWikiAuthorizationRights.GWIKI_DISALLOW.name()) == true) {
      return false;
    }
    return isAllowTo(ctx, r);
  }

  public Locale getLocaleByLang(String lang)
  {
    if (StringUtils.isEmpty(lang) == true) {
      return null;
    }
    if (lang.equals("en") == true || lang.equals("eng") == true) {
      return Locale.ENGLISH;
    } else if (lang.equals("de") == true || lang.equals("deu") == true) {
      return Locale.GERMAN;
    }
    return new Locale(lang);
  }

  @Override
  public Locale getCurrentUserLocale(GWikiContext ctx)
  {
    String lang = getUserProp(ctx, USER_LANG);
    if (StringUtils.isBlank(lang) == false) {
      Locale loc = getLocaleByLang(lang);
      if (loc != null) {
        return loc;
      }
    }
    return ctx.getRequest().getLocale();
  }

  protected void clearSession(GWikiContext wikiContext)
  {
    wikiContext.getWikiWeb().getSessionProvider().clearSessionAttributes(wikiContext);
  }

  @Override
  public boolean runIfAuthentificated(GWikiContext wikiContext, CallableX<Void, RuntimeException> callback)
  {
    if (initThread(wikiContext) == true) {
      return false;
    }
    try {
      callback.call();
    } finally {
      clearThread(wikiContext);
    }
    return true;
  }

  @Override
  public <T> T runWithRight(GWikiContext wikiContext, String addRight, CallableX<T, RuntimeException> callback)
  {
    String[] rights = new String[1];
    rights[0] = addRight;
    return runWithRights(wikiContext, rights, callback);
  }

}
