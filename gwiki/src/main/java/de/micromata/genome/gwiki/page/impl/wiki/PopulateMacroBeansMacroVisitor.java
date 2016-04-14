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

package de.micromata.genome.gwiki.page.impl.wiki;

import de.micromata.genome.gwiki.page.GWikiContext;
import de.micromata.genome.gwiki.page.impl.wiki.fragment.GWikiFragment;
import de.micromata.genome.gwiki.page.impl.wiki.fragment.GWikiSimpleFragmentVisitor;

/**
 * The macro beans will be populated on first render. This visitor populates beans also without rendering.
 * 
 * @author Roger Rene Kommer (r.kommer@micromata.de)
 * 
 */
public class PopulateMacroBeansMacroVisitor extends GWikiSimpleFragmentVisitor
{
  private GWikiContext wikiContext;

  public PopulateMacroBeansMacroVisitor(GWikiContext wikiContext)
  {
    this.wikiContext = wikiContext;
  }

  /*
   * (non-Javadoc)
   * 
   * @seede.micromata.genome.gwiki.page.impl.wiki.fragment.GWikiFragmentVisitor#begin(de.micromata.genome.gwiki.page.impl.wiki.fragment.
   * GWikiFragment)
   */
  public void begin(GWikiFragment fragment)
  {
    if ((fragment instanceof GWikiMacroFragment) == false) {
      return;
    }
    final GWikiMacroFragment mf = (GWikiMacroFragment) fragment;
    if ((mf.getMacro() instanceof GWikiMacroBean) == false) {
      return;
    }
    GWikiMacroBean mb = (GWikiMacroBean) mf.getMacro();
    mb.populateIfNeeded(mf.getAttrs(), wikiContext);

  }

}
