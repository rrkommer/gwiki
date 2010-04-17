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

import org.apache.commons.lang.StringUtils;

import de.micromata.genome.gwiki.page.GWikiContext;
import de.micromata.genome.gwiki.page.impl.wiki.GWikiBodyEvalMacro;
import de.micromata.genome.gwiki.page.impl.wiki.GWikiMacroBean;
import de.micromata.genome.gwiki.page.impl.wiki.GWikiMacroRenderFlags;
import de.micromata.genome.gwiki.page.impl.wiki.MacroAttributes;

/**
 * See also GWikiSectionMacro.
 * 
 * @author Roger Rene Kommer (r.kommer@micromata.de)
 * 
 */
public class GWikiColumnMacro extends GWikiMacroBean implements GWikiBodyEvalMacro
{

  private static final long serialVersionUID = 8874624229089176168L;

  private String width;

  private String styleClass;

  private String style;

  public GWikiColumnMacro()
  {
    setRenderModes(GWikiMacroRenderFlags.combine(GWikiMacroRenderFlags.TrimTextContent));
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.micromata.genome.gwiki.page.impl.wiki.GWikiMacroBean#renderImpl(de.micromata.genome.gwiki.page.GWikiContext,
   * de.micromata.genome.gwiki.page.impl.wiki.MacroAttributes)
   */
  @Override
  public boolean renderImpl(GWikiContext ctx, MacroAttributes attrs)
  {
    if (StringUtils.isEmpty(styleClass) == true && StringUtils.isNotEmpty(attrs.getArgs().getStringValue("class")) == true) {
      styleClass = attrs.getArgs().getStringValue("class");
    }
    ctx.append("<td");
    if (StringUtils.isNotEmpty(width) == true) {
      ctx.append(" width=\"").append(width).append("\"");
    }
    if (StringUtils.isNotEmpty(styleClass) == true) {
      ctx.append(" class=\"").append(styleClass).append("\"");
    }
    if (StringUtils.isNotEmpty(style) == true) {
      ctx.append(" style=\"").append(style).append("\"");
    }
    ctx.append(">\n");
    if (attrs.getChildFragment() != null) {
      attrs.getChildFragment().render(ctx);
    }
    ctx.append("\n</td>");
    return true;
  }

  public String getWidth()
  {
    return width;
  }

  public void setWidth(String width)
  {
    this.width = width;
  }

  public String getStyleClass()
  {
    return styleClass;
  }

  public void setStyleClass(String styleClass)
  {
    this.styleClass = styleClass;
  }

  public String getStyle()
  {
    return style;
  }

  public void setStyle(String style)
  {
    this.style = style;
  }

}
