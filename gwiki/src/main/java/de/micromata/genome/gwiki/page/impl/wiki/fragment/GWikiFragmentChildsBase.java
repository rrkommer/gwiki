////////////////////////////////////////////////////////////////////////////
//
// Copyright (C) 2010-2013 Micromata GmbH / Roger Rene Kommer
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

package de.micromata.genome.gwiki.page.impl.wiki.fragment;

import java.util.ArrayList;
import java.util.List;

import de.micromata.genome.gwiki.model.AuthorizationFailedException;
import de.micromata.genome.gwiki.page.GWikiContext;

/**
 * Abstract implementation of a fragment with childs.
 * 
 * @author Roger Rene Kommer (r.kommer@micromata.de)
 * 
 */
public abstract class GWikiFragmentChildsBase extends GWikiFragmentBase implements GWikiNestableFragment
{

  private static final long serialVersionUID = -167135240006781273L;

  protected List<GWikiFragment> childs;

  public GWikiFragmentChildsBase()
  {
    childs = new ArrayList<GWikiFragment>();
  }

  public GWikiFragmentChildsBase(List<GWikiFragment> childs)
  {
    this.childs = childs;
  }

  public GWikiFragmentChildsBase(GWikiFragmentChildsBase other)
  {
    childs = new ArrayList<GWikiFragment>();
    childs.addAll(other.childs);
  }

  public void iterate(GWikiFragmentVisitor visitor)
  {
    visitor.begin(this);
    List<GWikiFragment> lchilds = getChilds();
    for (int i = 0; i < lchilds.size(); ++i) {
      lchilds.get(i).iterate(visitor);
    }
    visitor.end(this);
  }

  public void getChildSouce(StringBuilder sb)
  {
    List<GWikiFragment> lchilds = getChilds();
    for (GWikiFragment c : lchilds) {
      c.getSource(sb);
    }
  }

  public void renderChilds(GWikiContext ctx)
  {
    List<GWikiFragment> lchilds = getChilds();
    for (GWikiFragment c : lchilds) {
      c.render(ctx);
    }
  }

  /**
   * 
   * @param search
   * @param replace
   * @return true if replaced
   */
  public boolean replaceChilds(GWikiFragment search, List<GWikiFragment> replace)
  {
    List<GWikiFragment> lchilds = getChilds();
    int idx = lchilds.indexOf(search);
    if (idx == -1) {
      return false;
    }
    lchilds.remove(idx);
    for (GWikiFragment ins : replace) {
      lchilds.add(idx++, ins);
    }
    return true;
  }

  public void ensureRight(GWikiContext ctx) throws AuthorizationFailedException
  {
    List<GWikiFragment> lchilds = getChilds();
    for (GWikiFragment c : lchilds) {
      c.ensureRight(ctx);
    }
  }

  public void addChilds(List<GWikiFragment> childs)
  {
    List<GWikiFragment> lchilds = getChilds();
    lchilds.addAll(childs);
  }

  public void addChilds(GWikiFragment child)
  {
    List<GWikiFragment> lchilds = getChilds();
    lchilds.add(child);
  }

  public void addChild(GWikiFragment child)
  {
    List<GWikiFragment> lchilds = getChilds();
    lchilds.add(child);
  }

  public List<GWikiFragment> getChilds()
  {
    return childs;
  }

}
