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

import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.jstl.fmt.LocalizationContext;

import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;

import de.micromata.genome.gwiki.model.GWikiElement;
import de.micromata.genome.gwiki.model.GWikiWeb;
import de.micromata.genome.gwiki.model.logging.GWikiLog;
import de.micromata.genome.gwiki.page.GWikiContext;
import de.micromata.genome.gwiki.page.impl.GWikiI18nElement;
import de.micromata.genome.gwiki.web.GWikiServlet;

/**
 * @author Roger Rene Kommer (r.kommer@micromata.de)
 * 
 */
public class GWikiI18NCombinedResourceBundle extends ResourceBundle
{
  private Locale locale;

  private LocalizationContext prevLocContext;

  private List<String> modules;

  public GWikiI18NCombinedResourceBundle(Locale locale, LocalizationContext prevLocContext, List<String> modules)
  {
    super();
    this.locale = locale;
    this.prevLocContext = prevLocContext;
    this.modules = modules;
  }

  @Override
  public Enumeration<String> getKeys()
  {
    for (String module : modules) {
      GWikiElement el = GWikiWeb.get().findElement(module);
      if (el == null) {
        GWikiLog.warn("I18N Module not found: " + module);
        continue;
      } 
      if ((el instanceof GWikiI18nElement) == false) {
        GWikiLog.warn("Element is not a I18N Module: " + module);
        continue;
      }
      GWikiI18nElement i18nel = (GWikiI18nElement) el;
      return Collections.enumeration(i18nel.getKeys(locale.getLanguage()));
    }
    
    return null;
  }

  protected Object decorateWiki(GWikiI18nElement i18nel, String key, Object value)
  {
    if ((value instanceof String) == false && GWikiI18NServletFilter.HTTPCTX.get() == null) {
      return value;
    }
    if (GWikiWeb.get() == null) {
      return value;
    }
    HttpServletRequest req = GWikiI18NServletFilter.HTTPCTX.get().getFirst();
    if (ObjectUtils.toString(req.getAttribute("gwiki18ndeco")).equals("true") == false) {
      return value;
    }
    if (GWikiMessageTag.getDomId4I18N(req, key) != null) {
      return value;
    }
    GWikiWeb wikiWeb = GWikiWeb.get();
    GWikiContext wikiContext = new GWikiContext(wikiWeb, GWikiServlet.INSTANCE, req, GWikiI18NServletFilter.HTTPCTX.get().getSecond());
    if (GWikiWeb.get().getAuthorization().isAllowToEdit(wikiContext, i18nel.getElementInfo()) == false) {
      return value;
    }
    String contextPath = wikiWeb.getContextPath();
    if (StringUtils.isEmpty(contextPath) == true) {
      contextPath = wikiContext.getRealContextPath();
    }
    String gewikiBase = contextPath + wikiWeb.getServletPath();
    String sval = (String) value;
    String ret = "<span class=\"gwiki18nk\" oncontextmenu=\"return gwikiI18NCtxMenu(this, '"
        + i18nel.getElementInfo().getId()
        + "', '"
        + key
        + "', '"
        + req.getRequestURI()
        + "', '"
        + gewikiBase
        + "')\">"
        + sval
        + "</span>";
    return ret;
  }

  protected Object getFromWiki(String key)
  {
    GWikiWeb wikiWeb = GWikiWeb.get();

    if (wikiWeb == null) {
      return null;
    }
    for (String module : modules) {
      GWikiElement el = wikiWeb.findElement(module);
      if (el == null) {
        GWikiLog.warn("I18N Module not found: " + module);
        continue;
      }
      if ((el instanceof GWikiI18nElement) == false) {
        GWikiLog.warn("Element is not a I18N Module: " + module);
        continue;
      }
      GWikiI18nElement i18nel = (GWikiI18nElement) el;

      String v = i18nel.getMessage(locale.getLanguage(), key);
      if (v != null) {
        return decorateWiki(i18nel, key, v);
      }
    }
    return null;
  }

  @Override
  protected Object handleGetObject(String key)
  {
    Object ret = getFromWiki(key);
    if (ret != null) {
      return ret;
    }
    if (prevLocContext == null) {
      return null;
    }
    return prevLocContext.getResourceBundle().getObject(key);
  }
}
