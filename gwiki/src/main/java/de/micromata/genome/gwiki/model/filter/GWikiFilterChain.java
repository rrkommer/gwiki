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

package de.micromata.genome.gwiki.model.filter;

import java.util.ArrayList;
import java.util.List;

import de.micromata.genome.gwiki.model.logging.GWikiLog;

/**
 * Chain implementation to dispatch events.
 * 
 * @author Roger Rene Kommer (r.kommer@micromata.de)
 * 
 */
public class GWikiFilterChain<R, E extends GWikiFilterEvent, F extends GWikiFilter<R, E, F>>
{
  private int curIndex = 0;

  private F target;

  private List<F> filterList = new ArrayList<F>();

  private boolean exceptionSafe = false;

  public GWikiFilterChain(GWikiFilters filters, F target, List<F> filterList)
  {
    this.target = target;
    this.filterList = filterList;
  }

  public R nextFilter(E event)
  {
    do {
      if (curIndex >= filterList.size()) {
        return target.filter(this, event);
      }
      if (exceptionSafe == false) {
        return filterList.get(curIndex++).filter(this, event);
      }
      try {
        return filterList.get(curIndex++).filter(this, event);
      } catch (Exception ex) {
        GWikiLog.error("Failure executed filter: " + ex.getMessage(), ex);
        continue;
      }
    } while (true);
  }

  public boolean isExceptionSafe()
  {
    return exceptionSafe;
  }

  public void setExceptionSafe(boolean exceptionSafe)
  {
    this.exceptionSafe = exceptionSafe;
  }
}
