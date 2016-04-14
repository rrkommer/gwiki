//
// Copyright (C) 2010-2016 Roger Rene Kommer & Micromata GmbH
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//  http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.
//

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
import de.micromata.genome.gwiki.page.impl.wiki.macros.GWikiHelpLinkMacro;
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

  private boolean withExport = true;

  /**
   * Will only used in onExport.
   */
  private String filterparams;

  private boolean withArchive;

  private transient boolean inExport = false;

  public GWikiViewAllPagesActionBean()
  {
    setFields("PAGEID|TITLE|TYPE|CREATEDBY|CREATEDAT|MODIFIEDBY|MODIFIEDAT|operations");
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

  protected void initHelp()
  {
    if (withExport == true) {
      wikiContext.setRequestAttribute(GWikiHelpLinkMacro.REQATTR_HELPPAGE, "gwikidocs/help/ExportElements");
    }
  }

  @Override
  public Object onInit()
  {
    if (wikiContext.getWikiWeb().getAuthorization().isAllowTo(wikiContext,
        GWikiAuthorizationRights.GWIKI_ADMIN.name()) == false) {
      withExport = false;
    }
    initHelp();
    return onFilter();
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
    withExport = true;
    initHelp();
    wikiContext.getWikiWeb().getAuthorization().ensureAllowTo(wikiContext, GWikiAuthorizationRights.GWIKI_ADMIN.name());
    SearchQuery query = super.buildQuery();
    if (query == null) {
      return null;
    }
    QueryResult qr = filter(query);
    if (qr.getResults().isEmpty() == true) {
      wikiContext.addValidationError("gwiki.page.AllPages.message.noelementstoexport");
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
          List<GWikiElementInfo> archive = wikiContext.getWikiWeb().getStorage()
              .getVersions(el.getElementInfo().getId());
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
    if (canViewNonViewable() == true) {
      return true;
    }
    if (ei.isViewable() == false) {
      return false;
    }
    return true;
  }

  @Override
  public String renderField(String fieldName, GWikiElementInfo ei)
  {
    if (fieldName.equals("operations") == true) {
      return "<a href='"
          + wikiContext.localUrl("edit/PageInfo")
          + "?pageId="
          + ei.getId()
          + "' title='"
          + wikiContext.getTranslated("gwiki.page.AllPages.link.info")
          + "'>"
          + wikiContext.getTranslated("gwiki.page.AllPages.link.info")
          + "</a> "
          + "<a href='"
          + wikiContext.localUrl("edit/EditPage")
          + "?pageId="
          + ei.getId()
          + "' title='"
          + wikiContext.getTranslated("gwiki.page.AllPages.link.edit")
          + "'>"
          + wikiContext.getTranslated("gwiki.page.AllPages.link.edit")
          + "</a>";
    }
    if (fieldName.equals("PAGEID") == true) {

      if (ei.isViewable() == true) {
        return "<a href='" + wikiContext.localUrl(ei.getId()) + "'>" + ei.getId() + "</a>";
      }
    }

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
