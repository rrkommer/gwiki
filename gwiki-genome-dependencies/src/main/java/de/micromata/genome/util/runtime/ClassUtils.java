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

package de.micromata.genome.util.runtime;

import java.lang.reflect.Method;

public class ClassUtils
{
  /**
   * Find first method with given name
   * 
   * @param clazz
   * @param method
   * @return null if not found
   */
  public static Method findFirstMethod(Class< ? > clazz, String method)
  {
    for (Method m : clazz.getDeclaredMethods()) {
      if (m.getName().equals(method) == true)
        return m;
    }
    if (clazz.getSuperclass() != null)
      return findFirstMethod(clazz.getSuperclass(), method);
    return null;
  }

}
