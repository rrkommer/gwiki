////////////////////////////////////////////////////////////////////////////
//
// Copyright (C) 2010 Micromata GmbH
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

package de.micromata.genome.gwiki.model.config;

import java.util.Properties;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.servlet.ServletConfig;

import org.springframework.beans.factory.config.PropertyPlaceholderConfigurer;

/**
 * Place holder implementation that ${} expressions can be used in spring xml context files.
 * 
 * @author Roger Rene Kommer (r.kommer@micromata.de)
 * 
 */
public class GWikiDAOContextPropertyPlaceholderConfigurer extends PropertyPlaceholderConfigurer
{
  protected ServletConfig servletConfig;

  public GWikiDAOContextPropertyPlaceholderConfigurer(ServletConfig servletConfig)
  {
    this.servletConfig = servletConfig;
  }

  public GWikiDAOContextPropertyPlaceholderConfigurer()
  {

  }

  protected String resolveByJndi(String key)
  {
    try {
      InitialContext ctx = new InitialContext();
      Object val = ctx.lookup(key);
      if (val instanceof String) {
        return (String) val;
      }
      return null;
    } catch (NamingException e) {
      return null;
    }
  }

  protected String resolveByServletConfig(String key)
  {
    if (servletConfig == null) {
      return null;
    }
    return servletConfig.getInitParameter(key);
  }

  protected String resolveByServletContext(String key)
  {
    if (servletConfig == null || servletConfig.getServletContext() == null) {
      return null;
    }
    return servletConfig.getServletContext().getInitParameter(key);
  }

  public String resolveBySystemProperties(String key)
  {
    return System.getProperty(key);
  }

  @Override
  protected String resolvePlaceholder(String key, Properties props)
  {
    String val;
    val = resolveByJndi(key);
    if (val != null) {
      return val;
    }
    val = resolveByServletContext(key);
    if (val != null) {
      return val;
    }
    val = resolveByServletConfig(key);
    if (val != null) {
      return val;
    }
    val = resolveBySystemProperties(key);
    if (val != null) {
      return val;
    }
    return super.resolvePlaceholder(key, props);
  }

  public ServletConfig getServletConfig()
  {
    return servletConfig;
  }

  public void setServletConfig(ServletConfig servletConfig)
  {
    this.servletConfig = servletConfig;
  }
}
