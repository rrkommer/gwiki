/////////////////////////////////////////////////////////////////////////////
//
// Project   Micromata Genome Core
//
// Author    roger@micromata.de
// Created   23.02.2008
// Copyright Micromata 23.02.2008
//
/////////////////////////////////////////////////////////////////////////////
package de.micromata.genome.util.types;

import java.io.Serializable;

import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

/**
 * 
 * @author roger@micromata.de
 * 
 */
public class Triple<L, M, R> implements Serializable
{
  private static final long serialVersionUID = -1011236041210348804L;

  final private L left;

  final private M middle;

  final private R right;

  public Triple(L left, M middle, R right)
  {
    this.left = left;
    this.middle = middle;
    this.right = right;
  }

  public static <L, M, R> Triple<L, M, R> make(L l, M m, R r)
  {
    return new Triple<L, M, R>(l, m, r);
  }

  public L getLeft()
  {
    return left;
  }

  public M getMiddle()
  {
    return middle;
  }

  public R getRight()
  {
    return right;
  }

  public L getFirst()
  {
    return left;
  }

  public M getSecond()
  {
    return middle;
  }

  public R getThird()
  {
    return right;
  }

  public String toString()
  {
    final String result = new ToStringBuilder(this, ToStringStyle.SIMPLE_STYLE) //
        .append("1:", left) //
        .append("2:", middle) //
        .append("3:", right) //
        .toString();
    return result;
  }

  @Override
  public int hashCode()
  {
    return (ObjectUtils.hashCode(left) * 31 + ObjectUtils.hashCode(middle)) * 31 + ObjectUtils.hashCode(right);
  }
}
