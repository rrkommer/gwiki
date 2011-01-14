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
package de.micromata.genome.gwiki.model.mpt;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;

import de.micromata.genome.gdbfs.FileSystem;
import de.micromata.genome.gdbfs.FsObject;
import de.micromata.genome.gdbfs.SubFileSystem;
import de.micromata.genome.gwiki.model.GWikiWeb;
import de.micromata.genome.gwiki.page.GWikiContext;
import de.micromata.genome.gwiki.web.GWikiServlet;

/**
 * @author Roger Rene Kommer (r.kommer@micromata.de)
 * 
 */
public class GWikiReqSessionMptIdSelector implements GWikiMptIdSelector
{
  public static final String MPT_KEY = "mptkey";

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
    id = (String) req.getSession().getAttribute(MPT_KEY);
    return id;
  }

  public void setTenant(GWikiContext wikiContext, String tenantId)
  {
    if (StringUtils.isBlank(tenantId) == true) {
      wikiContext.getRequest().getSession().removeAttribute(MPT_KEY);
      wikiContext.getRequest().removeAttribute(MPT_KEY);
    } else {
     wikiContext.getRequest().setAttribute(MPT_KEY, tenantId);
      wikiContext.getRequest().getSession().setAttribute(MPT_KEY, tenantId);
    }
  }

  protected FileSystem getTenantMasterFileSystem(GWikiWeb rootWiki)
  {
    return rootWiki.getDaoContext().getBean("MptFileSystem", FileSystem.class);

  }

  public FileSystem getTenantFileSystem(GWikiWeb rootWiki, String tenantId)
  {
    FileSystem mptFs = getTenantMasterFileSystem(rootWiki);
    mptFs.mkdirs(tenantId);
    SubFileSystem mptFsSp = new SubFileSystem(mptFs, tenantId);
    return mptFsSp;

  }

  /*
   * (non-Javadoc)
   * 
   * @see de.micromata.genome.gwiki.model.mpt.GWikiMptIdSelector#getTenants(de.micromata.genome.gwiki.web.GWikiServlet)
   */
  public List<String> getTenants(GWikiWeb rootWiki)
  {
    FileSystem fs = getTenantMasterFileSystem(rootWiki);
    List<String> retl = new ArrayList<String>();
    for (FsObject fsl : fs.listFiles("", null, 'D', false)) {
      String n = fsl.getName();
      if (n.startsWith("/") == true) {
        n = n.substring(1);
      }
      retl.add(n);
    }
    return retl;
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.micromata.genome.gwiki.model.mpt.GWikiMptIdSelector#idIsPartOfTenant(de.micromata.genome.gwiki.web.GWikiServlet,
   * java.lang.String)
   */
  public String idIsPartOfTenant(GWikiWeb rootWiki, String pageId)
  {
    List<String> tnl = getTenants(rootWiki);
    for (String tn : tnl) {
      FileSystem fs = getTenantFileSystem(rootWiki, tn);
      if (fs.exists(pageId + "Settings.properties") == true) {
        return tn;
      }
    }
    return null;
  }
}
