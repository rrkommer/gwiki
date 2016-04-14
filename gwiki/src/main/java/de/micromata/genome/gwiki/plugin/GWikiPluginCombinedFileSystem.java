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
    setAutoCreateDirectories(true);
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

  protected void parentToThis(FsObject f)
  {
    f.setFileSystem(this);
  }

  public FsObject getFileObject(final String name)
  {
    FsObject sfs = super.getFileObject(name);
    if (sfs == null) {
      return null;
    }
    if (sfs.getFileSystem() != this) {
      sfs = (FsObject) sfs.clone();
      sfs.setFileSystem(this);
    }
    return sfs;
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
        for (FsObject l : fsl) {
          FsObject lc = (FsObject) l.clone();
          parentToThis(lc);
          ret.add(lc);
        }
      }
    }
    for (GWikiPlugin plugin : pluginRepository.getPassivePlugins()) {
      FileSystem fs = plugin.getFileSystem();
      boolean existspub = fs.exists("content/gwiki/pub");
      if (existspub == false) {
        continue;
      }
      if (plugin.getGwikiFileSystem() == null) {
        if (plugin.getFileSystem().exists("content/gwiki") == true) {
          plugin.setGwikiFileSystem(new SubFileSystem(plugin.getFileSystem(), "content/gwiki"));
        }
      }
      if (plugin.getGwikiFileSystem() != null) {
        List<FsObject> fsl = plugin.getGwikiFileSystem().listFiles(name, matcher, searchType, recursive);
        for (FsObject l : fsl) {
          if (l.getPathPart().startsWith("pub/") == false) {
            continue;
          }
          FsObject lc = (FsObject) l.clone();
          parentToThis(lc);
          ret.add(lc);
        }
      }
    }
    return ret;
  }

  private FileSystem getFs(String name)
  {
    for (GWikiPlugin plugin : pluginRepository.getActivePlugins()) {
      if (plugin.getGwikiFileSystem() != null && plugin.getGwikiFileSystem().exists(name) == true) {
        if (plugin.getDescriptor().isPrimaryFsRead(name) == true) {
          if (primary.exists(name) == true) {
            return primary;
          }
        }
        return plugin.getGwikiFileSystem();
      }
    }
    if (name.startsWith("pub/") == false) {
      return getPrimary();
    }
    for (GWikiPlugin plugin : pluginRepository.getPassivePlugins()) {
      if (plugin.getGwikiFileSystem() != null && plugin.getGwikiFileSystem().exists(name) == true) {
        if (plugin.getDescriptor().isPrimaryFsRead(name) == true) {
          if (primary.exists(name) == true) {
            return primary;
          }
        }
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
    FileSystem fsOrg = getFs(name);
    if (fsOrg.isReadOnly() == false) {
      return fsOrg;
    }
    return getPrimary();
  }

}
