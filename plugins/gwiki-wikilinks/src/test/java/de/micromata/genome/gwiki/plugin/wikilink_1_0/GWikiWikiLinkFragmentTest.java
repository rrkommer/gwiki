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

package de.micromata.genome.gwiki.plugin.wikilink_1_0;

import org.junit.Test;

import de.micromata.genome.gwiki.page.impl.wiki.fragment.GWikiFragment;
import de.micromata.genome.gwiki.page.impl.wiki.fragment.GWikiFragmentText;

/**
 * @author roger
 * 
 */
public class GWikiWikiLinkFragmentTest
{
  private GWikiFragment parseWikiLink(String text)
  {
    GWikiFragmentText textFrag = new GWikiFragmentText(text);
    GWikiFragment ret = GWikiWikiLinkFragment.parseText(textFrag);
    System.out.println(text + " => " + ret.toString());
    return ret;
  }

  @Test
  public void testParseWikLink()
  {
    parseWikiLink("getCurrentUserLocale");
    parseWikiLink("WikiLink");
    parseWikiLink("This is a WikiLink");
    parseWikiLink("WikiLink is this");
    parseWikiLink("WikiLink AnAnother");
  }
}
