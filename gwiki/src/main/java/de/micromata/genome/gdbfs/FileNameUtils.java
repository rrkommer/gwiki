/////////////////////////////////////////////////////////////////////////////
//
// Project   DHL-ParcelOnlinePostage
//
// Author    roger@micromata.de
// Created   30.11.2009
// Copyright Micromata 30.11.2009
//
/////////////////////////////////////////////////////////////////////////////
package de.micromata.genome.gdbfs;

import org.apache.commons.lang.StringUtils;

/**
 * Internal utils to deal with file names.
 * 
 * @author roger@micromata.de
 * 
 */
public class FileNameUtils
{
  public static String normalize(String name)
  {
    if (StringUtils.isEmpty(name) == true) {
      return name;
    }
    name = StringUtils.replace(name, "\\", "/");
    name = StringUtils.replace(name, "//", "/");
    return name;
  }

  public static String getNamePart(String name)
  {
    if (name == null) {
      return null;
    }
    int idx = name.lastIndexOf('/');
    if (idx == -1) {
      return name;
    }
    return name.substring(idx + 1);

  }

  public static String join(String... components)
  {
    if (components.length < 2) {
      return components[0];
    }
    String ret = components[0];
    for (int i = 1; i < components.length; ++i) {
      ret = join(ret, components[i]);
    }
    return ret;
  }

  public static String getParentDir(String name)
  {
    if (name == null) {
      return null;
    }
    int idx = name.lastIndexOf('/');
    if (idx != -1) {
      return name.substring(0, idx);
    } else {
      return "";
    }
  }

  public static String join(String first, String second)
  {
    if (StringUtils.isEmpty(second) == true) {
      return first;
    }
    if (StringUtils.isEmpty(first) == true) {
      return second;
    }
    if (first.endsWith("/") == true || second.startsWith("/") == true) {
      if (first.endsWith("/") == true && second.startsWith("/") == true) {
        return first + second.substring(1);
      }
      return first + second;
    }
    return first + "/" + second;
  }
}
