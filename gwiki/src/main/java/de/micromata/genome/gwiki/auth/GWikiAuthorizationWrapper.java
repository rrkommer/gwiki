////////////////////////////////////////////////////////////////////////////
// 
// Copyright (C) 2010-2013 Micromata GmbH / Roger Rene Kommer
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

import java.util.Locale;

import de.micromata.genome.gwiki.model.GWikiAuthorization;
import de.micromata.genome.gwiki.model.GWikiElementInfo;
import de.micromata.genome.gwiki.page.GWikiContext;
import de.micromata.genome.util.runtime.CallableX;

/**
 * @author Christian Claus (c.claus@micromata.de)
 * 
 */
public class GWikiAuthorizationWrapper implements GWikiAuthorization
{
  protected GWikiAuthorization parent;

  public GWikiAuthorizationWrapper()
  {

  }

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

  /**
   * @param ctx
   * @param key
   * @param value
   * @param persist
   * @see de.micromata.genome.gwiki.model.GWikiAuthorization#setUserProp(de.micromata.genome.gwiki.page.GWikiContext,
   *      java.lang.String, java.lang.String, boolean)
   */
  @Override
  public void setUserProp(GWikiContext ctx, String key, String value, boolean persist)
  {
    parent.setUserProp(ctx, key, value, persist);
  }

  /**
   * @param ctx
   * @return
   * @see de.micromata.genome.gwiki.model.GWikiAuthorization#getCurrentUserLocale(de.micromata.genome.gwiki.page.GWikiContext)
   */
  @Override
  public Locale getCurrentUserLocale(GWikiContext ctx)
  {
    return parent.getCurrentUserLocale(ctx);
  }

  /**
   * @param <T>
   * @param wikiContext
   * @param addRight
   * @param callback
   * @return
   * @see de.micromata.genome.gwiki.model.GWikiAuthorization#runWithRight(de.micromata.genome.gwiki.page.GWikiContext,
   *      java.lang.String, de.micromata.genome.util.runtime.CallableX)
   */
  @Override
  public <T> T runWithRight(GWikiContext wikiContext, String addRight, CallableX<T, RuntimeException> callback)
  {
    return parent.runWithRight(wikiContext, addRight, callback);
  }

  /**
   * @param <T>
   * @param wikiContext
   * @param addRights
   * @param callback
   * @return
   * @see de.micromata.genome.gwiki.model.GWikiAuthorization#runWithRights(de.micromata.genome.gwiki.page.GWikiContext,
   *      java.lang.String[], de.micromata.genome.util.runtime.CallableX)
   */
  @Override
  public <T> T runWithRights(GWikiContext wikiContext, String[] addRights, CallableX<T, RuntimeException> callback)
  {
    return parent.runWithRights(wikiContext, addRights, callback);
  }

  /**
   * @param <T>
   * @param wikiContext
   * @param callback
   * @return
   * @see de.micromata.genome.gwiki.model.GWikiAuthorization#runAsSu(de.micromata.genome.gwiki.page.GWikiContext,
   *      de.micromata.genome.util.runtime.CallableX)
   */
  @Override
  public <T> T runAsSu(GWikiContext wikiContext, CallableX<T, RuntimeException> callback)
  {
    return parent.runAsSu(wikiContext, callback);
  }

  /**
   * @param <T>
   * @param user
   * @param wikiContext
   * @param callback
   * @return
   * @see de.micromata.genome.gwiki.model.GWikiAuthorization#runAsUser(java.lang.String,
   *      de.micromata.genome.gwiki.page.GWikiContext, de.micromata.genome.util.runtime.CallableX)
   */
  @Override
  public <T> T runAsUser(String user, GWikiContext wikiContext, CallableX<T, RuntimeException> callback)
  {
    return parent.runAsUser(user, wikiContext, callback);
  }

  /**
   * @param wikiContext
   * @param callback
   * @return
   * @see de.micromata.genome.gwiki.model.GWikiAuthorization#runIfAuthentificated(de.micromata.genome.gwiki.page.GWikiContext,
   *      de.micromata.genome.util.runtime.CallableX)
   */
  @Override
  public boolean runIfAuthentificated(GWikiContext wikiContext, CallableX<Void, RuntimeException> callback)
  {
    return parent.runIfAuthentificated(wikiContext, callback);
  }

  /**
   * @param wikiContext
   * @return
   * @see de.micromata.genome.gwiki.model.GWikiAuthorization#initThread(de.micromata.genome.gwiki.page.GWikiContext)
   */
  @Override
  public boolean initThread(GWikiContext wikiContext)
  {
    return parent.initThread(wikiContext);
  }

  /**
   * @param wikiContext
   * @see de.micromata.genome.gwiki.model.GWikiAuthorization#clearThread(de.micromata.genome.gwiki.page.GWikiContext)
   */
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
