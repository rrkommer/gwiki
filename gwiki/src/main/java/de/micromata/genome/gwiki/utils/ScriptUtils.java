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

package de.micromata.genome.gwiki.utils;

import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.codehaus.groovy.runtime.InvokerHelper;

import groovy.lang.Binding;
import groovy.lang.GroovyClassLoader;
import groovy.lang.GroovyShell;
import groovy.lang.MetaClass;
import groovy.lang.MetaMethod;

/**
 * static Utils functions to deal with groovy.
 * 
 * @author Roger Rene Kommer (r.kommer@micromata.de)
 * 
 */
public class ScriptUtils
{
  public static Object executeScriptCode(String code, Map<String, Object> vars)
  {
    if (StringUtils.isBlank(code) == true) {
      return null;
    }
    GroovyClassLoader loader = new GroovyClassLoader(Thread.currentThread().getContextClassLoader());
    Binding binding = new Binding(vars);
    GroovyShell shell = new GroovyShell(loader, binding);
    return shell.evaluate(code);
  }

  public static Object invokeScriptFunktion(String code, String method, Object... args)
  {
    return invokeScriptFunktion(getScriptObject(code), method, args);
  }

  public static Object getScriptObject(String code)
  {
    GroovyClassLoader loader = new GroovyClassLoader(Thread.currentThread().getContextClassLoader());
    try {
      Class<?> cls = loader.parseClass(code);
      Object obj = cls.newInstance();
      return obj;
    } catch (Throwable ex) {
      throw new RuntimeException("Cannot execute script Method: " + code + "; " + ex.getMessage(), ex);
    }
  }

  public static Object invokeScriptFunktion(Object obj, String method, Object... args)
  {
    if (obj == null) {
      return null;
    }
    try {
      return InvokerHelper.invokeMethod(obj, method, args);
    } catch (Throwable ex) {
      throw new RuntimeException("Cannot execute script Method: " + ex.getMessage(), ex);
    }
  }

  public static boolean hasMethod(Object obj, String method, Object... args)
  {
    if (obj == null) {
      return false;
    }
    MetaClass mc = InvokerHelper.getMetaClass(obj);
    MetaMethod mm = mc.getMetaMethod(method, args);
    return mm != null;
  }
}
