/////////////////////////////////////////////////////////////////////////////
//
// Project   DHL-ParcelOnlinePostage
//
// Author    roger@micromata.de
// Created   14.12.2009
// Copyright Micromata 14.12.2009
//
/////////////////////////////////////////////////////////////////////////////
package de.micromata.genome.gwiki.utils;

/**
 * A diff status of one line.
 * 
 * @author roger
 * 
 */
public class DiffLine
{
  public static enum DiffType
  {
    Equal, //
    Differ, //
    LeftNew, //
    RightNew, //
    ;
  }

  private DiffType diffType = DiffType.Equal;

  private String left;

  private int leftIndex;

  private String right;

  private int rightIndex;

  public DiffLine(DiffType diffType, String left, int li, String right, int ri)
  {
    this.diffType = diffType;
    this.left = left;
    this.leftIndex = li;
    this.right = right;
    this.rightIndex = ri;
  }

  public DiffType getDiffType()
  {
    return diffType;
  }

  public void setDiffType(DiffType diffType)
  {
    this.diffType = diffType;
  }

  public String getLeft()
  {
    return left;
  }

  public void setLeft(String left)
  {
    this.left = left;
  }

  public String getRight()
  {
    return right;
  }

  public void setRight(String right)
  {
    this.right = right;
  }

  public int getLeftIndex()
  {
    return leftIndex;
  }

  public void setLeftIndex(int leftIndex)
  {
    this.leftIndex = leftIndex;
  }

  public int getRightIndex()
  {
    return rightIndex;
  }

  public void setRightIndex(int rightIndex)
  {
    this.rightIndex = rightIndex;
  }

}
