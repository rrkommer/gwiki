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
// Created   24.01.2009
// Copyright Micromata 24.01.2009
//
/////////////////////////////////////////////////////////////////////////////
package de.micromata.genome.util.bean;

import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.lang.ArrayUtils;

/**
 * AttrSetter/Getter implementation based on BeanUtils
 * 
 * The set-Method must called always with the same BEAN type.
 * 
 * @author roger@micromata.de
 * 
 */
public class PropertyAttrSetterGetter<BEAN, VAL> implements NamedAttrSetter<BEAN, VAL>, NamedAttrGetter<BEAN, VAL>
{
  private String propertyName;

  private transient PropertyDescriptor propDescriptor;

  public PropertyAttrSetterGetter(String propertyName)
  {
    this.propertyName = propertyName;
  }

  public PropertyAttrSetterGetter(PropertyDescriptor propDescriptor)
  {
    this.propertyName = propDescriptor.getName();
    this.propDescriptor = propDescriptor;
  }

  private void init(BEAN bean)
  {
    if (propDescriptor != null)
      return;

    try {
      propDescriptor = PropertyUtils.getPropertyDescriptor(bean, propertyName);
    } catch (Exception ex) {
      throw new RuntimeException("Cannot retrieve property " + propertyName + " from bean class " + bean.getClass().getName());
    }
  }

  public void set(BEAN bean, VAL value)
  {
    init(bean);
    try {
      propDescriptor.getWriteMethod().invoke(bean, new Object[] { value});
    } catch (RuntimeException ex) {
      throw ex;
    } catch (InvocationTargetException ex) {
      Throwable nex = ex.getCause();
      if (nex instanceof RuntimeException) {
        throw (RuntimeException) nex;
      }
      throw new RuntimeException(nex);
    } catch (Exception ex) {
      throw new RuntimeException(ex);
    }
  }

  @SuppressWarnings("unchecked")
  public VAL get(BEAN bean)
  {
    init(bean);
    try {
      return (VAL) propDescriptor.getReadMethod().invoke(bean, ArrayUtils.EMPTY_OBJECT_ARRAY);
    } catch (RuntimeException ex) {
      throw ex;
    } catch (InvocationTargetException ex) {
      Throwable nex = ex.getCause();
      if (nex instanceof RuntimeException) {
        throw (RuntimeException) nex;
      }
      throw new RuntimeException(nex);
    } catch (Exception ex) {
      throw new RuntimeException(ex);
    }
  }

  public String getName()
  {
    return propertyName;
  }

  public String getPropertyName()
  {
    return propertyName;
  }

  public void setPropertyName(String propertyName)
  {
    this.propertyName = propertyName;
  }

  public PropertyDescriptor getPropDescriptor()
  {
    return propDescriptor;
  }

  public void setPropDescriptor(PropertyDescriptor propDescriptor)
  {
    this.propDescriptor = propDescriptor;
  }

}
