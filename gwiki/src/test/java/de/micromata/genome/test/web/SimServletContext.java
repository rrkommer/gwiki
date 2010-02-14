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

////////////////////////////////////////////////////////////////////////////

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

////////////////////////////////////////////////////////////////////////////

package de.micromata.genome.test.web;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.Filter;
import javax.servlet.RequestDispatcher;
import javax.servlet.Servlet;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;

import org.apache.commons.collections15.iterators.IteratorEnumeration;

import de.micromata.genome.gwiki.web.StandaloneRequestDispatcher;

/**
 * 
 * @author Roger Rene Kommer (r.kommer@micromata.de)
 * 
 */
public class SimServletContext implements ServletContext
{
  private String contextName;

  private String webDir;

  private File webDirFile;

  private Map<String, String> initParameters = new HashMap<String, String>();

  private Map<String, Object> attributes = new HashMap<String, Object>();

  private List<Filter> filters = new ArrayList<Filter>();

  private HttpServlet servlet;

  public SimServletContext(String contextName, String webDir)
  {
    this.contextName = contextName;
    this.webDir = webDir;
    this.webDirFile = new File(webDir);
    if (webDirFile.exists() == false) {
      throw new RuntimeException("SimServletContext; local web directory does not exists");
    }
  }

  public void setServlet(Class< ? extends HttpServlet> servletClass, String servletName, Map<String, String> initParams)
  {
    try {
      SimServletConfig config = new SimServletConfig();
      config.setServletName(servletName);
      config.setServletContext(this);
      if (initParams != null) {
        config.getInitParameter().putAll(initParams);
      }
      this.servlet = servletClass.newInstance();
      this.servlet.init(config);
    } catch (Exception ex) {
      throw new RuntimeException("Exception registering servlet with name " + servletName + ": " + ex.getMessage(), ex);
    }
  }

  public String getWebDir()
  {
    return webDir;
  }

  public void setWebDir(String webDir)
  {
    this.webDir = webDir;
  }

  public InputStream getResourceAsStream(String name)
  {
    File f = new File(webDirFile, name);
    if (f.exists() == false) {
      return null;
    }
    try {
      return new FileInputStream(f);
    } catch (IOException ex) {
      return null;
    }
  }

  /*
   * (non-Javadoc)
   * 
   * @see javax.servlet.ServletContext#getAttribute(java.lang.String)
   */
  public Object getAttribute(String name)
  {
    return attributes.get(name);
  }

  /*
   * (non-Javadoc)
   * 
   * @see javax.servlet.ServletContext#getAttributeNames()
   */
  @SuppressWarnings("unchecked")
  public Enumeration getAttributeNames()
  {
    return new IteratorEnumeration(attributes.keySet().iterator());
  }

  /*
   * (non-Javadoc)
   * 
   * @see javax.servlet.ServletContext#getContext(java.lang.String)
   */
  public ServletContext getContext(String uripath)
  {
    if (uripath.startsWith("/" + this.contextName)) {
      return this;
    } else {
      return null;
    }
  }

  /*
   * (non-Javadoc)
   * 
   * @see javax.servlet.ServletContext#getContextPath()
   */
  public String getContextPath()
  {
    return contextName;
  }

  /*
   * (non-Javadoc)
   * 
   * @see javax.servlet.ServletContext#getInitParameter(java.lang.String)
   */
  public String getInitParameter(String name)
  {
    return initParameters.get(name);
  }

  /*
   * (non-Javadoc)
   * 
   * @see javax.servlet.ServletContext#getInitParameterNames()
   */
  @SuppressWarnings("unchecked")
  public Enumeration getInitParameterNames()
  {
    return new IteratorEnumeration(initParameters.keySet().iterator());
  }

  /*
   * (non-Javadoc)
   * 
   * @see javax.servlet.ServletContext#getMajorVersion()
   */
  public int getMajorVersion()
  {
    return 2;
  }

  /*
   * (non-Javadoc)
   * 
   * @see javax.servlet.ServletContext#getMimeType(java.lang.String)
   */
  public String getMimeType(String file)
  {
    return null;
  }

  /*
   * (non-Javadoc)
   * 
   * @see javax.servlet.ServletContext#getMinorVersion()
   */
  public int getMinorVersion()
  {
    return 4;
  }

  /*
   * (non-Javadoc)
   * 
   * @see javax.servlet.ServletContext#getNamedDispatcher(java.lang.String)
   */
  public RequestDispatcher getNamedDispatcher(String name)
  {
    return new StandaloneRequestDispatcher(name);
  }

  /*
   * (non-Javadoc)
   * 
   * @see javax.servlet.ServletContext#getRealPath(java.lang.String)
   */
  public String getRealPath(String path)
  {
    return null;
  }

  /*
   * (non-Javadoc)
   * 
   * @see javax.servlet.ServletContext#getRequestDispatcher(java.lang.String)
   */
  public RequestDispatcher getRequestDispatcher(String path)
  {
    return new StandaloneRequestDispatcher(path);
  }

  /*
   * (non-Javadoc)
   * 
   * @see javax.servlet.ServletContext#getResource(java.lang.String)
   */
  public URL getResource(String path) throws MalformedURLException
  {
    return Thread.currentThread().getContextClassLoader().getResource(path);
  }

  /*
   * (non-Javadoc)
   * 
   * @see javax.servlet.ServletContext#getResourcePaths(java.lang.String)
   */
  @SuppressWarnings("unchecked")
  public Set getResourcePaths(String path)
  {
    return null;
  }

  /*
   * (non-Javadoc)
   * 
   * @see javax.servlet.ServletContext#getServerInfo()
   */
  public String getServerInfo()
  {
    return "Sim Servlet Environment, version 1.0.";
  }

  /*
   * (non-Javadoc)
   * 
   * @see javax.servlet.ServletContext#getServlet(java.lang.String)
   */
  public Servlet getServlet(String name) throws ServletException
  {
    return null;
  }

  /*
   * (non-Javadoc)
   * 
   * @see javax.servlet.ServletContext#getServletContextName()
   */
  public String getServletContextName()
  {
    return contextName;
  }

  /*
   * (non-Javadoc)
   * 
   * @see javax.servlet.ServletContext#getServletNames()
   */
  @SuppressWarnings("unchecked")
  public Enumeration getServletNames()
  {
    return Collections.enumeration(Collections.emptySet());
  }

  /*
   * (non-Javadoc)
   * 
   * @see javax.servlet.ServletContext#getServlets()
   */
  @SuppressWarnings("unchecked")
  public Enumeration getServlets()
  {
    return Collections.enumeration(Collections.emptySet());
  }

  /*
   * (non-Javadoc)
   * 
   * @see javax.servlet.ServletContext#log(java.lang.String)
   */
  public void log(String msg)
  {
    // System.out.println("MockServletContext: " + message);
    // TODO Auto-generated method stub

  }

  /*
   * (non-Javadoc)
   * 
   * @see javax.servlet.ServletContext#log(java.lang.Exception, java.lang.String)
   */
  public void log(Exception exception, String msg)
  {
    // TODO Auto-generated method stub

  }

  /*
   * (non-Javadoc)
   * 
   * @see javax.servlet.ServletContext#log(java.lang.String, java.lang.Throwable)
   */
  public void log(String message, Throwable throwable)
  {
    // TODO Auto-generated method stub

  }

  /*
   * (non-Javadoc)
   * 
   * @see javax.servlet.ServletContext#removeAttribute(java.lang.String)
   */
  public void removeAttribute(String name)
  {
    attributes.remove(name);

  }

  /*
   * (non-Javadoc)
   * 
   * @see javax.servlet.ServletContext#setAttribute(java.lang.String, java.lang.Object)
   */
  public void setAttribute(String name, Object object)
  {
    attributes.put(name, object);
  }
}
