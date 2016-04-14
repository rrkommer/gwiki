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


package de.micromata.genome.gwiki.utils;

import junit.framework.TestCase;
import de.micromata.genome.gwiki.utils.DiffLine.DiffType;

public class DiffUtilsTest extends TestCase
{
  public void testWordDiffBuilder2()
  {
    String left = "aaa.ccc";
    String right = "aaa.bbb.ccc";
    WordDiffBuilder diffBuilder = new WordDiffBuilder();
    DiffSet df = diffBuilder.parse(left, right);
    assertEquals(3, df.getLines().size());
    assertEquals(DiffType.Equal, df.getLines().get(0).getDiffType());
    assertEquals(DiffType.RightNew, df.getLines().get(1).getDiffType());
    assertEquals(DiffType.Equal, df.getLines().get(2).getDiffType());
  }
  public void testWordDiffBuilder1()
  {
    String left = "aaa ccc";
    String right = "aaa bbb ccc";
    WordDiffBuilder diffBuilder = new WordDiffBuilder();
    DiffSet df = diffBuilder.parse(left, right);
    assertEquals(3, df.getLines().size());
    assertEquals(DiffType.Equal, df.getLines().get(0).getDiffType());
    assertEquals(DiffType.RightNew, df.getLines().get(1).getDiffType());
    assertEquals(DiffType.Equal, df.getLines().get(2).getDiffType());
  }

  public void testDiffLine3()
  {
    String left = "ac";
    String right = "axc";
    CharacterDiffBuilder diffBuilder = new CharacterDiffBuilder();
    DiffSet df = diffBuilder.parse(left, right);
    assertEquals(3, df.getLines().size());
    assertEquals(DiffType.Equal, df.getLines().get(0).getDiffType());
    assertEquals(DiffType.RightNew, df.getLines().get(1).getDiffType());
    assertEquals(DiffType.Equal, df.getLines().get(2).getDiffType());
  }

  public void testDiffLine2()
  {
    String left = "axc";
    String right = "ac";
    CharacterDiffBuilder diffBuilder = new CharacterDiffBuilder();
    DiffSet df = diffBuilder.parse(left, right);
    assertEquals(3, df.getLines().size());
    assertEquals(DiffType.Equal, df.getLines().get(0).getDiffType());
    assertEquals(DiffType.LeftNew, df.getLines().get(1).getDiffType());
    assertEquals(DiffType.Equal, df.getLines().get(2).getDiffType());
  }

  public void testDiffLine1()
  {
    String left = "abc";
    String right = "axc";
    CharacterDiffBuilder diffBuilder = new CharacterDiffBuilder();
    DiffSet df = diffBuilder.parse(left, right);
    assertEquals(3, df.getLines().size());
    assertEquals(DiffType.Equal, df.getLines().get(0).getDiffType());
    assertEquals(DiffType.Differ, df.getLines().get(1).getDiffType());
    assertEquals(DiffType.Equal, df.getLines().get(2).getDiffType());
  }

  public void testMidNew2()
  {
    String left = "b\nc";
    String right = "b\na\nc";
    DiffBuilder diffBuilder = new DiffBuilder();
    DiffSet df = diffBuilder.parse(left, right);
    assertEquals(3, df.getLines().size());
    assertEquals(DiffType.Equal, df.getLines().get(0).getDiffType());
    assertEquals(DiffType.RightNew, df.getLines().get(1).getDiffType());
    assertEquals(DiffType.Equal, df.getLines().get(2).getDiffType());
  }

  public void testMidNew1()
  {
    String left = "b\na\nc";
    String right = "b\nc";
    DiffBuilder diffBuilder = new DiffBuilder();
    DiffSet df = diffBuilder.parse(left, right);
    assertEquals(3, df.getLines().size());
    assertEquals(DiffType.Equal, df.getLines().get(0).getDiffType());
    assertEquals(DiffType.LeftNew, df.getLines().get(1).getDiffType());
    assertEquals(DiffType.Equal, df.getLines().get(2).getDiffType());
  }

  public void testLastNew2()
  {
    String left = "b\na";
    String right = "b";
    DiffBuilder diffBuilder = new DiffBuilder();
    DiffSet df = diffBuilder.parse(left, right);
    assertEquals(2, df.getLines().size());
    assertEquals(DiffType.Equal, df.getLines().get(0).getDiffType());
    assertEquals(DiffType.LeftNew, df.getLines().get(1).getDiffType());
  }

  public void testLastNew()
  {
    String left = "b";
    String right = "b\na";
    DiffBuilder diffBuilder = new DiffBuilder();
    DiffSet df = diffBuilder.parse(left, right);
    assertEquals(2, df.getLines().size());
    assertEquals(DiffType.Equal, df.getLines().get(0).getDiffType());
    assertEquals(DiffType.RightNew, df.getLines().get(1).getDiffType());
  }

  public void testFirstNew2()
  {
    String left = "b";
    String right = "a\nb";
    DiffBuilder diffBuilder = new DiffBuilder();
    DiffSet df = diffBuilder.parse(left, right);
    assertEquals(2, df.getLines().size());
    assertEquals(DiffType.RightNew, df.getLines().get(0).getDiffType());
    assertEquals(DiffType.Equal, df.getLines().get(1).getDiffType());
  }

  public void testFirstNew()
  {
    String left = "a\nb";
    String right = "b";
    DiffBuilder diffBuilder = new DiffBuilder();
    DiffSet df = diffBuilder.parse(left, right);
    assertEquals(2, df.getLines().size());
    assertEquals(DiffType.LeftNew, df.getLines().get(0).getDiffType());
    assertEquals(DiffType.Equal, df.getLines().get(1).getDiffType());
  }

  public void testLastDiffers()
  {
    String left = "a\nb";
    String right = "a\nx";
    DiffBuilder diffBuilder = new DiffBuilder();
    DiffSet df = diffBuilder.parse(left, right);
    assertEquals(2, df.getLines().size());
    assertEquals(DiffType.Equal, df.getLines().get(0).getDiffType());
    assertEquals(DiffType.Differ, df.getLines().get(1).getDiffType());
  }

  public void testFirstDiffers()
  {
    String left = "x\nb";
    String right = "a\nb";
    DiffBuilder diffBuilder = new DiffBuilder();
    DiffSet df = diffBuilder.parse(left, right);
    assertEquals(2, df.getLines().size());
    assertEquals(DiffType.Differ, df.getLines().get(0).getDiffType());
    assertEquals(DiffType.Equal, df.getLines().get(1).getDiffType());
  }

  public void testEqualIgnoreWs()
  {
    String left = " a \n b";
    String right = "a\nb ";
    DiffBuilder diffBuilder = new DiffBuilder(true);
    DiffSet df = diffBuilder.parse(left, right);
    assertEquals(2, df.getLines().size());
    assertEquals(DiffType.Equal, df.getLines().get(0).getDiffType());
    assertEquals(DiffType.Equal, df.getLines().get(1).getDiffType());
  }

  public void testEqual()
  {
    String left = "a\nb";
    String right = left;
    DiffBuilder diffBuilder = new DiffBuilder();
    DiffSet df = diffBuilder.parse(left, right);
    assertEquals(2, df.getLines().size());
    assertEquals(DiffType.Equal, df.getLines().get(0).getDiffType());
    assertEquals(DiffType.Equal, df.getLines().get(1).getDiffType());
  }
}
