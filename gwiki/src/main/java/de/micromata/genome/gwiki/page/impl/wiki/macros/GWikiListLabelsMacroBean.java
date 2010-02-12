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
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import de.micromata.genome.gwiki.model.GWikiElementInfo;
import de.micromata.genome.gwiki.model.GWikiPropKeys;
import de.micromata.genome.gwiki.model.matcher.GWikiViewableMatcher;
import de.micromata.genome.gwiki.page.GWikiContext;
import de.micromata.genome.gwiki.page.impl.wiki.GWikiMacroBean;
import de.micromata.genome.gwiki.page.impl.wiki.MacroAttributes;

/**
 * List all keywords.
 * 
 * @author Roger Rene Kommer (r.kommer@micromata.de)
 * 
 */
public class GWikiListLabelsMacroBean extends GWikiMacroBean implements GWikiPropKeys
{

  private static final long serialVersionUID = 5047549602538390544L;

  /**
   * List all keywords with this and childs.
   */
  private String pageId;

  private String space;

  protected static String getKeywordDisplay(String kw)
  {
    if (kw.startsWith("(") == false) {
      return kw;
    }
    return kw.substring(1, kw.length() - 1);
  }

  protected static Map<String, List<GWikiElementInfo>> collectKeywords(GWikiContext ctx)
  {
    List<GWikiElementInfo> eis = ctx.getElementFinder().getPageInfos(new GWikiViewableMatcher(ctx));
    Map<String, List<GWikiElementInfo>> keywordMap = new TreeMap<String, List<GWikiElementInfo>>();
    for (GWikiElementInfo ei : eis) {
      List<String> kwl = ei.getProps().getStringList(KEYWORDS);
      if (kwl == null || kwl.isEmpty() == true) {
        continue;
      }
      for (String kw : kwl) {
        kw = getKeywordDisplay(kw);
        List<GWikiElementInfo> el = keywordMap.get(kw);
        if (el == null) {
          el = new ArrayList<GWikiElementInfo>();
          keywordMap.put(kw, el);
        }
        el.add(ei);
      }
    }
    return keywordMap;
  }

  @Override
  public boolean renderImpl(GWikiContext ctx, MacroAttributes attrs)
  {
    // gwiki TODO
    return true;
  }

  public String getPageId()
  {
    return pageId;
  }

  public void setPageId(String pageId)
  {
    this.pageId = pageId;
  }

  public String getSpace()
  {
    return space;
  }

  public void setSpace(String space)
  {
    this.space = space;
  }

}
