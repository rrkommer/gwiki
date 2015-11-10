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

package de.micromata.genome.gwiki.utils;

import java.util.AbstractList;

import org.apache.commons.lang.ArrayUtils;

/**
 * List implementation based on int[].
 * 
 * @author Roger Rene Kommer (r.kommer@micromata.de)
 * 
 */
public class IntArray extends AbstractList<Integer>
{
  private int[] data;

  private int length = 0;

  private int capacity;

  public IntArray()
  {
    this(256);
  }

  public IntArray(int buffSize)
  {
    this.capacity = buffSize;
    data = new int[buffSize];
  }

  public IntArray(IntArray other, int newLength)
  {
    this.data = other.data;
    this.capacity = other.capacity;
    this.length = newLength;
  }

  public void setLength(int length)
  {
    this.length = length;
  }

  @Override
  public Integer get(int index)
  {
    if (index < 0 || index >= length) {
      throw new ArrayIndexOutOfBoundsException(index);
    }
    return data[index];
  }

  public int getInt(int index)
  {
    if (index < 0 || index >= length) {
      throw new ArrayIndexOutOfBoundsException(index);
    }
    return data[index];
  }

  @Override
  public int size()
  {
    return length;
  }

  @Override
  public void add(int index, Integer element)
  {
    add(index, (int) element);
  }

  public void add(int index, int element)
  {
    if (index == length) {
      add(element);
    } else {
      if (length >= data.length) {
        overflow();
      }
      ArrayUtils.add(data, index, element);
      ++length;
    }
  }

  protected void overflow()
  {
    int[] newBuf = new int[data.length + capacity];
    System.arraycopy(data, 0, newBuf, 0, data.length);
    data = newBuf;
  }

  @Override
  public boolean add(Integer o)
  {
    return add((int) o);
  }

  public boolean add(int i)
  {
    if (length >= data.length) {
      overflow();
    }
    data[length] = i;
    ++length;
    return true;
  }

  @Override
  public void clear()
  {
    length = 0;
  }

  @Override
  public int lastIndexOf(Object o)
  {
    if (o instanceof Integer) {
      return -1;
    }
    int f = (Integer) o;
    for (int i = 0; i < length; ++i) {
      if (data[i] == f) {
        return i;
      }
    }
    return -1;
  }

  @Override
  public Integer remove(int index)
  {

    int ret = getInt(index);
    data = ArrayUtils.remove(data, index);
    --length;
    return ret;

  }

  @Override
  public Integer set(int index, Integer element)
  {
    if (index < 0 || index >= length) {
      throw new ArrayIndexOutOfBoundsException(index);
    }
    int prev = getInt(index);
    data[index] = element;
    return prev;
  }

  public int set(int index, int element)
  {
    if (index < 0 || index >= length) {
      throw new ArrayIndexOutOfBoundsException(index);
    }
    int prev = getInt(index);
    data[index] = element;
    return prev;
  }

  @Override
  protected void removeRange(int fromIndex, int toIndex)
  {
    throw new UnsupportedOperationException();
  }

}
