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

import de.micromata.genome.gwiki.model.GWikiElement;
import de.micromata.genome.gwiki.page.GWikiContext;
import de.micromata.genome.gwiki.page.impl.GWikiWikiPageBaseArtefakt;

/**
 * Event for a GWikiWikiPageCompileFilter.
 * 
 * @author Roger Rene Kommer (r.kommer@micromata.de)
 * 
 */
public class GWikiWikiPageCompileFilterEvent extends GWikiFilterEvent
{
  protected GWikiElement element;

  protected GWikiWikiPageBaseArtefakt wikiPageArtefakt;

  public GWikiWikiPageCompileFilterEvent(GWikiContext wikiContext, GWikiElement element, GWikiWikiPageBaseArtefakt wikiPageArtefakt)
  {
    super(wikiContext);
    this.element = element;
    this.wikiPageArtefakt = wikiPageArtefakt;
  }

  public GWikiWikiPageBaseArtefakt getWikiPageArtefakt()
  {
    return wikiPageArtefakt;
  }

  public void setWikiPageArtefakt(GWikiWikiPageBaseArtefakt wikiPageArtefakt)
  {
    this.wikiPageArtefakt = wikiPageArtefakt;
  }

  public GWikiElement getElement()
  {
    return element;
  }

  public void setElement(GWikiElement element)
  {
    this.element = element;
  }

}
