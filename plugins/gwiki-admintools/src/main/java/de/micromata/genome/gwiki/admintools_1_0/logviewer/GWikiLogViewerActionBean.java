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
package de.micromata.genome.gwiki.admintools_1_0.logviewer;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import de.micromata.genome.gwiki.model.GWikiLogEntry;
import de.micromata.genome.gwiki.model.GWikiLogLevel;
import de.micromata.genome.gwiki.model.GWikiLogViewer;
import de.micromata.genome.gwiki.model.GWikiLogging;
import de.micromata.genome.gwiki.page.impl.actionbean.ActionBeanBase;
import de.micromata.genome.gwiki.utils.StringUtils;
import de.micromata.genome.util.types.Pair;

/**
 * @author Stefan Stuetzer (s.stuetzer@micromata.de)
 */
public class GWikiLogViewerActionBean extends ActionBeanBase
{
  private String searchMessage;

  private String selectedLogLevel;

  private Date from;

  private String fromDate;

  private int fromHour;

  private int fromMin;

  private int fromSec;

  private Date to;

  private String toDate;

  private int toHour;

  private int toMin;

  private int toSec;

  private String paramKey1;

  private String paramValue1;

  private String paramKey2;

  private String paramValue2;

  private int offset;

  private int selectedPageSize;

  private List<GWikiLogEntry> result;
  
  public ThreadLocal<SimpleDateFormat> internalTimestamp = new ThreadLocal<SimpleDateFormat>() {
    @Override
    protected SimpleDateFormat initialValue()
    {
      SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
      return sdf;
    }
  };

  @Override
  public Object onInit()
  {
    Calendar cal = Calendar.getInstance();
    Date now = new Date();

    fromDate = internalTimestamp.get().format(now);
    toDate = internalTimestamp.get().format(now);

    // prefill from time
    cal.setTime(now);
    cal.add(Calendar.HOUR_OF_DAY, -2);
    this.fromHour = cal.get(Calendar.HOUR_OF_DAY);
    this.fromMin = cal.get(Calendar.MINUTE);
    this.fromSec = cal.get(Calendar.SECOND);

    // prefill to time
    cal.setTime(now);
    cal.add(Calendar.MINUTE, 10);
    this.toHour = cal.get(Calendar.HOUR_OF_DAY);
    this.toMin = cal.get(Calendar.MINUTE);
    this.toSec = cal.get(Calendar.SECOND);

    return null;
  }

  public Object onSearch()
  {
    try {
      to = formatDate(toDate, toHour, toMin, toSec);
      from = formatDate(fromDate, fromHour, fromMin, fromSec);
    } catch (ParseException e) {
      wikiContext.addSimpleValidationError("Date not valid");
    }

    List<Pair<String, String>> searchParams = new ArrayList<Pair<String, String>>();
    if (StringUtils.isNotBlank(paramKey1) == true && StringUtils.isNotBlank(paramValue1) == true) {
      searchParams.add(new Pair<String, String>(paramKey1, paramValue1));
    }
    if (StringUtils.isNotBlank(paramKey2) == true && StringUtils.isNotBlank(paramValue2) == true) {
      searchParams.add(new Pair<String, String>(paramKey2, paramValue2));
    }

    result = new ArrayList<GWikiLogEntry>();
    GWikiLogging loggingProvider = wikiContext.getWikiWeb().getLogging();
    if (loggingProvider instanceof GWikiLogViewer) {
      ((GWikiLogViewer) loggingProvider).grep(from, to, GWikiLogLevel.valueOf(selectedLogLevel), this.searchMessage, searchParams,
          this.offset, this.selectedPageSize, new GWikiLogViewer.Callback() {

            public void found(GWikiLogEntry entry)
            {
              result.add(entry);
            }
          });
    }
    return null;
  }

  public Object onClear()
  {
    this.searchMessage = null;
    this.paramKey1 = null;
    this.paramValue1 = null;
    this.paramKey2 = null;
    this.paramValue2 = null;
    this.fromDate = null;

    this.toDate = null;

    this.selectedPageSize = 10;
    return null;
  }

  private Date formatDate(String stringDate, int hour, int min, int sec) throws ParseException
  {
    Date date = internalTimestamp.get().parse(stringDate);
    Calendar c = Calendar.getInstance();
    c.setTime(date);
    c.set(Calendar.HOUR_OF_DAY, hour);
    c.set(Calendar.MINUTE, min);
    c.set(Calendar.SECOND, sec);

    return c.getTime();
  }

  /**
   * @return the selectedLogLevel
   */
  public String getSelectedLogLevel()
  {
    return selectedLogLevel;
  }

  /**
   * @param selectedLogLevel the selectedLogLevel to set
   */
  public void setSelectedLogLevel(String selectedLogLevel)
  {
    this.selectedLogLevel = selectedLogLevel;
  }

  /**
   * @return the offset
   */
  public int getOffset()
  {
    return offset;
  }

  /**
   * @param offset the offset to set
   */
  public void setOffset(int offset)
  {
    this.offset = offset;
  }

  /**
   * @return the paramKey1
   */
  public String getParamKey1()
  {
    return paramKey1;
  }

  /**
   * @param paramKey1 the paramKey1 to set
   */
  public void setParamKey1(String paramKey1)
  {
    this.paramKey1 = paramKey1;
  }

  /**
   * @return the paramValue1
   */
  public String getParamValue1()
  {
    return paramValue1;
  }

  /**
   * @param paramValue1 the paramValue1 to set
   */
  public void setParamValue1(String paramValue1)
  {
    this.paramValue1 = paramValue1;
  }

  /**
   * @return the paramKey2
   */
  public String getParamKey2()
  {
    return paramKey2;
  }

  /**
   * @param paramKey2 the paramKey2 to set
   */
  public void setParamKey2(String paramKey2)
  {
    this.paramKey2 = paramKey2;
  }

  /**
   * @return the paramValue2
   */
  public String getParamValue2()
  {
    return paramValue2;
  }

  /**
   * @param paramValue2 the paramValue2 to set
   */
  public void setParamValue2(String paramValue2)
  {
    this.paramValue2 = paramValue2;
  }

  /**
   * @param searchMessage the searchMessage to set
   */
  public void setSearchMessage(String searchMessage)
  {
    this.searchMessage = searchMessage;
  }

  /**
   * @return the fromDate
   */
  public String getFromDate()
  {
    return fromDate;
  }

  /**
   * @param fromDate the fromDate to set
   */
  public void setFromDate(String fromDate)
  {
    this.fromDate = fromDate;
  }

  /**
   * @return the fromHour
   */
  public int getFromHour()
  {
    return fromHour;
  }

  /**
   * @param fromHour the fromHour to set
   */
  public void setFromHour(int fromHour)
  {
    this.fromHour = fromHour;
  }

  /**
   * @return the fromMin
   */
  public int getFromMin()
  {
    return fromMin;
  }

  /**
   * @param fromMin the fromMin to set
   */
  public void setFromMin(int fromMin)
  {
    this.fromMin = fromMin;
  }

  /**
   * @return the fromSec
   */
  public int getFromSec()
  {
    return fromSec;
  }

  /**
   * @param fromSec the fromSec to set
   */
  public void setFromSec(int fromSec)
  {
    this.fromSec = fromSec;
  }

  /**
   * @return the toDate
   */
  public String getToDate()
  {
    return toDate;
  }

  /**
   * @param toDate the toDate to set
   */
  public void setToDate(String toDate)
  {
    this.toDate = toDate;
  }

  /**
   * @return the toHour
   */
  public int getToHour()
  {
    return toHour;
  }

  /**
   * @param toHour the toHour to set
   */
  public void setToHour(int toHour)
  {
    this.toHour = toHour;
  }

  /**
   * @return the toMin
   */
  public int getToMin()
  {
    return toMin;
  }

  /**
   * @param toMin the toMin to set
   */
  public void setToMin(int toMin)
  {
    this.toMin = toMin;
  }

  /**
   * @return the toSec
   */
  public int getToSec()
  {
    return toSec;
  }

  /**
   * @param toSec the toSec to set
   */
  public void setToSec(int toSec)
  {
    this.toSec = toSec;
  }

  /**
   * @return the searchMessage
   */
  public String getSearchMessage()
  {
    return searchMessage;
  }

  public List<Pair<String, String>> getLogLevels()
  {
    List<Pair<String, String>> levels = new ArrayList<Pair<String, String>>();
    for (GWikiLogLevel level : GWikiLogLevel.values()) {
      levels.add(new Pair<String, String>(level.name(), level.name()));
    }
    return levels;
  }

  /**
   * @param selectedPageSize the selectedPageSize to set
   */
  public void setSelectedPageSize(int selectedPageSize)
  {
    this.selectedPageSize = selectedPageSize;
  }

  /**
   * @return the selectedPageSize
   */
  public int getSelectedPageSize()
  {
    return selectedPageSize;
  }

  /**
   * @param result the result to set
   */
  public void setResult(List<GWikiLogEntry> result)
  {
    this.result = result;
  }

  /**
   * @return the result
   */
  public List<GWikiLogEntry> getResult()
  {
    return result;
  }
}
