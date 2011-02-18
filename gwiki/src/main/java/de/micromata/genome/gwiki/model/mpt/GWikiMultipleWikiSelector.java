////////////////////////////////////////////////////////////////////////////
// 
// Copyright (C) 2010 Micromata GmbH
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
// http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.
// 
////////////////////////////////////////////////////////////////////////////
package de.micromata.genome.gwiki.model.mpt;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;

import de.micromata.genome.gdbfs.FileSystem;
import de.micromata.genome.gdbfs.FsObject;
import de.micromata.genome.gdbfs.ReadWriteCombinedFileSystem;
import de.micromata.genome.gwiki.model.GWikiElement;
import de.micromata.genome.gwiki.model.GWikiElementInfo;
import de.micromata.genome.gwiki.model.GWikiLog;
import de.micromata.genome.gwiki.model.GWikiPageCacheTimedImpl;
import de.micromata.genome.gwiki.model.GWikiPropKeys;
import de.micromata.genome.gwiki.model.GWikiStandardWikiSelector;
import de.micromata.genome.gwiki.model.GWikiStorage;
import de.micromata.genome.gwiki.model.GWikiWeb;
import de.micromata.genome.gwiki.model.config.GWikiDAOContext;
import de.micromata.genome.gwiki.model.filter.GWikiFilterChain;
import de.micromata.genome.gwiki.model.filter.GWikiLoadElementInfosFilter;
import de.micromata.genome.gwiki.model.filter.GWikiLoadElementInfosFilterEvent;
import de.micromata.genome.gwiki.page.GWikiContext;
import de.micromata.genome.gwiki.page.GWikiStandaloneContext;
import de.micromata.genome.gwiki.spi.storage.GWikiFileStorage;
import de.micromata.genome.gwiki.web.GWikiServlet;
import de.micromata.genome.util.runtime.CallableX;

/**
 * @author Roger Rene Kommer (r.kommer@micromata.de)
 */
public class GWikiMultipleWikiSelector extends GWikiStandardWikiSelector
{

  private GWikiMptIdSelector mptIdSelector;

  private Map<String, GWikiWeb> customWikis = Collections.synchronizedMap(new HashMap<String, GWikiWeb>());

  private ThreadLocal<GWikiWeb> THREAD_WIKI = new ThreadLocal<GWikiWeb>() {

    @Override
    protected GWikiWeb initialValue()
    {
      return wiki;
    }

  };

  public GWikiWeb getWikiWeb(GWikiServlet servlet)
  {
    final GWikiWeb wiki = THREAD_WIKI.get();
    if (wiki != null) {
      return wiki;
    }
    return super.getWikiWeb(servlet);
  }

  @Override
  public GWikiWeb getTenantWikiWeb(GWikiServlet servlet, String tenant)
  {
    GWikiWeb wiki = customWikis.get(tenant);
    if (wiki != null) {
      return wiki;
    }
    List<String> tenants = mptIdSelector.getTenants(getRootWikiWeb(servlet));
    if (tenants.contains(tenant) == false) {
      return null;
    }

    wiki = createDerivedWiki(servlet, tenant);
    customWikis.put(tenant, wiki);
    return wiki;
  }

  public String getTenantId(GWikiServlet servlet, HttpServletRequest req)
  {
    return mptIdSelector.getTenantId(servlet, req);
  }

  protected void loadDerivedWiki(final GWikiWeb wikiWeb)
  {
    wikiWeb.runInPluginContext(new CallableX<Void, RuntimeException>() {

      public Void call() throws RuntimeException
      {

        // modCheckTimoutMs = config.getCheckFileSystemForModTimeout();
        Map<String, GWikiElementInfo> npageInfos = new HashMap<String, GWikiElementInfo>();
        npageInfos = wikiWeb.getFilter().loadPageInfos(GWikiContext.getCurrent(), npageInfos, new GWikiLoadElementInfosFilter() {
          public Void filter(GWikiFilterChain<Void, GWikiLoadElementInfosFilterEvent, GWikiLoadElementInfosFilter> chain,
              GWikiLoadElementInfosFilterEvent event)
          {
            wikiWeb.getStorage().loadPageInfos(event.getPageInfos());
            // lastModCounter = getStorage().getModificationCounter();
            wikiWeb.getDaoContext().getPageCache().setPageInfoMap(event.getPageInfos());
            return null;
          }
        });
        return null;
      }
    });
  }

  protected GWikiWeb createDerivedWiki(GWikiServlet servlet, final String cid)
  {
    final GWikiWeb rootWiki = getWikiWeb(servlet);
    GWikiDAOContext mptDaoContext = new GWikiDAOContext(rootWiki.getDaoContext());

    final FileSystem mptFsSp = mptIdSelector.getTenantFileSystem(rootWiki, cid);
    ReadWriteCombinedFileSystem cmp = new ReadWriteCombinedFileSystem(mptFsSp, rootWiki.getDaoContext().getStorage().getFileSystem());

    GWikiStorage storage = new GWikiFileStorage(mptFsSp) {

      protected GWikiElementInfo afterPageInfoLoad(FsObject fo, GWikiElementInfo el, final Map<String, GWikiElementInfo> ret)
      {
        if (ret.containsKey(el.getId()) == true) {
          return null;
        }
        if (fo.getFileSystem() == mptFsSp) {
          el.getProps().setStringValue(GWikiPropKeys.TENANT_ID, cid);
        }
        return el;
      }

      public GWikiElement storeElement(final GWikiContext wikiContext, final GWikiElement elm, final boolean keepModifiedAt)
      {
        GWikiElement el = super.storeElement(wikiContext, elm, keepModifiedAt);
        el.getElementInfo().getProps().setStringValue(GWikiPropKeys.TENANT_ID, cid);
        return el;
      }
      
      public GWikiElement loadElement(final GWikiElementInfo ei) {
        if (getStorage().exists(ei.getId() + GWikiStorage.SETTINGS_SUFFIX) == true) {
          return super.loadElement(ei);
        }
        return rootWiki.getStorage().loadElement(ei);
      }
    };
    mptDaoContext.setStorage(storage);
    GWikiPageCacheTimedImpl pageCache = new GWikiPageCacheTimedImpl();

    GWikiCombinedPageCache combPageCache = new GWikiCombinedPageCache(pageCache, rootWiki.getDaoContext().getPageCache());
    // challenge:
    // Cache should be combined, where cached objects should
    // detect if owned by root gwiki or derived.

    // both for pageinfo and for page content

    mptDaoContext.setPageCache(combPageCache);
    GWikiWeb mptWiki = new GWikiWeb(rootWiki);
    mptWiki.setTenantId(cid);
    storage.setWikiWeb(mptWiki);
    mptWiki.setDaoContext(mptDaoContext);
    pageCache.initPageCache(mptWiki);

    GWikiStandaloneContext wikiContext = new GWikiStandaloneContext(mptWiki, servlet, servlet.getContextPath(), servlet.getServletPath());
    try {
      GWikiContext.setCurrent(wikiContext);
      loadDerivedWiki(mptWiki);
    } finally {
      GWikiContext.setCurrent(null);
    }
    storage.setFileSystem(cmp);
    return mptWiki;
  }

  @Override
  public void initWiki(GWikiServlet servlet, HttpServletRequest req, HttpServletResponse resp)
  {
    super.initWiki(servlet, req, resp);

    final String cid = getTenantId(servlet, req);
    if (cid == null) {
      THREAD_WIKI.set(wiki);
      return;
    }
    GWikiWeb wiki = customWikis.get(cid);
    if (wiki != null) {
      THREAD_WIKI.set(wiki);
      return;
    }
    wiki = createDerivedWiki(servlet, cid);
    customWikis.put(cid, wiki);
    THREAD_WIKI.set(wiki);
    // TODO plc deinitwiki per request cyclus
  }

  public void clearTenant(GWikiContext wikiContext, String tenantId)
  {
    customWikis.remove(tenantId);
  }

  public void deinitWiki(GWikiServlet servlet, HttpServletRequest req, HttpServletResponse resp)
  {
    THREAD_WIKI.remove();
  }

  public GWikiMptIdSelector getMptIdSelector()
  {
    return mptIdSelector;
  }

  public void setMptIdSelector(GWikiMptIdSelector mptIdSelector)
  {
    this.mptIdSelector = mptIdSelector;
  }

  @Override
  public void enterTenant(GWikiContext wikiContext, String tenantId)
  {
    mptIdSelector.setTenant(wikiContext, tenantId);
    initWiki(GWikiServlet.INSTANCE, wikiContext.getRequest(), wikiContext.getResponse());
    wikiContext.setWikiWeb(THREAD_WIKI.get());
  }
  
  @Override
  public void createTenant(GWikiContext wikiContext, String tenantId)
  {
    if (customWikis.get(tenantId) != null) {
      GWikiLog.warn("tenant already exists");
      return;
    }
    
    GWikiWeb newTenant = createDerivedWiki(GWikiServlet.INSTANCE, tenantId);
    customWikis.put(tenantId, newTenant);
  }
  
  @Override
  public void leaveTenant(GWikiContext wikiContext)
  {
   enterTenant(wikiContext, StringUtils.EMPTY);
  }
}
