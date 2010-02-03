/////////////////////////////////////////////////////////////////////////////
//
// Project   DHL-ParcelOnlinePostage
//
// Author    roger@micromata.de
// Created   09.11.2009
// Copyright Micromata 09.11.2009
//
/////////////////////////////////////////////////////////////////////////////
package de.micromata.genome.gwiki.page.impl.wiki;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import de.micromata.genome.util.types.Converter;

public class MacroAttributesUtils
{
  private static enum State
  {
    ParseKey, ParseValue,
  };

  protected static String trim(String text)
  {
    if (text.length() < 3) {
      return text;
    }
    String ttext = StringUtils.trim(text);
    if (ttext.length() > 0 && ttext.endsWith("\\")) {
      return text;
    }

    return ttext;
  }

  public static Map<String, String> decode(String text)
  {
    Map<String, String> map = new HashMap<String, String>();
    if (StringUtils.isEmpty(text) == true)
      return map;

    text = trim(text);

    List<String> tlist = Converter.parseStringTokens(text, "|\\=", true);
    if (tlist.size() == 0)
      return map;
    if (tlist.size() == 1) {
      map.put(MacroAttributes.DEFAULT_VALUE_KEY, tlist.get(0));
      return map;
    }
    StringBuilder sb = new StringBuilder();
    String curKey = null;
    String curValue = null;
    State state = State.ParseKey;
    for (int i = 0; i < tlist.size(); ++i) {
      String t = tlist.get(i);
      if ("\\".equals(t) == true) {
        ++i;
        t = tlist.get(i);
        sb.append(t);
        continue;
      } else if ("=".equals(t) == true) {
        if (state == State.ParseValue) {
          sb.append(t);
          continue;
        }
        curKey = sb.toString();
        sb = new StringBuilder();
        state = State.ParseValue;
      } else if ("|".equals(t) == true) {
        // if (state != State.ParseValue) {
        // //eigentlich fehler
        // throw new IllegalStateException("Parsing '|' in state: " + state);
        // }
        curValue = sb.toString();
        map.put(curKey, curValue);
        sb = new StringBuilder();
        curKey = null;
        curValue = null;
        state = State.ParseKey;
      } else {
        sb.append(t);
      }
    }
    if (curKey != null) {
      curValue = sb.toString();
      map.put(curKey, StringUtils.defaultString(curValue));
    }
    return map;
  }
}
