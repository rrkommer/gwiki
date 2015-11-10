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

/////////////////////////////////////////////////////////////////////////////
//
// $RCSfile: CronExpression.java,v $
//
// Project   genome
//
// Author    Wolfgang Jung (w.jung@micromata.de)
// Created   03.01.2007
// Copyright Micromata 03.01.2007
//
// $Id: CronExpression.java,v 1.2 2007/02/23 13:24:10 noodles Exp $
// $Revision: 1.2 $
// $Date: 2007/02/23 13:24:10 $
//
/////////////////////////////////////////////////////////////////////////////
package de.micromata.genome.gwiki.chronos.spi;

import java.io.ObjectStreamException;
import java.io.Serializable;
import java.util.BitSet;
import java.util.Calendar;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * The representation of a typical cron-expression. The expression consists of 5 fields:
 * <ul>
 * <li>minutes</li>
 * <li>hours</li>
 * <li>day in month</li>
 * <li>month</li>
 * <li>day of week</li>
 * </ul>
 * Each field can be in one of the following forms:
 * <ul>
 * <li><em>*</em> - matches all possible values</li>
 * <li><em>n</em> - a number, matches if the dates component is equal to the number</li>
 * <li><em>m-n</em> - a number range, matches if the dates component falls into the range</li>
 * <li><em>*&slash;n</em> - modulo operator, matches if the dates component is divisible by <em>n</em></li>
 * <li><em>m-n/x</em> - a number range, matches if the dates component <em>t</em> falls into the range <em>[m,n]</em> and <em>t-m</em> is
 * divisible by <em>x</em></li>
 * </ul>
 * The day of week field uses the following numbers for the weekdays:
 * <ul>
 * <li>1 : Monday: may be abbreviated as MON
 * <li>
 * <li>2: Tuesday (TUE)
 * <li>
 * <lI>7: Sunday (SUN)</li>
 * </ul>
 * <b>Differences to Unix cron</b> The Unix cron expressions handles the day of week different, example
 * 
 * <pre>
 *             0 0 1 *  MON
 * </pre>
 * 
 * On Unix, this expression fires on the first every month, but also on every Monday. This implementation fires only if the first of the
 * month is also an monday. This enables cron expressions, that fire on the first sunday every month:
 * 
 * <pre>
 *             0 0 1-7 *  SUN
 * </pre>
 * 
 * 
 * 
 */
public class CronExpression implements Serializable
{
  /**
   *
   */
  private static final long serialVersionUID = -7759438443785356083L;

  /** The logger */
  private static final org.apache.log4j.Logger log = org.apache.log4j.Logger.getLogger(CronExpression.class);

  private static final Pattern SINGLE_EXPRESSION = Pattern.compile("^([0-9]+)$");

  private static final Pattern RANGE_EXPRESSION = Pattern.compile("^([0-9]+)-([0-9]+)$");

  private static final Pattern EVERY_EXPRESSION = Pattern.compile("^\\*/([0-9]+)$");

  private static final Pattern RANGE_EVERY_EXPRESSION = Pattern.compile("^([0-9]+)-([0-9]+)/([0-9]+)$");

  private final String cronExpression;

  private final transient BitSet minutes = new BitSet(60);

  private final transient BitSet hours = new BitSet(24);

  private final transient BitSet days = new BitSet(32);

  private final transient BitSet months = new BitSet(13);

  private final transient BitSet dows = new BitSet(7);

  public CronExpression(final String arg)
  {
    parseCronExpression(arg);
    this.cronExpression = arg;
    if (log.isDebugEnabled()) {
      log.debug("Converted " + arg + " to " + getNormalizedString());
    }
  }

  public Object readResolve() throws ObjectStreamException
  {
    return new CronExpression(this.cronExpression);
  }

  private void parseCronExpression(final String arg)
  {
    final String components[] = arg.split("(\\s+)");
    if (components.length != 5 && components.length != 4) {
      throw new IllegalArgumentException("Cron expression must have 4 or 5 components, found only " + components.length + " in " + arg);
    }
    parseSubExpression(this.minutes, components[0], 0, 59, 0);
    parseSubExpression(this.hours, components[1], 0, 23, 0);
    parseSubExpression(this.days, components[2], 1, 32, 0);
    parseSubExpression(this.months, components[3], 1, 12, 1);
    if (components.length == 5) {
      parseSubExpression(this.dows, substituteWeekDays(components[4]), 1, 7, 0);
    } else {
      this.dows.set(1, 8);
    }
  }

  private String substituteWeekDays(String weekdays)
  {
    weekdays = weekdays.replace("MON", "1");
    weekdays = weekdays.replace("TUE", "2");
    weekdays = weekdays.replace("WED", "3");
    weekdays = weekdays.replace("THU", "4");
    weekdays = weekdays.replace("FRI", "5");
    weekdays = weekdays.replace("SAT", "6");
    weekdays = weekdays.replace("SUN", "7");
    return weekdays;
  }

  private void parseSubExpression(final BitSet bitSet, final String expression, final int min, final int max, final int offset)
  {
    // split after ","
    final String[] parts = expression.split(",");
    if (parts.length > 1) {
      for (final String part : parts) {
        parseSubExpression(bitSet, part, min, max, offset);
      }
      return;
    }
    // expression is now one of "*", "x", "x-y", "*/m" or "x-y/m"
    parseSimpleExpression(bitSet, expression, min, max, offset);
  }

  /**
   * @param bitSet
   * @param expression
   * @param min
   * @param max
   */
  private void parseSimpleExpression(final BitSet bitSet, final String expression, final int min, final int max, final int offset)
  {
    if (expression.equals("*") == true) {
      // all possibilities
      bitSet.set(min - offset, max + 1 - offset, true);
    } else {
      final Matcher singleExpression = SINGLE_EXPRESSION.matcher(expression);
      final Matcher rangeExpression = RANGE_EXPRESSION.matcher(expression);
      final Matcher everyExpression = EVERY_EXPRESSION.matcher(expression);
      final Matcher rangeEveryExpression = RANGE_EVERY_EXPRESSION.matcher(expression);
      if (singleExpression.matches() == true) {
        handleSingleNumber(bitSet, expression, min, max, singleExpression, offset);
      } else if (rangeExpression.matches() == true) {
        handleRange(bitSet, expression, min, max, rangeExpression, offset);
      } else if (everyExpression.matches() == true) {
        handleDivisior(bitSet, expression, min, max, everyExpression, offset);
      } else if (rangeEveryExpression.matches() == true) {
        handleRangeDivisor(bitSet, expression, min, max, rangeEveryExpression, offset);
      } else {
        throw new IllegalArgumentException("no valid cron-expression: " + expression);
      }
    }
  }

  /**
   * @param bitSet
   * @param expression
   * @param min
   * @param max
   * @param singleExpression
   * @param offset
   */
  private void handleSingleNumber(final BitSet bitSet, final String expression, final int min, final int max,
      final Matcher singleExpression, final int offset)
  {
    final int from = Integer.parseInt(singleExpression.group(1));
    ensureRange(expression, from, min, max);
    bitSet.set(from - offset);
  }

  /**
   * @param bitSet
   * @param expression
   * @param min
   * @param max
   * @param rangeEveryExpression
   * @param offset
   */
  private void handleRangeDivisor(final BitSet bitSet, final String expression, final int min, final int max,
      final Matcher rangeEveryExpression, final int offset)
  {
    int from = Integer.parseInt(rangeEveryExpression.group(1));
    final int to = Integer.parseInt(rangeEveryExpression.group(2));
    final int step = Integer.parseInt(rangeEveryExpression.group(3));
    while (from <= to) {
      ensureRange(expression, from, min, max);
      bitSet.set(from - offset);
      from += step;
    }
  }

  /**
   * @param bitSet
   * @param expression
   * @param min
   * @param max
   * @param everyExpression
   * @param offset
   */
  private void handleDivisior(final BitSet bitSet, final String expression, final int min, final int max, final Matcher everyExpression,
      final int offset)
  {
    int from = min;
    final int step = Integer.parseInt(everyExpression.group(1));
    while (from <= max) {
      ensureRange(expression, from, min, max);
      bitSet.set(from - offset);
      from += step;
    }
  }

  /**
   * @param bitSet
   * @param expression
   * @param min
   * @param max
   * @param rangeExpression
   * @param offset
   */
  private void handleRange(final BitSet bitSet, final String expression, final int min, final int max, final Matcher rangeExpression,
      final int offset)
  {
    final int from = Integer.parseInt(rangeExpression.group(1));
    final int to = Integer.parseInt(rangeExpression.group(2)) + 1;
    ensureRange(expression, from, min, max);
    ensureRange(expression, to, min, max);
    bitSet.set(from - offset, to - offset, true);
  }

  private void ensureRange(final String expression, final int from, final int min, final int max)
  {
    if (from < min || from > max) {
      throw new IllegalArgumentException("Cron expression would take value "
          + from
          + " outside of range ["
          + min
          + ","
          + max
          + "] while parsing '"
          + expression
          + "'");
    }
  }

  public String getNormalizedString()
  {
    return getNormalizedString(this.minutes, 0)
        + " "
        + getNormalizedString(this.hours, 0)
        + " "
        + getNormalizedString(this.days, 0)
        + " "
        + getNormalizedString(this.months, 1)
        + " "
        + getNormalizedString(this.dows, 0);
  }

  private String getNormalizedString(final BitSet bitSet, final int offset)
  {
    final StringBuilder sb = new StringBuilder();
    for (int i = 0; i < bitSet.size(); i++) {
      if (bitSet.get(i) == true) {
        if (sb.length() != 0) {
          sb.append(",");
        }
        sb.append(i + offset);
      }
    }
    return sb.toString();
  }

  @Override
  public String toString()
  {
    return this.cronExpression;
  }

  private void debug(final String message, final Calendar cal)
  {
    if (log.isDebugEnabled()) {
      log.debug(message + " " + cal.getTime() + " for " + this.cronExpression);
    }
  }

  public Date getNextFireTime(final Date date)
  {
    Calendar cal = Calendar.getInstance();
    cal.setTime(date);
    cal.set(Calendar.MILLISECOND, 0);
    cal.set(Calendar.SECOND, 0);
    if (this.dows.cardinality() == 0) {
      return getNextPossibleDate(cal, true).getTime();
    }
    // maximum 20 years in advance...
    cal = getNextPossibleDate(cal, true);
    debug("Checking weekday of", cal);
    for (int i = 0; i < 20; i++) {
      if (this.dows.get(dayOfWeek(cal)) == true) {
        debug("Found match", cal);
        return cal.getTime();
      }
      debug("Weekday does not match, advancing", cal);
      cal = getNextPossibleDate(cal, false);
    }
    return null;
  }

  private Calendar getNextPossibleDate(final Calendar cal, final boolean includeHoursAndMinutes)
  {
    boolean advanced;

    if (includeHoursAndMinutes == true) {
      advanced = advance(cal, Calendar.MINUTE, this.minutes);
      if (advanced == false && this.hours.get(cal.get(Calendar.HOUR_OF_DAY))) {
        return cal;
      }
      advanced = advance(cal, Calendar.HOUR_OF_DAY, this.hours);
      if (advanced == false) {
        return cal;
      }
    }
    advanced = advance(cal, Calendar.DAY_OF_MONTH, this.days);
    if (advanced == false && this.months.get(cal.get(Calendar.MONTH))) {
      return cal;
    }

    advanced = advance(cal, Calendar.MONTH, this.months);
    if (advanced == false) {
      return cal;
    }
    cal.add(1, Calendar.YEAR);
    return cal;
  }

  private boolean advance(final Calendar cal, final int field, final BitSet bitSet)
  {
    boolean advance = false;
    final int cur = cal.get(field);
    int next = bitSet.nextSetBit(cur + 1);
    if (next < 0) {
      next = bitSet.nextSetBit(0);
      advance = true;
    }
    cal.set(field, next);
    return advance;
  }

  private int dayOfWeek(final Calendar cal)
  {
    switch (cal.get(Calendar.DAY_OF_WEEK)) {
      case Calendar.MONDAY:
        return 1;
      case Calendar.TUESDAY:
        return 2;
      case Calendar.WEDNESDAY:
        return 3;
      case Calendar.THURSDAY:
        return 4;
      case Calendar.FRIDAY:
        return 5;
      case Calendar.SATURDAY:
        return 6;
      case Calendar.SUNDAY:
        return 7;
    }
    throw new IllegalArgumentException("Day " + cal + " has no valid day of week?");
  }

  public boolean matches(final Date date)
  {
    final Calendar cal = Calendar.getInstance();
    cal.setTime(date);
    if (this.minutes.get(cal.get(Calendar.MINUTE)) == false) {
      return false;
    }
    if (this.hours.get(cal.get(Calendar.HOUR_OF_DAY)) == false) {
      return false;
    }

    if (this.days.get(cal.get(Calendar.DAY_OF_MONTH)) == false) {
      return false;
    }

    if (this.months.get(cal.get(Calendar.MONTH)) == false) {
      return false;
    }

    if (this.dows.get(dayOfWeek(cal)) == false) {
      return false;
    }
    return true;
  }
}
