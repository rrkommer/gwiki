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

import org.apache.commons.lang.StringUtils;

import de.micromata.genome.gwiki.model.GWikiElementInfo;
import de.micromata.genome.gwiki.page.GWikiContext;

/**
 * @author Roger Rene Kommer (r.kommer@micromata.de)
 * 
 */
public class GWikiForumDescription
{
  private GWikiContext wikiContext;

  private List<GWikiForumDescription> childForums = new ArrayList<GWikiForumDescription>();

  private List<GWikiForumPostDescription> posts = new ArrayList<GWikiForumPostDescription>();

  private GWikiElementInfo elementInfo;

  public static List<GWikiForumDescription> findForums(GWikiContext wikiContext, String pageId)
  {
    List<GWikiForumDescription> ret = new ArrayList<GWikiForumDescription>();
    List<GWikiElementInfo> eis = wikiContext.getElementFinder().getPageDirectPages(pageId);
    for (GWikiElementInfo ei : eis) {
      GWikiForumDescription fd = new GWikiForumDescription(wikiContext, ei);
      ret.add(fd);
    }
    return ret;
  }

  public GWikiForumDescription()
  {

  }

  public GWikiForumDescription(GWikiContext wikiContext, GWikiElementInfo elementInfo)
  {
    this.wikiContext = wikiContext;
    this.elementInfo = elementInfo;

    List<GWikiElementInfo> eis = wikiContext.getElementFinder().getPageDirectPages(elementInfo.getId());
    for (GWikiElementInfo ei : eis) {
      String mtid = "";
      if (ei.getMetaTemplate() != null) {
        mtid = ei.getMetaTemplate().getPageId();
      }
      if (StringUtils.equals(mtid, "admin/templates/Forum1_0MetaTemplate") == true) {
        GWikiForumDescription fd = new GWikiForumDescription(wikiContext, ei);
        childForums.add(fd);
      } else if (StringUtils.equals(mtid, "admin/templates/ForumPost1_0MetaTemplate") == true) {
        GWikiForumPostDescription postdesc = new GWikiForumPostDescription(elementInfo, ei);
        posts.add(postdesc);
      }
    }
  }

  public String getTitle()
  {
    return wikiContext.getTranslatedProp(elementInfo.getTitle());
  }

  public int getThreadCount()
  {
    int sum = 0;
    for (GWikiForumPostDescription fd : posts) {
      sum += fd.getThreadCount();
    }
    return sum;
  }

  public List<GWikiForumPostDescription> getPosts()
  {
    return posts;
  }

  public int getPostCount()
  {
    return posts.size();
  }

  public List<GWikiForumDescription> getChildForums()
  {
    return childForums;
  }

  public void setChildForums(List<GWikiForumDescription> childForums)
  {
    this.childForums = childForums;
  }

  public GWikiElementInfo getElementInfo()
  {
    return elementInfo;
  }

  public void setElementInfo(GWikiElementInfo elementInfo)
  {
    this.elementInfo = elementInfo;
  }

}
