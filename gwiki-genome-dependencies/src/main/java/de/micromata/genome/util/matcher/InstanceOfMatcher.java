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

package de.micromata.genome.util.matcher;

/**
 * 
 * @author roger
 * 
 */
public class InstanceOfMatcher extends MatcherBase<Object>
{

  private static final long serialVersionUID = 1346403525135515211L;

  private Class< ? > clazz;

  public InstanceOfMatcher()
  {

  }

  public InstanceOfMatcher(String clazz)
  {
    try {
      this.clazz = Class.forName(clazz);
    } catch (ClassNotFoundException cne) {
      /**
       * @logging
       * @reason Kann die angegebene Klasse nicht laden
       * @action Konfiguration überprüfen
       */
      throw new RuntimeException("Could not load class: " + clazz + "; " + cne.getMessage(), cne);
    }
  }

  public boolean match(Object object)
  {
    return clazz.isAssignableFrom(object.getClass());
  }

  public String toString()
  {
    return "<EXPR> instanceof " + clazz.getCanonicalName();
  }

  public Class< ? > getClazz()
  {
    return clazz;
  }

  public void setClazz(Class< ? > clazz)
  {
    this.clazz = clazz;
  }

}
