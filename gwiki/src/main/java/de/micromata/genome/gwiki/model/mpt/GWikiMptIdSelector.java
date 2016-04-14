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

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import de.micromata.genome.gdbfs.FileSystem;
import de.micromata.genome.gwiki.model.GWikiWeb;
import de.micromata.genome.gwiki.page.GWikiContext;
import de.micromata.genome.gwiki.web.GWikiServlet;

/**
 * Selects from request the tenant.
 * 
 * @author Roger Rene Kommer (r.kommer@micromata.de)
 * 
 */
public interface GWikiMptIdSelector
{
  /**
   * return the tenant id for given request.
   * 
   * @param servlet
   * @param req
   * @return null if no tenant is given and the root gwiki is used.
   */

  String getTenantId(GWikiServlet servlet, HttpServletRequest req);

  /**
   * set given tenant in session or user or similar.
   * 
   * @param wikiContext
   * @param tenantId
   */
  void setTenant(GWikiContext wikiContext, String tenantId);

  /**
   * 
   * @param servlet
   * @return a list of available tenants.
   */
  List<String> getTenants(GWikiWeb rootWiki);

  /**
   * 
   * @param servlet
   * @param pageId
   * @return null if non
   */
  String idIsPartOfTenant(GWikiWeb rootWiki, String pageId);

  /**
   * 
   * @param rootWiki the root wiki
   * @param tenantId id of the wiki
   * @return null, if no file system for this wiki exists.
   */
  FileSystem getTenantFileSystem(GWikiWeb rootWiki, String tenantId);

}
