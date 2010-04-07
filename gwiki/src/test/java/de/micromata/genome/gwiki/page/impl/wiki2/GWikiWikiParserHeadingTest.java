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

package de.micromata.genome.gwiki.page.impl.wiki2;

public class GWikiWikiParserHeadingTest extends GWikiWikiParserTestBase
{
  public void testHeadingWithText()
  {
    w2htest("h1. Heading\nText", "<h1><a name=\"Heading\" target=\"_top\"></a>Heading</h1>\n<p>Text</p>\n");
  }

  public void testHeadingWithEffect()
  {
    w2htest("h1. *Heading*", "<h1><a name=\"Heading\" target=\"_top\"></a><b>Heading</b></h1>\n");
  }

  public void testHeading()
  {
    w2htest("h1. Heading", "<h1><a name=\"Heading\" target=\"_top\"></a>Heading</h1>\n");
  }

}
