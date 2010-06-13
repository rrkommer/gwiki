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

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import de.micromata.genome.gdbfs.CombinedFileSystem;
import de.micromata.genome.gdbfs.FileSystem;
import de.micromata.genome.gdbfs.FileSystemEventListener;
import de.micromata.genome.gdbfs.FileSystemEventType;
import de.micromata.genome.gdbfs.FsObject;
import de.micromata.genome.gdbfs.SubFileSystem;
import de.micromata.genome.util.matcher.Matcher;

/**
 * File system delegating "admin/plugins/" pathes to file system.
 * 
 * @author Roger Rene Kommer (r.kommer@micromata.de)
 * 
 */
public class GWikiPluginCombinedFileSystem extends CombinedFileSystem
{
  private GWikiPluginRepository pluginRepository;

  public GWikiPluginCombinedFileSystem(GWikiPluginRepository pluginRepository, FileSystem primary)
  {
    super(primary, null);
    this.pluginRepository = pluginRepository;
  }

  public void addMount(GWikiPlugin plugin)
  {
    // TODO send notifications
  }

  public void removeMount(GWikiPlugin plugin)
  {
    // TODO send notifications
  }

  public void registerListener(FileSystemEventType eventType, Matcher<String> fileNameMatcher, FileSystemEventListener listener)
  {
    primary.registerListener(eventType, fileNameMatcher, listener);
  }

  public List<FsObject> listFiles(String name, Matcher<String> matcher, Character searchType, boolean recursive)
  {
    List<FsObject> ret2 = primary.listFiles(name, matcher, searchType, recursive);
    List<FsObject> ret = new ArrayList<FsObject>();
    Set<String> names = new TreeSet<String>();
    ret.addAll(ret2);
    for (FsObject fs : ret2) {
      names.add(fs.getName());
    }
    for (GWikiPlugin plugin : pluginRepository.getActivePlugins()) {
      if (plugin.getGwikiFileSystem() == null) {
        if (plugin.getFileSystem().exists("content/gwiki") == true) {
          plugin.setGwikiFileSystem(new SubFileSystem(plugin.getFileSystem(), "content/gwiki"));
        }
      }
      if (plugin.getGwikiFileSystem() != null) {
        List<FsObject> fsl = plugin.getGwikiFileSystem().listFiles(name, matcher, searchType, recursive);
        ret.addAll(fsl);
      }
    }
    return ret;
  }

  private FileSystem getFs(String name)
  {
    for (GWikiPlugin plugin : pluginRepository.getActivePlugins()) {
      if (plugin.getGwikiFileSystem() != null && plugin.getGwikiFileSystem().exists(name) == true) {
        return plugin.getGwikiFileSystem();
      }
    }
    return getPrimary();
  }

  public void checkEvents(boolean force)
  {
    primary.checkEvents(force);
    for (GWikiPlugin plugin : pluginRepository.getActivePlugins()) {
      if (plugin.getGwikiFileSystem() != null) {
        plugin.getGwikiFileSystem().checkEvents(force);
      }
    }
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.micromata.genome.gdbfs.CombinedFileSystem#getFsForRead(java.lang.String)
   */
  @Override
  public FileSystem getFsForRead(String name)
  {
    return getFs(name);
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.micromata.genome.gdbfs.CombinedFileSystem#getFsForWrite(java.lang.String)
   */
  @Override
  public FileSystem getFsForWrite(String name)
  {
    return getPrimary();
  }

}
