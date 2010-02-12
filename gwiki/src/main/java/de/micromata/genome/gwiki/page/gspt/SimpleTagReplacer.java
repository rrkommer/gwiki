/////////////////////////////////////////////////////////////////////////////
//
// Project   DHL-ParcelOnlinePostage
//
// Author    roger@micromata.de
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
public class SimpleTagReplacer extends TagReplacer
{

  public SimpleTagReplacer(String tagName, Class< ? > tagClass, boolean evaluateELViaGroovy)
  {
    super(tagName, tagClass, evaluateELViaGroovy);
  }

  @Override
  public String replace(ReplacerContext ctx, Map<String, String> attr, boolean isClosed)
  {
    String a = attributesToGroovyArray(attr);
    StringBuilder sb = new StringBuilder();
    sb.append("<%  if (").append(TagSupport.class.getName()).append(".initSimpleTag(new ").append(tagClass.getName()).append("(), ")
        .append(a).append(", pageContext) == false) \n").append("      return;\n").append("%>\n");

    return sb.toString();
  }
}
