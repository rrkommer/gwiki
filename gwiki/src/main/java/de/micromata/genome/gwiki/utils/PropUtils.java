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

package de.micromata.genome.gwiki.utils;

import java.io.IOException;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.io.IOUtils;
import org.apache.commons.io.output.ByteArrayOutputStream;
import org.apache.commons.lang.StringUtils;

import de.micromata.genome.util.collections.OrderedProperties;
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

  public static OrderedProperties toProperties(String text)
  {
    OrderedProperties props = new OrderedProperties();
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

  public static String fromProperties(Map<String, String> map)
  {
    Object om = map;
    if (om instanceof Properties) {
      return fromProperties((Properties) om);
    }
    Properties props = new Properties();
    props.putAll(map);
    return fromProperties(props);
  }
}
