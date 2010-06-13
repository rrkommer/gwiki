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
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.collections15.iterators.IteratorEnumeration;

import de.micromata.genome.gdbfs.FsObject;
import de.micromata.genome.gdbfs.FsObjectURLStreamHandler;
import de.micromata.genome.gdbfs.ZipRamFileSystem;
import de.micromata.genome.util.matcher.EveryMatcher;

/**
 * @author Roger Rene Kommer (r.kommer@micromata.de)
 * 
 */
public class GWikiPluginJavaClassLoader extends URLClassLoader
{
  // private FsObject storage;

  /**
   * List of resource paths
   */
  private List<Map<String, FsObject>> resPaths = new ArrayList<Map<String, FsObject>>();

  private Map<String, Class< ? >> loadedClasses = Collections.synchronizedMap(new HashMap<String, Class< ? >>());

  /**
   * for performance reason cache missing class requests.
   */
  private Set<String> missedClasses = Collections.synchronizedSet(new HashSet<String>());

  /**
   * If true, don't call parent class loader
   */
  private boolean isolated = false;

  public GWikiPluginJavaClassLoader()
  {
    super(new URL[0], Thread.currentThread().getContextClassLoader());
    // this.storage = storage;
  }

  public GWikiPluginJavaClassLoader(boolean isolated)
  {
    super(new URL[0], Thread.currentThread().getContextClassLoader());
    // this.storage = storage;
    this.isolated = isolated;
  }

  public GWikiPluginJavaClassLoader(ClassLoader parent)
  {
    super(new URL[0], parent);
    // this.storage = storage;
  }

  /**
   * copy constructor.
   * 
   * @param other must not null
   */
  public GWikiPluginJavaClassLoader(GWikiPluginJavaClassLoader other)
  {
    super(other.getURLs(), other.getParent());
    this.resPaths = other.resPaths;
    // this.storage = other.storage;
    this.isolated = other.isolated;
  }

  public URL[] getURLs()
  {
    List<URL> ret = new ArrayList<URL>();
    try {

      for (Map<String, FsObject> rsm : resPaths) {
        for (Map.Entry<String, FsObject> me : rsm.entrySet()) {
          ret.add(new URL("repository", "local", 0, me.getValue().getName(), new FsObjectURLStreamHandler(me.getValue())));
        }
      }
    } catch (MalformedURLException ex) {
      throw new RuntimeException("Ooops", ex);
    }
    URL[] r = ret.toArray(new URL[0]);

    return r;
  }

  public void addJar(FsObject storage)
  {
    try {
      Map<String, FsObject> fileMap = new HashMap<String, FsObject>();
      byte[] data = storage.getFileSystem().readBinaryFile(storage.getName());

      InputStream is = new ByteArrayInputStream(data);
      ZipRamFileSystem zfsys = new ZipRamFileSystem();
      zfsys.load(is);
      List<FsObject> files = zfsys.listFiles("", new EveryMatcher<String>(), 'F', true);
      for (FsObject fso : files) {
        fileMap.put(fso.getName(), fso);
      }
      // ZipInputStream zf = new ZipInputStream(is);
      // ZipEntry ze;
      //
      // while ((ze = zf.getNextEntry()) != null) {
      // String zeName = ze.getName().replace('\\', '/');
      // if (zeName.startsWith("/")) {
      // zeName = zeName.substring(1);// TODO check
      // }
      // if (zeName.endsWith("/") == false) {
      // fileMap.put(zeName, new StorageClassLoaderResPath(zeName, loader));
      // }
      // zf.closeEntry();
      // }
      // zf.close();
      resPaths.add(fileMap);
    } catch (Exception ex) {
      throw new RuntimeException("IO Failure while loading jar: " + storage.getName(), ex);
    }

  }

  /**
   * Add all Jars in given path inside the storage to classpath
   */
  public void addJarPath(FsObject storage) throws IOException
  {
    // rw fixed: vorher stand ein "." vor "jar"; es wird jedoch nur die Endung erwartet.
    List<FsObject> files = storage.getFileSystem().listFilesByPattern(storage.getName(), "*.jar", 'D', false);
    for (FsObject lf : files) {
      addJar(lf);
    }
  }

  /**
   * Add all ressources from classpath out of the storage
   */
  /**
   * @param storage Wher to find classPath
   * @param classpath
   * @throws IOException
   */
  public void addClassPath(FsObject storage) throws IOException
  {
    Map<String, FsObject> fileMap = new HashMap<String, FsObject>();
    List<FsObject> files = storage.getFileSystem().listFiles("", new EveryMatcher<String>(), 'F', true);
    for (FsObject lf : files) {
      String name = lf.getName();
      if (name.startsWith("/") == true) {
        name = name.substring(1);
      }
      fileMap.put(name, lf);
    }
    // ResLoader loader = new StorageLoader(new StorageEntry(storage, classpath));
    // Map<String, StorageClassLoaderResPath> fileMap = new HashMap<String, StorageClassLoaderResPath>();
    //
    // List<String> localFiles = storage.getFilesOfDirectory(classpath, null, true);
    // for (String lf : localFiles) {
    // // if (lf.endsWith(".class") == true) {
    // // lf = lf.replace('\\', '/');
    // // String cname = lf.substring(0, lf.length() - ".class".length()).replace('/', '.');
    // // // String cname = lf.substring(0, lf.length() - ".class".length());
    // // // classResources.put(cname, new StorageEntry(storage, FilenameUtils.normalize(classpath + "/" + lf)));
    // // } else {
    // // // otherResources.put(lf, new StorageEntry(storage, FilenameUtils.normalize(classpath + "/" + lf)));
    // // }
    // String normPath = lf.replace('\\', '/');
    // fileMap.put(normPath, new StorageClassLoaderResPath(normPath, loader));
    // }
    resPaths.add(fileMap);
  }

  /**
   * 
   * Construiert aus byte[] eine Klasse. Klassenname format bitte beachten
   * 
   * @param className so: de.micromata.Foo
   * @param content
   * @return
   */
  public Class< ? > loadDefine(String className, byte[] content)
  {
    Class< ? > lcls = super.defineClass(className, content, 0, content.length);
    loadedClasses.put(className, lcls);
    return lcls;
  }

  @Override
  public synchronized Class< ? > loadClass(String name, boolean resolve) throws ClassNotFoundException
  {

    if (loadedClasses.containsKey(name) == true)
      return loadedClasses.get(name);
    if (missedClasses.contains(name) == true)
      throw new ClassNotFoundException("Class " + name + " cannot be found (cached)");

    String clsName = name.replace('.', '/') + ".class";
    for (Map<String, FsObject> rsm : resPaths) {
      if (rsm.containsKey(clsName) == false) {
        continue;
      }
      FsObject sclr = rsm.get(clsName);
      byte[] data = sclr.getFileSystem().readBinaryFile(sclr.getName());
      Class< ? > cls = loadDefine(name, data);
      return cls;

    }
    if (isolated == true) {
      throw new ClassNotFoundException("Cannot find class: " + name);
    }
    try {
      Class< ? > cls = super.loadClass(name, resolve);
      if (cls == null) {
        missedClasses.add(name);
      }
      return cls;
    } catch (ClassNotFoundException ex) {
      missedClasses.add(name);
      throw ex;
    }
  }

  @Override
  public Enumeration<URL> getResources(String name) throws IOException
  {
    return new IteratorEnumeration<URL>(getResourceList(name, false).iterator());
  }

  public List<URL> getResourceList(String name, boolean onlyFirst) throws IOException
  {
    List<URL> ressources = new ArrayList<URL>();
    try {
      for (Map<String, FsObject> rsm : resPaths) {
        if (rsm.containsKey(name) == false) {
          continue;
        }
        FsObject rsp = rsm.get(name);
        ressources.add(new URL("repository", "local", 0, rsp.getName(), new FsObjectURLStreamHandler(rsp)));
      }
      if (isolated == true) {
        return ressources;
      }
      Enumeration<URL> suen = super.getResources(name);
      for (; suen.hasMoreElements();) {
        ressources.add(suen.nextElement());
        if (onlyFirst == true)
          return ressources;
      }
      return ressources;
    } catch (MalformedURLException ex) {
      throw new RuntimeException(ex);
    }
  }

  @Override
  public URL getResource(String name)
  {
    try {
      List<URL> l = getResourceList(name, true);
      if (l.size() > 0)
        return l.get(0);
      return null;
    } catch (IOException ex) {
      return null;
    }
  }

  @Override
  public InputStream getResourceAsStream(String name)
  {
    for (Map<String, FsObject> rsm : resPaths) {
      if (rsm.containsKey(name) == false) {
        continue;
      }
      FsObject rsp = rsm.get(name);
      byte[] data = rsp.getFileSystem().readBinaryFile(rsp.getName());
      return new ByteArrayInputStream(data);
    }
    if (isolated == true)
      return null;
    // if called super, in some case nullpointer occours
    return super.getResourceAsStream(name);
  }

  /**
   * Unfortunatelly this is needed as guard because default implementation of definePackage calls getPackage. Additionally Package
   * constructors all all private or protected.
   */
  private ThreadLocal<Boolean> inDefinePackage = new ThreadLocal<Boolean>();

  protected Package getPackage(String name)
  {
    if (inDefinePackage.get() != null) {// don't recursive
      return null;
    }
    Package sp = super.getPackage(name);
    if (sp != null) {
      return sp;
    }
    try {
      inDefinePackage.set(Boolean.TRUE);
      return definePackage(name, "", "1.0", "Generic", "Generic", "1.0", "Generic", null);
    } finally {
      inDefinePackage.set(null);
    }
  }

  public void setLoadedClasses(Map<String, Class< ? >> loadedClasses)
  {
    this.loadedClasses = loadedClasses;
  }

  public boolean isIsolated()
  {
    return isolated;
  }

  public void setIsolated(boolean isolated)
  {
    this.isolated = isolated;
  }

  public Map<String, Class< ? >> getLoadedClasses()
  {
    return loadedClasses;
  }
}
