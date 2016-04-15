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

package de.micromata.genome.gwiki.model;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import de.micromata.genome.gwiki.model.config.GWikiDAOContext;
import de.micromata.genome.gwiki.page.GWikiContext;
import de.micromata.genome.gwiki.web.GWikiServlet;

/**
 * Init and hold wikis.
 * 
 * May implement also multiple wikis.
 * 
 * @author Roger Rene Kommer (r.kommer@micromata.de)
 * 
 */
public interface GWikiWikiSelector
{

  /**
   * Initializes GWiki with given request.
   * 
   * The implementation should store selected GWikiWeb in Thread local or similar.
   *
   * @param servlet the servlet
   * @param req the req
   * @param resp the resp
   */
  void initWiki(GWikiServlet servlet, HttpServletRequest req, HttpServletResponse resp);

  /**
   * Deinitialize GWiki per request.
   *
   * @param servlet the servlet
   * @param req the req
   * @param resp the resp
   */
  void deinitWiki(GWikiServlet servlet, HttpServletRequest req, HttpServletResponse resp);

  /**
   * Check if initialized. If not initialize it.
   *
   * @param servlet the servlet
   * @param daoContext the dao context
   */
  void initWiki(GWikiServlet servlet, GWikiDAOContext daoContext);

  /**
   * return the current GWikiWeb.
   *
   * @param servlet the servlet
   * @return the wiki web
   */
  GWikiWeb getWikiWeb(GWikiServlet servlet);

  /**
   * Gets the root wiki web.
   *
   * @param servlet the servlet
   * @return the root wiki web.
   */
  GWikiWeb getRootWikiWeb(GWikiServlet servlet);

  /**
   * return the wikiweb for the tenant.
   *
   * @param servlet the servlet
   * @param tenant the tenant
   * @return null, if tenant is not known.
   */
  GWikiWeb getTenantWikiWeb(GWikiServlet servlet, String tenant);

  /**
   * Checks if GWikiWeb is already initialized.
   *
   * @param servlet the servlet
   * @return true, if successful
   */
  boolean hasWikiWeb(GWikiServlet servlet);

  /**
   * Switch to given tenant.
   *
   * @param wikiContext the wiki context
   * @param tenantId the tenant id
   * 
   *          Throw RuntimeException if tenants are not supported or given tenant does not exists
   */
  void enterTenant(GWikiContext wikiContext, String tenantId);

  /**
   * Switch to root wiki.
   *
   * @param wikiContext the wiki context
   * 
   * @throws RuntimeException if tenants are not supported or given tenant does not exists
   */
  void leaveTenant(GWikiContext wikiContext) throws RuntimeException;

  /**
   * Clear tenant (cache etc.)
   *
   * @param wikiContext the wiki context
   * @param tenantId the tenant id
   */
  void clearTenant(GWikiContext wikiContext, String tenantId);

  /**
   * Creates a tenant with the spefified name withoout switching the context to that tenant.
   *
   * @param wikiContext the wiki context
   * @param tenantId the tenant id
   * @throws RuntimeException if tenants are not supported
   */
  void createTenant(GWikiContext wikiContext, String tenantId) throws RuntimeException;

}
