////////////////////////////////////////////////////////////////////////////
//
// Copyright (C) 2010-2013 Micromata GmbH / Roger Rene Kommer
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

import java.util.ArrayList;

import org.apache.commons.lang.StringUtils;

import de.micromata.genome.gwiki.model.GWikiElement;
import de.micromata.genome.gwiki.model.GWikiElementInfo;
import de.micromata.genome.gwiki.page.GWikiContext;
import de.micromata.genome.gwiki.page.RenderModes;
import de.micromata.genome.gwiki.page.impl.wiki.GWikiMacroBean;
import de.micromata.genome.gwiki.page.impl.wiki.GWikiMacroClassFactory;
import de.micromata.genome.gwiki.page.impl.wiki.GWikiMacroFactory;
import de.micromata.genome.gwiki.page.impl.wiki.MacroAttributes;

/**
 * Implements the hierarchical bread crumbs as Macro.
 * 
 * @author Christian Claus (c.claus@micromata.de)
 * 
 */
public class GWikiHierarchicalBreadcrumbMacroBean extends GWikiMacroBean
{

  private static final long serialVersionUID = 9108047152475068594L;

  public static GWikiMacroFactory getFactory()
  {
    return new GWikiMacroClassFactory(GWikiHierarchicalBreadcrumbMacroBean.class);
  }

  public static void doRender(final GWikiContext ctx)
  {
    new GWikiHierarchicalBreadcrumbMacroBean().render(new MacroAttributes(), ctx);
  }

  private void setParentForElement(final GWikiContext ctx, final GWikiElementInfo ei, final ArrayList<String> breadcrumbs)
  {
    final String parentId = ei.getParentId();

    if (parentId != null && ctx.getWikiWeb().findElement(parentId) != null) {
      final GWikiElementInfo elementInfo = ctx.getWikiWeb().findElement(parentId).getElementInfo();
      if (elementInfo != null && elementInfo.isViewable() && elementInfo.isIndexed()) {
        breadcrumbs.add(elementInfo.getId());
        setParentForElement(ctx, elementInfo, breadcrumbs);
      }
    }
  }

  @Override
  public boolean renderImpl(final GWikiContext ctx, final MacroAttributes attrs)
  {
    if (RenderModes.NoToc.isSet(ctx.getRenderMode()) == true) {
      return true;
    }

    final GWikiElementInfo elementInfo = ctx.getCurrentElement().getElementInfo();

    if (elementInfo.getId() == null) {
      return false;
    }

    final ArrayList<String> breadcrumbs = new ArrayList<String>();

    if (elementInfo.isIndexed()) {
      breadcrumbs.add(elementInfo.getId());
      setParentForElement(ctx, elementInfo, breadcrumbs);
    } else {
      final String pageId = ctx.getRequestParameter("pageId");
      final String parentPageId = ctx.getRequestParameter("parentPageId");

      if (StringUtils.isNotBlank(pageId)
          && ctx.getWikiWeb().findElement(pageId) != null
          && ctx.getWikiWeb().findElement(pageId).getElementInfo().isIndexed()) {
        computeParameter(ctx, breadcrumbs, pageId);
      } else if (StringUtils.isNotBlank(parentPageId)
          && ctx.getWikiWeb().findElement(parentPageId) != null
          && ctx.getWikiWeb().findElement(parentPageId).getElementInfo().isIndexed()) {
        computeParameter(ctx, breadcrumbs, parentPageId);
      } else {
        breadcrumbs.add(ctx.getWikiWeb().getWelcomePageId(ctx));
      }
    }

    ctx.append("<ul>");

    for (int i = breadcrumbs.size() - 1; i >= 0; i--) {
      ctx.append("<li>").append(ctx.renderLocalUrl(breadcrumbs.get(i))).append("</li>");
    }

    ctx.append("</ul>");
    ctx.flush();

    return true;
  }

  private void computeParameter(final GWikiContext ctx, final ArrayList<String> breadcrumbs, final String pageId)
  {
    final GWikiElement elem = ctx.getWikiWeb().findElement(pageId);

    if (elem != null) {
      breadcrumbs.add(pageId);
      setParentForElement(ctx, elem.getElementInfo(), breadcrumbs);
    } else {
      breadcrumbs.add(ctx.getWikiWeb().getWelcomePageId(ctx));
    }
  }
}
