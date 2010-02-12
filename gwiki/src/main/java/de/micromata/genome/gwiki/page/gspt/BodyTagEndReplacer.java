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
public class BodyTagEndReplacer extends TagReplacer
{

  public BodyTagEndReplacer(String tagName, Class< ? > tagClass, boolean evaluateELViaGroovy)
  {
    super(tagName, tagClass, evaluateELViaGroovy);
  }

  @Override
  public String getEnd()
  {
    return ">";
  }

  @Override
  public String getStart()
  {
    return "</" + tagName;
  }

  @Override
  public String replace(ReplacerContext ctx, Map<String, String> attr, boolean isClosed)
  {
    String tagSupport = TagSupport.class.getName();
    StringBuilder sb = new StringBuilder();
    sb.append("<% /* seof ").append(tagName).append(" */\n").append("     if (").append(tagSupport).append(
        ".continueAfterBody(pageContext) == false)\n").append("        break;\n").append("    }\n").append("    ").append(tagSupport)
        .append(".afterBody(this, pageContext);\n").append("  }\n")
        // .append(" out = pageContext.getInternalGroovyOut();\n")
        .append("  if (").append(tagSupport).append(".endTag(this, pageContext) == false)\n").append("    return;\n").append("/* eeof ")
        .append(tagName).append("*/ %>") //
    ;
    return sb.toString();
  }
}
