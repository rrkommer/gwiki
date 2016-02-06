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

package de.micromata.genome.gwiki.model;

import java.util.Locale;

import de.micromata.genome.gwiki.auth.GWikiSimpleUser;
import de.micromata.genome.gwiki.page.GWikiContext;
import de.micromata.genome.util.runtime.CallableX;

/**
 * Service interface for authentification, authorisation and user information.
 * 
 * @author Roger Rene Kommer (r.kommer@micromata.de)
 * 
 */
public interface GWikiAuthorization
{
  /*
   * following values will be used in connection with userProps.
   */
  public static final String USER_EMAIL = "email";

  public static final String USER_SKIN = "skin";

  public static final String USER_LANG = "lang";

  public static final String USER_TZ = "timezone";

  public static final String USER_HOME = "homePageId";

  // yyyy-MM-dd HH:mm:ss:SSS
  public static final String USER_DATEFORMAT = "timestampformat";

  /**
   * Need authorized user to access normal pages.
   * 
   * Normally looks if in the session an authorized user exists and is valid.
   * 
   * If return false, GUI may redirect to login page.
   * 
   * @return true if either no auth at all or no authorized user.
   */
  public boolean needAuthorization(GWikiContext ctx);

  /**
   * Login user with given user and password.
   * 
   * In most cases the implementation also store the user object in the session.
   * 
   * @param ctx
   * @param user
   * @param password
   * @return
   */
  public boolean login(GWikiContext ctx, String user, String password);

  /**
   * logout the user.
   * 
   * In most cases the implementation also remove user object from session.
   * 
   * @param ctx
   */
  public void logout(GWikiContext ctx);

  boolean afterLogin(GWikiContext ctx, GWikiSimpleUser su);

  /**
   * 
   * Return the effective right the page requires.
   * 
   * If the given pageRight is not defined (derived) this method checks recursivally in parent pages.
   * 
   * @param ctx
   * @param ei the element to check
   * @param pageRight the element right, like "AUTH_VIEW" or "AUTH_EDIT"
   * @return
   */
  public String getEffectiveRight(GWikiContext ctx, GWikiElementInfo ei, String pageRight);

  /**
   * Check if current user is allowed to view current element.
   * 
   * @param ctx
   * @param ei
   * @return true if user is allowed to view this element
   */
  public boolean isAllowToView(GWikiContext ctx, GWikiElementInfo ei);

  /**
   * Check if current user is allowed to edit current element.
   * 
   * @param ctx
   * @param ei
   * @return true if user is allowed to edit this element info
   */
  public boolean isAllowToEdit(GWikiContext ctx, GWikiElementInfo ei);

  /**
   * Check if current user is allowed to create a new element.
   * 
   * @param ctx
   * @param ei Element to create.
   * @return
   */
  public boolean isAllowToCreate(GWikiContext ctx, GWikiElementInfo ei);

  /**
   * Checks if current user has the given right.
   * 
   * @param ctx
   * @param right if null or empty allways accept
   * @return
   */
  public boolean isAllowTo(GWikiContext ctx, String right);

  /**
   * Same es isAllowTo() but throws Exception if not.
   * 
   * @param ctx
   * @param right if right is null or empty, always accept
   * @throws AuthorizationFailedException
   */

  public void ensureAllowTo(GWikiContext ctx, String right);

  /**
   * return true if current user is anon user.
   * 
   * @param ctx
   * @return
   */
  public boolean isCurrentAnonUser(GWikiContext ctx);

  /**
   * Return the user name of current logged in user.
   * 
   * Non-Logged In user may return "anon"
   * 
   * @param ctx
   * @return the current user name, which is logged in in this session.
   */
  public String getCurrentUserName(GWikiContext ctx);

  /**
   * @param ctx
   * @return the current user email, which is logged in in this session.
   */
  public String getCurrentUserEmail(GWikiContext ctx);

  /**
   * looking for user spezific value. First looking at non-persistant (Session), than perist storage.
   * 
   * @param ctx
   * @param key
   * @return
   */
  public String getUserProp(GWikiContext ctx, String key);

  /**
   * Set user specific value.
   * 
   * @param ctx
   * @param key
   * @param value
   * @param persist
   */
  public void setUserProp(GWikiContext ctx, String key, String value, boolean persist);

  /**
   * Return the users locale.
   * 
   * @param ctx
   * @return should never return null.
   */
  public Locale getCurrentUserLocale(GWikiContext ctx);

  /**
   * 
   * @param <T> return type of callable
   * @param wikiContext current wiki context
   * @param addRight the user should temparary get this right.
   * @return
   */
  public <T> T runWithRight(GWikiContext wikiContext, String addRight, CallableX<T, RuntimeException> callback);

  /**
   * Same as runWithRight
   * 
   * @param <T>
   * @param wikiContext
   * @param addRights
   * @param callback
   * @return
   */
  public <T> T runWithRights(GWikiContext wikiContext, String[] addRights, CallableX<T, RuntimeException> callback);

  /**
   * Run a code block with all rights granted.
   * 
   * @param <T> return value. If no return value use Void.
   * @param wikiContext
   * @param callback
   * @return the value returned by the callback.
   */
  public <T> T runAsSu(GWikiContext wikiContext, CallableX<T, RuntimeException> callback);

  /**
   * Run code block with this user.
   * 
   * @param <T> return value
   * @param user
   * @param wikiContext
   * @param callback
   * @return
   */
  public <T> T runAsUser(String user, GWikiContext wikiContext, CallableX<T, RuntimeException> callback);

  /**
   * 
   * @param wikiContext
   * @param callback will be called a user can be found in session.
   * @return false if no valid user can be found in session.
   */
  public boolean runIfAuthentificated(GWikiContext wikiContext, CallableX<Void, RuntimeException> callback);

  /**
   * Same as needAuthorization, but optionally register user also in thread local.
   * 
   * @param wikiContext
   * @return
   */
  boolean initThread(GWikiContext wikiContext);

  /**
   * if authorization using Thread local variables to store current user, this variables should cleared.
   * 
   * @param wikiContext
   */
  void clearThread(GWikiContext wikiContext);

  /**
   * Something on user storage has changed and the current user has to be reloaded.
   * 
   * @param wikiContext
   */
  void reloadUser(GWikiContext wikiContext);
}
