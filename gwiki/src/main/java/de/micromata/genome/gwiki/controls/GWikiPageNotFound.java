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

  public Object onUnbound()
  {
    return onInit();
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
