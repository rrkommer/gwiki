/////////////////////////////////////////////////////////////////////////////
//
// Project   DHL-ParcelOnlinePostage
//
// Author    roger@micromata.de
// Created   26.11.2009
// Copyright Micromata 26.11.2009
//
/////////////////////////////////////////////////////////////////////////////
package de.micromata.genome.gwiki.page.impl.i18n;

import java.util.Locale;

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

  public Void filter(GWikiFilterChain<Void, GWikiServeElementFilterEvent, GWikiServeElementFilter> chain, GWikiServeElementFilterEvent event)
  {
    GWikiContext wikiContext = event.getWikiContext();

    wikiContext.getWikiWeb().getI18nProvider().addTranslationElement(wikiContext, "edit/StandardI18n");
    for (String i18nm : event.getElement().getElementInfo().getProps().getStringList(GWikiPropKeys.I18NMODULES)) {
      wikiContext.getWikiWeb().getI18nProvider().addTranslationElement(wikiContext, i18nm);
    }
    Locale loc = wikiContext.getWikiWeb().getAuthorization().getCurrentUserLocale(wikiContext);
    if (loc == null) {
      loc = Locale.getDefault();
    }
    wikiContext.getCreatePageContext().setAttribute(CTAG_PAGE_FMT_LOCALE_KEY, loc);
    wikiContext.getCreatePageContext().setAttribute(CTAG_PAGE_FMT_LOCALIZATION_KEY,
        new LocalizationContext(new GWikiI18nResourcenBundle(wikiContext, loc), loc));
    // wikiContext.getPageContext().setAttribute(Config.FMT_LOCALIZATION_CONTEXT,
    // new LocalizationContext(new GWikiI18nResourcenBundle(wikiContext, loc), loc));
    return chain.nextFilter(event);
  }
}
