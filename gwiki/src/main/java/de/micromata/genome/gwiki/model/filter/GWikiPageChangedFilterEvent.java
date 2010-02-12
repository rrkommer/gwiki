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

package de.micromata.genome.gwiki.model.filter;

import de.micromata.genome.gwiki.model.GWikiElementInfo;
import de.micromata.genome.gwiki.model.GWikiWeb;
import de.micromata.genome.gwiki.page.GWikiContext;

/**
 * Event for GWikiPageChangedFilter.
 * 
 * @author Roger Rene Kommer (r.kommer@micromata.de)
 * 
 */
public class GWikiPageChangedFilterEvent extends GWikiFilterEvent
{
  private GWikiWeb wikiWeb;

  private GWikiElementInfo newInfo;

  private GWikiElementInfo oldInfo;

  public GWikiPageChangedFilterEvent(GWikiContext wikiContext, GWikiWeb wikiWeb, GWikiElementInfo newInfo, GWikiElementInfo oldInfo)
  {
    super(wikiContext);
    this.wikiWeb = wikiWeb;
    this.newInfo = newInfo;
    this.oldInfo = oldInfo;

  }

  public GWikiElementInfo getNewInfo()
  {
    return newInfo;
  }

  public void setNewInfo(GWikiElementInfo newInfo)
  {
    this.newInfo = newInfo;
  }

  public GWikiElementInfo getOldInfo()
  {
    return oldInfo;
  }

  public void setOldInfo(GWikiElementInfo oldInfo)
  {
    this.oldInfo = oldInfo;
  }

  public GWikiWeb getWikiWeb()
  {
    return wikiWeb;
  }

  public void setWikiWeb(GWikiWeb wikiWeb)
  {
    this.wikiWeb = wikiWeb;
  }

}
