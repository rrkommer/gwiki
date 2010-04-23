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

package de.micromata.genome.gwiki.spi.storage;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
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
import de.micromata.genome.gwiki.model.GWikiLog;
import de.micromata.genome.gwiki.model.GWikiPersistArtefakt;
import de.micromata.genome.gwiki.model.GWikiPropKeys;
import de.micromata.genome.gwiki.model.GWikiProps;
import de.micromata.genome.gwiki.model.GWikiPropsArtefakt;
import de.micromata.genome.gwiki.model.GWikiStorage;
import de.micromata.genome.gwiki.model.GWikiTextArtefakt;
import de.micromata.genome.gwiki.model.GWikiWeb;
import de.micromata.genome.gwiki.model.config.GWikiMetaTemplate;
import de.micromata.genome.gwiki.model.filter.GWikiFilterChain;
import de.micromata.genome.gwiki.model.filter.GWikiStorageDeleteElementFilter;
import de.micromata.genome.gwiki.model.filter.GWikiStorageDeleteElementFilterEvent;
import de.micromata.genome.gwiki.model.filter.GWikiStorageStoreElementFilter;
import de.micromata.genome.gwiki.model.filter.GWikiStorageStoreElementFilterEvent;
import de.micromata.genome.gwiki.page.GWikiContext;
import de.micromata.genome.gwiki.page.search.GlobalIndexFile;
import de.micromata.genome.gwiki.page.search.IndexStoragePersistHandler;
import de.micromata.genome.gwiki.page.search.WordIndexTextArtefakt;
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
  private long standardLockTimeout = TimeInMillis.SECOND * 10;

  private FileSystem storage;

  private Map<String, String> pageTypes = new HashMap<String, String>();

  /** TODO gwiki pruefen, ob das ueberhaupt noch notwendig ist */
  private Map<String, String> artefaktTypes = new HashMap<String, String>();

  private GWikiWeb wikiWeb;

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

  public String toString()
  {
    return "storage: " + ObjectUtils.toString(storage);
  }

  public String getArtefaktClassNameFromType(String type)
  {
    return artefaktTypes.get(type);
  }

  protected void resolvePageInfos(Map<String, GWikiElementInfo> map)
  {
    // currently not used
  }

  public long getModificationCounter()
  {
    return storage.getModificationCounter();
  }

  @SuppressWarnings("unchecked")
  protected Map<String, String> loadProperties(String name)
  {
    Map<String, String> map = new HashMap<String, String>();
    Properties props = new Properties();
    byte[] data = storage.readBinaryFile(name);
    if (data == null)
      return map;
    try {
      props.load(new ByteArrayInputStream(data));
    } catch (IOException ex) {
      throw new RuntimeIOException("Failed to load properties: " + name + "; " + ex.getMessage(), ex);
    } catch (Exception ex) {
      throw new RuntimeException("Failed to load properties: " + name + "; " + ex.getMessage(), ex);
    }

    map.putAll((Map<String, String>) (Map) props);
    return map;
  }

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

  private String storage2WikiPath(String path)
  {
    if (path.startsWith("/") == true)
      return path.substring(1);
    return path;
  }

  public <R> R runInTransaction(final long lockWaitTime, final CallableX<R, RuntimeException> callback)
  {
    return storage.runInTransaction(null, lockWaitTime, false, new CallableX<R, RuntimeException>() {

      public R call() throws RuntimeException
      {
        return callback.call();
      }
    });
  }

  /**
   * note: must synchronized, because otherwise deadlock with storege
   * 
   * @param ret
   */
  public synchronized void loadPageInfos(final Map<String, GWikiElementInfo> ret)
  {
    storage.runInTransaction(null, standardLockTimeout, false, new CallableX<Void, RuntimeException>() {

      public Void call() throws RuntimeException
      {
        Matcher<String> matcher = new BooleanListRulesFactory<String>().createMatcher("+*Settings.properties,-*arch/*,-tmp/*");
        long stms = System.currentTimeMillis();
        List<FsObject> elements = storage.listFiles("/", matcher, 'F', true);

        wikiWeb.getLogging().addPerformance("Fs.ListPageInfoFiles", System.currentTimeMillis() - stms, 0);
        stms = System.currentTimeMillis();
        for (FsObject fo : elements) {
          String e = storage2WikiPath(fo.getName());
          GWikiProps p = new GWikiProps(loadProperties(e));
          GWikiElementInfo el = new GWikiElementInfo(p, wikiWeb.findMetaTemplate(p.getStringValue(GWikiPropKeys.WIKIMETATEMPLATE)));
          String id = e.substring(0, e.length() - "Settings.properties".length());
          el.setId(id);
          p.setStringValue(GWikiPropKeys.PAGEID, id);
          ret.put(id, el);
        }
        wikiWeb.getLogging().addPerformance("Fs.LoadPageInfos", System.currentTimeMillis() - stms, 0);
        resolvePageInfos(ret);
        return null;
      }
    });

  }

  public FileSystem getFileSystem()
  {
    return storage;
  }

  protected GWikiElementInfo createElementInfo(FsObject e)
  {
    GWikiProps p = new GWikiProps(loadProperties(e.getName()));
    GWikiElementInfo el = new GWikiElementInfo(p, wikiWeb.findMetaTemplate(p.getStringValue(GWikiPropKeys.WIKIMETATEMPLATE)));
    String sp = storage2WikiPath(e.getName());
    String cid = sp.substring(0, sp.length() - "Settings.properties".length());
    el.setId(cid);
    p.setStringValue(GWikiPropKeys.PAGEID, cid);
    return el;
  }

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

  public List<GWikiElementInfo> loadPageInfos(String path)
  {
    List<GWikiElementInfo> ret = new ArrayList<GWikiElementInfo>();
    Matcher<String> matcher = new BooleanListRulesFactory<String>().createMatcher("*Settings.properties");
    List<FsObject> elements = storage.listFiles(path, matcher, 'F', true);
    for (FsObject e : elements) {
      ret.add(createElementInfo(e));
    }
    // resolvePageInfos(ret);
    return ret;
  }

  protected Map<String, String> getHead(String id)
  {
    return loadProperties(id + "Settings.properties");
  }

  protected String getStringContent(String id, String suffix)
  {
    return storage.readTextFile(id + suffix);
  }

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
    if (mti == null)
      return;

    GWikiMetaTemplate template = GWikiWeb.get().findMetaTemplate(mti.getId());
    ei.setMetaTemplate(template);
  }

  protected GWikiElement createHardWiredElement(String type, GWikiElementInfo ei)
  {
    try {
      String typeClass = pageTypes.get(type);
      if (typeClass == null) {
        throw new RuntimeException("Unknown element type: " + type + " in id " + ei.getId());
      }
      Class< ? extends GWikiElement> cls = (Class< ? extends GWikiElement>) Class.forName(typeClass);
      Constructor< ? extends GWikiElement> constr = cls.getConstructor(new Class< ? >[] { GWikiElementInfo.class});
      GWikiElement el = (GWikiElement) constr.newInstance(new Object[] { ei});
      return el;
    } catch (Throwable ex) {
      throw new RuntimeException("Cannot instantiate: " + type + " in id " + ei.getId() + "; " + ex.getMessage(), ex);
    }
  }

  @SuppressWarnings("unchecked")
  public GWikiElement createElement(GWikiElementInfo ei)
  {
    initMetaTemplate(ei);
    String type = ei.getType();
    if (StringUtils.isBlank(type) == true && ei.getMetaTemplate() != null) {
      type = ei.getMetaTemplate().getElementType();
    }
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

  byte[] readBinaryIfExists(String fname)
  {
    if (storage.exists(fname) == false) {
      return null;
    }
    return storage.readBinaryFile(fname);
  }

  String readTextIfExists(String fname)
  {
    if (storage.exists(fname) == false) {
      return "";
    }
    return storage.readTextFile(fname);
  }

  @SuppressWarnings("unchecked")
  public GWikiElement hasModifiedArtefakts(GWikiElementInfo ei)
  {
    GWikiElement element = createElement(ei);
    Map<String, GWikiArtefakt< ? >> parts = new HashMap<String, GWikiArtefakt< ? >>();
    element.collectParts(parts);
    for (Map.Entry<String, GWikiArtefakt< ? >> me : parts.entrySet()) {
      if ((me.getValue() instanceof GWikiPersistArtefakt) == false)
        continue;
      GWikiPersistArtefakt art = (GWikiPersistArtefakt) me.getValue();
      String fname = art.buildFileName(ei.getId(), me.getKey());
      FsObject fsobj = storage.getFileObject(fname);
      if (fsobj == null)
        continue;
      long modtime = fsobj.getModifiedAt().getTime();
      // long now = System.currentTimeMillis();
      if (modtime > ei.getLoadedTimeStamp())
        return loadElement(ei);
    }
    return null;
  }

  public GWikiElementInfo loadElementInfo(String path)
  {
    String fname = path + "Settings.properties";
    if (storage.exists(fname) == false)
      return null;
    FsObject obj = storage.getFileObject(fname);
    return createElementInfo(obj);
  }

  public GWikiElement loadElement(String pageId)
  {
    GWikiElementInfo elinfo = loadElementInfo(pageId);
    if (elinfo == null)
      return null;
    return loadElement(elinfo);
  }

  public GWikiElement loadElementImpl(final GWikiElementInfo ei)
  {
    GWikiElement element = createElement(ei);
    Map<String, GWikiArtefakt< ? >> parts = new HashMap<String, GWikiArtefakt< ? >>();
    element.collectParts(parts);
    for (Map.Entry<String, GWikiArtefakt< ? >> me : parts.entrySet()) {
      if (me.getKey().equals("Settings") == true) {
        continue;
      }
      if ((me.getValue() instanceof GWikiPersistArtefakt< ? >) == false)
        continue;
      GWikiPersistArtefakt< ? > art = (GWikiPersistArtefakt< ? >) me.getValue();
      String fname = art.buildFileName(ei.getId(), me.getKey());
      if (art instanceof GWikiBinaryArtefakt< ? >) {
        ((GWikiBinaryArtefakt< ? >) art).setStorageData(readBinaryIfExists(fname));
      } else if (art instanceof GWikiTextArtefakt< ? >) {
        ((GWikiTextArtefakt< ? >) art).setStorageData(readTextIfExists(fname));
      } else if (art instanceof GWikiPropsArtefakt) {
        ((GWikiPropsArtefakt) art).setStorageData(loadProperties(fname));
      } else {
        throw new RuntimeException("Unknown artefakt storage type: " + art.getClass().toString() + " in id: " + ei.getId());
      }
    }
    element.getElementInfo().setLoadedTimeStamp(System.currentTimeMillis());
    return element;

  }

  public GWikiElement loadElement(final GWikiElementInfo ei)
  {
    return loadElementImpl(ei);
  }

  public List<GWikiElementInfo> getVersions(String id)
  {
    return loadVersionPageInfos(id);
  }

  /**
   * first is dir or empty string
   * 
   * @param id
   * @return
   */
  protected Pair<String, String> splitId(String id)
  {
    int lidx = id.lastIndexOf('/');
    if (lidx == -1) {
      return Pair.make("", id);
    }
    return Pair.make(id.substring(0, lidx), id.substring(lidx + 1));
  }

  @SuppressWarnings("unchecked")
  protected void archivePage(final GWikiElement el)
  {
    storage.runInTransaction(null, standardLockTimeout, false, new CallableX<Void, RuntimeException>() {

      public Void call() throws RuntimeException
      {
        // List<GWikiArtefakt> artefakts = el.getArtefakts();
        Map<String, GWikiArtefakt< ? >> parts = new HashMap<String, GWikiArtefakt< ? >>();
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

        // artefakts.add();
        for (Map.Entry<String, GWikiArtefakt< ? >> me : parts.entrySet()) {
          if ((me.getValue() instanceof GWikiPersistArtefakt) == false)
            continue;
          GWikiPersistArtefakt art = (GWikiPersistArtefakt) me.getValue();
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
              // TODO Log
            }
          }

        }
        return null;
      }
    });
  }

  public void persist(final GWikiContext wikiContext, GWikiElement element, Map<String, GWikiArtefakt< ? >> parts)
  {
    wikiWeb.getFilter().storeElement(wikiContext, element, parts, new GWikiStorageStoreElementFilter() {

      public Void filter(GWikiFilterChain<Void, GWikiStorageStoreElementFilterEvent, GWikiStorageStoreElementFilter> chain,
          GWikiStorageStoreElementFilterEvent event)
      {
        storeImpl(event.getElement(), event.getParts());
        return null;
      }
    });
  }

  @SuppressWarnings("unchecked")
  protected void deleteImpl(final GWikiContext wikiContext, final GWikiElement element, final Map<String, GWikiArtefakt< ? >> parts)
  {

    storage.runInTransaction(null, standardLockTimeout, false, new CallableX<Void, RuntimeException>() {
      public Void call() throws RuntimeException
      {
        if (element.getMetaTemplate() != null
            && element.getMetaTemplate().isNoArchiv() == false
            && wikiContext.getBooleanRequestAttribute(STORE_NO_ARCHIVE) != true) {
          archivePage(element);
        }
        String id = element.getElementInfo().getId();
        for (Map.Entry<String, GWikiArtefakt< ? >> me : parts.entrySet()) {
          if ((me.getValue() instanceof GWikiPersistArtefakt) == false)
            continue;
          GWikiPersistArtefakt art = (GWikiPersistArtefakt) me.getValue();
          String fname = art.buildFileName(id, me.getKey());
          boolean deleted = storage.delete(fname);
          if (deleted == false) {
            // TODO Log GLog.warn(Category.Wiki, "Cannot delete file: " + fname);
          }
        }
        return null;
      }
    });

  }

  protected void setVersionStamps(final GWikiElement el, final boolean keepModifiedAt)
  {
    GWikiProps p = el.getElementInfo().getProps();
    String uname = wikiWeb.getAuthorization().getCurrentUserName(GWikiContext.getCurrent());
    Date now = new Date();
    if (p.getStringValue(GWikiPropKeys.CREATEDBY, null) == null) {
      p.setStringValue(GWikiPropKeys.CREATEDBY, uname);
    }
    if (p.getStringValue(GWikiPropKeys.CREATEDAT, null) == null) {
      p.setDateValue(GWikiPropKeys.CREATEDBY, now);
    }
    if (keepModifiedAt == false || p.getStringValue(GWikiPropKeys.MODIFIEDBY, null) == null) {
      p.setStringValue(GWikiPropKeys.MODIFIEDBY, uname);
    }
    if (keepModifiedAt == false || p.getStringValue(GWikiPropKeys.MODIFIEDAT, null) == null) {
      p.setDateValue(GWikiPropKeys.MODIFIEDAT, now);
    }
  }

  public void storeElementImpl(final GWikiContext wikiContext, final GWikiElement page, final boolean keepModifiedAt)
  {
    storage.runInTransaction(null, standardLockTimeout, false, new CallableX<Void, RuntimeException>() {

      public Void call() throws RuntimeException
      {
        setVersionStamps(page, keepModifiedAt);
        Map<String, GWikiArtefakt< ? >> parts = getParts(page);
        persist(wikiContext, page, parts);
        return null;
      }
    });
  }

  public GWikiElement storeElement(final GWikiContext wikiContext, final GWikiElement elm, final boolean keepModifiedAt)
  {
    return storage.runInTransaction(null, standardLockTimeout, false, new CallableX<GWikiElement, RuntimeException>() {

      public GWikiElement call() throws RuntimeException
      {

        GWikiElement olel = loadElement(elm.getElementInfo().getId());

        if (elm.getElementInfo().getMetaTemplate() != null
            && elm.getElementInfo().getMetaTemplate().isNoArchiv() == false
            && wikiContext.getBooleanRequestAttribute(GWikiStorage.STORE_NO_ARCHIVE) == false) {
          if (olel != null) {
            try {
              archivePage(olel);
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

  public static Map<String, GWikiArtefakt< ? >> getParts(GWikiElement element)
  {
    Map<String, GWikiArtefakt< ? >> parts = new HashMap<String, GWikiArtefakt< ? >>();
    element.collectParts(parts);
    return parts;
  }

  public void deleteElement(final GWikiContext wikiContext, GWikiElement element)
  {
    Map<String, GWikiArtefakt< ? >> parts = getParts(element);
    wikiWeb.getFilter().deleteElement(wikiContext, element, parts, new GWikiStorageDeleteElementFilter() {

      public Void filter(GWikiFilterChain<Void, GWikiStorageDeleteElementFilterEvent, GWikiStorageDeleteElementFilter> chain,
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

  public void storeImpl(final GWikiElement element, final Map<String, GWikiArtefakt< ? >> parts)
  {
    // TODO gwiki alles in einer transaktion laufen lassen.
    storage.runInTransaction(null, standardLockTimeout, false, new CallableX<Void, RuntimeException>() {

      public Void call() throws RuntimeException
      {
        storeImplNoTrans(element, parts);
        return null;
      }
    });
  }

  protected FileSystem getFsForWrite(String fname)
  {
    FileSystem fs = storage.getFsForWrite(fname);
    String pn = FileNameUtils.getParentDir(fname);
    if (fs.existsForWrite(pn) == false) {
      fs.mkdirs(pn);
    }
    return fs;
  }

  protected void writeBinaryFile(String fname, byte[] data, boolean overWrite)
  {
    FileSystem fs = getFsForWrite(fname);
    fs.writeBinaryFile(fname, data, overWrite);
  }

  protected void writeTextFile(String fname, String data, boolean overWrite)
  {
    FileSystem fs = getFsForWrite(fname);
    fs.writeTextFile(fname, data, overWrite);
  }

  public void storeImplNoTrans(final GWikiElement element, final Map<String, GWikiArtefakt< ? >> parts)
  {

    String id = element.getElementInfo().getId();
    if (id.indexOf('/') != -1) {
      FileSystem fsw = storage.getFsForWrite(id);
      String ppath = id.substring(0, id.lastIndexOf('/'));
      if (fsw.existsForWrite(ppath) == false) {
        fsw.mkdirs(ppath);
      }
    }
    for (Map.Entry<String, GWikiArtefakt< ? >> me : parts.entrySet()) {
      if ((me.getValue() instanceof GWikiPersistArtefakt< ? >) == false)
        continue;

      GWikiPersistArtefakt< ? > art = (GWikiPersistArtefakt< ? >) me.getValue();
      String fname = art.buildFileName(id, me.getKey());
      if (art instanceof GWikiPropsArtefakt) {
        GWikiPropsArtefakt pa = (GWikiPropsArtefakt) art;
        Map<String, String> map = pa.getStorageData();
        if (map != null) {
          storeProps(fname, map);
        }
      } else if (art instanceof GWikiTextArtefakt< ? >) {
        GWikiTextArtefakt< ? > ta = (GWikiTextArtefakt< ? >) art;
        String text = ta.getStorageData();
        if (text != null) {
          writeTextFile(fname, text, /* ta.isNoArchiveData() == true */true);
        }
      } else if (art instanceof GWikiBinaryArtefakt< ? >) {
        GWikiBinaryArtefakt< ? > ba = (GWikiBinaryArtefakt< ? >) art;
        byte[] data = ba.getStorageData();
        if (data != null) {
          writeBinaryFile(fname, data, /* ta.isNoArchiveData() == true */true);
        }
      } else {
        throw new RuntimeException("Cannot store artefakt type: " + art.getClass().toString() + " from page: " + id);
      }
    }
  }

  public boolean isArchivePageId(String path)
  {
    return path.startsWith("arch/") == true || path.contains("/arch/") == true;
  }

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
   * @see de.micromata.genome.gwiki.model.GWikiStorage#rebuildIndex(java.util.Collection)
   */
  public void rebuildIndex(GWikiContext wikiContext, Collection<GWikiElementInfo> eis, boolean completeUpdate)
  {
    Map<String, WordIndexTextArtefakt> textIndice = new HashMap<String, WordIndexTextArtefakt>();
    IndexStoragePersistHandler pe = new IndexStoragePersistHandler();
    for (GWikiElementInfo ei : eis) {
      try {
        GWikiElement el = loadElement(ei);
        Map<String, GWikiArtefakt< ? >> parts = getParts(el);
        Map<String, GWikiArtefakt< ? >> cp = new HashMap<String, GWikiArtefakt< ? >>();
        Map<String, GWikiArtefakt< ? >> np = new HashMap<String, GWikiArtefakt< ? >>();
        cp.putAll(parts);
        pe.createNewIndex(wikiContext, wikiContext.getWikiWeb().getStorage(), el, cp);
        if (cp.containsKey(IndexStoragePersistHandler.TEXTINDEX_PARTNAME) == false) {
          continue;
        }
        WordIndexTextArtefakt wit = (WordIndexTextArtefakt) cp.get(IndexStoragePersistHandler.TEXTINDEX_PARTNAME);
        textIndice.put(wit.getPageId(), wit);
        np.put(IndexStoragePersistHandler.TEXTINDEX_PARTNAME, wit);
        np.put(IndexStoragePersistHandler.TEXTEXTRACT_PARTNMAE, cp.get(IndexStoragePersistHandler.TEXTEXTRACT_PARTNMAE));
        storeImplNoTrans(el, np);
        if (completeUpdate == false) {
          GlobalIndexFile.updateSegment(wikiContext.getWikiWeb().getStorage(), el, wit.getStorageData());
        }
      } catch (Exception ex) {
        GWikiLog.warn("Failed to index: " + ei.getId() + ": " + ex.getMessage(), ex);
      }
    }
    if (completeUpdate == true) {
      GlobalIndexFile.writeGlobalIndexFiles(wikiContext, textIndice);
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

}
