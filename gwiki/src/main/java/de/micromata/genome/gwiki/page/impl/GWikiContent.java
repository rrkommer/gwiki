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

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import de.micromata.genome.gwiki.page.GWikiContext;
import de.micromata.genome.gwiki.page.RenderModes;
import de.micromata.genome.gwiki.page.impl.wiki.fragment.GWikiCollectMacroFragmentVisitor;
import de.micromata.genome.gwiki.page.impl.wiki.fragment.GWikiFragment;
import de.micromata.genome.gwiki.page.impl.wiki.fragment.GWikiFragmentChildContainer;
import de.micromata.genome.gwiki.page.impl.wiki.fragment.GWikiNestableFragment;
import de.micromata.genome.gwiki.page.impl.wiki.macros.GWikiChunkMacro;

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

  public boolean renderChunk(GWikiContext ctx, String chunkName)
  {
    Map<String, String> attrMap = new HashMap<String, String>();
    Locale loc = ctx.getWikiWeb().getAuthorization().getCurrentUserLocale(ctx);
    String lang = loc.getLanguage();
    attrMap.put("name", chunkName + ":" + lang);
    GWikiCollectMacroFragmentVisitor v = new GWikiCollectMacroFragmentVisitor("chunk", attrMap);
    this.iterate(v);
    if (v.getFound().isEmpty() == true) {
      attrMap.put("name", chunkName);
      v = new GWikiCollectMacroFragmentVisitor("chunk", attrMap);
      this.iterate(v);
      if (v.getFound().isEmpty() == true) {
        return true;
      }
    }
    for (GWikiFragment frag : v.getFound()) {
      GWikiNestableFragment child = (GWikiNestableFragment) frag;
      child.renderChilds(ctx);
    }
    if (RenderModes.InMem.isSet(ctx.getRenderMode()) == false) {
      ctx.flush();
    }
    return true;
  }

  public boolean render(GWikiContext ctx)
  {
    Object chunk = ctx.getRequestParameter(GWikiChunkMacro.REQUESTATTR_GWIKICHUNK);
    if (chunk == null) {
      chunk = ctx.getRequestAttribute(GWikiChunkMacro.REQUESTATTR_GWIKICHUNK);
    }
    if (chunk instanceof String) {
      return renderChunk(ctx, (String) chunk);
    }
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
