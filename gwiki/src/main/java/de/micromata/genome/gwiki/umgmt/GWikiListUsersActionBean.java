/////////////////////////////////////////////////////////////////////////////
//
// Project   DHL-ParcelOnlinePostage
//
// Author    roger@micromata.de
// Created   15.11.2009
// Copyright Micromata 15.11.2009
//
/////////////////////////////////////////////////////////////////////////////
package de.micromata.genome.gwiki.umgmt;

import java.io.Serializable;

import de.micromata.genome.gwiki.controls.GWikiPageListActionBean;
import de.micromata.genome.gwiki.model.GWikiElement;
import de.micromata.genome.gwiki.model.GWikiElementInfo;
import de.micromata.genome.gwiki.model.GWikiProps;
import de.micromata.genome.gwiki.page.GWikiContext;

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
      return "<a href='" + wikiContext.localUrl("edit/UserProfile") + "?pageId=" + elementInfo.getId() + "&backUrl=edit/ListUsers'>Edit</a>";
    }
    return super.renderField(fieldName, elementInfo);
  }
}
