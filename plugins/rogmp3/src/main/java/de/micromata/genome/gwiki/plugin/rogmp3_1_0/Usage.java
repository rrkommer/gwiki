package de.micromata.genome.gwiki.plugin.rogmp3_1_0;

import java.util.Date;

import de.micromata.genome.util.types.Converter;

public class Usage
{
  public static final Integer USER = 0;

  public static final Integer TYPE = 1;

  public static final Integer PK = 2;

  public static final Integer COUNT = 3;

  public static final Integer DATE = 4;

  public static final Integer COLUMNCOUNT = 5;

  protected String[] rec;

  public String[] getRec()
  {
    return rec;
  }

  public void setRec(String[] rec)
  {
    this.rec = rec;
  }

  public Usage()
  {
    rec = new String[COLUMNCOUNT];
    setCount(0);
    setDate(new Date(0));
  }

  public Usage(String userName, String type, String pk)
  {
    this();
    rec[USER] = userName;
    rec[TYPE] = type;
    rec[PK] = pk;
  }

  public Usage(String[] rec)
  {
    super();
    this.rec = rec;
  }

  public void setCount(int c)
  {
    rec[COUNT] = Integer.toString(c);
  }

  public int getCount()
  {
    return Integer.parseInt(rec[COUNT]);
  }

  public void incrementCount()
  {
    setCount(getCount() + 1);
  }

  public Date getDate()
  {
    return Converter.parseIsoDateToDate(rec[DATE]);
  }
  public String getDateString()
  {
    return rec[DATE];
  }

  public void setDate(Date date)
  {
    rec[DATE] = Converter.formatByIsoDayFormat(date);
  }
}
