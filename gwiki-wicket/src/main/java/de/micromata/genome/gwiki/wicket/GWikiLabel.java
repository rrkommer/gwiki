package de.micromata.genome.gwiki.wicket;

import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.model.Model;

import de.micromata.genome.gwiki.model.GWikiElement;
import de.micromata.genome.gwiki.model.GWikiWeb;
import de.micromata.genome.gwiki.page.GWikiContext;
import de.micromata.genome.gwiki.page.GWikiStandaloneContext;

/**
 * Label fetched from gwiki fragment.
 * 
 * @author Roger Rene Kommer (r.kommer@micromata.de)
 * 
 */
public class GWikiLabel extends Label
{

  private static final long serialVersionUID = -3802667568734694375L;

  public GWikiLabel(String id, final String wikiPage)
  {

    super(id, new Model<String>() {

      private static final long serialVersionUID = -7289059256589066359L;

      @Override
      public String getObject()
      {
        return getWikiPage(wikiPage);
      }

    });
    setEscapeModelStrings(false);
  }

  public static String getWikiPage(String pageId)
  {

    GWikiWeb wikiWeb = GWikiWeb.getWiki();
    GWikiElement el = wikiWeb.getElement(pageId);

    GWikiStandaloneContext wikiContext = GWikiStandaloneContext.create();
    try {
      GWikiContext.setCurrent(wikiContext);
      el.serve(wikiContext);
      String sout = wikiContext.getOutString();
      return sout;
    } finally {
      GWikiContext.setCurrent(null);
    }
  }

}
