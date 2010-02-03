/////////////////////////////////////////////////////////////////////////////
//
// Project   DHL-ParcelOnlinePostage
//
// Author    roger@micromata.de
// Created   16.12.2006
// Copyright Micromata 16.12.2006
//
/////////////////////////////////////////////////////////////////////////////
package de.micromata.genome.gwiki.page.gspt;

import java.util.Map;

import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.tagext.TagInfo;
import javax.servlet.jsp.tagext.TagLibraryInfo;

import de.micromata.genome.gwiki.page.gspt.taglibs.TagLibraryInfoImpl;



public class JTagLibReplacer extends ReplacerBase
{

  protected GsptPreprocessor processor;

  public JTagLibReplacer(GsptPreprocessor processor)
  {
    this.processor = processor;
  }

  public String getEnd()
  {
    return "%>";
  }

  public String getStart()
  {
    return "<%@ taglib";
  }

  protected TagLibraryInfo createTagLibraryInfo(PageContext pageContext, String prefix, String uri)
  {
    return new TagLibraryInfoImpl(pageContext, prefix, uri);
  }

  public String replace(ReplacerContext ctx, Map<String, String> attr, boolean isClosed)
  {
    String uri = attr.get("uri");
    String prefix = attr.get("prefix");
    PageContext pageContext = processor.getPageContext();
    TagLibraryInfo tli = createTagLibraryInfo(pageContext, prefix, uri);
    for (TagInfo ti : tli.getTags()) {
      if (ti.getTagClassName() == null) {
        continue;
      }
      processor.addTagLib(prefix, ti);
    }
    processor.addPrefixCheck(prefix);
    return "";
  }
}
