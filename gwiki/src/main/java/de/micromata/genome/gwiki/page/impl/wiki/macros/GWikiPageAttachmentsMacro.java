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

import java.util.List;

import org.apache.commons.lang3.StringUtils;

import de.micromata.genome.gwiki.controls.GWikiPageInfoActionBean;
import de.micromata.genome.gwiki.model.GWikiElement;
import de.micromata.genome.gwiki.model.GWikiElementInfo;
import de.micromata.genome.gwiki.page.GWikiContext;
import de.micromata.genome.gwiki.page.RenderModes;
import de.micromata.genome.gwiki.page.impl.wiki.GWikiMacroBean;
import de.micromata.genome.gwiki.page.impl.wiki.GWikiMacroInfo.MacroParamType;
import de.micromata.genome.gwiki.page.impl.wiki.MacroAttributes;
import de.micromata.genome.gwiki.page.impl.wiki.MacroInfo;
import de.micromata.genome.gwiki.page.impl.wiki.MacroInfoParam;

/**
 * @author Roger Rene Kommer (r.kommer@micromata.de)
 * 
 */
@MacroInfo(info = "Renders a list of the attachment defined in a page.",
    params = { @MacroInfoParam(name = "page", info = "pageId owning the attachment. Default is current page"),
        @MacroInfoParam(name = "inline",
            info = "If false renders a link with shows the attachment listening in own page.",
            type = MacroParamType.Boolean) })
public class GWikiPageAttachmentsMacro extends GWikiMacroBean
{
  private static final long serialVersionUID = -5905511059700663321L;

  private String page;

  private boolean inline;

  /*
   * (non-Javadoc)
   * 
   * @see
   * de.micromata.genome.gwiki.page.impl.wiki.GWikiMacroBean#renderImpl(de.micromata.genome.gwiki.page.GWikiContext,
   * de.micromata.genome.gwiki.page.impl.wiki.MacroAttributes)
   */
  @Override
  public boolean renderImpl(GWikiContext ctx, MacroAttributes attrs)
  {
    if (RenderModes.NoToc.isSet(ctx.getRenderMode()) == true) {
      return true;
    }
    if (ctx.getCurrentElement() == null) {
      return true;
    }
    GWikiElement el;
    if (StringUtils.isEmpty(page) == true) {
      el = ctx.getCurrentElement();
    } else {
      el = ctx.getWikiWeb().getElement(page);
    }
    List<GWikiElementInfo> attachments = ctx.getElementFinder().getPageAttachments(el.getElementInfo().getId());
    if (attachments.isEmpty() == true) {
      return true;
    }
    if (inline == false) {
      String id = ctx.genHtmlId("pageattachment");

      StringBuilder sb = new StringBuilder();
      sb.append("<a href=\"").append(ctx.localUrl("edit/PageInfo")).append("?pageId=")
          .append(el.getElementInfo().getId())
          .append("&amp;showBoxElements=Attachments\"").append(">").append(attachments.size()).append(" ")
          .append(ctx.getTranslated("gwiki.page.attachments.name")).append("</a>");
      ctx.append("<span id=\"").append(id).append("\" class=\"pageattachment\">").append(sb.toString())
          .append("</span>");
    } else {
      ctx.append(GWikiPageInfoActionBean.buildAttachmentBox(ctx, el.getElementInfo()));
    }
    return true;
  }

  public String getPage()
  {
    return page;
  }

  public void setPage(String page)
  {
    this.page = page;
  }

  public boolean isInline()
  {
    return inline;
  }

  public void setInline(boolean inline)
  {
    this.inline = inline;
  }
}
