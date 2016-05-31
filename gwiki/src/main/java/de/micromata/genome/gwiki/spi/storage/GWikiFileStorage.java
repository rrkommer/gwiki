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

package de.micromata.genome.gwiki.spi.storage;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;

import de.micromata.genome.gdbfs.FileNameUtils;
import de.micromata.genome.gdbfs.FileSystem;
import de.micromata.genome.gdbfs.FsObject;
import de.micromata.genome.gwiki.model.GWikiArtefakt;
import de.micromata.genome.gwiki.model.GWikiBinaryArtefakt;
import de.micromata.genome.gwiki.model.GWikiElement;
import de.micromata.genome.gwiki.model.GWikiElementFactory;
import de.micromata.genome.gwiki.model.GWikiElementInfo;
import de.micromata.genome.gwiki.model.GWikiGlobalConfig;
import de.micromata.genome.gwiki.model.GWikiPersistArtefakt;
import de.micromata.genome.gwiki.model.GWikiPropKeys;
import de.micromata.genome.gwiki.model.GWikiProps;
import de.micromata.genome.gwiki.model.GWikiPropsArtefakt;
import de.micromata.genome.gwiki.model.GWikiSettingsProps;
import de.micromata.genome.gwiki.model.GWikiStorage;
import de.micromata.genome.gwiki.model.GWikiTextArtefakt;
import de.micromata.genome.gwiki.model.GWikiWeb;
import de.micromata.genome.gwiki.model.config.GWikiMetaTemplate;
import de.micromata.genome.gwiki.model.filter.GWikiFilterChain;
import de.micromata.genome.gwiki.model.filter.GWikiStorageDeleteElementFilter;
import de.micromata.genome.gwiki.model.filter.GWikiStorageDeleteElementFilterEvent;
import de.micromata.genome.gwiki.model.filter.GWikiStorageStoreElementFilter;
import de.micromata.genome.gwiki.model.filter.GWikiStorageStoreElementFilterEvent;
import de.micromata.genome.gwiki.model.logging.GWikiLog;
import de.micromata.genome.gwiki.model.logging.GWikiLogCategory;
import de.micromata.genome.gwiki.page.GWikiContext;
import de.micromata.genome.gwiki.page.impl.wiki.macros.GWikiElementByPropComparator;
import de.micromata.genome.gwiki.page.search.GlobalIndexFile;
import de.micromata.genome.gwiki.page.search.IndexStoragePersistHandler;
import de.micromata.genome.gwiki.page.search.WordIndexTextArtefakt;
import de.micromata.genome.gwiki.utils.ClassUtils;
import de.micromata.genome.logging.GLog;
import de.micromata.genome.logging.LoggingServiceManager;
import de.micromata.genome.util.matcher.AndMatcher;
import de.micromata.genome.util.matcher.BooleanListRulesFactory;
import de.micromata.genome.util.matcher.Matcher;
import de.micromata.genome.util.matcher.NotMatcher;
import de.micromata.genome.util.matcher.string.RegExpMatcher;
import de.micromata.genome.util.matcher.string.StartWithMatcher;
import de.micromata.genome.util.runtime.CallableX;
import de.micromata.genome.util.runtime.RuntimeIOException;
import de.micromata.genome.util.types.Converter;
import de.micromata.genome.util.types.Pair;
import de.micromata.genome.util.types.TimeInMillis;

/**
 * Storage handle persistancy of GWiki Elements.
 * 
 * 
 * @author Roger Rene Kommer (r.kommer@micromata.de)
 * 
 */
public class GWikiFileStorage implements GWikiStorage
{

  /**
   * The standard lock timeout.
   */
  protected long standardLockTimeout = TimeInMillis.SECOND * 100;

  /**
   * The storage.
   */
  protected FileSystem storage;

  /**
   * The page types.
   */
  protected Map<String, String> pageTypes = new HashMap<String, String>();

  /**
   * TODO gwiki pruefen, ob das ueberhaupt noch notwendig ist.
   */
  protected Map<String, String> artefaktTypes = new HashMap<String, String>();

  /**
   * The Constant BeanConfigMetaTemplateSettingsFile.
   */
  private final static String BeanConfigMetaTemplateSettingsFile = "admin/templates/BeanConfigMetaTemplateSettings.properties";

  /**
   * The wiki web.
   */
  protected GWikiWeb wikiWeb;

  /**
   * Instantiates a new g wiki file storage.
   *
   * @param storage the storage
   */
  public GWikiFileStorage(FileSystem storage)
  {
    this.storage = storage;
    // TODO gwiki read /overwrite following from config
    pageTypes.put("config", "de.micromata.genome.gwiki.page.impl.GWikiConfigElement");
    pageTypes.put("gwiki", "de.micromata.genome.gwiki.page.impl.GWikiWikiPage");
    // pageTypes.put("attachment", "de.micromata.genome.gwiki.page.impl.GWikiFileAttachment");
    // pageTypes.put("i18n", "de.micromata.genome.gwiki.page.impl.GWikiI18nElement");

    artefaktTypes.put("gwiki", "de.micromata.genome.gwiki.page.impl.GWikiWikiPageArtefakt");
    artefaktTypes.put("html", "de.micromata.genome.gwiki.page.impl.GWikiHtmlArtefakt");
    artefaktTypes.put("pageSettings", "de.micromata.genome.gwiki.model.GWikiPropsArtefakt");
    artefaktTypes.put("pageTemplate", "de.micromata.genome.gwiki.page.impl.GWikiJspTemplateArtefakt");
    artefaktTypes.put("controler", "de.micromata.genome.gwiki.page.impl.GwikiControlerArtefakt");
  }

  @Override
  public String toString()
  {
    return "storage: " + ObjectUtils.toString(storage);
  }

  @Override
  public String getArtefaktClassNameFromType(String type)
  {
    return artefaktTypes.get(type);
  }

  /**
   * Resolve page infos.
   *
   * @param map the map
   */
  protected void resolvePageInfos(Map<String, GWikiElementInfo> map)
  {
    // currently not used
  }

  @Override
  public long getModificationCounter()
  {
    return storage.getModificationCounter();
  }

  /**
   * Load properties.
   *
   * @param name the name
   * @return the map
   */
  protected Map<String, String> loadProperties(String name)
  {
    Map<String, String> m = new HashMap<String, String>();
    loadProperties(name, m);
    return m;
  }

  /**
   * Load properties.
   *
   * @param name the name
   * @param map the map
   */
  @SuppressWarnings("unchecked")
  protected void loadProperties(String name, Map<String, String> map)
  {
    Properties props = new Properties();
    byte[] data = storage.readBinaryFile(name);
    if (data == null) {
      return;
    }
    try {
      props.load(new ByteArrayInputStream(data));
    } catch (IOException ex) {
      throw new RuntimeIOException("Failed to load properties: " + name + "; " + ex.getMessage(), ex);
    } catch (Exception ex) {
      throw new RuntimeException("Failed to load properties: " + name + "; " + ex.getMessage(), ex);
    }

    map.putAll((Map<String, String>) (Map<?, ?>) props);
  }

  /**
   * Store props.
   *
   * @param name the name
   * @param map the map
   */
  public void storeProps(String name, Map<String, String> map)
  {
    Properties props = new Properties();
    // no not put all, because properties does not like null values
    for (Map.Entry<String, String> me : map.entrySet()) {
      if (me.getKey() != null && me.getValue() != null) {
        props.setProperty(me.getKey(), me.getValue());
      }
    }
    ByteArrayOutputStream bout = new ByteArrayOutputStream();
    try {
      props.store(bout, "");
    } catch (IOException ex) {
      throw new RuntimeIOException(ex);
    }

    writeBinaryFile(name, bout.toByteArray(), true);
  }

  /**
   * Storage2 wiki path.
   *
   * @param path the path
   * @return the string
   */
  private String storage2WikiPath(String path)
  {
    if (path.startsWith("/") == true) {
      return path.substring(1);
    }
    return path;
  }

  @Override
  public <R> R runInTransaction(final long lockWaitTime, final CallableX<R, RuntimeException> callback)
  {
    return storage.runInTransaction(null, lockWaitTime, false, new CallableX<R, RuntimeException>()
    {

      @Override
      public R call() throws RuntimeException
      {
        return callback.call();
      }
    });
  }

  /**
   * After page info load.
   *
   * @param fo the fo
   * @param el the el
   * @param ret the ret
   * @return the g wiki element info
   */
  protected GWikiElementInfo afterPageInfoLoad(FsObject fo, GWikiElementInfo el,
      final Map<String, GWikiElementInfo> ret)
  {
    return el;
  }

  /**
   * Load page infos impl.
   *
   * @param fileSystem the file system
   * @param ret the ret
   */
  protected void loadPageInfosImpl(FileSystem fileSystem, final Map<String, GWikiElementInfo> ret)
  {
    Matcher<String> matcher = new BooleanListRulesFactory<String>()
        .createMatcher("+*Settings.properties,-*arch/*,-tmp/*");
    long stms = System.currentTimeMillis();
    List<FsObject> elements = fileSystem.listFiles("/", matcher, 'F', true);
    LoggingServiceManager.get().getStatsDAO().addPerformance(GWikiLogCategory.Wiki, "Fs.ListPageInfoFiles",
        System.currentTimeMillis() - stms, 0);
    stms = System.currentTimeMillis();

    for (FsObject fo : elements) {
      String e = storage2WikiPath(fo.getName());
      GWikiSettingsProps settings = new GWikiSettingsProps();
      loadProperties(e, settings.getMap());
      String mt = settings.getStringValue(GWikiPropKeys.WIKIMETATEMPLATE);
      GWikiMetaTemplate template = wikiWeb.findMetaTemplate(mt);
      if (template == null) {
        if (BeanConfigMetaTemplateSettingsFile.equals(e) == false) {
          GWikiLog.warn("Cannot load Metatemplate: '" + mt + "' for pageId:" + e);
          continue;
        }
      }
      GWikiElementInfo el = new GWikiElementInfo(settings, template);
      String id = e.substring(0, e.length() - GWikiStorage.SETTINGS_SUFFIX.length());
      el.setId(id);
      el = afterPageInfoLoad(fo, el, ret);
      if (el != null) {
        ret.put(id, el);
      }
    }
    LoggingServiceManager.get().getStatsDAO().addPerformance(GWikiLogCategory.Wiki, "Fs.LoadPageInfos",
        System.currentTimeMillis() - stms, 0);
    resolvePageInfos(ret);
  }

  /**
   * note: must synchronized, because otherwise deadlock with storege
   * 
   * @param ret
   */
  @Override
  public synchronized void loadPageInfos(final Map<String, GWikiElementInfo> ret)
  {
    storage.runInTransaction(null, standardLockTimeout, false, new CallableX<Void, RuntimeException>()
    {

      @Override
      public Void call() throws RuntimeException
      {
        loadPageInfosImpl(storage, ret);
        return null;
      }
    });

  }

  @Override
  public FileSystem getFileSystem()
  {
    return storage;
  }

  /**
   * Creates the element info.
   *
   * @param e the e
   * @return the g wiki element info
   */
  protected GWikiElementInfo createElementInfo(FsObject e)
  {
    GWikiProps p = new GWikiProps(loadProperties(e.getName()));
    String mt = p.getStringValue(GWikiPropKeys.WIKIMETATEMPLATE);

    GWikiElementInfo el = new GWikiElementInfo(p, wikiWeb.findMetaTemplate(mt));
    String sp = storage2WikiPath(e.getName());
    String cid = sp.substring(0, sp.length() - GWikiStorage.SETTINGS_SUFFIX.length());
    el.setId(cid);
    return el;
  }

  /**
   * Load version page infos.
   *
   * @param id the id
   * @return the list
   */
  public List<GWikiElementInfo> loadVersionPageInfos(String id)
  {

    int lidx = id.lastIndexOf('/');
    String archPath;
    if (lidx != -1) {
      String pp = id.substring(0, lidx);
      String ip = id.substring(lidx + 1);
      archPath = pp + "/arch/" + ip + "/";
    } else {
      archPath = "arch/" + id + "/";
    }
    return loadPageInfos(archPath);
  }

  @Override
  public List<String> findDeletedPages(Matcher<String> filter)
  {
    List<String> ret = new ArrayList<String>();
    // String matchers = "~\".+/arch/[^/]+\"";
    Matcher<String> matcher = new RegExpMatcher<String>(".+/arch/([^/]+)");// BooleanListRulesFactory<String>().createMatcher(matchers);
    matcher = new AndMatcher<String>(matcher, new NotMatcher<String>(new StartWithMatcher<String>("tmp/")));
    List<FsObject> fsobjs = storage.listFiles("", matcher, 'D', true);
    for (FsObject fso : fsobjs) {
      // String name = fso.getName();
      String namePart = fso.getNamePart();
      String pd = fso.getParent().getParent().getName();
      String id = FileNameUtils.join(pd, namePart);
      if (id.startsWith("/") == true) {
        id = id.substring(1);
      }
      if (filter != null && filter.match(id) == false) {
        continue;
      }
      if (wikiWeb.findElement(id) != null) {
        continue;
      }

      ret.add(id);
    }
    return ret;
  }

  @Override
  public List<GWikiElementInfo> loadPageInfos(String path)
  {
    List<GWikiElementInfo> ret = new ArrayList<GWikiElementInfo>();
    Matcher<String> matcher = new BooleanListRulesFactory<String>().createMatcher("*Settings.properties");
    List<FsObject> elements = storage.listFiles(path, matcher, 'F', true);
    for (FsObject e : elements) {
      ret.add(createElementInfo(e));
    }
    return ret;
  }

  /**
   * Gets the head.
   *
   * @param id the id
   * @return the head
   */
  protected Map<String, String> getHead(String id)
  {
    return loadProperties(id + GWikiStorage.SETTINGS_SUFFIX);
  }

  /**
   * Gets the string content.
   *
   * @param id the id
   * @param suffix the suffix
   * @return the string content
   */
  protected String getStringContent(String id, String suffix)
  {
    return storage.readTextFile(id + suffix);
  }

  /**
   * Inits the meta template.
   *
   * @param ei the ei
   */
  protected void initMetaTemplate(GWikiElementInfo ei)
  {
    if (ei.getMetaTemplate() != null) {
      return;
    }
    String mtk = ei.getProps().getStringValue(GWikiPropKeys.WIKIMETATEMPLATE);
    if (StringUtils.isEmpty(mtk) == true) {
      return;
    }
    GWikiElementInfo mti = GWikiWeb.get().findElementInfo(mtk);
    if (mti == null) {
      return;
    }

    GWikiMetaTemplate template = GWikiWeb.get().findMetaTemplate(mti.getId());
    ei.setMetaTemplate(template);
  }

  /**
   * Creates the hard wired element.
   *
   * @param type the type
   * @param ei the ei
   * @return the g wiki element
   */
  @SuppressWarnings("unchecked")
  protected GWikiElement createHardWiredElement(String type, GWikiElementInfo ei)
  {
    try {
      String typeClass = pageTypes.get(type);
      if (typeClass == null) {
        throw new RuntimeException("Unknown element type: " + type + " in id " + ei.getId());
      }
      Class<? extends GWikiElement> cls = (Class<? extends GWikiElement>) ClassUtils.classForName(typeClass);
      Constructor<? extends GWikiElement> constr = cls.getConstructor(new Class<?>[] { GWikiElementInfo.class });
      GWikiElement el = constr.newInstance(new Object[] { ei });
      return el;
    } catch (RuntimeException ex) {
      throw ex;
    } catch (Throwable ex) {
      throw new RuntimeException("Cannot instantiate: " + type + " in id " + ei.getId() + "; " + ex.getMessage(), ex);
    }
  }

  @Override
  public GWikiElement createElement(GWikiElementInfo ei)
  {
    initMetaTemplate(ei);
    String type = ei.getType();

    // String id = ei.getId();
    GWikiElement el;
    if (pageTypes.containsKey(type) == true) {
      el = createHardWiredElement(type, ei);
    } else {
      GWikiElementFactory elf = wikiWeb.getWikiConfig().getElementFactories().get(type);
      if (elf == null) {
        throw new RuntimeException("Cannot find GWikiElementFactory for type: " + type);

      }
      el = elf.createElement(ei, GWikiContext.getCurrent());
    }
    return el;

  }

  /**
   * Read binary if exists.
   *
   * @param fname the fname
   * @return the byte[]
   */
  byte[] readBinaryIfExists(String fname)
  {
    if (storage.exists(fname) == false) {
      return null;
    }
    return storage.readBinaryFile(fname);
  }

  /**
   * Read text if exists.
   *
   * @param fname the fname
   * @return the string
   */
  String readTextIfExists(String fname)
  {
    if (storage.exists(fname) == false) {
      return "";
    }
    return storage.readTextFile(fname);
  }

  @Override
  public GWikiElement hasModifiedArtefakts(GWikiElementInfo ei)
  {
    GWikiElement element = createElement(ei);
    Map<String, GWikiArtefakt<?>> parts = new HashMap<String, GWikiArtefakt<?>>();
    element.collectParts(parts);
    for (Map.Entry<String, GWikiArtefakt<?>> me : parts.entrySet()) {
      if ((me.getValue() instanceof GWikiPersistArtefakt) == false) {
        continue;
      }
      GWikiPersistArtefakt<?> art = (GWikiPersistArtefakt<?>) me.getValue();
      String fname = art.buildFileName(ei.getId(), me.getKey());
      FsObject fsobj = storage.getFileObject(fname);
      if (fsobj == null) {
        continue;
      }
      long modtime = fsobj.getLastModified();
      // long now = System.currentTimeMillis();
      if (modtime > ei.getLoadedTimeStamp()) {
        return loadElement(ei);
      }
    }
    return null;
  }

  @Override
  public GWikiElementInfo loadElementInfo(String path)
  {
    String fname = path + GWikiStorage.SETTINGS_SUFFIX;
    if (storage.exists(fname) == false) {
      return null;
    }
    FsObject obj = storage.getFileObject(fname);
    return createElementInfo(obj);
  }

  @Override
  public GWikiElement loadElement(String pageId)
  {
    GWikiElementInfo elinfo = loadElementInfo(pageId);
    if (elinfo == null) {
      return null;
    }
    return loadElement(elinfo);
  }

  /**
   * Load element impl.
   *
   * @param ei the ei
   * @return the g wiki element
   */
  public GWikiElement loadElementImpl(final GWikiElementInfo ei)
  {
    GWikiElement element = createElement(ei);
    Map<String, GWikiArtefakt<?>> parts = new HashMap<String, GWikiArtefakt<?>>();
    element.collectParts(parts);
    for (Map.Entry<String, GWikiArtefakt<?>> me : parts.entrySet()) {
      if (me.getKey().equals("Settings") == true) {
        continue;
      }
      if ((me.getValue() instanceof GWikiPersistArtefakt<?>) == false) {
        continue;
      }
      GWikiPersistArtefakt<?> art = (GWikiPersistArtefakt<?>) me.getValue();
      String fname = art.buildFileName(ei.getId(), me.getKey());
      if (art instanceof GWikiBinaryArtefakt<?>) {
        ((GWikiBinaryArtefakt<?>) art).setStorageData(readBinaryIfExists(fname));
      } else if (art instanceof GWikiTextArtefakt<?>) {
        ((GWikiTextArtefakt<?>) art).setStorageData(readTextIfExists(fname));
      } else if (art instanceof GWikiPropsArtefakt) {
        ((GWikiPropsArtefakt) art).setStorageData(loadProperties(fname));
      } else {
        throw new RuntimeException(
            "Unknown artefakt storage type: " + art.getClass().toString() + " in id: " + ei.getId());
      }
    }
    element.getElementInfo().setLoadedTimeStamp(System.currentTimeMillis());
    return element;

  }

  @Override
  public GWikiElement loadElement(final GWikiElementInfo ei)
  {
    return loadElementImpl(ei);
  }

  @Override
  public List<GWikiElementInfo> getVersions(String id)
  {
    return loadVersionPageInfos(id);
  }

  /**
   * first is dir or empty string.
   *
   * @param id the id
   * @return the pair
   */
  protected Pair<String, String> splitId(String id)
  {
    int lidx = id.lastIndexOf('/');
    if (lidx == -1) {
      return Pair.make("", id);
    }
    return Pair.make(id.substring(0, lidx), id.substring(lidx + 1));
  }

  /**
   * Clean up archived files.
   *
   * @param wikiContext the wiki context
   * @param el the el
   * @param maxcount the maxcount
   * @param maxdays the maxdays
   */
  protected void cleanUpArchivedFiles(final GWikiContext wikiContext, final GWikiElement el, int maxcount, int maxdays)
  {
    if (maxcount == -1 && maxdays == -1) {
      return;
    }
    List<GWikiElementInfo> archives = getVersions(el.getElementInfo().getId());
    List<GWikiElementInfo> keep = new ArrayList<GWikiElementInfo>();
    keep.addAll(archives);
    List<GWikiElementInfo> remove = new ArrayList<GWikiElementInfo>();
    long now = System.currentTimeMillis();
    Collections.sort(keep, new GWikiElementByPropComparator(GWikiPropKeys.MODIFIEDAT, (String) null));
    if (maxdays != -1) {
      for (Iterator<GWikiElementInfo> it = keep.iterator(); it.hasNext();) {
        GWikiElementInfo ei = it.next();
        if (maxdays != -1) {
          Date date = ei.getModifiedAt();
          if (date == null) {
            it.remove();
            remove.add(ei);
            continue;
          }
          long dif = (now - date.getTime()) / TimeInMillis.DAY;
          if (dif > maxdays) {
            it.remove();
            remove.add(ei);
            continue;
          }
        }
      }
    }
    if (maxcount != -1 && keep.size() > maxcount) {
      remove.addAll(keep.subList(0, keep.size() - maxcount));
    }
    for (GWikiElementInfo ei : remove) {
      final GWikiElement bel = loadElement(ei);
      final Map<String, GWikiArtefakt<?>> parts = getParts(bel);
      destroyElement(wikiContext, bel, parts);
    }
  }

  /**
   * Archive page.
   *
   * @param wikiContext the wiki context
   * @param el the el
   */
  protected void archivePage(final GWikiContext wikiContext, final GWikiElement el)
  {
    storage.runInTransaction(null, standardLockTimeout, false, new CallableX<Void, RuntimeException>()
    {

      @Override
      public Void call() throws RuntimeException
      {

        GWikiGlobalConfig wikiConfig = wikiContext.getWikiWeb().getWikiConfig();

        int maxc = wikiConfig.getArchiveMaxCount();
        int maxd = wikiConfig.getArchiveMaxDays();
        if (maxc == 0 || maxd == 0) {
          cleanUpArchivedFiles(wikiContext, el, maxc, maxd);
          return null;
        }

        // List<GWikiArtefakt> artefakts = el.getArtefakts();
        Map<String, GWikiArtefakt<?>> parts = new HashMap<String, GWikiArtefakt<?>>();
        el.collectParts(parts);
        GWikiElementInfo clelinfo = new GWikiElementInfo(el.getElementInfo());
        String mod = clelinfo.getProps().getStringValue(GWikiPropKeys.MODIFIEDAT);
        if (StringUtils.isBlank(mod) == true) {
          mod = GWikiProps.formatTimeStamp(new Date());
        }
        String id = clelinfo.getId();

        int lidx = id.lastIndexOf('/');
        String newId;
        String archivDir;
        if (lidx != -1) {
          String pp = id.substring(0, lidx);
          String ip = id.substring(lidx + 1);
          newId = pp + "/arch/" + ip + "/" + mod;
          archivDir = pp + "/arch/" + ip;
        } else {
          newId = "arch/" + id + "/" + mod;
          archivDir = "arch/" + id;
        }
        if (loadElementInfo(newId) != null) {
          return null;
        }
        clelinfo.setId(newId);

        for (Map.Entry<String, GWikiArtefakt<?>> me : parts.entrySet()) {
          if ((me.getValue() instanceof GWikiPersistArtefakt) == false) {
            continue;
          }
          GWikiPersistArtefakt<?> art = (GWikiPersistArtefakt<?>) me.getValue();
          String oldName = art.buildFileName(el.getElementInfo().getId(), me.getKey());
          String newName = art.buildFileName(clelinfo.getId(), me.getKey());
          if (storage.exists(oldName) == true) {
            if (storage.existsForWrite(archivDir) == false) {
              storage.mkdirs(archivDir);
            }
            boolean success = storage.rename(oldName, newName);
            if (success == false) {
              GWikiLog.warn("Cannot rename file. From: " + oldName + "; to: " + newName);
              success = storage.rename(oldName, newName);
            }
          }
        }
        cleanUpArchivedFiles(wikiContext, el, maxc, maxd);
        return null;
      }
    });
  }

  /**
   * Persist.
   *
   * @param wikiContext the wiki context
   * @param element the element
   * @param parts the parts
   */
  public void persist(final GWikiContext wikiContext, GWikiElement element, Map<String, GWikiArtefakt<?>> parts)
  {
    wikiWeb.getFilter().storeElement(wikiContext, element, parts, new GWikiStorageStoreElementFilter()
    {

      @Override
      public Void filter(
          GWikiFilterChain<Void, GWikiStorageStoreElementFilterEvent, GWikiStorageStoreElementFilter> chain,
          GWikiStorageStoreElementFilterEvent event)
      {
        storeImpl(event.getElement(), event.getParts());
        return null;
      }
    });
  }

  /**
   * Delete impl.
   *
   * @param wikiContext the wiki context
   * @param element the element
   * @param parts the parts
   */
  protected void deleteImpl(final GWikiContext wikiContext, final GWikiElement element,
      final Map<String, GWikiArtefakt<?>> parts)
  {

    storage.runInTransaction(null, standardLockTimeout, false, new CallableX<Void, RuntimeException>()
    {
      @Override
      public Void call() throws RuntimeException
      {
        if (element.getMetaTemplate() != null
            && element.getMetaTemplate().isNoArchiv() == false
            && wikiContext.getBooleanRequestAttribute(STORE_NO_ARCHIVE) != true) {
          archivePage(wikiContext, element);
        }
        destroyElement(wikiContext, element, parts);
        return null;
      }
    });
  }

  /**
   * Destroy element.
   *
   * @param wikiContext the wiki context
   * @param element the element
   * @param parts the parts
   */
  protected void destroyElement(final GWikiContext wikiContext, final GWikiElement element,
      final Map<String, GWikiArtefakt<?>> parts)
  {
    String id = element.getElementInfo().getId();
    for (Map.Entry<String, GWikiArtefakt<?>> me : parts.entrySet()) {
      if ((me.getValue() instanceof GWikiPersistArtefakt) == false) {
        continue;
      }
      GWikiPersistArtefakt<?> art = (GWikiPersistArtefakt<?>) me.getValue();
      String fname = art.buildFileName(id, me.getKey());
      boolean deleted = storage.delete(fname);
      if (deleted == false) {
        // TODO Log GLog.warn(Category.Wiki, "Cannot delete file: " + fname);
      }
    }
  }

  /**
   * Sets the version stamps.
   *
   * @param el the el
   * @param keepModifiedAt the keep modified at
   */
  protected void setVersionStamps(final GWikiElement el, final boolean keepModifiedAt)
  {
    GWikiProps p = el.getElementInfo().getProps();
    String uname = wikiWeb.getAuthorization().getCurrentUserName(GWikiContext.getCurrent());
    int oldVersion = p.getIntValue(GWikiPropKeys.VERSION, 0);
    p.setIntValue(GWikiPropKeys.VERSION, oldVersion + 1);
    Date now = new Date();
    if (p.getStringValue(GWikiPropKeys.CREATEDBY, null) == null) {
      p.setStringValue(GWikiPropKeys.CREATEDBY, uname);
    }
    if (p.getStringValue(GWikiPropKeys.CREATEDAT, null) == null) {
      p.setDateValue(GWikiPropKeys.CREATEDAT, now);
    }
    if (keepModifiedAt == false || p.getStringValue(GWikiPropKeys.MODIFIEDBY, null) == null) {
      p.setStringValue(GWikiPropKeys.MODIFIEDBY, uname);
    }
    if (keepModifiedAt == false || p.getStringValue(GWikiPropKeys.MODIFIEDAT, null) == null) {
      p.setDateValue(GWikiPropKeys.MODIFIEDAT, now);
    }
  }

  /**
   * Store element impl.
   *
   * @param wikiContext the wiki context
   * @param page the page
   * @param keepModifiedAt the keep modified at
   */
  public void storeElementImpl(final GWikiContext wikiContext, final GWikiElement page, final boolean keepModifiedAt)
  {
    storage.runInTransaction(null, standardLockTimeout, false, new CallableX<Void, RuntimeException>()
    {

      @Override
      public Void call() throws RuntimeException
      {
        setVersionStamps(page, keepModifiedAt);
        Map<String, GWikiArtefakt<?>> parts = getParts(page);
        persist(wikiContext, page, parts);
        return null;
      }
    });
  }

  @Override
  public GWikiElement storeElement(final GWikiContext wikiContext, final GWikiElement elm, final boolean keepModifiedAt)
  {
    return storage.runInTransaction(null, standardLockTimeout, false, new CallableX<GWikiElement, RuntimeException>()
    {

      @Override
      public GWikiElement call() throws RuntimeException
      {

        GWikiElement olel = loadElement(elm.getElementInfo().getId());

        if (elm.getElementInfo().getMetaTemplate() != null
            && elm.getElementInfo().getMetaTemplate().isNoArchiv() == false
            && wikiContext.getBooleanRequestAttribute(GWikiStorage.STORE_NO_ARCHIVE) == false) {
          if (olel != null) {
            try {
              archivePage(wikiContext, olel);
            } catch (Exception ex) {
              GWikiLog.warn("Cannot archive page: " + olel.getElementInfo().getId(), ex);
            }
          }
        } else {
          if (olel != null) {
            // TODO gwiki spaeter kommen noch filter! Falls diese abbrechen, ist die Seite WEG!!
            deleteElement(wikiContext, olel);
          }
        }
        storeElementImpl(wikiContext, elm, keepModifiedAt);
        return elm;
      }
    });

  }

  /**
   * Gets the parts.
   *
   * @param element the element
   * @return the parts
   */
  public static Map<String, GWikiArtefakt<?>> getParts(GWikiElement element)
  {
    Map<String, GWikiArtefakt<?>> parts = new HashMap<String, GWikiArtefakt<?>>();
    element.collectParts(parts);
    return parts;
  }

  @Override
  public void deleteElement(final GWikiContext wikiContext, GWikiElement element)
  {
    Map<String, GWikiArtefakt<?>> parts = getParts(element);
    wikiWeb.getFilter().deleteElement(wikiContext, element, parts, new GWikiStorageDeleteElementFilter()
    {

      @Override
      public Void filter(
          GWikiFilterChain<Void, GWikiStorageDeleteElementFilterEvent, GWikiStorageDeleteElementFilter> chain,
          GWikiStorageDeleteElementFilterEvent event)
      {
        deleteImpl(event.getWikiContext(), event.getElement(), event.getParts());
        return null;
      }
    });
    // for (StoragePersistEventHandler persHandler : beforePersistHandler) {
    // if (persHandler.onDelete(this, element, parts) == false) {
    // return;
    // }
    // }
    //

  }

  /**
   * Store impl.
   *
   * @param element the element
   * @param parts the parts
   */
  public void storeImpl(final GWikiElement element, final Map<String, GWikiArtefakt<?>> parts)
  {
    // TODO gwiki alles in einer transaktion laufen lassen.
    storage.runInTransaction(null, standardLockTimeout, false, new CallableX<Void, RuntimeException>()
    {

      @Override
      public Void call() throws RuntimeException
      {
        storeImplNoTrans(element, parts);
        return null;
      }
    });
  }

  /**
   * Gets the fs for write.
   *
   * @param fname the fname
   * @return the fs for write
   */
  protected FileSystem getFsForWrite(String fname)
  {
    FileSystem fs = storage.getFsForWrite(fname);
    String pn = FileNameUtils.getParentDir(fname);
    if (fs.existsForWrite(pn) == false) {
      fs.mkdirs(pn);
    }
    return fs;
  }

  /**
   * Write binary file.
   *
   * @param fname the fname
   * @param data the data
   * @param overWrite the over write
   */
  protected void writeBinaryFile(String fname, byte[] data, boolean overWrite)
  {
    FileSystem fs = getFsForWrite(fname);
    fs.writeBinaryFile(fname, data, overWrite);
  }

  /**
   * Write text file.
   *
   * @param fname the fname
   * @param data the data
   * @param overWrite the over write
   */
  protected void writeTextFile(String fname, String data, boolean overWrite)
  {
    FileSystem fs = getFsForWrite(fname);
    fs.writeTextFile(fname, data, overWrite);
  }

  /**
   * Store impl no trans.
   *
   * @param element the element
   * @param parts the parts
   */
  public void storeImplNoTrans(final GWikiElement element, final Map<String, GWikiArtefakt<?>> parts)
  {

    String id = element.getElementInfo().getId();
    if (id.indexOf('/') != -1) {
      FileSystem fsw = storage.getFsForWrite(id);
      String ppath = id.substring(0, id.lastIndexOf('/'));
      if (fsw.existsForWrite(ppath) == false) {
        if (fsw.mkdirs(ppath) == false) {
          GWikiLog.warn("Unable to create parent directory: " + ppath);
        }
      }
    }
    for (Map.Entry<String, GWikiArtefakt<?>> me : parts.entrySet()) {
      if ((me.getValue() instanceof GWikiPersistArtefakt<?>) == false) {
        continue;
      }

      GWikiPersistArtefakt<?> art = (GWikiPersistArtefakt<?>) me.getValue();
      String fname = art.buildFileName(id, me.getKey());
      if (art instanceof GWikiPropsArtefakt) {
        GWikiPropsArtefakt pa = (GWikiPropsArtefakt) art;
        Map<String, String> map = pa.getStorageData();
        if (map != null) {
          storeProps(fname, map);
        }
      } else if (art instanceof GWikiTextArtefakt<?>) {
        GWikiTextArtefakt<?> ta = (GWikiTextArtefakt<?>) art;
        String text = ta.getStorageData();
        if (text != null) {
          writeTextFile(fname, text, /* ta.isNoArchiveData() == true */true);
        }
      } else if (art instanceof GWikiBinaryArtefakt<?>) {
        GWikiBinaryArtefakt<?> ba = (GWikiBinaryArtefakt<?>) art;
        byte[] data = ba.getStorageData();
        if (data != null) {
          writeBinaryFile(fname, data, /* ta.isNoArchiveData() == true */true);
        }
      } else {
        throw new RuntimeException("Cannot store artefakt type: " + art.getClass().toString() + " from page: " + id);
      }
    }
  }

  @Override
  public boolean isArchivePageId(String path)
  {
    return path.startsWith("arch/") == true || path.contains("/arch/") == true;
  }

  /**
   * Gets the orginal page id from archive page id.
   *
   * @param path the path
   * @return the orginal page id from archive page id
   */
  public String getOrginalPageIdFromArchivePageId(String path)
  {
    if (isArchivePageId(path) == false) {
      throw new RuntimeException("Can only unarchive archived files: " + path);
    }
    // arch/pageid/page
    List<String> pels = Converter.parseStringTokens(path, "/", false);
    if (pels.size() < 3) {
      throw new RuntimeException("Wrong name scheme for archive files: " + path);
    }
    if (pels.get(pels.size() - 3).equals("arch") == false) {
      throw new RuntimeException("Wrong name scheme for archive files: " + path);
    }
    String pageId = pels.get(pels.size() - 2);
    StringBuilder np = new StringBuilder();
    for (int i = 0; i < pels.size() - 3; ++i) {
      if (np.length() > 0) {
        np.append("/");
      }
      np.append(pels.get(i));
    }
    if (np.length() > 0) {
      np.append("/");
    }
    np.append(pageId);
    String res = np.toString();
    return res;
  }

  @Override
  public GWikiElementInfo restoreElement(GWikiContext wikiContext, GWikiElement elm)
  {
    String orgPageId = getOrginalPageIdFromArchivePageId(elm.getElementInfo().getId());
    elm.getElementInfo().setId(orgPageId);
    storeElement(wikiContext, elm, true);
    return elm.getElementInfo();
  }

  /**
   * TODO NOT persist itself.
   * 
   * See de.micromata.genome.gwiki.model.GWikiStorage#rebuildIndex(java.util.Collection)
   *
   * @param wikiContext the wiki context
   * @param eis the eis
   * @param completeUpdate the complete update
   */
  @Override
  public void rebuildIndex(GWikiContext wikiContext, Iterable<GWikiElementInfo> eis, boolean completeUpdate)
  {
    Map<String, WordIndexTextArtefakt> textIndice = new HashMap<String, WordIndexTextArtefakt>();
    IndexStoragePersistHandler pe = new IndexStoragePersistHandler();
    List<Pair<GWikiElement, String>> ll = new ArrayList<Pair<GWikiElement, String>>();

    for (GWikiElementInfo ei : eis) {
      try {
        if (completeUpdate == false) {
          Date d = ei.getModifiedAt();
          String idxName = ei.getId() + IndexStoragePersistHandler.TEXTINDEX_PARTNAME + ".txt";

          FileSystem fs = getFsForWrite(idxName);
          if (fs.existsForWrite(idxName) == true) {
            long lm = fs.getLastModified(idxName);
            if (d != null && d.getTime() != 0 && lm > d.getTime()) {
              continue;
            }
          }
        }
        GWikiElement el = loadElement(ei);
        Map<String, GWikiArtefakt<?>> parts = getParts(el);
        Map<String, GWikiArtefakt<?>> cp = new HashMap<String, GWikiArtefakt<?>>();
        Map<String, GWikiArtefakt<?>> np = new HashMap<String, GWikiArtefakt<?>>();
        cp.putAll(parts);
        pe.createNewIndex(wikiContext, wikiContext.getWikiWeb().getStorage(), el, cp);
        if (cp.containsKey(IndexStoragePersistHandler.TEXTINDEX_PARTNAME) == false) {
          continue;
        }
        WordIndexTextArtefakt wit = (WordIndexTextArtefakt) cp.get(IndexStoragePersistHandler.TEXTINDEX_PARTNAME);
        textIndice.put(wit.getPageId(), wit);
        np.put(IndexStoragePersistHandler.TEXTINDEX_PARTNAME, wit);
        np.put(IndexStoragePersistHandler.TEXTEXTRACT_PARTNMAE,
            cp.get(IndexStoragePersistHandler.TEXTEXTRACT_PARTNMAE));
        storeImplNoTrans(el, np);

        if (completeUpdate == false) {
          ll.add(Pair.make(el, wit.getStorageData()));
          // GlobalIndexFile.updateSegment(wikiContext.getWikiWeb().getStorage(), el, wit.getStorageData());
        }
        GLog.note(GWikiLogCategory.Wiki, "Index updated for: " + ei.getId());
      } catch (Exception ex) {
        GWikiLog.warn("Failed to index: " + ei.getId() + ": " + ex.getMessage(), ex);
      }
    }
    if (completeUpdate == true) {
      GlobalIndexFile.writeGlobalIndexFiles(wikiContext, textIndice);
    } else {
      if (ll.isEmpty() == false) {
        GlobalIndexFile.updateSegments(this, ll);
      }
    }
  }

  public FileSystem getStorage()
  {
    return storage;
  }

  public void setStorage(FileSystem storage)
  {
    this.storage = storage;
  }

  public Map<String, String> getPageTypes()
  {
    return pageTypes;
  }

  public void setPageTypes(Map<String, String> pageTypes)
  {
    this.pageTypes = pageTypes;
  }

  public Map<String, String> getArtefaktTypes()
  {
    return artefaktTypes;
  }

  public void setArtefaktTypes(Map<String, String> artefaktTypes)
  {
    this.artefaktTypes = artefaktTypes;
  }

  public GWikiWeb getWikiWeb()
  {
    return wikiWeb;
  }

  @Override
  public void setWikiWeb(GWikiWeb wikiWeb)
  {
    this.wikiWeb = wikiWeb;
  }

  public long getStandardLockTimeout()
  {
    return standardLockTimeout;
  }

  public void setStandardLockTimeout(long standardLockTimeout)
  {
    this.standardLockTimeout = standardLockTimeout;
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.micromata.genome.gwiki.model.GWikiStorage#setFileSystem(de.micromata.genome.gdb`.FileSystem)
   */
  @Override
  public void setFileSystem(FileSystem fileSystem)
  {
    storage = fileSystem;
  }

}
