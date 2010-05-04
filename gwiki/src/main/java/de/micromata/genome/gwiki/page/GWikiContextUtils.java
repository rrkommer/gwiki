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

package de.micromata.genome.gwiki.page;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import de.micromata.genome.gwiki.model.GWikiArtefakt;
import de.micromata.genome.gwiki.model.GWikiElement;
import de.micromata.genome.gwiki.model.GWikiElementInfo;
import de.micromata.genome.gwiki.page.impl.GWikiContent;
import de.micromata.genome.gwiki.page.impl.GWikiWikiPageArtefakt;
import de.micromata.genome.gwiki.page.impl.wiki.GWikiMacroFragment;
import de.micromata.genome.gwiki.page.impl.wiki.fragment.GWikiCollectMacroFragmentVisitor;
import de.micromata.genome.gwiki.web.GWikiServlet;

/**
 * Utitilies for dealing with GWikiContext.
 * 
 * @author Roger Rene Kommer (r.kommer@micromata.de)
 * 
 */
public class GWikiContextUtils
{
  public static void renderRequiredHtmlHeaders(GWikiContext wikiContext)
  {
    for (String s : wikiContext.getRequiredHeader()) {
      wikiContext.append(s);
    }
  }

  public static void renderRequiredJs(GWikiContext wikiContext)
  {
    for (String s : wikiContext.getRequiredJs()) {
      wikiContext.append("<script type=\"text/javascript\" src=\"" + wikiContext.localUrl(s) + "\"></script>\n");
    }
  }

  public static void renderRequiredCss(GWikiContext wikiContext)
  {
    for (String s : wikiContext.getRequiredCss()) {
      wikiContext.append("<link rel=\"stylesheet\" type=\"text/css\"  src=\"" + wikiContext.localUrl(s) + "\"/>\n");
    }
  }

  /**
   * 
   * @param path pageId or static
   * @return
   */
  public static String resolveSkinLink(String path)
  {
    GWikiContext wikiContext = GWikiContext.getCurrent();
    if (wikiContext == null) {
      return path;
    }
    return resolveSkinLink(wikiContext, path);

  }

  public static String resolveSkinLink(GWikiContext wikiContext, String path)
  {
    int idx = path.indexOf("{SKIN}/");
    if (idx == -1) {
      return path;
    }
    String skin = wikiContext.getSkin();
    String rurl = StringUtils.replace(path, "{SKIN}", skin);
    if (rurl.startsWith("/") == true) {
      rurl = rurl.substring(1);
    }
    if (rurl.startsWith("static/") == true) {
      if (GWikiServlet.INSTANCE == null) {
        return rurl;
      }
      if (GWikiServlet.INSTANCE.hasStaticResource(rurl, wikiContext) == true) {
        return rurl;
      }
      rurl = StringUtils.replace(path, "{SKIN}/", "");
      return rurl;
    } else {
      if (wikiContext.getWikiWeb().findElementInfo(rurl) != null) {
        return rurl;
      }
      rurl = StringUtils.replace(path, "{SKIN}/", "");
      return rurl;
    }
  }

  /**
   * Find a wiki artefakt from given element.
   * 
   * @param ci
   * @param ctx
   * @return null if non found.
   */
  public static GWikiWikiPageArtefakt getWikiFromElement(GWikiElementInfo ci, GWikiContext ctx)
  {
    GWikiElement el = ctx.getWikiWeb().findElement(ci.getId());
    if (el == null) {
      return null;
    }
    GWikiArtefakt< ? > ma = el.getMainPart();
    if (ma instanceof GWikiWikiPageArtefakt) {
      return (GWikiWikiPageArtefakt) ma;
    }
    Map<String, GWikiArtefakt< ? >> map = new HashMap<String, GWikiArtefakt< ? >>();
    el.collectParts(map);
    ma = map.get("MainPage");
    if (ma instanceof GWikiWikiPageArtefakt) {
      return (GWikiWikiPageArtefakt) ma;
    }
    for (GWikiArtefakt< ? > a : map.values()) {
      if (a instanceof GWikiWikiPageArtefakt) {
        return (GWikiWikiPageArtefakt) a;
      }
    }
    return null;
  }

  /**
   * render pageintro section of given page.
   * 
   * @param wikiContext
   * @return false, if current page is not compatible or does not have a pageintro inside wiki artefakt.
   */
  public static boolean renderPageIntro(String pageId, GWikiContext wikiContext)
  {
    GWikiElementInfo ei = wikiContext.getWikiWeb().findElementInfo(pageId);
    if (ei == null) {
      return false;
    }
    GWikiWikiPageArtefakt wiki = getWikiFromElement(ei, wikiContext);
    if (wiki == null) {
      return false;
    }
    GWikiCollectMacroFragmentVisitor col = new GWikiCollectMacroFragmentVisitor("pageintro");
    if (wiki.compileFragements(wikiContext) == false) {
      return false;
    }
    GWikiContent cont = wiki.getCompiledObject();
    cont.iterate(col);
    if (col.getFound().isEmpty() == false) {
      GWikiMacroFragment mf = (GWikiMacroFragment) col.getFound().get(0);
      mf.renderChilds(wikiContext);
      return true;
    }
    return false;
  }

  /**
   * render pageintro section of current page.
   * 
   * @param wikiContext
   * @return false, if current page is not compatible or does not have a pageintro inside wiki artefakt.
   */
  public static boolean renderCurrentPageIntro(GWikiContext wikiContext)
  {
    if (wikiContext.getCurrentElement() == null) {
      return false;
    }
    return renderPageIntro(wikiContext.getCurrentElement().getElementInfo().getId(), wikiContext);
  }
}
