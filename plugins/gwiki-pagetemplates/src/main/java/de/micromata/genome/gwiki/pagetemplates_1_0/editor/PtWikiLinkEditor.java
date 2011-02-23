////////////////////////////////////////////////////////////////////////////
// 
// Copyright (C) 2010 Micromata GmbH
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
package de.micromata.genome.gwiki.pagetemplates_1_0.editor;

import static de.micromata.genome.util.xml.xmlbuilder.Xml.attrs;
import static de.micromata.genome.util.xml.xmlbuilder.Xml.text;
import static de.micromata.genome.util.xml.xmlbuilder.html.Html.input;
import static de.micromata.genome.util.xml.xmlbuilder.html.Html.table;
import static de.micromata.genome.util.xml.xmlbuilder.html.Html.td;
import static de.micromata.genome.util.xml.xmlbuilder.html.Html.tr;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import de.micromata.genome.gwiki.model.GWikiElement;
import de.micromata.genome.gwiki.model.GWikiElementInfo;
import de.micromata.genome.gwiki.page.GWikiContext;
import de.micromata.genome.gwiki.page.GWikiStandaloneContext;
import de.micromata.genome.gwiki.page.RenderModes;
import de.micromata.genome.gwiki.page.impl.GWikiContent;
import de.micromata.genome.gwiki.page.impl.wiki.MacroAttributes;
import de.micromata.genome.gwiki.page.impl.wiki.fragment.GWikiFragmentLink;
import de.micromata.genome.gwiki.page.impl.wiki.macros.GWikiChildrenMacro;
import de.micromata.genome.gwiki.page.impl.wiki.macros.GWikiElementByChildOrderComparator;
import de.micromata.genome.gwiki.page.impl.wiki.macros.GWikiElementByIntPropComparator;
import de.micromata.genome.gwiki.page.impl.wiki.macros.GWikiElementByOrderComparator;
import de.micromata.genome.gwiki.page.impl.wiki.macros.GWikiElementByPropComparator;
import de.micromata.genome.gwiki.page.impl.wiki.parser.GWikiWikiParser;
import de.micromata.genome.util.xml.xmlbuilder.XmlElement;

/**
 * @author Christian Claus (c.claus@micromata.de)
 */
public class PtWikiLinkEditor extends PtWikiTextEditorBase
{

  private static final long serialVersionUID = 5901053792188232570L;

  public PtWikiLinkEditor(GWikiElement element, String sectionName, String editor)
  {
    super(element, sectionName, editor);
  }
  
  @Override
  public void prepareHeader(GWikiContext wikiContext) 
  {
    super.prepareHeader(wikiContext);
    wikiContext.getRequiredJs().add("/static/js/jstree/jquery.jstree.js");
  }
  
  /*
   * (non-Javadoc)
   * 
   * @see de.micromata.genome.gwiki.model.GWikiArtefaktBase#renderWithParts(de.micromata.genome.gwiki.page.GWikiContext)
   */
  @Override
  public boolean renderWithParts(GWikiContext ctx)
  {
    ctx.append("<p>" + ctx.getTranslated("gwiki.editor.hyperlink.headline") + "</p>");
    
    XmlElement inputFile = input( //
        attrs("id", sectionName + "_filechooser", "name", sectionName, "value", "http://", "size", "50"));
    
    XmlElement chooseButton = input( //
        attrs("value", "choose", "type", "button", "onclick", "$(\"#filechooser\").toggle()"));
    
    XmlElement inputTitle = input( //
        attrs("name", "title"));
    
    XmlElement table = table( //
        attrs()).nest( //
            tr( //
                td(text(ctx.getTranslated("gwiki.editor.hyperlink.address"))), //
                td(inputFile), //
                td(chooseButton) //
            ), //
            tr( //
                td(text(ctx.getTranslated("gwiki.editor.hyperlink.nicename"))), //
                td(inputTitle) //
            ) //
        );
    
    
    ctx.append(table.toString());
    
    MacroAttributes attrs = new MacroAttributes();
    attrs.getArgs().setStringValue("depth", "5");
    attrs.getArgs().setStringValue("sort", "title");
    attrs.getArgs().setStringValue("withPageIntro", "true");
    attrs.getArgs().setStringValue("page", "pub/en/Index");
    attrs.getArgs().setStringValue("type", "all");
    attrs.getArgs().setStringValue("viewAll", "true");

//    attrs.getArgs().setStringValue("type", "attachment");
//    GWikiChildrenMacro children = new GWikiChildrenMacro();
//    children.render(attrs, ctx);
    
//    GWikiWikiParser wkparse = new GWikiWikiParser();
//    GWikiContent content = wkparse.parse(ctx, "{children:all=true|sort=title|page=pub/en/Index}");
//    GWikiStandaloneContext sctx = GWikiStandaloneContext.create();
//    sctx.setRenderMode(RenderModes.combine(RenderModes.InMem));
//    content.render(sctx);
//    sctx.flush();
//    ctx.append(sctx.getOutString());
    
    GWikiElementInfo ei = ctx.getWikiWeb().findElementInfo("pub/en/Index");;

    if (ei != null) {
      ctx.append("<div id='filechooser' style='margin-top:20px; font-family:verdana; font-size: 10px; display:none'>");
      renderTree(ei, 0, ctx);
      ctx.append("</div>");
      
      
      ctx.append("<script type=\"text/javascript\">");
      ctx.append("$(function () {");
      ctx.append("  $(\"#filechooser\").jstree({");
      ctx.append("  \"themes\" : { \"theme\" : \"apple\", \"dots\" : false, \"icons\" : false },");
//      ctx.append("  \"ui: {select_limit\" : 1}");
      ctx.append("\"plugins\" : [ \"themes\", \"html_data\", \"ui\" ]");
      ctx.append("});");
      ctx.append("});");
      ctx.append("$(\"#filechooser\").jstree(\"set_theme\",\"apple\");");
      ctx.append("</script>");
      
      
          
    }
    
    return true;
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.micromata.genome.gwiki.page.impl.GWikiEditorArtefakt#onSave(de.micromata.genome.gwiki.page.GWikiContext)
   */
  public void onSave(GWikiContext ctx)
  {
    String target = ctx.getRequestParameter(sectionName);
    String title = ctx.getRequest().getParameter("title");
    
    if (GWikiFragmentLink.isGlobalUrl(target)) {
      
    } else {
      
    }
    
    GWikiFragmentLink link = new GWikiFragmentLink(target);
    
    if (StringUtils.isNotEmpty(title)) {
      link.setTitle(title);
    }
    
    updateSection(link.toString());
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.micromata.genome.gwiki.page.impl.GWikiEditorArtefakt#getTabTitle()
   */
  public String getTabTitle()
  {
    return "";
  }
  
  private void renderTree(GWikiElementInfo ei, int level, GWikiContext ctx)
  {
    int depth = 99;
    
    if (RenderModes.NoToc.isSet(ctx.getRenderMode()) == true) {
      return;
    }
    if (ctx.getWikiWeb().getAuthorization().isAllowToView(ctx, ei) == false) {
      return;
    }
    if (ei.isNoToc() == true) {
      return;
    }
    List<GWikiElementInfo> cl;

    cl = ctx.getElementFinder().getAllDirectChilds(ei);

    Collections.sort(cl, new GWikiElementByOrderComparator(new GWikiElementByPropComparator("TITLE")));

    if (cl.isEmpty() == true) {
      return;
    }
    
    String xmlidattr = "";
    if (level == 1) {
      xmlidattr = " id='" + ctx.genHtmlId("childrentoc") + "'";
    }
    
    ctx.append("\n<ul" + xmlidattr + ">\n");
    
    for (GWikiElementInfo ci : cl) {
      if (ctx.getWikiWeb().getAuthorization().isAllowToView(ctx, ci) == false) {
        continue;
      }
     
      String renderLocalUrl = "";
      GWikiFragmentLink link = new GWikiFragmentLink(ci.getId());
      renderLocalUrl = link.getTarget();
      String filename = renderLocalUrl.substring(renderLocalUrl.lastIndexOf("/") + 1);
      
        if (ctx.getElementFinder().getAllDirectChilds(ci).isEmpty()) {
          String id = sectionName + "_filechooser";
          ctx.append("\n<li>").append("<a style='cursor:pointer' onclick=\"document.getElementById('" + id + "').value='" + renderLocalUrl + "';" + "\">" + filename + "</a>");
        } else {
          ctx.append("\n<li>").append("<a href=\"#\">" + filename + "</a>");
        }
  
        if (level + 1 > depth) {
          ctx.append("</li>\n"); // close child
          continue;
        }
        renderTree(ci, level + 1, ctx);
        ctx.append("</li>\n"); // close child
    }
    ctx.append("\n</ul>\n");
  }

}
