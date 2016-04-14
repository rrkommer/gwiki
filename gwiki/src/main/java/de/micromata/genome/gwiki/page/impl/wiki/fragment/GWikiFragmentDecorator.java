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

import de.micromata.genome.gwiki.page.GWikiContext;
import de.micromata.genome.gwiki.page.RenderModes;

public abstract class GWikiFragmentDecorator extends GWikiFragmentChildsBase
{

  private static final long serialVersionUID = -6329588696626151008L;

  private String prefix;

  private String suffix;

  public GWikiFragmentDecorator()
  {

  }

  public GWikiFragmentDecorator(String prefix, String suffix)
  {
    this.prefix = prefix;
    this.suffix = suffix;
  }

  public GWikiFragmentDecorator(String prefix, String suffix, List<GWikiFragment> childs)
  {
    super(childs);
    this.prefix = prefix;
    this.suffix = suffix;
  }

  public GWikiFragmentDecorator(GWikiFragmentDecorator other)
  {
    super(other);
    this.prefix = other.prefix;
    this.suffix = other.suffix;
  }

  public boolean render(GWikiContext ctx)
  {
    if (RenderModes.ForText.isSet(ctx.getRenderMode()) == false) {
      ctx.append(prefix);
    }
    renderChilds(ctx);
    if (RenderModes.ForText.isSet(ctx.getRenderMode()) == false) {
      ctx.append(suffix);
    }
    return true;
  }

  public String getPrefix()
  {
    return prefix;
  }

  public void setPrefix(String prefix)
  {
    this.prefix = prefix;
  }

  public String getSuffix()
  {
    return suffix;
  }

  public void setSuffix(String suffix)
  {
    this.suffix = suffix;
  }

}
