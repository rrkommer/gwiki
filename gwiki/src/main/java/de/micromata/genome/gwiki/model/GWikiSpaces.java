package de.micromata.genome.gwiki.model;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import de.micromata.genome.gwiki.model.GWikiAuthorization.UserPropStorage;
import de.micromata.genome.gwiki.page.GWikiContext;
import de.micromata.genome.util.types.Pair;

/**
 * 
 * @author Roger Rene Kommer (r.kommer.extern@micromata.de)
 *
 */
public class GWikiSpaces
{
  public static final String ENABLE_SPACES = "ENABLE_SPACES";
  public static final String ENABLE_LANG_SPACES = "ENABLE_LANG_SPACES";
  public static final String DEFAULT_LANG = "DEFAULT_LANG";
  public static final String AVAILABLE_SPACES = "AVAILABLE_SPACES";
  public static final String NAV_ONLY_CURRENT_SPACE = "NAV_ONLY_CURRENT_SPACE";
  public static final String USER_WIKI_SPACE_PROP = "WIKISPACE";

  private GWikiWeb wikiWeb;

  public GWikiSpaces(GWikiWeb wikiWeb)
  {
    this.wikiWeb = wikiWeb;
  }

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

  public GWikiElementInfo findWelcomeForSpace(GWikiContext ctx, String selpace)
  {
    GWikiProps props = findActiveSpaceConfig();
    if (props == null) {
      return null;
    }
    return findWelcomeForSpace(ctx, ctx.getWikiWeb().getAuthorization(), props, selpace);
  }

  private GWikiElementInfo findWelcomeIndexVariants(GWikiContext ctx, String spacepath)
  {
    GWikiElementInfo ei = wikiWeb.findElementInfo(spacepath + "/Index");
    if (ei != null) {
      return ei;
    }
    return wikiWeb.findElementInfo(spacepath + "/index");
  }

  private GWikiElementInfo findWelcomeLangVariants(GWikiContext ctx, String lang, String spacename)
  {
    GWikiElementInfo ei = findWelcomeIndexVariants(ctx, spacename + "/" + lang);
    if (ei != null) {
      return ei;
    }
    return findWelcomeIndexVariants(ctx, spacename + "-" + lang);
  }

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

  public String getUserCurrentSpaceId(GWikiContext ctx)
  {
    GWikiAuthorization auth = wikiWeb.getAuthorization();
    String selpace = auth.getUserProp(ctx, USER_WIKI_SPACE_PROP);
    return selpace;
  }

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

  public boolean showOnlyCurrentSpaceInNavigation()
  {
    GWikiProps props = findActiveSpaceConfig();
    if (props == null) {
      return false;
    }
    return props.getBooleanValue(NAV_ONLY_CURRENT_SPACE);
  }

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
   * 
   * @param ctx
   * @return List<Pair<Title,SpaceId>>
   */
  public List<Pair<String, String>> getAvailableSpaces(GWikiContext ctx)
  {
    List<Pair<String, String>> ret = new ArrayList<>();
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
      ret.add(Pair.make(title, avs));
    }
    if (auth.isCurrentAnonUser(ctx) == true) {
      return ret;
    }
    String username = auth.getCurrentUserName(ctx);
    ret.add(Pair.make("Personal", username));
    return ret;
  }

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
}
