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

import java.util.Map;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;

import de.micromata.genome.gwiki.page.GWikiContext;
import de.micromata.genome.gwiki.page.impl.wiki.GWikiBodyEvalMacro;
import de.micromata.genome.gwiki.page.impl.wiki.GWikiMacroBean;
import de.micromata.genome.gwiki.page.impl.wiki.GWikiMacroRenderFlags;
import de.micromata.genome.gwiki.page.impl.wiki.MacroAttributes;
import de.micromata.genome.gwiki.page.impl.wiki.MacroInfo;
import de.micromata.genome.gwiki.page.impl.wiki.MacroInfoParam;
import de.micromata.genome.gwiki.page.impl.wiki.fragment.GWikiFragmentLink;
import de.micromata.genome.gwiki.utils.WebUtils;

/**
 * @author Roger Rene Kommer (r.kommer@micromata.de)
 * 
 */
@MacroInfo(info = "Generates a fancy box (see http://fancybox.net/)",
    params = { @MacroInfoParam(name = "href", info = "link to image"),
        @MacroInfoParam(name = "title"), @MacroInfoParam(name = "width"),
        @MacroInfoParam(name = "height"), @MacroInfoParam(name = "padding"),
        @MacroInfoParam(name = "margin"), @MacroInfoParam(name = "overlayOpacity"), @MacroInfoParam(name = "speedIn"),
        @MacroInfoParam(name = "changeSpeed"), @MacroInfoParam(name = "autoDimensions"),
        @MacroInfoParam(name = "fitToView"),
        @MacroInfoParam(name = "autoSize")
    },
    renderFlags = { GWikiMacroRenderFlags.TrimTextContent })
public class GWikiFancyBoxMacroBean extends GWikiMacroBean implements GWikiBodyEvalMacro
{

  private static final long serialVersionUID = -3086810926057970512L;

  private static final String[] NON_FANCYBOX_OPTIONS = new String[] { "href", "title" };

  private static final String[] INT_FANCYBOX_OPTIONS = new String[] { "width", "height", "padding", "margin",
      "overlayOpacity", "speedIn",
      "changeSpeed", "autoDimensions", "fitToView", "autoSize" };

  private String href;

  private String title;

  public GWikiFancyBoxMacroBean()
  {
    setRenderModesFromAnnot();
  }

  private boolean isFencyScriptArg(String key)
  {
    return ArrayUtils.contains(NON_FANCYBOX_OPTIONS, key) == false;
  }

  private boolean isFancyIntArg(String key)
  {
    return ArrayUtils.contains(INT_FANCYBOX_OPTIONS, key);
  }

  // private boolean isFancyStringArg(String key)
  // {
  // return ArrayUtils.contains(INT_FANCYBOX_OPTIONS, key) == false;
  // }

  /*
   * (non-Javadoc)
   * 
   * @see
   * de.micromata.genome.gwiki.page.impl.wiki.GWikiMacroBean#renderImpl(de.micromata.genome.gwiki.page.GWikiContext,
   * de.micromata.genome.gwiki.page.impl.wiki.MacroAttributes)
   */
  @Override
  public boolean renderImpl(GWikiContext ctx, MacroAttributes attrs)
  {
    String id = ctx.genHtmlId("gwikifancybox");
    if (StringUtils.isBlank(href) == true) {

    }
    String url;
    if (GWikiFragmentLink.isGlobalUrl(href) == true) {
      url = href;
    } else {
      url = ctx.localUrl(href);
    }
    ctx.append("<a id=\"").append(id).append("\" href=\"").append(url).append("\" title=\"")
        .append(WebUtils.escapeHtml(title))
        .append("\">");

    attrs.getChildFragment().render(ctx);
    ctx.append("</a>");

    ctx.append(
        "<script type=\"text/javascript\">\njQuery(document).ready(function() {\n" + "$(\"#" + id + "\").fancybox({\n");
    boolean first = true;
    for (Map.Entry<String, String> me : attrs.getArgs().getMap().entrySet()) {
      if (isFencyScriptArg(me.getKey()) == false) {
        continue;
      }
      if (first == false) {
        ctx.append(",\n");
      }

      ctx.append(WebUtils.escapeJavaScript(me.getKey())).append(": ");
      if (isFancyIntArg(me.getKey()) == true) {
        ctx.append(WebUtils.escapeJavaScript(me.getValue()));
      } else {
        ctx.append("'").append(WebUtils.escapeJavaScript(me.getValue())).append("'");
      }
      first = false;
    }

    ctx.append("});\n" + "});\n" + "</script>\n");
    return true;
  }

  public String getHref()
  {
    return href;
  }

  public void setHref(String href)
  {
    this.href = href;
  }

  public String getTitle()
  {
    return title;
  }

  public void setTitle(String title)
  {
    this.title = title;
  }
}
