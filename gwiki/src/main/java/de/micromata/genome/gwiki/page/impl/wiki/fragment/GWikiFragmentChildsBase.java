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

  @Override
  public void iterate(GWikiFragmentVisitor visitor, GWikiFragment parent)
  {
    visitor.begin(this, parent);
    List<GWikiFragment> lchilds = getChilds();
    for (int i = 0; i < getChilds().size(); ++i) {
      if (getChilds().size() > i && getChilds().get(i) != null) {
        getChilds().get(i).iterate(visitor, this);
      }
    }
    visitor.end(this, parent);
  }

  public void getChildSouce(StringBuilder sb)
  {
    List<GWikiFragment> lchilds = getChilds();
    GWikiFragment prev = null;
    for (int i = 0; i < lchilds.size(); ++i) {
      GWikiFragment c = lchilds.get(i);
      GWikiFragment next = null;
      if (i < lchilds.size() - 1) {
        next = lchilds.get(i + 1);
      }
      c.getSource(sb, this, prev, next);
      prev = c;
    }
  }

  @Override
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
  @Override
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

  @Override
  public void ensureRight(GWikiContext ctx) throws AuthorizationFailedException
  {
    List<GWikiFragment> lchilds = getChilds();
    for (GWikiFragment c : lchilds) {
      c.ensureRight(ctx);
    }
  }

  @Override
  public void addChilds(List<GWikiFragment> childs)
  {
    List<GWikiFragment> lchilds = getChilds();
    lchilds.addAll(childs);
  }

  @Override
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

  @Override
  public List<GWikiFragment> getChilds()
  {
    return childs;
  }

  @Override
  public void setChilds(List<GWikiFragment> childs)
  {
    this.childs = childs;
  }

}
