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

package de.micromata.genome.gwiki.page.impl.wiki.macros;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import de.micromata.genome.gwiki.model.GWikiArtefakt;
import de.micromata.genome.gwiki.model.GWikiElement;
import de.micromata.genome.gwiki.model.GWikiExecutableArtefakt;
import de.micromata.genome.gwiki.page.GWikiContext;
import de.micromata.genome.gwiki.page.impl.wiki.GWikiMacroBean;
import de.micromata.genome.gwiki.page.impl.wiki.GWikiMacroInfo.MacroParamType;
import de.micromata.genome.gwiki.page.impl.wiki.GWikiRuntimeMacro;
import de.micromata.genome.gwiki.page.impl.wiki.MacroAttributes;
import de.micromata.genome.gwiki.page.impl.wiki.MacroInfo;
import de.micromata.genome.gwiki.page.impl.wiki.MacroInfoParam;
import de.micromata.genome.gwiki.page.impl.wiki.fragment.GWikiFragmentHeading;
import de.micromata.genome.gwiki.page.impl.wiki.fragment.GWikiFragmentText;

/**
 * Includes another Page.
 * 
 * 
 * 
 * @author Roger Rene Kommer (r.kommer@micromata.de)
 * 
 */
@MacroInfo(info = "Includes another page.",
    params = {
        @MacroInfoParam(name = "pageId", type = MacroParamType.PageId, required = true, info = "Page to include"),
        @MacroInfoParam(name = "partName", info = "Part Name to include"),
        @MacroInfoParam(name = "chunk", info = "Chunk name to include.<br/>"
            + "The chunks has to be defined with chunk macro in included page."),
        @MacroInfoParam(name = "complete", info = "Include complete page including frame decorations.",
            defaultValue = "false"),
        @MacroInfoParam(name = "titleHeading", type = MacroParamType.Integer,
            info = " if set include the title of the included page with given heading") })
public class GWikiIncludeMacro extends GWikiMacroBean implements GWikiRuntimeMacro
{
  private static final long serialVersionUID = -1172470071868033038L;

  /**
   * required page id to include
   */
  private String pageId;

  /**
   * Name of the part of the element. The part must implement the GWikiExecutableArtefakt interface.
   * 
   * Default is MainPage.
   */
  private String partName;

  /**
   * if given the chunk inside the Wiki page.
   */
  private String chunk;

  /**
   * Does not include only artefakt of element, but the complate page.
   * 
   * Default is false.
   */
  private boolean complete;

  /**
   * if set include the title of the included page with given heading
   */
  private int titleHeading = 0;

  protected void renderPart(GWikiContext ctx, MacroAttributes attrs, GWikiArtefakt<?> art)
  {
    if (StringUtils.isEmpty(chunk) == false) {
      ctx.setRequestAttribute(GWikiChunkMacro.REQUESTATTR_GWIKICHUNK, chunk);
    }
    ((GWikiExecutableArtefakt<?>) art).render(ctx);
  }

  @Override
  public boolean renderImpl(GWikiContext ctx, MacroAttributes attrs)
  {
    if (StringUtils.isEmpty(pageId) == true) {
      pageId = attrs.getArgs().getStringValue(MacroAttributes.DEFAULT_VALUE_KEY);
    }
    GWikiElement el = ctx.getWikiWeb().findElement(pageId);
    if (el == null) {
      renderErrorMessage(ctx, "include; Cannot find page with: " + pageId, attrs);
      return true;
    }
    if (complete == true) {
      if (StringUtils.isEmpty(chunk) == false) {
        ctx.setRequestAttribute(GWikiChunkMacro.REQUESTATTR_GWIKICHUNK, chunk);
      }
      ctx.getWikiWeb().serveWiki(pageId, ctx);
      return true;
    }
    if (titleHeading != 0) {
      GWikiFragmentHeading heading = new GWikiFragmentHeading(titleHeading);
      heading.addChild(new GWikiFragmentText(ctx.getTranslatedProp(el.getElementInfo().getTitle())));
      heading.render(ctx);
    }
    Map<String, GWikiArtefakt<?>> parts = new HashMap<String, GWikiArtefakt<?>>();
    el.collectParts(parts);
    if (StringUtils.isNotEmpty(partName) == true) {
      GWikiArtefakt<?> art = parts.get(partName);
      if (art == null) {
        renderErrorMessage(ctx, "include; Cannot find part " + partName + " in  " + pageId, attrs);
        return true;
      }
      if ((art instanceof GWikiExecutableArtefakt) == false) {
        renderErrorMessage(ctx, "include; Part is not executable: " + partName + " in  " + pageId, attrs);
        return true;
      }
      renderPart(ctx, attrs, art);
    } else {
      String lPart = "MainPage";
      if (parts.get(lPart) instanceof GWikiExecutableArtefakt) {
        renderPart(ctx, attrs, parts.get(lPart));
      } else {
        renderErrorMessage(ctx, "include; Cannot find executable Part MainPage in  " + pageId, attrs);
      }
    }
    return true;
  }

  public String getPageId()
  {
    return pageId;
  }

  public void setPageId(String pageId)
  {
    this.pageId = pageId;
  }

  public String getPartName()
  {
    return partName;
  }

  public void setPartName(String partName)
  {
    this.partName = partName;
  }

  public boolean isComplete()
  {
    return complete;
  }

  public void setComplete(boolean complete)
  {
    this.complete = complete;
  }

  public String getChunk()
  {
    return chunk;
  }

  public void setChunk(String chunk)
  {
    this.chunk = chunk;
  }

  public int getTitleHeading()
  {
    return titleHeading;
  }

  public void setTitleHeading(int titleHeading)
  {
    this.titleHeading = titleHeading;
  }

}
