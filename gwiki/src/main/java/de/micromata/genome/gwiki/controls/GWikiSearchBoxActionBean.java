/////////////////////////////////////////////////////////////////////////////
//
// Project   DHL-ParcelOnlinePostage
//
// Author    roger@micromata.de
// Created   14.11.2009
// Copyright Micromata 14.11.2009
//
/////////////////////////////////////////////////////////////////////////////
package de.micromata.genome.gwiki.controls;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.StringUtils;

import de.micromata.genome.gwiki.page.search.QueryResult;
import de.micromata.genome.gwiki.page.search.SearchQuery;
import de.micromata.genome.gwiki.page.search.SearchResult;
import de.micromata.genome.gwiki.utils.WebUtils;

/**
 * Ajax ActionBean for searching.
 * 
 * @author roger@micromata.de
 * 
 */
public class GWikiSearchBoxActionBean extends GWikiPageListActionBean
{
  private String pageId;

  private String pageType;

  public GWikiSearchBoxActionBean()
  {

  }

  @Override
  public Object onInit()
  {
    return onLinkAutocomplete();
  }

  public void renderSearchOps(String query)
  {
    StringBuilder sb = new StringBuilder();
    String searchUrl = wikiContext.localUrl("/edit/Search") + "?method_onSearch=go&se=" + WebUtils.encodeUrlParam(query);
    sb.append(searchUrl).append("|").append(StringEscapeUtils.escapeHtml("Globale Suche nach \"" + query + "\"\n"));
    if (StringUtils.isNotBlank(pageId) == true) {
      searchUrl += "&childs=" + pageId;
      sb.append(searchUrl).append("|").append(StringEscapeUtils.escapeHtml("Lokale Suche nach \"" + query + "\"\n"));
    }
    wikiContext.append(sb.toString());
  }

  public Object onLinkAutocomplete()
  {
    String q = wikiContext.getRequestParameter("q");
    String pageType = wikiContext.getRequestParameter("pageType");
    String queryexpr = "prop:NOINDEX = false and (prop:PAGEID ~ \"" + q + "\" or prop:TITLE ~ \"" + q + "\")";
    if (StringUtils.isNotEmpty(pageType) == true) {
      queryexpr = "prop:TYPE = " + pageType + " and (" + queryexpr + ")";
    }
    renderSearchOps(q);

    SearchQuery query = new SearchQuery(queryexpr, wikiContext.getWikiWeb().getPageInfos());

    query.setMaxCount(1000);
    QueryResult qr = filter(query);
    StringBuilder sb = new StringBuilder();
    // int size = qr.getResults().size();
    for (SearchResult sr : qr.getResults()) {
      if (sr.getElementInfo().isViewable() == false) {
        continue;
      }
      String t = wikiContext.getTranslatedProp(StringEscapeUtils.escapeHtml(sr.getElementInfo().getTitle()))
          + "<br/>("
          + StringEscapeUtils.escapeHtml(sr.getPageId())
          + ")";
      sb.append(wikiContext.localUrl(sr.getPageId())).append("|").append(t).append("\n");
    }
    wikiContext.append(sb.toString());
    wikiContext.flush();
    return noForward();
  }

  public String getPageId()
  {
    return pageId;
  }

  public void setPageId(String pageId)
  {
    this.pageId = pageId;
  }

  public String getPageType()
  {
    return pageType;
  }

  public void setPageType(String pageType)
  {
    this.pageType = pageType;
  }

}
