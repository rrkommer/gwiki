package de.micromata.genome.gwiki.page.impl.wiki.macros.html;

import de.micromata.genome.gwiki.page.impl.wiki.GWikiMacroRenderFlags;
import de.micromata.genome.gwiki.page.impl.wiki.MacroInfo;
import de.micromata.genome.gwiki.page.impl.wiki.macros.GWikiHtmlBodyRteTagMacro;

/**
 * 
 * @author Roger Rene Kommer (r.kommer.extern@micromata.de)
 *
 */
@MacroInfo(info = "create html span",
    renderFlags = { GWikiMacroRenderFlags.InTextFlow,
        GWikiMacroRenderFlags.RteSpan })
public class GWikiHtmlSpanMacro extends GWikiHtmlBodyRteTagMacro
{

}
