/////////////////////////////////////////////////////////////////////////////
//
// Project   Micromata Genome Core
//
// Author    roger@micromata.de
// Created   20.01.2008
// Copyright Micromata 20.01.2008
//
/////////////////////////////////////////////////////////////////////////////
package de.micromata.genome.util.text;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import de.micromata.genome.util.types.Converter;

public class PipeValueList
{
  public static String escape(String k)
  {
    k = StringUtils.replace(k, "\\", "\\\\");
    k = StringUtils.replace(k, "|", "\\|");
    k = StringUtils.replace(k, "=", "\\=");
    return k;

  }

  public static String encode(Map<String, String> map)
  {
    if (map == null)
      return "";
    StringBuilder sb = new StringBuilder();
    boolean isFirst = true;
    for (Map.Entry<String, String> me : map.entrySet()) {
      if (isFirst == false)
        sb.append("|");
      isFirst = false;
      String k = me.getKey();
      k = escape(k);
      sb.append(k).append("=");
      String v = me.getValue();
      v = escape(v);
      sb.append(v);
    }
    return sb.toString();
  }

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
    // }
    // if (text.charAt(text.length() - 2) != '\\') {// falls nicht am Ende escaped wurde, timmen
    // text = StringUtils.trim(text);
    // } else {
    // text = StringUtils.substring(text, 0, text.length() - 2) + StringUtils.substring(text, text.length() - 1, text.length()); // fallls.
    // escaped
    // wurde,
    // escape
    // zeichen
    // verwerfen
    // }

    // return text;
  }



  /**
   * der Text wird getrimmt. Es sei denn, an letzter Stelle ist ein escapetes(mit Backsclash) Zeichen.
   * 
   * aus "a=b|c=d\\\n" wird a=b|c=d\n und aus "a=b|c=d\n" wird a=b|c=d
   * 
   * 
   * 
   * @param text
   * @return
   */
  public static Map<String, String> decode(String text)
  {
    Map<String, String> map = new HashMap<String, String>();
    if (StringUtils.isEmpty(text) == true)
      return map;

    text = trim(text);

    List<String> tlist = Converter.parseStringTokens(text, "|\\=", true);
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
        if (state != State.ParseKey)
          throw new IllegalStateException("Parsing '=' in state: " + state);
        curKey = sb.toString();
        sb = new StringBuilder();
        state = State.ParseValue;
      } else if ("|".equals(t) == true) {
        if (state != State.ParseValue)
          throw new IllegalStateException("Parsing '=' in state: " + state);
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
