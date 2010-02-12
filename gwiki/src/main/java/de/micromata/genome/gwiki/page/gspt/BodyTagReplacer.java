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
 * @author roger
 * 
 */
public class BodyTagReplacer extends TagReplacer
{
  public BodyTagReplacer(String tagName, Class< ? > tagClass, boolean evaluateELViaGroovy)
  {
    super(tagName, tagClass, evaluateELViaGroovy);
  }

  @Override
  public String replace(ReplacerContext ctx, Map<String, String> attr, boolean isClosed)
  {
    String a = attributesToGroovyArray(attr);
    StringBuilder sb = new StringBuilder();
    String tagSupport = TagSupport.class.getName();
    if (isClosed == true) {
      sb.append("<% if (").append(tagSupport).append(".closedBodyTag(this, new ").append(tagClass.getName()).append("(), ").append(a)
          .append(", pageContext) == false) return;\n %>");
    } else {
      sb.append("<%  if (").append(tagSupport).append(".initTag(this, new ").append(tagClass.getName()).append("(), ").append(a).append(
          ", pageContext)) {\n");
      // sb.append(" out = pageContext.getInternalGroovyOut();\n");
      sb.append("      while(true) {\n");
      sb.append("%>");
    }
    // if (isClosed == true)
    // sb.append(new BodyTagEndReplacer(tagName, tagClass, isEvaluateELViaGroovy()).replace(attr, true));
    return sb.toString();
  }
}
