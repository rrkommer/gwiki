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

import java.util.ArrayList;
import java.util.List;

import de.micromata.genome.gwiki.model.GWikiElementInfo;
import de.micromata.genome.gwiki.page.GWikiContext;
import de.micromata.genome.gwiki.page.impl.wiki.filter.GWikiUseCounterFilter;

/**
 * @author Roger Rene Kommer (r.kommer@micromata.de)
 * 
 */
public class GWikiForumPostDescription
{
  public static final String FORUM_METATEMPLATE_ID = "admin/templates/Forum1_0MetaTemplate";

  public static final String POST_METATEMPLATE_ID = "admin/templates/ForumPost1_0MetaTemplate";

  private GWikiElementInfo forum;

  private GWikiElementInfo post;

  private GWikiElementInfo firstPost;

  private List<GWikiForumPostDescription> thread = new ArrayList<GWikiForumPostDescription>();

  private List<GWikiElementInfo> allPosts = new ArrayList<GWikiElementInfo>();

  private int postIndex = -1;

  public GWikiForumPostDescription(GWikiElementInfo forum, GWikiElementInfo post)
  {
    this.forum = forum;
    this.post = post;
  }

  public GWikiForumPostDescription(GWikiElementInfo firstPost, GWikiElementInfo forum, GWikiElementInfo post)
  {
    this(forum, post);
    this.firstPost = firstPost;
  }

  protected void findFirstPost(GWikiContext wikiContext)
  {
    if (firstPost != null) {
      return;
    }
    GWikiElementInfo cp = post;
    do {
      GWikiElementInfo pi = cp.getParent(wikiContext);
      if (pi == null) {
        break;
      }
      if (pi.getMetaTemplate().getPageId().equals(POST_METATEMPLATE_ID) == false) {
        break;
      }
      cp = pi;
    } while (true);
    firstPost = cp;
  }

  public void init(GWikiContext wikiContext)
  {
    if (post == null) {
      return;
    }
    findFirstPost(wikiContext);

    // for (GWikiElementInfo ei : wikiContext.getElementFinder().getAllDirectChilds(firstPost)) {
    // GWikiForumPostDescription fp = new GWikiForumPostDescription(firstPost, forum, ei);
    // // TODO maybe not needed.
    // fp.init(wikiContext);
    // thread.add(fp);
    // }
  }

  public int getPostIndex()
  {
    if (postIndex != -1) {
      return postIndex;
    }
    return postIndex;
  }

  protected void collectAllPosts(GWikiContext wikiContext, List<GWikiElementInfo> allPosts, GWikiElementInfo pei)
  {
    allPosts.add(pei);
    for (GWikiElementInfo ei : wikiContext.getElementFinder().getAllDirectChilds(pei)) {
      collectAllPosts(wikiContext, allPosts, ei);
    }
    // allPosts.add(this);
    // for (GWikiForumPostDescription t : thread) {
    // t.collectAllPosts(allPosts);
    // }
  }

  public List<GWikiElementInfo> getAllPosts(GWikiContext wikiContext)
  {
    if (allPosts.isEmpty() == false) {
      return allPosts;
    }
    collectAllPosts(wikiContext, allPosts, firstPost);
    return allPosts;
  }

  /**
   * 
   * @return may return null if no reply
   */
  public GWikiElementInfo getLastReply(GWikiElementInfo lastReply)
  {
    if (lastReply == null) {
      lastReply = post;
    } else if (lastReply.getModifiedAt() == null
        || (post.getModifiedAt() != null && lastReply.getModifiedAt().before(post.getModifiedAt()) == true)) {
      lastReply = post;
    }
    for (GWikiForumPostDescription fd : thread) {
      lastReply = fd.getLastReply(lastReply);
    }
    return lastReply;

  }

  public int getThreadCount()
  {
    int sum = 1;
    for (GWikiForumPostDescription fd : thread) {
      sum += fd.getThreadCount();
    }
    return sum;
  }

  public int getAccessCount(GWikiContext wikiContext)
  {
    return GWikiUseCounterFilter.getUseCounter(wikiContext, post.getId());
  }

  public GWikiElementInfo getForum()
  {
    return forum;
  }

  public void setForum(GWikiElementInfo forum)
  {
    this.forum = forum;
  }

  public GWikiElementInfo getPost()
  {
    return post;
  }

  public void setPost(GWikiElementInfo post)
  {
    this.post = post;
  }

  public List<GWikiForumPostDescription> getThread()
  {
    return thread;
  }

  public void setThread(List<GWikiForumPostDescription> thread)
  {
    this.thread = thread;
  }

  public GWikiElementInfo getFirstPost()
  {
    return firstPost;
  }

  public void setFirstPost(GWikiElementInfo firstPost)
  {
    this.firstPost = firstPost;
  }

}
