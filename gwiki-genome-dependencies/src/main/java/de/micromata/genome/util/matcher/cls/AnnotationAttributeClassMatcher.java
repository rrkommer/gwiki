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
// Created   04.07.2009
// Copyright Micromata 04.07.2009
//
/////////////////////////////////////////////////////////////////////////////
package de.micromata.genome.util.matcher.cls;

import java.lang.annotation.Annotation;
import java.lang.reflect.Array;
import java.lang.reflect.Method;

import org.apache.commons.lang.StringUtils;

import de.micromata.genome.util.matcher.MatcherBase;

/**
 * Look if annotation has attribute.
 * 
 * @author roger@micromata.de
 * 
 */
public class AnnotationAttributeClassMatcher extends MatcherBase<Class< ? >>
{

  private static final long serialVersionUID = 7887535795705625292L;

  private Class< ? extends Annotation> annotationClass;

  private String fieldName;

  private Method getterMethod;

  private String fieldValue;

  public AnnotationAttributeClassMatcher(Class< ? extends Annotation> anotationClass, String fieldName, String fieldValue)
  {
    this.annotationClass = anotationClass;
    this.fieldName = fieldName;
    this.fieldValue = fieldValue;
    try {
      getterMethod = anotationClass.getMethod(fieldName, new Class< ? >[] {});
    } catch (Exception ex) {
      throw new RuntimeException(ex);
    }
  }

  public String toString()
  {
    return "<Expr>.matchAttr(" + annotationClass.getCanonicalName() + ", " + fieldName + " = " + fieldValue + ")";
  }

  private Object getValue(Annotation anon)
  {
    Object value;
    try {
      value = getterMethod.invoke(anon, new Object[] {});
    } catch (Exception ex) {
      return null;
    }
    return value;
  }

  protected boolean compareSimpleType(Object val)
  {
    if (val instanceof Class< ? >) {
      return StringUtils.equals(((Class< ? >) val).getCanonicalName(), fieldValue);
    }
    final String sval = val != null ? val.toString() : null;
    return StringUtils.equals(sval, fieldValue);
  }

  protected boolean containsValue(Object array)
  {
    int len = Array.getLength(array);
    for (int i = 0; i < len; ++i) {
      Object val = Array.get(array, i);
      if (compareSimpleType(val) == true)
        return true;
    }
    return false;
  }

  public boolean match(Class< ? > cls)
  {
    final Annotation anon = AnnotationClassMatcher.findAnnotation(cls, annotationClass);

    if (anon == null)
      return false;
    Object val = getValue(anon);
    if (val.getClass().isArray() == true) {
      return containsValue(val);
    }
    return compareSimpleType(val);
  }

  public Class< ? extends Annotation> getAnnotationClass()
  {
    return annotationClass;
  }

  public String getFieldName()
  {
    return fieldName;
  }

  public String getFieldValue()
  {
    return fieldValue;
  }
}
