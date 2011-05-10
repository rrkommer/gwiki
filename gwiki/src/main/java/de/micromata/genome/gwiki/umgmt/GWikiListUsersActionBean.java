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

package de.micromata.genome.gwiki.umgmt;

import java.io.Serializable;

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
  }

  @Override
  public Object onInit()
  {
    return null;
  }

  @Override
  public String renderField(String fieldName, GWikiElementInfo elementInfo)
  {
    GWikiElement el = wikiContext.getWikiWeb().getElement(elementInfo);
    Serializable ser = el.getMainPart().getCompiledObject();
    GWikiProps props = (GWikiProps) ser;

    if ("USER".equals(fieldName) == true) {
      return GWikiContext.getNamePartFromPageId(elementInfo.getId());
    }
    if ("EMAIL".equals(fieldName) == true) {
      return props.getStringValue("email");
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
