/////////////////////////////////////////////////////////////////////////////
//
// Project   DHL-ParcelOnlinePostage
//
// Author    roger@micromata.de
// Created   13.11.2009
// Copyright Micromata 13.11.2009
//
/////////////////////////////////////////////////////////////////////////////
package de.micromata.genome.gwiki.page.impl.wiki.macros;

import de.micromata.genome.gwiki.model.GWikiElementInfo;
import de.micromata.genome.gwiki.page.GWikiContext;
import de.micromata.genome.gwiki.page.RenderModes;
import de.micromata.genome.gwiki.page.impl.wiki.GWikiMacroBean;
import de.micromata.genome.gwiki.page.impl.wiki.MacroAttributes;

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
    String pageId = ctx.getWikiElement().getElementInfo().getId();
    String pageCommentUrl = ctx.localUrl("/admin/macros/pages/PageComment");
    pageCommentUrl += "?pageId=" + pageId;

    StringBuilder sb = new StringBuilder();
    sb.append("<div id='PageComments'></div>\n")
    //
        .append("<script>\n") //
        .append("$(document).ready(function(){\n")//
        // .append("alert('ready');\n")
        .append("var url = '").append(pageCommentUrl).append("';\n") //
        .append("var pc = document.getElementById('PageComments');\n")//
        .append("$(pc).load(url + '');\n")//
        .append("});\n") //
        .append("</script>\n") //
    ;
    ctx.append(sb.toString());
    // $(tdd).load(url +" #loaded_content",null, function(){$(togglerImg).attr("src",oldSrc);});
    // var url = ctx.localUrl("/admin/macros/page/PageComment");
    //    
    // var param = $(button).attr('id');
    // param = param.substring(2);
    // url = url + "?pk="+encodeURIComponent(param);
    // var td = document.createElement("td");
    // $(td).attr('colspan','7');
    // $(td).attr('style','background-color:#fff;');
    // $(td).attr('id','id_'+param);
    // var tr = document.createElement("tr");
    // $(tr).attr('class','hideme');
    // tr.appendChild(td);
    // $(currentRow).after(tr);
    // var id ='id_'+param;
    // var tdd = document.getElementById(id);

    // TODO Auto-generated method stub
    return false;
  }
}
