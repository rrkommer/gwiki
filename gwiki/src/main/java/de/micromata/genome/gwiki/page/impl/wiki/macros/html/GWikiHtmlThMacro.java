package de.micromata.genome.gwiki.page.impl.wiki.macros.html;

import de.micromata.genome.gwiki.page.impl.wiki.MacroInfo;
import de.micromata.genome.gwiki.page.impl.wiki.macros.GWikiHtmlBodyTagMacro;

/**
 * 
 * @author Roger Rene Kommer (r.kommer.extern@micromata.de)
 *
 */
@MacroInfo(info = "Create an html th element")
public class GWikiHtmlThMacro extends GWikiHtmlBodyTagMacro
{
  public GWikiHtmlThMacro()
  {
    super(getStandardNestedBodyRenderFlags());
  }
}
