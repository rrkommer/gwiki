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

import de.micromata.genome.gwiki.page.impl.GWikiTextContentArtefakt;

/**
 * GWiki Artefakt to hold a word intext text file.
 * 
 * @author Roger Rene Kommer (r.kommer@micromata.de)
 * 
 */
public class WordIndexTextArtefakt extends GWikiTextContentArtefakt
{

  private static final long serialVersionUID = -6195979370834978197L;

  private Map<String, Integer> stopWords;

  private String pageId;

  public WordIndexTextArtefakt()
  {

  }

  public WordIndexTextArtefakt(String pageId)
  {
    this.pageId = pageId;
  }

  public Map<String, Integer> getStopWords()
  {
    if (stopWords != null) {
      return stopWords;
    }
    String text = getStorageData();
    stopWords = new HashMap<String, Integer>();
    int lastLevel = 0;
    int lastIdx = 0;
    boolean weightReaded = false;
    for (int i = 0; i < text.length(); ++i) {
      char c = text.charAt(i);
      if (c == '|') {
        String tf = text.substring(lastIdx, i);
        try {
          lastLevel = Integer.parseInt(tf);
          weightReaded = true;
        } catch (NumberFormatException ex) {
        }

        lastIdx = i + 1;
      } else if (c == '\n' || c == '\r') {
        if (i == lastIdx) {
          lastIdx = i + 1;
          continue;
        }
        String w = text.substring(lastIdx, i);
        int cl = lastLevel;
        if (weightReaded == false) {
          cl = 1;
        }
        stopWords.put(w, cl);
        weightReaded = false;
        lastIdx = i + 1;
      }
    }

    // 
    // stopWords.addAll(words);
    return stopWords;
  }

  public static String stopWordsToString(Map<String, Integer> stopWords)
  {
    StringBuilder sb = new StringBuilder();
    for (Map.Entry<String, Integer> me : stopWords.entrySet()) {
      sb.append(me.getValue().toString()).append("|").append(me.getKey()).append("\n");
    }
    return sb.toString();
  }

  public void setStopWords(Map<String, Integer> stopWords)
  {
    this.stopWords = stopWords;
    String sb = stopWordsToString(stopWords);
    setStorageData(sb.toString());
  }

  public boolean isNoArchiveData()
  {
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

}
