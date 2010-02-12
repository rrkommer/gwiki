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

import de.micromata.genome.gwiki.model.GWikiArtefakt;
import de.micromata.genome.gwiki.model.GWikiAttachment;
import de.micromata.genome.gwiki.model.GWikiElement;
import de.micromata.genome.gwiki.model.GWikiElementInfo;
import de.micromata.genome.gwiki.model.GWikiExecutableArtefakt;
import de.micromata.genome.gwiki.page.GWikiContext;
import de.micromata.genome.gwiki.page.impl.wiki.GWikiMacroBean;
import de.micromata.genome.gwiki.page.impl.wiki.MacroAttributes;

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

  @Override
  public boolean renderImpl(GWikiContext ctx, MacroAttributes attrs)
  {
    List<GWikiElementInfo> childs = ctx.getElementFinder().getDirectChilds(ctx.getWikiElement().getElementInfo());
    Collections.sort(childs, new GWikiElementByOrderComparator(new GWikiElementByIntPropComparator("ORDER", 0)));

    ctx.append("<table border=\"1\" cellspacing=\"0\">");
    int i = 0;
    int colNo = 0;
    for (GWikiElementInfo c : childs) {
      GWikiElement el = ctx.getWikiWeb().findElement(c.getId());
      if (el == null) {
        continue;
      }
      if (c.isViewable() == false) {
        continue;
      }
      GWikiArtefakt< ? > art = el.getMainPart();
      if ((art instanceof GWikiExecutableArtefakt< ? >) == false || art instanceof GWikiAttachment) {
        continue;
      }
      if (colNo == 0) {
        ctx.append("<tr>");
      }
      ctx.append("<td valign=\"top\">");
      GWikiExecutableArtefakt< ? > exec = (GWikiExecutableArtefakt< ? >) art;
      ctx.append("<table border=\"0\" cellspacing=\"0\" ><tr>");
      ctx.append("<th  valign=\"top\" width=\"" + columnWidth + "\">").append(ctx.getTranslatedProp(el.getElementInfo().getTitle()))
          .append("</th></tr>");
      ctx.append("<tr height=\"" + rowHeight + "\"><td valign=\"top\">");
      /* oncontextmenu=\"alert('clicked');\" */
      ctx.append("<div style=\"font-size:0.3em;size:0.3em;\"  ondblclick=\"document.location.href='"
          + ctx.localUrl(el.getElementInfo().getId())
          + "';\">");
      exec.render(ctx);
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

}
