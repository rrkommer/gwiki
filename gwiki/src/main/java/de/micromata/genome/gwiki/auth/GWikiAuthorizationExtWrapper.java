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

import java.util.Map;
import java.util.SortedMap;

import de.micromata.genome.gwiki.model.GWikiAuthorization;
import de.micromata.genome.gwiki.model.GWikiAuthorizationExt;
import de.micromata.genome.gwiki.model.GWikiProps;
import de.micromata.genome.gwiki.model.GWikiRight;
import de.micromata.genome.gwiki.page.GWikiContext;

/**
 * @author Christian Claus (c.claus@micromata.de)
 *
 */
public class GWikiAuthorizationExtWrapper extends GWikiAuthorizationWrapper implements GWikiAuthorizationExt
{
  protected GWikiAuthorizationExt parentExt;
  
  public GWikiAuthorizationExtWrapper()
  {

  }
  
  public GWikiAuthorizationExtWrapper(GWikiAuthorizationExt parentExt) 
  {
    this.parentExt = parentExt;
    this.parent = (GWikiAuthorization) parentExt;
  }
  
  
  /* (non-Javadoc)
   * @see de.micromata.genome.gwiki.model.GWikiAuthorizationExt#hasUser(de.micromata.genome.gwiki.page.GWikiContext, java.lang.String)
   */
  public boolean hasUser(GWikiContext wikiContext, String userName)
  {
    return parentExt.hasUser(wikiContext, userName);
  }

  /* (non-Javadoc)
   * @see de.micromata.genome.gwiki.model.GWikiAuthorizationExt#createUser(de.micromata.genome.gwiki.page.GWikiContext, java.lang.String, de.micromata.genome.gwiki.model.GWikiProps)
   */
  public boolean createUser(GWikiContext wikiContext, String userName, GWikiProps props)
  {
    return parentExt.createUser(wikiContext, userName, props);
  }

  /* (non-Javadoc)
   * @see de.micromata.genome.gwiki.model.GWikiAuthorizationExt#getSystemRights(de.micromata.genome.gwiki.page.GWikiContext)
   */
  public SortedMap<String, GWikiRight> getSystemRights(GWikiContext wikiContext)
  {
    return parentExt.getSystemRights(wikiContext);
  }

  /* (non-Javadoc)
   * @see de.micromata.genome.gwiki.model.GWikiAuthorizationExt#getUserRight(de.micromata.genome.gwiki.page.GWikiContext, java.util.Map, java.lang.String)
   */
  public SortedMap<String, GWikiRight> getUserRight(GWikiContext wikiContext, Map<String, GWikiRight> systemRights, String roleString)
  {
    return parentExt.getUserRight(wikiContext, systemRights, roleString);
  }
  
  
}
