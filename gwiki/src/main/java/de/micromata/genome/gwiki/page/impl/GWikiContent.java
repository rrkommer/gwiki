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

package de.micromata.genome.gwiki.page.impl;

import java.util.List;

import de.micromata.genome.gwiki.page.GWikiContext;
import de.micromata.genome.gwiki.page.RenderModes;
import de.micromata.genome.gwiki.page.impl.wiki.fragment.GWikiFragment;
import de.micromata.genome.gwiki.page.impl.wiki.fragment.GWikiFragmentChildContainer;

/**
 * Content of an elemenent.
 * 
 * @author Roger Rene Kommer (r.kommer@micromata.de)
 * 
 */
public class GWikiContent extends GWikiFragmentChildContainer
{

  private static final long serialVersionUID = 2245840781845976055L;

  public GWikiContent(List<GWikiFragment> fragments)
  {
    super(fragments);
  }

  public GWikiContent(GWikiContent other)
  {
    super(other);
  }

  @Override
  public Object clone()
  {
    return new GWikiContent(this);
  }

  public boolean render(GWikiContext ctx)
  {
    for (GWikiFragment f : childs) {
      if (f.render(ctx) == false)
        return false;
    }
    if (RenderModes.InMem.isSet(ctx.getRenderMode()) == false) {
      ctx.flush();
    }
    return true;
  }
}
