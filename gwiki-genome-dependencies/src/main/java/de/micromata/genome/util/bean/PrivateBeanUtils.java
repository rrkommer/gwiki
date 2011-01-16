/////////////////////////////////////////////////////////////////////////////
//
// Project   Micromata Genome Core
//
// Author    roger@micromata.de
// Created   27.02.2008
// Copyright Micromata 27.02.2008
//
/////////////////////////////////////////////////////////////////////////////
package de.micromata.genome.util.bean;

import java.lang.annotation.Annotation;
import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;

import de.micromata.genome.util.matcher.EveryMatcher;
import de.micromata.genome.util.matcher.Matcher;
import de.micromata.genome.util.types.Pair;

/**
 * Utility to access private/protected fields without public getter/setter
 * 
 * @author roger@micromata.de
 * 
 */
public class PrivateBeanUtils
{
  public static class AccessibleScope
  {
    private AccessibleObject object;

    private boolean wasAccessable = false;

    public AccessibleScope(AccessibleObject object)
    {
      this.object = object;
      wasAccessable = object.isAccessible();
      if (wasAccessable == false) {
        object.setAccessible(true);
      }
    }

    public void restore()
    {
      if (wasAccessable == false) {
        wasAccessable = true;
        object.setAccessible(false);
      }
    }
  }

  /**
   * Find a field of given bean
   * 
   * @param bean
   * @param fieldName
   * @return null if not found
   */
  public static Field findField(Object bean, String fieldName)
  {
    return findField(bean.getClass(), fieldName);
  }

  /**
   * For a given getVariable return variable
   * 
   * @param getter
   * @return null if not found
   */
  public static String getFieldNameFromGetter(String getter)
  {
    if (getter == null)
      return null;
    if (getter.startsWith("get") == true && getter.length() >= 4)
      return getter.substring(3, 4).toLowerCase() + getter.substring(4);
    if (getter.startsWith("is") == true && getter.length() >= 3)
      return getter.substring(2, 3).toLowerCase() + getter.substring(3);
    return null;
  }

  public static Field findField(Class< ? > cls, String fieldName)
  {
    Field f = null;
    try {
      f = cls.getDeclaredField(fieldName);
    } catch (SecurityException ex) {
    } catch (NoSuchFieldException ex) {
    }
    if (f != null)
      return f;
    if (cls == Object.class || cls.getSuperclass() == null)
      return null;
    return findField(cls.getSuperclass(), fieldName);
  }

  public static Object readField(Object bean, String fieldName)
  {
    Field f = findField(bean, fieldName);
    if (f == null) {
      throw new RuntimeException("No bean field found: " + bean.getClass().getName() + "." + fieldName);
    }
    return readField(bean, f);
  }

  public static Object readStaticField(Class< ? > beanClass, String fieldName)
  {
    Field f = findField(beanClass, fieldName);
    if (f == null) {
      throw new RuntimeException("No bean field found: " + beanClass.getName() + "." + fieldName);
    }
    return readField(null, f);
  }

  public static void writeStaticFiled(Class< ? > beanClass, String fieldName, Object value)
  {
    Field f = findField(beanClass, fieldName);
    if (f == null) {
      throw new RuntimeException("No bean field found: " + beanClass.getName() + "." + fieldName);
    }
    writeField(null, f, value);
  }

  /**
   * Read a bean field
   * 
   * @param bean
   * @param field
   * @return
   * @throws RuntimeException falls the bean filed can not be accessed
   */
  public static synchronized Object readField(Object bean, Field field)
  {
    AccessibleScope ascope = new AccessibleScope(field);
    try {
      Object o = field.get(bean);
      return o;
    } catch (Exception ex) {
      throw new RuntimeException("Failure accessing bean field: " + bean.getClass().getName() + "." + field + "; " + ex.getMessage(), ex);
    } finally {
      ascope.restore();
    }
  }

  /**
   * 
   * Write the beanfield
   * 
   * @param bean
   * @param fieldName
   * @param value
   * @throws RuntimeException is the bean field can not be found
   */
  public static void writeField(Object bean, String fieldName, Object value)
  {
    Field f = findField(bean, fieldName);
    if (f == null)
      throw new RuntimeException("No bean field found: " + bean.getClass().getName() + "." + fieldName);
    writeField(bean, f, value);
  }

  /**
   * Wirte a bean field
   * 
   * @param bean
   * @param field
   * @param value
   * @throws RuntimeException if the bean field can not be accessed
   */
  public static synchronized void writeField(Object bean, Field field, Object value)
  {
    boolean wasAccessable = field.isAccessible();
    if (wasAccessable == false)
      field.setAccessible(true);
    try {
      field.set(bean, value);
    } catch (Exception ex) {
      throw new RuntimeException("Failure accessing bean field: " + bean.getClass().getName() + "." + field + "; " + ex.getMessage(), ex);
    } finally {
      if (wasAccessable == false)
        field.setAccessible(false);
    }
  }

  public static void findFieldsWithAnnotation(Class< ? > clazz, Class< ? extends Annotation> annotation,
      List<Pair<Field, ? extends Annotation>> res)
  {
    for (Field f : clazz.getDeclaredFields()) {
      Annotation an = f.getAnnotation(annotation);
      if (an == null)
        continue;
      res.add(new Pair<Field, Annotation>(f, an));
    }
    if (clazz == Object.class || clazz.getSuperclass() == null)
      return;
    findFieldsWithAnnotation(clazz.getSuperclass(), annotation, res);
  }

  /**
   * Find all fields of bean which implementing iface
   * 
   * @param bean
   * @param iface
   * @param result
   * @param depth recursion depth in search
   */
  public static <T> void findNestedImplementing(Object bean, Class<T> iface, List<T> result, int depth, boolean includeStatics)
  {
    if (bean == null)
      return;
    Class< ? > clazz = bean.getClass();
    findNestedImplementing(bean, clazz, iface, result, depth, includeStatics);
  }

  @SuppressWarnings("unchecked")
  public static <T> void findNestedImplementing(Object bean, Class< ? > clazz, Class<T> iface, List<T> result, int depth,
      boolean includeStatics)
  {
    if (iface.isAssignableFrom(clazz) == true)
      result.add((T) bean);
    if (depth == 0)
      return;

    for (Field f : clazz.getDeclaredFields()) {
      if (includeStatics == false && (f.getModifiers() & Modifier.STATIC) == Modifier.STATIC)
        continue;
      Object o = readField(bean, f);
      findNestedImplementing(o, iface, result, depth - 1, includeStatics);
    }
    if (clazz == Object.class || clazz.getSuperclass() == null)
      return;
    findNestedImplementing(bean, clazz.getSuperclass(), iface, result, depth, includeStatics);
  }

  public static List<Pair<Field, ? extends Annotation>> findFieldsWithAnnotation(Object bean, Class< ? extends Annotation> annotation)
  {
    List<Pair<Field, ? extends Annotation>> ret = new ArrayList<Pair<Field, ? extends Annotation>>();
    findFieldsWithAnnotation(bean.getClass(), annotation, ret);
    return ret;
  }

  public static int getBeanSize(Object bean)
  {
    return getBeanSize(bean, new EveryMatcher<String>());
  }

  public static int getBeanSize(Object bean, Matcher<String> matcher)
  {
    IdentityHashMap<Object, Object> m = new IdentityHashMap<Object, Object>();
    return getBeanSize(bean, m, matcher);
  }

  public static int getBeanSize(Object bean, IdentityHashMap<Object, Object> m, Matcher<String> matcher)
  {

    if (bean == null)
      return 0;
    if (m.containsKey(bean) == true)
      return 0;
    try {
      Class< ? > clazz = bean.getClass();
      return getBeanSize(bean, clazz, m, matcher);
    } catch (NoClassDefFoundError ex) {
      return 0;
    }
  }

  public static int getBeanSize(Object bean, Class< ? > clazz, IdentityHashMap<Object, Object> m, Matcher<String> matcher)
  {
    if (m.containsKey(bean) == true)
      return 0;
    m.put(bean, null);
    return getBeanSizeIntern(bean, clazz, m, matcher);
  }

  public static int getBeanSizeIntern(Object bean, Class< ? > clazz, IdentityHashMap<Object, Object> m, Matcher<String> matcher)
  {
    if (matcher.match(clazz.getName()) == false) {
      return 0;
    }
    if (clazz.isArray() == true) {
      if (clazz == boolean[].class)
        return (((boolean[]) bean).length * 4);
      else if (clazz == char[].class)
        return (((char[]) bean).length * 2);
      else if (clazz == byte[].class)
        return (((byte[]) bean).length * 1);
      else if (clazz == short[].class)
        return (((short[]) bean).length * 2);
      else if (clazz == int[].class)
        return (((int[]) bean).length * 4);
      else if (clazz == long[].class)
        return (((long[]) bean).length * 4);
      else if (clazz == float[].class)
        return (((float[]) bean).length * 4);
      else if (clazz == double[].class)
        return (((double[]) bean).length * 8);
      else {
        int length = Array.getLength(bean);
        int ret = (length * 4);
        for (int i = 0; i < length; ++i) {
          ret += getBeanSize(Array.get(bean, i), m, matcher);
        }
        return ret;
      }
    }
    int ret = 0;
    try {
      for (Field f : clazz.getDeclaredFields()) {
        int mod = f.getModifiers();
        if (Modifier.isStatic(mod) == true)
          continue;
        if (f.getType() == Boolean.TYPE)
          ret += 4;
        else if (f.getType() == Character.TYPE)
          ret += 2;
        else if (f.getType() == Byte.TYPE)
          ret += 1;
        else if (f.getType() == Short.TYPE)
          ret += 2;
        else if (f.getType() == Integer.TYPE)
          ret += 4;
        else if (f.getType() == Long.TYPE)
          ret += 8;
        else if (f.getType() == Float.TYPE)
          ret += 4;
        else if (f.getType() == Double.TYPE)
          ret += 8;
        else {

          ret += 4;
          Object o = null;
          try {
            o = readField(bean, f);
            if (o == null) {
              continue;
            }
          } catch (NoClassDefFoundError ex) {
            // nothing
            continue;
          }
          ret += getBeanSize(o, o.getClass(), m, matcher);
        }
      }
    } catch (NoClassDefFoundError ex) {
      // ignore here.
    }
    if (clazz == Object.class || clazz.getSuperclass() == null)
      return ret;
    ret += getBeanSizeIntern(bean, clazz.getSuperclass(), m, matcher);
    return ret;
  }

  /**
   * @param bean
   * @param clazz
   * @param method
   * @param argCount
   * @return null if method cannot be found
   */
  public static Method findMethod(Object bean, Class< ? > clazz, String method, Object... args)
  {
    nextMethod: for (Method m : clazz.getDeclaredMethods()) {
      if (m.getName().equals(method) == false)
        continue;
      Class< ? >[] argClazzes = m.getParameterTypes();
      if (argClazzes.length != args.length)
        continue;
      for (int i = 0; i < args.length; ++i) {
        Object a = args[i];
        Class< ? > ac = argClazzes[i];
        if (a != null && ac.isAssignableFrom(a.getClass()) == false)
          continue nextMethod;
      }
      return m;
    }
    if (clazz != Object.class && clazz.getSuperclass() != null)
      return findMethod(bean, clazz.getSuperclass(), method, args);
    return null;
  }

  /**
   * Invokes a method
   * 
   * @param bean
   * @param method
   * @param args
   * @return return value of method call
   * @throws RuntimeException if bean is null, method cannot be found
   */
  public static Object invokeMethod(Object bean, String method, Object... args)
  {
    Method m = findMethod(bean, bean.getClass(), method, args);
    if (m == null) {
      throw new RuntimeException("Canot find method to call: " + bean.getClass().getName() + "." + method);
    }
    AccessibleScope ascope = new AccessibleScope(m);
    try {
      return m.invoke(bean, args);
    } catch (Exception ex) {
      throw new RuntimeException("Failure calling method: " + bean.getClass().getName() + "." + method + ": " + ex.getMessage(), ex);
    } finally {
      ascope.restore();
    }
  }

  /**
   * Used for copy-Constructors
   * 
   * @param bean
   * @param target
   * @param type
   */
  public static <T> void copyInstanceProperties(T source, T target)
  {
    copyInstanceProperties(source.getClass(), source, target);
  }

  public static <T> void copyInstanceProperties(Class< ? > targetClass, T source, T target)
  {
    if (targetClass == null)
      return;

    for (Field f : targetClass.getDeclaredFields()) {
      int mod = f.getModifiers();
      if (Modifier.isStatic(mod) == true)
        continue;
      Object value = readField(source, f);
      writeField(target, f, value);
    }
    copyInstanceProperties(targetClass.getSuperclass(), source, target);
  }

  /**
   * write all properties directly to fields, ignoring getter/setter
   * 
   * @param bean target bean
   * @param properties
   */
  public static void populate(Object bean, Map<String, Object> properties)
  {
    for (Map.Entry<String, Object> me : properties.entrySet()) {
      Field f = findField(bean, me.getKey());
      if (f == null) {
        continue;
      }
      writeField(bean, f, me.getValue());
    }
  }

  public static Map<String, Object> getAllNonStaticFields(Object bean)
  {
    Map<String, Object> ret = new HashMap<String, Object>();
    fetchAllNonStaticFields(ret, bean.getClass(), bean);
    return ret;
  }

  public static void fetchAllNonStaticFields(Map<String, Object> ret, Class< ? > clz, Object bean)
  {
    if (clz == null) {
      return;
    }
    for (Field f : clz.getDeclaredFields()) {
      int mod = f.getModifiers();
      if (Modifier.isStatic(mod) == true) {
        continue;
      }
      if (ret.containsKey(f.getName()) == true) {
        continue;
      }
      Object value = readField(bean, f);
      ret.put(f.getName(), value);
    }
    fetchAllNonStaticFields(ret, clz.getSuperclass(), bean);
  }
}
