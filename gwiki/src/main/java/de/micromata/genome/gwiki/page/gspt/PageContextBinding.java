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

package de.micromata.genome.gwiki.page.gspt;

import groovy.lang.Binding;
import groovy.lang.MissingPropertyException;

import java.util.Map;

import javax.servlet.jsp.PageContext;

/**
 * Internal implementation for jsp/GSPT-Parsing.
 * 
 * @author Roger Rene Kommer (r.kommer@micromata.de)
 * 
 */
public class PageContextBinding extends Binding
{
  private PageContext pageContext;

  public PageContextBinding(PageContext pageContext, Map<String, Object> vars)
  {
    super(vars);
    this.pageContext = pageContext;
  }

  @Override
  public Object getProperty(String property)
  {
    return super.getProperty(property);
  }

  @Override
  public Object getVariable(String name)
  {
    try {
      Object val = super.getVariable(name);
      return val;
    } catch (MissingPropertyException e) {
      // noting
    }
    Object val = pageContext.findAttribute(name);
    return val;
  }
}
