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

package de.micromata.genome.gwiki.model.mpt;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;

import de.micromata.genome.gwiki.page.GWikiContext;
import de.micromata.genome.gwiki.web.GWikiServlet;

/**
 * MptSelector which considers request parameters for retrieving current branch information. THIS SHOULD ONLY BE USED IN DEV MDOE!
 * 
 * @author Roger Rene Kommer (r.kommer@micromata.de)
 */
public class GWikiReqSessionMptIdSelector extends GWikiSessionMptIdSelector
{

  /*
   * (non-Javadoc)
   * 
   * @see de.micromata.genome.gwiki.model.mpt.GWikiMptSelector#getTenantId(de.micromata.genome.gwiki.web.GWikiServlet,
   * javax.servlet.http.HttpServletRequest)
   */
  public String getTenantId(GWikiServlet servlet, HttpServletRequest req)
  {
    String id = (String) req.getAttribute(MPT_KEY);
    if (StringUtils.isNotBlank(id) == true) {
      return id;
    }
    id = req.getParameter(MPT_KEY);
    if (id != null) {
      if (StringUtils.isBlank(id) == true) {
        req.getSession().removeAttribute(MPT_KEY);
        return null;
      } else {
        req.getSession().setAttribute(MPT_KEY, id);
        return id;
      }

    }
    return super.getTenantId(servlet, req);
  }

  public void setTenant(GWikiContext wikiContext, String tenantId)
  {
    if (StringUtils.isBlank(tenantId) == true) {
      wikiContext.getRequest().getSession().removeAttribute(MPT_KEY);
    } else {
     wikiContext.getRequest().setAttribute(MPT_KEY, tenantId);
    }
    super.setTenant(wikiContext, tenantId);
  }
}
