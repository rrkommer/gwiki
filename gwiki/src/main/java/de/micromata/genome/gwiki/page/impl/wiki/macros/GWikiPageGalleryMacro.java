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

package de.micromata.genome.gwiki.page.impl.wiki.macros;

import java.util.Collections;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import de.micromata.genome.gwiki.model.GWikiArtefakt;
import de.micromata.genome.gwiki.model.GWikiAttachment;
import de.micromata.genome.gwiki.model.GWikiElement;
import de.micromata.genome.gwiki.model.GWikiElementInfo;
import de.micromata.genome.gwiki.model.GWikiExecutableArtefakt;
import de.micromata.genome.gwiki.page.GWikiContext;
import de.micromata.genome.gwiki.page.impl.wiki.GWikiMacroBean;
import de.micromata.genome.gwiki.page.impl.wiki.MacroAttributes;
import de.micromata.genome.gwiki.page.impl.wiki.fragment.GWikiFragmentImage;

/**
 * Macro Create a gallery of pages.
 * 
 * @author Roger Rene Kommer (r.kommer@micromata.de)
 * 
 */
public class GWikiPageGalleryMacro extends GWikiMacroBean
{

  private static final long serialVersionUID = 3835612036105153588L;

  private int columns = 2;

  private String columnWidth = "300px";

  private String rowHeight = "200px";

  /**
   * Render only part with name.
   */
  private String part;

  private String parentPage;

  @Override
  public boolean renderImpl(GWikiContext ctx, MacroAttributes attrs)
  {
    GWikiElementInfo ci = ctx.getCurrentElement().getElementInfo();
    if (StringUtils.isNotEmpty(parentPage) == true) {
      ci = ctx.getWikiWeb().findElementInfo(parentPage);
      if (ci == null) {
        // TODO error
        return true;
      }
    }
    List<GWikiElementInfo> childs = ctx.getElementFinder().getDirectChilds(ci);
    Collections.sort(childs, new GWikiElementByChildOrderComparator(new GWikiElementByOrderComparator(new GWikiElementByIntPropComparator(
        "ORDER", 0))));

    ctx.append("<table border=\"1\" cellspacing=\"0\">");
    int i = 0;
    int colNo = 0;
    ctx.setRequestAttribute(GWikiFragmentImage.WIKI_MAX_IMAGE_WIDTH, columnWidth);

    for (GWikiElementInfo c : childs) {
      GWikiElement el = ctx.getWikiWeb().findElement(c.getId());
      if (el == null) {
        continue;
      }
      if (c.isViewable() == false) {
        continue;
      }

      GWikiArtefakt< ? > art = null;
      if (StringUtils.isNotEmpty(part) == true) {
        art = el.getPart(part);
      } else {
        art = el.getMainPart();
      }
      if ((art instanceof GWikiExecutableArtefakt< ? >) == false || art instanceof GWikiAttachment) {
        continue;
      }
      if (colNo == 0) {
        ctx.append("<tr>");
      }
      ctx.append("<td valign=\"top\">");
      GWikiExecutableArtefakt< ? > exec = (GWikiExecutableArtefakt< ? >) art;
      ctx.append("<table border=\"0\" cellspacing=\"0\" ><tr>");
      ctx.append("<th valign=\"top\" width=\"" + columnWidth + "\">")
          //
          .append("<div id=\"chid_" + el.getElementInfo().getId() + "\" class=\"gwikioresdraggable\">")
          .append(ctx.getTranslatedProp(el.getElementInfo().getTitle())).append("</div></th></tr>");
      ctx.append("<tr height=\"" + rowHeight + "\"><td valign=\"top\">");
      /* oncontextmenu=\"alert('clicked');\" */
      ctx.append("<div style=\"font-size:0.6em;size:0.6em;\"  ondblclick=\"document.location.href='"
          + ctx.localUrl(el.getElementInfo().getId())
          + "';\">");
      // ctx.append("<iframe>");
      // rowHeight

      exec.render(ctx);
      GWikiContext.setCurrent(ctx);
      // ctx.append("</iframe>");
      ctx.append("</div>");
      ctx.append("</td></tr></table>");
      ctx.append("</td>");
      if (colNo + 1 >= columns) {
        ctx.append("</tr>");
        colNo = 0;
      } else {
        ++colNo;
      }
      ++i;
    }
    ctx.append("</tr></table>");
    if (ctx.getWikiWeb().getAuthorization().isAllowToEdit(ctx, ci) == true) {
      ctx.append("<script type=\"text/javascript\">")//
          .append("jQuery(document).ready(function(){\n")//
          .append("jQuery('.gwikioresdraggable').draggable( {\n") //
          .append(" zIndex:     1000,\n") //
          .append("ghosting:   true,\n") //
          .append("revert: 'invalid',\n") //
          .append("snap: true,\n") //
          .append("opacity:    0.7\n") //
          .append("});\n")//
          .append("jQuery('.gwikioresdraggable').droppable({\n")//
          // .append(" accept : 'dropaccept',\n")//
          .append("tolerance: 'touch',\n")//
          .append("drop: function(event, ui) { \n")//
          // .append("alert('dropped');\n")//
          .append(
              "jQuery.ajax({\n"
                  + "cache: false,\n"
                  + "async: false,\n"
                  + "url: '"
                  + ctx.localUrl("edit/EditPage")
                  + "?method_onReorderChildsAsync=true',\n"
                  + "type: 'POST',\n"
                  + "dataType: 'html',\n"
                  + "data: { pageId: '"
                  + ci.getId()
                  + "', p2: ui.draggable.attr('id'), p1: this.id },\n"
                  + " complete: function(res, status) {\n"
                  + " //jQuery('#backupInfo').html(res.responseText);\n"
                  + " location.reload();\n"
                  + "}});\n"
                  + "//alert('dropped' + this.id + ' on ' + ui.draggable.attr('id'));\n"
                  + "}\n")//
          .append("});\n")//
          .append("});\n") //
          .append("</script>\n");
    }
    return true;
  }

  public int getColumns()
  {
    return columns;
  }

  public void setColumns(int columns)
  {
    this.columns = columns;
  }

  public String getColumnWidth()
  {
    return columnWidth;
  }

  public void setColumnWidth(String columnWidth)
  {
    this.columnWidth = columnWidth;
  }

  public String getRowHeight()
  {
    return rowHeight;
  }

  public void setRowHeight(String rowHeight)
  {
    this.rowHeight = rowHeight;
  }

  public String getPart()
  {
    return part;
  }

  public void setPart(String part)
  {
    this.part = part;
  }

  public String getParentPage()
  {
    return parentPage;
  }

  public void setParentPage(String parentPage)
  {
    this.parentPage = parentPage;
  }

}
