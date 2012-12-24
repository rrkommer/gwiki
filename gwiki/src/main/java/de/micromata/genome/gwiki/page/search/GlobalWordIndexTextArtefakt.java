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

package de.micromata.genome.gwiki.page.search;

import java.util.HashMap;
import java.util.Map;

import de.micromata.genome.gwiki.page.GWikiContext;
import de.micromata.genome.gwiki.page.impl.GWikiTextContentArtefakt;
import de.micromata.genome.util.types.Pair;

/**
 * GWiki Artefakt for a word index file.
 * 
 * @author Roger Rene Kommer (r.kommer@micromata.de)
 * 
 */
public class GlobalWordIndexTextArtefakt extends GWikiTextContentArtefakt
{

  private static final long serialVersionUID = 4593931055929646011L;

  private Map<String, String> indexFileMap;

  /**
   * 
   * @param ctx
   * @param res
   * @param normText
   * @return -1 if not in index, 0 not found, > 0 found with weight.
   */
  public int getFoundIndexWeight(GWikiContext ctx, SearchResult res, String normText)
  {
    String id = res.getPageId();
    String content = getCompileIndexFileMap().get(id);
    if (content == null) {
      return -1;
    }
    Pair<String, Integer> p = findInIdxText(content, normText);
    if (p == null) {
      return 0;
    }
    return p.getSecond();

  }

  // private Pair<String, Integer> findInIdxText(String content, Matcher<String> matcher)
  // {
  // String pt;
  // if (matcher instanceof StringPatternMatcherBase) {
  // pt = ((StringPatternMatcherBase) matcher).getPattern();
  // } else if (matcher instanceof EqualsMatcher) {
  // pt = ((EqualsMatcher<String>) matcher).getOther();
  // } else {
  // throw new RuntimeException("Unsupported Matcher");
  // }
  // String normpt = NormalizeUtils.normalize(pt);
  // return findInIdxText(content, normpt);
  // }

  private Pair<String, Integer> findInIdxText(String content, String normpt)
  {
    String fqnpt = "|" + normpt + "\n";
    int idx = content.indexOf(fqnpt);
    boolean exactMatch = false;
    if (idx != -1) {
      exactMatch = true;
    } else {
      idx = content.indexOf(normpt);
      if (idx == -1) {
        return null;
      }
    }
    int hit = 1;
    if (exactMatch == true)
      hit = 3;
    int weight = getWeightFromLine(content, idx);
    if (exactMatch == true) {
      return Pair.make(normpt, hit * weight);
    }
    return Pair.make(normpt, hit * weight);
  }

  private int getWeightFromLine(String content, int idx)
  {
    while (idx >= 0) {
      if (content.charAt(idx) == '|')
        break;
      --idx;
    }
    int eidx = idx;
    while (idx >= 0) {
      if (content.charAt(idx) == '\n') {
        break;
      }
      --idx;
    }
    String pt = content.substring(idx + 1, eidx);
    try {
      return Integer.parseInt(pt);
    } catch (NumberFormatException ex) {
      return 1;
    }
  }

  public Map<String, String> getCompileIndexFileMap()
  {
    if (indexFileMap != null) {
      return indexFileMap;
    }
    parseIndexFileMap();
    return indexFileMap;
  }

  protected void parseIndexFileMap()
  {
    indexFileMap = new HashMap<String, String>();
    String data = getStorageData();
    for (int i = 0; i < data.length(); ++i) {
      int idx = data.indexOf('<', i);
      if (idx == -1) {
        break;
      }
      int eidx = data.indexOf('\n', idx + 1);
      if (eidx == -1) {
        break;
      }
      String id = data.substring(idx + 1, eidx);
      String endTag = ">" + id + "\n";
      int eti = data.indexOf(endTag, eidx + 1);
      if (eti == -1) {
        break;
      }
      String b = data.substring(eidx + 1, eti);
      indexFileMap.put(id, b);
      i = eti + endTag.length() - 1;
    }
  }

  public boolean isNoArchiveData()
  {
    return true;
  }

  public Map<String, String> getIndexFileMap()
  {
    return indexFileMap;
  }

  public void setIndexFileMap(Map<String, String> indexFileMap)
  {
    this.indexFileMap = indexFileMap;
  }

}
