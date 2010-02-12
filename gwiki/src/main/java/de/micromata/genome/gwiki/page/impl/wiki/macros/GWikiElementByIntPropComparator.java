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

package de.micromata.genome.gwiki.page.impl.wiki.macros;

import java.util.Comparator;

import de.micromata.genome.gwiki.model.GWikiElementInfo;

/**
 * Comparator to compare two int values in the GWikielementInfo.
 * 
 * @author Roger Rene Kommer (r.kommer@micromata.de)
 * 
 */
public class GWikiElementByIntPropComparator extends GWikiElementComparatorBase
{
  private String propName;

  private int defaultValue;

  public GWikiElementByIntPropComparator()
  {

  }

  public GWikiElementByIntPropComparator(String propName)
  {
    this(propName, 0);
  }

  public GWikiElementByIntPropComparator(String propName, int defaultValue)
  {
    this.propName = propName;
    this.defaultValue = defaultValue;
  }

  public GWikiElementByIntPropComparator(String propName, int defaultValue, Comparator<GWikiElementInfo> parentComparator)
  {
    super(parentComparator);
    this.propName = propName;
    this.defaultValue = defaultValue;
  }

  public int compare(GWikiElementInfo o1, GWikiElementInfo o2)
  {
    int s1 = o1.getProps().getIntValue(propName, defaultValue);
    int s2 = o2.getProps().getIntValue(propName, defaultValue);
    if (s1 == s2)
      return compareParent(o1, o2);
    if (s1 < s2) {
      return -1;
    }
    return 1;
  }

  public String getPropName()
  {
    return propName;
  }

  public void setPropName(String propName)
  {
    this.propName = propName;
  }

  public int getDefaultValue()
  {
    return defaultValue;
  }

  public void setDefaultValue(int defaultValue)
  {
    this.defaultValue = defaultValue;
  }

}
