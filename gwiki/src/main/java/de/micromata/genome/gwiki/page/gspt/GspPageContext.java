/////////////////////////////////////////////////////////////////////////////
//
// Project   DHL-ParcelOnlinePostage
//
// Author    roger@micromata.de
// Created   19.11.2006
// Copyright Micromata 19.11.2006
//
/////////////////////////////////////////////////////////////////////////////
package de.micromata.genome.gwiki.page.gspt;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.el.ELContext;
import javax.servlet.ServletContext;
import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.jsp.PageContext;

import org.apache.taglibs.standard.tag.common.core.ImportSupport;

/**
 * PageContext with extensions for GWARs
 * 
 * @author roger@micromata.de
 * 
 */
public class GspPageContext extends ChildPageContext
{
  private ServletContext servletContext;

  private HttpServletRequest newRootRequest = null;

  private Map<String, Object> attributes = new HashMap<String, Object>();

  public GspPageContext(final PageContext parentPageContext)
  {
    this(parentPageContext, false);
  }

  public GspPageContext(final PageContext parentPageContext, boolean useParentPageScope)
  {
    super(parentPageContext);
    if (useParentPageScope == true) {
      attributes = new PageScopeMap(parentPageContext);
    }
    this.servletContext = parentPageContext.getServletContext();
  }

  public String globalUrl(String url)
  {
    if (ImportSupport.isAbsoluteUrl(url))
      return url;
    if (url.startsWith("/") == false)
      return url;
    HttpServletResponse resp = getHttpResponse();
    // if (this.getServletContext() instanceof GServletContext) {
    // GServletContext gsc = (GServletContext) this.getServletContext();
    // return resp.encodeURL(gsc.getParentContextPath() + url);
    // }
    HttpServletRequest request = (HttpServletRequest) getParentPageContext().getRequest();

    return resp.encodeURL(request.getContextPath() + url);
  }

  /**
   * Encode a a GWAR local Url
   * 
   * @param url
   * @return a absolute URL
   */
  public String localUrl(String url)
  {
    if (ImportSupport.isAbsoluteUrl(url))
      return url;

    if (url.startsWith("/") == false)
      return url;
    HttpServletRequest request = getHttpRequest();
    HttpServletResponse resp = getHttpResponse();
    String ret = resp.encodeURL(request.getContextPath() + url);
    return ret;
  }

  public void sendRedirect(String newUrl) throws IOException
  {
    getHttpResponse().sendRedirect(newUrl);
  }

  @Override
  public ServletRequest getRequest()
  {
    if (newRootRequest != null)
      return newRootRequest;
    return parentPageContext.getRequest();
  }

  public HttpServletRequest getHttpRequest()
  {
    return (HttpServletRequest) getRequest();
  }

  public HttpServletResponse getHttpResponse()
  {
    return (HttpServletResponse) getResponse();
  }

  @Override
  public ServletContext getServletContext()
  {
    return servletContext;
  }

  // not supported in gwiki
  // public void gsptInclude(String file, Script script) throws ServletException, IOException
  // {
  // GsptPageServlet servl = (GsptPageServlet) parentPageContext.getRequest().getAttribute("gsptServlet");
  // try {
  // servl.processPage(file, this, script);
  // } catch (Exception ex) {
  // if (ex instanceof IOException)
  // throw (IOException) ex;
  // if (ex instanceof ServletException)
  // throw new RuntimeException(ex.getClass().getName() + ": " + ex.getMessage(), ex.getCause());
  // ;
  // throw new RuntimeException("cannot service gsptInclude '" + file + "'", ex);
  // }
  // // pageContext.include(file, false);
  // }

  @Override
  public Object findAttribute(String key)
  {
    if (attributes.containsKey(key) == true)
      return attributes.get(key);
    return parentPageContext.findAttribute(key);
  }

  @Override
  public Object getAttribute(String key)
  {
    if (attributes.containsKey(key) == true)
      return attributes.get(key);
    return parentPageContext.getAttribute(key);
  }

  @Override
  public Object getAttribute(String key, int scope)
  {
    if (PageContext.PAGE_SCOPE == scope) {
      if (attributes.containsKey(key) == true)
        return attributes.get(key);
    }
    return parentPageContext.getAttribute(key, scope);
  }

  @Override
  public int getAttributesScope(String key)
  {
    if (attributes.containsKey(key) == true)
      return PageContext.PAGE_SCOPE;
    return parentPageContext.getAttributesScope(key);
  }

  @Override
  public void removeAttribute(String key)
  {
    if (attributes.containsKey(key) == true) {
      attributes.remove(key);
      return;
    }
    parentPageContext.removeAttribute(key);
  }

  @Override
  public void removeAttribute(String key, int scope)
  {
    if (PageContext.PAGE_SCOPE == scope) {
      if (attributes.containsKey(key) == true) {
        attributes.remove(key);
        return;
      }
    }
    parentPageContext.removeAttribute(key, scope);
  }

  @Override
  public void setAttribute(String key, Object value)
  {
    attributes.put(key, value);
    // parentPageContext.setAttribute(key, value);
  }

  @Override
  public void setAttribute(String key, Object value, int scope)
  {
    if (PageContext.PAGE_SCOPE == scope) {
      attributes.put(key, value);
      return;
    }
    parentPageContext.setAttribute(key, value, scope);
  }

  public void setNewRootRequest(HttpServletRequest newRootRequest)
  {
    this.newRootRequest = newRootRequest;
  }

  public void setServletContext(ServletContext servletContext)
  {
    this.servletContext = servletContext;
  }

  @Override
  public PageContext getParentPageContext()
  {
    return parentPageContext;
  }

  public void setParentPageContext(PageContext parentPageContext)
  {
    this.parentPageContext = parentPageContext;
  }

  public Map<String, Object> getAttributes()
  {
    return attributes;
  }

  public ELContext getELContext()
  {
    return null;
  }

}
