////////////////////////////////////////////////////////////////////////////
//
// Copyright (C) 2010-2014 Micromata GmbH / Roger Rene Kommer
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
package de.micromata.genome.gwiki.auth;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpSession;

import org.apache.commons.lang.StringUtils;

import de.micromata.genome.gwiki.model.logging.GWikiLogCategory;
import de.micromata.genome.gwiki.page.GWikiContext;
import de.micromata.genome.gwiki.umgmt.GWikiUserServeElementFilterEvent;
import de.micromata.genome.logging.GLog;
import de.micromata.genome.util.runtime.CallableX;

/**
 * A user is set outside in a session attribute.
 * 
 * @author Roger Rene Kommer (r.kommer@micromata.de)
 * 
 */
public class GWikiSessionAttributesUserAuthentification extends GWikiAuthorizationBase
{
  public static final String GWIKI_SESSION_ATTRIBUTE = "de.micromata.genome.gwiki.auth.GWikiSessionAttributesUserAuthentification";

  protected Map<String, Object> getSession(GWikiContext ctx)
  {
    Object attrs = ctx.getSession(true).getAttribute(GWIKI_SESSION_ATTRIBUTE);
    if (attrs instanceof Map) {
      return (Map<String, Object>) attrs;
    }
    return new HashMap<String, Object>();
  }

  protected GWikiSimpleUser getSingleUser(GWikiContext ctx)
  {
    GWikiSimpleUser su = GWikiUserServeElementFilterEvent.getUser();
    if (su != null) {
      return su;
    }
    return initUserFromSession(ctx);
  }

  protected GWikiSimpleUser initUserFromSession(GWikiContext ctx)
  {
    HttpSession session = ctx.getSession(false);
    if (session == null) {
      return null;
    }
    Object oumap = session.getAttribute(GWIKI_SESSION_ATTRIBUTE);
    if ((oumap instanceof Map) == false) {
      session.removeAttribute(GWIKI_SESSION_ATTRIBUTE);
      return null;
    }
    Map<String, Object> umap = (Map<String, Object>) oumap;
    GWikiSimpleUser nsu = new GWikiSimpleUser();
    nsu.setUser((String) umap.get("userName"));
    nsu.setEmail((String) umap.get("email"));
    nsu.setRightsMatcherRule((String) umap.get("rightsMatcherRule"));
    Object oprops = umap.get("props");
    if (oprops instanceof Map) {
      nsu.setProps((Map<String, String>) oprops);
    }
    GWikiUserServeElementFilterEvent.setUser(nsu);
    return nsu;
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.micromata.genome.gwiki.model.GWikiAuthorization#isAllowTo(de.micromata.genome.gwiki.page.GWikiContext,
   * java.lang.String)
   */
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

  /*
   * (non-Javadoc)
   * 
   * @see
   * de.micromata.genome.gwiki.model.GWikiAuthorization#needAuthorization(de.micromata.genome.gwiki.page.GWikiContext)
   */
  @Override
  public boolean needAuthorization(GWikiContext ctx)
  {
    return getSingleUser(ctx) == null;
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.micromata.genome.gwiki.model.GWikiAuthorization#login(de.micromata.genome.gwiki.page.GWikiContext,
   * java.lang.String, java.lang.String)
   */
  @Override
  public boolean login(GWikiContext ctx, String user, String password)
  {
    return false;
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.micromata.genome.gwiki.model.GWikiAuthorization#logout(de.micromata.genome.gwiki.page.GWikiContext)
   */
  @Override
  public void logout(GWikiContext ctx)
  {
    HttpSession session = ctx.getSession(true);
    clearAuthenticationCookie(ctx, null);
    session.removeAttribute(GWIKI_SESSION_ATTRIBUTE);
    GWikiUserServeElementFilterEvent.setUser(null);
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * de.micromata.genome.gwiki.model.GWikiAuthorization#isCurrentAnonUser(de.micromata.genome.gwiki.page.GWikiContext)
   */
  @Override
  public boolean isCurrentAnonUser(GWikiContext ctx)
  {
    return needAuthorization(ctx);
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * de.micromata.genome.gwiki.model.GWikiAuthorization#getCurrentUserName(de.micromata.genome.gwiki.page.GWikiContext)
   */
  @Override
  public String getCurrentUserName(GWikiContext ctx)
  {
    GWikiSimpleUser su = getSingleUser(ctx);
    if (su == null) {
      return "";
    }
    return su.getUser();
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * de.micromata.genome.gwiki.model.GWikiAuthorization#getCurrentUserEmail(de.micromata.genome.gwiki.page.GWikiContext)
   */
  @Override
  public String getCurrentUserEmail(GWikiContext ctx)
  {
    GWikiSimpleUser su = getSingleUser(ctx);
    if (su == null) {
      return "";
    }
    return su.getEmail();
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.micromata.genome.gwiki.model.GWikiAuthorization#getUserProp(de.micromata.genome.gwiki.page.GWikiContext,
   * java.lang.String)
   */
  @Override
  public String getUserProp(GWikiContext ctx, String key)
  {
    GWikiSimpleUser su = getSingleUser(ctx);
    if (su == null) {
      return "";
    }
    return su.getProps().get(key);
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.micromata.genome.gwiki.model.GWikiAuthorization#setUserProp(de.micromata.genome.gwiki.page.GWikiContext,
   * java.lang.String, java.lang.String, boolean)
   */
  @Override
  public void setUserProp(GWikiContext ctx, String key, String value, UserPropStorage storage)
  {
    switch (storage) {
      case Client:
        setUserPropInCookie(ctx, key, value);
        break;
      case Transient:
        GWikiSimpleUser su = getSingleUser(ctx);
        if (su == null) {
          return;
        }
        su.getProps().put(key, value);
        break;
      case Server:
        GLog.warn(GWikiLogCategory.Wiki, "Cannot store server user attribute");
        break;
    }

  }

  /*
   * (non-Javadoc)
   * 
   * @see de.micromata.genome.gwiki.model.GWikiAuthorization#runWithRights(de.micromata.genome.gwiki.page.GWikiContext,
   * java.lang.String[], de.micromata.genome.util.runtime.CallableX)
   */
  @Override
  public <T> T runWithRights(GWikiContext wikiContext, String[] addRights, CallableX<T, RuntimeException> callback)
  {
    return null;
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.micromata.genome.gwiki.model.GWikiAuthorization#runAsSu(de.micromata.genome.gwiki.page.GWikiContext,
   * de.micromata.genome.util.runtime.CallableX)
   */
  @Override
  public <T> T runAsSu(GWikiContext wikiContext, CallableX<T, RuntimeException> callback)
  {
    return null;
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.micromata.genome.gwiki.model.GWikiAuthorization#runAsUser(java.lang.String,
   * de.micromata.genome.gwiki.page.GWikiContext, de.micromata.genome.util.runtime.CallableX)
   */
  @Override
  public <T> T runAsUser(String user, GWikiContext wikiContext, CallableX<T, RuntimeException> callback)
  {
    // TODO Auto-generated method stub
    return null;
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.micromata.genome.gwiki.model.GWikiAuthorization#initThread(de.micromata.genome.gwiki.page.GWikiContext)
   */
  @Override
  public boolean initThread(GWikiContext wikiContext)
  {
    initUserFromSession(wikiContext);
    return true;
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.micromata.genome.gwiki.model.GWikiAuthorization#clearThread(de.micromata.genome.gwiki.page.GWikiContext)
   */
  @Override
  public void clearThread(GWikiContext wikiContext)
  {
    GWikiUserServeElementFilterEvent.setUser(null);
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.micromata.genome.gwiki.model.GWikiAuthorization#reloadUser(de.micromata.genome.gwiki.page.GWikiContext)
   */
  @Override
  public void reloadUser(GWikiContext wikiContext)
  {

  }

}
