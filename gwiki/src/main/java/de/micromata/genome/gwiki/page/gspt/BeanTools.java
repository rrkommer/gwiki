/////////////////////////////////////////////////////////////////////////////
//
// Project   DHL-ParcelOnlinePostage
//
// Author    roger@micromata.de
// Created   18.11.2006
// Copyright Micromata 18.11.2006
//$Header: $
/////////////////////////////////////////////////////////////////////////////
package de.micromata.genome.gwiki.page.gspt;

import org.apache.commons.lang.StringUtils;

/**
 * 
 * @author Roger Rene Kommer (r.kommer@micromata.de)
 * 
 *  TODO move this.
 */
public class BeanTools
{
  public static String propertyToGetterMethod(String prop)
  {
    if (StringUtils.isEmpty(prop) == true)
      return "";
    return "get" + prop.substring(0, 1).toUpperCase() + prop.substring(1) + "()";
  }
}
