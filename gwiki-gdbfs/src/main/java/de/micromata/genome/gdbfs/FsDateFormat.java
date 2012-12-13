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
  public static final TimeZone UTC_TIMEZONE = TimeZone.getTimeZone("UTC");

  public static ThreadLocal<SimpleDateFormat> internalTimestamp = new ThreadLocal<SimpleDateFormat>() {

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
   * @Todo use utc.
   * @param date
   * @return
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

  public static String date2string(Date date)
  {
    if (date == null) {
      return null;
    }
    return internalTimestamp.get().format(date);
  }
}
