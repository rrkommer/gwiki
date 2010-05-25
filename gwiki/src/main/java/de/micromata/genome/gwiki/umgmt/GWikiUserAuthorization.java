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

package de.micromata.genome.gwiki.umgmt;

import java.io.Serializable;
import java.security.MessageDigest;

import org.apache.commons.lang.StringUtils;

import de.micromata.genome.gwiki.auth.GWikiSimpleUser;
import de.micromata.genome.gwiki.auth.GWikiSimpleUserAuthorization;
import de.micromata.genome.gwiki.model.AuthorizationFailedException;
import de.micromata.genome.gwiki.model.GWikiAuthorizationExt;
import de.micromata.genome.gwiki.model.GWikiAuthorizationRights;
import de.micromata.genome.gwiki.model.GWikiElement;
import de.micromata.genome.gwiki.model.GWikiElementInfo;
import de.micromata.genome.gwiki.model.GWikiLog;
import de.micromata.genome.gwiki.model.GWikiPropKeys;
import de.micromata.genome.gwiki.model.GWikiProps;
import de.micromata.genome.gwiki.model.GWikiPropsArtefakt;
import de.micromata.genome.gwiki.model.GWikiWebUtils;
import de.micromata.genome.gwiki.page.GWikiContext;
import de.micromata.genome.util.runtime.CallableX;
import de.micromata.genome.util.types.Converter;

/**
 * User authorization.
 * 
 * @author Roger Rene Kommer (r.kommer@micromata.de)
 * 
 */
public class GWikiUserAuthorization extends GWikiSimpleUserAuthorization implements GWikiAuthorizationExt
{

  /**
   * is the current user your user
   * 
   * @param wikiContext
   * @return
   */
  public static boolean isOwnUser(GWikiContext wikiContext)
  {
    GWikiSimpleUser user = GWikiUserServeElementFilterEvent.getUser();
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

  public GWikiSimpleUser createSimpleUser(GWikiElement el, GWikiContext ctx, GWikiProps props)
  {
    String userId = GWikiContext.getNamePartFromPageId(el.getElementInfo().getId());
    GWikiSimpleUser user = new GWikiSimpleUser(userId, props.getStringValue(USER_PROP_PASSWORD), props.getStringValue(USER_PROP_EMAIL),
        props.getStringValue(USER_PROP_RIGHTSRULE));
    user.setProps(props.getMap());
    return user;
  }

  public boolean createUser(GWikiContext wikiContext, String userName, GWikiProps props)
  {
    if (hasUser(wikiContext, userName) == true) {
      return false;
    }
    String id = "admin/user/" + userName;
    GWikiElement userEl = GWikiWebUtils.createNewElement(wikiContext, id, "admin/templates/intern/WikiUserMetaTemplate", id);
    GWikiProps settings = userEl.getElementInfo().getProps();
    settings.setStringValue(GWikiPropKeys.AUTH_EDIT, GWikiAuthorizationRights.GWIKI_PRIVATE.name());
    settings.setStringValue(GWikiPropKeys.AUTH_VIEW, GWikiAuthorizationRights.GWIKI_PRIVATE.name());
    settings.setStringValue(GWikiPropKeys.CREATEDBY, userName);
    GWikiPropsArtefakt us = (GWikiPropsArtefakt) userEl.getMainPart();
    us.setCompiledObject(props);
    wikiContext.getWikiWeb().saveElement(wikiContext, userEl, false);
    return true;
  }

  public boolean hasUser(GWikiContext wikiContext, String userName)
  {
    if (StringUtils.isBlank(userName) == true) {
      return false;
    }
    String id = "admin/user/" + userName;
    GWikiElementInfo ei = wikiContext.getWikiWeb().findElementInfo(id);
    return ei != null;
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
    GWikiSimpleUser suser = createSimpleUser(el, ctx, props);
    return suser;
  }

  public <T> T runAsUser(String user, GWikiContext wikiContext, CallableX<T, RuntimeException> callback)
  {
    GWikiSimpleUser pu = GWikiUserServeElementFilterEvent.getUser();
    GWikiSimpleUser su = findUser(wikiContext, user);
    if (su == null) {
      throw new AuthorizationFailedException("User doesn't exits: " + user);
    }

    try {

      GWikiUserServeElementFilterEvent.setUser(su);
      return callback.call();
    } finally {
      GWikiUserServeElementFilterEvent.setUser(pu);
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
    GWikiUserServeElementFilterEvent.setUser(su);
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
