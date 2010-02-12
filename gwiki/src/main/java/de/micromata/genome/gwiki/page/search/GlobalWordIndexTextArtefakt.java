/////////////////////////////////////////////////////////////////////////////
//
// Project   DHL-ParcelOnlinePostage
//
// Author    roger@micromata.de
// Created   27.10.2009
// Copyright Micromata 27.10.2009
//
/////////////////////////////////////////////////////////////////////////////
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

  public int getFoundIndexWeight(GWikiContext ctx, SearchResult res, String normText)
  {
    String id = res.getPageId();
    String content = getCompileIndexFileMap().get(id);
    if (content == null) {
      return 0;
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
