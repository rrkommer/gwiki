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
package de.micromata.genome.gwiki.plugin.s5slideshow_1_0;

import org.apache.commons.collections15.ArrayStack;

import de.micromata.genome.gwiki.page.impl.wiki.GWikiMacroFragment;
import de.micromata.genome.gwiki.page.impl.wiki.fragment.GWikiFragment;
import de.micromata.genome.gwiki.page.impl.wiki.fragment.GWikiFragmentList;
import de.micromata.genome.gwiki.page.impl.wiki.fragment.GWikiFragmentP;
import de.micromata.genome.gwiki.page.impl.wiki.fragment.GWikiSimpleFragmentVisitor;

/**
 * @author Roger Rene Kommer (r.kommer@micromata.de)
 * 
 */
public class GWikiSlideIncrementPatcherFragmentVisitor extends GWikiSimpleFragmentVisitor
{

  public ArrayStack<Boolean> disableStack = new ArrayStack<Boolean>();

  protected boolean isDisabled()
  {
    if (disableStack.isEmpty() == true) {
      return false;
    }
    return disableStack.peek() == Boolean.TRUE;
  }

  /*
   * (non-Javadoc)
   * 
   * @seede.micromata.genome.gwiki.page.impl.wiki.fragment.GWikiFragmentVisitor#begin(de.micromata.genome.gwiki.page.impl.wiki.fragment.
   * GWikiFragment)
   */
  public void begin(GWikiFragment fragment)
  {
    if (fragment instanceof GWikiMacroFragment) {
      GWikiMacroFragment mf = (GWikiMacroFragment) fragment;
      if (mf.getMacro() instanceof GWikiSlideIncrementalMacro) {
        GWikiSlideIncrementalMacro im = (GWikiSlideIncrementalMacro) mf.getMacro();
        disableStack.push(im.isDisable());
      }
    }
    if (isDisabled() == true) {
      return;
    }
    if (fragment instanceof GWikiFragmentList) {
      GWikiFragmentList fl = (GWikiFragmentList) fragment;
      if (fl.getAddClass() == null) {
        fl.setAddClass("incremental");
      }
    }
    if (fragment instanceof GWikiFragmentP) {
      GWikiFragmentP p = (GWikiFragmentP) fragment;
      if (p.getAddClass() == null) {
        p.setAddClass("incremental");
      }
    }
    /* already via p */
    // if (fragment instanceof GWikiFragmentImage) {
    // GWikiFragmentImage p = (GWikiFragmentImage) fragment;
    // if (p.getStyleClass() == null) {
    // p.setStyleClass("incremental");
    // }
    // }
  }

  @Override
  public void end(GWikiFragment fragment)
  {
    if (fragment instanceof GWikiMacroFragment) {
      GWikiMacroFragment mf = (GWikiMacroFragment) fragment;
      if (mf.getMacro() instanceof GWikiSlideIncrementalMacro) {
        disableStack.pop();
      }
    }
    super.end(fragment);
  }

}
