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

package de.micromata.genome.gwiki.utils;

/**
 * A diff status of one line.
 * 
 * @author Roger Rene Kommer (r.kommer@micromata.de)
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
