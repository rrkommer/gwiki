/////////////////////////////////////////////////////////////////////////////
//
// Project   DHL-ParcelOnlinePostage
//
// Author    roger@micromata.de
// Created   19.10.2009
// Copyright Micromata 19.10.2009
//
/////////////////////////////////////////////////////////////////////////////
package de.micromata.genome.gwiki.page.gspt;

import java.util.Map;

import org.apache.commons.lang.StringUtils;

import de.micromata.genome.gwiki.model.GWikiArtefakt;
import de.micromata.genome.gwiki.model.GWikiElement;
import de.micromata.genome.gwiki.model.GWikiTextArtefakt;
import de.micromata.genome.gwiki.page.GWikiContext;

public class GWikiIncludeReplacer extends RegExpReplacer
{
  private GWikiContext context;

  public GWikiIncludeReplacer(GWikiContext context)
  {
    this();
    this.context = context;
  }

  protected GWikiIncludeReplacer()
  {
    super("(.*?)(<%?@\\s*include\\s+)(.*)", "(.*?)([@%]>)(.*)");
  }

  @Override
  public String getStart()
  {
    return "<@include";
  }

  @Override
  public String getEnd()
  {
    return "@>";
  }

  public String externalInclude(Map<String, String> attr, String fn)
  {
    return "<% wikiContext.include(\"" + fn + "\"); %>\n";
  }

  public String replace(ReplacerContext ctx, Map<String, String> attr, boolean isClosed)
  {
    String fn = attr.get("file");
    String embedd = attr.get("embedd");
    boolean doEmbedd = StringUtils.equals(embedd, "false") == false;
    if (doEmbedd == false)
      return externalInclude(attr, fn);

    GWikiContext wctx = (GWikiContext) ctx.getAttribute("wikiContext");
    String id = fn;
    if (id.endsWith(".gspt") == true) {
      id = id.substring(0, id.length() - ".gspt".length());
    }
    if (id.startsWith("/") == true)
      id = id.substring(1);
    GWikiElement el = wctx.getWikiWeb().getElement(id);
    GWikiArtefakt fact = el.getMainPart();
    if (fact instanceof GWikiTextArtefakt) {
      GWikiTextArtefakt text = (GWikiTextArtefakt) fact;
      return text.getStorageData();
    } else {
      return "<% wikiContext.includeText('" + id + "'); %>";
    }
  }

}
