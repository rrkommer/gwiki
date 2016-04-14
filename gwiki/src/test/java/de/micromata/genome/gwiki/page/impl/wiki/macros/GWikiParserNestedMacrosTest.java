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

package de.micromata.genome.gwiki.page.impl.wiki.macros;

import java.util.List;

import org.junit.Test;

import de.micromata.genome.gwiki.page.impl.wiki.fragment.GWikiFragment;
import de.micromata.genome.gwiki.page.impl.wiki2.GWikiWikiParserTestBase;

public class GWikiParserNestedMacrosTest extends GWikiWikiParserTestBase
{
  //  @Test
  public void xtestNestedDivs()
  {
    List<GWikiFragment> frags = parseText("{div:x=y}{div:x=z}asdf{div}{div}", stdMacroFactories);
  }

  @Test
  public void testNestedSpans()
  {
    List<GWikiFragment> frags = parseText("{span:x=y}{span:x=z}asdf{span}{span}", stdMacroFactories);
  }
}
