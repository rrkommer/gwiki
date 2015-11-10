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

package de.micromata.genome.util.types;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

/**
 * 
 * This class is extremely useful for loading resources and classes in a fault tolerant manner that works across different applications
 * servers.
 * 
 * It has come out of many months of frustrating use of multiple application servers at Atlassian, please don't change things unless you're
 * sure they're not going to break in one server or another!
 */
public class ClassLoaderUtils
{

  /**
   * Load a given resource.
   * 
   * This method will try to load the resource using the following methods (in order):
   * <ul>
   * <li>From Thread.currentThread().getContextClassLoader()
   * <li>From ClassLoaderUtils.class.getClassLoader()
   * <li>callingClass.getClassLoader()
   * </ul>
   * 
   * @param resourceName The name of the resource to load
   * @param callingClass The Class object of the calling object
   */
  public static URL getResource(String resourceName, Class< ? > callingClass)
  {
    URL url = Thread.currentThread().getContextClassLoader().getResource(resourceName);

    if (url == null) {
      url = ClassLoaderUtils.class.getClassLoader().getResource(resourceName);
    }

    if (url == null) {
      ClassLoader cl = callingClass.getClassLoader();

      if (cl != null) {
        url = cl.getResource(resourceName);
      }
    }

    if ((url == null) && (resourceName != null) && (resourceName.charAt(0) != '/')) {
      return getResource('/' + resourceName, callingClass);
    }

    return url;
  }

  /**
   * This is a convenience method to load a resource as a stream.
   * 
   * The algorithm used to find the resource is given in getResource()
   * 
   * @param resourceName The name of the resource to load
   * @param callingClass The Class object of the calling object
   */
  public static InputStream getResourceAsStream(String resourceName, Class< ? > callingClass)
  {
    URL url = getResource(resourceName, callingClass);

    try {
      return (url != null) ? url.openStream() : null;
    } catch (IOException e) {
      return null;
    }
  }

  /**
   * Load a class with a given name.
   * 
   * It will try to load the class in the following order:
   * <ul>
   * <li>From Thread.currentThread().getContextClassLoader()
   * <li>Using the basic Class.forName()
   * <li>From ClassLoaderUtils.class.getClassLoader()
   * <li>From the callingClass.getClassLoader()
   * </ul>
   * 
   * @param className The name of the class to load
   * @param callingClass The Class object of the calling object
   * @throws ClassNotFoundException If the class cannot be found anywhere.
   */
  public static Class< ? > loadClass(String className, Class< ? > callingClass) throws ClassNotFoundException
  {
    try {
      return Thread.currentThread().getContextClassLoader().loadClass(className);
    } catch (ClassNotFoundException e) {
      try {
        return Class.forName(className);
      } catch (ClassNotFoundException ex) {
        try {
          return ClassLoaderUtils.class.getClassLoader().loadClass(className);
        } catch (ClassNotFoundException exc) {
          return callingClass.getClassLoader().loadClass(className);
        }
      }
    }
  }
}
