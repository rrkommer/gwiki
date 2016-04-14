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

package de.micromata.genome.gwiki.page.search.expr;

import de.micromata.genome.gwiki.page.search.SearchResult;

/**
 * Compares values in the ElementInfo properties
 * 
 * @author Roger Rene Kommer (r.kommer@micromata.de)
 * 
 */
@Deprecated
public class SearchResultComparatorProps extends SearchResultComparatorBase
{
  private String key;

  public SearchResultComparatorProps(String key)
  {
    this.key = key;
  }

  @Override
  public int compareThis(SearchResult o1, SearchResult o2)
  {
    String v1 = o1.getElementInfo().getProps().getStringValue(key);
    String v2 = o2.getElementInfo().getProps().getStringValue(key);
    if (v1 == v2)
      return 0;
    if (v1 == null)
      return -1;
    if (v2 == null)
      return 1;
    return v1.compareTo(v2);
  }

  public String toString()
  {
    return "prop:" + key + (desc == true ? " desc" : " asc");
  }
}
