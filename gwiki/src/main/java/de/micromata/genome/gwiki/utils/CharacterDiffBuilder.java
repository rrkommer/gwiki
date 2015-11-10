////////////////////////////////////////////////////////////////////////////
//
// Copyright (C) 2010-2013 Micromata GmbH / Roger Rene Kommer
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
import java.util.List;

/**
 * Create a diff inside a line.
 * 
 * @author Roger Rene Kommer (r.kommer@micromata.de)
 * 
 */
public class CharacterDiffBuilder extends DiffBuilder
{
  public String normalize(String text)
  {
    // text = text.replaceAll("\\s", "");
    return text;
  }

  public boolean equalLine(String left, String right)
  {
    return left.equals(right);
  }

  public boolean isIgnore(String line)
  {
    return false;
  }

  protected List<String> parseText(String text)
  {
    //String[] ret = text.split("[]");
    List<String> ret = new ArrayList<String>(text.length());
    for (int i = 0; i < text.length(); ++i) {
      ret.add(Character.toString(text.charAt(i)));
    }
    return ret;
  }
}
