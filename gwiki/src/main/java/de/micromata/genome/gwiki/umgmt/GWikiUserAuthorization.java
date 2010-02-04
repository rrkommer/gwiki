/////////////////////////////////////////////////////////////////////////////
//
// Project   DHL-ParcelOnlinePostage
//
// Author    roger@micromata.de
// Created   13.11.2009
// Copyright Micromata 13.11.2009
//
/////////////////////////////////////////////////////////////////////////////
package de.micromata.genome.gwiki.umgmt;

import java.io.Serializable;
import java.security.MessageDigest;

import org.apache.commons.lang.StringUtils;

import de.micromata.genome.gwiki.auth.GWikiSimpleUser;
import de.micromata.genome.gwiki.auth.GWikiSimpleUserAuthorization;
import de.micromata.genome.gwiki.model.AuthorizationFailedException;
import de.micromata.genome.gwiki.model.GWikiElement;
import de.micromata.genome.gwiki.model.GWikiLog;
import de.micromata.genome.gwiki.model.GWikiProps;
import de.micromata.genome.gwiki.page.GWikiContext;
import de.micromata.genome.util.runtime.CallableX;
import de.micromata.genome.util.types.Converter;

public class GWikiUserAuthorization extends GWikiSimpleUserAuthorization
{
  /**
   * is the current user your user
   * 
   * @param wikiContext
   * @return
   */
  public static boolean isOwnUser(GWikiContext wikiContext)
  {
    GWikiSimpleUser user = GWikiUserServeElementFilterEvent.CURRENT_USER.get();
    if (user == null || user.isAnon() == true) {
      return false;
    }
    return true;
  }

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
      throw new RuntimeException(ex);
    }
  }

  @Override
  public boolean needAuthorization(GWikiContext ctx)
  {
    GWikiSimpleUser su = getSingleUser(ctx);
    if (su == null) {
      return true;
    }
    return su.getUser().equals("anon") == true;

  }

  public GWikiSimpleUser createUser(GWikiElement el, GWikiContext ctx, GWikiProps props)
  {
    String userId = GWikiContext.getNamePartFromPageId(el.getElementInfo().getId());
    GWikiSimpleUser user = new GWikiSimpleUser(userId, props.getStringValue("password"), props.getStringValue("email"), props
        .getStringValue("rightsrule"));
    user.setProps(props.getMap());
    return user;
  }

  protected GWikiElement findUserElement(GWikiContext ctx, String user)
  {
    if (ctx.getWikiWeb() == null)
      return null;
    String id = "admin/user/" + user;
    GWikiElement el = ctx.getWikiWeb().findElement(id);
    return el;
  }

  public GWikiSimpleUser findFallbackUser(GWikiContext ctx, String user)
  {
    String wdun = ctx.getWikiWeb().getDaoContext().getWebDavUserName();
    if (StringUtils.equals(wdun, user) == false) {
      return null;
    }
    GWikiSimpleUser wdu = new GWikiSimpleUser(wdun, ctx.getWikiWeb().getDaoContext().getWebDavPasswordHash(), "gwiki-noreply@micromata.de",
        "+*");
    return wdu;
  }

  public GWikiSimpleUser findUser(GWikiContext ctx, String user)
  {
    if (StringUtils.isBlank(user) == true) {
      return null;
    }
    GWikiElement el = findUserElement(ctx, user);
    if (el == null) {
      return findFallbackUser(ctx, user);
    }
    Serializable ser = el.getMainPart().getCompiledObject();
    GWikiProps props = (GWikiProps) ser;
    GWikiSimpleUser suser = createUser(el, ctx, props);
    return suser;
  }

  public <T> T runAsUser(String user, GWikiContext wikiContext, CallableX<T, RuntimeException> callback)
  {
    GWikiSimpleUser pu = GWikiUserServeElementFilterEvent.CURRENT_USER.get();
    GWikiSimpleUser su = findUser(wikiContext, user);
    if (su == null) {
      throw new AuthorizationFailedException("User doesn't exits: " + user);
    }

    try {

      GWikiUserServeElementFilterEvent.CURRENT_USER.set(su);
      return callback.call();
    } finally {
      GWikiUserServeElementFilterEvent.CURRENT_USER.set(pu);
    }
  }

  public boolean login(GWikiContext ctx, String user, String password)
  {
    GWikiSimpleUser su = findUser(ctx, user);
    if (su == null) {
      return false;
    }

    String penc = encrypt(password);
    if (StringUtils.equals(su.getPassword(), penc) == false) {
      return false;
    }

    setSingleUser(ctx, su);
    GWikiUserServeElementFilterEvent.CURRENT_USER.set(su);
    GWikiLog.note("User logged in: " + user);
    return true;
  }

  public void setUserProp(GWikiContext ctx, String key, String value)
  {
    GWikiSimpleUser su = getSingleUser(ctx);
    if (su == null) {
      return;
    }
    GWikiElement el = findUserElement(ctx, su.getUser());
    if (el == null) {
      return;
    }
    Serializable ser = el.getMainPart().getCompiledObject();
    GWikiProps props = (GWikiProps) ser;
    props.setStringValue(key, value);
    ctx.getWikiWeb().saveElement(ctx, el, false);
  }
}
