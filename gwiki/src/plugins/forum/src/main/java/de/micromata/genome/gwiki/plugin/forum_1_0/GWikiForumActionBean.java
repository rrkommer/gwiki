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
package de.micromata.genome.gwiki.plugin.forum_1_0;

import de.micromata.genome.gwiki.model.GWikiArtefakt;
import de.micromata.genome.gwiki.model.GWikiElement;
import de.micromata.genome.gwiki.model.GWikiElementInfo;
import de.micromata.genome.gwiki.model.GWikiExecutableArtefakt;
import de.micromata.genome.gwiki.page.impl.actionbean.ActionBeanBase;

/**
 * Main page view of forum.
 * 
 * @author Roger Rene Kommer (r.kommer@micromata.de)
 * 
 */
public class GWikiForumActionBean extends ActionBeanBase
{
  protected GWikiForumDescription forumDescription;

  private String pageId;

  protected boolean init()
  {
    pageId = wikiContext.getCurrentElement().getElementInfo().getId();
    forumDescription = new GWikiForumDescription(wikiContext, wikiContext.getCurrentElement().getElementInfo());
    String cssId = "inc/gwiki/css/forum.css";
    String styleCss = "inc/" + wikiContext.getSkin() + "/css/forum.css";
    if (wikiContext.getWikiWeb().findElementInfo(styleCss) != null) {
      cssId = styleCss;
    }
    wikiContext.getRequiredCss().add(cssId);
    return true;
  }

  public Object onInit()
  {
    if (init() == false) {
      return null;
    }
    return null;
  }

  public void renderPostHeader(GWikiForumPostDescription fd)
  {
    wikiContext.append("<td><span class=\"forumPostTitle\">");
    GWikiElementInfo ei = fd.getPost();
    // GWikiElementInfo ei = wikiContext.getWikiWeb().getElementInfo();
    wikiContext.append(esc(ei.getTitle())) //
        .append("</span>") //
        .append("<br/>") //
        .append("<span class=\"forumPostVersion\">") //
        .append("from ").append(esc(ei.getCreatedBy())) //
        .append(" (").append(wikiContext.getUserDateString(ei.getCreatedAt())).append(")") //
        .append("</span>") //
        .append("</td>") //
    ;

  }

  public void renderPost(GWikiForumPostDescription fd)
  {
    wikiContext.append("<td>");
    GWikiElement el = wikiContext.getWikiWeb().getElement(fd.getPost());
    GWikiArtefakt< ? > art = el.getPart("MainPage");
    if (art instanceof GWikiExecutableArtefakt< ? >) {
      ((GWikiExecutableArtefakt< ? >) art).render(wikiContext);
    } else {
      wikiContext.getWikiWeb().serveWiki(wikiContext, el);
    }
    wikiContext.append("<td>");
  }

  public GWikiForumDescription getForumDescription()
  {
    return forumDescription;
  }

  public void setForumDescription(GWikiForumDescription forumDescription)
  {
    this.forumDescription = forumDescription;
  }

}
