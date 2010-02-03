/////////////////////////////////////////////////////////////////////////////
//
// Project   DHL-ParcelOnlinePostage
//
// Author    roger@micromata.de
// Created   04.11.2009
// Copyright Micromata 04.11.2009
//
/////////////////////////////////////////////////////////////////////////////
package de.micromata.genome.gwiki.utils;

import groovy.lang.Binding;
import groovy.lang.GroovyClassLoader;
import groovy.lang.GroovyShell;
import groovy.lang.MetaClass;
import groovy.lang.MetaMethod;

import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.codehaus.groovy.runtime.InvokerHelper;

public class ScriptUtils
{
  public static void executeScriptCode(String code, Map<String, Object> vars)
  {
    if (StringUtils.isBlank(code) == true) {
      return;
    }
    GroovyClassLoader loader = new GroovyClassLoader(Thread.currentThread().getContextClassLoader());
    Binding binding = new Binding(vars);
    GroovyShell shell = new GroovyShell(loader, binding);
    shell.evaluate(code);
  }

  public static Object invokeScriptFunktion(String code, String method, Object... args)
  {
    return invokeScriptFunktion(getScriptObject(code), method, args);
  }

  public static Object getScriptObject(String code)
  {
    GroovyClassLoader loader = new GroovyClassLoader(Thread.currentThread().getContextClassLoader());
    try {
      Class< ? > cls = loader.parseClass(code);
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
