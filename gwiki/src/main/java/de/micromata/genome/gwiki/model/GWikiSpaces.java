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

package de.micromata.genome.gwiki.model;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import de.micromata.genome.gwiki.model.GWikiAuthorization.UserPropStorage;
import de.micromata.genome.gwiki.page.GWikiContext;

/**
 * The Class GWikiSpaces.
 *
 * @author Roger Rene Kommer (r.kommer.extern@micromata.de)
 */
public class GWikiSpaces
{

  /**
   * The Constant ENABLE_SPACES.
   */
  public static final String ENABLE_SPACES = "ENABLE_SPACES";

  /**
   * The Constant ENABLE_LANG_SPACES.
   */
  public static final String ENABLE_LANG_SPACES = "ENABLE_LANG_SPACES";

  /**
   * The Constant DEFAULT_LANG.
   */
  public static final String DEFAULT_LANG = "DEFAULT_LANG";

  /**
   * The Constant AVAILABLE_SPACES.
   */
  public static final String AVAILABLE_SPACES = "AVAILABLE_SPACES";

  /**
   * The Constant NAV_ONLY_CURRENT_SPACE.
   */
  public static final String NAV_ONLY_CURRENT_SPACE = "NAV_ONLY_CURRENT_SPACE";

  /**
   * The Constant USER_WIKI_SPACE_PROP.
   */
  public static final String USER_WIKI_SPACE_PROP = "WIKISPACE";

  /**
   * The wiki web.
   */
  private GWikiWeb wikiWeb;

  /**
   * Instantiates a new g wiki spaces.
   *
   * @param wikiWeb the wiki web
   */
  public GWikiSpaces(GWikiWeb wikiWeb)
  {
    this.wikiWeb = wikiWeb;
  }

  /**
   * Gets the welcome page id.
   *
   * @param ctx the ctx
   * @return the welcome page id
   */
  public String getWelcomePageId(GWikiContext ctx)
  {
    String pageId = findUserSpaceWelcome(ctx);
    if (pageId != null) {
      return pageId;
    }
    pageId = wikiWeb.getAuthorization().getUserProp(ctx, "welcomePageId");
    if (pageId != null) {
      return pageId;
    }
    pageId = wikiWeb.getDaoContext().getI18nProvider().translate(ctx, "gwiki.welcomePageId", null);
    if (pageId != null) {
      return pageId;
    }
    String ret = wikiWeb.getWikiConfig().getWelcomePageId();
    if (StringUtils.isEmpty(ret) == false) {
      return ret;
    }
    return "index";
  }

  /**
   * Find user space welcome.
   *
   * @param ctx the ctx
   * @return the string
   */
  public String findUserSpaceWelcome(GWikiContext ctx)
  {
    GWikiProps props = findActiveSpaceConfig();
    if (props == null) {
      return null;
    }

    GWikiAuthorization auth = wikiWeb.getAuthorization();
    String selpace = auth.getUserProp(ctx, USER_WIKI_SPACE_PROP);
    if (StringUtils.isBlank(selpace) == true) {
      return null;
    }
    GWikiElementInfo ei = findWelcomeForSpace(ctx, auth, props, selpace);
    if (ei != null) {
      return ei.getId();
    }
    return null;
  }

  /**
   * Find personal space.
   *
   * @param ctx the ctx
   * @param selpace the selpace
   * @return the g wiki element info
   */
  public GWikiElementInfo findPersonalSpace(GWikiContext ctx, String selpace)
  {
    GWikiAuthorization auth = wikiWeb.getAuthorization();
    if (auth.isCurrentAnonUser(ctx) == false) {
      String userName = auth.getCurrentUserName(ctx);
      if (selpace == null || StringUtils.equals(userName, selpace) == true) {
        GWikiElementInfo ei = findWelcomeIndexVariants(ctx, "home/" + userName);
        if (ei != null) {
          return ei;
        }
      }
    }
    return null;
  }

  /**
   * Find welcome for space.
   *
   * @param ctx the ctx
   * @param selpace the selpace
   * @return the g wiki element info
   */
  public GWikiElementInfo findWelcomeForSpace(GWikiContext ctx, String selpace)
  {
    GWikiProps props = findActiveSpaceConfig();
    if (props == null) {
      return null;
    }
    return findWelcomeForSpace(ctx, ctx.getWikiWeb().getAuthorization(), props, selpace);
  }

  /**
   * Find welcome index variants.
   *
   * @param ctx the ctx
   * @param spacepath the spacepath
   * @return the g wiki element info
   */
  private GWikiElementInfo findWelcomeIndexVariants(GWikiContext ctx, String spacepath)
  {
    GWikiElementInfo ei = wikiWeb.findElementInfo(spacepath + "/Index");
    if (ei != null) {
      return ei;
    }
    return wikiWeb.findElementInfo(spacepath + "/index");
  }

  /**
   * Find welcome lang variants.
   *
   * @param ctx the ctx
   * @param lang the lang
   * @param spacename the spacename
   * @return the g wiki element info
   */
  private GWikiElementInfo findWelcomeLangVariants(GWikiContext ctx, String lang, String spacename)
  {
    GWikiElementInfo ei = findWelcomeIndexVariants(ctx, spacename + "/" + lang);
    if (ei != null) {
      return ei;
    }
    return findWelcomeIndexVariants(ctx, spacename + "-" + lang);
  }

  /**
   * Find welcome for space.
   *
   * @param ctx the ctx
   * @param auth the auth
   * @param props the props
   * @param selpace the selpace
   * @return the g wiki element info
   */
  private GWikiElementInfo findWelcomeForSpace(GWikiContext ctx, GWikiAuthorization auth, GWikiProps props,
      String selpace)
  {
    GWikiElementInfo ei = findPersonalSpace(ctx, selpace);
    if (ei != null) {
      return ei;
    }
    String lang = auth.getUserProp(ctx, GWikiAuthorization.USER_LANG);
    if (StringUtils.isNotBlank(lang) == true) {
      ei = findWelcomeLangVariants(ctx, lang, selpace);
      if (ei != null) {
        return ei;
      }
    }
    String defaultLang = props.getStringValue(DEFAULT_LANG, null);
    if (defaultLang != null && StringUtils.equals(lang, defaultLang) == false) {
      ei = findWelcomeLangVariants(ctx, defaultLang, selpace);
      if (ei != null) {
        return ei;
      }
    }
    ei = findWelcomeIndexVariants(ctx, selpace);
    return ei;
  }

  /**
   * Gets the user current space id.
   *
   * @param ctx the ctx
   * @return the user current space id
   */
  public String getUserCurrentSpaceId(GWikiContext ctx)
  {
    GWikiAuthorization auth = wikiWeb.getAuthorization();
    String selpace = auth.getUserProp(ctx, USER_WIKI_SPACE_PROP);
    return selpace;
  }

  /**
   * Switch user space.
   *
   * @param ctx the ctx
   * @param newSpace the new space
   * @return the string
   */
  public String switchUserSpace(GWikiContext ctx, String newSpace)
  {
    GWikiProps props = findActiveSpaceConfig();
    if (props == null) {
      return null;
    }
    GWikiElementInfo ei = findPersonalSpace(ctx, newSpace);
    if (ei == null) {
      if (props.getStringList(AVAILABLE_SPACES).contains(newSpace) == false) {
        return null;
      }
    }
    GWikiAuthorization auth = wikiWeb.getAuthorization();
    auth.setUserProp(ctx, USER_WIKI_SPACE_PROP, newSpace, UserPropStorage.Transient);
    ei = findWelcomeForSpace(ctx, auth, props, newSpace);
    if (ei != null) {
      return ei.getId();
    }
    return null;
  }

  /**
   * Show only current space in navigation.
   *
   * @return true, if successful
   */
  public boolean showOnlyCurrentSpaceInNavigation()
  {
    GWikiProps props = findActiveSpaceConfig();
    if (props == null) {
      return false;
    }
    return props.getBooleanValue(NAV_ONLY_CURRENT_SPACE);
  }

  /**
   * Gets the space ids.
   *
   * @param ctx the ctx
   * @return the space ids
   */
  public List<String> getSpaceIds(GWikiContext ctx)
  {
    List<String> ret = new ArrayList<>();
    GWikiProps props = findActiveSpaceConfig();
    if (props == null) {
      return ret;
    }
    List<String> availables = props.getStringList(AVAILABLE_SPACES);
    ret.addAll(availables);
    return ret;
  }

  /**
   * The Class SpaceInfo.
   */
  public static class SpaceInfo
  {

    /**
     * The space id.
     */
    private String spaceId;

    /**
     * The title.
     */
    private String title;

    /**
     * The page id.
     */
    private String pageId;

    /**
     * Instantiates a new space info.
     *
     * @param spaceId the space id
     * @param title the title
     * @param pageId the page id
     */
    public SpaceInfo(String spaceId, String title, String pageId)
    {
      super();
      this.spaceId = spaceId;
      this.title = title;
      this.pageId = pageId;
    }

    public String getSpaceId()
    {
      return spaceId;
    }

    public void setSpaceId(String spaceId)
    {
      this.spaceId = spaceId;
    }

    public String getTitle()
    {
      return title;
    }

    public void setTitle(String title)
    {
      this.title = title;
    }

    public String getPageId()
    {
      return pageId;
    }

    public void setPageId(String pageId)
    {
      this.pageId = pageId;
    }

  }

  /**
   * Gets the available spaces.
   *
   * @param ctx the ctx
   * @return the available spaces
   */
  public List<SpaceInfo> getAvailableSpaces(GWikiContext ctx)
  {
    List<SpaceInfo> ret = new ArrayList<>();
    GWikiProps props = findActiveSpaceConfig();
    if (props == null) {
      return ret;
    }
    GWikiAuthorization auth = wikiWeb.getAuthorization();
    List<String> availables = props.getStringList(AVAILABLE_SPACES);
    for (String avs : availables) {
      GWikiElementInfo ei = findWelcomeForSpace(ctx, auth, props, avs);
      if (ei == null) {
        continue;
      }
      if (auth.isAllowToView(ctx, ei) == false) {
        continue;
      }
      String title = ctx.getTranslatedProp(ei.getTitle());
      ret.add(new SpaceInfo(avs, title, ei.getId()));
    }
    if (auth.isCurrentAnonUser(ctx) == true) {
      return ret;
    }
    String username = auth.getCurrentUserName(ctx);
    GWikiElementInfo userhome = findPersonalSpace(ctx, null);
    if (userhome != null) {
      ret.add(new SpaceInfo(username, "Personal", userhome.getId()));
    }
    return ret;
  }

  /**
   * Find active space config.
   *
   * @return the g wiki props
   */
  private GWikiProps findActiveSpaceConfig()
  {
    GWikiElement el = wikiWeb.findElement("admin/config/SpacesConfig");
    if (el == null) {
      return null;
    }
    GWikiProps props = (GWikiProps) el.getMainPart().getCompiledObject();
    if (props.getBooleanValue(ENABLE_SPACES) == false) {
      return null;
    }
    return props;
  }

  /**
   * Checks if is page in space.
   *
   * @param ctx the ctx
   * @param ei the ei
   * @param spaceId the space id
   * @return true, if is page in space
   */
  public boolean isPageInSpace(GWikiContext ctx, GWikiElementInfo ei, String spaceId)
  {
    GWikiElementInfo spaceRoot = findWelcomeForSpace(ctx, spaceId);
    if (spaceRoot == null) {
      return false;
    }
    return ctx.getElementFinder().isChildOf(ei, spaceRoot);
  }

  /**
   * Find current page space id.
   *
   * @param ctx the ctx
   * @return the string
   */
  public String findCurrentPageSpaceId(GWikiContext ctx)
  {
    if (ctx.getCurrentElement() == null) {
      return null;
    }
    GWikiElementInfo curei = ctx.getCurrentElement().getElementInfo();
    String userSpace = getUserCurrentSpaceId(ctx);
    if (StringUtils.isNotBlank(userSpace) == true) {
      if (isPageInSpace(ctx, curei, userSpace) == true) {
        return userSpace;
      }
    }
    for (SpaceInfo spaceInfo : getAvailableSpaces(ctx)) {
      if (ctx.getElementFinder().isChildOf(curei, ctx.getWikiWeb().findElementInfo(spaceInfo.getPageId())) == true) {
        return spaceInfo.getSpaceId();
      }
    }
    return null;
  }
}
