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

package de.micromata.genome.gwiki.page.impl.wiki.smileys;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections15.ArrayStack;

import de.micromata.genome.gwiki.page.GWikiContext;
import de.micromata.genome.gwiki.page.impl.wiki.fragment.GWikiFragment;
import de.micromata.genome.gwiki.page.impl.wiki.fragment.GWikiFragmentText;
import de.micromata.genome.gwiki.page.impl.wiki.fragment.GWikiFragmentVisitor;

public class GWikiSmileyContentIterator implements GWikiFragmentVisitor
{

  private ArrayStack<GWikiFragment> stack = new ArrayStack<GWikiFragment>();
  private GWikiContext wikiContext;
  private GWikiSmileyConfig smileyConfig;

  public GWikiSmileyContentIterator(GWikiContext ctx)
  {
    wikiContext = ctx;
    smileyConfig = GWikiSmileyConfig.get(ctx);
  }

  @Override
  public void begin(GWikiFragment fragment, GWikiFragment parent)
  {

    if ((fragment instanceof GWikiFragmentText) == false) {
      stack.push(fragment);
      return;
    }
    int childIdx = -1;
    if (parent != null) {
      childIdx = parent.getChilds().indexOf(fragment);
    } else if (stack.isEmpty() == false) {
      parent = stack.peek();
      childIdx = parent.getChilds().indexOf(fragment);
    }
    if (childIdx == -1) {
      return;
    }

    GWikiFragmentText tf = (GWikiFragmentText) fragment;
    String text = tf.getText().toString();
    patchSmileys(text, childIdx, parent);
    stack.push(fragment);
  }

  private void patchSmileys(String text, int childIdx, GWikiFragment parent)
  {
    int fidx = text.indexOf('(');
    if (fidx == -1) {
      return;
    }
    String start = text.substring(fidx);
    int lidx = start.indexOf(')');
    if (lidx == -1) {
      return;
    }
    int startIdx = 0;
    List<GWikiFragment> nfrags = new ArrayList<>();
    boolean smileyFound = false;
    int i;
    for (i = 0; i < text.length(); ++i) {
      if (text.charAt(i) == '(') {
        int endIdx = scanForSmiley(i + 1, text);
        if (endIdx != -1) {
          if (startIdx != i) {
            nfrags.add(new GWikiFragmentText(text.substring(startIdx, i)));
          }
          //          nfrags.add(new GWikiFragmentText("?" + text.substring(i + 1, endIdx) + "?"));
          GWikiSmileyInfo smi = smileyConfig.getSmileys().get(text.substring(i + 1, endIdx));
          nfrags.add(new GWikiFragmentSmiley(smi));
          smileyFound = true;
          startIdx = i = endIdx + 1;
        } else {
          if (i + 1 < text.length()) {
            break;
          }
          startIdx = i + 1;
          i = text.indexOf('(', startIdx);

          continue;
        }
      }
    }
    if (smileyFound == false) {
      return;
    }
    if (startIdx < text.length()) {
      nfrags.add(new GWikiFragmentText(text.substring(startIdx)));
    }
    parent.getChilds().remove(childIdx);
    int insertPos = childIdx;
    for (GWikiFragment fr : nfrags) {
      parent.getChilds().add(insertPos, fr);
      ++insertPos;
    }
    return;
  }

  private int scanForSmiley(int i, String text)
  {
    int startIdx = i;
    for (; i < text.length(); ++i) {
      char c = text.charAt(i);
      if (c == ')') {
        String smtxt = text.substring(startIdx, i);
        if (smileyConfig.getSmileys().containsKey(smtxt) == true) {
          return i;
        }
      } else if (Character.isWhitespace(c) == true) {
        return -1;
      }
    }
    return -1;
  }

  @Override
  public void end(GWikiFragment fragment, GWikiFragment parent)
  {
    stack.pop();
  }

  @Override
  public void begin(GWikiFragment fragment)
  {
    begin(fragment, null);

  }

  @Override
  public void end(GWikiFragment fragment)
  {
    end(fragment, null);
  }
}
