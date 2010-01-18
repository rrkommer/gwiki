/////////////////////////////////////////////////////////////////////////////
//
// Project   DHL-ParcelOnlinePostage
//
// Author    roger@micromata.de
// Created   16.08.2009
// Copyright Micromata 16.08.2009
//
/////////////////////////////////////////////////////////////////////////////
package de.micromata.genome.test.web;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionContext;

import org.apache.commons.collections15.iterators.IteratorEnumeration;

/**
 * Simulation of a HttpSession.
 * 
 * @author roger@micromata.de
 * 
 */
public class SimHttpSession implements HttpSession
{
  private Map<String, Object> sessionAttributes = new HashMap<String, Object>();

  private long created = System.currentTimeMillis();

  public SimHttpSession()
  {

  }

  public Object getAttribute(String key)
  {
    return sessionAttributes.get(key);
  }

  public Enumeration getAttributeNames()
  {
    return new IteratorEnumeration(sessionAttributes.keySet().iterator());
  }

  public long getCreationTime()
  {
    return created;
  }

  public String getId()
  {
    return "SimSession";
  }

  public long getLastAccessedTime()
  {
    return created;
  }

  public int getMaxInactiveInterval()
  {
    return 100000;
  }

  public ServletContext getServletContext()
  {
    return null;
  }

  public HttpSessionContext getSessionContext()
  {
    return null;
  }

  public Object getValue(String key)
  {
    return getAttribute(key);
  }

  public String[] getValueNames()
  {

    return null;
  }

  public void invalidate()
  {
    sessionAttributes.clear();
  }

  public boolean isNew()
  {
    return false;
  }

  public void putValue(String arg0, Object arg1)
  {
    setAttribute(arg0, arg1);

  }

  public void removeAttribute(String arg0)
  {
    sessionAttributes.remove(arg0);

  }

  public void removeValue(String arg0)
  {
    removeAttribute(arg0);
  }

  public void setAttribute(String key, Object value)
  {
    sessionAttributes.put(key, value);

  }

  public void setMaxInactiveInterval(int arg0)
  {

  }

}
