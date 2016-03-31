package de.micromata.genome.gwiki.page.impl.wiki.macros.html;

import de.micromata.genome.gwiki.page.impl.wiki.MacroInfo;
import de.micromata.genome.gwiki.page.impl.wiki.macros.GWikiHtmlBodyRteTagMacro;

/**
 * 
 * @author Roger Rene Kommer (r.kommer.extern@micromata.de)
 *
 */
@MacroInfo(info = "Create an html table")
public class GWikiHtmlTableMacro extends GWikiHtmlBodyRteTagMacro
{
  public GWikiHtmlTableMacro()
  {
    super(getStandardBodyRenderFlags());
  }
}
