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

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.StringUtils;

import de.micromata.genome.gwiki.model.GWikiAuthorizationRights;
import de.micromata.genome.gwiki.model.GWikiElement;
import de.micromata.genome.gwiki.model.GWikiElementInfo;
import de.micromata.genome.gwiki.model.GWikiPropKeys;
import de.micromata.genome.gwiki.model.GWikiSettingsProps;
import de.micromata.genome.gwiki.model.config.GWikiMetaTemplate;
import de.micromata.genome.gwiki.page.GWikiContext;
import de.micromata.genome.gwiki.page.RenderModes;
import de.micromata.genome.gwiki.page.impl.wiki.GWikiMacroBean;
import de.micromata.genome.gwiki.page.impl.wiki.GWikiMacroInfo.MacroParamType;
import de.micromata.genome.gwiki.page.impl.wiki.GWikiMacroRenderFlags;
import de.micromata.genome.gwiki.page.impl.wiki.MacroAttributes;
import de.micromata.genome.gwiki.page.impl.wiki.MacroInfo;
import de.micromata.genome.gwiki.page.impl.wiki.MacroInfoParam;

/**
 * Render a link or button to create a new link.
 * 
 * The user requires to edit the current page, create new elements and - if set the right for the new element.
 * 
 * @author Roger Rene Kommer (r.kommer@micromata.de)
 * 
 */
@MacroInfo(info = "Render a link or button to create a new link.<br/>" +
    "The user requires to edit the current page, create new elements and - if set the right for the new element.",
    params = {
        @MacroInfoParam(name = "pageId", info = "PageId of the new Page. Optional", type = MacroParamType.PageId),
        @MacroInfoParam(name = "metaTemplate", info = "Type of the new element. Optional"),
        @MacroInfoParam(name = "title", info = "Title of the new element. Optional"),
        @MacroInfoParam(name = "text", info = "Text used for the link. Optional"),
        @MacroInfoParam(name = "linkClass",
            info = "CSS Class used for the link. Optiona. if not set uses gwikiButton."),
        @MacroInfoParam(name = "parentPage",
            info = " Parent page for the new created element. Optional. if not set uses current page.",
            type = MacroParamType.PageId),
    },
    renderFlags = { GWikiMacroRenderFlags.InTextFlow, GWikiMacroRenderFlags.RteSpan })
public class GWikiNewElementMacro extends GWikiMacroBean
{

  private static final long serialVersionUID = 1035471530152122291L;

  /**
   * PageId of the new Page. Optional
   */
  private String pageId;

  /**
   * Type of the new element. Optional
   */
  private String metaTemplate;

  /**
   * Title of the new element. Optional
   */
  private String title;

  /**
   * Text used for the link. Optional
   */
  private String text;

  /**
   * Class used for the link. Optiona. if not set uses gwikiButton.
   */
  private String linkClass;

  /**
   * Parent page for the new created element. Optional. if not set uses current page.
   */
  private String parentPage;

  private String editPageId = "edit/EditPage";

  protected void renderNewPageLink(GWikiContext ctx, MacroAttributes attrs)
  {
    GWikiElement el = ctx.getCurrentElement();
    if (title != null) {
      ctx.append("&amp;title=").append(StringEscapeUtils.escapeXml(title));
    }
    if (pageId != null) {
      ctx.append("&amp;pageId=").append(StringEscapeUtils.escapeXml(pageId));
    }
    if (StringUtils.isNotEmpty(metaTemplate) == true) {
      ctx.append("&amp;metaTemplatePageId=").append(StringEscapeUtils.escapeXml(metaTemplate));
    }
    if (parentPage == null) {
      parentPage = el.getElementInfo().getId();
    }
    ctx.append("&amp;parentPageId=").append(StringEscapeUtils.escapeXml(parentPage));
  }

  @Override
  public boolean renderImpl(GWikiContext ctx, MacroAttributes attrs)
  {
    if (RenderModes.NoToc.isSet(ctx.getRenderMode()) == true) {
      return true;
    }
    GWikiElement el = ctx.getCurrentElement();
    if (ctx.getWikiWeb().getAuthorization().isAllowTo(ctx,
        GWikiAuthorizationRights.GWIKI_CREATEPAGES.name()) == false) {
      return true;
    }
    if (ctx.getWikiWeb().getAuthorization().isAllowToEdit(ctx, el.getElementInfo()) == false) {
      return true;
    }
    if (StringUtils.isNotEmpty(metaTemplate) == true) {
      GWikiMetaTemplate mt = ctx.getWikiWeb().findMetaTemplate(metaTemplate);
      if (mt == null) {
        return true;
      }
      GWikiSettingsProps props = new GWikiSettingsProps();
      // props.setStringValue(GWikiPropKeys.TYPE, mt.getElementType());
      props.setStringValue(GWikiPropKeys.WIKIMETATEMPLATE, metaTemplate);
      props.setStringValue(GWikiPropKeys.TITLE, "");
      GWikiElementInfo nei = new GWikiElementInfo(props, mt);
      if (ctx.getWikiWeb().getAuthorization().isAllowToCreate(ctx, nei) == false) {
        return true;
      }
    }
    if (linkClass == null) {
      linkClass = "gwikiButton";
    }
    ctx.append("<a class=\"" + linkClass + "\" href=\"" + ctx.localUrl(editPageId) + "?newPage=true");
    renderNewPageLink(ctx, attrs);

    ctx.append("\">");
    if (StringUtils.isEmpty(text) == true) {
      text = ctx.getTranslated("gwiki.macro.newelement.title");
    }
    ctx.append(StringEscapeUtils.escapeHtml(text));
    ctx.append("</a>");
    return true;
  }

  public String getPageId()
  {
    return pageId;
  }

  public void setPageId(String pageId)
  {
    this.pageId = pageId;
  }

  public String getMetaTemplate()
  {
    return metaTemplate;
  }

  public void setMetaTemplate(String metaTemplate)
  {
    this.metaTemplate = metaTemplate;
  }

  public String getTitle()
  {
    return title;
  }

  public void setTitle(String title)
  {
    this.title = title;
  }

  public String getText()
  {
    return text;
  }

  public void setText(String text)
  {
    this.text = text;
  }

  public String getLinkClass()
  {
    return linkClass;
  }

  public void setLinkClass(String linkClass)
  {
    this.linkClass = linkClass;
  }

  public String getParentPage()
  {
    return parentPage;
  }

  public void setParentPage(String parentPage)
  {
    this.parentPage = parentPage;
  }

  public String getEditPageId()
  {
    return editPageId;
  }

  public void setEditPageId(String editPageId)
  {
    this.editPageId = editPageId;
  }

}
