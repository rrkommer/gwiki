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

import de.micromata.genome.gdbfs.FileSystem;

/**
 * Loaded Plugin
 * 
 * @author Roger Rene Kommer (r.kommer@micromata.de)
 * 
 */
public class GWikiPlugin
{
  /**
   * The descriptor from gwikiplugin.xml
   */
  private GWikiPluginDescriptor descriptor;

  /**
   * The local file system
   */
  private FileSystem fileSystem;

  /**
   * The class loader for this plugin
   */
  private GWikiPluginJavaClassLoader pluginClassLoader;

  /**
   * Is activated.
   */
  private boolean activated = false;

  /**
   * The file system with gwiki files.
   */
  private FileSystem gwikiFileSystem;

  /**
   * File system mounted to admin/pluginfs/{pluginname}/*
   */
  private FileSystem mountedFileSystem;

  private List<GWikiPluginLifecycleListener> lifeCycleListener = new ArrayList<GWikiPluginLifecycleListener>();

  public GWikiPlugin()
  {

  }

  public GWikiPlugin(FileSystem fileSystem, GWikiPluginDescriptor descriptor)
  {
    this.fileSystem = fileSystem;
    this.descriptor = descriptor;
  }

  public GWikiPluginDescriptor getDescriptor()
  {
    return descriptor;
  }

  public void setDescriptor(GWikiPluginDescriptor descriptor)
  {
    this.descriptor = descriptor;
  }

  public FileSystem getFileSystem()
  {
    return fileSystem;
  }

  public void setFileSystem(FileSystem fileSystem)
  {
    this.fileSystem = fileSystem;
  }

  public GWikiPluginJavaClassLoader getPluginClassLoader()
  {
    return pluginClassLoader;
  }

  public void setPluginClassLoader(GWikiPluginJavaClassLoader pluginClassLoader)
  {
    this.pluginClassLoader = pluginClassLoader;
  }

  public FileSystem getGwikiFileSystem()
  {
    return gwikiFileSystem;
  }

  public void setGwikiFileSystem(FileSystem gwikiFileSystem)
  {
    this.gwikiFileSystem = gwikiFileSystem;
  }

  public boolean isActivated()
  {
    return activated;
  }

  public void setActivated(boolean activated)
  {
    this.activated = activated;
  }

  public FileSystem getMountedFileSystem()
  {
    return mountedFileSystem;
  }

  public void setMountedFileSystem(FileSystem mountedFileSystem)
  {
    this.mountedFileSystem = mountedFileSystem;
  }

  public List<GWikiPluginLifecycleListener> getLifeCycleListener()
  {
    return lifeCycleListener;
  }

  public void setLifeCycleListener(List<GWikiPluginLifecycleListener> lifeCycleListener)
  {
    this.lifeCycleListener = lifeCycleListener;
  }
}
