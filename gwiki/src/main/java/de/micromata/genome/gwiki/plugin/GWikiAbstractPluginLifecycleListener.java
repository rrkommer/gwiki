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
package de.micromata.genome.gwiki.plugin;

import de.micromata.genome.gwiki.model.GWikiWeb;

/**
 * Just a NOP version of GWikiPluginLifecycleListener.
 * 
 * @author Roger Rene Kommer (r.kommer@micromata.de)
 * 
 */
public class GWikiAbstractPluginLifecycleListener implements GWikiPluginLifecycleListener
{

  /*
   * (non-Javadoc)
   * 
   * @see de.micromata.genome.gwiki.plugin.GWikiPluginLifecycleListener#activated(de.micromata.genome.gwiki.model.GWikiWeb,
   * de.micromata.genome.gwiki.plugin.GWikiPlugin)
   */
  public void activated(GWikiWeb wikiWeb, GWikiPlugin plugin)
  {
  }

  public void webLoaded(GWikiWeb wikiWeb, GWikiPlugin plugin)
  {
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.micromata.genome.gwiki.plugin.GWikiPluginLifecycleListener#deactivate(de.micromata.genome.gwiki.model.GWikiWeb,
   * de.micromata.genome.gwiki.plugin.GWikiPlugin)
   */
  public void deactivate(GWikiWeb wikiWeb, GWikiPlugin plugin)
  {
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.micromata.genome.gwiki.plugin.GWikiPluginLifecycleListener#deactivated(de.micromata.genome.gwiki.model.GWikiWeb,
   * de.micromata.genome.gwiki.plugin.GWikiPlugin)
   */
  public void deactivated(GWikiWeb wikiWeb, GWikiPlugin plugin)
  {
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.micromata.genome.gwiki.plugin.GWikiPluginLifecycleListener#shutdown(de.micromata.genome.gwiki.model.GWikiWeb,
   * de.micromata.genome.gwiki.plugin.GWikiPlugin)
   */
  public void shutdown(GWikiWeb wikiWeb, GWikiPlugin plugin)
  {
  }

}
