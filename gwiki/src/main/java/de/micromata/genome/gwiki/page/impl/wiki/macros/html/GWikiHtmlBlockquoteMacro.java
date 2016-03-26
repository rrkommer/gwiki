package de.micromata.genome.gwiki.page.impl.wiki.macros.html;

import de.micromata.genome.gwiki.page.impl.wiki.MacroInfo;
import de.micromata.genome.gwiki.page.impl.wiki.macros.GWikiHtmlBodyRteTagMacro;

/**
 * 
 * @author Roger Rene Kommer (r.kommer.extern@micromata.de)
 *
 */
@MacroInfo(info = "creates an blockquote element.")
public class GWikiHtmlBlockquoteMacro extends GWikiHtmlBodyRteTagMacro
{
  public GWikiHtmlBlockquoteMacro()
  {
    super(getStandardNestedBodyRenderFlags());
  }
}
