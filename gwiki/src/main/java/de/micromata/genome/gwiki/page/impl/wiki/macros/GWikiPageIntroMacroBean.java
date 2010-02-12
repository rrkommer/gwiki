/////////////////////////////////////////////////////////////////////////////
//
// Project   DHL-ParcelOnlinePostage
//
// Author    roger@micromata.de
// Created   14.12.2009
// Copyright Micromata 14.12.2009
//
/////////////////////////////////////////////////////////////////////////////
package de.micromata.genome.gwiki.page.impl.wiki.macros;

import de.micromata.genome.gwiki.page.GWikiContext;
import de.micromata.genome.gwiki.page.impl.wiki.GWikiBodyEvalMacro;
import de.micromata.genome.gwiki.page.impl.wiki.GWikiMacroBean;
import de.micromata.genome.gwiki.page.impl.wiki.GWikiMacroRenderFlags;
import de.micromata.genome.gwiki.page.impl.wiki.GWikiMacroRte;
import de.micromata.genome.gwiki.page.impl.wiki.MacroAttributes;
import de.micromata.genome.gwiki.utils.html.Html2WikiTransformInfo;
import de.micromata.genome.gwiki.utils.html.Html2WikiTransformInfo.AttributeMatcher;
import de.micromata.genome.util.matcher.EqualsMatcher;

/**
 * Render a pageIntro section.
 * 
 * @author Roger Rene Kommer (r.kommer@micromata.de)
 * 
 */
public class GWikiPageIntroMacroBean extends GWikiMacroBean implements GWikiBodyEvalMacro, GWikiMacroRte
{

  private static final long serialVersionUID = -3030397042733595461L;

  private static Html2WikiTransformInfo transformInfo = new Html2WikiTransformInfo("div", "pageintro", GWikiPageIntroMacroBean.class);
  static {
    AttributeMatcher am = new AttributeMatcher();
    am.setName("class");
    am.setValueMatcher(new EqualsMatcher<String>("wikiPageIntro"));
    transformInfo.getAttributeMatcher().add(am);
  }

  public GWikiPageIntroMacroBean()
  {
    setRenderModes(GWikiMacroRenderFlags.combine(GWikiMacroRenderFlags.NewLineAfterStart, GWikiMacroRenderFlags.NewLineBeforeEnd));
  }

  @Override
  public boolean renderImpl(GWikiContext ctx, MacroAttributes attrs)
  {
    ctx.append("<div class=\"wikiPageIntro\">");
    if (attrs.getChildFragment() != null) {
      attrs.getChildFragment().render(ctx);
    }
    ctx.append("</div>");
    return true;
  }

  public Html2WikiTransformInfo getTransformInfo()
  {
    return transformInfo;
  }

  @Override
  public boolean evalBody()
  {
    return true;
  }

}
