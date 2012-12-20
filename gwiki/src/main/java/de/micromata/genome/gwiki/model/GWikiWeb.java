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

package de.micromata.genome.gwiki.model;

import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;

import de.micromata.genome.gwiki.model.config.GWikiDAOContext;
import de.micromata.genome.gwiki.model.config.GWikiMetaTemplate;
import de.micromata.genome.gwiki.model.filter.GWikiFilterChain;
import de.micromata.genome.gwiki.model.filter.GWikiFilters;
import de.micromata.genome.gwiki.model.filter.GWikiLoadElementInfosFilter;
import de.micromata.genome.gwiki.model.filter.GWikiLoadElementInfosFilterEvent;
import de.micromata.genome.gwiki.model.filter.GWikiServeElementFilter;
import de.micromata.genome.gwiki.model.filter.GWikiServeElementFilterEvent;
import de.micromata.genome.gwiki.page.GWikiContext;
import de.micromata.genome.gwiki.page.gspt.GWikiJspProcessor;
import de.micromata.genome.gwiki.page.impl.GWikiConfigElement;
import de.micromata.genome.gwiki.page.impl.GWikiWikiPageArtefakt;
import de.micromata.genome.gwiki.page.impl.actionbean.ActionBeanUtils;
import de.micromata.genome.gwiki.page.search.ContentSearcher;
import de.micromata.genome.gwiki.web.GWikiServlet;
import de.micromata.genome.util.runtime.CallableX;
import de.micromata.genome.util.types.TimeInMillis;

/**
 * Central instance of a wiki.
 * 
 * @author Roger Rene Kommer (r.kommer@micromata.de)
 * 
 */
public class GWikiWeb
{
  public static final String PREVIEW_SESSION_KEY = "de.micromata.genome.gwiki.model.USERPREVIEW";

  public static final String WIKI_NOCACHE_REQ_ATTR = "de.micromata.genome.gwiki.model.wikiNoCache";

  public static final String WIKI_NOCACHE_REQ_PARAM = "wikiNoCache";

  private String tenantId;

  /**
   * TODO remove this instance. GWikiServlet.INSTANCE.wiki
   */
  // private static GWikiWeb INSTANCE;

  private GWikiDAOContext daoContext;

  private GWikiFilters filter = new GWikiFilters();

  private boolean inBootStrapping = false;

  private GWikiGlobalConfig wikiGlobalConfig;

  private long lastModCheckTime = 0;

  private long modCheckTimoutMs = TimeInMillis.HOUR;

  private long lastModCounter = 0;

  /**
   * In case of import, do not reload automatically.
   */
  private boolean disableReload = false;

  /**
   * Is only necessary using genome plugins.
   */
  private String contextPath;

  private String servletPath;

  private String eTagWiki;

  /**
   * checked elements for mofications.
   */
  private ThreadLocal<Set<String>> devModeChecked = new ThreadLocal<Set<String>>() {

    @Override
    protected Set<String> initialValue()
    {
      return new HashSet<String>();
    }

  };

  /**
   * Initialize GWiki if and return global GWikiWeb instance.
   * 
   * @return
   */
  public static GWikiWeb getWiki()
  {
    if (GWikiServlet.INSTANCE == null) {
      throw new RuntimeException("Cannot initialize GWikiWeb because no GWikiServlet.INSTANCE can be found");
    }
    return GWikiServlet.INSTANCE.getWikiWeb();
  }

  public static GWikiWeb getRootWiki()
  {
    if (GWikiServlet.INSTANCE == null) {
      throw new RuntimeException("Cannot initialize GWikiWeb because no GWikiServlet.INSTANCE can be found");
    }
    return GWikiServlet.INSTANCE.getRootWikiWeb();
  }

  public GWikiWeb(GWikiDAOContext daoContext)
  {
    this.daoContext = daoContext;
    daoContext.getStorage().setWikiWeb(this);

    getLogging().note("Inited GWiki", null, GLogAttributeNames.Miscellaneous, daoContext.toString());
    initPageCache();
  }

  public GWikiWeb(GWikiWeb wikiWeb)
  {
    this.contextPath = wikiWeb.contextPath;
    this.daoContext = wikiWeb.daoContext;
    this.disableReload = wikiWeb.disableReload;
    this.filter = wikiWeb.filter;
    this.lastModCheckTime = wikiWeb.lastModCheckTime;
    this.lastModCounter = wikiWeb.lastModCounter;
    this.modCheckTimoutMs = wikiWeb.modCheckTimoutMs;
    this.servletPath = wikiWeb.servletPath;
    this.wikiGlobalConfig = wikiWeb.wikiGlobalConfig;
    initETag();
    // no tenantId
  }

  /**
   * return global GWiki instance. Use getWiki
   * 
   * @return null if not initialized.
   */
  public static GWikiWeb get()
  {
    if (GWikiServlet.INSTANCE == null || GWikiServlet.INSTANCE.hasWikiWeb() == false) {
      return null;
    }
    return getWiki();
  }

  protected void initPageCache()
  {
    daoContext.getPageCache().initPageCache(this);
    initETag();
  }

  public <T, EX extends Throwable> T runInPluginContext(CallableX<T, EX> callback) throws EX
  {
    ClassLoader previousClassLoader = null;
    try {
      previousClassLoader = daoContext.getPluginRepository().initClassLoader();
      return callback.call();
    } finally {
      if (previousClassLoader != null) {
        Thread.currentThread().setContextClassLoader(previousClassLoader);
      }
    }
  }

  public synchronized void loadWeb()
  {
    long start = System.currentTimeMillis();
    try {
      inBootStrapping = true;

      filter = new GWikiFilters();
      final GWikiGlobalConfig config = reloadWikiConfig();
      daoContext.getPluginRepository().initPluginRepository(this, config);
      runInPluginContext(new CallableX<Void, RuntimeException>() {

        public Void call() throws RuntimeException
        {
          // if (GWikiServlet.INSTANCE.getRootWikiWeb() == null) {
          // daoContext.getWikiSelector().
          // }
          initFilters(config);
          // / force to load config first to register listener

          modCheckTimoutMs = config.getCheckFileSystemForModTimeout();
          Map<String, GWikiElementInfo> npageInfos = new HashMap<String, GWikiElementInfo>();
          npageInfos = filter.loadPageInfos(GWikiContext.getCurrent(), npageInfos, new GWikiLoadElementInfosFilter() {
            public Void filter(GWikiFilterChain<Void, GWikiLoadElementInfosFilterEvent, GWikiLoadElementInfosFilter> chain,
                GWikiLoadElementInfosFilterEvent event)
            {
              getStorage().loadPageInfos(event.getPageInfos());
              lastModCounter = getStorage().getModificationCounter();
              daoContext.getPageCache().setPageInfoMap(event.getPageInfos());
              return null;
            }
          });
          return null;
        }
      });
      daoContext.getPluginRepository().afterWebLoaded(this, config);
    } finally {
      getLogging().addPerformance("GWikiWeb.loadWeb", System.currentTimeMillis() - start, 0);
      inBootStrapping = false;

    }

  }

  public synchronized void reloadWeb()
  {
    if (disableReload == true) {
      return;
    }
    if (inBootStrapping == true) {
      return;
    }
    try {
      inBootStrapping = true;
      // clear cache, otherwise some controler classes are loaded twice and cannot cast A to A
      daoContext.getPageCache().clearCachedPages();
      loadWeb();

      daoContext.getPageCache().clearCachedPages();
    } finally {
      inBootStrapping = false;
    }

  }

  public void initETag()
  {
    eTagWiki = Long.toString(System.currentTimeMillis());
  }

  public void serveWiki(String page, final GWikiContext ctx)
  {
    if (wikiGlobalConfig.checkFileSystemForExternalMod() == true) {
      devModeChecked.get().clear();
    }
    try {
      if (inBootStrapping == true) {
        synchronized (this) {
        }
      }
      serveWiki(ctx, page);
    } finally {
      if (wikiGlobalConfig.checkFileSystemForExternalMod() == true) {
        devModeChecked.get().clear();
      }
    }
  }

  protected void initStandardReqParams(final GWikiContext ctx)
  {
    if (StringUtils.equals(ctx.getRequestParameter(WIKI_NOCACHE_REQ_PARAM), "true") == true) {
      ctx.setRequestAttribute(WIKI_NOCACHE_REQ_ATTR, Boolean.TRUE);
    }
  }

  protected boolean noCache(final GWikiContext ctx)
  {
    return ctx.getBooleanRequestAttribute(WIKI_NOCACHE_REQ_ATTR);
  }

  public static boolean doRedirectContentToPageNotFound(String mimeType)
  {
    if (mimeType == null) {
      return true;
    }
    if (mimeType.startsWith("text/") == true && mimeType.endsWith("/css") == false) {
      return true;
    }
    return false;
  }

  public void serveWiki(final GWikiContext ctx, String pageId)
  {
    ClassLoader previousClassLoader = null;
    try {
      previousClassLoader = daoContext.getPluginRepository().initClassLoader();
      try {
        ctx.getRequest().setCharacterEncoding("UTF-8");
        ctx.getResponse().setCharacterEncoding("UTF-8");
      } catch (UnsupportedEncodingException ex) {
        throw new RuntimeException(ex);
      }
      daoContext.getStorage().getFileSystem().checkEvents(false);
      initStandardReqParams(ctx);
      String welcomePage = StringUtils.defaultIfEmpty(wikiGlobalConfig.getMap().get(GWikiGlobalConfig.GWIKI_WELCOME_PAGE), "index");
      ctx.getRequest().setAttribute("welcomePageId", welcomePage);
      if (StringUtils.isEmpty(pageId) == true) {
        pageId = welcomePage;
      }
      GWikiElement el = findElement(pageId);
      if (el == null) {
        String mimeType = daoContext.getMimeTypeProvider().getMimeType(ctx, pageId);
        GWikiLog.note("PageNot Found: " + pageId);
        if (doRedirectContentToPageNotFound(mimeType) == false) {
          ctx.sendErrorSilent(404);
          return;
        }
        // el = findElement(pageId);
        ctx.setRequestAttribute("NotFoundPageId", pageId);
        pageId = "admin/PageNotFound";
        el = findElement(pageId);
        if (el == null)
          throw new RuntimeException("No Wiki page found: " + "admin/PageNotFound");
      }

      String mimeType = daoContext.getMimeTypeProvider().getMimeType(ctx, el);
      if (mimeType != null) {
        ctx.getResponse().setContentType(mimeType);
      }

      filter.serveElement(ctx, el, new GWikiServeElementFilter() {

        public Void filter(GWikiFilterChain<Void, GWikiServeElementFilterEvent, GWikiServeElementFilter> chain,
            GWikiServeElementFilterEvent event)
        {
          serveWiki(event.getWikiContext(), event.getElement());
          return null;
        }
      });
    } finally {
      if (previousClassLoader != null) {
        Thread.currentThread().setContextClassLoader(previousClassLoader);
      }
    }

  }

  public void serveWiki(final GWikiContext ctx, final GWikiElement el)
  {
    try {
      serveWikiIntern(ctx, el);
    } catch (AuthorizationFailedException ex) {
      GWikiLog.warn("Invalid access to page: " + el.getElementInfo().getId(), ex);
      ctx.getRequest().setAttribute("exception", ex);
      GWikiElement nel = findElement("admin/AccessDenied");
      if (nel == null)
        throw ex;
      serveWikiIntern(ctx, nel);
    } catch (RuntimeException ex) {
      if (GWikiServlet.isIgnorableAppServeIOException(ex) == true) {
        GWikiLog.note("IO Error rendering page: " + el.getElementInfo().getId() + "; " + ex.getMessage());
      } else {
        GWikiLog.warn("Error rendering page: " + el.getElementInfo().getId() + "; " + ex.getMessage(), ex);
      }
      ctx.getRequest().setAttribute("exception", ex);
      GWikiElement nel = findElement("admin/InternalError");
      if (nel == null)
        throw ex;
      serveWikiIntern(ctx, nel);
    }
  }

  protected boolean isIncluded(final GWikiContext ctx)
  {
    return ctx.getRequest().getAttribute("javax.servlet.include.servlet_path") != null;
  }

  protected void serveWikiIntern(final GWikiContext ctx, final GWikiElement el)
  {
    try {
      if (getAuthorization().initThread(ctx) == false) {
        if (getAuthorization().isAllowToView(ctx, el.getElementInfo()) == false) {
          if (isIncluded(ctx) == true) {
            return;
          } else {
            String url = "admin/Login";
            if (el != null) {
              url += "?pageId=" + el.getElementInfo().getId();
            }
            ActionBeanUtils.redirect(ctx, url);
            return;
          }
        }
      }

      if (getAuthorization().isAllowToView(ctx, el.getElementInfo()) == false) {
        throw new AuthorizationFailedException("Page is not allowed to view: " + el.getElementInfo().getId());
      }
      el.prepareHeader(ctx);
      el.serve(ctx);
    } finally {
      getAuthorization().clearThread(ctx);
    }

  }

  public GWikiElement getHomeElement(GWikiContext ctx)
  {
    String hp = getAuthorization().getUserProp(ctx, "HOMEPAGE");
    if (StringUtils.isNotEmpty(hp) == true) {
      GWikiElement el = findElement(hp);
      if (el != null) {
        return el;
      }
    }
    return findElement(getWikiConfig().getWelcomePageId());
  }

  public GWikiElementInfo findElementInfo(String path)
  {
    return daoContext.getPageCache().getPageInfo(path);
  }

  public GWikiElement getElement(String pageId)
  {
    GWikiElement el = findElement(pageId);
    if (el != null)
      return el;
    throw new RuntimeException("Cannot find page with id: " + pageId);
  }

  protected void checkNewElementInfo(String path)
  {
    if (inBootStrapping == true)
      return;
    if (getStorage().isArchivePageId(path) == true)
      return;
    // on bootstrap this is null
    if (daoContext.getPageCache().hasPageInfo(path) == false) {
      GWikiElementInfo el = getStorage().loadElementInfo(path);
      if (el != null) {
        reloadWeb();
      }
      return;
    }
  }

  public GWikiElement checkElementForModification(GWikiElement element)
  {
    if (inBootStrapping == true) {
      return null;
    }
    long now = System.currentTimeMillis();
    if (now > lastModCheckTime + modCheckTimoutMs) {
      // das ist toetlich bei usage counter
      long modC = getStorage().getModificationCounter();
      if (modC != lastModCounter) {
        lastModCounter = modC;
        // TODO GWIki hmm. UseCounter macht jedesmall das schmutzig fs schmutzig
        // return true;
      }
      lastModCheckTime = now;
    }
    if (wikiGlobalConfig == null || wikiGlobalConfig.checkFileSystemForExternalMod() == false) {
      return null;
    }
    if (devModeChecked.get().contains(element.getElementInfo().getId()) == true)
      return null;

    GWikiElement newEle = getStorage().hasModifiedArtefakts(element.getElementInfo());
    devModeChecked.get().add(element.getElementInfo().getId());
    return newEle;
  }

  /**
   * Load element from underlying storage - not using the cache.
   * 
   * @param path
   * @return
   */
  public GWikiElement loadNewElement(String path)
  {
    return findElement(path, false);
  }

  public GWikiElement getElement(GWikiElementInfo ei)
  {
    GWikiElement el = getElementByCache(ei.getId());
    if (el != null)
      return el;
    el = getStorage().loadElement(ei);

    daoContext.getPageCache().putCachedPage(ei.getId(), el);

    return el;
  }

  public GWikiElement findElement(String path)
  {
    GWikiContext wikiContext = GWikiContext.getCurrent();
    if (wikiContext != null) {
      GWikiElement el = (GWikiElement) GWikiContext.getCurrent().getWikiWeb().getSessionProvider()
          .getSessionAttribute(GWikiContext.getCurrent(), PREVIEW_SESSION_KEY);
      if (el != null && StringUtils.equals(el.getElementInfo().getId(), path) == true) {
        return el;
      }
    }
    if (wikiContext != null && wikiContext.getBooleanRequestAttribute(WIKI_NOCACHE_REQ_ATTR) == true) {
      return findElement(path, false);
    }
    return findElement(path, true);
  }

  public GWikiElement getElementByCache(String path)
  {
    if (daoContext.getPageCache().hasCachedPage(path) == true) {
      GWikiElement element = daoContext.getPageCache().getPage(path);
      GWikiElement nel = checkElementForModification(element);
      if (nel == null) {
        return element;
      }
      onReplacePageInfo(nel.getElementInfo());
      daoContext.getPageCache().putCachedPage(nel.getElementInfo().getId(), nel);
      return nel;
    }
    return null;
  }

  public GWikiElement findElement(String path, boolean usePageCache)
  {
    if (path == null) {
      return null;
    }
    if (path.startsWith("/") == true) {
      path = path.substring(1);
    }
    if (usePageCache == false) {
      checkNewElementInfo(path);
    }
    GWikiElement ret = null;
    if (usePageCache == true) {
      ret = getElementByCache(path);
      if (ret != null) {
        return ret;
      }
    }
    try {
      GWikiElementInfo ei = null;
      if (usePageCache == true) {
        ei = daoContext.getPageCache().getPageInfo(path);
      }
      if (ei == null) {
        ei = getStorage().loadElementInfo(path);
        if (ei == null) {
          return null;
        }
      }
      GWikiElement el = getStorage().loadElement(ei);
      if (usePageCache == true && getStorage().isArchivePageId(ei.getId()) == false) {
        daoContext.getPageCache().putCachedPage(path, el);
      }
      return el;
    } catch (Throwable ex) {
      GWikiLog.warn("Cannot load element: " + path + "; " + ex.getMessage(), ex);
      return null;
    }
  }

  public GWikiMetaTemplate findMetaTemplate(String pageId)
  {
    if (StringUtils.isBlank(pageId) == true) {
      return null;
    }
    GWikiElement el = findElement(pageId);
    if (el == null)
      return null;
    try {
      GWikiConfigElement metaConfig = (GWikiConfigElement) el;
      GWikiMetaTemplate template = (GWikiMetaTemplate) metaConfig.getConfig().getCompiledObject();
      template.setPageId(pageId);
      return template;
    } catch (Exception ex) {
      GWikiLog.error("Failed to load metatemplate: " + pageId + "; " + ex.getMessage(), ex);
      return null;
    }
  }

  public List<GWikiElementInfo> getVersions(GWikiElementInfo ei)
  {
    return getStorage().getVersions(ei.getId());
  }

  public void removeWikiPage(GWikiContext wikiContext, GWikiElement element)
  {
    getStorage().deleteElement(wikiContext, element);

    GWikiPageCache pageCache = daoContext.getPageCache();
    // pacheCache.clearCachedPages();
    String pageId = element.getElementInfo().getId();
    pageCache.clearCachedPage(pageId);
    pageCache.removePageInfo(pageId);
    pageCache.clearCompiledFragments(GWikiWikiPageArtefakt.class);
  }

  protected void onReplacePageInfo(GWikiElementInfo ei)
  {
    GWikiPageCache pageCache = daoContext.getPageCache();
    pageCache.clearCachedPage(ei.getId());
    pageCache.putPageInfo(ei);
    pageCache.clearCompiledFragments(GWikiWikiPageArtefakt.class);
  }

  /**
   * Just reload given page from storage
   * 
   * @param pageId
   */
  public void reloadPage(String pageId)
  {
    if (pageId == null) {
      return;
    }
    GWikiPageCache pageCache = daoContext.getPageCache();
    GWikiElementInfo ei = getStorage().loadElementInfo(pageId);
    if (ei == null) {
      pageCache.removePageInfo(pageId);
    } else {
      pageCache.putPageInfo(ei);
    }
    pageCache.clearCachedPage(pageId);
  }

  public void restoreWikiPage(GWikiContext wikiContext, GWikiElement element)
  {
    GWikiElementInfo ei = getStorage().restoreElement(wikiContext, element);
    onReplacePageInfo(ei);
  }

  public void saveElement(GWikiContext wikiContext, GWikiElement element, boolean keepModifiedAt)
  {
    long start = System.currentTimeMillis();
    getStorage().storeElement(wikiContext, element, keepModifiedAt);
    getLogging().addPerformance("GWikiWeb.saveElement", System.currentTimeMillis() - start, 0);
    onReplacePageInfo(element.getElementInfo());
  }

  public void setPreviewPage(GWikiContext ctx, GWikiElement element)
  {
    ctx.setSessionAttribute(PREVIEW_SESSION_KEY, element);

  }

  public void rebuildIndex()
  {
    rebuildIndex(false);
  }

  public void rebuildIndex(boolean full)
  {
    long start = System.currentTimeMillis();
    getContentSearcher().rebuildIndex(GWikiContext.getCurrent(), null, full);
    getLogging().addPerformance("GWikiWeb.rebuildIndex", System.currentTimeMillis() - start, 0);
  }

  public void rebuildIndex(String pageId, GWikiContext ctx)
  {
    GWikiElementInfo ei = findElementInfo(pageId);
    if (ei == null) {
      ctx.addValidationError("gwiki.page.admin.WikiControl.message.rebuildpageidnotfound", pageId);
      return;
    }
    getContentSearcher().rebuildIndex(ctx, pageId, true);
  }

  public GWikiGlobalConfig getWikiConfig()
  {
    if (wikiGlobalConfig != null) {
      return wikiGlobalConfig;
    }
    GWikiGlobalConfig cfg = reloadWikiConfig();
    initFilters(cfg);
    return wikiGlobalConfig;
  }

  public synchronized GWikiGlobalConfig reloadWikiConfig()
  {
    GWikiElement el = findElement(GWikiGlobalConfig.GWIKI_GLOBAL_CONFIG_PATH, true);
    if (el == null) {
      throw new RuntimeException(GWikiGlobalConfig.GWIKI_GLOBAL_CONFIG_PATH + "cannot be found. Please Check GWikiContext.xml");
    }
    Serializable ser = el.getMainPart().getCompiledObject();
    GWikiGlobalConfig nwikiGlobalConfig = new GWikiGlobalConfig((GWikiProps) ser);
    return wikiGlobalConfig = nwikiGlobalConfig;
  }

  public synchronized void initFilters(GWikiGlobalConfig cfg)
  {
    filter = new GWikiFilters();
    filter.init(GWikiWeb.this, cfg);
  }

  public GWikiStorage getStorage()
  {
    return daoContext.getStorage();
  }

  public GWikiAuthorization getAuthorization()
  {
    return daoContext.getAuthorization();
  }

  public Iterable<GWikiElementInfo> getElementInfos()
  {
    return daoContext.getPageCache().getPageInfos();
  }

  public int getElementInfoCount()
  {
    return daoContext.getPageCache().getElementInfoCount();
  }

  public ContentSearcher getContentSearcher()
  {
    return daoContext.getContentSearcher();
  }

  public GWikiJspProcessor getJspProcessor()
  {
    return daoContext.getJspProcessor();
  }

  public GWikiLogging getLogging()
  {
    return daoContext.getLogging();
  }

  public String getStandardSkin()
  {
    return getWikiConfig().getDefaultSkin();
  }

  public GWikiFilters getFilter()
  {
    return filter;
  }

  public void setFilter(GWikiFilters filter)
  {
    this.filter = filter;
  }

  public boolean isDisableReload()
  {
    return disableReload;
  }

  public void setDisableReload(boolean disableCheckMod)
  {
    this.disableReload = disableCheckMod;
  }

  public GWikiI18nProvider getI18nProvider()
  {
    return daoContext.getI18nProvider();
  }

  public GWikiSessionProvider getSessionProvider()
  {
    return daoContext.getSessionProvider();
  }

  public GWikiSchedulerProvider getSchedulerProvider()
  {
    return daoContext.getSchedulerProvider();
  }

  public GWikiDAOContext getDaoContext()
  {
    return daoContext;
  }

  public void setDaoContext(GWikiDAOContext daoContext)
  {
    this.daoContext = daoContext;
  }

  public String getServletPath()
  {
    return servletPath;
  }

  public void setServletPath(String servletPath)
  {
    this.servletPath = servletPath;
  }

  public String getContextPath()
  {
    return contextPath;
  }

  public void setContextPath(String contextPath)
  {
    this.contextPath = contextPath;
  }

  public String getTenantId()
  {
    return tenantId;
  }

  public void setTenantId(String tenantId)
  {
    this.tenantId = tenantId;
  }

  public String geteTagWiki()
  {
    return eTagWiki;
  }

  public void seteTagWiki(String eTagWiki)
  {
    this.eTagWiki = eTagWiki;
  }
}
