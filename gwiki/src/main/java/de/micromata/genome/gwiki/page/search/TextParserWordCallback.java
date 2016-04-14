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
