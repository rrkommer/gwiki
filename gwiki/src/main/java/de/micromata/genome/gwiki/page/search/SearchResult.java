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

package de.micromata.genome.gwiki.page.search;

import de.micromata.genome.gwiki.model.GWikiElementInfo;

/**
 * Internal class to hold a search result element.
 * 
 * @author Roger Rene Kommer (r.kommer@micromata.de)
 * 
 */
public class SearchResult
{
  private GWikiElementInfo elementInfo;

  private int relevance = 1;

  private String textExerpt = "";

  public SearchResult(GWikiElementInfo elementInfo)
  {
    this.elementInfo = elementInfo;
  }

  public SearchResult(SearchResult other)
  {
    this.elementInfo = other.elementInfo;
    this.relevance = other.relevance;
    this.textExerpt = other.textExerpt;
  }

  public SearchResult(SearchResult other, int relevance)
  {
    this(other);
    this.relevance = relevance;
  }

  public String toString()
  {
    return getPageId() + ";" + relevance;
  }

  public String getPageId()
  {
    return elementInfo.getId();
  }

  public int getRelevance()
  {
    return relevance;
  }

  public void setRelevance(int relevance)
  {
    this.relevance = relevance;
  }

  public String getTextExerpt()
  {
    return textExerpt;
  }

  public void setTextExerpt(String textExerpt)
  {
    this.textExerpt = textExerpt;
  }

  public GWikiElementInfo getElementInfo()
  {
    return elementInfo;
  }

  public void setElementInfo(GWikiElementInfo elementInfo)
  {
    this.elementInfo = elementInfo;
  }
}
