/////////////////////////////////////////////////////////////////////////////
//
// Project   DHL-ParcelOnlinePostage
//
// Author    roger@micromata.de
// Created   21.08.2008
// Copyright Micromata 21.08.2008
//
/////////////////////////////////////////////////////////////////////////////
package de.micromata.genome.gwiki.page.gspt;

import java.util.HashMap;
import java.util.Map;

/**
 * Context for Replacer
 * 
 * @author Roger Rene Kommer (r.kommer@micromata.de)
 * 
 */
public class ReplacerContext
{
  public static final String SCRIPTFILENAME = "de.micromata.web.gspt.ReplacerContext.scriptFileName";

  public static final String COMPILECONTEXT = "de.micromata.web.gspt.ReplacerContext.compileContext";

  private Map<String, Object> attributes = new HashMap<String, Object>();

  public ReplacerContext()
  {

  }

  public static ReplacerContext createReplacer(String fileName, Map<String, Object> compileContext)
  {
    ReplacerContext ctx = new ReplacerContext();
    ctx.getAttributes().putAll(compileContext);
    ctx.setAttribute(SCRIPTFILENAME, fileName);
    ctx.setAttribute(COMPILECONTEXT, compileContext);
    return ctx;
  }

  public Object getAttribute(String key)
  {
    return attributes.get(key);
  }

  public void setAttribute(String key, Object value)
  {
    attributes.put(key, value);
  }

  public void removeAttribute(String key)
  {
    attributes.remove(key);
  }

  public Map<String, Object> getAttributes()
  {
    return attributes;
  }

  public void setAttributes(Map<String, Object> attributes)
  {
    this.attributes = attributes;
  }
}
