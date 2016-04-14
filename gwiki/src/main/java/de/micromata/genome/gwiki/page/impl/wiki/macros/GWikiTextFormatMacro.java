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

import org.apache.xerces.xni.XMLAttributes;

import de.micromata.genome.gwiki.page.GWikiContext;
import de.micromata.genome.gwiki.page.impl.wiki.GWikiBodyEvalMacro;
import de.micromata.genome.gwiki.page.impl.wiki.GWikiMacroBean;
import de.micromata.genome.gwiki.page.impl.wiki.GWikiMacroRte;
import de.micromata.genome.gwiki.page.impl.wiki.MacroAttributes;
import de.micromata.genome.gwiki.utils.html.Html2WikiFilter;
import de.micromata.genome.gwiki.utils.html.Html2WikiTransformInfo;

/**
 * Macro alternative for simple text character formatting, like *bold* _italic_ etc.
 * 
 * @author Roger Rene Kommer (r.kommer@micromata.de)
 * 
 */
public class GWikiTextFormatMacro extends GWikiMacroBean implements GWikiBodyEvalMacro, GWikiMacroRte
{

  private static final long serialVersionUID = 5227382407595976299L;

  private String cmd;

  private String tag;

  private String styleClass;

  /*
   * (non-Javadoc)
   * 
   * @see de.micromata.genome.gwiki.page.impl.wiki.GWikiMacroBean#renderImpl(de.micromata.genome.gwiki.page.GWikiContext,
   * de.micromata.genome.gwiki.page.impl.wiki.MacroAttributes)
   */
  @Override
  public boolean renderImpl(GWikiContext ctx, MacroAttributes attrs)
  {
    ctx.append("<").append(tag).append(">");
    attrs.getChildFragment().render(ctx);
    ctx.append("</").append(tag).append(">");
    return true;
  }

  protected void populate(MacroAttributes attrs, GWikiContext ctx)
  {
    cmd = attrs.getCmd();
    tag = Html2WikiFilter.DefaultWiki2HtmlTextDecoMap.get(cmd);
    super.populate(attrs, ctx);
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.micromata.genome.gwiki.page.impl.wiki.GWikiMacroRte#getTransformInfo()
   */
  public Html2WikiTransformInfo getTransformInfo()
  {

    Html2WikiTransformInfo ti = new Html2WikiTransformInfo() {

      @Override
      public boolean match(String tagName, XMLAttributes attributes, boolean withBody)
      {
        return false;
      }

    };
    return ti;
  }

  public String getStyleClass()
  {
    return styleClass;
  }

  public void setStyleClass(String styleClass)
  {
    this.styleClass = styleClass;
  }

}
