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

import java.util.Locale;

import de.micromata.genome.gwiki.model.GWikiAuthorization;
import de.micromata.genome.gwiki.model.GWikiElementInfo;
import de.micromata.genome.gwiki.page.GWikiContext;
import de.micromata.genome.util.runtime.CallableX;

/**
 * The Class GWikiAuthorizationWrapper.
 *
 * @author Christian Claus (c.claus@micromata.de)
 */
public class GWikiAuthorizationWrapper implements GWikiAuthorization
{

  /**
   * The parent.
   */
  protected GWikiAuthorization parent;

  /**
   * Instantiates a new g wiki authorization wrapper.
   */
  public GWikiAuthorizationWrapper()
  {

  }

  /**
   * Instantiates a new g wiki authorization wrapper.
   *
   * @param parent the parent
   */
  public GWikiAuthorizationWrapper(GWikiAuthorization parent)
  {
    this.parent = parent;
  }

  /**
   * @param ctx
   * @return
   * @see de.micromata.genome.gwiki.model.GWikiAuthorization#needAuthorization(de.micromata.genome.gwiki.page.GWikiContext)
   */
  @Override
  public boolean needAuthorization(GWikiContext ctx)
  {
    return parent.needAuthorization(ctx);
  }

  @Override
  public void createAuthenticationCookie(GWikiContext ctx, String user, String password)
  {
    parent.createAuthenticationCookie(ctx, user, password);

  }

  @Override
  public void clearAuthenticationCookie(GWikiContext ctx, String user)
  {
    parent.clearAuthenticationCookie(ctx, user);
  }

  /**
   * @param ctx
   * @param user
   * @param password
   * @return
   * @see de.micromata.genome.gwiki.model.GWikiAuthorization#login(de.micromata.genome.gwiki.page.GWikiContext,
   *      java.lang.String, java.lang.String)
   */
  @Override
  public boolean login(GWikiContext ctx, String user, String password)
  {
    return parent.login(ctx, user, password);
  }

  @Override
  public boolean afterLogin(GWikiContext ctx, GWikiSimpleUser su)
  {
    return parent.afterLogin(ctx, su);
  }

  /**
   * @param ctx
   * @see de.micromata.genome.gwiki.model.GWikiAuthorization#logout(de.micromata.genome.gwiki.page.GWikiContext)
   */
  @Override
  public void logout(GWikiContext ctx)
  {
    parent.logout(ctx);
  }

  /**
   * @param ctx
   * @param ei
   * @param pageRight
   * @return
   * @see de.micromata.genome.gwiki.model.GWikiAuthorization#getEffectiveRight(de.micromata.genome.gwiki.page.GWikiContext,
   *      de.micromata.genome.gwiki.model.GWikiElementInfo, java.lang.String)
   */
  @Override
  public String getEffectiveRight(GWikiContext ctx, GWikiElementInfo ei, String pageRight)
  {
    return parent.getEffectiveRight(ctx, ei, pageRight);
  }

  /**
   * @param ctx
   * @param ei
   * @return
   * @see de.micromata.genome.gwiki.model.GWikiAuthorization#isAllowToView(de.micromata.genome.gwiki.page.GWikiContext,
   *      de.micromata.genome.gwiki.model.GWikiElementInfo)
   */
  @Override
  public boolean isAllowToView(GWikiContext ctx, GWikiElementInfo ei)
  {
    return parent.isAllowToView(ctx, ei);
  }

  /**
   * @param ctx
   * @param ei
   * @return
   * @see de.micromata.genome.gwiki.model.GWikiAuthorization#isAllowToEdit(de.micromata.genome.gwiki.page.GWikiContext,
   *      de.micromata.genome.gwiki.model.GWikiElementInfo)
   */
  @Override
  public boolean isAllowToEdit(GWikiContext ctx, GWikiElementInfo ei)
  {
    return parent.isAllowToEdit(ctx, ei);
  }

  /**
   * @param ctx
   * @param ei
   * @return
   * @see de.micromata.genome.gwiki.model.GWikiAuthorization#isAllowToCreate(de.micromata.genome.gwiki.page.GWikiContext,
   *      de.micromata.genome.gwiki.model.GWikiElementInfo)
   */
  @Override
  public boolean isAllowToCreate(GWikiContext ctx, GWikiElementInfo ei)
  {
    return parent.isAllowToCreate(ctx, ei);
  }

  /**
   * @param ctx
   * @param right
   * @return
   * @see de.micromata.genome.gwiki.model.GWikiAuthorization#isAllowTo(de.micromata.genome.gwiki.page.GWikiContext,
   *      java.lang.String)
   */
  @Override
  public boolean isAllowTo(GWikiContext ctx, String right)
  {
    return parent.isAllowTo(ctx, right);
  }

  /**
   * @param ctx
   * @param right
   * @see de.micromata.genome.gwiki.model.GWikiAuthorization#ensureAllowTo(de.micromata.genome.gwiki.page.GWikiContext,
   *      java.lang.String)
   */
  @Override
  public void ensureAllowTo(GWikiContext ctx, String right)
  {
    parent.ensureAllowTo(ctx, right);
  }

  /**
   * @param ctx
   * @return
   * @see de.micromata.genome.gwiki.model.GWikiAuthorization#getCurrentUserName(de.micromata.genome.gwiki.page.GWikiContext)
   */
  @Override
  public String getCurrentUserName(GWikiContext ctx)
  {
    return parent.getCurrentUserName(ctx);
  }

  /**
   * @param ctx
   * @return
   * @see de.micromata.genome.gwiki.model.GWikiAuthorization#getCurrentUserEmail(de.micromata.genome.gwiki.page.GWikiContext)
   */
  @Override
  public String getCurrentUserEmail(GWikiContext ctx)
  {
    return parent.getCurrentUserEmail(ctx);
  }

  /**
   * @param ctx
   * @param key
   * @return
   * @see de.micromata.genome.gwiki.model.GWikiAuthorization#getUserProp(de.micromata.genome.gwiki.page.GWikiContext,
   *      java.lang.String)
   */
  @Override
  public String getUserProp(GWikiContext ctx, String key)
  {
    return parent.getUserProp(ctx, key);
  }

  @Override
  public void setUserProp(GWikiContext ctx, String key, String value, UserPropStorage storage)
  {
    parent.setUserProp(ctx, key, value, storage);
  }

  @Override
  public Locale getCurrentUserLocale(GWikiContext ctx)
  {
    return parent.getCurrentUserLocale(ctx);
  }

  @Override
  public <T> T runWithRight(GWikiContext wikiContext, String addRight, CallableX<T, RuntimeException> callback)
  {
    return parent.runWithRight(wikiContext, addRight, callback);
  }

  @Override
  public <T> T runWithRights(GWikiContext wikiContext, String[] addRights, CallableX<T, RuntimeException> callback)
  {
    return parent.runWithRights(wikiContext, addRights, callback);
  }

  @Override
  public <T> T runAsSu(GWikiContext wikiContext, CallableX<T, RuntimeException> callback)
  {
    return parent.runAsSu(wikiContext, callback);
  }

  @Override
  public <T> T runAsUser(String user, GWikiContext wikiContext, CallableX<T, RuntimeException> callback)
  {
    return parent.runAsUser(user, wikiContext, callback);
  }

  @Override
  public boolean runIfAuthentificated(GWikiContext wikiContext, CallableX<Void, RuntimeException> callback)
  {
    return parent.runIfAuthentificated(wikiContext, callback);
  }

  @Override
  public boolean initThread(GWikiContext wikiContext)
  {
    return parent.initThread(wikiContext);
  }

  @Override
  public void clearThread(GWikiContext wikiContext)
  {
    parent.clearThread(wikiContext);
  }

  @Override
  public void reloadUser(GWikiContext wikiContext)
  {
    parent.reloadUser(wikiContext);
  }

  @Override
  public boolean isCurrentAnonUser(GWikiContext ctx)
  {
    return parent.isCurrentAnonUser(ctx);
  }

}
