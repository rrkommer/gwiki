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
import de.micromata.genome.gwiki.page.impl.wiki.fragment.GWikiFragment;
import de.micromata.genome.gwiki.web.GWikiServlet;

/**
 * The Class GWikiContextUtils.
 */
/*
 * with GWikiContext.
 * 
 * @author Roger Rene Kommer (r.kommer@micromata.de)
 * 
 */
public class GWikiContextUtils
{

  /**
   * Render required html headers.
   *
   * @param wikiContext the wiki context
   */
  public static void renderRequiredHtmlHeaders(GWikiContext wikiContext)
  {
    for (String s : wikiContext.getRequiredHeader()) {
      wikiContext.append(s);
    }
  }

  /**
   * Render required js.
   *
   * @param wikiContext the wiki context
   */
  public static void renderRequiredJs(GWikiContext wikiContext)
  {
    for (String s : wikiContext.getRequiredJs()) {
      wikiContext.append("<script type=\"text/javascript\" src=\"" + wikiContext.localUrl(s) + "\"></script>\n");
    }
  }

  /**
   * Render required css.
   *
   * @param wikiContext the wiki context
   */
  public static void renderRequiredCss(GWikiContext wikiContext)
  {
    for (String s : wikiContext.getRequiredCss()) {
      wikiContext.append("<link rel=\"stylesheet\" type=\"text/css\"  href=\"" + wikiContext.localUrl(s) + "\"/>\n");
    }
  }

  /**
   * Resolve skin link.
   *
   * @param path pageId or static
   * @return the string
   */
  public static String resolveSkinLink(String path)
  {
    GWikiContext wikiContext = GWikiContext.getCurrent();
    if (wikiContext == null) {
      return path;
    }
    return resolveSkinLink(wikiContext, path);

  }

  /**
   * Resolve skin link.
   *
   * @param wikiContext the wiki context
   * @param path the path
   * @return the string
   */
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
   * Gets the wiki from element.
   *
   * @param ei the ei
   * @param ctx the ctx
   * @return the wiki from element
   */
  public static GWikiWikiPageArtefakt getWikiFromElement(GWikiElementInfo ei, GWikiContext ctx)
  {
    GWikiElement el = ctx.getWikiWeb().findElement(ei.getId());
    if (el == null) {
      return null;
    }
    return getWikiFromElement(el, ctx);
  }

  /**
   * Find a wiki artefakt from given element.
   *
   * @param el the el
   * @param ctx the ctx
   * @return null if non found.
   */

  public static GWikiWikiPageArtefakt getWikiFromElement(GWikiElement el, GWikiContext ctx)
  {
    if (el == null) {
      return null;
    }
    GWikiArtefakt<?> ma = el.getMainPart();
    if (ma instanceof GWikiWikiPageArtefakt) {
      return (GWikiWikiPageArtefakt) ma;
    }
    Map<String, GWikiArtefakt<?>> map = new HashMap<String, GWikiArtefakt<?>>();
    el.collectParts(map);
    ma = map.get("MainPage");
    if (ma instanceof GWikiWikiPageArtefakt) {
      return (GWikiWikiPageArtefakt) ma;
    }
    for (GWikiArtefakt<?> a : map.values()) {
      if (a instanceof GWikiWikiPageArtefakt) {
        return (GWikiWikiPageArtefakt) a;
      }
    }
    return null;
  }

  /**
   * Interface to iterate through visitor.
   * 
   * @author Roger Rene Kommer (r.kommer@micromata.de)
   * 
   */
  public static interface FragmentVisitor
  {

    /**
     * Visit.
     *
     * @param wikiContext the wiki context
     * @param element the element
     * @param wikiArtefakt the wiki artefakt
     * @param fragment the fragment
     * @return false, if stop iterating.
     */
    boolean visit(GWikiContext wikiContext, GWikiElement element, GWikiWikiPageArtefakt wikiArtefakt,
        GWikiFragment fragment);
  }

  /**
   * Internal exception to stop callback loop.
   * 
   * @author Roger Rene Kommer (r.kommer@micromata.de)
   * 
   */
  public static class StopSearchException extends RuntimeException
  {

    /**
     * The Constant serialVersionUID.
     */
    private static final long serialVersionUID = -8125005225862685066L;

  }

  /**
   * Do with macro fragment.
   *
   * @param wikiContext required
   * @param pageId if null, uses current page
   * @param partName if null, try to figure out part with wiki.
   * @param macroName Macro to find.
   * @param attributeMatcher can be null.
   * @param visitor the visitor
   * @return true, if successful
   */
  public static boolean doWithMacroFragment(final GWikiContext wikiContext, String pageId, String partName,
      String macroName,
      Map<String, String> attributeMatcher, final FragmentVisitor visitor)
  {
    GWikiElement element = null;
    if (StringUtils.isEmpty(pageId) == true) {
      element = wikiContext.getCurrentElement();
    } else {
      element = wikiContext.getWikiWeb().findElement(pageId);
    }
    if (element == null) {
      return false;
    }
    final GWikiElement cel = element;
    final GWikiWikiPageArtefakt wiki;
    if (StringUtils.isEmpty(partName) == true) {
      wiki = getWikiFromElement(element, wikiContext);
    } else {
      GWikiArtefakt<?> art = element.getPart(partName);
      if ((art instanceof GWikiWikiPageArtefakt) == false) {
        return false;
      }
      wiki = (GWikiWikiPageArtefakt) art;
    }
    if (wiki == null) {
      return false;
    }
    if (wiki.compileFragements(wikiContext) == false) {
      return false;
    }

    GWikiCollectMacroFragmentVisitor col = new GWikiCollectMacroFragmentVisitor(macroName, attributeMatcher)
    {

      @Override
      protected void addFragment(GWikiFragment fragment)
      {
        if (visitor.visit(wikiContext, cel, wiki, fragment) == false) {
          throw new StopSearchException();
        }
      }
    };
    try {
      GWikiContent cont = wiki.getCompiledObject();
      cont.iterate(col);
    } catch (StopSearchException se) {
      return true;
    }
    return false;
  }

  /**
   * render first macro body of given name of given page.
   *
   * @param pageId the page id
   * @param wikiContext the wiki context
   * @param macroName the macro name
   * @return false, if current page is not compatible or does not have a pageintro inside wiki artefakt.
   */
  public static boolean renderPageIntro(final String pageId, final GWikiContext wikiContext, final String macroName)
  {
    return doWithMacroFragment(wikiContext, null, null, macroName, null, new FragmentVisitor()
    {

      @Override
      public boolean visit(GWikiContext wikiContext, GWikiElement element, GWikiWikiPageArtefakt wikiArtefakt,
          GWikiFragment fragment)
      {
        GWikiMacroFragment mf = (GWikiMacroFragment) fragment;
        mf.renderChilds(wikiContext);
        return false;
      }
    });
  }

  /**
   * render first macro body of given name of current page.
   *
   * @param wikiContext the wiki context
   * @param macroName the macro name
   * @return false, if current page is not compatible or does not have a pageintro inside wiki artefakt.
   */
  public static boolean renderCurrentMacroContent(GWikiContext wikiContext, String macroName)
  {
    if (wikiContext.getCurrentElement() == null) {
      return false;
    }
    return renderPageIntro(wikiContext.getCurrentElement().getElementInfo().getId(), wikiContext, macroName);
  }
}
