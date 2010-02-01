/////////////////////////////////////////////////////////////////////////////
//
// Project   DHL-ParcelOnlinePostage
//
// Author    roger@micromata.de
// Created   19.12.2009
// Copyright Micromata 19.12.2009
//
/////////////////////////////////////////////////////////////////////////////
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
