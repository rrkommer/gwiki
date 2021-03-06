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

import org.apache.commons.lang3.StringUtils;

import de.micromata.genome.gwiki.page.GWikiContext;
import de.micromata.genome.gwiki.page.impl.wiki.GWikiMacroBean;
import de.micromata.genome.gwiki.page.impl.wiki.GWikiMacroInfo.MacroParamType;
import de.micromata.genome.gwiki.page.impl.wiki.MacroAttributes;
import de.micromata.genome.gwiki.page.impl.wiki.MacroInfo;
import de.micromata.genome.gwiki.page.impl.wiki.MacroInfoParam;

/**
 * @author Christian Claus (c.claus@micromata.de)
 * 
 */
@MacroInfo(info = "renders a page tree of children.",
    params = {
        @MacroInfoParam(name = "rootPageId",
            info = "Children root. If not set current page", type = MacroParamType.PageId),
        @MacroInfoParam(name = "height", info = "height in css declaration"),
        @MacroInfoParam(name = "width", info = "width in css declaration")
    })
public class GWikiPageTreeMacro extends GWikiMacroBean
{

  private static final long serialVersionUID = 4865948210393357947L;

  private String rootPageId;

  /**
   * height of the container
   */
  private String height;

  /**
   * width of the container
   */
  private String width;

  @Override
  public boolean renderImpl(final GWikiContext ctx, MacroAttributes attrs)
  {

    ctx.append("<div id='filechooser' style='margin-top: 20px; font-family: verdana; font-size: 10px;");

    if (StringUtils.isNotEmpty(width)) {
      ctx.append("width: " + width + "; ");
    }

    if (StringUtils.isNotBlank(height)) {
      ctx.append("height: " + height + "; ");
    }

    final String path = ctx.getServlet().getServletContext().getContextPath() + ctx.getRequest().getServletPath();

    ctx.append("'></div>");

    ctx.append("<script type='text/javascript'>");
    ctx.append("$.jstree._themes = '" + path + "/static/js/jstree/themes/';");
    ctx.append("$(document).ready(function () {");
    ctx.append("  $(\"#filechooser\").jstree({");
    ctx.append("    \"themes\" : { \"theme\" : \"classic\", \"dots\" : true, \"icons\" : true },");
    ctx.append("    \"plugins\" : [ \"themes\", \"html_data\", \"ui\" ],");
    ctx.append("    \"html_data\" : {\n");
    ctx.append("      \"ajax\" : {");
    ctx.append("        \"url\" : ");
    ctx.append("\"").append(path);
    ctx.append("/edit/TreeChildren\",\n");
    ctx.append("        \"data\" : function(n) {   return { \"method_onLoadAsync\" : \"true\", "
        + "\"id\" : n.attr ? n.attr(\"id\") : \""
        + (StringUtils.isNotBlank(rootPageId) ? rootPageId : "")
        + "\","
        + "\"target\" : \"true\""
        + " };      }\n");
    ctx.append("      }");
    ctx.append("    }\n");
    ctx.append("  });\n");
    ctx.append("});\n");
    ctx.append("</script>");

    ctx.flush();

    return true;
  }

  /**
   * @param rootPageId the rootPageId to set
   */
  public void setRootPageId(String rootPageId)
  {
    this.rootPageId = rootPageId;
  }

  /**
   * @return the rootPageId
   */
  public String getRootPageId()
  {
    return rootPageId;
  }

  /**
   * @return the rootPage
   */
  public String getRootPage()
  {
    return rootPageId;
  }

  /**
   * @param height the height to set
   */
  public void setHeight(String height)
  {
    this.height = height;
  }

  /**
   * @return the height
   */
  public String getHeight()
  {
    return height;
  }

  /**
   * @param width the width to set
   */
  public void setWidth(String width)
  {
    this.width = width;
  }

  /**
   * @return the width
   */
  public String getWidth()
  {
    return width;
  }
}
