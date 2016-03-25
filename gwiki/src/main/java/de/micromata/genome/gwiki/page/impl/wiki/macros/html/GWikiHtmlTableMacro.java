package de.micromata.genome.gwiki.page.impl.wiki.macros.html;

import de.micromata.genome.gwiki.page.impl.wiki.MacroInfo;
import de.micromata.genome.gwiki.page.impl.wiki.macros.GWikiHtmlBodyTagMacro;

/**
 * 
 * @author Roger Rene Kommer (r.kommer.extern@micromata.de)
 *
 */
@MacroInfo(info = "Create an html table")
public class GWikiHtmlTableMacro extends GWikiHtmlBodyTagMacro
{
  public GWikiHtmlTableMacro()
  {
    super(getStandardBodyRenderFlags());
  }
}
