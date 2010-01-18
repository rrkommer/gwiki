/////////////////////////////////////////////////////////////////////////////
//
// Project   DHL-ParcelOnlinePostage
//
// Author    roger@micromata.de
// Created   11.10.2008
// Copyright Micromata 11.10.2008
//
/////////////////////////////////////////////////////////////////////////////
package de.micromata.genome.gwiki.page.gspt.jdkrepl;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * Not synchronized version of Properties
 * 
 * @author roger@micromata.de
 * 
 */
public class UnsyncProperties extends HashMap<String, Object>
{
  /**
   * 
   */
  private static final long serialVersionUID = 5660994201955845607L;

  Map< ? extends String, ? extends Object> defaults;

  public UnsyncProperties()
  {
    super();
  }

  public UnsyncProperties(int initialCapacity, float loadFactor)
  {
    super(initialCapacity, loadFactor);
  }

  public UnsyncProperties(int initialCapacity)
  {
    super(initialCapacity);
  }

  public UnsyncProperties(Map< ? extends String, ? extends Object> m)
  {
    super(m);
  }

  @SuppressWarnings("unchecked")
  public UnsyncProperties(Properties props)
  {
    this((Map) props);
  }

  public String getProperty(String key)
  {
    Object oval = super.get(key);
    String sval = (oval instanceof String) ? (String) oval : null;
    return sval;
  }

  public String getProperty(String key, String defaultValue)
  {
    String val = getProperty(key);
    return (val == null) ? defaultValue : val;
  }

  public Object setProperty(String key, String value)
  {
    return put(key, value);
  }

  public Map< ? extends String, ? extends Object> getDefaults()
  {
    return defaults;
  }

  public void setDefaults(Map< ? extends String, ? extends Object> defaults)
  {
    this.defaults = defaults;
  }
}
