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
package de.micromata.genome.gwiki.pagelifecycle_1_0.model;

import de.micromata.genome.gwiki.model.GWikiAuthorizationExt;
import de.micromata.genome.gwiki.model.GWikiWeb;
import de.micromata.genome.gwiki.model.GWikiWikiSelector;
import de.micromata.genome.gwiki.model.mpt.GWikiMultipleWikiSelector;
import de.micromata.genome.gwiki.model.mpt.GWikiReqSessionMptIdSelector;
import de.micromata.genome.gwiki.pagelifecycle_1_0.auth.PlcSimpleUserAuthorization;
import de.micromata.genome.gwiki.plugin.GWikiAbstractPluginLifecycleListener;
import de.micromata.genome.gwiki.plugin.GWikiPlugin;
import de.micromata.genome.gwiki.web.GWikiServlet;

/**
 * @author Stefan Stuetzer (s.stuetzer@micromata.com)
 */
public class GWikiPlcInitializer extends GWikiAbstractPluginLifecycleListener
{

  private GWikiWikiSelector previousSelector;

  private GWikiAuthorizationExt previousAuthorization;

  /*
   * (non-Javadoc)
   * 
   * @see de.micromata.genome.gwiki.plugin.GWikiAbstractPluginLifecycleListener#activated(de.micromata.genome.gwiki.model.GWikiWeb,
   * de.micromata.genome.gwiki.plugin.GWikiPlugin)
   */
  @Override
  public void activated(GWikiWeb wikiWeb, GWikiPlugin plugin)
  {
    super.activated(wikiWeb, plugin);
    if (wikiWeb.getDaoContext().getWikiSelector() instanceof GWikiMultipleWikiSelector == false) {
      previousSelector = wikiWeb.getDaoContext().getWikiSelector();
      GWikiMultipleWikiSelector nms = new GWikiMultipleWikiSelector(previousSelector.getRootWikiWeb(GWikiServlet.INSTANCE));
      nms.setMptIdSelector(new GWikiReqSessionMptIdSelector());
      wikiWeb.getDaoContext().setWikiSelector(nms);
    }

    if (wikiWeb.getDaoContext().getAuthorization() instanceof PlcSimpleUserAuthorization == false) {
      previousAuthorization = (GWikiAuthorizationExt) wikiWeb.getDaoContext().getAuthorization();
      PlcSimpleUserAuthorization authorization = new PlcSimpleUserAuthorization(previousAuthorization);
      wikiWeb.getDaoContext().setAuthorization(authorization);
    }

  }

  /*
   * (non-Javadoc)
   * 
   * @see de.micromata.genome.gwiki.plugin.GWikiAbstractPluginLifecycleListener#deactivate(de.micromata.genome.gwiki.model.GWikiWeb,
   * de.micromata.genome.gwiki.plugin.GWikiPlugin)
   */
  @Override
  public void deactivate(GWikiWeb wikiWeb, GWikiPlugin plugin)
  {
    if (previousSelector != null) {
      wikiWeb.getDaoContext().setWikiSelector(previousSelector);
    }

    if (previousAuthorization != null) {
      wikiWeb.getDaoContext().setAuthorization(previousAuthorization);
    }
    super.deactivate(wikiWeb, plugin);
  }

}
