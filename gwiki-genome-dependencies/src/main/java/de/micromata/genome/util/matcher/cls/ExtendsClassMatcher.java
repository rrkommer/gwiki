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
