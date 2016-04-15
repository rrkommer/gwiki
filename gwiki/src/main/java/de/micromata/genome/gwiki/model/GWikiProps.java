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
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.TimeZone;

import org.apache.commons.lang.StringUtils;

import de.micromata.genome.gwiki.page.impl.wiki.MacroAttributesUtils;
import de.micromata.genome.gwiki.utils.CommaListParser;
import de.micromata.genome.gwiki.utils.Internalizator;
import de.micromata.genome.gwiki.utils.InternalizedHashMap;
import de.micromata.genome.gwiki.utils.NopInternalizator;

/**
 * Wrapper to a property (string/string) map.
 * 
 * @author Roger Rene Kommer (r.kommer@micromata.de)
 * 
 */
public class GWikiProps implements Serializable
{

  /**
   * The Constant serialVersionUID.
   */
  private static final long serialVersionUID = 6530388862209381595L;

  /**
   * The Constant UTC_TIMEZONE.
   */
  public static final TimeZone UTC_TIMEZONE = TimeZone.getTimeZone("UTC");

  /**
   * The internal timestamp.
   */
  public static ThreadLocal<SimpleDateFormat> internalTimestamp = new ThreadLocal<SimpleDateFormat>()
  {

    @Override
    protected SimpleDateFormat initialValue()
    {
      SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmssSSS");
      sdf.setTimeZone(UTC_TIMEZONE);
      return sdf;
    }
  };

  /**
   * convert Stringified date to internal Date.
   *
   * @param date the date
   * @return the date
   * 
   *         Todo use utc.
   */
  public static Date string2date(String date)
  {
    if (StringUtils.isEmpty(date) == true) {
      return null;
    }
    try {
      Date ld = internalTimestamp.get().parse(date);
      return ld;
    } catch (ParseException ex) {
      throw new RuntimeException("Cannot parse date: " + date + "; " + ex.getMessage(), ex);

    }
  }

  /**
   * Date2string.
   *
   * @param date the date
   * @return the string
   */
  public static String date2string(Date date)
  {
    if (date == null) {
      return null;
    }
    return internalTimestamp.get().format(date);
  }

  /**
   * The map.
   */
  private Map<String, String> map;

  /**
   * Instantiates a new g wiki props.
   */
  public GWikiProps()
  {
    this.map = new HashMap<String, String>();
  }

  /**
   * Instantiates a new g wiki props.
   *
   * @param keyInternalizator the key internalizator
   */
  public GWikiProps(Internalizator<String> keyInternalizator)
  {
    this.map = new InternalizedHashMap<String, String>(keyInternalizator, new NopInternalizator<String>());
  }

  /**
   * Instantiates a new g wiki props.
   *
   * @param keyInternalizator the key internalizator
   * @param valueInternalizator the value internalizator
   */
  public GWikiProps(Internalizator<String> keyInternalizator, Internalizator<String> valueInternalizator)
  {
    this.map = new InternalizedHashMap<String, String>(keyInternalizator, valueInternalizator);
  }

  /**
   * Instantiates a new g wiki props.
   *
   * @param other the other
   */
  public GWikiProps(GWikiProps other)
  {
    if (other.getMap() instanceof InternalizedHashMap) {
      InternalizedHashMap<String, String> om = (InternalizedHashMap<String, String>) other.getMap();
      this.map = new InternalizedHashMap<String, String>(om.getKeyInternatizator(), om.getValueInternatizator());
    } else {
      this.map = new HashMap<String, String>();
    }
    map.putAll(other.getMap());
  }

  /**
   * Instantiates a new g wiki props.
   *
   * @param map the map
   */
  public GWikiProps(Map<String, String> map)
  {
    this.map = map;
  }

  /**
   * Instantiates a new g wiki props.
   *
   * @param map the map
   */
  @SuppressWarnings({ "unchecked", "rawtypes" })
  public GWikiProps(Properties map)
  {
    this.map = (Map) map;
  }

  @Override
  public String toString()
  {
    return map.toString();
  }

  /**
   * Size.
   *
   * @return the int
   */
  public int size()
  {
    return map.size();
  }

  public boolean isEmpty()
  {
    return map.isEmpty();
  }

  /**
   * Contains key.
   *
   * @param key the key
   * @return true, if successful
   */
  public boolean containsKey(String key)
  {
    return map.containsKey(key);
  }

  /**
   * Removes the.
   *
   * @param key the key
   * @return the string
   */
  public String remove(String key)
  {
    return map.remove(key);
  }

  /**
   * Format time stamp.
   *
   * @param date the date
   * @return the string
   */
  public static String formatTimeStamp(Date date)
  {
    if (date == null) {
      return null;
    }
    return date2string(date);
  }

  /**
   * Parses the time stamp.
   *
   * @param d the d
   * @return the date
   */
  public static Date parseTimeStamp(String d)
  {
    if (StringUtils.isBlank(d) == true) {
      return null;
    }
    return string2date(d);

  }

  /**
   * Gets the string value.
   *
   * @param key the key
   * @return the string value
   */
  public String getStringValue(String key)
  {
    return map.get(key);
  }

  /**
   * Gets the string value.
   *
   * @param key the key
   * @param defaultValue the default value
   * @return the string value
   */
  public String getStringValue(String key, String defaultValue)
  {
    String ret = map.get(key);
    if (ret != null) {
      return ret;
    }
    return defaultValue;
  }

  /**
   * Sets the string value.
   *
   * @param key the key
   * @param value the value
   */
  public void setStringValue(String key, String value)
  {
    map.put(key, value);
  }

  /**
   * Gets the string list.
   *
   * @param key the key
   * @return the string list
   */
  public List<String> getStringList(String key)
  {
    String val = map.get(key);
    if (StringUtils.isEmpty(val) == true) {
      return Collections.emptyList();
    }
    return CommaListParser.parseCommaList(map.get(key));
  }

  /**
   * Sets the string list.
   *
   * @param key the key
   * @param list the list
   */
  public void setStringList(String key, List<String> list)
  {
    setStringValue(key, CommaListParser.encode(list));
  }

  /**
   * Gets the string value map.
   *
   * @param key the key
   * @return the string value map
   */
  public GWikiProps getStringValueMap(String key)
  {
    String argstr = getStringValue(key);
    if (StringUtils.isEmpty(argstr) == true) {
      return new GWikiProps();
    }
    return new GWikiProps(MacroAttributesUtils.decode(argstr));
  }

  /**
   * Sets the date value.
   *
   * @param key the key
   * @param date the date
   */
  public void setDateValue(String key, Date date)
  {
    if (date == null) {
      map.remove(key);
    } else {
      map.put(key, date2string(date));
    }
  }

  /**
   * Gets the date value.
   *
   * @param key the key
   * @return the date value
   */
  public Date getDateValue(String key)
  {
    String sv = map.get(key);
    if (StringUtils.isBlank(sv) == true) {
      return null;
    }
    return string2date(sv);
  }

  /**
   * Gets the boolean value.
   *
   * @param key the key
   * @return the boolean value
   */
  public boolean getBooleanValue(String key)
  {
    return getBooleanValue(key, false);
  }

  /**
   * Gets the boolean value.
   *
   * @param key the key
   * @param defaultValue the default value
   * @return the boolean value
   */
  public boolean getBooleanValue(String key, boolean defaultValue)
  {
    String val = map.get(key);
    if (StringUtils.isEmpty(val) == true) {
      return defaultValue;
    }
    return StringUtils.equals(val, "true");
  }

  /**
   * Sets the boolean value.
   *
   * @param key the key
   * @param value the value
   */
  public void setBooleanValue(String key, boolean value)
  {
    String val = value ? "true" : "false";
    map.put(key, val);
  }

  /**
   * Gets the int value.
   *
   * @param key the key
   * @param defaultValue the default value
   * @return the int value
   */
  public int getIntValue(String key, int defaultValue)
  {
    String val = map.get(key);
    if (StringUtils.isEmpty(val) == true) {
      return defaultValue;
    }
    try {
      return Integer.parseInt(val);
    } catch (NumberFormatException ex) {
      return defaultValue;
    }
  }

  /**
   * Sets the int value.
   *
   * @param key the key
   * @param value the value
   */
  public void setIntValue(String key, int value)
  {
    map.put(key, Integer.toString(value));
  }

  /**
   * Gets the long value.
   *
   * @param key the key
   * @param defaultValue the default value
   * @return the long value
   */
  public long getLongValue(String key, long defaultValue)
  {
    String val = map.get(key);
    if (StringUtils.isEmpty(val) == true) {
      return defaultValue;
    }
    try {
      return Long.parseLong(val);
    } catch (NumberFormatException ex) {
      return defaultValue;
    }
  }

  /**
   * Sets the long value.
   *
   * @param key the key
   * @param value the value
   */
  public void setLongValue(String key, long value)
  {
    map.put(key, Long.toString(value));
  }

  // etc.

  public Map<String, String> getMap()
  {
    return map;
  }

  public void setMap(Map<String, String> map)
  {
    this.map = map;
  }
}
