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

package de.micromata.genome.gwiki.controls;

import org.apache.commons.lang.StringUtils;

import de.micromata.genome.gwiki.model.AuthorizationFailedException;
import de.micromata.genome.gwiki.model.GWikiAuthorizationRights;
import de.micromata.genome.gwiki.model.config.GWikiMetaTemplate;
import de.micromata.genome.gwiki.page.impl.actionbean.ActionBeanBase;
import de.micromata.genome.util.types.Converter;

/**
 * ActionBean with no representation, which decides if target page can be created by current user and redirect to create page or page not
 * found.
 * 
 * @author Roger Rene Kommer (r.kommer@micromata.de)
 * 
 */
public class GWikiCreateOrShowPage extends ActionBeanBase
{
  private String pageId;

  private String title;

  private String metaTemplatePageId;

  private String parentPageId;

  public Object onInit()
  {
    if (StringUtils.isEmpty(pageId) == true) {
      throw new AuthorizationFailedException("pageId is missing");
    }
    if (wikiContext.getWikiWeb().findElementInfo(pageId) != null) {
      return pageId;
    }
    if (wikiContext.getWikiWeb().getAuthorization().isAllowTo(wikiContext, GWikiAuthorizationRights.GWIKI_CREATEPAGES.name()) == false) {
      return "admin/PageNotFound";
    }
    if (StringUtils.isNotBlank(metaTemplatePageId) == true) {

      GWikiMetaTemplate metaTemplate = wikiContext.getWikiWeb().findMetaTemplate(metaTemplatePageId);
      if (wikiContext.getWikiWeb().getAuthorization().isAllowTo(wikiContext, metaTemplate.getRequiredEditRight()) == false) {
        return "admin/PageNotFound";
      }
    }
    StringBuilder sb = new StringBuilder();
    sb.append("/edit/EditPage").append("?newPage=true&pageId=").append(Converter.encodeUrlParam(pageId));
    if (StringUtils.isNotBlank(title) == true) {
      sb.append("&title=").append(Converter.encodeUrlParam(title));
    }
    if (StringUtils.isNotBlank(metaTemplatePageId) == true) {
      sb.append("&metaTemplatePageId=").append(Converter.encodeUrlParam(metaTemplatePageId));
    }
    if (StringUtils.isNotBlank(parentPageId) == true) {
      sb.append("&parentPageId=").append(Converter.encodeUrlParam(parentPageId));
    }
    return sb.toString();
  }

  public String getPageId()
  {
    return pageId;
  }

  public void setPageId(String pageId)
  {
    this.pageId = pageId;
  }

  public String getTitle()
  {
    return title;
  }

  public void setTitle(String title)
  {
    this.title = title;
  }

  public String getMetaTemplatePageId()
  {
    return metaTemplatePageId;
  }

  public void setMetaTemplatePageId(String metaTemplatePageId)
  {
    this.metaTemplatePageId = metaTemplatePageId;
  }

  public String getParentPageId()
  {
    return parentPageId;
  }

  public void setParentPageId(String parentPageId)
  {
    this.parentPageId = parentPageId;
  }
}
