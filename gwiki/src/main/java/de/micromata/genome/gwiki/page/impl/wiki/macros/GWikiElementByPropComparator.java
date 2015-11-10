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

package de.micromata.genome.gwiki.page.impl.wiki.macros;

import java.util.Comparator;

import de.micromata.genome.gwiki.model.GWikiElementInfo;

/**
 * Compatator to compare two GWikiElementInfo by a property string value.
 * 
 * @author Roger Rene Kommer (r.kommer@micromata.de)
 * 
 */
public class GWikiElementByPropComparator extends GWikiElementComparatorBase
{
  protected String propName;

  protected String defaultValue;

  public GWikiElementByPropComparator()
  {

  }

  public GWikiElementByPropComparator(String propName)
  {
    this.propName = propName;
  }

  public GWikiElementByPropComparator(String propName, String defaultValue)
  {
    this.propName = propName;
    this.defaultValue = defaultValue;
  }

  public GWikiElementByPropComparator(String propName, Comparator<GWikiElementInfo> parentComparator)
  {
    super(parentComparator);
    this.propName = propName;
  }

  public int compare(GWikiElementInfo o1, GWikiElementInfo o2)
  {
    String s1 = o1.getProps().getStringValue(propName, defaultValue);
    String s2 = o2.getProps().getStringValue(propName, defaultValue);
    if (s1 == s2)
      return compareParent(o1, o2);
    if (s1 == null)
      return 1;
    if (s2 == null)
      return -1;
    int ret = s1.compareTo(s2);
    if (ret != 0)
      return ret;
    return compareParent(o1, o2);
  }

  public String getPropName()
  {
    return propName;
  }

  public void setPropName(String propName)
  {
    this.propName = propName;
  }

}
