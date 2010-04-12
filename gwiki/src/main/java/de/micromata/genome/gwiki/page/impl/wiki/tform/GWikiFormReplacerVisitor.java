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
package de.micromata.genome.gwiki.page.impl.wiki.tform;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections15.ArrayStack;

import de.micromata.genome.gwiki.page.impl.wiki.fragment.GWikiFragment;
import de.micromata.genome.gwiki.page.impl.wiki.fragment.GWikiFragmentChildsBase;
import de.micromata.genome.gwiki.page.impl.wiki.fragment.GWikiFragmentText;
import de.micromata.genome.gwiki.page.impl.wiki.fragment.GWikiFragmentUnsecureHtml;
import de.micromata.genome.gwiki.page.impl.wiki.fragment.GWikiFragmentVisitor;

/**
 * @author Roger Rene Kommer (r.kommer@micromata.de)
 * 
 */
public class GWikiFormReplacerVisitor implements GWikiFragmentVisitor
{
  private ArrayStack<GWikiFragment> stack = new ArrayStack<GWikiFragment>();

  /*
   * (non-Javadoc)
   * 
   * @seede.micromata.genome.gwiki.page.impl.wiki.fragment.GWikiFragmentVisitor#begin(de.micromata.genome.gwiki.page.impl.wiki.fragment.
   * GWikiFragment)
   */
  public void begin(GWikiFragment fragment)
  {
    stack.push(fragment);

  }

  protected boolean hasFormElement(String text)
  {
    int idx = text.indexOf('@');
    if (idx == -1) {
      return false;
    }
    String rt = text.substring(idx);
    if (rt.indexOf('@') == -1) {
      return false;
    }
    return true;
  }

  protected void parseFormElements(List<GWikiFragment> frags, String text)
  {
    int sidx = text.indexOf('@');
    if (sidx == -1) {
      if (frags.isEmpty() == false) {
        frags.add(new GWikiFragmentText(text));
      }
      return;
    }
    String rt = text.substring(sidx + 1);
    int eidx = rt.indexOf('@');
    if (eidx == -1) {
      if (frags.isEmpty() == false) {
        frags.add(new GWikiFragmentText(text));
      }
      return;
    }
    String vardef = text.substring(sidx + 1, eidx + sidx + 1);
    // TODO analyse.
    if (sidx != 0) {
      frags.add(new GWikiFragmentText(text.substring(0, sidx)));
    }
    frags.add(new GWikiFragmentUnsecureHtml("<input type=\"text\" name=\"" + vardef + "\"/>"));
    String lt = text.substring(sidx + eidx + 2);
    if (lt.length() > 0) {
      parseFormElements(frags, lt);
    }
  }

  protected List<GWikiFragment> parseFormElements(String text)
  {
    List<GWikiFragment> frags = new ArrayList<GWikiFragment>();
    parseFormElements(frags, text);
    return frags;
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * de.micromata.genome.gwiki.page.impl.wiki.fragment.GWikiFragmentVisitor#end(de.micromata.genome.gwiki.page.impl.wiki.fragment.GWikiFragment
   * )
   */
  public void end(GWikiFragment fragment)
  {
    stack.pop();
    if (fragment instanceof GWikiFragmentText) {
      GWikiFragmentText tf = (GWikiFragmentText) fragment;
      String t = tf.getHtml();
      List<GWikiFragment> frags = parseFormElements(t);
      if (frags.isEmpty() == false) {
        GWikiFragment pf = stack.peek();
        if (pf instanceof GWikiFragmentChildsBase) {
          ((GWikiFragmentChildsBase) pf).replaceChilds(fragment, frags);
        }
      }
    }
  }
}
