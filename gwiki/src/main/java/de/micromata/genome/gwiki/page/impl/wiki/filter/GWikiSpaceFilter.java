package de.micromata.genome.gwiki.page.impl.wiki.filter;

import java.io.IOException;

import org.apache.commons.lang.StringUtils;

import de.micromata.genome.gwiki.model.filter.GWikiFilterChain;
import de.micromata.genome.gwiki.model.filter.GWikiServeElementFilter;
import de.micromata.genome.gwiki.model.filter.GWikiServeElementFilterEvent;
import de.micromata.genome.gwiki.page.GWikiContext;

/**
 * 
 * @author Roger Rene Kommer (r.kommer.extern@micromata.de)
 *
 */
public class GWikiSpaceFilter implements GWikiServeElementFilter
{
  public static final String WIKI_SET_SPACE_PARAM = "_wikispaceset";

  @Override
  public Void filter(GWikiFilterChain<Void, GWikiServeElementFilterEvent, GWikiServeElementFilter> chain,
      GWikiServeElementFilterEvent event)
  {
    GWikiContext wikiContext = event.getWikiContext();
    String setspace = wikiContext.getRequestParameter(WIKI_SET_SPACE_PARAM);
    if (StringUtils.isNotBlank(setspace) == true) {
      String redid = wikiContext.getWikiWeb().getSpaces().switchUserSpace(wikiContext, setspace);
      if (redid != null) {
        try {
          wikiContext.getResponse().sendRedirect(wikiContext.localUrl(redid));
          return null;
          //          wikiContext.sendError(HttpServletResponse.SC_TEMPORARY_REDIRECT, redUrl);
        } catch (IOException ex) {
          ; // nothing
        }
      }
    }
    return chain.nextFilter(event);
  }

}
