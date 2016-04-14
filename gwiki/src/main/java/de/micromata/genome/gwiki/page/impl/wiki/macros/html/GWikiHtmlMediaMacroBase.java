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

package de.micromata.genome.gwiki.page.impl.wiki.macros.html;

import de.micromata.genome.gwiki.page.GWikiContext;
import de.micromata.genome.gwiki.page.impl.wiki.GWikiMacroInfo.MacroParamType;
import de.micromata.genome.gwiki.page.impl.wiki.MacroAttributes;
import de.micromata.genome.gwiki.page.impl.wiki.MacroInfo;
import de.micromata.genome.gwiki.page.impl.wiki.MacroInfoParam;
import de.micromata.genome.gwiki.page.impl.wiki.macros.GWikiHtmlTagMacro;

@MacroInfo(params = {
    @MacroInfoParam(name = "source", required = true, info = "URL or PageId to content (audio or video."),
    @MacroInfoParam(name = "width", info = "Width of the control"),
    @MacroInfoParam(name = "height", info = "Width of the control"),
    @MacroInfoParam(name = "preload", info = "Content should be preloaded",
        enumValues = { "auto", "none", "metadata" }),
    @MacroInfoParam(name = "controls", info = "Show controls", type = MacroParamType.Boolean)
})
public class GWikiHtmlMediaMacroBase extends GWikiHtmlTagMacro
{
  private String width;
  private String height;
  private String source;
  private String preload;
  private String controls;

  @Override
  protected void populate(MacroAttributes attrs, GWikiContext ctx)
  {
    super.populate(attrs, ctx);
    attrs.getArgs().remove("source");
  }

  @Override
  public boolean renderImpl(GWikiContext ctx, MacroAttributes attrs)
  {
    ctx.append("<", attrs.getCmd());
    renderAttributes(ctx, attrs.getArgs().getMap());
    ctx.append(">");
    ctx.append("<source src='").append(ctx.escape(source)).append("'/>");
    ctx.append("</").append(attrs.getCmd()).append(">");
    return true;
  }

  public String getWidth()
  {
    return width;
  }

  public void setWidth(String width)
  {
    this.width = width;
  }

  public String getHeight()
  {
    return height;
  }

  public void setHeight(String height)
  {
    this.height = height;
  }

  public String getSource()
  {
    return source;
  }

  public void setSource(String source)
  {
    this.source = source;
  }

  public String getPreload()
  {
    return preload;
  }

  public void setPreload(String preload)
  {
    this.preload = preload;
  }

  public String getControls()
  {
    return controls;
  }

  public void setControls(String controls)
  {
    this.controls = controls;
  }

}
