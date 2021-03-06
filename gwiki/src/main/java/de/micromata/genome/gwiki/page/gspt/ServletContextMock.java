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

package de.micromata.genome.gwiki.page.gspt;

import java.io.InputStream;
import java.net.URL;
import java.util.Enumeration;
import java.util.NoSuchElementException;
import java.util.Set;

import javax.servlet.RequestDispatcher;
import javax.servlet.Servlet;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;

import org.apache.log4j.Logger;
/**
 * Internal implementation for jsp/GSPT-Parsing.
 * 
 * @author Roger Rene Kommer (r.kommer@micromata.de)
 * 
 */
public class ServletContextMock implements ServletContext
{
  private static final Logger log = Logger.getLogger(ServletContextMock.class);

  private static final Enumeration< ? > emptyEnumeration = new Enumeration<Object>() {

    public boolean hasMoreElements()
    {
      return false;
    }

    public Object nextElement()
    {
      throw new NoSuchElementException();
    }

  };

  private String contextPath = "/";

  public ServletContextMock()
  {

  }

  public ServletContextMock(String contextPath)
  {
    this.contextPath = contextPath;
  }

  public Object getAttribute(String arg0)
  {
    return null;
  }

  public Enumeration< ? > getAttributeNames()
  {
    return emptyEnumeration;
  }

  public ServletContext getContext(String arg0)
  {
    return null;
  }

  public String getInitParameter(String arg0)
  {
    return null;
  }

  public Enumeration< ? > getInitParameterNames()
  {
    return emptyEnumeration;
  }

  public int getMajorVersion()
  {
    return 2;
  }

  public String getMimeType(String arg0)
  {
    return null;
  }

  public int getMinorVersion()
  {
    return 4;
  }

  public RequestDispatcher getNamedDispatcher(String arg0)
  {
    return null;
  }

  public String getRealPath(String name)
  {
    return getResource(name).getFile();
  }

  public RequestDispatcher getRequestDispatcher(String arg0)
  {
    return null;
  }

  public URL getResource(String name)
  {
    return ServletContextMock.class.getClassLoader().getResource(name);
  }

  public InputStream getResourceAsStream(String name)
  {
    return ServletContextMock.class.getClassLoader().getResourceAsStream(name);
  }

  public Set< ? > getResourcePaths(String arg0)
  {
    // TODO Auto-generated method stub
    return null;
  }

  public String getServerInfo()
  {
    return "svgt/0.1";
  }

  public Servlet getServlet(String arg0) throws ServletException
  {
    return null;
  }

  public String getServletContextName()
  {
    return "svgt";
  }

  public Enumeration< ? > getServletNames()
  {
    return emptyEnumeration;
  }

  public Enumeration< ? > getServlets()
  {
    return emptyEnumeration;
  }

  public void log(String arg0)
  {
    log.info(arg0);
  }

  public void log(Exception arg0, String arg1)
  {
    log(arg1, arg0);
  }

  public void log(String arg0, Throwable arg1)
  {
    log.info(arg0, arg1);
  }

  public void removeAttribute(String arg0)
  {
  }

  public void setAttribute(String arg0, Object arg1)
  {
  }

  public String getContextPath()
  {
    return contextPath;
  }

}
