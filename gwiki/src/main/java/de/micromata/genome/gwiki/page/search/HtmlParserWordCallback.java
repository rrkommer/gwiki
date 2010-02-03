/////////////////////////////////////////////////////////////////////////////
//
// Project   DHL-ParcelOnlinePostage
//
// Author    roger@micromata.de
// Created   26.10.2009
// Copyright Micromata 26.10.2009
//
/////////////////////////////////////////////////////////////////////////////
package de.micromata.genome.gwiki.page.search;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringEscapeUtils;

@Deprecated
public class HtmlParserWordCallback implements WordCallback
{
  private WordCallback backend;

  public HtmlParserWordCallback(WordCallback backend)
  {
    this.backend = backend;
  }

  private static enum State
  {
    Text, Tag, Comment
  }

  private String decode(String word)
  {
    final String w = StringEscapeUtils.unescapeHtml(word);
    return w;
  }

  private String getTagName(String word, int offset)
  {
    int i = offset;
    for (; i < word.length(); ++i) {
      if (Character.isLetter(word.charAt(i)) == false) {
        break;
      }
    }
    return word.substring(offset, i);
  }

  private static Map<String, Integer> tagLevels = new HashMap<String, Integer>();
  static {
    tagLevels.put("h1", 10);
    tagLevels.put("h2", 8);
    tagLevels.put("h3", 6);
    tagLevels.put("h4", 5);
    tagLevels.put("script", 0);
  }

  private int getLevelFromTag(String word, int idx)
  {
    String tagName = getTagName(word, idx);
    Integer l = tagLevels.get(word);
    if (l != null) {
      return l;
    }
    return 1;
  }

  public void callback(String word, int level)
  {

    int lastStart = 0;
    int i = 0;
    State state = State.Text;
    for (; i < word.length(); ++i) {
      char c = word.charAt(i);
      switch (state) {
        case Text:
          if (c == '<') {
            if (word.substring(i).startsWith("<!--") == true) {
              state = State.Comment;
            } else {
              state = State.Tag;
              level = getLevelFromTag(word, i);
            }
            if (i > lastStart) {
              String nw = word.substring(lastStart, i);
              nw = decode(nw);
              backend.callback(nw, 1);
            }
            if (state == State.Tag) {
              ++i;
            } else if (state == State.Comment) {
              i += 3;
            }
          }
          break;
        case Comment:
          if (c == '-' && word.substring(i).startsWith("-->") == true) {
            i += 3;
            lastStart = i;
            state = State.Text;
          }
          break;
        case Tag:
          if (c == '>') {
            lastStart = i + 1;
            state = State.Text;
          }
          break;
      }
    }
  }

  public void popLevel()
  {
    backend.popLevel();
  }

  public void pushLevel(int level)
  {
    backend.pushLevel(level);
  }
}
