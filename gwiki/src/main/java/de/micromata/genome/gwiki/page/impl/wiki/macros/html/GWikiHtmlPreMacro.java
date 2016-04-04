package de.micromata.genome.gwiki.page.impl.wiki.macros.html;

import de.micromata.genome.gwiki.page.impl.wiki.GWikiMacroRenderFlags;
import de.micromata.genome.gwiki.page.impl.wiki.MacroInfo;
import de.micromata.genome.gwiki.page.impl.wiki.macros.GWikiHtmlBodyRteTagMacro;

/**
 * 
 * @author Roger Rene Kommer (r.kommer.extern@micromata.de)
 *
 */
@MacroInfo(info = "Create an html table pre",
    renderFlags = { GWikiMacroRenderFlags.NewLineBlock,
        GWikiMacroRenderFlags.NoWrapWithP, GWikiMacroRenderFlags.ContainsTextBlock,
        GWikiMacroRenderFlags.TrimTextContent })
public class GWikiHtmlPreMacro extends GWikiHtmlBodyRteTagMacro
{
  @Override
  public boolean evalBody()
  {
    return false;
  }

}
