package de.micromata.genome.gwiki.wicket;

import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.model.Model;

import de.micromata.genome.gwiki.model.GWikiElement;
import de.micromata.genome.gwiki.model.GWikiWeb;
import de.micromata.genome.gwiki.page.GWikiContext;
import de.micromata.genome.gwiki.page.GWikiStandaloneContext;
import de.micromata.genome.gwiki.page.impl.GWikiWikiPage;
import de.micromata.genome.gwiki.page.impl.wiki.macros.GWikiChunkMacro;

/**
 * Label fetched from gwiki fragment.
 * 
 * @author Roger Rene Kommer (r.kommer@micromata.de)
 * 
 */
public class GWikiLabel extends Label
{

  private static final long serialVersionUID = -3802667568734694375L;

  public GWikiLabel(String id, final String wikiPage, final String wikiPart)
  {
    this(id, wikiPage, wikiPart, null);
  }

  public GWikiLabel(String id, final String wikiPage)
  {
    this(id, wikiPage, null, null);
  }

  public GWikiLabel(String id, final String wikiPage, final String wikiPart, final String wikiChunk)
  {

    super(id, new Model<String>() {

      private static final long serialVersionUID = -7289059256589066359L;

      @Override
      public String getObject()
      {
        return getWikiPage(wikiPage, wikiPart, wikiChunk);
      }

    });
    setEscapeModelStrings(false);
  }

  public static String getWikiPage(String pageId, String partName, String chunk)
  {

    GWikiWeb wikiWeb = GWikiWeb.getWiki();
    GWikiElement el = wikiWeb.getElement(pageId);

    GWikiStandaloneContext wikiContext = GWikiStandaloneContext.create();
    if (partName != null) {
      wikiContext.setRequestAttribute(GWikiWikiPage.REQUESTATTR_GWIKIPART, partName);
    }
    if (chunk != null) {
      wikiContext.setRequestAttribute(GWikiChunkMacro.REQUESTATTR_GWIKICHUNK, chunk);
    }
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
