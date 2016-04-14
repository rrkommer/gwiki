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

package de.micromata.genome.gwiki.plugin.blog_1_0;

import org.apache.commons.lang.StringUtils;

import de.micromata.genome.gwiki.model.GWikiElementInfo;

/**
 * Controler for a blog entry page.
 * 
 * @author Roger Rene Kommer (r.kommer@micromata.de)
 * 
 */
public class GWikiBlogEntryAction extends GWikiBlogBaseActionBean
{
  public boolean init()
  {
    blogPageId = wikiContext.getCurrentElement().getElementInfo().getParentId();
    if (StringUtils.isNotEmpty(blogPageId) == true) {
      blogPage = wikiContext.getWikiWeb().findElementInfo(blogPageId);
    }
    return super.init();
  }

  public void renderNextPrevPage()
  {
    GWikiElementInfo pe = null;
    GWikiElementInfo ne = null;
    if (blogPage == null) {
      return;
    }
    for (int i = 0; i < blogEntries.size(); ++i) {
      GWikiElementInfo ce = blogEntries.get(i);
      if (ce.getId().equals(wikiContext.getCurrentElement().getElementInfo().getId()) == true) {
        if (i > 0) {
          pe = blogEntries.get(i - 1);
        }
        if (i + 1 < blogEntries.size()) {
          ne = blogEntries.get(i + 1);
        }
        break;
      }
    }
    if (pe == null && ne == null) {
      return;
    }
    wikiContext.append("<div class=\"blogPageScroll\">");
    if (pe != null) {
      wikiContext.append(wikiContext.renderExistingLink(pe, "<< " + wikiContext.getTranslatedProp(pe.getTitle()), null));
    }
    if (ne != null) {
      if (pe != null) {
        wikiContext.append("&nbsp;|&nbsp;");
      }
      wikiContext.append(wikiContext.renderExistingLink(ne, wikiContext.getTranslatedProp(ne.getTitle()) + " >>", null));
    }
    wikiContext.append("</div>");
  }

  @Override
  public Object onInit()
  {
    if (init() == false) {
      return null;
    }
    return null;
  }
}
