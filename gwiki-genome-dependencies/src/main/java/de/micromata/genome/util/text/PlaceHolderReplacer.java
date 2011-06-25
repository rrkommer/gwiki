/////////////////////////////////////////////////////////////////////////////
//
// Project   DHL-ParcelOnlinePostage
//
// Author    roger@micromata.de
// Created   06.12.2009
// Copyright Micromata 06.12.2009
//
/////////////////////////////////////////////////////////////////////////////
package de.micromata.genome.util.text;

import java.util.Map;

/**
 * Utility to replace variables. TODO maybe buggy. Check agains StringVarResolver from labelengine
 * 
 * @author Roger Rene Kommer (r.kommer@micromata.de)
 * 
 */
public class PlaceHolderReplacer
{
  public static String resolveReplace(String text, String start, String end, StringResolver resolver)
  {
    if (text == null) {
      return text;
    }
    int idx = text.indexOf(start);
    if (idx == -1) {
      return text;
    }
    int eidx = 0;
    StringBuilder sb = new StringBuilder();
    do {
      int neidx = text.indexOf(end, idx + start.length());
      if (neidx == -1) {
        break;
      }
      String k = text.substring(idx + start.length(), neidx);

      sb.append(text.substring(eidx, idx));
      sb.append(resolver.resolve(k));
      eidx = neidx + end.length();
      idx = text.indexOf(start, eidx);
    } while (idx != -1);
    sb.append(text.substring(eidx));
    return sb.toString();
  }

  /**
   * replaces ${} variables with map content.
   * 
   * @param text
   * @param context
   * @return
   */
  public static String resolveReplaceDollarVars(String text, final Map<String, String> context)
  {
    return resolveReplace(text, "${", "}", new StringResolver() {

      public String resolve(String placeholder)
      {
        return context.get(placeholder);
      }
    });
  }
}
