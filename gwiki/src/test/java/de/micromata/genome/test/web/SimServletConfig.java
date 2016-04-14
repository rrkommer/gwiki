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

package de.micromata.genome.test.web;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;

import org.apache.commons.collections15.iterators.IteratorEnumeration;

/**
 * @author Roger Rene Kommer (r.kommer@micromata.de)
 * 
 */
public class SimServletConfig implements ServletConfig
{
  private Map<String, String> initParameter = new HashMap<String, String>();

  private String servletName;

  private ServletContext servletContext;

  /*
   * (non-Javadoc)
   * 
   * @see javax.servlet.ServletConfig#getInitParameter(java.lang.String)
   */
  public String getInitParameter(String name)
  {
    return initParameter.get(name);
  }

  /*
   * (non-Javadoc)
   * 
   * @see javax.servlet.ServletConfig#getInitParameterNames()
   */
  @SuppressWarnings({ "unchecked", "rawtypes"})
  public Enumeration getInitParameterNames()
  {
    return new IteratorEnumeration(initParameter.keySet().iterator());
  }

  /*
   * (non-Javadoc)
   * 
   * @see javax.servlet.ServletConfig#getServletContext()
   */
  public ServletContext getServletContext()
  {
    return servletContext;
  }

  /*
   * (non-Javadoc)
   * 
   * @see javax.servlet.ServletConfig#getServletName()
   */
  public String getServletName()
  {
    return servletName;
  }

  public Map<String, String> getInitParameter()
  {
    return initParameter;
  }

  public void setInitParameter(Map<String, String> initParameter)
  {
    this.initParameter = initParameter;
  }

  public void setServletName(String servletName)
  {
    this.servletName = servletName;
  }

  public void setServletContext(ServletContext servletContext)
  {
    this.servletContext = servletContext;
  }

}
