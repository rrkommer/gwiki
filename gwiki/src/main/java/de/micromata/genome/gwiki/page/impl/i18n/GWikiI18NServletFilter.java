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

package de.micromata.genome.gwiki.page.impl.i18n;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.jsp.jstl.core.Config;
import javax.servlet.jsp.jstl.fmt.LocalizationContext;

import org.apache.commons.lang3.StringUtils;

import de.micromata.genome.gwiki.model.GWikiWeb;
import de.micromata.genome.gwiki.web.GWikiServlet;
import de.micromata.genome.util.types.Converter;
import de.micromata.genome.util.types.Pair;

/**
 * Servlet Filter for using GWiki I18N Modules.
 * 
 * @author Roger Rene Kommer (r.kommer@micromata.de)
 * 
 */
public class GWikiI18NServletFilter implements Filter
{
  public static ThreadLocal<Pair<HttpServletRequest, HttpServletResponse>> HTTPCTX = new ThreadLocal<Pair<HttpServletRequest, HttpServletResponse>>();

  private List<String> modules;

  public static final String REQUEST_SCOPE_SUFFIX = ".request";

  public static final String LOCALIZATION_KEY = Config.FMT_LOCALIZATION_CONTEXT + REQUEST_SCOPE_SUFFIX;

  public static final String LOC_KEY = Config.FMT_LOCALE + REQUEST_SCOPE_SUFFIX;

  public void initWiki(HttpServletRequest req, HttpServletResponse resp)
  {
    if (GWikiServlet.INSTANCE == null) {
      throw new RuntimeException("Cannot initialize GWikiSnippets because no GWikiServlet.INSTANCE can be found");
    }
    GWikiWeb wikiWeb = GWikiWeb.get();

    if (wikiWeb == null) {
      GWikiServlet.INSTANCE.initWiki(req, resp);
      wikiWeb = GWikiWeb.get();
    }
  }

  /*
   * (non-Javadoc)
   * 
   * @see javax.servlet.Filter#doFilter(javax.servlet.ServletRequest, javax.servlet.ServletResponse, javax.servlet.FilterChain)
   */
  public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException
  {
    HttpServletRequest hreq = (HttpServletRequest) request;
    HttpServletResponse hresp = (HttpServletResponse) response;
    HTTPCTX.set(Pair.make(hreq, hresp));
    initWiki(hreq, hresp);
    Locale loc = hreq.getLocale();
    Object ploc = request.getAttribute(LOC_KEY);
    if (ploc instanceof Locale) {
      loc = (Locale) ploc;
    }
    LocalizationContext prevLocContext = null;
    Object plc = hreq.getAttribute(LOCALIZATION_KEY);
    if (plc instanceof LocalizationContext) {
      prevLocContext = (LocalizationContext) plc;
    }

    Object prevfml = hreq.getAttribute(LOC_KEY);
    Object prevlocaiz = hreq.getAttribute(LOCALIZATION_KEY);
    try {
      hreq.setAttribute(LOC_KEY, loc);
      hreq.setAttribute(LOCALIZATION_KEY, new LocalizationContext(new GWikiI18NCombinedResourceBundle(loc, prevLocContext, modules), loc));

      chain.doFilter(request, response);
    } finally {
      hreq.setAttribute(LOC_KEY, prevfml);
      hreq.setAttribute(LOCALIZATION_KEY, prevlocaiz);
      HTTPCTX.set(null);
    }
  }

  /*
   * (non-Javadoc)
   * 
   * @see javax.servlet.Filter#init(javax.servlet.FilterConfig)
   */
  public void init(FilterConfig filterConfig) throws ServletException
  {

    String moduless = filterConfig.getInitParameter("I18NModules");
    if (StringUtils.isEmpty(moduless) == true) {
      throw new ServletException("Filter needs init parameter I18NModules");
    }
    modules = Converter.parseStringTokens(moduless, ", ", false);
    if (modules.isEmpty() == true) {
      throw new ServletException("Filter needs init parameter I18NModules with at least one module");
    }
  }

  /*
   * (non-Javadoc)
   * 
   * @see javax.servlet.Filter#destroy()
   */
  public void destroy()
  {

  }

}
