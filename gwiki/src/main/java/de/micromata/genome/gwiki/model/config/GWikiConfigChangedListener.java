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

package de.micromata.genome.gwiki.model.config;

import de.micromata.genome.gwiki.model.GWikiGlobalConfig;
import de.micromata.genome.gwiki.model.filter.GWikiFilterChain;
import de.micromata.genome.gwiki.model.filter.GWikiPageChangedFilter;
import de.micromata.genome.gwiki.model.filter.GWikiPageChangedFilterEvent;

/**
 * Filter for changing global GWiki configuration.
 * 
 * @author Roger Rene Kommer (r.kommer@micromata.de)
 * 
 */
public class GWikiConfigChangedListener implements GWikiPageChangedFilter
{
  public Void filter(GWikiFilterChain<Void, GWikiPageChangedFilterEvent, GWikiPageChangedFilter> chain, GWikiPageChangedFilterEvent event)
  {
    // should on first position, otherwise filter recursion.
    chain.nextFilter(event);
    if (event.getNewInfo() == null) {
      return null;
    }
    if (event.getNewInfo().getId().equals("admin/config/GWikiConfig") == true) {
      GWikiGlobalConfig cfg = event.getWikiWeb().reloadWikiConfig();
      event.getWikiWeb().initFilters(cfg);
    }
    return null;
  }
}
