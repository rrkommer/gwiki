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
import de.micromata.genome.util.matcher.string.EndsWithMatcher;
import de.micromata.genome.util.runtime.RuntimeIOException;

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

  private FileSystem storePluginLocation;

  private CombinedClassLoader activePluginClassLoader;

  private GWikiPluginCombinedFileSystem pluginCombinedFileSystem;

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

  private void loadPlugin(GWikiWeb wikiWeb, FsObject dir)
  {
    try {
      String pluginxmlname = FileNameUtils.join(dir.getName(), "gwikiplugin.xml");
      if (dir.getFileSystem().exists(pluginxmlname) == false) {
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
    List<FsObject> fsObjects = fs.listFiles("", new EndsWithMatcher<String>(".zip"), 'F', false);
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
  }

  protected void activatePlugin(GWikiContext wikiContext, GWikiPlugin plugin)
  {
    initPluginClassPath(plugin.getDescriptor().getName(), plugin, wikiContext.getWikiWeb());
    activePlugins.add(plugin);
    plugin.setActivated(true);

  }

  private boolean reloadAfterActivation = true;

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
      activatePlugin(wikiContext, plugin);
      if (reloadAfterActivation == true) {
        wikiContext.getWikiWeb().reloadWeb();
      }
    }

  }

  private boolean shouldActivate(String name, GWikiPlugin plugin, GWikiWeb wikiWeb, GWikiGlobalConfig wikiConfig)
  {
    if (wikiConfig == null) {
      return false;
    }
    return wikiConfig.getActivePlugins().contains(name);
  }

  private void initPluginClassPath(String name, GWikiPlugin plugin, GWikiWeb wikiWeb)
  {
    GWikiPluginJavaClassLoader classLoader = new GWikiPluginJavaClassLoader();
    plugin.setPluginClassLoader(classLoader);
    try {
      if (plugin.getFileSystem().exists("classes") == true) {
        classLoader.addClassPath(new SubFileSystem(plugin.getFileSystem(), "classes").getFileObject(""));
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
    initPluginClassPath(name, plugin, wikiWeb);
    activePlugins.add(plugin);
    plugin.setActivated(true);
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

}
