////////////////////////////////////////////////////////////////////////////
// 
// Copyright (C) 2010 Micromata GmbH
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
package de.micromata.genome.gwiki.pagelifecycle_1_0.wizard;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.TimeZone;

import org.apache.commons.lang.StringUtils;

import de.micromata.genome.gwiki.model.GWikiArtefakt;
import de.micromata.genome.gwiki.model.GWikiElement;
import de.micromata.genome.gwiki.model.GWikiLog;
import de.micromata.genome.gwiki.model.GWikiProps;
import de.micromata.genome.gwiki.model.GWikiWebUtils;
import de.micromata.genome.gwiki.model.GWikiWikiSelector;
import de.micromata.genome.gwiki.model.mpt.GWikiMultipleWikiSelector;
import de.micromata.genome.gwiki.page.GWikiContext;
import de.micromata.genome.gwiki.page.impl.actionbean.ActionBeanBase;
import de.micromata.genome.gwiki.pagelifecycle_1_0.artefakt.BranchFileStats;
import de.micromata.genome.gwiki.pagelifecycle_1_0.artefakt.FileStatsDO;
import de.micromata.genome.gwiki.pagelifecycle_1_0.artefakt.GWikiBranchFileStatsArtefakt;
import de.micromata.genome.gwiki.pagelifecycle_1_0.model.FileState;
import de.micromata.genome.gwiki.pagelifecycle_1_0.model.PlcConstants;
import de.micromata.genome.gwiki.pagelifecycle_1_0.model.PlcUtils;
import de.micromata.genome.gwiki.web.GWikiServlet;
import de.micromata.genome.util.runtime.CallableX;

/**
 * Wizard step for persist article timing options
 * 
 * @author Stefan Stuetzer (s.stuetzer@micromata.com)
 */
public class TimingStepWizardAction extends ActionBeanBase
{

  private GWikiElement element;

  private List<String> tmAvailableReviewers;

  private String tmSelectedReviewer;

  private boolean tmImmediately;

  private Date tmFrom;

  private String tmFromDate;

  private int tmFromHour;

  private int tmFromMin;

  private Date tmTo;

  private String tmToDate;

  private int tmToHour;

  private int tmToMin;
  
  public static final TimeZone UTC_TIMEZONE = TimeZone.getTimeZone("UTC");

  public ThreadLocal<SimpleDateFormat> internalTimestamp = new ThreadLocal<SimpleDateFormat>() {
    @Override
    protected SimpleDateFormat initialValue()
    {
      SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
      sdf.setTimeZone(UTC_TIMEZONE);
      return sdf;
    }
  };

  public Object onSave()
  {
    GWikiWikiSelector wikiSelector = wikiContext.getWikiWeb().getDaoContext().getWikiSelector();
    if (wikiSelector == null || element == null) {
      return noForward();
    }

    if (wikiSelector instanceof GWikiMultipleWikiSelector == false) {
      return noForward();
    }

    GWikiMultipleWikiSelector multipleSelector = (GWikiMultipleWikiSelector) wikiSelector;
    String currentTenant = multipleSelector.getTenantId(GWikiServlet.INSTANCE, wikiContext.getRequest());

    // ensures branchfileststats present
    PlcUtils.ensureBranchMetaFiles(currentTenant, multipleSelector, wikiContext);

    final GWikiElement fileStats = wikiContext.getWikiWeb().findElement(PlcConstants.FILE_STATS_LOCATION);
    synchronized (fileStats.getElementInfo()) {
      GWikiArtefakt< ? > artefakt = fileStats.getMainPart();
      if (artefakt instanceof GWikiBranchFileStatsArtefakt == false) {
        return noForward();
      }

      GWikiBranchFileStatsArtefakt fileStatsArtefakt = (GWikiBranchFileStatsArtefakt) artefakt;
      BranchFileStats fileStatsContent = fileStatsArtefakt.getCompiledObject();

      String currentUserName = wikiContext.getWikiWeb().getAuthorization().getCurrentUserName(wikiContext);

      FileStatsDO newFileStat = new FileStatsDO();
      newFileStat.setPageId(element.getElementInfo().getId());
      newFileStat.setFileState(FileState.DRAFT);
      newFileStat.setCreatedAt(GWikiProps.formatTimeStamp(new Date()));
      newFileStat.setCreatedBy(currentUserName);
      newFileStat.setAssignedTo(currentUserName); // initial assigned to creator
      newFileStat.setStartAt(getStartAt());
      newFileStat.setEndAt(getEndAt());
      newFileStat.setOperators(new HashSet<String>(Arrays.asList(currentUserName)));
      fileStatsContent.addFileStats(newFileStat);

      fileStatsArtefakt.setStorageData(fileStatsContent.toString());

      wikiContext.getWikiWeb().getAuthorization().runAsSu(wikiContext, new CallableX<Void, RuntimeException>() {
        public Void call() throws RuntimeException
        {
          wikiContext.getWikiWeb().saveElement(wikiContext, fileStats, false);
          return null;
        }
      });
    }
    return null;
  }

  /**
   * Is this step visible in wizard 
   * @return
   */
  public boolean onIsVisible()
  {
    GWikiWikiSelector wikiSelector = wikiContext.getWikiWeb().getDaoContext().getWikiSelector();
    if (wikiSelector == null) {
      return false;
    }

    if (wikiSelector instanceof GWikiMultipleWikiSelector == false) {
      return false;
    }

    GWikiMultipleWikiSelector multipleSelector = (GWikiMultipleWikiSelector) wikiSelector;
    String currentTenant = multipleSelector.getTenantId(GWikiServlet.INSTANCE, wikiContext.getRequest());
    if (StringUtils.isBlank(currentTenant) == true) {
      return false;
    }
    return true;
  }

  /**
   * Formats the date to internat String repesentation
   * @param stringDate
   * @param hour
   * @param min
   * @param sec
   * @return
   */
  private String formatDate(String stringDate, int hour, int min, int sec)
  {
    // if release imeediately then store no timestamp
    if (isTmImmediately() == true) {
      return StringUtils.EMPTY;
    }
    try {
      Date date = internalTimestamp.get().parse(stringDate);
      Calendar c = Calendar.getInstance();
      c.setTime(date);
      c.set(Calendar.HOUR_OF_DAY, hour);
      c.set(Calendar.MINUTE, min);
      c.set(Calendar.SECOND, sec);

      Date dateTime = c.getTime();
      if (dateTime.before(new Date()) == true) {
        wikiContext.addValidationError("gwiki.page.articleWizard.error.date.past");
      }
      return GWikiProps.formatTimeStamp(dateTime);
    } catch (ParseException ex) {
      wikiContext.addValidationError("gwiki.page.articleWizard.error.date.invalid");
    }
    return StringUtils.EMPTY;
  }

  /**
   * @return
   */
  private String getEndAt()
  {
    return formatDate(tmToDate, tmToHour, tmToMin, 0);
  }

  /**
   * @return
   */
  private String getStartAt()
  {
    return formatDate(tmFromDate, tmFromHour, tmFromMin, 0);
  }

  public Object onValidate()
  {
    // check for correct date formats
    getStartAt();
    getEndAt();
    return null;
  }

  /**
   * @return the availableReviewers
   */
  public List<String> getTmAvailableReviewers()
  {
    return tmAvailableReviewers;
  }

  /**
   * @param availableReviewers the availableReviewers to set
   */
  public void setTmAvailableReviewers(List<String> availableReviewers)
  {
    this.tmAvailableReviewers = availableReviewers;
  }

  /**
   * @return the selectedReviewer
   */
  public String getTmSelectedReviewer()
  {
    return tmSelectedReviewer;
  }

  /**
   * @param selectedReviewer the selectedReviewer to set
   */
  public void setTmSelectedReviewer(String selectedReviewer)
  {
    this.tmSelectedReviewer = selectedReviewer;
  }

  /**
   * @return the immediately
   */
  public boolean isTmImmediately()
  {
    return tmImmediately;
  }

  /**
   * @param immediately the immediately to set
   */
  public void setTmImmediately(boolean immediately)
  {
    this.tmImmediately = immediately;
  }

  /**
   * @return the from
   */
  public Date getTmFrom()
  {
    return tmFrom;
  }

  /**
   * @param from the from to set
   */
  public void setTmFrom(Date from)
  {
    this.tmFrom = from;
  }

  /**
   * @return the fromDate
   */
  public String getTmFromDate()
  {
    return tmFromDate;
  }

  /**
   * @param fromDate the fromDate to set
   */
  public void setTmFromDate(String fromDate)
  {
    this.tmFromDate = fromDate;
  }

  /**
   * @return the fromHour
   */
  public int getTmFromHour()
  {
    return tmFromHour;
  }

  /**
   * @param fromHour the fromHour to set
   */
  public void setTmFromHour(int fromHour)
  {
    this.tmFromHour = fromHour;
  }

  /**
   * @return the fromMin
   */
  public int getTmFromMin()
  {
    return tmFromMin;
  }

  /**
   * @param fromMin the fromMin to set
   */
  public void setTmFromMin(int fromMin)
  {
    this.tmFromMin = fromMin;
  }

  /**
   * @return the to
   */
  public Date getTmTo()
  {
    return tmTo;
  }

  /**
   * @param to the to to set
   */
  public void setTmTo(Date to)
  {
    this.tmTo = to;
  }

  /**
   * @return the toDate
   */
  public String getTmToDate()
  {
    return tmToDate;
  }

  /**
   * @param toDate the toDate to set
   */
  public void setTmToDate(String toDate)
  {
    this.tmToDate = toDate;
  }

  /**
   * @return the toHour
   */
  public int getTmToHour()
  {
    return tmToHour;
  }

  /**
   * @param toHour the toHour to set
   */
  public void setTmToHour(int toHour)
  {
    this.tmToHour = toHour;
  }

  /**
   * @return the toMin
   */
  public int getTmToMin()
  {
    return tmToMin;
  }

  /**
   * @param toMin the toMin to set
   */
  public void setTmToMin(int toMin)
  {
    this.tmToMin = toMin;
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.micromata.genome.gwiki.page.impl.actionbean.ActionBeanBase#onInit()
   */
  @Override
  public Object onInit()
  {
    return null;
  }

  /**
   * @param element the element to set
   */
  public void setElement(GWikiElement element)
  {
    this.element = element;
  }

  /**
   * @return the element
   */
  public GWikiElement getElement()
  {
    return element;
  }
}
