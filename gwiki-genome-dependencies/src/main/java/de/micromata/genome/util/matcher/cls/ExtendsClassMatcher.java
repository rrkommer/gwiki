/////////////////////////////////////////////////////////////////////////////
//
// Project   DHL-ParcelOnlinePostage
//
// Author    roger@micromata.de
// Created   04.07.2009
// Copyright Micromata 04.07.2009
//
/////////////////////////////////////////////////////////////////////////////
package de.micromata.genome.util.matcher.cls;

import de.micromata.genome.util.matcher.MatcherBase;

/**
 * Extends or implements given class.
 * 
 * @author roger@micromata.de
 * 
 */
public class ExtendsClassMatcher extends MatcherBase<Class< ? >>
{

  private static final long serialVersionUID = 2781760884364697384L;

  private Class< ? > superClass;

  public ExtendsClassMatcher(Class< ? > superClass)
  {
    this.superClass = superClass;
  }

  public boolean match(Class< ? > object)
  {
    return superClass.isAssignableFrom(object);
  }

  public String toString()
  {
    return "<EXPR>.extendsClass(" + superClass.getCanonicalName() + ")";

  }

  public Class< ? > getSuperClass()
  {
    return superClass;
  }

}
