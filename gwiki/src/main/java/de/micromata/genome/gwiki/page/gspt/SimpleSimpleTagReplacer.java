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

public class SimpleSimpleTagReplacer extends TagReplacer
{

  public SimpleSimpleTagReplacer(String tagName, Class< ? > tagClass, boolean evaluateELViaGroovy)
  {
    super(tagName, tagClass, evaluateELViaGroovy, true);
  }

  @Override
  public String replace(ReplacerContext ctx, Map<String, String> attr, boolean isClosed)
  {
    String a = attributesToGroovyArray(attr);
    StringBuilder sb = new StringBuilder();
    sb.append("<%  ").append(TagSupport.class.getName()).append(".initSimpleSimpleTag(new ").append(tagClass.getName()).append("(), ")
        .append(a).append(", pageContext, { \n").append("%>\n");
    if (isClosed == true) {
      sb.append(new SimpleSimpleTagEndReplacer(tagName, tagClass, isEvaluateELViaGroovy()).replace(ctx, attr, true));
    }
    return sb.toString();
  }
}
