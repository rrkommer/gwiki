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
 * Matches if given Object is instanceof class
 * 
 * @author roger@micromata.de
 * 
 */
public class BeanInstanceOfMatcher extends MatcherBase<Object>
{

  private static final long serialVersionUID = 7753923065375842215L;

  private Class< ? > ofClass;

  public BeanInstanceOfMatcher()
  {

  }

  public BeanInstanceOfMatcher(Class< ? > ofClass)
  {
    this.ofClass = ofClass;
  }

  public boolean match(Object object)
  {
    if (object == null)
      return false;
    return ofClass.isAssignableFrom(object.getClass());
  }

  public Class< ? > getOfClass()
  {
    return ofClass;
  }

  public void setOfClass(Class< ? > ofClass)
  {
    this.ofClass = ofClass;
  }
}
