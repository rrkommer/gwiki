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

package de.micromata.genome.gwiki.page.impl.i18n;

import java.util.Locale;

import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.jstl.core.Config;
import javax.servlet.jsp.jstl.fmt.LocalizationContext;

import de.micromata.genome.gwiki.model.GWikiPropKeys;
import de.micromata.genome.gwiki.model.filter.GWikiFilterChain;
import de.micromata.genome.gwiki.model.filter.GWikiServeElementFilter;
import de.micromata.genome.gwiki.model.filter.GWikiServeElementFilterEvent;
import de.micromata.genome.gwiki.page.GWikiContext;

public class GWikiI18nPageRenderFilter implements GWikiServeElementFilter
{

  public static final String CTAG_PAGE_FMT_LOCALE_KEY = Config.FMT_LOCALE + ".page";

  public static final String CTAG_PAGE_FMT_LOCALIZATION_KEY = Config.FMT_LOCALIZATION_CONTEXT + ".page";

  @Override
  public Void filter(GWikiFilterChain<Void, GWikiServeElementFilterEvent, GWikiServeElementFilter> chain, GWikiServeElementFilterEvent event)
  {
    GWikiContext wikiContext = event.getWikiContext();

    for (String i18nm : event.getElement().getElementInfo().getProps().getStringList(GWikiPropKeys.I18NMODULES)) {
      wikiContext.getWikiWeb().getI18nProvider().addTranslationElement(wikiContext, i18nm);
    }
    Locale loc = wikiContext.getWikiWeb().getAuthorization().getCurrentUserLocale(wikiContext);
    if (loc == null) {
      loc = Locale.getDefault();
    }
    PageContext pageContext = wikiContext.getCreatePageContext();
    Object prevfml = pageContext.getAttribute(CTAG_PAGE_FMT_LOCALE_KEY);
    Object prevlocaiz = pageContext.getAttribute(CTAG_PAGE_FMT_LOCALIZATION_KEY);
    try {
      pageContext.setAttribute(CTAG_PAGE_FMT_LOCALE_KEY, loc);
      pageContext
          .setAttribute(CTAG_PAGE_FMT_LOCALIZATION_KEY, new LocalizationContext(new GWikiI18nResourcenBundle(wikiContext, loc), loc));
      // wikiContext.getPageContext().setAttribute(Config.FMT_LOCALIZATION_CONTEXT,
      // new LocalizationContext(new GWikiI18nResourcenBundle(wikiContext, loc), loc));
      return chain.nextFilter(event);
    } finally {
      pageContext.setAttribute(CTAG_PAGE_FMT_LOCALE_KEY, prevfml);
      pageContext.setAttribute(CTAG_PAGE_FMT_LOCALIZATION_KEY, prevlocaiz);
    }
  }
}
