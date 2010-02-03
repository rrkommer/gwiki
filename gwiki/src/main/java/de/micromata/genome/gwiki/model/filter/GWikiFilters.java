/////////////////////////////////////////////////////////////////////////////
//
// Project   DHL-ParcelOnlinePostage
//
// Author    roger@micromata.de
// Created   17.11.2009
// Copyright Micromata 17.11.2009
//
/////////////////////////////////////////////////////////////////////////////
package de.micromata.genome.gwiki.model.filter;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;

import de.micromata.genome.gwiki.model.GWikiArtefakt;
import de.micromata.genome.gwiki.model.GWikiElement;
import de.micromata.genome.gwiki.model.GWikiElementInfo;
import de.micromata.genome.gwiki.model.GWikiGlobalConfig;
import de.micromata.genome.gwiki.model.GWikiLog;
import de.micromata.genome.gwiki.model.GWikiWeb;
import de.micromata.genome.gwiki.page.GWikiContext;
import de.micromata.genome.gwiki.page.impl.GWikiWikiPageArtefakt;
import de.micromata.genome.gwiki.utils.ClassUtils;
import de.micromata.genome.util.runtime.CallableX;

/**
 * Filter registered in GWiki.
 * 
 * @author roger@micromata.de
 * 
 */
public class GWikiFilters
{
  private Set<Class< ? >> knownFilter = new HashSet<Class< ? >>();

  private List<GWikiServeElementFilter> serveElementFilters = new ArrayList<GWikiServeElementFilter>();

  private List<GWikiWikiPageRenderFilter> renderWikiPageFilters = new ArrayList<GWikiWikiPageRenderFilter>();

  private List<GWikiWikiPageCompileFilter> wikiCompileFilters = new ArrayList<GWikiWikiPageCompileFilter>();

  private List<GWikiLoadElementInfosFilter> loadPageInfosFilters = new ArrayList<GWikiLoadElementInfosFilter>();

  private List<GWikiStorageStoreElementFilter> storageStoreElementFilters = new ArrayList<GWikiStorageStoreElementFilter>();

  private List<GWikiStorageDeleteElementFilter> storageDeleteElementFilters = new ArrayList<GWikiStorageDeleteElementFilter>();

  private List<GWikiPageChangedFilter> pageChangedFilters = new ArrayList<GWikiPageChangedFilter>();

  public ThreadLocal<List<Class< ? >>> skipFilters = new ThreadLocal<List<Class< ? >>>() {

    @Override
    protected List<Class< ? >> initialValue()
    {
      return new ArrayList<Class< ? >>();
    }

  };

  public <R> R runWithoutFilters(Class< ? >[] filtersToSkip, CallableX<R, RuntimeException> callback)
  {
    List<Class< ? >> of = skipFilters.get();
    List<Class< ? >> nf = new ArrayList<Class< ? >>();
    nf.addAll(of);
    for (Class< ? > cls : filtersToSkip) {
      nf.add(cls);
    }
    skipFilters.set(nf);
    try {
      return callback.call();
    } finally {
      skipFilters.set(of);
    }
  }

  public <R, E extends GWikiFilterEvent, F extends GWikiFilter<R, E, F>> boolean skipFilter(GWikiFilter<R, E, F> filter)
  {
    List<Class< ? >> fscp = skipFilters.get();
    for (Class< ? > cls : fscp) {
      if (cls.isAssignableFrom(filter.getClass()) == true) {
        return true;
      }
    }
    return false;
  }

  public void storeElement(GWikiContext ctx, GWikiElement el, Map<String, GWikiArtefakt< ? >> parts, GWikiStorageStoreElementFilter target)
  {
    GWikiStorageStoreElementFilterEvent event = new GWikiStorageStoreElementFilterEvent(ctx, el, parts);
    new GWikiFilterChain<Void, GWikiStorageStoreElementFilterEvent, GWikiStorageStoreElementFilter>(this, target,
        storageStoreElementFilters).nextFilter(event);
  }

  public void deleteElement(GWikiContext ctx, GWikiElement el, Map<String, GWikiArtefakt< ? >> parts, GWikiStorageDeleteElementFilter target)
  {
    GWikiStorageDeleteElementFilterEvent event = new GWikiStorageDeleteElementFilterEvent(ctx, el, parts);
    new GWikiFilterChain<Void, GWikiStorageDeleteElementFilterEvent, GWikiStorageDeleteElementFilter>(this, target,
        storageDeleteElementFilters).nextFilter(event);
  }

  public void serveElement(GWikiContext ctx, GWikiElement el, GWikiServeElementFilter target)
  {
    ctx.setWikiElement(el);
    GWikiServeElementFilterEvent event = new GWikiServeElementFilterEvent(ctx, el);
    new GWikiFilterChain<Void, GWikiServeElementFilterEvent, GWikiServeElementFilter>(this, target, serveElementFilters).nextFilter(event);
  }

  public Boolean renderWikiWikiPage(GWikiContext ctx, GWikiWikiPageArtefakt artefakt, GWikiWikiPageRenderFilter target)
  {
    GWikiWikiPageRenderFilterEvent event = new GWikiWikiPageRenderFilterEvent(ctx, artefakt);
    return new GWikiFilterChain<Boolean, GWikiWikiPageRenderFilterEvent, GWikiWikiPageRenderFilter>(this, target, renderWikiPageFilters)
        .nextFilter(event);
  }

  public void compileWikiWikiPage(GWikiContext ctx, GWikiElement element, GWikiWikiPageArtefakt artefakt, GWikiWikiPageCompileFilter target)
  {
    GWikiWikiPageCompileFilterEvent event = new GWikiWikiPageCompileFilterEvent(ctx, element, artefakt);
    new GWikiFilterChain<Void, GWikiWikiPageCompileFilterEvent, GWikiWikiPageCompileFilter>(this, target, wikiCompileFilters)
        .nextFilter(event);
  }

  public void pageChanged(GWikiContext ctx, GWikiWeb wikiWeb, GWikiElementInfo newElement, GWikiElementInfo oldElement)
  {
    if (newElement != null) {
      String id = newElement.getId();
      if (oldElement != null) {
        GWikiLog.note("Page changed:" + id);
      } else {
        GWikiLog.note("Page New:" + id);
      }
    } else if (oldElement != null) {
      GWikiLog.note("Page Deleted:" + oldElement.getId());
    }
    pageChanged(ctx, wikiWeb, newElement, oldElement, new GWikiPageChangedFilter() {

      public Void filter(GWikiFilterChain<Void, GWikiPageChangedFilterEvent, GWikiPageChangedFilter> chain,
          GWikiPageChangedFilterEvent event)
      {
        return null;
      }
    });
  }

  public void pageChanged(GWikiContext ctx, GWikiWeb wikiWeb, GWikiElementInfo newElement, GWikiElementInfo oldElement,
      GWikiPageChangedFilter target)
  {
    GWikiPageChangedFilterEvent event = new GWikiPageChangedFilterEvent(ctx, wikiWeb, newElement, oldElement);
    new GWikiFilterChain<Void, GWikiPageChangedFilterEvent, GWikiPageChangedFilter>(this, target, pageChangedFilters).nextFilter(event);
  }

  public Map<String, GWikiElementInfo> loadPageInfos(GWikiContext ctx, Map<String, GWikiElementInfo> npageInfos,
      GWikiLoadElementInfosFilter target)
  {
    GWikiLoadElementInfosFilterEvent event = new GWikiLoadElementInfosFilterEvent(ctx, npageInfos);
    new GWikiFilterChain<Void, GWikiLoadElementInfosFilterEvent, GWikiLoadElementInfosFilter>(this, target, loadPageInfosFilters)
        .nextFilter(event);
    return event.getPageInfos();
  }

  public void registerNewFilterClass(GWikiWeb wikiWeb, Class< ? > filter)
  {
    Object obj = ClassUtils.createDefaultInstance(filter);
    registerNewFilterObject(obj);
    if (obj instanceof GWikiFilterInit) {
      ((GWikiFilterInit) obj).init(wikiWeb);
    }
  }

  public void registerNewFilterObject(Object obj)
  {
    if (obj instanceof GWikiServeElementFilter) {
      serveElementFilters.add(0, (GWikiServeElementFilter) obj);
    }
    if (obj instanceof GWikiWikiPageRenderFilter) {
      renderWikiPageFilters.add(0, (GWikiWikiPageRenderFilter) obj);
    }
    if (obj instanceof GWikiLoadElementInfosFilter) {
      loadPageInfosFilters.add(0, (GWikiLoadElementInfosFilter) obj);
    }
    if (obj instanceof GWikiWikiPageCompileFilter) {
      wikiCompileFilters.add(0, (GWikiWikiPageCompileFilter) obj);
    }
    if (obj instanceof GWikiStorageStoreElementFilter) {
      storageStoreElementFilters.add(0, (GWikiStorageStoreElementFilter) obj);
    }
    if (obj instanceof GWikiStorageDeleteElementFilter) {
      storageDeleteElementFilters.add(0, (GWikiStorageDeleteElementFilter) obj);
    }
    if (obj instanceof GWikiPageChangedFilter) {
      pageChangedFilters.add(0, (GWikiPageChangedFilter) obj);
    }
  }

  public void init(GWikiWeb wikiWeb, GWikiGlobalConfig wikiConfig)
  {
    List<String> regClasses = wikiConfig.getStringList("GWIKI_FILTER_CLASSES");
    if (regClasses != null) {
      for (String rc : regClasses) {
        rc = StringUtils.trim(rc);
        if (StringUtils.isEmpty(rc) == true) {
          continue;
        }
        try {
          registerFilter(wikiWeb, ClassUtils.classForName(rc));
        } catch (Throwable ex) {
          GWikiLog.warn("Cannot register filter class: " + rc + "; " + ex.getMessage(), ex);
        }
      }
    }
  }

  public void registerFilter(GWikiWeb wikiWeb, Class< ? > filter)
  {
    if (knownFilter.contains(filter) == true) {
      return;
    }
    registerNewFilterClass(wikiWeb, filter);
  }
}
