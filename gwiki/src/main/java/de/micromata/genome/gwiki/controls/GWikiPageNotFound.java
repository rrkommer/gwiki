/////////////////////////////////////////////////////////////////////////////
//
// Project   DHL-ParcelOnlinePostage
//
// Author    roger@micromata.de
// Created   24.11.2009
// Copyright Micromata 24.11.2009
//
/////////////////////////////////////////////////////////////////////////////
package de.micromata.genome.gwiki.controls;

import org.apache.commons.lang.StringUtils;

import de.micromata.genome.gwiki.model.GWikiAuthorizationRights;
import de.micromata.genome.util.web.MimeUtils;

/**
 * Standard Page not found ActionBean.
 * 
 * @author Roger Rene Kommer (r.kommer@micromata.de)
 * 
 */
public class GWikiPageNotFound extends GWikiCreateOrShowPage
{
  private boolean allowCreatePage = false;

  public Object onInit()
  {
    if (StringUtils.isEmpty(getPageId()) == true) {
      setPageId((String) wikiContext.getRequestAttribute("NotFoundPageId"));
    }
    if (StringUtils.isNotEmpty(getPageId()) == true) {
      if (wikiContext.getWikiWeb().getAuthorization().isAllowTo(wikiContext, GWikiAuthorizationRights.GWIKI_CREATEPAGES.name()) == true) {
        allowCreatePage = true;
      }
      String mime = MimeUtils.getMimeTypeFromFile(getPageId());
      if (StringUtils.isNotEmpty(mime) && StringUtils.endsWith(mime, "html") == false) {
        return noForward();
      }
    }

    return null;
  }

  public boolean isAllowCreatePage()
  {
    return allowCreatePage;
  }

  public void setAllowCreatePage(boolean allowCreatePage)
  {
    this.allowCreatePage = allowCreatePage;
  }

}
