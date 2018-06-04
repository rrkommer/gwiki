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

package de.micromata.genome.gwiki.plugin.wikilink_1_0;

import org.apache.commons.collections4.ArrayStack;

import de.micromata.genome.gwiki.page.impl.wiki.fragment.GWikiFragment;
import de.micromata.genome.gwiki.page.impl.wiki.fragment.GWikiFragmentHeading;
import de.micromata.genome.gwiki.page.impl.wiki.fragment.GWikiFragmentLink;
import de.micromata.genome.gwiki.page.impl.wiki.fragment.GWikiFragmentText;
import de.micromata.genome.gwiki.page.impl.wiki.fragment.GWikiFragmentVisitor;

/**
 * @author roger
 * 
 */
public class GWikiWikiLinkContentIterator implements GWikiFragmentVisitor
{
  private ArrayStack<GWikiFragment> stack = new ArrayStack<GWikiFragment>();

  protected boolean isInLink()
  {
    for (int i = 0; i < stack.size(); ++i) {
      GWikiFragment frag = stack.peek(i);
      if (frag instanceof GWikiFragmentLink || frag instanceof GWikiFragmentHeading) {
        return true;
      }
    }
    return false;
  }

  public void begin(GWikiFragment fragment)
  {
    if (fragment instanceof GWikiFragmentText && isInLink() == false && (fragment instanceof GWikiWikiLinkFragment) == false) {
      GWikiFragmentText textFrag = (GWikiFragmentText) fragment;
      int idx = stack.get().getChilds().indexOf(fragment);
      if (idx != -1) {
        stack.get().getChilds().set(idx, GWikiWikiLinkFragment.parseText(textFrag));
      }

    }
    stack.push(fragment);
  }

  public void end(GWikiFragment fragment)
  {
    stack.pop();
  }
}
