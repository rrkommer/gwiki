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

/*
 * This implemention is derived from: 
 * 
 * Copyright 1999,2004 The Apache Software Foundation.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package de.micromata.genome.gwiki.web.tags;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.tagext.BodyTagSupport;

import org.apache.taglibs.standard.resources.Resources;
import org.apache.taglibs.standard.tag.common.core.ImportSupport;
import org.apache.taglibs.standard.tag.common.core.ParamParent;
import org.apache.taglibs.standard.tag.common.core.ParamSupport;
import org.apache.taglibs.standard.tag.common.core.Util;

import de.micromata.genome.gwiki.model.GWikiWeb;
import de.micromata.genome.gwiki.page.GWikiContext;
import de.micromata.genome.gwiki.page.GWikiContextUtils;

/**
 * Tag for url rendering to other pages.
 * 
 * @author Roger Rene Kommer (r.kommer@micromata.de)
 * 
 */
public class GWikiUrlTag extends BodyTagSupport implements ParamParent
{

  // *********************************************************************
  // Protected state

  /**
   * 
   */
  private static final long serialVersionUID = -2949538047772043469L;

  protected String value; // 'value' attribute

  protected String context; // 'context' attribute

  // *********************************************************************
  // Private state

  protected String var; // 'var' attribute

  private int scope; // processed 'scope' attr

  private ParamSupport.ParamManager params; // added parameters

  // *********************************************************************
  // Constructor and initialization

  public GWikiUrlTag()
  {
    super();
    init();
  }

  private void init()
  {
    value = var = null;
    params = null;
    context = null;
    scope = PageContext.PAGE_SCOPE;
  }

  // *********************************************************************
  // Tag attributes known at translation time

  public void setVar(String var)
  {
    this.var = var;
  }

  public void setScope(String scope)
  {
    this.scope = Util.getScope(scope);
  }

  // *********************************************************************
  // Collaboration with subtags

  // inherit Javadoc
  @Override
  public void addParameter(String name, String value)
  {
    params.addParameter(name, value);
  }

  // *********************************************************************
  // Tag logic

  // resets any parameters that might be sent
  @Override
  public int doStartTag() throws JspException
  {
    params = new ParamSupport.ParamManager();
    return EVAL_BODY_BUFFERED;
  }

  // gets the right value, encodes it, and prints or stores it
  @Override
  public int doEndTag() throws JspException
  {
    String result; // the eventual result

    // add (already encoded) parameters
    String baseUrl = resolveUrl(value, context, pageContext);
    result = params.aggregateParams(baseUrl);

    // if the URL is relative, rewrite it
    if (!ImportSupport.isAbsoluteUrl(result)) {
      HttpServletResponse response = ((HttpServletResponse) pageContext.getResponse());
      result = response.encodeURL(result);
    }

    // store or print the output
    if (var != null) {
      pageContext.setAttribute(var, result, scope);
    } else {
      try {
        pageContext.getOut().print(result);
      } catch (java.io.IOException ex) {
        throw new JspTagException(ex.getMessage());
      }
    }

    return EVAL_PAGE;
  }

  // Releases any resources we may have (or inherit)
  @Override
  public void release()
  {
    init();
  }

  // *********************************************************************
  // Utility methods
  /**
   * /inc/{SKIN}/img/image.gif
   */
  public static String resolveUrl(String url, String context, PageContext pageContext) throws JspException
  {
    // don't touch absolute URLs
    if (ImportSupport.isAbsoluteUrl(url) == true) {
      return url;
    }
    if (url == null) {
      return "";
    }
    url = GWikiContextUtils.resolveSkinLink(url);
    GWikiContext wikiContext = GWikiContext.getCurrent();
    final String servletPath;
    if (wikiContext != null && wikiContext.getWikiWeb() != null) {
      servletPath = GWikiContext.getCurrent().getWikiWeb().getServletPath();
    } else {
      servletPath = GWikiWeb.get().getServletPath();
    }

    // normalize relative URLs against a context root
    HttpServletRequest request = (HttpServletRequest) pageContext.getRequest();
    if (context == null) {
      if (url.startsWith("/")) {
        return (request.getContextPath() + servletPath + url);
      } else {
        return url;
      }
    } else {
      if (!context.startsWith("/") || !url.startsWith("/")) {
        throw new JspTagException(Resources.getMessage("IMPORT_BAD_RELATIVE"));
      }
      return (context + servletPath + url);
    }
  }

  // for tag attribute
  public void setValue(String value) throws JspTagException
  {
    this.value = value;
  }

  // for tag attribute
  public void setContext(String context) throws JspTagException
  {
    this.context = context;
  }
}
