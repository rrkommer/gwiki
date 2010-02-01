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

import de.micromata.genome.util.matcher.BooleanListRulesFactory;

/**
 * Matcher which receives a class and matches
 * 
 * @author roger@micromata.de
 * 
 */
public class ClassMatcherBooleanListFactory extends BooleanListRulesFactory<Class< ? >>
{

  public ClassMatcherBooleanListFactory()
  {
    super(new ClassMatcherFactory());
  }

}
