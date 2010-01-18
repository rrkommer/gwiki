/////////////////////////////////////////////////////////////////////////////
//
// Project   DHL-ParcelOnlinePostage
//
// Author    roger@micromata.de
// Created   18.07.2009
// Copyright Micromata 18.07.2009
//
/////////////////////////////////////////////////////////////////////////////
package de.micromata.genome.util.matcher.cls;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import org.apache.commons.lang.StringUtils;

import de.micromata.genome.util.matcher.MatcherBase;

/**
 * Class Matcher looking for Methods
 * 
 * @author roger@micromata.de
 * 
 */
public class ContainsMethod extends MatcherBase<Class< ? >>
{
  /**
   * 
   */
  private static final long serialVersionUID = -1710941778093509319L;

  /**
   * May be null if return type is not relevant
   */
  private Class< ? > returnType;

  /**
   * May be null if return type is not relevant
   */
  private Class< ? >[] params;

  /**
   * May be null if return type is not relevant
   */
  private String name;

  /**
   * If true only look at declared class
   */
  private boolean declared = false;

  /**
   * true if static method, otherwise only dynamic methods
   */
  private boolean staticMethod = false;

  private boolean publicMethod = true;

  public ContainsMethod(Class< ? > returnType, Class< ? >[] params, String name, boolean declared, boolean staticMethod,
      boolean publicMethod)
  {
    super();
    this.returnType = returnType;
    this.params = params;
    this.name = name;
    this.declared = declared;
    this.staticMethod = staticMethod;
    this.publicMethod = publicMethod;
  }

  public boolean match(Class< ? > cls)
  {
    if (matchThisClass(cls) == true)
      return true;
    if (declared == false || cls.getSuperclass() == null)
      return false;
    return match(cls.getSuperclass());
  }

  public boolean matchThisClass(Class< ? > cls)
  {
    for (Method m : cls.getMethods()) {
      int mods = m.getModifiers();
      if (staticMethod == true && (mods & Modifier.STATIC) != Modifier.STATIC)
        continue;
      if (staticMethod == false && (mods & Modifier.STATIC) == Modifier.STATIC)
        continue;
      if (publicMethod == true && (mods & Modifier.PUBLIC) != Modifier.PUBLIC)
        continue;

      if (name != null) {
        if (StringUtils.equals(m.getName(), name) == false)
          continue;
      }
      if (returnType != null) {
        if (m.getReturnType() != returnType)
          continue;
      }
      if (params != null) {
        Class< ? >[] pt = m.getParameterTypes();
        if (pt.length != params.length)
          continue;
        for (int i = 0; i < pt.length; ++i) {
          if (pt[i] != params[i])
            continue;
        }
      }
      return true;
    }
    return false;
  }

  public Class< ? > getReturnType()
  {
    return returnType;
  }

  public void setReturnType(Class< ? > returnType)
  {
    this.returnType = returnType;
  }

  public Class< ? >[] getParams()
  {
    return params;
  }

  public void setParams(Class< ? >[] params)
  {
    this.params = params;
  }

  public String getName()
  {
    return name;
  }

  public void setName(String name)
  {
    this.name = name;
  }

  public boolean isDeclared()
  {
    return declared;
  }

  public void setDeclared(boolean declared)
  {
    this.declared = declared;
  }

  public boolean isStaticMethod()
  {
    return staticMethod;
  }

  public void setStaticMethod(boolean staticMethod)
  {
    this.staticMethod = staticMethod;
  }
}
