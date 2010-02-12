/////////////////////////////////////////////////////////////////////////////
//
// Project   DHL-ParcelOnlinePostage
//
// Author    roger@micromata.de
// Created   01.12.2009
// Copyright Micromata 01.12.2009
//
/////////////////////////////////////////////////////////////////////////////
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
