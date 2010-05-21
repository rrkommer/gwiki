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
package de.micromata.genome.gwiki.page.impl.wiki.macros;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import org.apache.commons.lang.StringUtils;

import de.micromata.genome.gwiki.model.GWikiArtefakt;
import de.micromata.genome.gwiki.model.GWikiElement;
import de.micromata.genome.gwiki.model.GWikiElementInfo;
import de.micromata.genome.gwiki.page.GWikiContext;
import de.micromata.genome.gwiki.page.impl.GWikiWikiPageArtefakt;
import de.micromata.genome.gwiki.page.impl.wiki.GWikiMacroBean;
import de.micromata.genome.gwiki.page.impl.wiki.MacroAttributes;
import de.micromata.genome.gwiki.page.impl.wiki.fragment.GWikiFragment;
import de.micromata.genome.gwiki.page.impl.wiki.fragment.GWikiFragmentLink;
import de.micromata.genome.gwiki.page.impl.wiki.fragment.GWikiSimpleFragmentVisitor;
import de.micromata.genome.gwiki.page.search.QueryResult;
import de.micromata.genome.gwiki.page.search.SearchQuery;
import de.micromata.genome.gwiki.page.search.SearchResult;

/**
 * Find orphan links in wiki pages.
 * 
 * @author Roger Rene Kommer (r.kommer@micromata.de)
 * 
 */
public class GWikiOrphanLinksMacro extends GWikiMacroBean
{

  private static final long serialVersionUID = 6568665314805818125L;

  private String searchExpression;

  /*
   * (non-Javadoc)
   * 
   * @see de.micromata.genome.gwiki.page.impl.wiki.GWikiMacroBean#renderImpl(de.micromata.genome.gwiki.page.GWikiContext,
   * de.micromata.genome.gwiki.page.impl.wiki.MacroAttributes)
   */
  @Override
  public boolean renderImpl(final GWikiContext wikiContext, MacroAttributes attrs)
  {
    String se = StringUtils.defaultString(searchExpression);

    Collection<GWikiElementInfo> webInfos = wikiContext.getWikiWeb().getPageInfos().values();
    if (StringUtils.isNotEmpty(se) == true) {
      List<SearchResult> sr = new ArrayList<SearchResult>(webInfos.size());
      for (GWikiElementInfo wi : webInfos) {
        sr.add(new SearchResult(wi));
      }
      SearchQuery query = new SearchQuery(se, true, sr);
      query.setSearchOffset(0);
      query.setMaxCount(10000);
      QueryResult qr = wikiContext.getWikiWeb().getContentSearcher().search(wikiContext, query);
      List<GWikiElementInfo> res = new ArrayList<GWikiElementInfo>(qr.getFoundItems());
      for (SearchResult sres : qr.getResults()) {
        res.add(sres.getElementInfo());
      }
      webInfos = res;
    }

    final Map<String, Set<String>> missingLinks = new TreeMap<String, Set<String>>();
    for (GWikiElementInfo ei : webInfos) {

      GWikiElement el = wikiContext.getWikiWeb().getElement(ei);
      Map<String, GWikiArtefakt< ? >> m = new HashMap<String, GWikiArtefakt< ? >>();
      final Set<String> mll = new TreeSet<String>();
      if (StringUtils.isNotEmpty(ei.getParentId()) == true) {
        if (wikiContext.getWikiWeb().findElementInfo(ei.getParentId()) == null) {
          mll.add(ei.getParentId());
        }
      }

      el.collectParts(m);
      for (GWikiArtefakt< ? > a : m.values()) {
        if (a instanceof GWikiWikiPageArtefakt) {
          GWikiWikiPageArtefakt w = (GWikiWikiPageArtefakt) a;
          if (w.compileFragements(wikiContext) == false) {
            continue;
          }
          if (w.getCompiledObject() == null) {
            continue;
          }

          w.getCompiledObject().iterate(new GWikiSimpleFragmentVisitor() {

            public void begin(GWikiFragment fragment)
            {
              if (fragment instanceof GWikiFragmentLink) {
                GWikiFragmentLink lnk = (GWikiFragmentLink) fragment;
                String t = lnk.getTargetPageId();
                if (t == null) {
                  return;
                }
                if (wikiContext.getWikiWeb().findElementInfo(t) == null) {
                  mll.add(t);
                }
              }
            }
          });
        }
        if (mll.isEmpty() == false) {
          missingLinks.put(ei.getId(), mll);
        }
      }
    }
    for (Map.Entry<String, Set<String>> me : missingLinks.entrySet()) {
      wikiContext.append(wikiContext.renderLocalUrl(me.getKey()));
      wikiContext.append(" <a href=\"" + wikiContext.localUrl("edit/EditPage") + "?pageId=" + me.getKey() + "\">(Edit)</a>");
      wikiContext.append(": ");
      boolean first = true;
      for (String tl : me.getValue()) {
        if (first == false) {
          wikiContext.append(", ");
        }
        first = false;
        wikiContext.append(tl);
      }
      wikiContext.append("<br/>\n");
    }
    return true;
  }

  public String getSearchExpression()
  {
    return searchExpression;
  }

  public void setSearchExpression(String searchExpression)
  {
    this.searchExpression = searchExpression;
  }

}
