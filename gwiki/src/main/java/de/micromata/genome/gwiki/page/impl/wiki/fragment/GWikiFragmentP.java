/////////////////////////////////////////////////////////////////////////////
//
// Project   DHL-ParcelOnlinePostage
//
// Author    roger@micromata.de
// Created   31.12.2009
// Copyright Micromata 31.12.2009
//
/////////////////////////////////////////////////////////////////////////////
package de.micromata.genome.gwiki.page.impl.wiki.fragment;

import de.micromata.genome.gwiki.page.GWikiContext;
import de.micromata.genome.gwiki.page.RenderModes;

/**
 * Represents p html element.
 * 
 * @author Roger Rene Kommer (r.kommer@micromata.de)
 * 
 */
public class GWikiFragmentP extends GWikiFragmentHtml
{

  private static final long serialVersionUID = -8245596367479475761L;

  public GWikiFragmentP()
  {
    super("<p/>\n");
  }

  @Override
  public void getSource(StringBuilder sb)
  {
    sb.append("\n\n");
  }

  @Override
  public boolean render(GWikiContext ctx)
  {
    if (RenderModes.ForRichTextEdit.isSet(ctx.getRenderMode()) == true) {
      ctx.append("<br/>\n<br/>\n");
      return true;
    }
    return super.render(ctx);
  }
}
