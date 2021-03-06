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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;

/**
 * Some standard tools handling time.
 * 
 * @author Roger Rene Kommer (r.kommer@micromata.de)
 * 
 */
public class TimeUtils
{
  public static final String ISO_DATETIME = "yyyy-MM-dd HH:mm";

  public static final String ISO_TIMESTAMP = "yyyy-MM-dd HH:mm:ss:SSS";

  private static final ThreadLocal<Map<String, SimpleDateFormat>> dateFormatterCache = new ThreadLocal<Map<String, SimpleDateFormat>>() {

    @Override
    protected Map<String, SimpleDateFormat> initialValue()
    {
      return new HashMap<String, SimpleDateFormat>();
    }
  };

  public static SimpleDateFormat getDateFormatter(String tfs)
  {
    Map<String, SimpleDateFormat> m = dateFormatterCache.get();
    if (m.containsKey(tfs) == true) {
      return m.get(tfs);
    }
    try {
      SimpleDateFormat sd = new SimpleDateFormat(tfs);
      m.put(tfs, sd);
      return sd;
    } catch (Exception ex) {
      // TODO gwiki warn
      return getDateFormatter(ISO_DATETIME);
    }

  }

  public static String formatDate(Date date, String timeFormat, String timeZone)
  {
    SimpleDateFormat sdf = getDateFormatter(timeFormat);
    TimeZone ltz = TimeZone.getDefault();
    Date nd = date;
    TimeZone tz = TimeZone.getTimeZone(timeZone);
    if (ltz.hasSameRules(tz) == false) {
      long lo = ltz.getRawOffset();
      long to = tz.getRawOffset();
      nd = new Date(date.getTime() - lo + to);
    }
    return sdf.format(nd);
  }

  public static Date parseDate(String sdate, String timeFormat, String timeZone)
  {
    SimpleDateFormat sdf = getDateFormatter(timeFormat);
    TimeZone ltz = TimeZone.getDefault();
    Date date;
    try {
      date = sdf.parse(sdate);
    } catch (ParseException pe) {
      throw new RuntimeException("Cannot parse date: " + sdate + "; " + pe.getMessage(), pe);
    }
    Date nd = date;
    TimeZone tz = TimeZone.getTimeZone(timeZone);
    if (ltz.hasSameRules(tz) == false) {
      long lo = ltz.getRawOffset();
      long to = tz.getRawOffset();
      nd = new Date(date.getTime() - to + lo);
    }
    return nd;
  }
}
