/////////////////////////////////////////////////////////////////////////////
//
// Project   DHL-ParcelOnlinePostage
//
// Author    roger@micromata.de
// Created   21.10.2009
// Copyright Micromata 21.10.2009
//
/////////////////////////////////////////////////////////////////////////////
package de.micromata.genome.gwiki.model;

import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.StringUtils;

import de.micromata.genome.gwiki.page.GWikiContext;
import de.micromata.genome.gwiki.page.search.GWikiIndexedArtefakt;
import de.micromata.genome.gwiki.page.search.WordCallback;
import de.micromata.genome.gwiki.utils.AppendableI;

public class GWikiSettingsPropsArtefakt extends GWikiPropsArtefakt implements GWikiIndexedArtefakt
{
  private static final long serialVersionUID = -7444414246579867245L;

  public GWikiSettingsPropsArtefakt()
  {

  }

  public GWikiSettingsPropsArtefakt(GWikiProps props)
  {
    super(props);
  }

  public void getPreview(GWikiContext ctx, AppendableI sb)
  {
    // StringBuilder sb = new StringBuilder();
    Map<String, String> m = getStorageData();
    String title = m.get(GWikiPropKeys.TITLE);
    if (StringUtils.isNotBlank(title) == true) {
      title = ctx.getTranslatedProp(title);
      sb.append("<h1 class=\"gwikititle\">").append(StringEscapeUtils.escapeHtml(title)).append("</h1>");
    }
    List<String> keywords = new GWikiProps(m).getStringList(GWikiPropKeys.KEYWORDS);
    if (keywords != null && keywords.isEmpty() == false) {
      sb.append("<ul>");
      for (String kw : keywords) {
        sb.append("<li class=\"gwikikeyword\">").append(StringEscapeUtils.escapeHtml(kw)).append("</li>\n");
      }
      sb.append("</ul>");
    }
  }
}
