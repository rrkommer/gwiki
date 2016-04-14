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

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import javax.servlet.http.HttpSession;

import de.micromata.genome.gwiki.page.GWikiContext;

/**
 * GWikiSessionProvider for standard sessions.
 * 
 * @author Roger Rene Kommer (r.kommer@micromata.de)
 * 
 */
public class GWikiStandardSessionProvider implements GWikiSessionProvider
{
  public static String SESSKEYS = "de.micromata.genome.gwiki.model.GWikiStandardSessionProvider.SESSKEYS";

  public Object getSessionAttribute(GWikiContext wikiContext, String key)
  {
    HttpSession session = wikiContext.getSession(false);
    if (session == null) {
      return null;
    }
    return session.getAttribute(key);
  }

  @SuppressWarnings("unchecked")
  public void setSessionAttribute(GWikiContext wikiContext, String key, Serializable object)
  {
    HttpSession session = wikiContext.getSession(true);
    if (session == null) {
      return;
    }
    Set<String> keys = (Set<String>) session.getAttribute(SESSKEYS);
    if (keys == null) {
      keys = new HashSet<String>();
    }
    if (keys.contains(key) == false) {
      keys.add(key);
      session.setAttribute(SESSKEYS, keys);
    }
    session.setAttribute(key, object);
  }

  @SuppressWarnings("unchecked")
  public void removeSessionAttribute(GWikiContext wikiContext, String key)
  {
    HttpSession session = wikiContext.getSession(true);
    Set<String> keys = (Set<String>) session.getAttribute(SESSKEYS);
    if (keys != null && keys.contains(key) == true) {
      keys.remove(key);
      session.setAttribute(SESSKEYS, keys);
    }
    session.removeAttribute(key);
  }

  @SuppressWarnings("unchecked")
  public void clearSessionAttributes(GWikiContext wikiContext)
  {
    HttpSession session = wikiContext.getSession(true);
    Set<String> keys = (Set<String>) session.getAttribute(SESSKEYS);
    if (keys == null) {
      return;
    }
    for (String k : keys) {
      session.removeAttribute(k);
    }
  }
}
