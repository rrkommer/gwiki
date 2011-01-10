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

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections15.iterators.IteratorEnumeration;

/**
 * @author Roger Rene Kommer (r.kommer@micromata.de)
 * 
 */
public class CombinedClassLoader extends URLClassLoader
{
  private List<ClassLoader> parents = new ArrayList<ClassLoader>();

  public CombinedClassLoader(ClassLoader... parentsClassLoaders)
  {
    super(new URL[0]);
    for (ClassLoader cl : parentsClassLoaders) {
      addParent(cl);
    }
  }

  public CombinedClassLoader(List<ClassLoader> parentsClassLoaders)
  {
    super(new URL[0]);
    for (ClassLoader cl : parentsClassLoaders) {
      addParent(cl);
    }
  }

  public boolean containsClassLoader(ClassLoader other)
  {
    for (ClassLoader cl : parents)
      if (cl == other)
        return true;
    return false;
  }

  /**
   * Fuegt den ClassLoader hinzu, wenn er nicht bereits vorhanden ist
   * 
   * @param cl Der neue ClassLoader
   */
  public void addParent(ClassLoader cl)
  {
    if (cl != null && containsClassLoader(cl) == false) {
      parents.add(cl);

    }
  }

  @Override
  protected Class< ? > findClass(String name) throws ClassNotFoundException
  {
    for (ClassLoader cl : parents) {
      try {
        Class< ? > ret = cl.loadClass(name);
        if (ret != null) {
          return ret;
        }
      } catch (ClassNotFoundException cnf) {
        // ignore
      }
    }
    // the parent will do it.
    return super.findClass(name);
  }

  /**
   * Returns the search path of URLs for loading classes and resources. This includes the original list of URLs specified to the
   * constructor, along with any URLs subsequently appended by the addURL() method.
   */
  @Override
  public URL[] getURLs()
  {
    return super.getURLs();

  }

  @Override
  public URL findResource(String name)
  {
    for (ClassLoader cl : parents) {
      URL resource = cl.getResource(name);
      if (resource != null)
        return resource;
    }
    return null;
    // return super.findResource(name);
  }

  @Override
  public URL getResource(String name)
  {
    for (ClassLoader cl : parents) {
      URL resource = cl.getResource(name);
      if (resource != null)
        return resource;
    }
    // return super.getResource(name);
    return null;
  }

  @Override
  public InputStream getResourceAsStream(String name)
  {
    for (ClassLoader cl : parents) {
      InputStream resource = cl.getResourceAsStream(name);
      if (resource != null)
        return resource;
    }
    return null;
  }

  @Override
  public Enumeration<URL> findResources(String name) throws IOException
  {
    List<URL> res = new ArrayList<URL>();
    for (ClassLoader cl : parents) {
      Enumeration<URL> resource = cl.getResources(name);
      CollectionUtils.addAll(res, resource);
    }
    return new IteratorEnumeration<URL>(res.iterator());
  }

  @Override
  public Enumeration<URL> getResources(String name) throws IOException
  {
    return findResources(name);

  }

  @Override
  protected synchronized Class< ? > loadClass(String name, boolean resolve) throws ClassNotFoundException
  {
    return super.loadClass(name, resolve);
  }

  @Override
  public Class< ? > loadClass(String name) throws ClassNotFoundException
  {
    for (ClassLoader cl : parents) {
      try {
        Class< ? > cls = cl.loadClass(name);
        return cls;
      } catch (ClassNotFoundException ign) {
      } catch (NoClassDefFoundError ign2) {

      }

    }
    throw new ClassNotFoundException(name);
  }

  @Override
  protected String findLibrary(String libname)
  {
    return super.findLibrary(libname);
  }

  public List<ClassLoader> getParents()
  {
    return parents;
  }

  public void setParents(List<ClassLoader> parents)
  {
    this.parents = parents;
  }
}
