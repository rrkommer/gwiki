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
package de.micromata.genome.gwiki.page.impl.parser;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

import junit.framework.Assert;
import junit.framework.TestCase;
import de.micromata.genome.gwiki.page.impl.wiki.parser.GWikiPipeListParser;
import de.micromata.genome.util.types.Pair;

/**
 * @author Roger Rene Kommer (r.kommer@micromata.de)
 * 
 */
public class GWikiPipeListParserTest extends TestCase
{

  public void testEmpty()
  {
    Set<String> a = Collections.emptySet();
    Pair<Map<String, String>, List<String>> ret = GWikiPipeListParser.splitListMapArguments("", a);
    Assert.assertEquals(0, ret.getFirst().size());
    Assert.assertEquals(1, ret.getSecond().size());
  }

  public void testSingle()
  {
    Set<String> a = Collections.emptySet();
    Pair<Map<String, String>, List<String>> ret = GWikiPipeListParser.splitListMapArguments("X", a);
    Assert.assertEquals(0, ret.getFirst().size());
    Assert.assertEquals(1, ret.getSecond().size());
    Assert.assertEquals("X", ret.getSecond().get(0));
  }

  public void testDouble()
  {
    Set<String> a = Collections.emptySet();
    Pair<Map<String, String>, List<String>> ret = GWikiPipeListParser.splitListMapArguments("X|A", a);
    Assert.assertEquals(0, ret.getFirst().size());
    Assert.assertEquals(2, ret.getSecond().size());
    Assert.assertEquals("A", ret.getSecond().get(1));
  }

  public void testEscaped()
  {
    Set<String> a = Collections.emptySet();
    Pair<Map<String, String>, List<String>> ret = GWikiPipeListParser.splitListMapArguments("X\\|A", a);
    Assert.assertEquals(0, ret.getFirst().size());
    Assert.assertEquals(1, ret.getSecond().size());
    Assert.assertEquals("X|A", ret.getSecond().get(0));
  }

  public void testEscaped2()
  {
    Set<String> a = Collections.emptySet();
    Pair<Map<String, String>, List<String>> ret = GWikiPipeListParser.splitListMapArguments("X\\\\|A", a);
    Assert.assertEquals(0, ret.getFirst().size());
    Assert.assertEquals(2, ret.getSecond().size());
    Assert.assertEquals("X\\", ret.getSecond().get(0));
    Assert.assertEquals("A", ret.getSecond().get(1));
  }
}
