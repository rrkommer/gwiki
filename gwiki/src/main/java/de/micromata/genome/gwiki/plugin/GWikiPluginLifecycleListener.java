////////////////////////////////////////////////////////////////////////////
// 
// Copyright (C) 2010-2013 Micromata GmbH / Roger Rene Kommer
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
 * inside the GWikiPluginFilterDescriptor.
 * 
 * @author Roger Rene Kommer (r.kommer@micromata.de)
 * 
 */
public interface GWikiPluginLifecycleListener
{
  // void loaded(GWikiWeb wikiWeb, GWikiPlugin plugin);

  void activated(GWikiWeb wikiWeb, GWikiPlugin plugin);

  /**
   * Web pages are loaded and web is ready.
   * 
   * @param wikiWeb
   * @param plugin
   */
  void webLoaded(GWikiWeb wikiWeb, GWikiPlugin plugin);

  void deactivate(GWikiWeb wikiWeb, GWikiPlugin plugin);

  void deactivated(GWikiWeb wikiWeb, GWikiPlugin plugin);

  /**
   * May not be called
   * 
   * @param wikiWeb
   * @param plugin
   */
  void shutdown(GWikiWeb wikiWeb, GWikiPlugin plugin);

}
