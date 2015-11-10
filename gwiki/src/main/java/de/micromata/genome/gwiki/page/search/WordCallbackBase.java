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

package de.micromata.genome.gwiki.page.search;

import org.apache.commons.collections15.ArrayStack;
import org.apache.commons.collections15.Buffer;

/**
 * Internal class to collect words.o
 * 
 * @author Roger Rene Kommer (r.kommer@micromata.de)
 * 
 */
public abstract class WordCallbackBase implements WordCallback
{
  protected Buffer<Integer> offsetLevel = new ArrayStack<Integer>();

  protected int getLevelOffset()
  {
    if (offsetLevel.isEmpty() == true) {
      return 0;
    }
    int ret = 0;
    for (Integer i : offsetLevel) {
      ret += i;
    }
    return ret;
  }

  public void popLevel()
  {
    offsetLevel.remove();
  }

  public void pushLevel(int level)
  {
    offsetLevel.add(level);
  }

}
