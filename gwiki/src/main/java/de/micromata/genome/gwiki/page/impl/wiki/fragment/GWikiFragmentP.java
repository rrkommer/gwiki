////////////////////////////////////////////////////////////////////////////
//
// Copyright (C) 2010-2013 Micromata GmbH / Roger Rene Kommer
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
// http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.
//
////////////////////////////////////////////////////////////////////////////

package de.micromata.genome.gwiki.page.impl.wiki.fragment;

import java.util.List;

import de.micromata.genome.gwiki.page.GWikiContext;

/**
 * Represents p html element.
 * 
 * @author Roger Rene Kommer (r.kommer@micromata.de)
 * 
 */
public class GWikiFragmentP extends GWikiFragmentChildsBase
{

  private static final long serialVersionUID = -8245596367479475761L;

  /**
   * TODO later. Create Class HtmlAttrs witch contains class.
   */
  private String addClass;

  public GWikiFragmentP()
  {
    // super("<p/>\n");
  }

  public GWikiFragmentP(List<GWikiFragment> childs)
  {
    super(childs);
  }

  @Override
  public void getSource(StringBuilder sb)
  {
    sb.append("\n\n");
    getChildSouce(sb);
  }

  // @Override
  public boolean render(GWikiContext ctx)
  {
    // if (RenderModes.ForRichTextEdit.isSet(ctx.getRenderMode()) == true) {
    // renderChilds(ctx);
    // ctx.append("<br/>\n<br/>\n");
    // return true;
    // }
    if (childs != null && childs.size() > 0) {
      if (addClass != null) {
        ctx.append("<p class=\"").append(addClass).append("\">");
      } else {
        ctx.append("<p>");
      }
      renderChilds(ctx);
      ctx.append("</p>\n");
    } else {
      if (addClass != null) {
        ctx.append("<p class=\"").append(addClass).append("\"/>\n");
      } else {
        ctx.append("<p/>\n");
      }
    }
    return true;
  }

  public String getAddClass()
  {
    return addClass;
  }

  public void setAddClass(String addClass)
  {
    this.addClass = addClass;
  }
}
