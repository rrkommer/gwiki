/////////////////////////////////////////////////////////////////////////////
//
// Project   DHL-ParcelOnlinePostage
//
// Author    roger@micromata.de
// Created   12.11.2009
// Copyright Micromata 12.11.2009
//
/////////////////////////////////////////////////////////////////////////////
package de.micromata.genome.gwiki.utils;

import java.io.IOException;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.io.IOUtils;
import org.apache.commons.io.output.ByteArrayOutputStream;
import org.apache.commons.lang.StringUtils;

import de.micromata.genome.util.runtime.RuntimeIOException;

/**
 * utils to deal with properties.
 * 
 * @author Roger Rene Kommer (r.kommer@micromata.de)
 * 
 */
public class PropUtils
{
  /**
   * Replaces ${} expressions.
   * 
   * @param text
   * @param keyValues key and values at even and odd position.
   * @return
   */
  public static String eval(String text, String... keyValues)
  {
    for (int i = 0; i < keyValues.length; ++i) {
      String search = "${" + keyValues[i] + "}";
      if (keyValues.length <= i + 1) {
        return text;
      }
      String repl = keyValues[++i];
      text = StringUtils.replace(text, search, repl);
    }
    return text;
  }

  public static final String PROPS_ENCODING = "ISO-8859-1";

  public static Properties toProperties(String text)
  {
    Properties props = new Properties();
    if (StringUtils.isEmpty(text) == true) {
      return props;
    }
    try {
      props.load(IOUtils.toInputStream(text, PROPS_ENCODING));
      return props;
    } catch (IOException ex) {
      throw new RuntimeIOException("Failed to parse Property file: " + ex.getMessage(), ex);
    }
  }

  public static String fromProperties(Properties props)
  {
    ByteArrayOutputStream bout = new ByteArrayOutputStream();
    try {
      props.store(bout, "");
      return new String(bout.toByteArray(), PROPS_ENCODING);
    } catch (Exception ex) {
      throw new RuntimeException(ex);
    }
  }

  @SuppressWarnings("unchecked")
  public static String fromProperties(Map<String, String> map)
  {
    Object om = map;
    if (om instanceof Properties) {
      return fromProperties((Properties) om);
    }
    Properties props = new Properties();
    props.putAll((Map) map);
    return fromProperties(props);
  }
}
