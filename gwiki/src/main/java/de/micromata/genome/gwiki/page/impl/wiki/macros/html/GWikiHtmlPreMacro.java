package de.micromata.genome.gwiki.page.impl.wiki.macros.html;

import de.micromata.genome.gwiki.page.impl.wiki.GWikiMacroRenderFlags;
import de.micromata.genome.gwiki.page.impl.wiki.MacroInfo;
import de.micromata.genome.gwiki.page.impl.wiki.macros.GWikiHtmlBodyTagMacro;

/**
 * 
 * @author Roger Rene Kommer (r.kommer.extern@micromata.de)
 *
 */
@MacroInfo(info = "Create an html table pre",
    renderFlags = { GWikiMacroRenderFlags.NewLineAfterStart, GWikiMacroRenderFlags.NewLineBeforeEnd,
        GWikiMacroRenderFlags.TrimTextContent })
public class GWikiHtmlPreMacro extends GWikiHtmlBodyTagMacro
{
  @Override
  public boolean evalBody()
  {
    return false;
  }

}
