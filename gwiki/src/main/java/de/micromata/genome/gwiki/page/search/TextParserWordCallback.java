/////////////////////////////////////////////////////////////////////////////
//
// Project   DHL-ParcelOnlinePostage
//
// Author    roger@micromata.de
// Created   24.10.2009
// Copyright Micromata 24.10.2009
//
/////////////////////////////////////////////////////////////////////////////
package de.micromata.genome.gwiki.page.search;

/**
 * split text into words.
 * 
 * @author Roger Rene Kommer (r.kommer@micromata.de)
 * 
 */
public class TextParserWordCallback implements WordCallback
{
  private WordCallback backend;

  public TextParserWordCallback(WordCallback backend)
  {
    this.backend = backend;
  }

  public void popLevel()
  {
    backend.popLevel();
  }

  public void pushLevel(int level)
  {
    backend.pushLevel(level);
  }

  public void callback(String word, int level)
  {
    if (word == null)
      return;
    int maxidx = word.length();
    int lastStart = -1;
    int i = 0;

    while (i < maxidx) {
      char c = word.charAt(i);
      if (Character.isLetter(c) == true) {
        if (lastStart == -1) {
          lastStart = i;
        }
      } else {
        if (lastStart != -1 && i > lastStart) {
          backend.callback(word.substring(lastStart, i), level);
          lastStart = -1;
        }
      }
      ++i;
    }
  }
}
