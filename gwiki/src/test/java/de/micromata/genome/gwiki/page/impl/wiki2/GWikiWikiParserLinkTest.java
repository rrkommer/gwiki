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

////////////////////////////////////////////////////////////////////////////

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

////////////////////////////////////////////////////////////////////////////

package de.micromata.genome.gwiki.page.impl.wiki2;

import java.util.List;

import de.micromata.genome.gwiki.page.impl.wiki.fragment.GWikiFragment;
import de.micromata.genome.gwiki.page.impl.wiki.fragment.GWikiFragmentLink;

public class GWikiWikiParserLinkTest extends GWikiWikiParserTestBase
{

  public void testSingleLinkWithEscapedTitel()
  {
    List<GWikiFragment> frags = parseText("[Ein\\|Titel|ein/link]");
    frags = unwrapP(frags);
    assertEquals(1, frags.size());
    assertTrue(frags.get(0) instanceof GWikiFragmentLink);
    GWikiFragmentLink l = (GWikiFragmentLink) frags.get(0);
    assertEquals("ein/link", l.getTarget());
    // currently not supported:
    // frags = l.getChilds();
    // assertEquals(1, frags.size());
    // assertTrue(frags.get(0) instanceof GWikiFragmentText);
    // assertEquals("Ein|Titel", ((GWikiFragmentText) frags.get(0)).getSource());
  }

  public void testSingleLinkWithBoldTitle()
  {
    List<GWikiFragment> frags = parseText("[*Ein Titel*|ein/link]");
    frags = unwrapP(frags);
    assertEquals(1, frags.size());
    assertTrue(frags.get(0) instanceof GWikiFragmentLink);
    GWikiFragmentLink l = (GWikiFragmentLink) frags.get(0);
    assertEquals("ein/link", l.getTarget());
    // currently not supported
    // frags = l.getChilds();
    // assertEquals(1, frags.size());
    // assertTrue(frags.get(0) instanceof GWikiFragmentDecorator);
    // GWikiFragmentDecorator dec = (GWikiFragmentDecorator) frags.get(0);
    // assertEquals("<b>", dec.getPrefix());
    // frags = dec.getChilds();
    // assertEquals(1, frags.size());
    // assertEquals("Ein Titel", ((GWikiFragmentText) frags.get(0)).getSource());
  }

  public void testSingleLinkWithTitle()
  {
    List<GWikiFragment> frags = parseText("[Ein Titel|ein/link]");
    frags = unwrapP(frags);
    assertEquals(1, frags.size());
    assertTrue(frags.get(0) instanceof GWikiFragmentLink);
    GWikiFragmentLink l = (GWikiFragmentLink) frags.get(0);
    assertEquals("ein/link", l.getTarget());
    // currently not supported
    // frags = l.getChilds();
    // assertEquals(1, frags.size());
    // assertTrue(frags.get(0) instanceof GWikiFragmentText);
    // assertEquals("Ein Titel", ((GWikiFragmentText) frags.get(0)).getSource());
  }

  public void testSingleLink()
  {
    List<GWikiFragment> frags = parseText("[ein/link]");
    frags = unwrapP(frags);
    assertEquals(1, frags.size());
    assertTrue(frags.get(0) instanceof GWikiFragmentLink);
    GWikiFragmentLink l = (GWikiFragmentLink) frags.get(0);
    assertEquals("ein/link", l.getTarget());
  }

}
