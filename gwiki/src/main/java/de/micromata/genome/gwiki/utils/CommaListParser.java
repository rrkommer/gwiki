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

package de.micromata.genome.gwiki.utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.commons.lang.StringUtils;

/**
 * All samples with trim.
 * 
 * Rules of the format.
 * 
 * Unquoted all character are valid except comma.
 * 
 * Quoted start with and end ".
 * 
 * Inside the quote text following character has be masked:
 * 
 * A following \ or " has to be quoted with \. All \ with other character following are simple \.
 * 
 * 'a\' => "a\\".
 * 
 * '\a' => "\a"
 * 
 * '"' => "\"".
 * 
 * 
 * a single
 * 
 * A, b => [[A],[b]].
 * 
 * A,b => [[A],[b]].
 * 
 * a\b,c => [[a\b],[c]].
 * 
 * "a,b",c => [[a,b],c].
 * 
 * "\"a,b" => ["a,b]
 * 
 * @see CommaListParserTest
 * 
 * @author Roger Rene Kommer (r.kommer@micromata.de)
 * 
 */
public class CommaListParser
{
  public static List<String> parseCommaList(String input)
  {
    return parseCommaList(input, true);
  }

  private static enum State
  {
    StartField, //
    InField, //
    InQuotedField
  }

  /**
   * return behind the last nonwhitespace character.
   * 
   * @param input
   * @param position
   * @return
   */
  private static int seekNonWsBack(String input, int position)
  {
    for (int i = position; i >= 0; --i) {
      if (Character.isWhitespace(input.charAt(i)) == false) {
        return i + 1;
      }
    }
    return 0;
  }

  /**
   * return index on non whitespace
   * 
   * @param input
   * @param position
   * @return
   */
  private static int skeepWs(String input, int position)
  {
    for (int i = position; i < input.length(); ++i) {
      if (Character.isWhitespace(input.charAt(i)) == false) {
        return i;
      }
    }
    return input.length();
  }

  private static String unquote(String text)
  {
    StringBuilder sb = new StringBuilder();
    for (int i = 0; i < text.length(); ++i) {
      char c = text.charAt(i);
      if (c != '\\') {
        sb.append(c);
        continue;
      }
      if (i + 1 >= text.length()) {
        sb.append("\\");
        break;
      }
      char c2 = text.charAt(i + 1);
      if (c2 == '\\' || c2 == '"') {
        ++i;
        sb.append(c2);
        continue;
      }
      sb.append(c);
    }
    return sb.toString();
  }

  public static List<String> parseCommaList(String input, boolean trimValues)
  {
    if (input == null || input.length() == 0) {
      return Collections.emptyList();
    }
    List<String> ret = new ArrayList<String>();
    int lastBegin = 0;
    State state = State.StartField;
    loop: for (int i = 0; i < input.length(); ++i) {
      char c = input.charAt(i);
      switch (state) {
        case StartField:
          if (trimValues == true && Character.isWhitespace(c) == true) {
            lastBegin = i + 1;
            continue;
          }
          if (c == ',') {
            ret.add("");
            lastBegin = i + 1;
            continue;
          }
          if (c == '"') {
            lastBegin = i + 1;
            state = State.InQuotedField;
            continue;
          }
          state = State.InField;
          lastBegin = i;
          break;
        case InField:
          if (c == ',') {
            int lc = i;
            if (trimValues == true) {
              lc = seekNonWsBack(input, lc - 1);
            }
            ret.add(input.substring(lastBegin, lc));
            state = State.StartField;
            continue;
          }
          continue;
        case InQuotedField:
          if (c == '\\') {
            if (i + 1 >= input.length()) {
              ret.add(input.substring(lastBegin, i + 1));
              break loop;
            }
            char nc = input.charAt(i + 1);
            if (nc == '\"' || nc == '\\') {
              ++i;
              continue;
            }
            continue;
          } else if (c == '"') {
            String t = input.substring(lastBegin, i);
            t = unquote(t);
            ret.add(t);
            i = skeepWs(input, i + 1);
            if (i >= input.length()) {
              lastBegin = i;
              break loop;
            }
            char c2 = input.charAt(i);
            if (c2 != ',') {
              throw new RuntimeException("In Input at position " + i + " expects ',', got: " + c2);
            }
            ++i;
            lastBegin = i;
            state = State.StartField;
          }
          break;
      }
    }
    if (lastBegin < input.length()) {
      switch (state) {
        case InField: {
          String t = input.substring(lastBegin, input.length());
          t = StringUtils.trim(t);
          ret.add(t);
          break;
        }
        case InQuotedField:
          throw new RuntimeException("In Input at position "
              + (input.length() - 1)
              + " expects ending '\"', got end of input. text: '"
              + input
              + "'");
        case StartField:
          if (ret.size() > 0) {
            ret.add("");
          }
          break;
      }
    } else if (state == State.StartField && ret.size() > 0) {
      ret.add("");
    }
    return ret;
  }

  public static String quote(String s)
  {
    s = StringUtils.replace(s, "\\", "\\\\");
    s = StringUtils.replace(s, "\"", "\\\"");
    return "\"" + s + "\"";
  }

  public static String encode(List<String> args)
  {
    if (args == null || args.size() == 0) {
      return "";
    }

    StringBuilder sb = new StringBuilder();
    for (String e : args) {
      if (sb.length() > 0) {
        sb.append(",");
      }
      if (e.indexOf(",") != -1) {
        sb.append(quote(e));
      } else {
        sb.append(e);
      }
    }
    return sb.toString();
  }
}
