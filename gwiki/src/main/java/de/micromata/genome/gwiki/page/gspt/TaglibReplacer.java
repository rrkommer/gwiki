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
public class TaglibReplacer extends ReplacerBase
{
  GsptPreprocessor processor;

  public TaglibReplacer(GsptPreprocessor processor)
  {
    this.processor = processor;
  }

  public String getEnd()
  {
    return "@>";
  }

  public String getStart()
  {
    return "<@tag";
  }

  public String replace(ReplacerContext ctx, Map<String, String> attr, boolean isClosed)
  {
    String cname = attr.get("class");
    String tname = attr.get("name");
    processor.addTagLib(tname, cname);
    return "";
  }

}
