//
// Copyright (C) 2010-2016 Roger Rene Kommer & Micromata GmbH
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//  http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.
//

package de.micromata.genome.gwiki.page.impl.wiki.macros;

import de.micromata.genome.gwiki.model.GWikiElementInfo;
import de.micromata.genome.gwiki.page.GWikiContext;
import de.micromata.genome.gwiki.page.RenderModes;
import de.micromata.genome.gwiki.page.impl.wiki.GWikiMacroBean;
import de.micromata.genome.gwiki.page.impl.wiki.MacroAttributes;
import de.micromata.genome.gwiki.page.impl.wiki.MacroInfo;

/**
 * Macro to create a comment section inside a page.
 * 
 * @author Roger Rene Kommer (r.kommer@micromata.de)
 * 
 */
@MacroInfo(info = "Macro to create a comment section inside a page.")
public class GWikiPageCommentMacro extends GWikiMacroBean
{

  private static final long serialVersionUID = 7194212848694183135L;

  public static void doRender(GWikiContext ctx)
  {
    new GWikiPageCommentMacro().render(new MacroAttributes(), ctx);
  }

  @Override
  public boolean renderImpl(GWikiContext ctx, MacroAttributes attrs)
  {
    GWikiElementInfo comment = ctx.getWikiWeb().findElementInfo("admin/macros/pages/PageComment");
    if (comment == null) {
      return true;
    }

    if (ctx.getWikiWeb().getAuthorization().isAllowToView(ctx, comment) == false) {
      return true;
    }
    if (RenderModes.NoToc.isSet(ctx.getRenderMode()) == true) {
      return true;
    }
    String pageId = ctx.getCurrentElement().getElementInfo().getId();
    String pageCommentUrl = ctx.localUrl("/admin/macros/pages/PageComment");
    pageCommentUrl += "?pageId=" + pageId;// + "&wcembedded=true";

    StringBuilder sb = new StringBuilder();

    sb.append("<div id='PageComments'></div>\n")
        //
        .append("<script type=\"text/javascript\">\n") //
        .append("$(document).ready(function(){\n")//
        // .append("alert('ready');\n")
        .append("var url = '").append(pageCommentUrl).append("';\n") //
        .append("var pc = document.getElementById('PageComments');\n")//
        .append("$(pc).load(url + '', { '_ts': new Date().valueOf() } );\n")//
        .append("});\n") //
        .append("</script>\n") //
    ;
    ctx.append(sb.toString());
    return false;
  }
}
