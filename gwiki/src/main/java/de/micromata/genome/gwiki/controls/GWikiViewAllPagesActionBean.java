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

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import de.micromata.genome.gdbfs.ZipWriteFileSystem;
import de.micromata.genome.gwiki.model.GWikiAuthorizationRights;
import de.micromata.genome.gwiki.model.GWikiElement;
import de.micromata.genome.gwiki.model.GWikiElementInfo;
import de.micromata.genome.gwiki.model.GWikiWeb;
import de.micromata.genome.gwiki.page.search.QueryResult;
import de.micromata.genome.gwiki.page.search.SearchQuery;
import de.micromata.genome.gwiki.page.search.SearchResult;
import de.micromata.genome.gwiki.spi.storage.GWikiFileStorage;
import de.micromata.genome.util.runtime.RuntimeIOException;

/**
 * ActionBean to show/filter all pages in the wiki.
 * 
 * @author Roger Rene Kommer (r.kommer@micromata.de)
 * 
 */
public class GWikiViewAllPagesActionBean extends GWikiPageListActionBean
{
  protected Boolean canViewNonViewableElements = null;

  private boolean withExport;

  /**
   * Will only used in onExport.
   */
  private String filterparams;

  private boolean withArchive;

  private transient boolean inExport = false;

  public GWikiViewAllPagesActionBean()
  {

  }

  public Map<String, String> decode(String search)
  {
    if (StringUtils.isEmpty(search) == true) {
      return Collections.emptyMap();
    }
    Map<String, String> parmsMap = new HashMap<String, String>();
    String params[] = search.split("&");

    for (String param : params) {
      String temp[] = param.split("=");
      if (temp.length < 2) {
        continue;
      }
      if (StringUtils.isEmpty(temp[0]) == true || StringUtils.isEmpty(temp[1]) == true) {
        continue;
      }
      try {
        parmsMap.put(temp[0], java.net.URLDecoder.decode(temp[1], "UTF-8"));
      } catch (UnsupportedEncodingException ex) {
        throw new RuntimeException("Cannot decode url param: " + temp[0] + "=" + temp[1] + "; " + ex.getMessage(), ex);
      }
    }
    return parmsMap;
  }

  @Override
  public Object onInit()
  {
    if (wikiContext.getWikiWeb().getAuthorization().isAllowTo(wikiContext, GWikiAuthorizationRights.GWIKI_ADMIN.name()) == false) {
      withExport = false;
    }
    return null;
  }

  @Override
  public String getjqGridSearchExpression()
  {
    if (inExport == false) {
      return super.getjqGridSearchExpression();
    }
    Map<String, String> fim = decode(filterparams);
    if (fim.isEmpty() == true) {
      return null;
    }
    StringBuilder sb = new StringBuilder();
    for (Map.Entry<String, String> me : fim.entrySet()) {
      String f = me.getKey();
      String sval = me.getValue();
      if (f.equals("filterExpression") == true) {
        continue;
      }
      if (StringUtils.isBlank(sval) == true) {
        continue;
      }
      if (sb.length() != 0) {
        sb.append(" and ");
      }
      sb.append("prop:" + f + " ~ " + sval);
    }
    return sb.toString();
  }

  public Object onExport()
  {
    inExport = true;
    wikiContext.getWikiWeb().getAuthorization().ensureAllowTo(wikiContext, GWikiAuthorizationRights.GWIKI_ADMIN.name());
    SearchQuery query = super.buildQuery();
    if (query == null) {
      return null;
    }
    QueryResult qr = filter(query);
    if (qr.getResults().isEmpty() == true) {
      wikiContext.addSimpleValidationError("Keine Elemente zu exportieren");
      return null;
    }
    wikiContext.getResponse().setContentType("application/zip");
    wikiContext.getResponse().setHeader("Content-disposition", "attachment; filename=\"" + "gwiki_pages.zip" + "\"");

    try {
      ZipWriteFileSystem zfs = new ZipWriteFileSystem(wikiContext.getResponse().getOutputStream());
      GWikiFileStorage zipSt = new GWikiFileStorage(zfs);
      // THIS IS A HACK, but fast and memory saving.
      GWikiWeb wikiWeb = new GWikiWeb(wikiContext.getWikiWeb().getDaoContext());
      zipSt.setWikiWeb(wikiWeb);
      for (SearchResult sr : qr.getResults()) {
        GWikiElement el = wikiContext.getWikiWeb().getElement(sr.getElementInfo());
        zipSt.storeElementImpl(wikiContext, el, true);
        if (withArchive == true) {
          List<GWikiElementInfo> archive = wikiContext.getWikiWeb().getStorage().getVersions(el.getElementInfo().getId());
          for (GWikiElementInfo ai : archive) {
            GWikiElement ael = wikiContext.getWikiWeb().getStorage().loadElement(ai);
            zipSt.storeElement(wikiContext, ael, true);
          }
        }
      }
      zfs.close();
    } catch (IOException ex) {
      throw new RuntimeIOException(ex);
    }
    return noForward();

  }

  protected boolean canViewNonViewable()
  {
    if (canViewNonViewableElements != null) {
      return canViewNonViewableElements;
    }
    canViewNonViewableElements = wikiContext.getWikiWeb().getAuthorization().isAllowTo(wikiContext,
        GWikiAuthorizationRights.GWIKI_DEVELOPER.name());
    if (canViewNonViewableElements == false) {
      canViewNonViewableElements = wikiContext.getWikiWeb().getAuthorization().isAllowTo(wikiContext,
          GWikiAuthorizationRights.GWIKI_ADMIN.name());
    }
    return canViewNonViewableElements;
  }

  @Override
  protected boolean filterBeforeQuery(GWikiElementInfo ei)
  {
    if (canViewNonViewable() == true)
      return true;
    if (ei.isViewable() == false)
      return false;
    return true;
  }

  public String renderField(String fieldName, GWikiElementInfo ei)
  {
    if (fieldName.equals("operations") == true) {
      return "<a href='"
          + wikiContext.localUrl("edit/PageInfo")
          + "?pageId="
          + ei.getId()
          + "'>Infos</a> "
          + "<a href='"
          + wikiContext.localUrl("edit/EditPage")
          + "?pageId="
          + ei.getId()
          + "'>Edit</a>";
    }
    if (fieldName.equals("PAGEID") == true) {

      if (ei.isViewable() == true) {
        return "<a href='" + wikiContext.localUrl(ei.getId()) + "'>" + ei.getId() + "</a>";
      }
    }
    // if (fieldName.equals("MODIFIEDAT") == true || fieldName.equals("CREATEDAT") == true) {
    // String s = super.renderField(fieldName, ei);
    // s = wikiContext.getUserDateString(GWikiProps.parseTimeStamp(s));
    // return s;
    // }
    return super.renderField(fieldName, ei);
  }

  public boolean isWithExport()
  {
    return withExport;
  }

  public void setWithExport(boolean withExport)
  {
    this.withExport = withExport;
  }

  public String getFilterparams()
  {
    return filterparams;
  }

  public void setFilterparams(String filterparams)
  {
    this.filterparams = filterparams;
  }

  public boolean isWithArchive()
  {
    return withArchive;
  }

  public void setWithArchive(boolean withArchive)
  {
    this.withArchive = withArchive;
  }

}
