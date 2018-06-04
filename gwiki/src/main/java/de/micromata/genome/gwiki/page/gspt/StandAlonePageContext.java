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

import java.io.IOException;
import java.util.ArrayList;
import java.util.EmptyStackException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.el.ELContext;
import javax.servlet.Servlet;
import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.el.ELException;
import javax.servlet.jsp.el.ExpressionEvaluator;
import javax.servlet.jsp.el.VariableResolver;
import javax.servlet.jsp.tagext.BodyContent;

import org.apache.commons.collections4.MapUtils;
import org.apache.commons.collections4.iterators.IteratorEnumeration;
import org.apache.log4j.Logger;

/**
 * TODO See ServletStandalonePageContext, this class superflous?
 * 
 * @author Roger Rene Kommer (r.kommer@micromata.de)
 * 
 */
@SuppressWarnings("deprecation")
public class StandAlonePageContext extends PageContext
{
  private static final Logger log = Logger.getLogger(StandAlonePageContext.class);

  protected final static Map<String, Object> applicationScope = MapUtils.synchronizedMap(new HashMap<String, Object>());

  public static final int SCOPE_SIZE = 3; // request, session, application

  private final JspWriter writer;

  private List<Map< ? super String, Object>> scopes;

  private final VariableResolver resolver;

  private BodyContent topOfWriterStack;

  private final ServletContext servletCtx;

  private HttpServletRequest request;

  private HttpServletResponse response;

  public StandAlonePageContext()
  {
    this(new NullJspWriter(), null, null, null);
  }

  public StandAlonePageContext(JspWriter writer)
  {
    this(writer, null, null, null);
  }

  public StandAlonePageContext(JspWriter jspWriter, ServletContext servletContext, HttpServletRequest request, HttpServletResponse response)
  {
    this.writer = jspWriter;
    this.request = request;
    this.response = response;
    this.servletCtx = servletContext != null ? servletContext : new ServletContextMock();
    initScopes();
    this.resolver = new VariableResolver() {
      public Object resolveVariable(String key) throws ELException
      {
        return findAttribute(key);
      }
    };
  }

  protected void initScopes()
  {
    scopes = new ArrayList<Map< ? super String, Object>>(4);
    scopes.add(new HashMap<String, Object>()); // PAGE_SCOPE
    scopes.add(new HashMap<String, Object>()); // REQUEST_SCOPE
    scopes.add(new HashMap<String, Object>()); // SESSION_SCOPE
    scopes.add(applicationScope); // APPLICATION_SCOPE
  }

  @Override
  public void forward(String path) throws ServletException, IOException
  {
    if (request == null) {
      throw new UnsupportedOperationException("StandAlonePageContext.forward not supported");
    }
    request.getRequestDispatcher(path).forward(request, response);
  }

  @Override
  public Exception getException()
  {
    log.debug("getException");
    return null;
  }

  @Override
  public Object getPage()
  {
    log.debug("getPage");
    return null;
  }

  @Override
  public ServletRequest getRequest()
  {
    return request;
  }

  @Override
  public ServletResponse getResponse()
  {
    return response;
  }

  @Override
  public ServletConfig getServletConfig()
  {
    log.debug("getServletConfig");
    return null;
  }

  @Override
  public ServletContext getServletContext()
  {
    return servletCtx;
  }

  @Override
  public HttpSession getSession()
  {
    log.debug("getSession");
    // throw new UnsupportedOperationException("Svgt; SvgtPageContext.getSession not supported");
    return null;
  }

  @Override
  public void handlePageException(Exception ex) throws ServletException, IOException
  {
    throw new ServletException(ex.getMessage(), ex);
  }

  @Override
  public void handlePageException(Throwable ex) throws ServletException, IOException
  {
    throw new ServletException(ex.getMessage(), ex);
  }

  @Override
  public void include(String path, boolean flush) throws ServletException, IOException
  {
    if (request == null) {
      throw new UnsupportedOperationException("Svgt; StandAlonePageContext.include() not implemented");
    }
    request.getRequestDispatcher(path).include(request, response);
    if (flush == true) {
      response.flushBuffer();
    }
  }

  @Override
  public void include(String path) throws ServletException, IOException
  {
    include(path, true);
  }

  @Override
  public void initialize(Servlet arg0, ServletRequest arg1, ServletResponse arg2, String arg3, boolean arg4, int arg5, boolean arg6)
      throws IOException, IllegalStateException, IllegalArgumentException
  {
    // unused
  }

  @Override
  public void release()
  {
    // unused
  }

  @Override
  public Object findAttribute(String key)
  {

    for (Map< ? super String, Object> m : scopes) {
      if (m.containsKey(key) == true)
        return m.get(key);
    }
    return null;
  }

  @Override
  public Object getAttribute(String key, int scope)
  {
    assertValidScope(scope, true, true);
    return scopes.get(scope - 1).get(key);
  }

  private static void assertValidScope(int scope, boolean allowSession, boolean allowApplication)
  {
    if ((scope < PageContext.PAGE_SCOPE) || (scope > PageContext.APPLICATION_SCOPE)) {
      throw new IllegalArgumentException("scope " + scope + " not supported");
    }
    if ((allowSession == false) && (scope == PageContext.SESSION_SCOPE)) {
      throw new IllegalArgumentException("session scope not supported");
    }
  }

  @Override
  public Object getAttribute(String key)
  {
    return scopes.get(0).get(key);
  }

  @SuppressWarnings({ "unchecked", "rawtypes"})
  @Override
  public Enumeration getAttributeNamesInScope(int scope)
  {
    assertValidScope(scope, true, true);
    return new IteratorEnumeration(scopes.get(scope - 1).keySet().iterator());
  }

  @Override
  public int getAttributesScope(String key)
  {
    int i = 1;
    for (Map< ? super String, Object> m : scopes) {
      if (m.containsKey(key) == true) {
        return i;
      }
      ++i;
    }
    return 0;
  }

  @Override
  public ExpressionEvaluator getExpressionEvaluator()
  {
    throw new UnsupportedOperationException("we have groovy templates => we do neither need nor provide an explicit ExpressionEvaluator");
  }

  @Override
  public JspWriter getOut()
  {
    if (topOfWriterStack != null) {
      return topOfWriterStack;
    }
    return writer;
  }

  @Override
  public VariableResolver getVariableResolver()
  {
    return resolver;
  }

  @Override
  public void removeAttribute(String key, int scope)
  {
    assertValidScope(scope, true, false);
    Map< ? super String, Object> scopeMap = scopes.get(scope - 1);
    if (scopeMap != null) {
      scopeMap.remove(key);
    }
  }

  @Override
  public void removeAttribute(String key)
  {
    for (int i = 0; i < PageContext.APPLICATION_SCOPE - 1; ++i) {
      scopes.get(i).remove(key);
    }
  }

  @Override
  public void setAttribute(String key, Object value, int scope)
  {
    assertValidScope(scope, false, false);
    Map< ? super String, Object> scopeMap = scopes.get(scope - 1);
    if (scopeMap != null) {
      scopeMap.put(key, value);
    }
  }

  @Override
  public void setAttribute(String key, Object value)
  {
    setAttribute(key, value, PageContext.PAGE_SCOPE);
  }

  @Override
  public BodyContent pushBody()
  {
    topOfWriterStack = new BodyContentImpl(getOut());
    return topOfWriterStack;
  }

  @Override
  public JspWriter popBody()
  {
    if (topOfWriterStack == null) {
      throw new EmptyStackException();
    }
    JspWriter parent = topOfWriterStack.getEnclosingWriter();
    if (parent instanceof BodyContent) {
      topOfWriterStack = (BodyContent) parent;
    }
    return parent;
  }

  public void setAttributes(Map<String, Object> map, int scope)
  {
    scopes.get(scope - 1).putAll(map);
  }

  public ELContext getELContext()
  {
    throw new UnsupportedOperationException("we have groovy templates => we do neither need nor provide an explicit ELContext");
  }
}