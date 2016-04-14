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
import java.util.List;

import de.micromata.genome.gwiki.page.impl.wiki.smileys.GWikiWikiSmileyParsePostprocessor;

public class GWikiWikiParsePostprocessorRegistry
{
  private static GWikiWikiParsePostprocessorRegistry INSTANCE = new GWikiWikiParsePostprocessorRegistry();
  private List<GWikiWikiParsePostprocessor> processors = new ArrayList<>();

  public static GWikiWikiParsePostprocessorRegistry get()
  {
    return INSTANCE;
  }

  public GWikiWikiParsePostprocessorRegistry()
  {
    register(new GWikiWikiParagraphParsePostprocessor());
    register(new GWikiWikiSmileyParsePostprocessor());
  }

  public List<GWikiWikiParsePostprocessor> getProcessors()
  {
    return processors;
  }

  public void register(GWikiWikiParsePostprocessor proc)
  {
    for (int i = 0; i < processors.size(); ++i) {
      if (processors.get(i).getPrio() > proc.getPrio()) {
        processors.add(i, proc);
        return;
      }
    }
    processors.add(proc);
  }
}
