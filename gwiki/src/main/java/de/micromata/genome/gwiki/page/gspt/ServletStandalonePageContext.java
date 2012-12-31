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

package de.micromata.genome.gwiki.page.gspt;

import java.io.IOException;
import java.util.EmptyStackException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import javax.el.ELContext;
import javax.servlet.Servlet;
import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.el.ELException;
import javax.servlet.jsp.el.ExpressionEvaluator;
import javax.servlet.jsp.el.VariableResolver;
import javax.servlet.jsp.tagext.BodyContent;

import org.apache.commons.collections.IteratorUtils;
import org.apache.commons.lang.StringUtils;

import de.micromata.genome.gwiki.page.gspt.jdkrepl.PrintWriterPatched;

/**
 * Implementation of a standalone PageContext.
 * 
 * @author Roger Rene Kommer (r.kommer@micromata.de)
 * 
 */
@SuppressWarnings("deprecation")
public class ServletStandalonePageContext extends PageContext
{
  private HttpServletRequest request;

  private HttpServletResponse response;

  private HttpServlet servlet;

  protected JspWriter writer;

  public HttpServlet getServlet()
  {
    return servlet;
  }

  public void setServlet(HttpServlet servlet)
  {
    this.servlet = servlet;
  }

  public JspWriter getWriter()
  {
    return writer;
  }

  public void setWriter(JspWriter writer)
  {
    this.writer = writer;
  }

  public ServletContext getServletCtx()
  {
    return servletCtx;
  }

  public void setServletCtx(ServletContext servletCtx)
  {
    this.servletCtx = servletCtx;
  }

  public Map<String, Object> getPageAttributes()
  {
    return pageAttributes;
  }

  public void setPageAttributes(Map<String, Object> pageAttributes)
  {
    this.pageAttributes = pageAttributes;
  }

  public VariableResolver getResolver()
  {
    return resolver;
  }

  public void setResolver(VariableResolver resolver)
  {
    this.resolver = resolver;
  }

  public BodyContent getTopOfWriterStack()
  {
    return topOfWriterStack;
  }

  public void setTopOfWriterStack(BodyContent topOfWriterStack)
  {
    this.topOfWriterStack = topOfWriterStack;
  }

  public void setSession(HttpSession session)
  {
    this.session = session;
  }

  protected ServletContext servletCtx;

  protected HttpSession session;

  protected Map<String, Object> pageAttributes = new HashMap<String, Object>();

  private VariableResolver resolver;

  private BodyContent topOfWriterStack;

  public ServletStandalonePageContext()
  {

  }

  @Override
  public void initialize(Servlet servlet, ServletRequest request, ServletResponse response, String errorPageURL, boolean needsSession,
      int bufferSize, boolean autoFlush) throws IOException, IllegalStateException, IllegalArgumentException
  {

    this.request = (HttpServletRequest) request;
    this.response = (HttpServletResponse) response;
    this.servlet = (HttpServlet) servlet;
    PrintWriterPatched pout = new PrintWriterPatched(response.getOutputStream());
    GspJspWriter gsptWriter = new GspJspWriter(pout);
    this.writer = gsptWriter;
    this.servletCtx = this.servlet.getServletContext();
    this.session = this.request.getSession(false);

    this.resolver = new VariableResolver() {

      public Object resolveVariable(String key) throws ELException
      {
        return findAttribute(key);
      }

    };
  }

  @Override
  public void forward(String page) throws ServletException, IOException
  {
    request.getRequestDispatcher(page).forward(request, response);
  }

  @Override
  public Object getAttribute(String key, int scope)
  {
    switch (scope) {
      case PageContext.REQUEST_SCOPE:
        return request.getAttribute(key);
      case PageContext.APPLICATION_SCOPE:
        return servletCtx.getAttribute(key);
      case PageContext.SESSION_SCOPE:
        if (session != null) {
          return session.getAttribute(key);
        }
        return null;
      default:
        return pageAttributes.get(key);
    }
  }

  @SuppressWarnings({ "rawtypes", "unchecked"})
  @Override
  public Enumeration getAttributeNamesInScope(int scope)
  {
    switch (scope) {
      case PageContext.REQUEST_SCOPE:
        return request.getAttributeNames();
      case PageContext.SESSION_SCOPE:
        if (session != null) {
          return session.getAttributeNames();
        }
        return IteratorUtils.asEnumeration(IteratorUtils.EMPTY_ITERATOR);
      case PageContext.APPLICATION_SCOPE:
        return servletCtx.getAttributeNames();
      default:
        return IteratorUtils.asEnumeration(pageAttributes.keySet().iterator());
    }

  }

  @SuppressWarnings("rawtypes")
  boolean contains(Enumeration en, String key)
  {
    for (; en.hasMoreElements();) {
      String o = (String) en.nextElement();
      if (StringUtils.equals(key, o) == true)
        return true;
    }
    return false;
  }

  @Override
  public int getAttributesScope(String key)
  {
    if (pageAttributes.containsKey(key) == true)
      return PageContext.PAGE_SCOPE;
    if (contains(request.getAttributeNames(), key) == true)
      return PageContext.REQUEST_SCOPE;
    if (session != null) {
      if (contains(session.getAttributeNames(), key) == true) {
        return PageContext.SESSION_SCOPE;
      }
    }
    if (contains(servletCtx.getAttributeNames(), key) == true)
      return PageContext.APPLICATION_SCOPE;
    return 0;
  }

  @Override
  public void setAttribute(String key, Object value, int scope)
  {
    if (value == null) {
      removeAttribute(key, scope);
      return;
    }
    switch (scope) {
      case PageContext.PAGE_SCOPE:
        pageAttributes.put(key, value);
        break;
      case PageContext.REQUEST_SCOPE:
        request.setAttribute(key, value);
        break;
      case PageContext.SESSION_SCOPE:
        if (session != null) {
          session.setAttribute(key, value);
        }
        break;
      case PageContext.APPLICATION_SCOPE:
        servletCtx.setAttribute(key, value);
        break;
      default:

        break;
    }
  }

  @Override
  public Object findAttribute(String key)
  {
    int scope = getAttributesScope(key);
    if (scope == 0)
      return null;
    return getAttribute(key, scope);
  }

  @Override
  public void removeAttribute(String key)
  {
    pageAttributes.remove(key);
    request.removeAttribute(key);
    if (session != null) {
      session.removeAttribute(key);
    }
    servletCtx.removeAttribute(key);
  }

  @Override
  public void removeAttribute(String key, int scope)
  {
    switch (scope) {
      case PageContext.PAGE_SCOPE:
        pageAttributes.remove(key);
        break;
      case PageContext.REQUEST_SCOPE:
        request.removeAttribute(key);
        break;
      case PageContext.SESSION_SCOPE:
        if (session != null) {
          session.removeAttribute(key);
        }
        break;
      case PageContext.APPLICATION_SCOPE:
        servletCtx.removeAttribute(key);
        break;
      default:
        break;
    }
  }

  @Override
  public void setAttribute(String key, Object value)
  {
    if (value == null) {
      pageAttributes.remove(key);
    } else {
      pageAttributes.put(key, value);
    }
  }

  @Override
  public Object getAttribute(String key)
  {
    return pageAttributes.get(key);
  }

  @Override
  public HttpSession getSession()
  {
    return request.getSession();
  }

  @Override
  public Object getPage()
  {
    return servlet;
  }

  @Override
  public Exception getException()
  {
    return null;
  }

  @Override
  public void include(String page, boolean flush) throws ServletException, IOException
  {
    if (flush == true) {
      if (topOfWriterStack != null) {
        if (topOfWriterStack instanceof BodyFlusher) {
          ((BodyFlusher) topOfWriterStack).flushBody();
        } else {
          // System.out.println("No Bodyflusher: " + topOfWriterStack.getClass());
        }
      }
      JspWriter out = getOut();
      if (out instanceof BodyFlusher) {
        ((BodyFlusher) out).flushBody();
      } else {
        // System.out.println("No Bodyflusher: " + topOfWriterStack.getClass());
      }
    } else {
      // System.out.println("no flush");
    }
    request.getRequestDispatcher(page).include(request, response);
  }

  @Override
  public void include(String page) throws ServletException, IOException
  {
    include(page, true);
  }

  @Override
  public ServletConfig getServletConfig()
  {
    return servlet.getServletConfig();
  }

  @Override
  public ServletContext getServletContext()
  {
    return servlet.getServletContext();
  }

  public HttpServletRequest getRequest()
  {
    return request;
  }

  public void setRequest(HttpServletRequest request)
  {
    this.request = request;
  }

  public HttpServletResponse getResponse()
  {
    return response;
  }

  public void setResponse(HttpServletResponse response)
  {
    this.response = response;
  }

  @Override
  public void handlePageException(Exception ex) throws ServletException, IOException
  {
    handlePageException((Throwable) ex);
  }

  @Override
  public void handlePageException(Throwable ex) throws ServletException, IOException
  {
    if (ex instanceof RuntimeException) {
      throw (RuntimeException) ex;
    }
    throw new ServletException(ex);
  }

  @Override
  public JspWriter getOut()
  {
    if (topOfWriterStack != null)
      return topOfWriterStack;
    return this.writer;
  }

  @Override
  public BodyContent pushBody()
  {
    // try {
    // getOut().flush();
    // } catch (IOException ex) {
    // throw new RuntimeException(ex);
    // }
    topOfWriterStack = new BodyContentImpl(getOut());
    return topOfWriterStack;
  }

  @Override
  public JspWriter pushBody(java.io.Writer writer)
  {
    topOfWriterStack = new BodyContentImpl(new GspJspWriter(new PrintWriterPatched(writer), JspWriter.NO_BUFFER), JspWriter.NO_BUFFER,
        false);
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
    } else {
      topOfWriterStack = null;
    }
    return parent;
  }

  @Override
  public void release()
  {
    if (topOfWriterStack != null) {
      if (topOfWriterStack instanceof BodyFlusher) {
        try {
          ((BodyFlusher) topOfWriterStack).flushBody();
        } catch (IOException ex) {
          // TODO handle ex
        }
      }
    }
    JspWriter out = getOut();
    if (out instanceof BodyFlusher) {
      try {
        ((BodyFlusher) out).flushBody();
        out.flush();
      } catch (IOException ex) {
        // TODO handle ex
      }

    } else {
      // System.out.println("No body flusher!");
    }

  }

  @Override
  public ExpressionEvaluator getExpressionEvaluator()
  {
    throw new UnsupportedOperationException("we have groovy templates => we do neither need nor provide an explicit ExpressionEvaluator");
  }

  @Override
  public VariableResolver getVariableResolver()
  {
    return this.resolver;
  }

  public ELContext getELContext()
  {
    return null;
  }

}
