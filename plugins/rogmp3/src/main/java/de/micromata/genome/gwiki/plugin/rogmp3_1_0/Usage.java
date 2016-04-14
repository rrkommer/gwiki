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
