//
// Copyright (C) 2010-2016 Roger Rene Kommer & Micromata GmbH
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//  http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.
//

package de.micromata.genome.gdbfs;

import org.apache.commons.lang3.StringUtils;

/**
 * Internal utils to deal with file names.
 * 
 * @author Roger Rene Kommer (r.kommer@micromata.de)
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

  /**
   * Gets the name part.
   *
   * @param name the name
   * @return the name part
   */
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
    if (StringUtils.isEmpty(second) == true || second.equals("/") == true) {
      return first;
    }
    if (StringUtils.isEmpty(first) == true || first.equals("/") == true) {
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
