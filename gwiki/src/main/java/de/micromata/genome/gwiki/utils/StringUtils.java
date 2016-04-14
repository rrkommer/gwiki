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

package de.micromata.genome.gwiki.utils;

import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.ArrayUtils;

import de.micromata.genome.util.runtime.CallableX1;
import de.micromata.genome.util.types.Pair;

/**
 * Extension to commons StringUtils.
 * 
 * @author Roger Rene Kommer (r.kommer@micromata.de)
 * 
 */
public class StringUtils extends org.apache.commons.lang.StringUtils
{
  public static final Pair<Integer, String> INDEX_OF_ANY_NOT_FOUND_PAIR = new Pair<Integer, String>(-1, null);

  /**
   * 
   * @param text
   * @param search
   * @return
   */
  public static Pair<Integer, String> indexOfAny(String text, int offset, List<String> search)
  {
    if (search == null) {
      return INDEX_OF_ANY_NOT_FOUND_PAIR;
    }
    return indexOfAny(text, offset, search.toArray(new String[] {}));
  }

  public static int indexOfAny(String str, int offset, char[] searchChars)
  {
    if (isEmpty(str) || ArrayUtils.isEmpty(searchChars)) {
      return -1;
    }
    for (int i = offset; i < str.length(); i++) {
      char ch = str.charAt(i);
      for (int j = 0; j < searchChars.length; j++) {
        if (searchChars[j] == ch) {
          return i;
        }
      }
    }
    return -1;
  }

  public static Pair<Integer, String> indexOfAny(String text, int offset, String[] search)
  {
    if (text == null) {
      return INDEX_OF_ANY_NOT_FOUND_PAIR;
    }
    int[] idxe = new int[search.length];
    for (int i = 0; i < search.length; ++i) {
      if (search[i] == null) {
        idxe[i] = -1;
      } else {
        idxe[i] = text.indexOf(search[i], offset);
      }
    }
    Arrays.sort(idxe);
    for (int i = 0; i < idxe.length; ++i) {
      if (idxe[i] != -1) {
        return Pair.make(idxe[i], search[i]);
      }
    }
    return INDEX_OF_ANY_NOT_FOUND_PAIR;
  }

  public static String replace(String text, Pattern p, int group, CallableX1<String, String, RuntimeException> replacer)
  {
    if (isEmpty(text) == true) {
      return text;
    }
    StringBuilder sb = new StringBuilder();
    Matcher m = p.matcher(text);
    int lastIdx = 0;
    while (m.find() == true) {
      int idx = m.start(group);
      int eidx = m.end(group);
      if (idx > lastIdx) {
        sb.append(text.substring(lastIdx, idx));
      }
      sb.append(replacer.call(text.substring(idx, eidx)));
      lastIdx = eidx;
    }
    if (lastIdx < text.length()) {
      sb.append(text.substring(lastIdx, text.length()));
    }
    return sb.toString();
  }

  /**
   * get character at offset. different to charAt() it return 0 if outside range.
   * 
   * @param text
   * @param offset
   * @return
   */
  public static char lookAhead(String text, int offset)
  {
    if (text == null) {
      return 0;
    }
    if (offset < 0) {
      return 0;
    }
    if (offset >= text.length()) {
      return 0;
    }
    return text.charAt(offset);
  }

  /**
   * *
   * 
   * <pre>
   * StringUtils.splitFirst(null, ':')      = []
   * StringUtils.splitFirst("")        = []
   * StringUtils.splitFirst("a")       = ['a']
   * StringUtils.splitFirst("a: x")     = ['a', ' x' ]
   * </pre>
   * 
   * @param text
   * @param ch
   * @return
   */
  public static String[] splitFirst(String text, char ch)
  {
    if (text == null || text.length() == 0) {
      return new String[] {};
    }
    int idx = text.indexOf(ch);
    if (idx == -1) {
      return new String[] { text };
    }
    return new String[] { text.substring(0, idx), text.substring(idx + 1) };
  }

  /**
   * 
   * @param text
   * @return true, if text only contains \r and \n
   */
  public static boolean isNewLine(String text)
  {
    if (text == null || text.length() == 0) {
      return false;
    }
    String newLineChars = "\n\r";
    for (int i = 0; i < text.length(); ++i) {
      if (newLineChars.indexOf(text.charAt(i)) == -1) {
        return false;
      }
    }
    return true;
  }
}
