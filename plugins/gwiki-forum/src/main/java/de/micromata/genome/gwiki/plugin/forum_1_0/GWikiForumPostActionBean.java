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

package de.micromata.genome.gwiki.plugin.forum_1_0;

import java.util.List;

import org.apache.commons.lang3.StringUtils;

import de.micromata.genome.gwiki.model.GWikiElementInfo;
import de.micromata.genome.gwiki.page.GWikiContext;
import de.micromata.genome.gwiki.page.impl.wiki.MacroAttributes;
import de.micromata.genome.gwiki.page.impl.wiki.macros.GWikiNewElementMacro;

/**
 * Main page view of forum.
 * 
 * @author Roger Rene Kommer (r.kommer@micromata.de)
 * 
 */
public class GWikiForumPostActionBean extends GWikiForumBaseActionBean
{
  public static final int DEFAULT_PAGE_SIZE = 10;

  /**
   * shown page num. -1 is last page.
   */
  private int pageNum = 0;

  /**
   * Number of post shown in one page.
   */
  private int pageSize = DEFAULT_PAGE_SIZE;

  private GWikiForumPostDescription description;

  private int postIndex = 0;

  @Override
  public Object onInit()
  {
    initCss();
    GWikiElementInfo post = wikiContext.getCurrentElement().getElementInfo();
    String parentId = wikiContext.getCurrentElement().getElementInfo().getParentId();
    GWikiElementInfo parent = wikiContext.getWikiWeb().findElementInfo(parentId);
    if (parent == null) {
      return null;
    }

    description = new GWikiForumPostDescription(parent, wikiContext.getCurrentElement().getElementInfo());
    description.init(wikiContext);
    List<GWikiElementInfo> allp = description.getAllPosts(wikiContext);
    postIndex = 0;
    for (int i = 0; i < allp.size(); ++i) {
      if (allp.get(i) == post) {
        postIndex = i;
        break;
      }
    }
    if (pageSize == 0) {
      pageSize = DEFAULT_PAGE_SIZE;
    }
    pageNum = postIndex / pageSize;
    return null;
  }

  public void renderPostList()
  {
    int start = pageSize * pageNum;
    int end = pageSize * (pageNum + 1);
    List<GWikiElementInfo> allp = description.getAllPosts(wikiContext);
    for (int i = start; i < allp.size() && i < end; ++i) {
      renderPost(allp.get(i));
    }
    // description.getThread();
  }

  public void renderPost(GWikiElementInfo post)
  {
    wikiContext.append("<div class=\"forumMessage\">\n");
    wikiContext.append("<div class=\"forumMessageHead\">\n");

    wikiContext.append("</div>");
    wikiContext.append("<div class=\"forumMessageBody\">\n");
    if (StringUtils.isNotEmpty(post.getTitle()) == true) {
      wikiContext.append("<h2>").appendEscText(post.getTitle()).append("</h2>\n");
    }
    wikiContext.includeArtefakt(post.getId(), "MainPage");

    wikiContext.append("</div>");
    wikiContext.append("<div class=\"forumMessageFooter\">\n");
    if (wikiContext.getWikiWeb().getAuthorization().isAllowToCreate(wikiContext, description.getFirstPost()) == true) {
      GWikiNewElementMacro nelm = new GWikiNewElementMacro();
      nelm.setMetaTemplate("admin/templates/ForumPost1_0MetaTemplate");
      nelm.setParentPage(description.getFirstPost().getId());
      nelm.setTitle("");
      nelm.setText("Reply");
      nelm.renderImpl(wikiContext, new MacroAttributes());

      nelm = new GWikiNewElementMacro() {
        protected void renderNewPageLink(GWikiContext ctx, MacroAttributes attrs)
        {
          super.renderNewPageLink(ctx, attrs);
          ctx.append("&amp;quoteParent=true");
        }
      };
      nelm.setMetaTemplate("admin/templates/ForumPost1_0MetaTemplate");
      nelm.setParentPage(description.getFirstPost().getId());
      nelm.setTitle("");
      nelm.setText("Reply & Quote");
      nelm.renderImpl(wikiContext, new MacroAttributes());
    }
    // wikiContext.append("here footer");
    wikiContext.append("</div>");
    wikiContext.append("</div>");
  }

  public void renderPost(GWikiForumPostDescription pdesc)
  {
    GWikiElementInfo post = pdesc.getPost();
    renderPost(post);
  }

  public void renderNewPostButton()
  {
    if (wikiContext.getWikiWeb().getAuthorization().isAllowToCreate(wikiContext, description.getFirstPost()) == false) {
      return;
    }
    GWikiNewElementMacro nelm = new GWikiNewElementMacro();
    nelm.setMetaTemplate("admin/templates/ForumPost1_0MetaTemplate");
    nelm.setParentPage(description.getFirstPost().getId());
    nelm.setTitle("");
    nelm.setText("New Post");
    nelm.renderImpl(wikiContext, new MacroAttributes());
  }

  public int getPageNum()
  {
    return pageNum;
  }

  public void setPageNum(int pageNum)
  {
    this.pageNum = pageNum;
  }

  public int getPageSize()
  {
    return pageSize;
  }

  public void setPageSize(int pageSize)
  {
    this.pageSize = pageSize;
  }

}
