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

package de.micromata.genome.gwiki.page.impl.wiki.filter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import com.uwyn.jhighlight.tools.StringUtils;

import de.micromata.genome.gwiki.model.GWikiElementInfo;
import de.micromata.genome.gwiki.model.GWikiPropKeys;
import de.micromata.genome.gwiki.model.filter.GWikiFilterChain;
import de.micromata.genome.gwiki.model.filter.GWikiLoadElementInfosFilter;
import de.micromata.genome.gwiki.model.filter.GWikiLoadElementInfosFilterEvent;
import de.micromata.genome.gwiki.page.GWikiContext;
import de.micromata.genome.gwiki.utils.CommaListParser;
import de.micromata.genome.util.types.Pair;

public class GWikiKeywordLoadElementInfosFilter implements GWikiLoadElementInfosFilter
{
  private static GWikiKeywordLoadElementInfosFilter INSTANCE;

  public static GWikiKeywordLoadElementInfosFilter getInstance()
  {
    return INSTANCE;
  }

  public GWikiKeywordLoadElementInfosFilter()
  {
    INSTANCE = this;
  }

  /**
   * Space -> Map<Keyword(String) -> KeyWord(Pattern, List<GWikiElementInfo>>)
   */
  private Map<String, Map<String, Pair<Pattern, List<GWikiElementInfo>>>> keywordsToElements = null;

  public static void checkKeywordProperties(GWikiContext ctx, String value)
  {
    try {
      List<String> keywords = CommaListParser.parseCommaList(value);
      for (String kw : keywords) {
        kw = StringUtils.replace(kw, ")", "){0,1}");
        Pattern.compile(kw);
      }
    } catch (Exception ex) {
      ctx.addValidationError("gwiki.edit.EditPage.message.invalidkeywordformat", ex.getMessage());
    }
  }

  public void addKeywords(GWikiContext wikiContext, GWikiElementInfo ei)
  {
    List<String> keywords = ei.getProps().getStringList(GWikiPropKeys.KEYWORDS);
    if (keywords == null || keywords.isEmpty() == true) {
      return;
    }
    String space = ei.getWikiSpace(wikiContext);
    Map<String, Pair<Pattern, List<GWikiElementInfo>>> spacekeywords = keywordsToElements.get(space);
    if (spacekeywords == null) {
      spacekeywords = new HashMap<String, Pair<Pattern, List<GWikiElementInfo>>>();
      keywordsToElements.put(space, spacekeywords);
    }
    for (String kw : keywords) {
      if (kw.contains("(") == true) {
        kw = StringUtils.replace(kw, ")", "){0,1}");
      }
      Pair<Pattern, List<GWikiElementInfo>> ellp = spacekeywords.get(kw);
      if (ellp == null) {
        ellp = Pair.make(Pattern.compile(kw), (List<GWikiElementInfo>) new ArrayList<GWikiElementInfo>());
        spacekeywords.put(kw, ellp);
      }
      ellp.getValue().add(ei);
    }
  }

  public void clearKeywords()
  {
    keywordsToElements = null;
  }

  public void fillKeyWords(GWikiContext wikiContext, Map<String, GWikiElementInfo> eis)
  {
    keywordsToElements = new HashMap<String, Map<String, Pair<Pattern, List<GWikiElementInfo>>>>();
    keywordsToElements.clear();
    for (Map.Entry<String, GWikiElementInfo> me : eis.entrySet()) {
      addKeywords(wikiContext, me.getValue());
    }
  }

  public Void filter(GWikiFilterChain<Void, GWikiLoadElementInfosFilterEvent, GWikiLoadElementInfosFilter> chain,
      GWikiLoadElementInfosFilterEvent event)
  {
    chain.nextFilter(event);
    fillKeyWords(event.getWikiContext(), event.getPageInfos());
    return null;
  }

  public Map<String, Map<String, Pair<Pattern, List<GWikiElementInfo>>>> getKeywords(GWikiContext wikiContext)
  {
    if (keywordsToElements != null) {
      return keywordsToElements;
    }
    fillKeyWords(wikiContext, wikiContext.getWikiWeb().getPageInfos());
    return keywordsToElements;
  }

  public Map<String, Map<String, Pair<Pattern, List<GWikiElementInfo>>>> getKeywordsToElements()
  {
    return keywordsToElements;
  }

  public void setKeywordsToElements(Map<String, Map<String, Pair<Pattern, List<GWikiElementInfo>>>> keywordsToElements)
  {
    this.keywordsToElements = keywordsToElements;
  }

}
