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

package de.micromata.genome.gwiki.plugin.rogmp3_1_0;

import org.junit.Assert;
import org.junit.Test;

public class SplitTest
{
  private void check(String line, String[] expected)
  {
    Assert.assertArrayEquals(expected, CsvTable.split(line, '|'));
  }

  @Test
  public void testSplit()
  {
    check("", new String[] { ""});
    check("bla", new String[] { "bla"});
    check("a|b", new String[] { "a", "b"});
    check("|a|b", new String[] { "", "a", "b"});
    check("a|||b", new String[] { "a", "", "", "b"});

    String[] rec = CsvTable.split("1|440 hertz|Wolferl 200||||||||ADD|||1991||||||||||1319||0|Wolferl_200|1|1899-12-30 00:01:14||", '|');
    String t = rec[26];
  }
}
