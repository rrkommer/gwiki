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

package de.micromata.genome.gwiki.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import de.micromata.genome.gwiki.model.matcher.GWikiElementPropMatcher;
import de.micromata.genome.gwiki.model.matcher.GWikiElementTypeMatcher;
import de.micromata.genome.gwiki.model.matcher.GWikiPageIdMatcher;
import de.micromata.genome.gwiki.model.matcher.GWikiViewableMatcher;
import de.micromata.genome.gwiki.page.GWikiContext;
import de.micromata.genome.gwiki.page.impl.GWikiWikiPageArtefakt;
import de.micromata.genome.gwiki.page.impl.wiki.fragment.GWikiFragment;
import de.micromata.genome.gwiki.page.impl.wiki.fragment.GWikiSimpleFragmentVisitor;
import de.micromata.genome.gwiki.page.impl.wiki.macros.GWikiElementByChildOrderComparator;
import de.micromata.genome.gwiki.page.impl.wiki.macros.GWikiElementByI18NPropsComparator;
import de.micromata.genome.gwiki.page.impl.wiki.macros.GWikiElementByIntPropComparator;
import de.micromata.genome.gwiki.page.impl.wiki.macros.GWikiElementByOrderComparator;
import de.micromata.genome.gwiki.page.search.QueryResult;
import de.micromata.genome.gwiki.page.search.SearchQuery;
import de.micromata.genome.gwiki.page.search.SearchResult;
import de.micromata.genome.util.matcher.AndMatcher;
import de.micromata.genome.util.matcher.EqualsMatcher;
import de.micromata.genome.util.matcher.Matcher;

/**
 * Helper to find Elements.
 * 
 * The common way to access this is wikiContext.getElementFinder().
 * 
 * @author Roger Rene Kommer (r.kommer@micromata.de)
 * 
 */
public class GWikiElementFinder
{
  protected GWikiContext wikiContext;

  public GWikiElementFinder(GWikiContext wikiContext)
  {
    this.wikiContext = wikiContext;

  }

  public Matcher<GWikiElementInfo> byPageIdName(Matcher<String> matcher)
  {
    return new GWikiPageIdMatcher(wikiContext, matcher);
  }

  public List<GWikiElementInfo> getPageInfos(Matcher<GWikiElementInfo> matcher)
  {
    List<GWikiElementInfo> ret = new ArrayList<GWikiElementInfo>();

    for (GWikiElementInfo ei : wikiContext.getWikiWeb().getElementInfos()) {
      if (matcher.match(ei) == true) {
        ret.add(ei);
      }
    }
    return ret;
  }

  public List<GWikiElement> getPages(Matcher<GWikiElementInfo> matcher)
  {
    List<GWikiElement> ret = new ArrayList<GWikiElement>();
    for (GWikiElementInfo ei : wikiContext.getWikiWeb().getElementInfos()) {
      if (matcher.match(ei) == true) {
        ret.add(wikiContext.getWikiWeb().getElement(ei));
      }
    }
    return ret;
  }

  public List<GWikiElementInfo> getPageAttachments(String pageId)
  {
    List<GWikiElementInfo> childs = getPageInfos(//
    new AndMatcher<GWikiElementInfo>(//
        new GWikiViewableMatcher(wikiContext), new AndMatcher<GWikiElementInfo>(//
            new GWikiElementPropMatcher(wikiContext, GWikiPropKeys.PARENTPAGE, new EqualsMatcher<String>(pageId)),//
            new GWikiElementTypeMatcher(wikiContext, "attachment"))));
    return childs;
  }

  /**
   * get all direct child pages.
   * 
   * @param pageId
   * @return
   */
  public List<GWikiElementInfo> getPageDirectPages(String pageId)
  {
    List<GWikiElementInfo> childs = getPageInfos(//
    new AndMatcher<GWikiElementInfo>(//
        new GWikiViewableMatcher(wikiContext), // 
        new AndMatcher<GWikiElementInfo>(//
            new GWikiElementPropMatcher(wikiContext, GWikiPropKeys.PARENTPAGE, //
                new EqualsMatcher<String>(pageId)),//
            new GWikiElementTypeMatcher(wikiContext, "gwiki"))));

    Collections.sort(childs, new GWikiElementByChildOrderComparator(//
        new GWikiElementByOrderComparator(//
            new GWikiElementByIntPropComparator("ORDER", 0, //
                new GWikiElementByI18NPropsComparator(wikiContext, GWikiPropKeys.TITLE)))));
    return childs;
  }

  public List<GWikiElementInfo> getDirectChilds(GWikiElementInfo ei)
  {
    String pageId = ei.getId();
    return getDirectChilds(pageId);
  }

  /**
   * return all direct childs unsorted.
   * 
   * @param pageId
   * @return
   */
  public List<GWikiElementInfo> getDirectChilds(String pageId)
  {
    List<GWikiElementInfo> childs = getPageInfos(//
    new AndMatcher<GWikiElementInfo>(// "TITLE"
        new GWikiViewableMatcher(wikiContext), //
        new GWikiElementPropMatcher(wikiContext, GWikiPropKeys.PARENTPAGE, new EqualsMatcher<String>(pageId))//
    )//
    );

    return childs;
  }

  public List<GWikiElementInfo> getAllDirectChilds(GWikiElementInfo ei)
  {
    String pageId = ei.getId();
    List<GWikiElementInfo> childs = getPageInfos(//
    new GWikiElementPropMatcher(wikiContext, GWikiPropKeys.PARENTPAGE, new EqualsMatcher<String>(pageId))//
    );
    return childs;
  }

  public List<GWikiElementInfo> getAllDirectChildsByType(GWikiElementInfo ei, final String type) {
    return getPageInfos(new AndMatcher<GWikiElementInfo>(//
        new GWikiElementPropMatcher(wikiContext, GWikiPropKeys.PARENTPAGE, new EqualsMatcher<String>(ei.getId())), // 
        new GWikiElementTypeMatcher(wikiContext, type)));
  }
  
  public static interface FragmentCallback<T extends GWikiFragment>
  {
    void callback(GWikiElement element, String partName, GWikiArtefakt< ? > artefakt, T fragment);
  }

  /**
   * 
   * @param <T> Type of fragment
   * @param seachExpression search expression. if null or empty all elements.
   * @param fragmentClass class of requested fragment class.
   * @param callback callback will be called for each fragment
   */
  public <T extends GWikiFragment> void iterateAllFragments(String seachExpression, final Class<T> fragmentClass,
      final FragmentCallback<T> callback)
  {
    Iterable<GWikiElementInfo> webInfos = wikiContext.getWikiWeb().getElementInfos();
    if (StringUtils.isNotEmpty(seachExpression) == true) {
      List<SearchResult> sr = new ArrayList<SearchResult>(wikiContext.getWikiWeb().getElementInfoCount());
      for (GWikiElementInfo wi : webInfos) {
        sr.add(new SearchResult(wi));
      }
      SearchQuery query = new SearchQuery(seachExpression, true, sr);
      query.setSearchOffset(0);
      query.setMaxCount(10000);
      QueryResult qr = wikiContext.getWikiWeb().getContentSearcher().search(wikiContext, query);
      List<GWikiElementInfo> res = new ArrayList<GWikiElementInfo>(qr.getFoundItems());
      for (SearchResult sres : qr.getResults()) {
        res.add(sres.getElementInfo());
      }
      webInfos = res;
    }
    for (GWikiElementInfo ei : webInfos) {

      final GWikiElement el = wikiContext.getWikiWeb().getElement(ei);
      Map<String, GWikiArtefakt< ? >> m = new HashMap<String, GWikiArtefakt< ? >>();
      el.collectParts(m);
      for (Map.Entry<String, GWikiArtefakt< ? >> me : m.entrySet()) {
        final GWikiArtefakt< ? > a = me.getValue();
        final String partName = me.getKey();
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
              if (fragmentClass == null || fragmentClass.isAssignableFrom(fragment.getClass()) == true) {
                callback.callback(el, partName, a, (T) fragment);
              }
            }
          });
        }
      }
    }
  }

  /**
   * Try to load a config on given elements main part. You should only read the returned props.
   * 
   * @param pageId
   * @return empty props if not found.
   */
  public GWikiProps getConfigProps(String pageId)
  {
    if (StringUtils.isEmpty(pageId) == true) {
      return new GWikiProps();
    }
    GWikiElement el = wikiContext.getWikiWeb().findElement(pageId);
    if (el == null) {
      return new GWikiProps();
    }
    GWikiArtefakt< ? > art = el.getMainPart();
    if ((art instanceof GWikiPropsArtefakt) == false) {
      return new GWikiProps();
    }
    return ((GWikiPropsArtefakt) art).getCompiledObject();
  }
}
