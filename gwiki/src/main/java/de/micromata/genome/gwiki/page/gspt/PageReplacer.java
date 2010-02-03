/////////////////////////////////////////////////////////////////////////////
//
// Project   DHL-ParcelOnlinePostage
//
// Author    roger@micromata.de
// Created   07.01.2007
// Copyright Micromata 07.01.2007
//
/////////////////////////////////////////////////////////////////////////////
package de.micromata.genome.gwiki.page.gspt;

import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import de.micromata.genome.util.types.Converter;

public class PageReplacer extends RegExpReplacer
{
  public PageReplacer()
  {
    super("(.*?)(<%?@\\s*page\\s+)(.*)", "(.*?)([@%]>)(.*)");
  }

  public String replace(ReplacerContext ctx, Map<String, String> attr, boolean isClosed)
  {
    String contentType = attr.get("contentType");
    String imports = attr.get("import");
    StringBuilder sb = new StringBuilder();
    // TODO test
    if (StringUtils.isNotBlank(contentType) == true && StringUtils.isBlank(imports) == true) {
      sb.append("<% pageContext.getResponse().setContentType(\"" + contentType + "\"); %>");
      // uebernommen
    }

    if (StringUtils.isNotBlank(imports) == true) {
      List<String> imps = Converter.parseStringTokens(imports, ", ", false);
      sb.append("<%# ");
      for (String im : imps) {
        sb.append("  import ").append(im).append(";\n");
      }
      sb.append("%>");
    }
    String isELIgnored = attr.get("isELIgnored");
    if (StringUtils.isNotBlank(isELIgnored) == true) {
      sb.append("<% pageContext.setEvaluateTagAttributes(").append(isELIgnored).append(" == false); %>");
    }
    String useParentTagContext = attr.get("useParentTagContext");
    if (StringUtils.isNotBlank(useParentTagContext) == true) {
      sb.append("<% pageContext.setUseParentTagContext(").append(useParentTagContext).append("); %>");
    }
    return sb.toString();
  }
}
