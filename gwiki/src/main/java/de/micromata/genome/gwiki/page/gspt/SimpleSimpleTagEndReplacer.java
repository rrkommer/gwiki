/////////////////////////////////////////////////////////////////////////////
//
// Project   DHL-ParcelOnlinePostage
//
// Author    jens@micromata.de
// Created   19.11.2006
// Copyright Micromata 19.11.2006
//
/////////////////////////////////////////////////////////////////////////////
package de.micromata.genome.gwiki.page.gspt;

import java.util.Map;

/**
 * Internal implementation for jsp/GSPT-Parsing.
 * 
 * @author Roger Rene Kommer (r.kommer@micromata.de)
 * 
 */
public class SimpleSimpleTagEndReplacer extends TagReplacer
{

  public SimpleSimpleTagEndReplacer(String tagName, Class< ? > tagClass, boolean evaluateELViaGroovy)
  {
    super(tagName, tagClass, evaluateELViaGroovy);
  }

  public String getEnd()
  {
    return ">";
  }

  public String getStart()
  {
    return "</" + tagName;
  }

  public String replace(ReplacerContext ctx, Map<String, String> attr, boolean isClosed)
  {
    StringBuilder sb = new StringBuilder();
    sb.append("<% });\n %>");
    return sb.toString();
  }
}
