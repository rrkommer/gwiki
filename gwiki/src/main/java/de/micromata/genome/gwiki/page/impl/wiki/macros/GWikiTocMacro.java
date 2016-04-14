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

package de.micromata.genome.gwiki.page.impl.wiki.macros;

import java.util.ArrayList;
import java.util.List;

import de.micromata.genome.gwiki.model.GWikiArtefakt;
import de.micromata.genome.gwiki.page.GWikiContext;
import de.micromata.genome.gwiki.page.impl.GWikiWikiPageArtefakt;
import de.micromata.genome.gwiki.page.impl.wiki.GWikiMacroBean;
import de.micromata.genome.gwiki.page.impl.wiki.GWikiMacroRenderFlags;
import de.micromata.genome.gwiki.page.impl.wiki.MacroAttributes;
import de.micromata.genome.gwiki.page.impl.wiki.MacroInfo;
import de.micromata.genome.gwiki.page.impl.wiki.fragment.GWikiFragment;
import de.micromata.genome.gwiki.page.impl.wiki.fragment.GWikiFragmentHeading;
import de.micromata.genome.gwiki.page.impl.wiki.fragment.GWikiFragmentVisitor;
import de.micromata.genome.gwiki.page.impl.wiki.fragment.GWikiSimpleFragmentVisitor;

/**
 * implements to toc macro.
 * 
 * @author Roger Rene Kommer (r.kommer@micromata.de)
 * 
 */
@MacroInfo(info = "With the macro toc a table of content of the local page headings can be created.",
    renderFlags = { GWikiMacroRenderFlags.NoWrapWithP })
public class GWikiTocMacro extends GWikiMacroBean
{

  private static final long serialVersionUID = 1796882233774927873L;

  private int minLevel = 1;

  private int maxLevel = 7;

  private String exclude = null;

  public GWikiTocMacro()
  {
    setRenderModesFromAnnot();
  }

  protected int openCloseLevel(int lastLevel, int curLevel, GWikiContext sb)
  {
    int i = lastLevel;
    for (; i > curLevel; --i) {
      sb.append("</ul>");
    }
    i = lastLevel;
    for (; i < curLevel; ++i) {
      sb.append("<ul>");
    }
    return curLevel;
  }

  @Override
  public boolean renderImpl(GWikiContext ctx, MacroAttributes attrs)
  {
    GWikiArtefakt<?> af = ctx.getCurrentPart();
    if ((af instanceof GWikiWikiPageArtefakt) == false) {
      ctx.append("-- toc only works in Wiki-Parts");
      return true;
    }
    GWikiWikiPageArtefakt wik = (GWikiWikiPageArtefakt) af;
    GWikiFragment content = wik.getCompiledObject();
    final List<GWikiFragmentHeading> headings = new ArrayList<GWikiFragmentHeading>();

    GWikiFragmentVisitor colV = new GWikiSimpleFragmentVisitor()
    {

      @Override
      public void begin(GWikiFragment fragment)
      {
        if (fragment instanceof GWikiFragmentHeading) {
          headings.add((GWikiFragmentHeading) fragment);
        }
      }

    };

    content.iterate(colV, null);

    int lastLevel = 0;
    for (GWikiFragmentHeading hf : headings) {
      lastLevel = openCloseLevel(lastLevel, hf.getLevel(), ctx);
      String lt = hf.getLinkText(ctx);
      ctx.append("<li>").append("<a href=\"#").append(lt).append("\">").append(lt).append("</a>").append("</li>");
      lastLevel = hf.getLevel();
    }
    openCloseLevel(lastLevel, 0, ctx);
    return true;
  }

  public int getMinLevel()
  {
    return minLevel;
  }

  public void setMinLevel(int minLevel)
  {
    this.minLevel = minLevel;
  }

  public int getMaxLevel()
  {
    return maxLevel;
  }

  public void setMaxLevel(int maxLevel)
  {
    this.maxLevel = maxLevel;
  }

  public String getExclude()
  {
    return exclude;
  }

  public void setExclude(String exclude)
  {
    this.exclude = exclude;
  }

}
