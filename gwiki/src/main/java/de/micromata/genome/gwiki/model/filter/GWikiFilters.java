////////////////////////////////////////////////////////////////////////////
//
// Copyright (C) 2010-2013 Micromata GmbH / Roger Rene Kommer
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

package de.micromata.genome.gwiki.model.filter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;

import de.micromata.genome.gwiki.auth.GWikiSimpleUser;
import de.micromata.genome.gwiki.model.GWikiArtefakt;
import de.micromata.genome.gwiki.model.GWikiElement;
import de.micromata.genome.gwiki.model.GWikiElementInfo;
import de.micromata.genome.gwiki.model.GWikiGlobalConfig;
import de.micromata.genome.gwiki.model.GWikiLog;
import de.micromata.genome.gwiki.model.GWikiLogCategory;
import de.micromata.genome.gwiki.model.GWikiWeb;
import de.micromata.genome.gwiki.model.filter.GWikiUserLogonFilterEvent.LoginState;
import de.micromata.genome.gwiki.page.GWikiContext;
import de.micromata.genome.gwiki.page.attachments.TextExtractor;
import de.micromata.genome.gwiki.page.attachments.TxtTextExtractor;
import de.micromata.genome.gwiki.page.attachments.XmlTextExtractor;
import de.micromata.genome.gwiki.page.impl.GWikiWikiPageBaseArtefakt;
import de.micromata.genome.gwiki.plugin.GWikiPluginFilterDescriptor;
import de.micromata.genome.gwiki.utils.ClassUtils;
import de.micromata.genome.logging.GLog;
import de.micromata.genome.util.runtime.CallableX;

/**
 * Filter registered in GWiki.
 * 
 * @author Roger Rene Kommer (r.kommer@micromata.de)
 * 
 */
public class GWikiFilters
{
  private Set<Class<?>> knownFilter = new HashSet<Class<?>>();

  private List<GWikiServeElementFilter> serveElementFilters = new ArrayList<GWikiServeElementFilter>();

  private List<GWikiWikiPageRenderFilter> renderWikiPageFilters = new ArrayList<GWikiWikiPageRenderFilter>();

  private List<GWikiWikiPageCompileFilter> wikiCompileFilters = new ArrayList<GWikiWikiPageCompileFilter>();

  private List<GWikiLoadElementInfosFilter> loadPageInfosFilters = new ArrayList<GWikiLoadElementInfosFilter>();

  private List<GWikiStorageStoreElementFilter> storageStoreElementFilters = new ArrayList<GWikiStorageStoreElementFilter>();

  private List<GWikiStorageDeleteElementFilter> storageDeleteElementFilters = new ArrayList<GWikiStorageDeleteElementFilter>();

  private List<GWikiPageChangedFilter> pageChangedFilters = new ArrayList<GWikiPageChangedFilter>();

  private List<GWikiSkinRenderFilter> skinRenderFilters = new ArrayList<GWikiSkinRenderFilter>();

  private List<GWikiUserLogonFilter> userLogonFilters = new ArrayList<GWikiUserLogonFilter>();

  public ThreadLocal<List<Class<?>>> skipFilters = new ThreadLocal<List<Class<?>>>()
  {

    @Override
    protected List<Class<?>> initialValue()
    {
      return new ArrayList<Class<?>>();
    }

  };

  private Map<String, TextExtractor> textExtractors = new HashMap<String, TextExtractor>();

  private static Map<String, TextExtractor> standardTextExtractors = new HashMap<String, TextExtractor>();

  static {
    standardTextExtractors.put(".txt", new TxtTextExtractor());
    standardTextExtractors.put(".xml", new XmlTextExtractor());
    standardTextExtractors.put(".html", new XmlTextExtractor());
    standardTextExtractors.put(".htm", new XmlTextExtractor());
  }

  public <R> R runWithoutFilters(Class<?>[] filtersToSkip, CallableX<R, RuntimeException> callback)
  {
    List<Class<?>> of = skipFilters.get();
    List<Class<?>> nf = new ArrayList<Class<?>>();
    nf.addAll(of);
    for (Class<?> cls : filtersToSkip) {
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
    List<Class<?>> fscp = skipFilters.get();
    for (Class<?> cls : fscp) {
      if (cls.isAssignableFrom(filter.getClass()) == true) {
        return true;
      }
    }
    return false;
  }

  public void storeElement(GWikiContext ctx, GWikiElement el, Map<String, GWikiArtefakt<?>> parts,
      GWikiStorageStoreElementFilter target)
  {
    GWikiStorageStoreElementFilterEvent event = new GWikiStorageStoreElementFilterEvent(ctx, el, parts);
    new GWikiFilterChain<Void, GWikiStorageStoreElementFilterEvent, GWikiStorageStoreElementFilter>(this, target,
        storageStoreElementFilters).nextFilter(event);
  }

  public void deleteElement(GWikiContext ctx, GWikiElement el, Map<String, GWikiArtefakt<?>> parts,
      GWikiStorageDeleteElementFilter target)
  {
    GWikiStorageDeleteElementFilterEvent event = new GWikiStorageDeleteElementFilterEvent(ctx, el, parts);
    new GWikiFilterChain<Void, GWikiStorageDeleteElementFilterEvent, GWikiStorageDeleteElementFilter>(this, target,
        storageDeleteElementFilters).nextFilter(event);
  }

  public void serveElement(final GWikiContext ctx, final GWikiElement el, final GWikiServeElementFilter target)
  {
    ctx.runElement(el, new CallableX<Void, RuntimeException>()
    {
      @Override
      public Void call() throws RuntimeException
      {
        GWikiServeElementFilterEvent event = new GWikiServeElementFilterEvent(ctx, el);

        new GWikiFilterChain<Void, GWikiServeElementFilterEvent, GWikiServeElementFilter>(GWikiFilters.this, target,
            serveElementFilters)
                .nextFilter(event);
        return null;
      }
    });
  }

  public Boolean renderWikiWikiPage(GWikiContext ctx, GWikiWikiPageBaseArtefakt artefakt,
      GWikiWikiPageRenderFilter target)
  {
    GWikiWikiPageRenderFilterEvent event = new GWikiWikiPageRenderFilterEvent(ctx, artefakt);
    return new GWikiFilterChain<Boolean, GWikiWikiPageRenderFilterEvent, GWikiWikiPageRenderFilter>(this, target,
        renderWikiPageFilters)
            .nextFilter(event);
  }

  public void compileWikiWikiPage(GWikiContext ctx, GWikiElement element, GWikiWikiPageBaseArtefakt artefakt,
      GWikiWikiPageCompileFilter target)
  {
    GWikiWikiPageCompileFilterEvent event = new GWikiWikiPageCompileFilterEvent(ctx, element, artefakt);
    new GWikiFilterChain<Void, GWikiWikiPageCompileFilterEvent, GWikiWikiPageCompileFilter>(this, target,
        wikiCompileFilters)
            .nextFilter(event);
  }

  public void pageChanged(GWikiContext ctx, GWikiWeb wikiWeb, GWikiElementInfo newElement, GWikiElementInfo oldElement)
  {
    if (newElement != null) {
      String id = newElement.getId();
      if (oldElement != null) {
        GLog.note(GWikiLogCategory.Wiki, "Page changed:" + id);
      } else {
        GLog.note(GWikiLogCategory.Wiki, "Page New:" + id);
      }
    } else if (oldElement != null) {
      GLog.note(GWikiLogCategory.Wiki, "Page Deleted:" + oldElement.getId());
    }
    pageChanged(ctx, wikiWeb, newElement, oldElement, new GWikiPageChangedFilter()
    {

      @Override
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
    new GWikiFilterChain<Void, GWikiPageChangedFilterEvent, GWikiPageChangedFilter>(this, target, pageChangedFilters)
        .nextFilter(event);
  }

  public Map<String, GWikiElementInfo> loadPageInfos(GWikiContext ctx, Map<String, GWikiElementInfo> npageInfos,
      GWikiLoadElementInfosFilter target)
  {
    GWikiLoadElementInfosFilterEvent event = new GWikiLoadElementInfosFilterEvent(ctx, npageInfos);
    new GWikiFilterChain<Void, GWikiLoadElementInfosFilterEvent, GWikiLoadElementInfosFilter>(this, target,
        loadPageInfosFilters)
            .nextFilter(event);
    return event.getPageInfos();
  }

  /**
   * 
   * @param ctx
   * @param user
   * @return true if login was successfull
   */
  public boolean onLogin(GWikiContext ctx, GWikiSimpleUser user)
  {
    GWikiUserLogonFilterEvent event = new GWikiUserLogonFilterEvent(ctx, LoginState.Login, user);
    new GWikiFilterChain<Void, GWikiUserLogonFilterEvent, GWikiUserLogonFilter>(this, new GWikiUserLogonFilter()
    {

      @Override
      public Void filter(GWikiFilterChain<Void, GWikiUserLogonFilterEvent, GWikiUserLogonFilter> chain,
          GWikiUserLogonFilterEvent event)
      {
        if (event.isAbort() == true) {
          event.getWikiContext().getWikiWeb().getAuthorization().logout(event.getWikiContext());
        }
        return null;
      }
    }, userLogonFilters).nextFilter(event);
    return true;
  }

  /**
   * 
   * @param ctx
   * @param user
   * @return true if login was successfull
   */
  public boolean onLogout(GWikiContext ctx, GWikiSimpleUser user)
  {
    GWikiUserLogonFilterEvent event = new GWikiUserLogonFilterEvent(ctx, LoginState.Logout, user);
    new GWikiFilterChain<Void, GWikiUserLogonFilterEvent, GWikiUserLogonFilter>(this, new GWikiUserLogonFilter()
    {

      @Override
      public Void filter(GWikiFilterChain<Void, GWikiUserLogonFilterEvent, GWikiUserLogonFilter> chain,
          GWikiUserLogonFilterEvent event)
      {
        return null;
      }
    }, userLogonFilters).nextFilter(event);
    return true;
  }

  public void renderSkinGuiElement(GWikiContext ctx, String guiElement)
  {
    if (skinRenderFilters.isEmpty() == true) {
      return;
    }
    GWikiSkinRenderFilterEvent event = new GWikiSkinRenderFilterEvent(ctx, ctx.getCurrentElement(), guiElement);
    new GWikiFilterChain<Void, GWikiSkinRenderFilterEvent, GWikiSkinRenderFilter>(this, new GWikiSkinRenderFilter()
    {

      @Override
      public Void filter(GWikiFilterChain<Void, GWikiSkinRenderFilterEvent, GWikiSkinRenderFilter> chain,
          GWikiSkinRenderFilterEvent event)
      {
        return null;
      }
    }, skinRenderFilters)//
        .nextFilter(event);

  }

  public void registerNewFilterClass(GWikiWeb wikiWeb, Class<?> filter)
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
      serveElementFilters.add((GWikiServeElementFilter) obj);
    }
    if (obj instanceof GWikiWikiPageRenderFilter) {
      renderWikiPageFilters.add((GWikiWikiPageRenderFilter) obj);
    }
    if (obj instanceof GWikiLoadElementInfosFilter) {
      loadPageInfosFilters.add((GWikiLoadElementInfosFilter) obj);
    }
    if (obj instanceof GWikiWikiPageCompileFilter) {
      wikiCompileFilters.add((GWikiWikiPageCompileFilter) obj);
    }
    if (obj instanceof GWikiStorageStoreElementFilter) {
      storageStoreElementFilters.add((GWikiStorageStoreElementFilter) obj);
    }
    if (obj instanceof GWikiStorageDeleteElementFilter) {
      storageDeleteElementFilters.add((GWikiStorageDeleteElementFilter) obj);
    }
    if (obj instanceof GWikiPageChangedFilter) {
      pageChangedFilters.add((GWikiPageChangedFilter) obj);
    }
    if (obj instanceof GWikiSkinRenderFilter) {
      skinRenderFilters.add((GWikiSkinRenderFilter) obj);
    }
    if (obj instanceof GWikiUserLogonFilter) {
      userLogonFilters.add((GWikiUserLogonFilter) obj);
    }
  }

  // private Set<String> getCreateSet(Map<String, Set<String>> m, String k)
  // {
  // Set<String> ret = m.get(k);
  // if (ret != null) {
  // return ret;
  // }
  // ret = new HashSet<String>();
  // m.put(k, ret);
  // return ret;
  // }

  /**
   * put after behind before.
   * 
   * @param list
   * @param before
   * @param after
   */
  private void after(List<String> list, String before, String after)
  {
    int afterIdx = -1;
    for (int i = 0; i < list.size(); ++i) {
      String s = list.get(i);
      if (s.equals(before) == true) {
        if (afterIdx != -1) {
          list.remove(afterIdx);
          list.add(i, after);
        }
        return;
      }
      if (s.equals(after) == true) {
        afterIdx = i;
      }
    }
  }

  /**
   * put before before after
   * 
   * @param list
   * @param before
   * @param after
   */
  private void before(List<String> list, String before, String after)
  {
    int beforeIdx = -1;
    for (int i = list.size() - 1; i >= 0; --i) {
      String s = list.get(i);
      if (s.equals(after) == true) {
        if (beforeIdx != -1) {
          list.remove(beforeIdx);
          list.add(i, before);
        }
        return;
      }
      if (s.equals(before) == true) {
        beforeIdx = i;
      }
    }
  }

  /**
   * 
   * 
   * @param regClasses
   * @param pluginFilters
   * @return
   */
  public List<String> getSortedClasses(List<String> regClasses, final List<GWikiPluginFilterDescriptor> pluginFilters)
  {

    List<String> allFilters = new ArrayList<String>();
    if (regClasses != null) {
      allFilters.addAll(regClasses);
      // Collections.reverse(allFilters);
    }
    for (GWikiPluginFilterDescriptor pfd : pluginFilters) {
      allFilters.add(pfd.getClassName());
    }
    for (GWikiPluginFilterDescriptor pfd : pluginFilters) {
      if (pfd.getAfter() != null) {
        for (String as : pfd.getAfter()) {
          after(allFilters, as, pfd.getClassName());
        }
      }
      if (pfd.getBefore() != null) {
        for (String as : pfd.getBefore()) {
          before(allFilters, pfd.getClassName(), as);
        }
      }
    }
    // Collections.reverse(allFilters);
    return allFilters;
  }

  public void init(GWikiWeb wikiWeb, GWikiGlobalConfig wikiConfig)
  {
    List<String> regClasses = wikiConfig.getStringList("GWIKI_FILTER_CLASSES");
    final List<GWikiPluginFilterDescriptor> pluginFilters = wikiWeb.getDaoContext().getPluginRepository()
        .getPluginFilters();
    List<String> allClasses = getSortedClasses(regClasses, pluginFilters);
    for (String rc : allClasses) {
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
    initTextExtractors(wikiWeb, wikiConfig);
  }

  protected void initTextExtractors(GWikiWeb wikiWeb, GWikiGlobalConfig wikiConfig)
  {
    final Map<String, String> extm = wikiWeb.getDaoContext().getPluginRepository().getPluginTextExtractors();
    textExtractors.putAll(standardTextExtractors);
    ClassLoader cl = wikiWeb.getDaoContext().getPluginRepository().getActivePluginClassLoader();
    for (Map.Entry<String, String> me : extm.entrySet()) {
      try {
        Class<?> cls = Class.forName(me.getValue(), true, cl);
        TextExtractor extr = (TextExtractor) cls.newInstance();
        textExtractors.put(me.getKey(), extr);
      } catch (Exception ex) {
        GWikiLog.warn(
            "Cannot find text extractor class: " + me.getKey() + ": " + me.getValue() + "; " + ex.getMessage(), ex);
      }
    }
  }

  public void registerFilter(GWikiWeb wikiWeb, Class<?> filter)
  {
    if (knownFilter.contains(filter) == true) {
      return;
    }
    registerNewFilterClass(wikiWeb, filter);
  }

  public Map<String, TextExtractor> getTextExtractors()
  {
    return textExtractors;
  }

  public void setTextExtractors(Map<String, TextExtractor> textExtractors)
  {
    this.textExtractors = textExtractors;
  }

}
