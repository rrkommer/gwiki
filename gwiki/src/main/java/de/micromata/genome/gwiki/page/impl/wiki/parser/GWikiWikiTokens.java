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

package de.micromata.genome.gwiki.page.impl.wiki.parser;

import java.util.HashSet;
import java.util.Set;
import java.util.regex.Pattern;

import de.micromata.genome.gwiki.utils.IntArray;
import de.micromata.genome.gwiki.utils.StringUtils;
import de.micromata.genome.util.runtime.CallableX1;

/**
 * GWiki text parsed token.
 * 
 * @author Roger Rene Kommer (r.kommer@micromata.de)
 * 
 */
public class GWikiWikiTokens
{
  protected IntArray tokenOffsets = new IntArray();

  protected String lookupToken;

  protected int tokenPos = -1;

  protected String text;

  protected int textLength;

  protected Set<Character> stopTokens = new HashSet<Character>();

  public GWikiWikiTokens(String lookupToken, String text)
  {
    this.lookupToken = lookupToken;
    this.text = text;
    this.textLength = text.length();
    tokenize();
  }

  public GWikiWikiTokens(GWikiWikiTokens other)
  {
    this.tokenOffsets = other.tokenOffsets;
    this.lookupToken = other.lookupToken;
    this.tokenPos = other.tokenPos;
    this.text = other.text;
    this.stopTokens = other.stopTokens;
    this.textLength = other.textLength;
  }

  public GWikiWikiTokens(GWikiWikiTokens other, int tokenLength)
  {
    this(other);
    this.tokenOffsets = new IntArray(other.tokenOffsets, tokenLength);
    if (tokenLength < other.tokenOffsets.size()) {
      this.textLength = other.tokenOffsets.getInt(tokenLength);
    }
  }

  public int getTokenPos()
  {
    return tokenPos;
  }

  public void setTokenPos(int tkpos)
  {
    this.tokenPos = tkpos;
  }

  public String getTokens()
  {
    StringBuilder sb = new StringBuilder();
    for (int i = 0; i /*                                       * 2 */< tokenOffsets.size(); ++i) {
      String s = text.substring(tokenOffsets.getInt(i), tokenOffsets.getInt(i + 1));
      sb.append("[").append(s).append("]\n");
    }
    return sb.toString();
  }

  protected void tokenize()
  {
    int lastIdx = 0;
    for (int i = 0; i < text.length(); ++i) {
      final char c = text.charAt(i);
      if (lookupToken.indexOf(c) != -1) {
        if (lastIdx < i) {
          tokenOffsets.add(lastIdx);
          // tokenOffsets.add(i);
        }
        tokenOffsets.add(i);
        // tokenOffsets.add(i + 1);
        lastIdx = i + 1;
      }
    }
    if (lastIdx < text.length()) {
      tokenOffsets.add(lastIdx);
      // tokenOffsets.add(text.length());
    }
  }

  public boolean hasNext()
  {
    return hasNext(false);
  }

  public boolean hasNext(boolean ignoreStops)
  {
    if (eof(1, ignoreStops) == true) {
      return false;
    }
    return true;
  }

  public char addStopToken(char tk)
  {
    if (tk == 0) {
      return 0;
    }
    if (stopTokens.add(tk) == true) {
      return 0;
    }
    return tk;
  }

  public char removeStopToken(char tk)
  {
    if (tk == 0) {
      return 0;
    }
    if (stopTokens.remove(tk) == true) {
      return '|';
    }
    return 0;
  }

  public boolean currentIsStopToken()
  {
    int abpos = (tokenPos);
    if (abpos >= tokenOffsets.size()) {
      return false;
    }
    int idx = tokenOffsets.getInt(abpos);
    char pk = text.charAt(idx);
    if (stopTokens.contains(pk) == true) {
      return true;
    }
    return false;
  }

  public char nextToken()
  {
    if (hasNext() == false) {
      if (currentIsStopToken() == false) {
        ++tokenPos;
      }
      return 0;
    }
    return text.charAt(tokenOffsets.getInt(++tokenPos));
  }

  public char nextTokenIgnoreStops()
  {
    if (hasNext(true) == false) {
      return 0;
    }
    return text.charAt(tokenOffsets.getInt(++tokenPos));
  }

  public boolean isNormalWhiteSpace(char tk)
  {
    return tk == ' ' || tk == '\t';
  }

  public char skipWs()
  {
    while (isNormalWhiteSpace(curToken()) == true) {
      nextToken();
    }
    return curToken();
  }

  public char skipWsNl()
  {
    return skipWsNl(true);
  }

  public char skipWsNl(boolean ignoreStops)
  {
    while (eof(0, ignoreStops) == false && (isNormalWhiteSpace(curToken(ignoreStops)) == true || curToken(ignoreStops) == '\n')) {
      if (nextTokenIgnoreStops() == 0) {
        break;
      }
    }
    return curToken(ignoreStops);
  }

  public void pushBack()
  {
    if (curToken() != -1 && curToken() != 0) {
      setTokenPos(getTokenPos() - 1);
    }
  }

  public boolean eof()
  {
    return eof(0);
  }

  private boolean eof(int offset)
  {
    return eof(offset, false);
  }

  private boolean eof(int offset, boolean ignoreStops)

  {
    int abpos = tokenPos + offset;
    if (abpos < 0) {
      return true;
    }
    if (abpos >= tokenOffsets.size()) {
      return true;
    }
    int idx = tokenOffsets.getInt(abpos);
    // if (idx > text.length()) {
    // return true;
    // }
    char pk = text.charAt(idx);
    if (ignoreStops == false && stopTokens.contains(pk) == true) {
      return true;
    }
    return false;
  }

  public char curToken()
  {
    return curToken(false);
  }

  public char curToken(boolean ignoreStops)
  {
    if (eof(0, ignoreStops) == true) {
      return 0;
    }
    return text.charAt(tokenOffsets.getInt(tokenPos));
  }

  public char peekToken(int offset)
  {
    return peekToken(offset, false);
  }

  public char peekToken(int offset, boolean ignoreStops)
  {
    if (eof(offset, ignoreStops) == true) {
      return 0;
    }
    return text.charAt(tokenOffsets.getInt(tokenPos + offset));
  }

  public String curTokenString()
  {
    return curTokenString(false);
  }

  public String curTokenString(boolean ignoreStops)
  {
    if (eof(0, ignoreStops) == true) {
      return "";
    }
    if (tokenPos + 1 >= tokenOffsets.size()) {
      return text.substring(tokenOffsets.getInt(tokenPos), textLength);
    }
    return text.substring(tokenOffsets.getInt(tokenPos), tokenOffsets.getInt(tokenPos + 1));
  }

  public String peekTokenString(int offset, boolean ignoreStops)
  {
    if (eof(offset, ignoreStops) == true) {
      return "";
    }
    final int abspos = tokenPos + offset;
    if (abspos + 1 >= tokenOffsets.size()) {
      return text.substring(tokenOffsets.getInt(abspos), textLength);
    }
    return text.substring(tokenOffsets.getInt(abspos), tokenOffsets.getInt(abspos + 1));
  }

  public String getTokenString(int startToken, int endToken)
  {
    if (endToken >= tokenOffsets.size()) {
      return text.substring(tokenOffsets.getInt(startToken), textLength);
    }
    return text.substring(tokenOffsets.getInt(startToken), tokenOffsets.getInt(endToken));
  }

  public String getStringUntilOneOf(String tokens)
  {
    return getStringUntilOneOf(tokens, true);
  }

  public int getLineNoFromTokenOffset(int offset)
  {
    int nls = 1;
    for (int i = 0; i < tokenOffsets.size() && i < offset; ++i) {
      char c = text.charAt(tokenOffsets.getInt(i));
      if (c == '\n') {
        ++nls;
      }
    }
    return nls;
  }

  public String getStringUntilNotOneOf(String tokens)
  {
    int curPos = tokenPos;
    do {
      char c = nextToken();
      if (tokens.indexOf(c) == -1) {
        String ret = getTokenString(curPos, tokenPos);
        return ret;

      }
    } while (eof() == false);
    return null;
  }

  public static Pattern unescapePattern = Pattern.compile("\\\\(.)");

  public String unescape(String ret)
  {
    if (ret.indexOf('\\') == -1) {
      return ret;
    }
    return StringUtils.replace(ret, unescapePattern, 1, new CallableX1<String, String, RuntimeException>() {

      public String call(String arg1) throws RuntimeException
      {
        return arg1;
      }
    });
  }

  public String getStringUntilOneOf(String tokens, boolean escaped)
  {
    int curPos = tokenPos;
    do {
      char c = nextToken();
      if (escaped == true && c == '\\') {
        c = nextToken();
        c = nextToken();
      }
      if (tokens.indexOf(c) != -1) {
        String ret = getTokenString(curPos, tokenPos);
        if (escaped == false) {
          return ret;
        }
        return unescape(ret);

      }
    } while (eof() == false);
    return null;
  }

  /**
   * Parses string quoted. Expcept tkpos on first char inside the string. tkpos will be behind quoted end
   * 
   * @return
   */
  public String parseStringQuoted()
  {
    StringBuilder sb = new StringBuilder();
    char tk = curToken(true);
    do {
      switch (tk) {
        case '\\':
          tk = nextTokenIgnoreStops();
          sb.append(curTokenString(true));
          break;
        case '"':
          nextTokenIgnoreStops();
          return sb.toString();
        default:
          sb.append(curTokenString(true));
          break;
      }
      tk = nextTokenIgnoreStops();
    } while (tk != 0);
    return sb.toString();
  }

  public int findToken(String... seq)
  {
    int curPos = tokenPos;
    // char c = nextToken();
    nextLoop: do {
      for (int i = 0; i < seq.length; ++i) {
        String cts = peekTokenString(i, true);
        if (seq[i].equals(cts) == false) {
          if (nextTokenIgnoreStops() == 0) {
            return -1;
          }
          continue nextLoop;
        }
      }
      int startTokenPos = tokenPos;
      tokenPos = curPos;
      return startTokenPos;
    } while (true);
    // return -1;
  }

  public int getLastToken()
  {
    return tokenOffsets.size() - 1;
  }
}
