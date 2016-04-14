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

package de.micromata.genome.gwiki.plugin.keywordsmarttags_1_0;

import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import org.apache.commons.collections15.ArrayStack;

import de.micromata.genome.gwiki.model.GWikiElementInfo;
import de.micromata.genome.gwiki.page.impl.wiki.fragment.GWikiFragmentLink;
import de.micromata.genome.gwiki.page.impl.wiki.fragment.GWikiFragment;
import de.micromata.genome.gwiki.page.impl.wiki.fragment.GWikiFragmentHeading;
import de.micromata.genome.gwiki.page.impl.wiki.fragment.GWikiFragmentHtml;
import de.micromata.genome.gwiki.page.impl.wiki.fragment.GWikiFragmentVisitor;
import de.micromata.genome.util.types.Pair;

public class KeyWordReplaceVisitor implements GWikiFragmentVisitor
{
  Map<String, Pair<Pattern, List<GWikiElementInfo>>> keywordsToElements;

  private ArrayStack<GWikiFragment> stack = new ArrayStack<GWikiFragment>();

  public KeyWordReplaceVisitor(Map<String, Pair<Pattern, List<GWikiElementInfo>>> keywordsToElements)
  {
    this.keywordsToElements = keywordsToElements;
  }

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

    if (fragment instanceof GWikiFragmentHtml && isInLink() == false && (fragment instanceof GWikiFragmentKeywordHtml) == false) {
      GWikiFragmentHtml htmlFrag = (GWikiFragmentHtml) fragment;
      int idx = stack.get().getChilds().indexOf(fragment);
      if (idx != -1) {
        stack.get().getChilds().set(idx, new GWikiFragmentKeywordHtml(htmlFrag));
      }

    }
    stack.push(fragment);
  }

  public void end(GWikiFragment fragment)
  {
    stack.pop();
  }

}