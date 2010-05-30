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
package de.micromata.genome.gwiki.page.impl.wiki.forum;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import de.micromata.genome.gwiki.model.GWikiElementInfo;
import de.micromata.genome.gwiki.page.GWikiContext;
import de.micromata.genome.gwiki.page.impl.wiki.macros.GWikiPageCommentMacroActionBean;

/**
 * @author Roger Rene Kommer (r.kommer@micromata.de)
 * 
 */
public class GWikiForumDescription
{
  private GWikiContext wikiContext;

  private List<GWikiForumDescription> childForums = new ArrayList<GWikiForumDescription>();

  private List<GWikiElementInfo> comments = new ArrayList<GWikiElementInfo>();

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
    comments = GWikiPageCommentMacroActionBean.getCommentsForPage(wikiContext, elementInfo.getId());
    childForums = findForums(wikiContext, elementInfo.getId());
  }

  public String getTitle()
  {
    return wikiContext.getTranslatedProp(elementInfo.getTitle());
  }

  public int getThreadCount()
  {
    int count = 0;
    for (GWikiElementInfo ei : comments) {
      if (StringUtils.isEmpty(ei.getProps().getStringValue(GWikiPageCommentMacroActionBean.PROP_REPLY_TO)) == true) {
        ++count;
      }
    }
    return count;
  }

  public List<GWikiForumPostDescription> getPosts()
  {
    List<GWikiForumPostDescription> ret = new ArrayList<GWikiForumPostDescription>();
    for (GWikiElementInfo ci : comments) {
      ret.add(new GWikiForumPostDescription(elementInfo, ci));
    }
    return ret;
  }

  public int getPostCount()
  {
    return comments.size();
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

  public List<GWikiElementInfo> getComments()
  {
    return comments;
  }

  public void setComments(List<GWikiElementInfo> comments)
  {
    this.comments = comments;
  }

}
