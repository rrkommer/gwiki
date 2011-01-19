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
package de.micromata.genome.gwiki.plugin;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.xml.XmlBeanFactory;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;

import de.micromata.genome.gdbfs.FileNameUtils;
import de.micromata.genome.gdbfs.FileSystem;
import de.micromata.genome.gdbfs.FsObject;
import de.micromata.genome.gdbfs.SubFileSystem;
import de.micromata.genome.gdbfs.ZipRamFileSystem;
import de.micromata.genome.gwiki.model.GWikiElement;
import de.micromata.genome.gwiki.model.GWikiElementInfo;
import de.micromata.genome.gwiki.model.GWikiGlobalConfig;
import de.micromata.genome.gwiki.model.GWikiLog;
import de.micromata.genome.gwiki.model.GWikiPropsArtefakt;
import de.micromata.genome.gwiki.model.GWikiWeb;
import de.micromata.genome.gwiki.page.GWikiContext;
import de.micromata.genome.gwiki.page.impl.wiki.GWikiMacro;
import de.micromata.genome.gwiki.page.impl.wiki.GWikiMacroClassFactory;
import de.micromata.genome.gwiki.page.impl.wiki.GWikiMacroFactory;
import de.micromata.genome.gwiki.utils.ClassUtils;
import de.micromata.genome.util.matcher.BooleanListRulesFactory;
import de.micromata.genome.util.runtime.CallableX;
import de.micromata.genome.util.runtime.RuntimeIOException;
import de.micromata.genome.util.text.TextSplitterUtils;
import de.micromata.genome.util.types.Pair;

/**
 * Repository off all plugins.
 * 
 * @author Roger Rene Kommer (r.kommer@micromata.de)
 * 
 */
public class GWikiPluginRepository
{
  private Map<String, GWikiPlugin> plugins = new HashMap<String, GWikiPlugin>();;

  private List<GWikiPlugin> activePlugins = new ArrayList<GWikiPlugin>();

  private List<FileSystem> pluginLocations;

  /**
   * Where plugin zips are stored.
   */
  private FileSystem storePluginLocation;

  private CombinedClassLoader activePluginClassLoader;

  private GWikiPluginCombinedFileSystem pluginCombinedFileSystem;

  private boolean reloadAfterActivation = true;

  protected void clear()
  {
    plugins.clear();
    activePlugins.clear();
    // pluginLocations = null;
    // storePluginLocation = null;
    activePluginClassLoader = null;
    pluginCombinedFileSystem = null;
  }

  private GWikiPluginDescriptor loadDescriptor(FileSystem fs, String fn)
  {
    byte[] data = fs.readBinaryFile(fn);

    Resource resource = new ByteArrayResource(data);
    XmlBeanFactory bf = new XmlBeanFactory(resource);
    return (GWikiPluginDescriptor) bf.getBean("gwikiplugin");
  }

  private void initLifecycleListener(final GWikiWeb wikiWeb, final GWikiPlugin plugin)
  {
    wikiWeb.runInPluginContext(new CallableX<Void, RuntimeException>() {

      public Void call() throws RuntimeException
      {
        List<String> cll = TextSplitterUtils.parseStringTokenWOD(plugin.getDescriptor().getPluginLifecycleListener(), ',', ' ');
        for (String cl : cll) {
          plugin.getLifeCycleListener().add(ClassUtils.createDefaultInstance(cl, GWikiPluginLifecycleListener.class));
        }
        return null;
      }
    });

  }

  private enum PluginLayout
  {
    Pre10, JarSource
  }

  private void loadPlugin(GWikiWeb wikiWeb, FsObject dir)
  {
    try {

      String pluginxmlname = FileNameUtils.join(dir.getName(), "gwikiplugin.xml");
      if (dir.getFileSystem().exists(pluginxmlname) == false) {
        pluginxmlname = FileNameUtils.join(dir.getName(), "gwikiplugin.xml");
        GWikiLog.warn("No gwikiplugin.xml: " + pluginxmlname);
        return;
      }
      GWikiPluginDescriptor pdesc = loadDescriptor(dir.getFileSystem(), pluginxmlname);
      if (plugins.containsKey(pdesc.getName()) == true) {
        GWikiLog.note("Plugin already loaded: " + pdesc.getName());
        return;
      }
      GWikiPlugin plugin = new GWikiPlugin(new SubFileSystem(dir.getFileSystem(), dir.getName()), pdesc);
      plugins.put(pdesc.getName(), plugin);

    } catch (Exception ex) {
      GWikiLog.warn("Failed to load plugin from directory: " + dir.getName() + "; " + ex.getMessage(), ex);
    }
  }

  private void loadPlugins(GWikiWeb wikiWeb, FileSystem fs)
  {
    List<FsObject> fsObjects = fs.listFiles("", new BooleanListRulesFactory<String>().createMatcher("*.jar,*.zip"), 'F', false);
    for (FsObject fo : fsObjects) {
      byte[] data = fo.getFileSystem().readBinaryFile(fo.getName());
      ZipRamFileSystem fr = new ZipRamFileSystem(fo.getName(), new ByteArrayInputStream(data));
      loadPlugin(wikiWeb, fr.getFileObject(""));
    }
    fsObjects = fs.listFiles("", new BooleanListRulesFactory<String>().createMatcher("+*,-.*"), 'D', false);
    for (FsObject fo : fsObjects) {
      loadPlugin(wikiWeb, fo);
    }
  }

  protected void deactivatePlugin(GWikiContext wikiContext, GWikiPlugin plugin)
  {
    for (GWikiPluginLifecycleListener lcl : plugin.getLifeCycleListener()) {
      lcl.deactivate(wikiContext.getWikiWeb(), plugin);
    }
    if (activePlugins.remove(plugin) == false) {
      GWikiLog.warn("Plugin cannot be removed from activePlugin list: " + plugin.getDescriptor().getName());
    }
    if (activePluginClassLoader != null) {
      if (activePluginClassLoader.getParents().remove(plugin.getPluginClassLoader()) == false) {
        GWikiLog.warn("PluginClassLoader cannot be removed from activeClassLoader list: " + plugin.getDescriptor().getName());
      }
    }
    plugin.setPluginClassLoader(null);
    plugin.setActivated(false);
    for (GWikiPluginLifecycleListener lcl : plugin.getLifeCycleListener()) {
      lcl.deactivated(wikiContext.getWikiWeb(), plugin);
    }
  }

  protected void initLifecycleManager(GWikiWeb wikiWeb, GWikiPlugin plugin)
  {
    initLifecycleListener(wikiWeb, plugin);
    for (GWikiPluginLifecycleListener lcl : plugin.getLifeCycleListener()) {
      lcl.activated(wikiWeb, plugin);
    }
  }

  protected void activatePlugin(GWikiWeb wikiWeb, GWikiPlugin plugin)
  {
    initPluginClassPath(plugin.getDescriptor().getName(), plugin, wikiWeb);
    activePlugins.add(plugin);
    plugin.setActivated(true);
  }

  public void deactivatePlugin(GWikiContext wikiContext, String pluginName)
  {
    GWikiPlugin plugin = plugins.get(pluginName);
    boolean deactivated = false;
    if (plugin != null && plugin.isActivated() == true) {
      deactivatePlugin(wikiContext, plugin);
      deactivated = true;
    }
    GWikiGlobalConfig wikiConfig = wikiContext.getWikiWeb().getWikiConfig();
    List<String> activePlugins = wikiConfig.getActivePlugins();
    if (activePlugins.contains(pluginName) == true) {
      List<String> nal = new ArrayList<String>(activePlugins.size() + 1);
      nal.addAll(activePlugins);
      nal.remove(pluginName);
      wikiConfig.setStringList(GWikiGlobalConfig.GWIKI_ACTIVE_PLUGINS, nal);
      GWikiElement el = wikiContext.getWikiWeb().getElement(GWikiGlobalConfig.GWIKI_GLOBAL_CONFIG_PATH);
      ((GWikiPropsArtefakt) el.getMainPart()).setCompiledObject(wikiConfig);
      wikiContext.getWikiWeb().saveElement(wikiContext, el, false);
    }
    if (reloadAfterActivation == true && deactivated == true) {
      wikiContext.getWikiWeb().reloadWeb();
    }
  }

  public void activePlugin(GWikiContext wikiContext, String pluginName)
  {
    GWikiPlugin plugin = plugins.get(pluginName);
    if (plugin == null) {
      wikiContext.addSimpleValidationError("Plugin with name cannot be found: " + pluginName);
      return;
    }
    GWikiGlobalConfig wikiConfig = wikiContext.getWikiWeb().getWikiConfig();
    List<String> activePluginNames = wikiConfig.getActivePlugins();
    if (activePluginNames.contains(pluginName) == false) {
      List<String> nal = new ArrayList<String>(activePluginNames.size() + 1);
      nal.addAll(activePluginNames);
      nal.add(pluginName);
      wikiConfig.setStringList(GWikiGlobalConfig.GWIKI_ACTIVE_PLUGINS, nal);
      GWikiElement el = wikiContext.getWikiWeb().getElement(GWikiGlobalConfig.GWIKI_GLOBAL_CONFIG_PATH);
      ((GWikiPropsArtefakt) el.getMainPart()).setCompiledObject(wikiConfig);
      wikiContext.getWikiWeb().saveElement(wikiContext, el, false);
    }
    if (this.activePlugins.contains(plugin) == false) {
      activatePlugin(wikiContext.getWikiWeb(), plugin);

      if (reloadAfterActivation == true) {
        wikiContext.getWikiWeb().reloadWeb();
      }
    }

  }

  private boolean shouldActivate(String name, GWikiPlugin plugin, GWikiWeb wikiWeb, GWikiGlobalConfig wikiConfig)
  {
    for (GWikiPlugin pg : activePlugins) {
      if (pg == plugin) {
        return false;
      }
    }
    if (wikiConfig == null) {
      return false;
    }
    return wikiConfig.getActivePlugins().contains(name);
  }

  /*
   * @return first plugin name, second version condition.
   */
  private Pair<String, String> getPluginNameAndVersion(String pnn)
  {
    int idx = pnn.indexOf(':');
    if (idx == -1) {
      return Pair.make(pnn, "1.0");
    }
    return Pair.make(pnn.substring(0, idx), pnn.substring(idx + 1));
  }

  private ClassLoader getActiveClassLoader(Pair<String, String> pp)
  {
    for (GWikiPlugin p : activePlugins) {
      if (p.getDescriptor().getName().equals(pp.getFirst()) == true) {
        return p.getPluginClassLoader();
      }
    }
    return null;
  }

  private ClassLoader getDependingClassLoader(String pnn, GWikiPlugin plugin, GWikiWeb wikiWeb)
  {
    Pair<String, String> pp = getPluginNameAndVersion(pnn);
    ClassLoader ret = getActiveClassLoader(pp);
    if (ret != null) {
      return ret;
    }
    for (Map.Entry<String, GWikiPlugin> me : plugins.entrySet()) {
      if (me.getKey().equals(pp.getFirst()) == true) {
        activatePlugin(wikiWeb, me.getValue());
        ret = getActiveClassLoader(pp);
        break;
      }
    }
    if (ret == null) {
      throw new RuntimeException("Cannot find/load pending plugin " + pnn + " for plugin " + plugin.getDescriptor().getName());
    }
    return ret;
  }

  private ClassLoader getPluginClassLoader(GWikiPlugin plugin, GWikiWeb wikiWeb)
  {
    if (plugin.getDescriptor().getRequiredPlugins().isEmpty() == true) {
      return GWikiPluginJavaClassLoader.class.getClassLoader();
    }
    List<ClassLoader> parents = new ArrayList<ClassLoader>();
    for (String pn : plugin.getDescriptor().getRequiredPlugins()) {
      parents.add(getDependingClassLoader(pn, plugin, wikiWeb));
    }
    CombinedClassLoader cl = new CombinedClassLoader(parents);
    return cl;
  }

  private void initPluginClassPath(String name, GWikiPlugin plugin, GWikiWeb wikiWeb)
  {
    // Next version: add combined with pending classs loaders
    ClassLoader parentClassLoader = getPluginClassLoader(plugin, wikiWeb);
    GWikiPluginJavaClassLoader classLoader = new GWikiPluginJavaClassLoader(parentClassLoader);
    plugin.setPluginClassLoader(classLoader);
    classLoader.setPluginName(name);

    // classLoader.setIsolated(true);
    try {
      if (plugin.getFileSystem().exists("classes") == true) {
        classLoader.addClassPath(new SubFileSystem(plugin.getFileSystem(), "classes").getFileObject(""));
      }
      // in cases of external plugin projects
      if (plugin.getFileSystem().exists("target/classes") == true) {
        classLoader.addClassPath(new SubFileSystem(plugin.getFileSystem(), "target/classes").getFileObject(""));
      }
      if (plugin.getFileSystem().exists("lib") == true) {
        classLoader.addJarPath(plugin.getFileSystem().getFileObject("lib"));
      }
    } catch (IOException ex) {
      throw new RuntimeIOException(ex);
    }
  }

  private void initPlugin(String name, GWikiPlugin plugin, GWikiWeb wikiWeb, GWikiGlobalConfig wikiConfig)
  {
    if (shouldActivate(name, plugin, wikiWeb, wikiConfig) == false) {
      return;
    }
    activatePlugin(wikiWeb, plugin);
  }

  private void initPlugins(GWikiWeb wikiWeb, GWikiGlobalConfig wikiConfig)
  {
    for (Map.Entry<String, GWikiPlugin> me : plugins.entrySet()) {
      initPlugin(me.getKey(), me.getValue(), wikiWeb, wikiConfig);
    }
  }

  /**
   * conflict: use gwikiconfig to determine if plugin is loaded. but plugin should be initialized before reading file system.
   * 
   * @param wikiWeb
   */
  public void initPluginRepository(GWikiWeb wikiWeb, GWikiGlobalConfig wikiConfig)
  {
    clear();
    if (storePluginLocation == null) {
      storePluginLocation = new SubFileSystem(wikiWeb.getDaoContext().getStorage().getFileSystem(), "admin/plugins");
    }
    if (pluginLocations == null) {
      pluginLocations = new ArrayList<FileSystem>();
      pluginLocations.add(storePluginLocation);
    }
    for (FileSystem fs : pluginLocations) {
      loadPlugins(wikiWeb, fs);
    }
    initPlugins(wikiWeb, wikiConfig);
    if (activePlugins.isEmpty() == false) {
      FileSystem pfs = wikiWeb.getDaoContext().getStorage().getFileSystem();
      if (pfs instanceof GWikiPluginCombinedFileSystem) {
        GWikiPluginCombinedFileSystem ppcf = (GWikiPluginCombinedFileSystem) pfs;
        pfs = ppcf.getPrimary();
      }
      pluginCombinedFileSystem = new GWikiPluginCombinedFileSystem(this, pfs);
      wikiWeb.getDaoContext().getStorage().setFileSystem(pluginCombinedFileSystem);
      List<ClassLoader> classLoaders = new ArrayList<ClassLoader>();
      for (GWikiPlugin plugin : activePlugins) {
        if (plugin.getPluginClassLoader() != null) {
          classLoaders.add(plugin.getPluginClassLoader());
        }
      }
      if (classLoaders.isEmpty() == false) {
        activePluginClassLoader = new CombinedClassLoader(classLoaders);
        for (GWikiPlugin plugin : activePlugins) {
          initLifecycleManager(wikiWeb, plugin);
        }
      }
    }
  }

  /**
   * Set Thread context class loader with plugins.
   * 
   * @return previous context class loader.
   */
  public ClassLoader initClassLoader()
  {
    ClassLoader parent = Thread.currentThread().getContextClassLoader();
    if (activePluginClassLoader != null) {
      List<ClassLoader> cll = new ArrayList<ClassLoader>(activePluginClassLoader.getParents().size() + 1);
      cll.addAll(activePluginClassLoader.getParents());
      cll.add(parent);
      CombinedClassLoader nc = new CombinedClassLoader(cll);
      Thread.currentThread().setContextClassLoader(nc);
    }
    return parent;
  }

  public void getMacros(GWikiContext wikiContext, Map<String, GWikiMacroFactory> facs)
  {
    for (GWikiPlugin plugin : activePlugins) {
      Map<String, String> macros = plugin.getDescriptor().getMacros();
      for (Map.Entry<String, String> me : macros.entrySet()) {
        facs.put(me.getKey(), new GWikiMacroClassFactory((Class< ? extends GWikiMacro>) ClassUtils.classForName(me.getValue())));
      }
    }
  }

  public List<GWikiElementInfo> getTemplates(GWikiContext wikiContext)
  {
    List<GWikiElementInfo> ret = new ArrayList<GWikiElementInfo>();
    for (GWikiPlugin plugin : activePlugins) {
      for (String s : plugin.getDescriptor().getTemplates()) {
        GWikiElementInfo ei = wikiContext.getWikiWeb().findElementInfo(s);
        if (ei == null) {
          GWikiLog.warn("Cannot find template: " + s + " from plugin: " + plugin.getDescriptor().getName());
        } else {
          ret.add(ei);
        }
      }
    }
    return ret;
  }

  /**
   * 
   * @return filter descriptions of active plugins.
   */
  public List<GWikiPluginFilterDescriptor> getPluginFilters()
  {
    List<GWikiPluginFilterDescriptor> ret = new ArrayList<GWikiPluginFilterDescriptor>();
    for (GWikiPlugin plugin : activePlugins) {
      ret.addAll(plugin.getDescriptor().getFilter());
    }
    return ret;
  }

  /**
   * 
   * @return all text extractors.
   * 
   *         key: file extends
   * 
   *         value: class name
   */
  public Map<String, String> getPluginTextExtractors()
  {
    Map<String, String> ret = new HashMap<String, String>();
    for (GWikiPlugin plugin : activePlugins) {
      Map<String, String> p = plugin.getDescriptor().getTextExtractors();
      if (p != null) {
        ret.putAll(p);
      }
    }
    return ret;
  }

  public Map<String, GWikiPlugin> getPlugins()
  {
    return plugins;
  }

  public void setPlugins(Map<String, GWikiPlugin> plugins)
  {
    this.plugins = plugins;
  }

  public List<FileSystem> getPluginLocations()
  {
    return pluginLocations;
  }

  public void setPluginLocations(List<FileSystem> pluginLocations)
  {
    this.pluginLocations = pluginLocations;
  }

  public FileSystem getStorePluginLocation()
  {
    return storePluginLocation;
  }

  public void setStorePluginLocation(FileSystem storePluginLocation)
  {
    this.storePluginLocation = storePluginLocation;
  }

  public List<GWikiPlugin> getActivePlugins()
  {
    return activePlugins;
  }

  public void setActivePlugins(List<GWikiPlugin> activePlugins)
  {
    this.activePlugins = activePlugins;
  }

  public CombinedClassLoader getActivePluginClassLoader()
  {
    return activePluginClassLoader;
  }

  public GWikiPluginCombinedFileSystem getPluginCombinedFileSystem()
  {
    return pluginCombinedFileSystem;
  }

}
