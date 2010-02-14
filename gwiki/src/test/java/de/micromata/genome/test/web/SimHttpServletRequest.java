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

package de.micromata.genome.test.web;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.security.Principal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.ServletInputStream;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.collections15.iterators.IteratorEnumeration;

import de.micromata.genome.gwiki.web.StandaloneRequestDispatcher;

/**
 * 
 * @author Roger Rene Kommer (r.kommer@micromata.de)
 * 
 */
public class SimHttpServletRequest implements HttpServletRequest// extends MockHttpServletRequest
{
  private String contextPath;

  private String servletPath;

  private ServletInputStream servletIs;

  private BufferedReader reader;

  private HttpSession session;

  private String authType;

  private Cookie[] cookies;

  private Map<String, List<String>> headers = new HashMap<String, List<String>>();

  private Map<String, Object> attributes = new HashMap<String, Object>();

  private Map<String, String[]> parameters = new HashMap<String, String[]>();

  private String method = "POST";

  private String protocol = "http";

  private String serverName = "localhost";

  private int serverPort = 8080;

  private String pathInfo = "";

  private String queryString = "";

  private Principal userPrincipal;

  private Set<String> roles = new HashSet<String>();

  private String chacarcterEncoding = "UTF-8";

  private List<Locale> locales = new ArrayList<Locale>();

  private ServletContext servletContext;

  public SimHttpServletRequest(String contextPath, String servletPath)
  {
    this.contextPath = contextPath;
    this.servletPath = servletPath;
  }

  public SimHttpServletRequest()
  {
    this("/", "/x");
  }

  public HttpSession getSession()
  {
    return getSession(true);
  }

  public HttpSession getSession(boolean createNew)
  {
    if ((session == null) && (createNew == true)) {
      session = new SimHttpSession(servletContext);
    }
    return session;
  }

  public void setBody(final Reader reader)
  {
    this.reader = new BufferedReader(reader);
    setInputStream(new ReaderInputStream(reader));
  }

  public void setBody(final InputStream is)
  {
    this.reader = new BufferedReader(new InputStreamReader(is));
    setInputStream(new ReaderInputStream(reader));
  }

  protected void setInputStream(final InputStream servletIs)
  {
    this.servletIs = new ServletInputStream() {

      public int available() throws IOException
      {
        return servletIs.available();
      }

      public void close() throws IOException
      {
        servletIs.close();
      }

      public boolean equals(Object obj)
      {
        return servletIs.equals(obj);
      }

      public int hashCode()
      {
        return servletIs.hashCode();
      }

      public void mark(int readlimit)
      {
        servletIs.mark(readlimit);
      }

      public boolean markSupported()
      {
        return servletIs.markSupported();
      }

      public int read(byte[] b, int off, int len) throws IOException
      {
        return servletIs.read(b, off, len);
      }

      public int read(byte[] b) throws IOException
      {
        return servletIs.read(b);
      }

      public void reset() throws IOException
      {
        servletIs.reset();
      }

      public long skip(long n) throws IOException
      {
        return servletIs.skip(n);
      }

      public String toString()
      {
        return servletIs.toString();
      }

      @Override
      public int read() throws IOException
      {
        throw new UnsupportedOperationException();
      }
    };
  }

  public ServletInputStream getInputStream() throws IOException
  {
    return servletIs;
  }

  public BufferedReader getReader() throws IOException
  {
    return reader;
  }

  /*
   * (non-Javadoc)
   * 
   * @see javax.servlet.http.HttpServletRequest#getAuthType()
   */
  public String getAuthType()
  {
    return authType;
  }

  /*
   * (non-Javadoc)
   * 
   * @see javax.servlet.http.HttpServletRequest#getContextPath()
   */
  public String getContextPath()
  {
    return contextPath;
  }

  /*
   * (non-Javadoc)
   * 
   * @see javax.servlet.http.HttpServletRequest#getCookies()
   */
  public Cookie[] getCookies()
  {
    return cookies;
  }

  @SuppressWarnings("unchecked")
  protected <T> T headerDefaultValue(Class<T> cls)
  {
    if (cls.isAssignableFrom(Long.class)) {
      return (T) (Object) Long.valueOf(0);
    }
    if (cls.isAssignableFrom(Integer.class)) {
      return (T) (Object) Integer.valueOf(0);
    }
    return null;
  }

  @SuppressWarnings("unchecked")
  protected <T> T parseValue(String value, Class<T> cls)
  {
    if (cls.isAssignableFrom(Long.class)) {
      return (T) (Object) Long.parseLong(value);
    }
    if (cls.isAssignableFrom(Integer.class)) {
      return (T) (Object) Integer.parseInt(value);
    }
    return (T) (Object) value;
  }

  protected <T> T getHeaderValue(String name, Class<T> cls)
  {
    List<String> s = headers.get(name);
    if (s == null || s.size() == 0) {
      return headerDefaultValue(cls);
    }
    return parseValue(s.get(0), cls);
  }

  /*
   * (non-Javadoc)
   * 
   * @see javax.servlet.http.HttpServletRequest#getDateHeader(java.lang.String)
   */
  public long getDateHeader(String name)
  {
    return getHeaderValue(name, Long.class);
  }

  /*
   * (non-Javadoc)
   * 
   * @see javax.servlet.http.HttpServletRequest#getHeader(java.lang.String)
   */
  public String getHeader(String name)
  {
    return getHeaderValue(name, String.class);
  }

  /*
   * (non-Javadoc)
   * 
   * @see javax.servlet.http.HttpServletRequest#getHeaderNames()
   */
  @SuppressWarnings("unchecked")
  public Enumeration getHeaderNames()
  {
    return new IteratorEnumeration(headers.keySet().iterator());
  }

  /*
   * (non-Javadoc)
   * 
   * @see javax.servlet.http.HttpServletRequest#getHeaders(java.lang.String)
   */
  @SuppressWarnings("unchecked")
  public Enumeration getHeaders(String name)
  {
    List<String> hl = headers.get(name);
    if (hl == null) {
      return new IteratorEnumeration(Collections.EMPTY_LIST.iterator());
    }
    return new IteratorEnumeration(hl.iterator());
  }

  /*
   * (non-Javadoc)
   * 
   * @see javax.servlet.http.HttpServletRequest#getIntHeader(java.lang.String)
   */
  public int getIntHeader(String name)
  {
    return getHeaderValue(name, Integer.class);
  }

  /*
   * (non-Javadoc)
   * 
   * @see javax.servlet.http.HttpServletRequest#getMethod()
   */
  public String getMethod()
  {
    return method;
  }

  /*
   * (non-Javadoc)
   * 
   * @see javax.servlet.http.HttpServletRequest#getPathInfo()
   */
  public String getPathInfo()
  {
    return pathInfo;
  }

  /*
   * (non-Javadoc)
   * 
   * @see javax.servlet.http.HttpServletRequest#getPathTranslated()
   */
  public String getPathTranslated()
  {
    return getPathInfo();
  }

  /*
   * (non-Javadoc)
   * 
   * @see javax.servlet.http.HttpServletRequest#getQueryString()
   */
  public String getQueryString()
  {
    return queryString;
  }

  /*
   * (non-Javadoc)
   * 
   * @see javax.servlet.http.HttpServletRequest#getRemoteUser()
   */
  public String getRemoteUser()
  {
    Principal p = getUserPrincipal();
    if (p == null) {
      return null;
    }
    return p.getName();
  }

  /*
   * (non-Javadoc)
   * 
   * @see javax.servlet.http.HttpServletRequest#getRequestURI()
   */
  public String getRequestURI()
  {
    return contextPath + pathInfo;
  }

  /*
   * (non-Javadoc)
   * 
   * @see javax.servlet.http.HttpServletRequest#getRequestURL()
   */
  public StringBuffer getRequestURL()
  {
    return new StringBuffer(this.protocol + "://" + serverName + ":" + serverPort + contextPath + servletPath + pathInfo);
  }

  /*
   * (non-Javadoc)
   * 
   * @see javax.servlet.http.HttpServletRequest#getRequestedSessionId()
   */
  public String getRequestedSessionId()
  {
    if (session == null) {
      return null;
    }
    return session.getId();

  }

  /*
   * (non-Javadoc)
   * 
   * @see javax.servlet.http.HttpServletRequest#getServletPath()
   */
  public String getServletPath()
  {
    return servletPath;
  }

  /*
   * (non-Javadoc)
   * 
   * @see javax.servlet.http.HttpServletRequest#getUserPrincipal()
   */
  public Principal getUserPrincipal()
  {
    return userPrincipal;
  }

  /*
   * (non-Javadoc)
   * 
   * @see javax.servlet.http.HttpServletRequest#isRequestedSessionIdFromCookie()
   */
  public boolean isRequestedSessionIdFromCookie()
  {
    return true;
  }

  /*
   * (non-Javadoc)
   * 
   * @see javax.servlet.http.HttpServletRequest#isRequestedSessionIdFromURL()
   */
  public boolean isRequestedSessionIdFromURL()
  {
    return false;
  }

  /*
   * (non-Javadoc)
   * 
   * @see javax.servlet.http.HttpServletRequest#isRequestedSessionIdFromUrl()
   */
  public boolean isRequestedSessionIdFromUrl()
  {
    return false;
  }

  /*
   * (non-Javadoc)
   * 
   * @see javax.servlet.http.HttpServletRequest#isRequestedSessionIdValid()
   */
  public boolean isRequestedSessionIdValid()
  {
    return true;
  }

  /*
   * (non-Javadoc)
   * 
   * @see javax.servlet.http.HttpServletRequest#isUserInRole(java.lang.String)
   */
  public boolean isUserInRole(String role)
  {
    return roles.contains(role);
  }

  /*
   * (non-Javadoc)
   * 
   * @see javax.servlet.ServletRequest#getAttribute(java.lang.String)
   */
  public Object getAttribute(String name)
  {
    return attributes.get(name);
  }

  /*
   * (non-Javadoc)
   * 
   * @see javax.servlet.ServletRequest#getAttributeNames()
   */
  @SuppressWarnings("unchecked")
  public Enumeration getAttributeNames()
  {
    return new IteratorEnumeration(attributes.keySet().iterator());
  }

  /*
   * (non-Javadoc)
   * 
   * @see javax.servlet.ServletRequest#getCharacterEncoding()
   */
  public String getCharacterEncoding()
  {
    return chacarcterEncoding;
  }

  /*
   * (non-Javadoc)
   * 
   * @see javax.servlet.ServletRequest#getContentLength()
   */
  public int getContentLength()
  {
    return -1;
  }

  /*
   * (non-Javadoc)
   * 
   * @see javax.servlet.ServletRequest#getContentType()
   */
  public String getContentType()
  {
    return null;
  }

  /*
   * (non-Javadoc)
   * 
   * @see javax.servlet.ServletRequest#getLocalAddr()
   */
  public String getLocalAddr()
  {
    return "127.0.0.1";
  }

  /*
   * (non-Javadoc)
   * 
   * @see javax.servlet.ServletRequest#getLocalName()
   */
  public String getLocalName()
  {
    return getServerName();
  }

  /*
   * (non-Javadoc)
   * 
   * @see javax.servlet.ServletRequest#getLocalPort()
   */
  public int getLocalPort()
  {
    return getServerPort();
  }

  /*
   * (non-Javadoc)
   * 
   * @see javax.servlet.ServletRequest#getLocale()
   */
  public Locale getLocale()
  {
    return locales.get(0);
  }

  /*
   * (non-Javadoc)
   * 
   * @see javax.servlet.ServletRequest#getLocales()
   */
  @SuppressWarnings("unchecked")
  public Enumeration getLocales()
  {
    return new IteratorEnumeration(locales.iterator());
  }

  /*
   * (non-Javadoc)
   * 
   * @see javax.servlet.ServletRequest#getParameter(java.lang.String)
   */
  public String getParameter(String name)
  {
    String[] pm = parameters.get(name);
    if (pm != null) {
      return pm[0];
    }
    return null;
  }

  /*
   * (non-Javadoc)
   * 
   * @see javax.servlet.ServletRequest#getParameterMap()
   */
  public Map getParameterMap()
  {
    return parameters;
  }

  /*
   * (non-Javadoc)
   * 
   * @see javax.servlet.ServletRequest#getParameterNames()
   */
  @SuppressWarnings("unchecked")
  public Enumeration getParameterNames()
  {
    return new IteratorEnumeration(parameters.keySet().iterator());
  }

  /*
   * (non-Javadoc)
   * 
   * @see javax.servlet.ServletRequest#getParameterValues(java.lang.String)
   */
  public String[] getParameterValues(String name)
  {
    return parameters.get(name);
  }

  /*
   * (non-Javadoc)
   * 
   * @see javax.servlet.ServletRequest#getProtocol()
   */
  public String getProtocol()
  {
    return protocol;
  }

  /*
   * (non-Javadoc)
   * 
   * @see javax.servlet.ServletRequest#getRealPath(java.lang.String)
   */
  public String getRealPath(String path)
  {
    return path;
  }

  /*
   * (non-Javadoc)
   * 
   * @see javax.servlet.ServletRequest#getRemoteAddr()
   */
  public String getRemoteAddr()
  {
    return "127.0.0.1";
  }

  /*
   * (non-Javadoc)
   * 
   * @see javax.servlet.ServletRequest#getRemoteHost()
   */
  public String getRemoteHost()
  {
    return "localhost";
  }

  /*
   * (non-Javadoc)
   * 
   * @see javax.servlet.ServletRequest#getRemotePort()
   */
  public int getRemotePort()
  {
    return 9999;
  }

  /*
   * (non-Javadoc)
   * 
   * @see javax.servlet.ServletRequest#getRequestDispatcher(java.lang.String)
   */
  public RequestDispatcher getRequestDispatcher(String path)
  {
    return new StandaloneRequestDispatcher(path);
  }

  /*
   * (non-Javadoc)
   * 
   * @see javax.servlet.ServletRequest#getScheme()
   */
  public String getScheme()
  {
    return getProtocol();
  }

  /*
   * (non-Javadoc)
   * 
   * @see javax.servlet.ServletRequest#getServerName()
   */
  public String getServerName()
  {
    return serverName;
  }

  /*
   * (non-Javadoc)
   * 
   * @see javax.servlet.ServletRequest#getServerPort()
   */
  public int getServerPort()
  {
    return serverPort;
  }

  /*
   * (non-Javadoc)
   * 
   * @see javax.servlet.ServletRequest#isSecure()
   */
  public boolean isSecure()
  {
    return protocol.equalsIgnoreCase("https") == true;
  }

  /*
   * (non-Javadoc)
   * 
   * @see javax.servlet.ServletRequest#removeAttribute(java.lang.String)
   */
  public void removeAttribute(String name)
  {
    attributes.remove(name);
  }

  /*
   * (non-Javadoc)
   * 
   * @see javax.servlet.ServletRequest#setAttribute(java.lang.String, java.lang.Object)
   */
  public void setAttribute(String name, Object o)
  {
    attributes.put(name, o);
  }

  /*
   * (non-Javadoc)
   * 
   * @see javax.servlet.ServletRequest#setCharacterEncoding(java.lang.String)
   */
  public void setCharacterEncoding(String env) throws UnsupportedEncodingException
  {
    chacarcterEncoding = env;
  }

  public ServletInputStream getServletIs()
  {
    return servletIs;
  }

  public void setServletIs(ServletInputStream servletIs)
  {
    this.servletIs = servletIs;
  }

  public Map<String, List<String>> getHeaders()
  {
    return headers;
  }

  public void setHeaders(Map<String, List<String>> headers)
  {
    this.headers = headers;
  }

  public Map<String, Object> getAttributes()
  {
    return attributes;
  }

  public void setAttributes(Map<String, Object> attributes)
  {
    this.attributes = attributes;
  }

  public Map<String, String[]> getParameters()
  {
    return parameters;
  }

  public void setParameters(Map<String, String[]> parameters)
  {
    this.parameters = parameters;
  }

  public Set<String> getRoles()
  {
    return roles;
  }

  public void setRoles(Set<String> roles)
  {
    this.roles = roles;
  }

  public String getChacarcterEncoding()
  {
    return chacarcterEncoding;
  }

  public void setChacarcterEncoding(String chacarcterEncoding)
  {
    this.chacarcterEncoding = chacarcterEncoding;
  }

  public ServletContext getServletContext()
  {
    return servletContext;
  }

  public void setServletContext(ServletContext servletContext)
  {
    this.servletContext = servletContext;
  }

  public void setContextPath(String contextPath)
  {
    this.contextPath = contextPath;
  }

  public void setServletPath(String servletPath)
  {
    this.servletPath = servletPath;
  }

  public void setReader(BufferedReader reader)
  {
    this.reader = reader;
  }

  public void setSession(HttpSession session)
  {
    this.session = session;
  }

  public void setAuthType(String authType)
  {
    this.authType = authType;
  }

  public void setCookies(Cookie[] cookies)
  {
    this.cookies = cookies;
  }

  public void setMethod(String method)
  {
    this.method = method;
  }

  public void setProtocol(String protocol)
  {
    this.protocol = protocol;
  }

  public void setServerName(String serverName)
  {
    this.serverName = serverName;
  }

  public void setServerPort(int serverPort)
  {
    this.serverPort = serverPort;
  }

  public void setPathInfo(String pathInfo)
  {
    this.pathInfo = pathInfo;
  }

  public void setQueryString(String queryString)
  {
    this.queryString = queryString;
  }

  public void setUserPrincipal(Principal userPrincipal)
  {
    this.userPrincipal = userPrincipal;
  }

  public void setLocales(List<Locale> locales)
  {
    this.locales = locales;
  }

}
