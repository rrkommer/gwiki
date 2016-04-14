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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import de.micromata.genome.util.types.Pair;

/**
 * @author Roger Rene Kommer (r.kommer@micromata.de)
 * 
 */
public class GWikiPipeListParser
{
  public static List<String> splitEscapedPipeList(String text)
  {
    List<String> ret = new ArrayList<String>();
    StringBuilder buf = new StringBuilder();
    int tl = text.length();
    for (int i = 0; i < tl; ++i) {
      char c = text.charAt(i);
      switch (c) {
        case '\\':
          if (tl > i) {
            ++i;
            buf.append(text.charAt(i));
            continue;
          } else {
            return ret;
          }
        case '|':
          ret.add(buf.toString());
          buf = new StringBuilder();
          break;
        default:
          buf.append(c);
          break;
      }
    }
    ret.add(buf.toString());
    return ret;
  }

  public static Pair<Map<String, String>, List<String>> splitListMapArguments(String t, Set<String> knownAttributes)
  {
    List<String> elems = splitEscapedPipeList(t);
    Map<String, String> m = new HashMap<String, String>();
    for (Iterator<String> it = elems.iterator(); it.hasNext();) {
      String s = it.next();
      int idx = s.indexOf('=');
      if (idx != -1) {
        String key = s.substring(0, idx);
        if (knownAttributes.contains(key) == true) {
          m.put(key, s.substring(idx + 1));
          it.remove();
          continue;
        }
      }
    }
    return Pair.make(m, elems);
  }
}
