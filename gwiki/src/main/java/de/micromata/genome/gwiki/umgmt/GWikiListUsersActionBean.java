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

package de.micromata.genome.gwiki.umgmt;

import java.io.Serializable;

import org.apache.commons.lang.StringEscapeUtils;

import de.micromata.genome.gwiki.controls.GWikiPageListActionBean;
import de.micromata.genome.gwiki.model.GWikiElement;
import de.micromata.genome.gwiki.model.GWikiElementInfo;
import de.micromata.genome.gwiki.model.GWikiProps;
import de.micromata.genome.gwiki.page.GWikiContext;

/**
 * Action for users.
 * 
 * @author Roger Rene Kommer (r.kommer@micromata.de)
 * 
 */
public class GWikiListUsersActionBean extends GWikiPageListActionBean
{
  public GWikiListUsersActionBean()
  {
    fixedFilterExpression = "prop:PAGEID like \"admin/user/*\"";
    setFields("USER|EMAIL|operations");
  }

  @Override
  public Object onInit()
  {
    return onFilter();
  }

  @Override
  public Object onFilter()
  {
    Object ret = super.onFilter();
    return ret;
  }

  @Override
  public String renderField(String fieldName, GWikiElementInfo elementInfo)
  {
    GWikiElement el = wikiContext.getWikiWeb().getElement(elementInfo);
    Serializable ser = el.getMainPart().getCompiledObject();
    GWikiProps props = (GWikiProps) ser;

    if ("USER".equals(fieldName) == true) {
      return StringEscapeUtils.escapeHtml(GWikiContext.getNamePartFromPageId(elementInfo.getId()));
    }
    if ("EMAIL".equals(fieldName) == true) {
      return StringEscapeUtils.escapeHtml(props.getStringValue("email"));
    }
    if ("operations".equals(fieldName) == true) {
      return "<a href='"
          + wikiContext.localUrl("edit/UserProfile")
          + "?pageId="
          + elementInfo.getId()
          + "&backUrl=edit/ListUsers'>"
          + wikiContext.getTranslated("gwiki.umgmt.listusers.edit")
          + "</a>";
    }
    return super.renderField(fieldName, elementInfo);
  }
}
