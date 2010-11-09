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
import de.micromata.genome.gwiki.page.impl.wiki.MacroAttributes;
import de.micromata.genome.gwiki.page.impl.wiki.macros.GWikiNewElementMacro;

/**
 * Main page view of forum.
 * 
 * @author Roger Rene Kommer (r.kommer@micromata.de)
 * 
 */
public class GWikiForumActionBean extends GWikiForumBaseActionBean
{
  protected GWikiForumDescription forumDescription;

  protected String pageId;

  protected boolean init()
  {
    pageId = wikiContext.getCurrentElement().getElementInfo().getId();
    forumDescription = new GWikiForumDescription(wikiContext, wikiContext.getCurrentElement().getElementInfo());
    return true;
  }

  public Object onInit()
  {
    initCss();
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
    int c = fd.getAccessCount(wikiContext);
    wikiContext.append(wikiContext.renderLocalUrl(ei.getId())) //
        .append("</span>") //
        .append("<br/>") //
        .append("<span class=\"forumPostVersion\">") //
        .append("from ").append(esc(ei.getCreatedBy())) //
        .append(" (").append(wikiContext.getUserDateString(ei.getCreatedAt())).append(")") //
        .append("</span>") //
        .append("</td>") //
        .append("<td>") //
        .append(fd.getThreadCount() - 1).append("</td><td>") //
        .append(c) //
        .append("</td><td>");
    GWikiElementInfo lastReply = fd.getLastReply(null);
    if (lastReply == fd.getPost()) {
      wikiContext.append("no reply");
    } else {
      wikiContext.append("from ").append(lastReply.getCreatedBy()).append("<br/>")//
          .append(" (").append(wikiContext.getUserDateString(ei.getCreatedAt())).append(")") //
      ;
    }
    wikiContext.append("</td>");
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

  public void renderNewPostButton()
  {
    if (wikiContext.getWikiWeb().getAuthorization().isAllowToCreate(wikiContext, getForumDescription().getElementInfo()) == false) {
      return;
    }
    GWikiNewElementMacro nelm = new GWikiNewElementMacro();
    nelm.setEditPageId("forum1_0/EditPost");
    nelm.setMetaTemplate("admin/templates/ForumPost1_0MetaTemplate");
    nelm.setParentPage(getForumDescription().getElementInfo().getId());
    nelm.setTitle("");
    nelm.setText("New Post");
    nelm.renderImpl(wikiContext, new MacroAttributes());
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
