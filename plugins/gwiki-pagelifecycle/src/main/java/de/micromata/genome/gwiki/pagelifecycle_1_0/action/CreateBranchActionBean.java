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
package de.micromata.genome.gwiki.pagelifecycle_1_0.action;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.commons.lang.StringUtils;

import de.micromata.genome.gwiki.model.GWikiArtefakt;
import de.micromata.genome.gwiki.model.GWikiElement;
import de.micromata.genome.gwiki.model.GWikiProps;
import de.micromata.genome.gwiki.model.GWikiPropsArtefakt;
import de.micromata.genome.gwiki.model.GWikiWebUtils;
import de.micromata.genome.gwiki.model.GWikiWikiSelector;
import de.micromata.genome.gwiki.model.mpt.GWikiMultipleWikiSelector;
import de.micromata.genome.gwiki.page.impl.actionbean.ActionBeanBase;
import de.micromata.genome.gwiki.pagelifecycle_1_0.model.BranchState;

/**
 * Actionbean for creating new branches (content-releases)
 * 
 * @author Stefan Stuetzer (s.stuetzer@micromata.com)
 */
public class CreateBranchActionBean extends ActionBeanBase
{
  private static final String INFO_TEMPLATE_ID = "admin/templates/GWikiBranchInfoElementTemplate";

  private static final String FILESTATS_TEMPLATE_ID = "admin/templates/GWikiBranchFileStatsTemplate";

  private String branchId;

  private String description;

  private String releaseStartDate;

  private String releaseEndDate;

  public Object onCreateBranch()
  {
    if (isBranchIdValid() == false) {
      return null;
    }
    if (isReleaseDateValid() == false) {
      return null;
    }

    GWikiWikiSelector wikiSelector = wikiContext.getWikiWeb().getDaoContext().getWikiSelector();
    if (wikiSelector instanceof GWikiMultipleWikiSelector == false) {
      wikiContext.addSimpleValidationError("No multiple branches supported.");
      return null;
    }

    // create or enter new branch
    wikiSelector.enterTenant(wikiContext, this.branchId);

    createBranchInfoElement();
    createBranchFileStats();

    // leave branch
    wikiSelector.leaveTenant(wikiContext);

    return wikiContext.getWikiWeb().getHomeElement(wikiContext);
  }

  private void createBranchFileStats()
  {
    GWikiElement el = GWikiWebUtils.createNewElement(wikiContext, "admin/branch/BranchFileStats", FILESTATS_TEMPLATE_ID,
        "Branch File Stats");
    wikiContext.getWikiWeb().saveElement(wikiContext, el, false);
  }

  /**
   * Creates initial the branch info element dolding branch meta data
   */
  private void createBranchInfoElement()
  {
    GWikiElement el = GWikiWebUtils.createNewElement(wikiContext, "admin/branch/BranchInfoElement", INFO_TEMPLATE_ID, "BranchInfo");
    GWikiArtefakt< ? > artefakt = el.getMainPart();

    GWikiPropsArtefakt art = (GWikiPropsArtefakt) artefakt;
    GWikiProps props = art.getCompiledObject();
    props.setStringValue("BRANCH_ID", this.branchId);
    props.setStringValue("DESCRIPTION", this.description);
    props.setStringValue("BRANCH_STATE", BranchState.OFFLINE.name());
    props.setStringValue("RELEASE_DATE", getDateString(this.releaseStartDate));
    props.setStringValue("RELEASE_END_DATE", getDateString(this.releaseEndDate));

    wikiContext.getWikiWeb().saveElement(wikiContext, el, false);
  }

  private boolean isBranchIdValid()
  {
    if (StringUtils.isBlank(this.branchId) == true) {
      wikiContext.addSimpleValidationError("branch id not valid. It must have at least one character");
      return false;
    }
    if (StringUtils.containsAny(this.branchId, new char[] { '/', ',', '*', '#', '"', '\'', ' '})) {
      wikiContext.addSimpleValidationError("Branch-Id not valid. It contains invalid chracters.");
      return false;
    }
    if (this.branchId.length() > 50) {
      wikiContext.addSimpleValidationError("Branch-Id not valid. There are too many characters.");
      return false;
    }
    return true;
  }

  private boolean isReleaseDateValid()
  {
    // no dates given -> true
    if (StringUtils.isBlank(this.releaseStartDate) == true && StringUtils.isBlank(this.releaseEndDate) == true) {
      return true;
    }

    try {
      // try to parse date strings
      SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
      if (StringUtils.isNotBlank(this.releaseStartDate) == true) {
        sdf.parse(this.releaseStartDate);
      }
      if (StringUtils.isNotBlank(this.releaseEndDate) == true) {
        sdf.parse(this.releaseEndDate);
      }
      return true;
    } catch (ParseException ex) {
      wikiContext.addSimpleValidationError("Release date(s) not valid");

      return false;
    }
  }

  /**
   * @param releaseStartDate2
   * @return
   */
  private String getDateString(String date)
  {
    if (StringUtils.isBlank(date) == true) {
      return StringUtils.EMPTY;
    }

    try {
      SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
      Date parsedDate = sdf.parse(date);
      return GWikiProps.formatTimeStamp(parsedDate);
    } catch (ParseException ex) {
      wikiContext.addSimpleValidationError("Could not parse release dates.");
    }
    return null;
  }

  /**
   * @param branchId the branchId to set
   */
  public void setBranchId(String branchId)
  {
    this.branchId = branchId;
  }

  /**
   * @return the branchId
   */
  public String getBranchId()
  {
    return branchId;
  }

  /**
   * @param description the description to set
   */
  public void setDescription(String description)
  {
    this.description = description;
  }

  /**
   * @return the description
   */
  public String getDescription()
  {
    return description;
  }

  /**
   * @param releaseStartDate the releaseStartDate to set
   */
  public void setReleaseStartDate(String releaseStartDate)
  {
    this.releaseStartDate = releaseStartDate;
  }

  /**
   * @return the releaseStartDate
   */
  public String getReleaseStartDate()
  {
    return releaseStartDate;
  }

  /**
   * @param releaseEndDate the releaseEndDate to set
   */
  public void setReleaseEndDate(String releaseEndDate)
  {
    this.releaseEndDate = releaseEndDate;
  }

  /**
   * @return the releaseEndDate
   */
  public String getReleaseEndDate()
  {
    return releaseEndDate;
  }

}