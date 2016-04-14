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

import java.util.ArrayList;
import java.util.List;

import de.micromata.genome.util.types.Converter;

/**
 * Build a diff view for a line.
 * 
 * @author Roger Rene Kommer (r.kommer@micromata.de)
 * 
 */
public class WordDiffBuilder extends DiffBuilder
{
  protected List<String> parseText(String text)
  {
    String splitTokens = ".:,!?-_ \t\n";
    List<String> tks = Converter.parseStringTokens(text, splitTokens, true);
    List<String> ret = new ArrayList<String>();
    int idx = 0;
    for (String tk : tks) {
      if (tk.length() == 1 && splitTokens.contains(tk) && ret.size() > 0) {
        ret.set(idx - 1, ret.get(idx - 1) + tk);
      } else {
        ret.add(tk);
        ++idx;
      }

    }
    return ret;
  }
}
