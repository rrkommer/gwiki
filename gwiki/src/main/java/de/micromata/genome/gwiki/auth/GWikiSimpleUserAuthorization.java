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

import java.security.MessageDigest;

import org.apache.commons.lang3.StringUtils;

import de.micromata.genome.gwiki.model.AuthorizationFailedException;
import de.micromata.genome.gwiki.model.GWikiElement;
import de.micromata.genome.gwiki.page.GWikiContext;
import de.micromata.genome.gwiki.umgmt.GWikiUserServeElementFilterEvent;
import de.micromata.genome.util.matcher.EqualsMatcher;
import de.micromata.genome.util.matcher.Matcher;
import de.micromata.genome.util.matcher.OrMatcher;
import de.micromata.genome.util.runtime.CallableX;
import de.micromata.genome.util.types.Converter;

/**
 * Very basic implementation of the GWikiAuthorizationBase with users held by spring context file.
 * 
 * @author Roger Rene Kommer (r.kommer@micromata.de)
 * 
 */
public class GWikiSimpleUserAuthorization extends GWikiAuthorizationBase
{

  public static final String SINGLEUSER_SESSION_KEY = "de.micromata.genome.gwiki.model.SingleUser";

  public static GWikiSimpleUserConfig defaultConfig;

  static {
    defaultConfig = new GWikiSimpleUserConfig();
    // defaultUsers = new HashMap<String, GWikiSimpleUser>();
    defaultConfig.getUsers().put("gwikisu", new GWikiSimpleUser("gwikisu", "gwiki", "genome@micromata.de", "+*"));
    defaultConfig.getUsers().put("anon", new GWikiSimpleUser("anon", "anon", "genome@micromata.de", "GWIKI_VIEWPAGES"));
    defaultConfig.getUsers().put("gwikiadmin",
        new GWikiSimpleUser("gwikiadmin", "gwiki", "genome@micromata.de", "+*,-GWIKI_DEVELOPER"));
    defaultConfig.getUsers()
        .put("gwikideveloper",
            new GWikiSimpleUser("gwikideveloper", "gwiki", "genome@micromata.de", "+*,-GWIKI_ADMIN"));
  }

  private boolean recursiveLogGuard = false;

  @Override
  public <T> T runAsSu(GWikiContext wikiContext, CallableX<T, RuntimeException> callback)
  {
    GWikiSimpleUser su = GWikiUserServeElementFilterEvent.getUser();
    try {
      GWikiUserServeElementFilterEvent.setUser(defaultConfig.getUser("gwikisu"));
      return callback.call();
    } finally {
      GWikiUserServeElementFilterEvent.setUser(su);
    }
  }

  @Override
  public <T> T runAsUser(String user, GWikiContext wikiContext, CallableX<T, RuntimeException> callback)
  {
    GWikiSimpleUser su = defaultConfig.getUser(user);
    if (su == null) {
      throw new AuthorizationFailedException("User doesn't exits: " + user);
    }
    GWikiSimpleUser pu = GWikiUserServeElementFilterEvent.getUser();
    try {
      GWikiUserServeElementFilterEvent.setUser(su);
      return callback.call();
    } finally {
      GWikiUserServeElementFilterEvent.setUser(pu);
    }
  }

  private Matcher<String> addRights(Matcher<String> rightsMatcher, String[] rights)
  {
    Matcher<String> ret = rightsMatcher;
    for (String r : rights) {
      ret = new OrMatcher<String>(new EqualsMatcher<String>(r), ret);
    }
    return ret;

  }

  @Override
  public <T> T runWithRights(GWikiContext wikiContext, String[] addRights, CallableX<T, RuntimeException> callback)
  {
    GWikiSimpleUser su = GWikiUserServeElementFilterEvent.getUser();

    try {
      GWikiSimpleUser cu = new GWikiSimpleUser(su);
      cu.setRightsMatcher(addRights(cu.getRightsMatcher(), addRights));
      return callback.call();
    } finally {
      GWikiUserServeElementFilterEvent.setUser(su);
    }
  }

  @Override
  public boolean needAuthorization(GWikiContext ctx)
  {
    return ctx.getSessionAttribute(SINGLEUSER_SESSION_KEY) == null;
  }

  @Override
  public boolean initThread(GWikiContext wikiContext)
  {
    GWikiSimpleUser su = getSingleUser(wikiContext);
    if (su == null/* || StringUtils.equals(su.getUser(), "anon") == true */) {
      return false;
    }
    GWikiUserServeElementFilterEvent.setUser(su);
    return true;
  }

  @Override
  public void clearThread(GWikiContext ctx)
  {
    GWikiUserServeElementFilterEvent.setUser(null);
  }

  public static GWikiSimpleUserConfig getConfig(GWikiContext ctx)
  {
    if (ctx.getWikiWeb() == null) {
      return null;
    }
    GWikiElement el = ctx.getWikiWeb().findElement("admin/config/GWikiUsers");
    if (el == null) {
      return defaultConfig;
    }
    GWikiSimpleUserConfig config = (GWikiSimpleUserConfig) el.getMainPart().getCompiledObject();
    if (config == null) {
      return defaultConfig;
    }
    return config;

  }

  protected GWikiSimpleUser loginPredefined(GWikiContext ctx, String user, String password)
  {
    GWikiSimpleUser su = defaultConfig.getUser(user);
    if (su == null) {
      return null;
    }
    if (isPasswordCorrect(su, password) == true) {
      return su;
    }
    return null;
  }

  private boolean isPasswordCorrect(GWikiSimpleUser user, String password)
  {
    String crp = encrypt(password);
    if (StringUtils.equals(crp, user.getPassword()) == true) {
      return true;
    }
    if (StringUtils.equals(password, user.getPassword()) == true) {
      return true;
    }
    return false;
  }

  @Override
  @Deprecated
  public boolean login(GWikiContext ctx, String user, String password)
  {
    GWikiSimpleUser su = getConfig(ctx).getUser(user);
    if (su == null) {
      GWikiSimpleUserConfig userConfig = getConfig(ctx);
      if (userConfig == null) {
        su = loginPredefined(ctx, user, password);
      }
      if (su == null) {
        return false;
      }
    } else {
      String crp = encrypt(password);
      if (isPasswordCorrect(su, password) == false) {
        return false;
      }
    }
    afterLogin(ctx, su);
    setSingleUser(ctx, su);
    return true;
  }

  @Override
  public void logout(GWikiContext ctx)
  {
    clearAuthenticationCookie(ctx, null);
    GWikiSimpleUser su = getSingleUser(ctx);
    if (su != null) {
      if (ctx.getWikiWeb().getFilter().onLogout(ctx, su) == false) {
        return;
      }
    }
    ctx.removeSessionAttribute(SINGLEUSER_SESSION_KEY);

    clearSession(ctx);
  }

  @Override
  public boolean isCurrentAnonUser(GWikiContext ctx)
  {
    return getSingleUser(ctx).isAnon();
  }

  @Override
  public GWikiSimpleUser findUser(GWikiContext wikiContext, String userName)
  {
    return getConfig(wikiContext).getUser("anon");
  }

  public GWikiSimpleUser getSingleUser(GWikiContext wikiContext)
  {
    GWikiSimpleUser su = GWikiUserServeElementFilterEvent.getUser();
    if (su != null) {
      return su;
    }
    su = (GWikiSimpleUser) wikiContext.getSessionAttribute(SINGLEUSER_SESSION_KEY);
    if (su != null) {
      GWikiUserServeElementFilterEvent.setUser(su);
      return su;
    }
    if (wikiContext.getWikiWeb() != null) {
      su = findUserByAuthenticationToken(wikiContext);
      if (su != null) {
        wikiContext.createSession();
        wikiContext.setSessionAttribute(SINGLEUSER_SESSION_KEY, su);
        GWikiUserServeElementFilterEvent.setUser(su);
        return su;
      }
      su = findUser(wikiContext, GWikiSimpleUser.ANON_USER_NAME);
      if (su == null) {
        su = defaultConfig.getUser(GWikiSimpleUser.ANON_USER_NAME);
      }
      if (su != null) {
        GWikiSimpleUser anon = new GWikiSimpleUser(su);
        wikiContext.setSessionAttribute(SINGLEUSER_SESSION_KEY, anon);
        GWikiUserServeElementFilterEvent.setUser(anon);
      }
    }
    return su;
  }

  @Override
  public void reloadUser(GWikiContext wikiContext)
  {
    String userName = wikiContext.getWikiWeb().getAuthorization().getCurrentUserName(wikiContext);
    if (StringUtils.isEmpty(userName) == true) {
      return;
    }
    GWikiSimpleUser su = findUser(wikiContext, userName);
    if (su != null) {
      setSingleUser(wikiContext, su);
      GWikiUserServeElementFilterEvent.setUser(su);
    }
  }

  public static void setSingleUser(GWikiContext ctx, GWikiSimpleUser su)
  {
    ctx.createSession();
    ctx.setSessionAttribute(SINGLEUSER_SESSION_KEY, su);
  }

  @Override
  public String getCurrentUserEmail(GWikiContext ctx)
  {
    return getSingleUser(ctx).getEmail();
  }

  @Override
  public String getCurrentUserName(GWikiContext ctx)
  {
    if (recursiveLogGuard == true) {
      return "anon";
    }
    try {
      recursiveLogGuard = true;
      return getSingleUser(ctx).getUser();
    } finally {
      recursiveLogGuard = false;
    }

  }

  @Override
  public boolean isAllowTo(GWikiContext ctx, String right)
  {
    if (StringUtils.isBlank(right) == true) {
      return true;
    }

    GWikiSimpleUser su = getSingleUser(ctx);
    if (su == null) {
      return false;
    }
    if (su.getRightsMatcher() == null) {
      return false;
    }
    return su.getRightsMatcher().match(right);
  }

  @Override
  public String getUserProp(GWikiContext ctx, String key)
  {
    String val = ctx.getCookie(key);
    if (val != null) {
      return val;
    }
    GWikiSimpleUser su = getSingleUser(ctx);
    if (su == null) {
      return null;
    }
    return su.getProps().get(key);
  }

  /**
   * Encodes with a SHA-Hash
   * 
   * TODO RK use !salting!
   * 
   * @return encoded value as BASE64
   */
  public static String encrypt(String plaintext)
  {
    try {
      MessageDigest md = MessageDigest.getInstance("SHA");
      md.update(plaintext.getBytes("UTF-8"));
      byte raw[] = md.digest();
      String hash = Converter.encodeBase64(raw);
      return hash;
    } catch (Exception ex) {
      /**
       * @logging
       * @reason Beim asymetrischen verschluesssseln ist ein Fehler aufgetreten.
       * @action Check Java -Installation
       */
      throw new RuntimeException("Error while executing hashing encryption: " + ex.getMessage(), ex);
    }
  }

  /**
   * 
   * @param plainText
   * @return possible combinations of password.
   */
  public static long getPasswortCombinations(String plainText)
  {
    if (plainText == null) {
      return 0;
    }
    int r = 0;
    boolean lc = false;
    boolean uc = false;
    boolean dig = false;
    boolean other = false;
    char[] chars = plainText.toCharArray();
    for (int c : chars) {
      if (Character.isLowerCase(c) == true) {
        if (lc == false) {
          r += 26;
        }
        lc = true;
      } else if (Character.isUpperCase(c) == true) {
        if (uc == false) {
          r += 26;
          uc = true;
        }
      } else if (Character.isDigit(c) == true) {
        if (dig == false) {
          r += 10;
          dig = true;
        }
      } else {
        if (other == false) {
          r += 50;
          other = true;
        }
      }
    }
    return (long) Math.pow(r, plainText.length());
  }

  /**
   * 
   * @param plainText
   * @return password crack possibiltity in percent
   */
  public static int rankPasswort(String plainText)
  {
    long combinations = getPasswortCombinations(plainText);
    // asume 20 passwort hacks per second
    long timeInDays = combinations / (400L * 86400L);
    long lifetime = 365;
    return (int) (((double) timeInDays / (double) lifetime) * 100);
  }
}
