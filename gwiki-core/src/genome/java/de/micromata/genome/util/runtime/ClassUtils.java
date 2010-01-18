package de.micromata.genome.util.runtime;

import java.lang.reflect.Method;

public class ClassUtils
{
  /**
   * Find first method with given name
   * 
   * @param clazz
   * @param method
   * @return null if not found
   */
  public static Method findFirstMethod(Class< ? > clazz, String method)
  {
    for (Method m : clazz.getDeclaredMethods()) {
      if (m.getName().equals(method) == true)
        return m;
    }
    if (clazz.getSuperclass() != null)
      return findFirstMethod(clazz.getSuperclass(), method);
    return null;
  }

}
