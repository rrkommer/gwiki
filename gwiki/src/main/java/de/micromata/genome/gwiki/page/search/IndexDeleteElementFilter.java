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
package de.micromata.genome.gwiki.page.search;

import de.micromata.genome.gwiki.model.GWikiStorage;
import de.micromata.genome.gwiki.model.filter.GWikiFilterChain;
import de.micromata.genome.gwiki.model.filter.GWikiStorageDeleteElementFilter;
import de.micromata.genome.gwiki.model.filter.GWikiStorageDeleteElementFilterEvent;

/**
 * Delete text extract and index file in case an element will be deleted.
 * 
 * @author Roger Rene Kommer (r.kommer@micromata.de)
 * 
 */
public class IndexDeleteElementFilter implements GWikiStorageDeleteElementFilter
{

  /*
   * (non-Javadoc)
   * 
   * @see de.micromata.genome.gwiki.model.filter.GWikiFilter#filter(de.micromata.genome.gwiki.model.filter.GWikiFilterChain,
   * de.micromata.genome.gwiki.model.filter.GWikiFilterEvent)
   */
  public Void filter(GWikiFilterChain<Void, GWikiStorageDeleteElementFilterEvent, GWikiStorageDeleteElementFilter> chain,
      GWikiStorageDeleteElementFilterEvent event)
  {
    if (event.getWikiContext().getBooleanRequestAttribute(GWikiStorage.STORE_NO_INDEX) == true) {
      return chain.nextFilter(event);
    }
    String id = event.getElement().getElementInfo().getId();
    String fnidx = id + IndexStoragePersistHandler.TEXTINDEX_PARTNAME + ".txt";
    String fnte = id + IndexStoragePersistHandler.TEXTEXTRACT_PARTNMAE + ".txt";
    event.getWikiContext().getWikiWeb().getStorage().getFileSystem().delete(fnidx);
    event.getWikiContext().getWikiWeb().getStorage().getFileSystem().delete(fnte);
    return chain.nextFilter(event);
  }
}
