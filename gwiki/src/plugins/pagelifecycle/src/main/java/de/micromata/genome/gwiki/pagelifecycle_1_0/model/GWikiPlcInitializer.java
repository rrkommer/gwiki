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

import de.micromata.genome.gwiki.model.GWikiLog;
import de.micromata.genome.gwiki.model.GWikiWeb;
import de.micromata.genome.gwiki.plugin.GWikiAbstractPluginLifecycleListener;
import de.micromata.genome.gwiki.plugin.GWikiPlugin;

/**
 * @author stefans
 *
 */
public class GWikiPlcInitializer extends GWikiAbstractPluginLifecycleListener
{
  /* (non-Javadoc)
   * @see de.micromata.genome.gwiki.plugin.GWikiAbstractPluginLifecycleListener#activated(de.micromata.genome.gwiki.model.GWikiWeb, de.micromata.genome.gwiki.plugin.GWikiPlugin)
   */
  @Override
  public void activated(GWikiWeb wikiWeb, GWikiPlugin plugin)
  {
    super.activated(wikiWeb, plugin);
    GWikiLog.note("Activate PLC Plugin");
  }
  
  /* (non-Javadoc)
   * @see de.micromata.genome.gwiki.plugin.GWikiAbstractPluginLifecycleListener#deactivate(de.micromata.genome.gwiki.model.GWikiWeb, de.micromata.genome.gwiki.plugin.GWikiPlugin)
   */
  @Override
  public void deactivate(GWikiWeb wikiWeb, GWikiPlugin plugin)
  {
    GWikiLog.note("Deactivate PLC Plugin");
    super.deactivate(wikiWeb, plugin);
  }

}
