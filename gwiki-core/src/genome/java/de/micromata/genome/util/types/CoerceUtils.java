/////////////////////////////////////////////////////////////////////////////
//
// Project   Micromata Genome Core
//
// Author    roger@micromata.de
// Created   18.01.2008
// Copyright Micromata 18.01.2008
//
/////////////////////////////////////////////////////////////////////////////
package de.micromata.genome.util.types;

import org.apache.commons.lang.ObjectUtils;

/**
 * Collection of methods to weak cast types
 * 
 * @author roger@micromata.de
 * 
 */
public class CoerceUtils
{
  public static double toDouble(Object o)
  {
    if (o instanceof Number)
      return ((Number) o).doubleValue();
    if (o instanceof String)
      return Double.parseDouble((String) o);
    throw new RuntimeException("Cannot coerce value to double: " + ObjectUtils.toString(o));
  }

}
