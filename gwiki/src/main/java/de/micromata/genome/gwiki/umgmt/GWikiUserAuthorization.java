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
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

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
import de.micromata.genome.gwiki.model.GWikiRight;
import de.micromata.genome.gwiki.model.GWikiRoleConfig;
import de.micromata.genome.gwiki.model.GWikiWebUtils;
import de.micromata.genome.gwiki.page.GWikiContext;
import de.micromata.genome.util.matcher.BooleanListMatcher;
import de.micromata.genome.util.matcher.BooleanListRulesFactory;
import de.micromata.genome.util.matcher.EqualsMatcher;
import de.micromata.genome.util.matcher.Matcher;
import de.micromata.genome.util.matcher.TreeStateMatcher;
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

  @SuppressWarnings("unchecked")
  private Matcher<String> replaceMatcherRules(GWikiRoleConfig rc, Matcher<String> m)
  {
    if (m instanceof EqualsMatcher) {
      EqualsMatcher<String> em = (EqualsMatcher<String>) m;
      GWikiRight role = rc.getRoles().get(em.getOther());
      if (role != null && role.getDefinitionRule() != null) {
        return role.getDefinitionRule();
      }
      return m;
    }
    if (m instanceof BooleanListMatcher) {
      BooleanListMatcher<String> bm = (BooleanListMatcher<String>) m;
      List<Matcher<String>> ml = bm.getMatcherList();
      for (int i = 0; i < ml.size(); ++i) {
        ml.set(i, replaceMatcherRules(rc, ml.get(i)));
      }
      return m;
    }
    if (m instanceof TreeStateMatcher) {
      TreeStateMatcher tm = (TreeStateMatcher) m;
      tm.setNested(replaceMatcherRules(rc, tm.getNested()));
    }
    return m;
  }

  public GWikiSimpleUser createSimpleUser(GWikiElement el, GWikiContext ctx, GWikiProps props)
  {
    String userId = GWikiContext.getNamePartFromPageId(el.getElementInfo().getId());
    String rr = props.getStringValue(USER_PROP_RIGHTSRULE);

    GWikiSimpleUser user = new GWikiSimpleUser(userId, props.getStringValue(USER_PROP_PASSWORD), props.getStringValue(USER_PROP_EMAIL), rr);
    user.setProps(props.getMap());

    GWikiRoleConfig rc = getRoleConfig(ctx);
    if (rc == null) {
      return user;
    }
    user.setRightsMatcher(replaceMatcherRules(rc, user.getRightsMatcher()));
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
    if (ctx.getWikiWeb().getFilter().onLogin(ctx, su) == false) {
      GWikiUserServeElementFilterEvent.setUser(null);
      return false;
    }
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

  public static GWikiRoleConfig getRoleConfig(GWikiContext wikiContext)
  {
    GWikiElement el = wikiContext.getWikiWeb().findElement("admin/config/GWikiUserRolesConfig");
    if (el == null) {
      return null;
    }
    return (GWikiRoleConfig) el.getMainPart().getCompiledObject();
  }

  protected void getConfigSystemRights(GWikiContext wikiContext, Map<String, GWikiRight> rights)
  {
    GWikiRoleConfig rc = getRoleConfig(wikiContext);
    if (rc == null) {
      return;
    }
    rights.putAll(rc.getRoles());
  }

  public void getSystemRights(GWikiContext wikiContext, Map<String, GWikiRight> rights)
  {
    for (GWikiAuthorizationRights ar : GWikiAuthorizationRights.values()) {
      rights.put(ar.name(), new GWikiRight(ar.name(), GWikiRight.RIGHT_CAT_SYSTEM_RIGHT, null));
    }
    getConfigSystemRights(wikiContext, rights);
  }

  public void getPageRights(GWikiContext wikiContext, SortedMap<String, GWikiRight> rights)
  {
    for (GWikiElementInfo ei : wikiContext.getWikiWeb().getElementInfos()) {
      String r = ei.getProps().getStringValue(GWikiPropKeys.AUTH_EDIT);
      if (StringUtils.isNotBlank(r) == true) {
        if (rights.containsKey(r) == false) {
          rights.put(r, new GWikiRight(r, GWikiRight.RIGHT_CAT_PAGE_RIGHT, null));
        }
      }
      r = ei.getProps().getStringValue(GWikiPropKeys.AUTH_VIEW);
      if (StringUtils.isNotBlank(r) == true) {
        if (rights.containsKey(r) == false) {
          rights.put(r, new GWikiRight(r, GWikiRight.RIGHT_CAT_PAGE_RIGHT, null));
        }
      }
    }
  }

  public SortedMap<String, GWikiRight> getSystemRights(GWikiContext wikiContext)
  {
    SortedMap<String, GWikiRight> ret = new TreeMap<String, GWikiRight>();
    getSystemRights(wikiContext, ret);
    getPageRights(wikiContext, ret);
    return ret;
  }

  public GWikiRight getRightFromString(String rs, Map<String, GWikiRight> systemRights)
  {
    GWikiRight r = systemRights.get(rs);
    if (r != null) {
      return r;
    }
    return new GWikiRight(rs, GWikiRight.RIGHT_CAT_OTHER_RIGHT, "");
  }

  @SuppressWarnings("unchecked")
  public boolean collectRights(Matcher<String> m, Map<String, GWikiRight> systemRights, SortedMap<String, GWikiRight> ret)
  {
    if (m instanceof EqualsMatcher) {
      EqualsMatcher<String> em = (EqualsMatcher<String>) m;
      ret.put(em.getOther(), getRightFromString(em.getOther(), systemRights));
      return true;
    }
    if (m instanceof BooleanListMatcher) {
      BooleanListMatcher<String> bm = (BooleanListMatcher<String>) m;
      List<Matcher<String>> ml = bm.getMatcherList();
      for (int i = 0; i < ml.size(); ++i) {
        if (collectRights(ml.get(i), systemRights, ret) == false) {
          return false;
        }
      }
      return true;
    }
    if (m instanceof TreeStateMatcher) {
      TreeStateMatcher tm = (TreeStateMatcher) m;
      if (tm.isValue() == false) {
        return false;
      }
      return collectRights(tm.getNested(), systemRights, ret);
    }
    return false;
  }

  public SortedMap<String, GWikiRight> getUserRight(GWikiContext wikiContext, Map<String, GWikiRight> systemRights, String roleString)
  {
    Matcher<String> rightsMatcher = new BooleanListRulesFactory<String>().createMatcher(roleString);
    GWikiRoleConfig rc = getRoleConfig(wikiContext);
    if (rc != null) {
      rightsMatcher = replaceMatcherRules(rc, rightsMatcher);
    }

    SortedMap<String, GWikiRight> ret = new TreeMap<String, GWikiRight>();
    if (collectRights(rightsMatcher, systemRights, ret) == true) {
      return ret;
    }
    ret = new TreeMap<String, GWikiRight>();
    List<String> roles = Converter.parseStringTokens(roleString, ",", false);
    for (String role : roles) {
      if (role.startsWith("+") == true) {
        role = role.substring(1);
      }
      if (systemRights.containsKey(role) == true) {
        ret.put(role, systemRights.get(role));
      } else {
        ret.put(role, new GWikiRight(role, GWikiRight.RIGHT_CAT_OTHER_RIGHT, ""));
      }
    }
    // props.getStringList(key)
    return ret;
  }
}
