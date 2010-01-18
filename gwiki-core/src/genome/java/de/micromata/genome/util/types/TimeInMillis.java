/////////////////////////////////////////////////////////////////////////////
//
// Project   Micromata Genome Core
//
// Author    roger@micromata.de
// Created   04.04.2008
// Copyright Micromata 04.04.2008
//
/////////////////////////////////////////////////////////////////////////////
package de.micromata.genome.util.types;

/**
 * Just a few constants for Time in milli seconds
 * 
 * @author roger@micromata.de
 * 
 */
public interface TimeInMillis
{
  public static final long SECOND = 1000;

  public static final long MINUTE = SECOND * 60;

  public static final long HOUR = MINUTE * 60;

  public static final long DAY = HOUR * 24;

  public static final long WEEK = DAY * 7;

  public static final long MAX_MONTH = DAY * 31;

  public static final long YEAR = DAY * 265;
}
