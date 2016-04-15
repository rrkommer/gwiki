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

package de.micromata.genome.gwiki.model;

import java.util.Map;
import java.util.SortedMap;

import de.micromata.genome.gwiki.page.GWikiContext;

/**
 * Extended Authorization interface with user managment functions.
 * 
 * @author Roger Rene Kommer (r.kommer@micromata.de)
 * 
 */
public interface GWikiAuthorizationExt extends GWikiAuthorization
{

  /**
   * The Constant USER_PROP_PASSWORD.
   */
  public static final String USER_PROP_PASSWORD = "password";

  /**
   * The Constant USER_PROP_EMAIL.
   */
  public static final String USER_PROP_EMAIL = "email";

  /**
   * The Constant USER_PROP_DEACTIVATED.
   */
  public static final String USER_PROP_DEACTIVATED = "deactivated";

  /**
   * The Constant USER_PROP_RIGHTSRULE.
   */
  public static final String USER_PROP_RIGHTSRULE = "rightsrule";

  /**
   * Checks for user.
   *
   * @param wikiContext the wiki context
   * @param userName the user name
   * @return true if user is known.
   */
  public boolean hasUser(GWikiContext wikiContext, String userName);

  /**
   * Create a new user.
   *
   * @param wikiContext the wiki context
   * @param userName the user name
   * @param props see USER_PROP_* constants.
   * @return true, if successful
   */
  public boolean createUser(GWikiContext wikiContext, String userName, GWikiProps props);

  /**
   * give a list of known rights in the system.
   *
   * @param wikiContext the wiki context
   * @return the system rights
   */
  public SortedMap<String, GWikiRight> getSystemRights(GWikiContext wikiContext);

  /**
   * Give the rights of the given user.
   *
   * @param wikiContext the wiki context
   * @param systemRights the system rights
   * @param roleString the role string
   * @return the user right
   */
  public SortedMap<String, GWikiRight> getUserRight(GWikiContext wikiContext, Map<String, GWikiRight> systemRights,
      String roleString);
}
