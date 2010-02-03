/////////////////////////////////////////////////////////////////////////////
//
// Project   DHL-ParcelOnlinePostage
//
// Author    roger@micromata.de
// Created   17.11.2009
// Copyright Micromata 17.11.2009
//
/////////////////////////////////////////////////////////////////////////////
package de.micromata.genome.gwiki.page.impl.wiki.filter;

import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import de.micromata.genome.gwiki.model.GWikiElementInfo;
import de.micromata.genome.gwiki.model.filter.GWikiFilterChain;
import de.micromata.genome.gwiki.model.filter.GWikiWikiPageCompileFilter;
import de.micromata.genome.gwiki.model.filter.GWikiWikiPageCompileFilterEvent;
import de.micromata.genome.gwiki.page.RenderModes;
import de.micromata.genome.gwiki.page.impl.GWikiContent;
import de.micromata.genome.gwiki.page.impl.GWikiWikiPageArtefakt;
import de.micromata.genome.util.types.Pair;

public class GWikiWikiPageRenderKeywordLinkFilter implements GWikiWikiPageCompileFilter
{
  public Void filter(GWikiFilterChain<Void, GWikiWikiPageCompileFilterEvent, GWikiWikiPageCompileFilter> chain,
      GWikiWikiPageCompileFilterEvent event)
  {
    chain.nextFilter(event);
    int renderMode = event.getWikiContext().getRenderMode();
    if (RenderModes.NoLinks.isSet(renderMode) == true
        || RenderModes.ForText.isSet(renderMode) == true
        || RenderModes.ForRichTextEdit.isSet(renderMode) == true) {
      return null;
    }
    GWikiWikiPageArtefakt a = event.getWikiPageArtefakt();
    GWikiContent content = a.getCompiledObject();
    GWikiKeywordLoadElementInfosFilter fe = GWikiKeywordLoadElementInfosFilter.getInstance();
    if (fe != null && content != null) {
      String space = event.getElement().getElementInfo().getWikiSpace(event.getWikiContext());
      Map<String, Pair<Pattern, List<GWikiElementInfo>>> spaceKeyWords = fe.getKeywords(event.getWikiContext()).get(space);
      if (spaceKeyWords != null) {
        content.iterate(new KeyWordReplaceVisitor(spaceKeyWords));
      }
    }
    return null;
  }

}
