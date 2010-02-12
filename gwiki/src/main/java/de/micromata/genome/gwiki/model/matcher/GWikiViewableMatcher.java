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

import de.micromata.genome.gwiki.model.GWikiAuthorization;
import de.micromata.genome.gwiki.model.GWikiElementInfo;
import de.micromata.genome.gwiki.page.GWikiContext;

/**
 * Matches if GWikiElement is viewable.
 * 
 * @author Roger Rene Kommer (r.kommer@micromata.de)
 * 
 */
public class GWikiViewableMatcher extends GWikiElementMatcherBase
{

  private static final long serialVersionUID = 8271727742238624099L;

  GWikiAuthorization auth;

  public GWikiViewableMatcher(GWikiContext wikiContext)
  {
    super(wikiContext);
    auth = wikiContext.getWikiWeb().getAuthorization();
  }

  public boolean match(GWikiElementInfo object)
  {
    if (object == null) {
      return false;
    }
    if (auth.isAllowToView(wikiContext, object) == true) {
      return true;
    }
    return false;
  }

}
