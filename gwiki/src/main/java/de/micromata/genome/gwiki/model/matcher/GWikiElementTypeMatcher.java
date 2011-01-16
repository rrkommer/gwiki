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
package de.micromata.genome.gwiki.model.matcher;

import org.apache.commons.lang.StringUtils;

import de.micromata.genome.gwiki.model.GWikiElementInfo;
import de.micromata.genome.gwiki.model.GWikiLog;
import de.micromata.genome.gwiki.page.GWikiContext;

/**
 * @author Roger Rene Kommer (r.kommer@micromata.de)
 * 
 */
public class GWikiElementTypeMatcher extends GWikiElementMatcherBase
{

  private static final long serialVersionUID = -4409390062352940849L;

  private String elementType;

  /**
   * @param wikiContext
   */
  public GWikiElementTypeMatcher(GWikiContext wikiContext, String elementType)
  {
    super(wikiContext);
    this.elementType = elementType;
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.micromata.genome.gwiki.model.matcher.GWikiElementMatcherBase#match(de.micromata.genome.gwiki.model.GWikiElementInfo)
   */
  @Override
  public boolean match(GWikiElementInfo ei)
  {
    if (ei.getMetaTemplate() == null) {
      GWikiLog.warn("Element without metatemplate: " + ei.getId());
      return false;
    }

    return StringUtils.equals(elementType, ei.getMetaTemplate().getElementType());
  }
}
