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

package de.micromata.genome.gwiki.page.impl.wiki.rte.els;

import java.util.List;

import de.micromata.genome.gwiki.model.GWikiElementInfo;
import de.micromata.genome.gwiki.page.GWikiContext;
import de.micromata.genome.gwiki.page.impl.wiki.fragment.GWikiFragment;
import de.micromata.genome.gwiki.page.impl.wiki.fragment.GWikiFragmentLink;
import de.micromata.genome.gwiki.page.impl.wiki.parser.GWikiWikiParserContext;
import de.micromata.genome.gwiki.page.impl.wiki.rte.DomElementEvent;
import de.micromata.genome.gwiki.page.impl.wiki.rte.DomElementListener;
import de.micromata.genome.gwiki.utils.StringUtils;

public class RteLinkDomElementListener implements DomElementListener
{

  @Override
  public boolean listen(DomElementEvent event)
  {
    GWikiWikiParserContext parseContext = event.getParseContext();
    GWikiFragmentLink link = parseLink(event);
    List<GWikiFragment> frags = event.walkCollectChilds();
    link.addChilds(frags);
    parseContext.addFragment(link);
    return false;
  }

  protected GWikiFragmentLink parseLink(DomElementEvent event)
  {
    // if (StringUtils.isNotEmpty(attributes.getValue("wikitarget")) == true) {
    // parseContext.addFragment(new GWikiFragementLink(attributes.getValue("wikitarget")));
    // return;
    // }
    String href = getAttribute(event, "href", "url");
    //    String stitdefined = event.getAttr("data-wiki-titledefined");
    //    Boolean titdefined = null;
    //    if (StringUtils.isNotBlank(stitdefined) == true) {
    //      titdefined = Boolean.valueOf(stitdefined);
    //    }
    GWikiContext wikiContext = GWikiContext.getCurrent();
    String tat;
    String title = null;
    tat = getAttribute(event, "title");
    title = tat;
    String id = href;
    if (href != null && wikiContext != null) {
      String ctxpath = wikiContext.getRequest().getContextPath();
      if (href.startsWith(ctxpath) == true) {
        if (ctxpath.length() > 0) {
          id = href.substring(ctxpath.length() + 1);
        }
        if (id.startsWith("/") == true) {
          id = id.substring(1);
        }
        GWikiElementInfo ei = wikiContext.getWikiWeb().findElementInfo(id);
        if (ei == null) {
          id = href;
        } else {
          String origtitle = wikiContext.getTranslatedProp(ei.getTitle());
          if (StringUtils.equals(origtitle, title) == true) {
            title = null;
          }
        }
      }
    }
    if (id == null) {
      id = "";
    }
    GWikiFragmentLink link = new GWikiFragmentLink(id);

    if (StringUtils.isNotBlank(title) == true) {
      link.setTitle(title);
    }
    tat = getAttribute(event, "target", "windowTarget");
    if (StringUtils.isNotBlank(tat) == true) {
      link.setWindowTarget(tat);
    }
    tat = getAttribute(event, "class");
    if (StringUtils.isNotBlank(tat) == true) {
      link.setLinkClass(tat);
    }
    return link;
  }

  public static String getAttribute(DomElementEvent event, String nativeKey)
  {
    return getAttribute(event, nativeKey, nativeKey);
  }

  public static String getAttribute(DomElementEvent event, String nativeKey, String dataKey)
  {
    String ret = event.getAttr("data-wiki-" + dataKey);
    if (StringUtils.isEmpty(ret) == false) {
      return ret;
    }
    return event.getAttr(nativeKey);
  }
}
