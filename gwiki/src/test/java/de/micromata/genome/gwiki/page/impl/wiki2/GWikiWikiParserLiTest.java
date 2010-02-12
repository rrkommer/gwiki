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

////////////////////////////////////////////////////////////////////////////


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

////////////////////////////////////////////////////////////////////////////


package de.micromata.genome.gwiki.page.impl.wiki2;

public class GWikiWikiParserLiTest extends GWikiWikiParserTestBase
{
  public void testNl()
  {
    w2htest("* bla\\\\\nx\n* blub", "<ul class=\"star\"><li>bla<br/>\nx</li><li>blub</li></ul>");
  }
  public void testNested()
  {
    w2htest("* bla\n** blub", "<ul class=\"star\"><li>bla</li><ul class=\"star\"><li>blub</li></ul></ul>");
  }

  public void testLiCh()
  {
    w2htest("- bla\n* blub", "<ul class=\"minus\" type=\"square\"><li>bla</li></ul><ul class=\"star\"><li>blub</li></ul>");
  }

  public void testLi5()
  {
    w2htest("# bla\n# blub", "<ol><li>bla</li><li>blub</li></ol>");
  }

  public void testLi4()
  {
    w2htest("- bla\n- blub", "<ul class=\"minus\" type=\"square\"><li>bla</li><li>blub</li></ul>");
  }

  public void testLi3()
  {
    w2htest("* bla\n\n* blub", "<ul class=\"star\"><li>bla</li></ul><br/>\n<ul class=\"star\"><li>blub</li></ul>");
  }

  public void testLi2()
  {
    w2htest("* bla\n* blub", "<ul class=\"star\"><li>bla</li><li>blub</li></ul>");
  }

  public void testLi1()
  {
    w2htest("* bla", "<ul class=\"star\"><li>bla</li></ul>");
  }
}
