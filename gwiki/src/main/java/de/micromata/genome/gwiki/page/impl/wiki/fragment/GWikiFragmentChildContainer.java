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

import java.util.List;

import de.micromata.genome.gwiki.model.AuthorizationFailedException;
import de.micromata.genome.gwiki.page.GWikiContext;

/**
 * 
 * @author Roger Rene Kommer (r.kommer@micromata.de)
 * 
 */
public class GWikiFragmentChildContainer extends GWikiFragmentChildsBase
{
  private static final long serialVersionUID = 8561771659956368069L;

  public GWikiFragmentChildContainer()
  {
    super();
  }

  public GWikiFragmentChildContainer(GWikiFragmentChildContainer other)
  {
    super(other);
  }

  public GWikiFragmentChildContainer(List<GWikiFragment> childs)
  {
    super(childs);
  }

  @Override
  public boolean render(GWikiContext ctx)
  {
    if (childs == null) {
      return true;
    }
    for (GWikiFragment c : childs) {
      if (c.render(ctx) == false) {
        return false;
      }
    }
    return true;
  }

  @Override
  public void ensureRight(GWikiContext ctx) throws AuthorizationFailedException
  {
    if (childs == null) {
      return;
    }
    for (GWikiFragment c : childs) {
      c.ensureRight(ctx);
    }
  }

  @Override
  public void getSource(StringBuilder sb)
  {
    getChildSouce(sb);
  }

  @Override
  public int getRenderModes()
  {
    return 0;
  }

}
