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

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;

import org.apache.commons.beanutils.BeanUtilsBean;
import org.apache.commons.beanutils.ConvertUtilsBean;
import org.apache.commons.lang3.StringUtils;

/**
 * Some simplified interface for classes.
 * 
 * @author Roger Rene Kommer (r.kommer@micromata.de)
 * 
 */
public class ClassUtils
{
  public static Class< ? > classForName(String className)
  {
    ClassLoader cl = Thread.currentThread().getContextClassLoader();
    try {
      return Class.forName(className, true, cl);
    } catch (Throwable ex) {
      try {
        Class.forName(className, true, cl);
      } catch (Exception ex2) {

      }
      throw new RuntimeException("Failed to load class: " + className + "; " + ex.getMessage(), ex);
    }
  }

  public static <T> Class<T> classForName(String className, Class<T> ifacecls)
  {
    return (Class<T>) classForName(className);
  }

  @SuppressWarnings("unchecked")
  public static <T> T createDefaultInstance(String className, Class< ? extends T> classExpected)
  {
    try {
      Class< ? extends T> ret = (Class< ? extends T>) Class.forName(className, true, Thread.currentThread().getContextClassLoader());
      return ret.newInstance();
    } catch (Throwable ex) {
      throw new RuntimeException("Cannot create class instance: " + ex.toString(), ex);
    }
  }

  public static Object createDefaultInstance(String className)
  {
    return createDefaultInstance(classForName(className));
  }

  public static <T> T createDefaultInstance(Class< ? extends T> cls)
  {
    try {
      return cls.newInstance();
    } catch (Throwable ex) {
      throw new RuntimeException("Cannot Instantiate class instance: " + ex.toString(), ex);
    }
  }

  /**
   * 
   * @param <T>
   * @param cls
   * @param args must have the exact type
   * @return
   */
  public static <T> T createInstance(Class< ? extends T> cls, Class< ? >[] argTypes, Object... args)
  {
    try {
      Constructor< ? extends T> construktur = cls.getConstructor(argTypes);
      return construktur.newInstance(args);
    } catch (Throwable ex) {
      throw new RuntimeException("Cannot instantiate constructor: " + ex.getMessage(), ex);
    }
  }

  @SuppressWarnings("unchecked")
  public static <T> T invokeDefaultStaticMethod(Class<T> expectedClass, String className, String methodName)
  {
    Class< ? > cls = classForName(className);
    try {
      Method m = cls.getMethod(methodName, new Class[] {});
      T t = (T) m.invoke(null, new Object[] {});
      if (t == null) {
        return t;
      }
      if (expectedClass.isAssignableFrom(t.getClass()) == false) {
        throw new RuntimeException("method: "
            + methodName
            + " from class: "
            + className
            + " does not return correct class type: "
            + expectedClass
            + " but: "
            + t.getClass());
      }
      return t;
    } catch (NoSuchMethodException ex) {
      throw new RuntimeException("Cannot find method: " + methodName + " in class: " + className);
    } catch (IllegalArgumentException ex) {
      throw new RuntimeException(ex);
    } catch (IllegalAccessException ex) {
      throw new RuntimeException(ex);
    } catch (InvocationTargetException ex) {
      Throwable nex = ex.getCause();
      if (nex instanceof RuntimeException) {
        throw (RuntimeException) nex;
      }
      throw new RuntimeException(ex);
    }
  }

  public static String getSetter(String propName)
  {
    if (StringUtils.isEmpty(propName) == true) {
      return null;
    }
    if (propName.length() == 1) {
      return "set" + propName.toUpperCase();
    }
    return "set" + propName.substring(0, 1).toUpperCase() + propName.substring(1);
  }

  public static String getGetter(String propName)
  {
    if (StringUtils.isEmpty(propName) == true) {
      return null;
    }
    if (propName.length() == 1) {
      return "get" + propName.toUpperCase();
    }
    return "get" + propName.substring(0, 1).toUpperCase() + propName.substring(1);
  }

  public static boolean hasMethod(Class< ? > cls, String method)
  {
    if (StringUtils.isEmpty(method) == true) {
      return false;
    }
    for (Method m : cls.getMethods()) {
      if (method.equals(m.getName()) == true) {
        return true;
      }
    }
    return false;
  }

  public static Object convert(Object source, Class< ? > cls)
  {
    ConvertUtilsBean converter = new ConvertUtilsBean();
    if (source instanceof String[]) {
      return converter.convert((String[]) source, cls);
    } else if (source instanceof String) {
      return converter.convert((String) source, cls);
    }
    return source;
  }

  public static void populateBeanWithPuplicMembers(Object bean, Map<String, Object> reqMap)
  {
    Class< ? > cls = bean.getClass();
    for (Map.Entry<String, Object> me : reqMap.entrySet()) {
      try {
        String s = getSetter(me.getKey());
        if (hasMethod(cls, s) == true) {
          BeanUtilsBean.getInstance().setProperty(bean, me.getKey(), me.getValue());
        } else {
          Field f;
          try {
            f = cls.getField(me.getKey());
            if (f != null) {
              f.set(bean, convert(me.getValue(), f.getType()));
            }
          } catch (NoSuchFieldException ex) {
            continue;
          }
        }
      } catch (Exception ex) {
        throw new RuntimeException("Failure to set propert: "
            + me.getKey()
            + " in class: "
            + cls.getName()
            + "; with value: "
            + me.getValue()
            + ";"
            + ex.getMessage(), ex);
      }
    }
  }
}
