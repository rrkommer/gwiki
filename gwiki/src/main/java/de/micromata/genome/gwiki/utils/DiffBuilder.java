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

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import de.micromata.genome.gwiki.utils.DiffLine.DiffType;
import de.micromata.genome.util.types.Converter;

/**
 * create a diff between two text files for each line.
 * 
 * @author Roger Rene Kommer (r.kommer@micromata.de)
 * 
 */
public class DiffBuilder
{
  protected boolean ignoreWs = false;

  public DiffBuilder()
  {

  }

  public DiffBuilder(boolean ignoreWs)
  {
    this.ignoreWs = ignoreWs;
  }

  public String normalize(String text)
  {
    text = text.replaceAll("\\s", "");
    return text;
  }

  public boolean equalLine(String left, String right)
  {
    if (left.equals(right) == true) {
      return true;
    }
    if (ignoreWs == false) {
      return false;
    }
    return normalize(left).equals(normalize(right));
  }

  protected boolean isIgnoreLine(String line)
  {
    return StringUtils.isBlank(line);
  }

  public int seekNext(List<String> lines, int i, String compareTo)
  {
    for (; i < lines.size(); ++i) {
      String l = lines.get(i);
      if (equalLine(l, compareTo) == true) {
        if (isIgnoreLine(l) == false) {
          return i;
        }
      }
    }
    return -1;
  }

  protected List<String> parseText(String text)
  {
    return Converter.parseStringTokens(text, "\n", false);
  }

  public DiffSet parse(String leftText, String rightText)
  {
    List<String> leftLines = parseText(leftText);
    List<String> rightLines = parseText(rightText);
    DiffSet diffSet = new DiffSet();
    int li = 0;
    int ri = 0;
    for (; li < leftLines.size() && ri < rightLines.size();) {
      String left = leftLines.get(li);
      String right = rightLines.get(ri);
      if (equalLine(left, right) == true) {
        diffSet.addLine(new DiffLine(DiffType.Equal, left, li, right, ri));
        ++li;
        ++ri;
        continue;
      }
      int lo = seekNext(leftLines, li + 1, right);
      int ro = seekNext(rightLines, ri + 1, left);
      if (ro == -1 && lo == -1) {
        diffSet.addLine(new DiffLine(DiffType.Differ, left, li, right, ri));
        ++li;
        ++ri;
        continue;
      }
      if (lo != -1 && (ro == -1 || lo < ro)) {
        for (; li < lo; ++li) {
          diffSet.addLine(new DiffLine(DiffType.LeftNew, leftLines.get(li), li, null, -1));
        }
        continue;
      }
      if (ro != -1 && (lo == -1 || ro < lo)) {
        for (; ri < ro; ++ri) {
          diffSet.addLine(new DiffLine(DiffType.RightNew, null, -1, rightLines.get(ri), ri));
        }
        continue;
      }
      // oppps
    }

    for (; li < leftLines.size(); ++li) {
      diffSet.addLine(new DiffLine(DiffType.LeftNew, leftLines.get(li), li, null, -1));
    }
    for (; ri < rightLines.size(); ++ri) {
      diffSet.addLine(new DiffLine(DiffType.RightNew, null, -1, rightLines.get(ri), ri));
    }
    return diffSet;
  }

  public DiffSet parse(Map<String, String> leftMap, Map<String, String> rightMap)
  {

    DiffSet diffSet = new DiffSet();
    Set<String> allKeys = new TreeSet<String>();
    allKeys.addAll(leftMap.keySet());
    allKeys.addAll(rightMap.keySet());
    int li = 0;
    int ri = 0;
    for (String k : allKeys) {
      String lv = leftMap.get(k);
      String rv = rightMap.get(k);
      if (lv == null) {
        diffSet.addLine(new PropDiffLine(DiffType.RightNew, k, null, -1, rv, ri));
        ++ri;
      } else if (rv == null) {
        diffSet.addLine(new PropDiffLine(DiffType.LeftNew, k, lv, li, null, -1));
        ++li;
      } else if (rv.equals(lv) == true) {
        diffSet.addLine(new PropDiffLine(DiffType.Equal, k, lv, li, rv, ri));
        ++li;
        ++ri;
      } else {
        diffSet.addLine(new PropDiffLine(DiffType.Differ, k, lv, li, rv, ri));
        ++li;
        ++ri;
      }
    }
    return diffSet;
  }
}
