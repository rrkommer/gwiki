package de.micromata.genome.gwiki.page;

/**
 * Utitilies for dealing with GWikiContext.
 * 
 * @author roger
 * 
 */
public class GWikiContextUtils
{
  public static void renderRequiredJs(GWikiContext wikiContext)
  {
    for (String s : wikiContext.getRequiredJs()) {
      wikiContext.append("<script type=\"text/javascript\" src=\"" + wikiContext.localUrl(s) + "\"></script>\n");
    }
  }

  public static void renderRequiredCss(GWikiContext wikiContext)
  {
    for (String s : wikiContext.getRequiredCss()) {
      wikiContext.append("<link rel=\"stylesheet\" type=\"text/css\"  src=\"" + wikiContext.localUrl(s) + "\"/>\n");
    }
  }
}
