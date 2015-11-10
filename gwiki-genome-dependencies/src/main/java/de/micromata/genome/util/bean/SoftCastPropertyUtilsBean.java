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

/////////////////////////////////////////////////////////////////////////////
//
// Project Genome Core
//
// Author    roger@micromata.de
// Created   24.08.2008
// Copyright Micromata 24.08.2008
//
/////////////////////////////////////////////////////////////////////////////
package de.micromata.genome.util.bean;

import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;

import org.apache.commons.beanutils.BeanUtilsBean;
import org.apache.commons.beanutils.ContextClassLoaderLocal;
import org.apache.commons.beanutils.PropertyUtilsBean;

/**
 * Combination with PropertyUtilsBean and BeanUtilsBean
 * 
 * If a value is not compatible with PropertyUtilsBean, tries to use BeanUtilsBean
 * 
 * 
 * @author roger@micromata.de
 * 
 */
public class SoftCastPropertyUtilsBean extends PropertyUtilsBean
{
  private static final ContextClassLoaderLocal beansByClassLoader = new ContextClassLoaderLocal() {
    // Creates the default instance used when the context classloader is unavailable
    protected Object initialValue()
    {
      return new SoftCastPropertyUtilsBean();
    }
  };

  private BeanUtilsBean beanUtilsBeans = BeanUtilsBean.getInstance();

  public synchronized static SoftCastPropertyUtilsBean getInstance()
  {
    return (SoftCastPropertyUtilsBean) beansByClassLoader.get();
  }

  // public Object numberCast(Number value, Class< ? > target)
  // {
  // if (target == Integer.class)
  // return value.intValue();
  // if (target == Long.class)
  // return value.longValue();
  // if (target == Short.class)
  // return value.shortValue();
  // if (target == Float.class)
  // return value.floatValue();
  // if (target == Double.class)
  // return value.doubleValue();
  // if (target == Byte.class)
  // return (Byte) value.byteValue();
  // if (target == BigDecimal.class)
  // return new BigDecimal(value.doubleValue());
  // return value;
  // }

  public Class< ? > getWrappedClass(Class< ? > target)
  {
    if (target.isPrimitive() == false)
      return target;
    if (target == Integer.TYPE)
      return Integer.class;
    if (target == Long.TYPE)
      return Long.class;
    if (target == Byte.TYPE)
      return Byte.class;
    if (target == Short.TYPE)
      return Short.class;
    if (target == Float.TYPE)
      return Short.class;
    if (target == Double.TYPE)
      return Double.class;
    if (target == Character.TYPE)
      return Character.class;
    if (target == Boolean.TYPE)
      return Boolean.class;
    throw new RuntimeException("Unmapped basic type: " + target);
  }

  // public Object rightCast(Object value, Class< ? > target)
  // {
  // target = getWrappedClass(target);
  // if (target == value.getClass())
  // return value;
  //
  // if (Number.class.isAssignableFrom(target) == true) {
  // if (value instanceof Number) {
  // return numberCast((Number) value, target);
  // }
  // }
  // if (String.class == target) {
  // return ObjectUtils.toString(value);
  // }
  // if (value instanceof String) {
  //
  // }
  // return value;
  // }

  public Class< ? > getPropertyClass(Object bean, String name) throws IllegalAccessException, InvocationTargetException,
      NoSuchMethodException
  {
    PropertyDescriptor pdesc = super.getPropertyDescriptor(bean, name);
    if (pdesc == null)
      return null;
    return pdesc.getPropertyType();
  }

  // public Object rightCast(Object bean, String name, Object value) throws IllegalAccessException, InvocationTargetException,
  // NoSuchMethodException
  // {
  // if (value == null)
  // return value;
  // PropertyDescriptor pdesc = super.getPropertyDescriptor(bean, name);
  // if (pdesc == null)
  // return value;
  // Class< ? > targetClazz = pdesc.getPropertyType();
  // if (targetClazz == value.getClass())
  // return value;
  // return rightCast(value, targetClazz);
  // }

  // public Object rightCast(Object bean, Method method, Object value)
  // {
  //
  // return value;
  // }

  // /** This just catches and wraps IllegalArgumentException. */
  // protected Object invokeMethod(Method method, Object bean, Object[] values) throws IllegalAccessException, InvocationTargetException
  // {
  // try {
  //
  // return method.invoke(bean, values);
  //
  // } catch (IllegalArgumentException e) {
  // if (values.length != 1)
  // throw e;
  // Object nv = rightCast(values[0], method.getParameterTypes()[0]);
  // if (nv == values[0])
  // throw e;
  // return invokeMethod(method, bean, new Object[] { nv});
  // }
  // }
  public final boolean isPropAssigneable(Class< ? > valueClass, Class< ? > propClass)
  {
    if (propClass == null)
      return false;
    if (valueClass == propClass)
      return true;
    if (propClass.isAssignableFrom(valueClass) == true)
      return true;
    valueClass = getWrappedClass(valueClass);
    propClass = getWrappedClass(propClass);
    if (valueClass == propClass)
      return true;
    if (propClass.isAssignableFrom(valueClass) == true)
      return true;
    return false;
  }

  @Override
  public void setSimpleProperty(Object bean, String name, Object value) throws IllegalAccessException, InvocationTargetException,
      NoSuchMethodException
  {
    Class< ? > valueClass = (value == null ? Object.class : value.getClass());
    Class< ? > propClass = getPropertyClass(bean, name);
    if (isPropAssigneable(valueClass, propClass) == true) {
      super.setSimpleProperty(bean, name, value);
    } else {
      beanUtilsBeans.setProperty(bean, name, value);
    }
  }
}
