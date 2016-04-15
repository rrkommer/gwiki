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

package de.micromata.genome.gdbfs;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import org.apache.commons.lang.StringUtils;

/**
 * Formats standard filesystem date formats.
 * 
 * @author roger
 * 
 */
public class FsDateFormat
{

  /**
   * The Constant UTC_TIMEZONE.
   */
  public static final TimeZone UTC_TIMEZONE = TimeZone.getTimeZone("UTC");

  /**
   * The internal timestamp.
   */
  public static ThreadLocal<SimpleDateFormat> internalTimestamp = new ThreadLocal<SimpleDateFormat>()
  {

    @Override
    protected SimpleDateFormat initialValue()
    {
      SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmssSSS");
      sdf.setTimeZone(UTC_TIMEZONE);
      return sdf;
    }
  };

  /**
   * convert Stringified date to internal Date
   * 
   * Todo use utc.
   *
   * @param date the date
   * @return the date
   */
  public static Date string2date(String date)
  {
    if (StringUtils.isEmpty(date) == true) {
      return null;
    }
    try {
      Date ld = internalTimestamp.get().parse(date);
      return ld;
    } catch (ParseException ex) {
      throw new RuntimeException("Cannot parse date: " + date + "; " + ex.getMessage(), ex);

    }
  }

  /**
   * Date2string.
   *
   * @param date the date
   * @return the string
   */
  public static String date2string(Date date)
  {
    if (date == null) {
      return null;
    }
    return internalTimestamp.get().format(date);
  }
}
