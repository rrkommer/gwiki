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
package de.micromata.genome.gwiki.model;

import java.util.List;

import de.micromata.genome.gwiki.page.GWikiContext;

/**
 * Standard implementation for GWikiMenuProvider.
 * 
 * @author Roger Rene Kommer (r.kommer@micromata.de)
 * 
 */
public class GWikiStandardMenuProvider implements GWikiMenuProvider
{

  protected GWikiMenu createMenuFromElement(GWikiContext wikiContext, String pageId)
  {
    GWikiElementInfo ei = wikiContext.getWikiWeb().findElementInfo(pageId);
    if (ei == null) {
      return null;
    }
    return createMenuFromElement(wikiContext, ei);
  }

  protected GWikiMenu createMenuFromElement(GWikiContext wikiContext, GWikiElementInfo ei)
  {
    GWikiAuthorization auth = wikiContext.getWikiWeb().getAuthorization();
    if (auth.isAllowToView(wikiContext, ei) == false) {
      return null;
    }
    GWikiMenu menu = new GWikiMenu();
    menu.setLabel(wikiContext.getTranslatedProp(ei.getTitle()));
    menu.setUrl(wikiContext.localUrl(ei.getId()));
    menu.setIconMedium(ei.getProps().getStringValue(GWikiPropKeys.ICON_MEDIUM, null));
    return menu;
  }

  protected void addSubMenuFromElement(GWikiMenu menu, GWikiContext wikiContext, String pageId)
  {
    GWikiMenu sm = createMenuFromElement(wikiContext, pageId);
    if (sm != null) {
      menu.getChildren().add(sm);
    }
  }

  protected GWikiMenu getMenuWithChildsFromElement(GWikiContext wikiContext, GWikiElementInfo ei)
  {
    GWikiMenu menu = createMenuFromElement(wikiContext, ei);
    if (menu == null) {
      return null;
    }
    List<GWikiElementInfo> childs = wikiContext.getElementFinder().getPageDirectPages(ei.getId());
    for (GWikiElementInfo ci : childs) {
      GWikiMenu cm = getMenuWithChildsFromElement(wikiContext, ci);
      if (cm != null) {
        menu.getChildren().add(cm);
      }
    }
    return menu;
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.micromata.genome.gwiki.model.GWikiMenuProvider#getAdminMenu()
   */
  public GWikiMenu getAdminMenu(GWikiContext wikiContext)
  {
    if (wikiContext.getWikiWeb().getAuthorization().isAllowTo(wikiContext, GWikiAuthorizationRights.GWIKI_ADMIN.name()) == false
        && wikiContext.getWikiWeb().getAuthorization().isAllowTo(wikiContext, GWikiAuthorizationRights.GWIKI_DEVELOPER.name()) == false) {
      return null;
    }
    GWikiElementInfo ei = wikiContext.getWikiWeb().findElementInfo("admin/Index");
    if (ei == null) {
      return null;
    }
    GWikiMenu adminMenu = getMenuWithChildsFromElement(wikiContext, ei);
    if (adminMenu == null) {
      return null;
    }
    if (adminMenu.getChildren().isEmpty() == true) {
      return null;
    }
    adminMenu.setIconMedium(wikiContext.localUrl("/inc/gwiki/img/icons/heart16.png"));

    if (wikiContext.getWikiWeb().getAuthorization().isAllowTo(wikiContext, GWikiAuthorizationRights.GWIKI_FSWEBDAV.name()) == true) {
      GWikiMenu menu = new GWikiMenu();
      menu.setLabel(wikiContext.getTranslated("gwiki.page.AllPages.menu.webdav"));
      menu.setUrl(wikiContext.localUrl("/dav/"));
      menu.setTarget("_blank");
      adminMenu.getChildren().add(menu);
    }
    return adminMenu;
  }

  public GWikiMenu getUserMenu(GWikiContext wikiContext)
  {
    GWikiMenu menu = new GWikiMenu();
    menu.setIconMedium(wikiContext.localUrl("/inc/gwiki/img/icons/user16.png"));
    if (wikiContext.getWikiWeb().getAuthorization().isAllowTo(wikiContext, GWikiAuthorizationRights.GWIKI_VIEWPAGES.name()) == false) {
      menu.setLabel("Login");
      menu.setUrl(wikiContext.localUrl("/admin/Login"));
      return menu;
    }
    menu.setLabel(wikiContext.getWikiWeb().getAuthorization().getCurrentUserName(wikiContext));
    GWikiMenu sm = createMenuFromElement(wikiContext, "edit/ChangeNotification");
    if (sm != null && wikiContext.getCurrentElement() != null) {
      sm.addUrlParam("pageId", wikiContext.getCurrentElement().getElementInfo().getId());
      menu.getChildren().add(sm);
    }

    if (wikiContext.getWikiWeb().getAuthorization().isAllowTo(wikiContext, GWikiAuthorizationRights.GWIKI_CREATEPAGES.name()) == true) {
      GWikiMenu hm = new GWikiMenu();
      hm.setLabel(wikiContext.getTranslated("gwiki.page.headmenu.privatespace.label"));
      hm.setUrl(wikiContext.localUrl("/home/" + wikiContext.getWikiWeb().getAuthorization().getCurrentUserName(wikiContext) + "/index"));
      menu.getChildren().add(hm);
    }
    if (de.micromata.genome.gwiki.umgmt.GWikiUserAuthorization.isOwnUser(wikiContext) == true) {
      GWikiMenu pm = createMenuFromElement(wikiContext, "edit/UserProfile");
      if (pm != null && wikiContext.getCurrentElement() != null) {
        pm.addUrlParam("backUrl", wikiContext.getCurrentElement().getElementInfo().getId());
      }
      if (pm != null) {
        menu.getChildren().add(pm);
      }
    }
    menu.getChildren().add(GWikiMenu.createDivider());
    addSubMenuFromElement(menu, wikiContext, "admin/Logout");
    return menu;
  }
}
