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

import de.micromata.genome.gwiki.model.GWikiArtefakt;
import de.micromata.genome.gwiki.model.GWikiElement;
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
    return true;
  }

  public Object onInit()
  {
    if (init() == false) {
      return null;
    }
    return null;
  }

  public void renderPost(GWikiForumPostDescription fd)
  {
    GWikiElement el = wikiContext.getWikiWeb().getElement(fd.getPost());
    GWikiArtefakt< ? > art = el.getPart("MainPage");
    if (art instanceof GWikiExecutableArtefakt< ? >) {
      ((GWikiExecutableArtefakt< ? >) art).render(wikiContext);
    } else {
      wikiContext.getWikiWeb().serveWiki(wikiContext, el);
    }
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
