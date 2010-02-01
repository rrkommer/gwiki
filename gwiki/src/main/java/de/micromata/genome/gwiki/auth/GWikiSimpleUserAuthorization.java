/////////////////////////////////////////////////////////////////////////////
//
// Project   DHL-ParcelOnlinePostage
//
// Author    roger@micromata.de
// Created   03.11.2009
// Copyright Micromata 03.11.2009
//
/////////////////////////////////////////////////////////////////////////////
package de.micromata.genome.gwiki.auth;

import java.security.MessageDigest;

import org.apache.commons.lang.StringUtils;

import de.micromata.genome.gwiki.model.AuthorizationFailedException;
import de.micromata.genome.gwiki.model.GWikiElement;
import de.micromata.genome.gwiki.page.GWikiContext;
import de.micromata.genome.gwiki.umgmt.GWikiUserServeElementFilterEvent;
import de.micromata.genome.util.runtime.CallableX;
import de.micromata.genome.util.types.Converter;

/**
 * Very basic implementation of the GWikiAuthorizationBase with users held by spring context file.
 * 
 * @author roger@micromata.de
 * 
 */
public class GWikiSimpleUserAuthorization extends GWikiAuthorizationBase
{

  public static final String SINGLEUSER_SESSION_KEY = "de.micromata.genome.gwiki.model.SingleUser";

  public static GWikiSimpleUserConfig defaultConfig;

  // public static Map<String, GWikiSimpleUser> defaultUsers;
  static {
    defaultConfig = new GWikiSimpleUserConfig();
    // defaultUsers = new HashMap<String, GWikiSimpleUser>();
    defaultConfig.getUsers().put("gwikisu", new GWikiSimpleUser("gwikisu", "gwiki", "genome@micromata.de", "+*"));
    defaultConfig.getUsers().put("anon", new GWikiSimpleUser("anon", "anon", "genome@micromata.de", "GWIKI_VIEWPAGES"));
    defaultConfig.getUsers().put("gwikiadmin", new GWikiSimpleUser("gwikiadmin", "gwiki", "genome@micromata.de", "+*,-GWIKI_DEVELOPER"));
    defaultConfig.getUsers()
        .put("gwikideveloper", new GWikiSimpleUser("gwikideveloper", "gwiki", "genome@micromata.de", "+*,-GWIKI_ADMIN"));
  }

  public <T> T runAsSu(GWikiContext wikiContext, CallableX<T, RuntimeException> callback)
  {
    GWikiSimpleUser su = GWikiUserServeElementFilterEvent.CURRENT_USER.get();
    try {
      GWikiUserServeElementFilterEvent.CURRENT_USER.set(defaultConfig.getUser("gwikisu"));
      return callback.call();
    } finally {
      GWikiUserServeElementFilterEvent.CURRENT_USER.set(su);
    }
  }

  public <T> T runAsUser(String user, GWikiContext wikiContext, CallableX<T, RuntimeException> callback)
  {
    GWikiSimpleUser su = defaultConfig.getUser(user);
    if (su == null) {
      throw new AuthorizationFailedException("User doesn't exits: " + user);
    }
    GWikiSimpleUser pu = GWikiUserServeElementFilterEvent.CURRENT_USER.get();
    try {
      GWikiUserServeElementFilterEvent.CURRENT_USER.set(su);
      return callback.call();
    } finally {
      GWikiUserServeElementFilterEvent.CURRENT_USER.set(pu);
    }
  }

  public boolean needAuthorization(GWikiContext ctx)
  {
    return ctx.getSessionAttribute(SINGLEUSER_SESSION_KEY) == null;
  }

  public boolean initThread(GWikiContext wikiContext)
  {
    GWikiSimpleUser su = getSingleUser(wikiContext);
    if (su == null || StringUtils.equals(su.getUser(), "anon") == true) {
      return false;
    }
    GWikiUserServeElementFilterEvent.CURRENT_USER.set(su);
    return true;
  }

  public void clearThread(GWikiContext ctx)
  {
    GWikiUserServeElementFilterEvent.CURRENT_USER.set(null);
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
    if (StringUtils.equals(password, su.getPassword()) == true) {
      return su;
    }
    return null;
  }

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
      if (StringUtils.equals(crp, su.getPassword()) == false) {
        return false;
      }
    }
    setSingleUser(ctx, su);
    return true;
  }

  public void logout(GWikiContext ctx)
  {
    ctx.removeSessionAttribute(SINGLEUSER_SESSION_KEY);
    clearSession(ctx);
  }

  public static GWikiSimpleUser getSingleUser(GWikiContext ctx)
  {
    GWikiSimpleUser su = GWikiUserServeElementFilterEvent.CURRENT_USER.get();
    if (su != null) {
      return su;
    }
    su = (GWikiSimpleUser) ctx.getSessionAttribute(SINGLEUSER_SESSION_KEY);
    if (su == null && ctx.getWikiWeb() != null) {
      su = getConfig(ctx).getUser("anon");
    }
    return su;
  }

  public static void setSingleUser(GWikiContext ctx, GWikiSimpleUser su)
  {
    ctx.setSessionAttribute(SINGLEUSER_SESSION_KEY, su);
  }

  public String getCurrentUserEmail(GWikiContext ctx)
  {
    return getSingleUser(ctx).getEmail();
  }

  public String getCurrentUserName(GWikiContext ctx)
  {
    return getSingleUser(ctx).getUser();
  }

  public boolean isAllowTo(GWikiContext ctx, String right)
  {
    if (StringUtils.isBlank(right) == true)
      return true;

    return getSingleUser(ctx).getRightsMatcher().match(right);
  }

  public String getUserProp(GWikiContext ctx, String key)
  {
    GWikiSimpleUser su = getSingleUser(ctx);
    if (su == null) {
      return null;
    }
    return su.getProps().get(key);
  }

  public void setUserProp(GWikiContext ctx, String key, String value, boolean persist)
  {
    GWikiSimpleUser su = getSingleUser(ctx);
    if (su == null) {
      return;
    }
    su.getProps().put(key, value);
    if (persist == true) {
      // TODO gwiki
    }
  }

  /**
   * Verschlüsselt ein Klartext mit einem SHA-Hash
   * 
   * @return der verschlüsselte Wert als BASE64
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
       * @reason Beim asymetrischen verschlüsseln ist ein Fehler aufgetreten.
       * @action Überprüfen der Java-Installation
       */
      throw new RuntimeException("Error while executing hashing encryption: " + ex.getMessage(), ex);
    }
  }
}
