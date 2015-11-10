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


public class CollectTextWordCallback  extends WordCallbackBase
{
  private StringBuilder buffer = new StringBuilder();

  private int lastLevel = 1;

  private boolean needFinalClose = false;

  public void callback(String word, int level)
  {
    int ll = level + getLevelOffset();
    if (lastLevel == ll) {
      buffer.append(word);
    } else {
      if (buffer.length() > 0) {
        buffer.append("</^>");
      }
      buffer.append("<^").append(ll).append(">").append(word);
      needFinalClose = true;
      lastLevel = ll;
    }
  }

  public StringBuilder getBuffer()
  {
    if (needFinalClose == true) {
      buffer.append("</^>");
      needFinalClose = false;
    }
    return buffer;
  }

  public void setBuffer(StringBuilder buffer)
  {
    this.buffer = buffer;
  }

  public int getLastLevel()
  {
    return lastLevel;
  }

  public void setLastLevel(int lastLevel)
  {
    this.lastLevel = lastLevel;
  }
}
