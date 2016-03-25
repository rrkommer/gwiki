package de.micromata.genome.gwiki.page.impl.wiki.fragment;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import de.micromata.genome.gwiki.model.GWikiAuthorizationRights;
import de.micromata.genome.gwiki.page.impl.wiki.GWikiCompileTimeMacro;
import de.micromata.genome.gwiki.page.impl.wiki.GWikiCompileTimeMacroBase;
import de.micromata.genome.gwiki.page.impl.wiki.GWikiMacroFragment;
import de.micromata.genome.gwiki.page.impl.wiki.GWikiMacroInfo;
import de.micromata.genome.gwiki.page.impl.wiki.GWikiMacroRenderFlags;
import de.micromata.genome.gwiki.page.impl.wiki.MacroInfo;
import de.micromata.genome.gwiki.page.impl.wiki.parser.GWikiWikiParserContext;
import de.micromata.genome.gwiki.page.impl.wiki.parser.GWikiWikiTokens;

@MacroInfo(info = "The macro html marks a region of text as pure HTML.<br/>"
    + "To save a page containing the html macro the user requires the right GWIKI_EDITHTML.",
    renderFlags = { GWikiMacroRenderFlags.NewLineAfterStart,
        GWikiMacroRenderFlags.NewLineBeforeEnd,
        GWikiMacroRenderFlags.NoWrapWithP, GWikiMacroRenderFlags.ContainsTextBlock })
public class GWikiUnsecureHtmlMacro extends GWikiCompileTimeMacroBase implements GWikiCompileTimeMacro
{

  private static final long serialVersionUID = 6651596226823444417L;

  public GWikiUnsecureHtmlMacro(GWikiMacroInfo macroInfo)
  {
    super(macroInfo);
    setRenderModesFromAnnot();
  }

  @Override
  protected GWikiAuthorizationRights requiredRight()
  {
    return GWikiAuthorizationRights.GWIKI_EDITHTML;
  }

  @Override
  public boolean evalBody()
  {
    return false;
  }

  @Override
  public boolean hasBody()
  {
    return true;
  }

  @Override
  public Collection<GWikiFragment> getFragments(GWikiMacroFragment macroFrag, GWikiWikiTokens tks,
      GWikiWikiParserContext ctx)
  {
    List<GWikiFragment> fragl = new ArrayList<GWikiFragment>();
    macroFrag.addChild(new GWikiFragmentUnsecureHtml(macroFrag.getAttrs().getBody()));
    fragl.add(macroFrag);
    return fragl;
  }

}