/////////////////////////////////////////////////////////////////////////////
//
// Project   DHL-ParcelOnlinePostage
//
// Author    roger@micromata.de
// Created   21.11.2006
// Copyright Micromata 21.11.2006
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
public class GUrlReplacer extends ReplacerBase
{

  public String getEnd()
  {
    return "/>";
  }

  public String getStart()
  {
    return "<g:url";
  }

  public String replace(ReplacerContext ctx, Map<String, String> attr, boolean isClosed)
  {
    String url = attr.get("value");
    return "<%= pageContext.globalUrl(\"" + url + "\") %>";
  }

}
