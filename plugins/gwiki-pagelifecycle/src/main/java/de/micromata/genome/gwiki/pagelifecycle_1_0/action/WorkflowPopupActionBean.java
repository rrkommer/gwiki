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

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import de.micromata.genome.gwiki.model.GWikiArtefakt;
import de.micromata.genome.gwiki.model.GWikiElement;
import de.micromata.genome.gwiki.model.GWikiElementInfo;
import de.micromata.genome.gwiki.model.GWikiEmailProvider;
import de.micromata.genome.gwiki.model.GWikiPropKeys;
import de.micromata.genome.gwiki.model.GWikiProps;
import de.micromata.genome.gwiki.model.GWikiPropsArtefakt;
import de.micromata.genome.gwiki.model.GWikiWeb;
import de.micromata.genome.gwiki.model.matcher.GWikiPageIdMatcher;
import de.micromata.genome.gwiki.model.mpt.GWikiMultipleWikiSelector;
import de.micromata.genome.gwiki.page.GWikiContext;
import de.micromata.genome.gwiki.page.impl.GWikiChangeCommentArtefakt;
import de.micromata.genome.gwiki.pagelifecycle_1_0.artefakt.BranchFileStats;
import de.micromata.genome.gwiki.pagelifecycle_1_0.artefakt.FileStatsDO;
import de.micromata.genome.gwiki.pagelifecycle_1_0.artefakt.GWikiBranchFileStatsArtefakt;
import de.micromata.genome.gwiki.pagelifecycle_1_0.model.BranchState;
import de.micromata.genome.gwiki.pagelifecycle_1_0.model.FileState;
import de.micromata.genome.gwiki.pagelifecycle_1_0.model.PlcConstants;
import de.micromata.genome.util.matcher.BooleanListRulesFactory;
import de.micromata.genome.util.matcher.Matcher;
import de.micromata.genome.util.runtime.CallableX;

/**
 * ActionBean for Dashboard Popups offering workflow functionality
 * 
 * @author Stefan Stuetzer (s.stuetzer@micromata.com)
 */
public class WorkflowPopupActionBean extends PlcActionBeanBase
{
  private String pageId;

  private String comment;

  private String newPageState;

  private boolean assignMode;

  private String selectedAssignee;

  private boolean branchMode;

  private String branch = "";

  private String selectedBranch;

  private GWikiChangeCommentArtefakt commentArtefakt;

  private GWikiElement element;

  /*
   * (non-Javadoc)
   * 
   * @see de.micromata.genome.gwiki.page.impl.actionbean.ActionBeanBase#onInit()
   */
  @Override
  public Object onInit()
  {
    init();
    return null;
  }

  public Object onSave()
  {
    init();
    saveComment();
    if (wikiContext.hasValidationErrors()) {
      return null;
    }

    updateFileStats(FileState.valueOf(newPageState), selectedAssignee);

    wikiContext.runInTenantContext(branch, getWikiSelector(), new CallableX<Void, RuntimeException>() {
      public Void call() throws RuntimeException
      {
        wikiContext.getWikiWeb().saveElement(wikiContext, element, false);
        return null;
      }
    });

    sendStatusUpdateMail();
    
    wikiContext.append("<script type='text/javascript'>parent.$.fancybox.close();window.parent.location.reload();</script>");
    wikiContext.flush();
    return noForward();
  }

  public Object onCancel()
  {
    wikiContext.append("<script type='text/javascript'>parent.$.fancybox.close();</script>");
    wikiContext.flush();
    return noForward();
  }

  /**
   * loads the page
   */
  private void init()
  {
    wikiContext.runInTenantContext(branch, getWikiSelector(), new CallableX<Void, RuntimeException>() {
      public Void call() throws RuntimeException
      {
        GWikiElement page = wikiContext.getWikiWeb().getElement(pageId);
        if (page == null) {
          return null;
        }
        Map<String, GWikiArtefakt< ? >> map = new HashMap<String, GWikiArtefakt< ? >>();
        page.collectParts(map);
        element = page;

        GWikiArtefakt< ? > artefakt = map.get("ChangeComment");
        if (artefakt instanceof GWikiChangeCommentArtefakt == true) {
          commentArtefakt = (GWikiChangeCommentArtefakt) artefakt;
        }
        return null;
      }
    });
  }

  /**
   * Updates current page statuses in central filestats element
   */
  private void updateFileStats(final FileState fileState, final String newAssignee)
  {

    wikiContext.runInTenantContext(branch, getWikiSelector(), new CallableX<Void, RuntimeException>() {
      public Void call() throws RuntimeException
      {
        GWikiElement fileStats = wikiContext.getWikiWeb().findElement(PlcConstants.FILE_STATS_LOCATION);
        if (fileStats == null || fileStats.getMainPart() == null) {
          wikiContext.addValidationError("gwiki.page.ViewBranchContent.error.setstate");
          return null;
        }

        GWikiArtefakt< ? > artefakt = fileStats.getMainPart();
        if (artefakt instanceof GWikiBranchFileStatsArtefakt == false) {
          wikiContext.addValidationError("gwiki.page.ViewBranchContent.error.setstate");
          return null;
        }

        GWikiBranchFileStatsArtefakt branchFilestatsArtefakt = (GWikiBranchFileStatsArtefakt) artefakt;
        BranchFileStats pageStats = branchFilestatsArtefakt.getCompiledObject();
        FileStatsDO fileStatsForPage = pageStats.getFileStatsForId(pageId);
        if (fileStatsForPage == null) {
          wikiContext.addValidationError("gwiki.page.ViewBranchContent.error.setstate");
          return null;
        }

        // set new file state
        fileStatsForPage.setFileState(fileState);
        fileStatsForPage.setLastModifiedAt(GWikiProps.formatTimeStamp(new Date()));
        fileStatsForPage.setLastModifiedBy(wikiContext.getWikiWeb().getAuthorization().getCurrentUserName(wikiContext));

        // set new assignee
        if (StringUtils.isNotBlank(newAssignee) == true) {
          fileStatsForPage.getOperators().add(newAssignee);
          fileStatsForPage.setAssignedTo(newAssignee);
        }

        branchFilestatsArtefakt.setStorageData(pageStats.toString());
        wikiContext.getWikiWeb().saveElement(wikiContext, fileStats, true);

        return null;
      }
    });
  }

  /**
   * Send an info mail to all operators who were involced in wiorkflow process of current edited page
   */
  private void sendStatusUpdateMail()
  {
    final Map<String, String> ctx = new HashMap<String, String>();
    ctx.put(GWikiEmailProvider.FROM, wikiContext.getWikiWeb().getWikiConfig().getSendEmail());
    ctx.put(GWikiEmailProvider.SUBJECT, "Statusupdate: ");
    String body = "The status of the page was changed";
    ctx.put(GWikiEmailProvider.TEXT, body);
    ctx.put(GWikiEmailProvider.MAILTEMPLATE, "edit/pagelifecycle/mailtemplates/StatusUpdateMailTemplate");
    ctx.put(GWikiEmailProvider.TO, "s.stuetzer@micromata.com");

    wikiContext.getWikiWeb().getDaoContext().getEmailProvider().sendEmail(ctx);
  }

  /**
   * saves the new comment for the page
   */
  private void saveComment()
  {
    String oldText = commentArtefakt.getStorageData();
    String ntext;
    String uname = wikiContext.getWikiWeb().getAuthorization().getCurrentUserName(wikiContext);
    int prevVersion = element.getElementInfo().getProps().getIntValue(GWikiPropKeys.VERSION, 0);
    if (StringUtils.isNotBlank(comment) == true) {
      Date now = new Date();
      ntext = "{changecomment:modifiedBy="
          + uname
          + "|modifiedAt="
          + GWikiProps.date2string(now)
          + "|version="
          + (prevVersion + 1)
          + "}\n"
          + comment
          + "\n{changecomment}\n"
          + StringUtils.defaultString(oldText);
    } else {
      ntext = oldText;
    }
    ntext = StringUtils.trimToNull(ntext);
    commentArtefakt.setStorageData(ntext);
  }

  /**
   * returns a list of all available (offline) branches for assign an article to
   */
  public List<GWikiProps> getAvailableBranches()
  {
    List<String> allTenants = getWikiSelector().getMptIdSelector().getTenants(GWikiWeb.getRootWiki());
    List<GWikiProps> branchProps = new ArrayList<GWikiProps>();

    for (String tenantId : allTenants) {
      if (PlcConstants.DRAFT_ID.equalsIgnoreCase(tenantId) == true) {
        continue;
      }

      GWikiProps branchInfoProp = wikiContext.runInTenantContext(tenantId, getWikiSelector(),
          new CallableX<GWikiProps, RuntimeException>() {
            public GWikiProps call() throws RuntimeException
            {
              GWikiElement branchInfoElement = wikiContext.getWikiWeb().findElement(PlcConstants.BRANCH_INFO_LOCATION);
              if (branchInfoElement == null) {
                return null;
              }

              GWikiArtefakt< ? > artefakt = branchInfoElement.getMainPart();
              if (artefakt instanceof GWikiPropsArtefakt == false) {
                return null;
              }

              GWikiPropsArtefakt propsArtefakt = (GWikiPropsArtefakt) artefakt;
              return propsArtefakt.getCompiledObject();
            }
          });

      if (branchInfoProp == null) {
        continue;
      }

      // only add offline branches
      if (BranchState.OFFLINE.name().equals(branchInfoProp.getStringValue("BRANCH_STATE")) == true) {
        branchProps.add(branchInfoProp);
      }
    }
    return branchProps;
  }

  /**
   * Returns a list of all possible new assignees
   */
  public List<String> getAvailableAssignees()
  {
    // TODO stefan an Rollen knüpfen
    final Matcher<String> m = new BooleanListRulesFactory<String>().createMatcher("admin/user/*");
    final List<GWikiElementInfo> userInfos = wikiContext.getElementFinder().getPageInfos(new GWikiPageIdMatcher(wikiContext, m));

    List<String> users = new ArrayList<String>();

    for (final GWikiElementInfo user : userInfos) {
      users.add(GWikiContext.getNamePartFromPageId(user.getId()));
    }
    return users;
  }

  /**
   * returns the pagetitle of the current edited page
   */
  public String getPageTitle()
  {
    return wikiContext.runInTenantContext(branch, getWikiSelector(), new CallableX<String, RuntimeException>() {
      public String call() throws RuntimeException
      {
        GWikiElement page = wikiContext.getWikiWeb().getElement(pageId);
        return page == null ? StringUtils.EMPTY : page.getElementInfo().getTitle();
      }
    });
  }

  /**
   * @param branchMode the branchMode to set
   */
  public void setBranchMode(boolean branchMode)
  {
    this.branchMode = branchMode;
  }

  /**
   * @return the branchMode
   */
  public boolean isBranchMode()
  {
    return branchMode;
  }

  /**
   * @param pageId the pageId to set
   */
  public void setPageId(String pageId)
  {
    this.pageId = pageId;
  }

  /**
   * @return the pageId
   */
  public String getPageId()
  {
    return pageId;
  }

  /**
   * @param comment the comment to set
   */
  public void setComment(String comment)
  {
    this.comment = comment;
  }

  /**
   * @return the comment
   */
  public String getComment()
  {
    return comment;
  }

  /**
   * @param branch the branch to set
   */
  public void setBranch(String branch)
  {
    this.branch = branch;
  }

  /**
   * @return the branch
   */
  public String getBranch()
  {
    return branch;
  }

  /**
   * @param selectedBranch the selectedBranch to set
   */
  public void setSelectedBranch(String selectedBranch)
  {
    this.selectedBranch = selectedBranch;
  }

  /**
   * @return the selectedBranch
   */
  public String getSelectedBranch()
  {
    return selectedBranch;
  }

  /**
   * @param assignMode the assignMode to set
   */
  public void setAssignMode(boolean assignMode)
  {
    this.assignMode = assignMode;
  }

  /**
   * @return the assignMode
   */
  public boolean isAssignMode()
  {
    return assignMode;
  }

  /**
   * @param selectedAssignee the selectedAssignee to set
   */
  public void setSelectedAssignee(String selectedAssignee)
  {
    this.selectedAssignee = selectedAssignee;
  }

  /**
   * @return the selectedAssignee
   */
  public String getSelectedAssignee()
  {
    return selectedAssignee;
  }

  /**
   * @param action the action to set
   */
  public void setNewPageState(String action)
  {
    this.newPageState = action;
  }

  /**
   * @return the action
   */
  public String getNewPageState()
  {
    return newPageState;
  }
}
