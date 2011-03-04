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
import java.util.Set;

import org.apache.commons.lang.StringUtils;

import de.micromata.genome.gwiki.model.AuthorizationFailedException;
import de.micromata.genome.gwiki.model.GWikiArtefakt;
import de.micromata.genome.gwiki.model.GWikiElement;
import de.micromata.genome.gwiki.model.GWikiElementInfo;
import de.micromata.genome.gwiki.model.GWikiEmailProvider;
import de.micromata.genome.gwiki.model.GWikiLog;
import de.micromata.genome.gwiki.model.GWikiPropKeys;
import de.micromata.genome.gwiki.model.GWikiProps;
import de.micromata.genome.gwiki.model.GWikiPropsArtefakt;
import de.micromata.genome.gwiki.model.GWikiWeb;
import de.micromata.genome.gwiki.model.matcher.GWikiPageIdMatcher;
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
  private static final String EMPTY_SELECTION = "-1";

  /**
   * id of current selected page
   */
  private String pageId;

  /**
   * The current page instance
   */
  private GWikiElement page;
  
  /**
   * Depending objects of the element (e.g. attachments)
   */
  private HashMap<String, GWikiElement> depObjects;

  /**
   * Dependent objects the user want to consider in workflow step
   */
  private String[] selectedDepObjects;

  /**
   * Flag if comment is required. This will be validated
   */
  private boolean commentRequired;

  /**
   * Change comment 
   */
  private String comment;

  /**
   * new state of the page (see {@link FileState})
   */
  private String newPageState;

  /**
   * Popup mode for selecting a new assignee
   */
  private boolean assignMode;

  /**
   *  the new selected assignee (if <code>assignMode</code> was <code>true</code>)
   */
  private String selectedAssignee;

  /**
   * flag if users should get an email notification
   */
  private boolean sendMail;

  /**
   * current branch of the seleted file
   */
  private String branch = "";

  /**
   * Popup mode for selecting a new branch
   */
  private boolean branchMode;

  /**
   * the new selected branch (if <code>branchMode</code> was <code>true</code>)
   */
  private String selectedBranch;

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

  /**
   * Save Handler
   */
  public Object onSave()
  {
    init();
    performValidation();
    if (wikiContext.hasValidationErrors()) {
      return null;
    }
    applyComment();

    // if branch switch copy elements else simple save
    if (branchMode == true && StringUtils.equals(branch, selectedBranch) == false) {
      copyAndSave();
    } else {
      simpleSave();
    }
    sendStatusUpdateMail();
    return closeFancyBox(true);
  }

  public Object onCancel()
  {
    return closeFancyBox(false);
  }
  
  /**
   * loads the page from branch
   */
  private void init()
  {
    wikiContext.runInTenantContext(branch, getWikiSelector(), new CallableX<Void, RuntimeException>() {
      public Void call() throws RuntimeException
      {
        GWikiElement page = wikiContext.getWikiWeb().getElement(pageId);
        WorkflowPopupActionBean.this.page = page;

        // check if dependent objects exists and have the same state as the parent
        List<GWikiElementInfo> childs = wikiContext.getElementFinder().getAllDirectChilds(page.getElementInfo());
        for (final GWikiElementInfo child : childs) {
          GWikiElement fileStats = wikiContext.getWikiWeb().findElement(PlcConstants.FILE_STATS_LOCATION);
          if (fileStats == null || fileStats.getMainPart() == null) {
            return null;
          }

          GWikiArtefakt< ? > artefakt = fileStats.getMainPart();
          if (artefakt instanceof GWikiBranchFileStatsArtefakt == false) {
            return null;
          }

          GWikiBranchFileStatsArtefakt branchFilestatsArtefakt = (GWikiBranchFileStatsArtefakt) artefakt;
          BranchFileStats pageStats = branchFilestatsArtefakt.getCompiledObject();
          FileStatsDO fileStatsForPage = pageStats.getFileStatsForId(child.getId());
          if (fileStatsForPage != null && fileStatsForPage.getFileState() != FileState.valueOf(newPageState)) {
            getDepObjects().put(child.getId(), wikiContext.getWikiWeb().getElement(child));
          }
        }
        return null;
      }
    });
  }

  /**
   * Save without branch switch of article
   */
  private void simpleSave()
  {
    // updates the branchfile stats
    updateFileStats(FileState.valueOf(newPageState), selectedAssignee, branch, getAllPageIdsForUpdate());
    
    // save change comment to page 
    wikiContext.runInTenantContext(branch, getWikiSelector(), new CallableX<Void, RuntimeException>() {
      public Void call() throws RuntimeException
      {
         wikiContext.getWikiWeb().saveElement(wikiContext, page, false);
        return null;
      }
    });
  }

  /**
   * Copies the article and all its meta information from current branch to new branch
   */
  private void copyAndSave()
  {
    List<String> updatePages = getAllPageIdsForUpdate();
    
    // copy current filestats entry to source target branch
    copyAndSaveFileStats(branch, selectedBranch, updatePages);

    // add new page information to filestats of new branch
    updateFileStats(FileState.valueOf(newPageState), selectedAssignee, selectedBranch, updatePages);

    // save page(s) in new branch
    wikiContext.runInTenantContext(selectedBranch, getWikiSelector(), new CallableX<Void, RuntimeException>() {
      public Void call() throws RuntimeException
      {
        for (GWikiElement pageToUpdate : getAllPagesForUpdate()) {
          wikiContext.getWikiWeb().saveElement(wikiContext, pageToUpdate, true);
        }
        return null;
      }
    });

    // remove page(s) in old branch
    wikiContext.runInTenantContext(branch, getWikiSelector(), new CallableX<Void, RuntimeException>() {
      public Void call() throws RuntimeException
      {
        for (GWikiElement pageToUpdate : getAllPagesForUpdate()) {
          wikiContext.getWikiWeb().removeWikiPage(wikiContext, pageToUpdate);
        }
        return null;
      }
    });
  }

  /**
   * Validates user input
   */
  private void performValidation()
  {
    if (page == null) {
      wikiContext.addValidationError("gwiki.plc.dashboard.popup.error.pagenotfound");
    }
    if (assignMode == true && EMPTY_SELECTION.equals(selectedAssignee) == true) {
      wikiContext.addValidationError("gwiki.plc.dashboard.popup.error.noreviewer");
    }
    if (branchMode == true && StringUtils.isBlank(selectedBranch) == true) {
      wikiContext.addValidationError("gwiki.plc.dashboard.popup.error.nobranch");
    }
    if (commentRequired == true && StringUtils.isBlank(comment) == true) {
      wikiContext.addValidationError("gwiki.plc.dashboard.popup.error.nocomment");
    }
  }

  /**
   * Copies the filestats for a page from a source branch to a destination branch
   * 
   * @param sourceTenant
   * @param targetTenant
   * @param pageId
   */
  private void copyAndSaveFileStats(final String sourceTenant, final String targetTenant, final List<String> pageIds)
  {
    // copy file state meta information from source...
    final List<FileStatsDO> soureFileStats = wikiContext.runInTenantContext(sourceTenant, getWikiSelector(),
        new CallableX<List<FileStatsDO>, RuntimeException>() {
          public List<FileStatsDO> call() throws RuntimeException
          {
            GWikiElement fileStats = wikiContext.getWikiWeb().findElement(PlcConstants.FILE_STATS_LOCATION);
            if (fileStats == null || fileStats.getMainPart() == null) {
              return null;
            }

            GWikiArtefakt< ? > artefakt = fileStats.getMainPart();
            if (artefakt instanceof GWikiBranchFileStatsArtefakt == false) {
              return null;
            }

            GWikiBranchFileStatsArtefakt branchFilestatsArtefakt = (GWikiBranchFileStatsArtefakt) artefakt;
            BranchFileStats pageStats = branchFilestatsArtefakt.getCompiledObject();
            List<FileStatsDO> fileStatsForPage = pageStats.getFileStatsForIds(pageIds);
            return fileStatsForPage;
          }
        });
    if (soureFileStats == null || soureFileStats.isEmpty() == true) {
      return;
    }

    // .. to destination
    wikiContext.runInTenantContext(targetTenant, getWikiSelector(), new CallableX<Void, RuntimeException>() {
      public Void call() throws RuntimeException
      {
        final GWikiElement fileStats = wikiContext.getWikiWeb().findElement(PlcConstants.FILE_STATS_LOCATION);
        if (fileStats == null || fileStats.getMainPart() == null) {
          return null;
        }
        GWikiArtefakt< ? > artefakt = fileStats.getMainPart();
        if (artefakt instanceof GWikiBranchFileStatsArtefakt == false) {
          return null;
        }

        GWikiBranchFileStatsArtefakt branchFilestatsArtefakt = (GWikiBranchFileStatsArtefakt) artefakt;
        BranchFileStats pageStats = branchFilestatsArtefakt.getCompiledObject();
        for (FileStatsDO source : soureFileStats) {
          pageStats.addFileStats(source);
        }
        branchFilestatsArtefakt.setStorageData(pageStats.toString());

        // because filestats is located in /admin folder you need to be su to store/update that file
        wikiContext.getWikiWeb().getAuthorization().runAsSu(wikiContext, new CallableX<Void, RuntimeException>() {
          public Void call() throws RuntimeException
          {
            wikiContext.getWikiWeb().saveElement(wikiContext, fileStats, true);
            return null;
          }});
        return null;
      }
    });
  }

  /**
   * Updates current page statuses in central filestats element in specified branch
   * 
   * @param branchId
   */
  private void updateFileStats(final FileState fileState, final String newAssignee, final String branchId, final List<String> pageIds)
  {
    wikiContext.runInTenantContext(branchId, getWikiSelector(), new CallableX<Void, RuntimeException>() {
      public Void call() throws RuntimeException
      {
        final GWikiElement fileStats = wikiContext.getWikiWeb().findElement(PlcConstants.FILE_STATS_LOCATION);
        if (fileStats == null || fileStats.getMainPart() == null) {
          return null;
        }

        GWikiArtefakt< ? > artefakt = fileStats.getMainPart();
        if (artefakt instanceof GWikiBranchFileStatsArtefakt == false) {
          return null;
        }
        GWikiBranchFileStatsArtefakt branchFilestatsArtefakt = (GWikiBranchFileStatsArtefakt) artefakt;
        BranchFileStats pageStats = branchFilestatsArtefakt.getCompiledObject();
        
        // update meta data of each page
        for (final String editPageId : pageIds) {
          FileStatsDO fileStatsForPage = pageStats.getFileStatsForId(editPageId);
          if (fileStatsForPage == null) {
            continue;
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
        }
        branchFilestatsArtefakt.setStorageData(pageStats.toString());
        
        // because filestats is located in /admin folder you need to be su to store/update that file
        wikiContext.getWikiWeb().getAuthorization().runAsSu(wikiContext, new CallableX<Void, RuntimeException>() {
          public Void call() throws RuntimeException
          {
            wikiContext.getWikiWeb().saveElement(wikiContext, fileStats, true);
            return null;
          }});
        
        return null;
      }
    });
  }

  /**
   * Send an info mail to all operators who were involced in workflow process of current edited page
   */
  private void sendStatusUpdateMail()
  {
    if (sendMail == false) {
      return;
    }

    // TODO stefan internationalisierung der Mail. In den eingestellten Sprachen der Empfänger??
    StringBuilder body = new StringBuilder();
    body.append("The state of page ").append(this.getPageTitle()).append(" (").append(wikiContext.globalUrl(pageId))
        .append(") was changed by ") //
        .append(wikiContext.getWikiWeb().getAuthorization().getCurrentUserName(wikiContext));

    if (StringUtils.isNotBlank(newPageState) == true) {
      body.append(" to new state ").append(newPageState);
    }

    if (StringUtils.isNotBlank(comment) == true) {
      body.append("\n\nComment:\n").append(comment);
    }

    final Map<String, String> ctx = new HashMap<String, String>();
    ctx.put(GWikiEmailProvider.FROM, wikiContext.getWikiWeb().getWikiConfig().getSendEmail());
    ctx.put(GWikiEmailProvider.SUBJECT, "The workflow state of the page was changed.");
    ctx.put(GWikiEmailProvider.TEXT, body.toString());
    ctx.put(GWikiEmailProvider.MAILTEMPLATE, "edit/pagelifecycle/mailtemplates/StatusUpdateMailTemplate");
    String receipients = getRecipients();
    ctx.put(GWikiEmailProvider.TO, "s.stuetzer@micromata.com");

    GWikiLog.note("Sent status update mail", "Recipient", receipients);
    wikiContext.getWikiWeb().getDaoContext().getEmailProvider().sendEmail(ctx);
  }

  /**
   * Closes the fancy box
   * 
   * @param reloadParent if <code>true</code> the page where the fancybox were opened will be reloaded after closing (e.g. for loading
   *          chnages made in the fancybox)
   */
  private Object closeFancyBox(final boolean reloadParent)
  {
    StringBuffer sb = new StringBuffer("<script type='text/javascript'>");
    sb.append("parent.$.fancybox.close();");
    if (reloadParent == true) {
      sb.append("window.parent.location.reload();");
    }
    sb.append("</script>");
    wikiContext.append(sb.toString());
    wikiContext.flush();
    return noForward();
  }

  /**
   * @return
   */
  private String getRecipients()
  {
    return wikiContext.runInTenantContext(branch, getWikiSelector(), new CallableX<String, RuntimeException>() {

      public String call() throws RuntimeException
      {
        GWikiElement filestats = wikiContext.getWikiWeb().findElement(PlcConstants.FILE_STATS_LOCATION);
        if (filestats == null) {
          return StringUtils.EMPTY;
        }
        GWikiArtefakt< ? > artefakt = filestats.getMainPart();
        if (artefakt instanceof GWikiBranchFileStatsArtefakt == false) {
          return StringUtils.EMPTY;
        }

        GWikiBranchFileStatsArtefakt fileStatsArtefakt = (GWikiBranchFileStatsArtefakt) artefakt;
        BranchFileStats stats = fileStatsArtefakt.getCompiledObject();
        FileStatsDO statsForId = stats.getFileStatsForId(pageId);
        if (statsForId == null) {
          return StringUtils.EMPTY;
        }

        final List<String> mailAdresses = new ArrayList<String>();
        Set<String> operators = statsForId.getOperators();
        for (final String operator : operators) {
          try {
            wikiContext.getWikiWeb().getAuthorization().runAsUser(operator, wikiContext, new CallableX<Void, RuntimeException>() {
              public Void call() throws RuntimeException
              {
                String email = wikiContext.getWikiWeb().getAuthorization().getCurrentUserEmail(wikiContext);
                if (StringUtils.isEmpty(email) == true) {
                  return null;
                }
                mailAdresses.add(email);
                return null;
              }
            });
          } catch (AuthorizationFailedException ex) {
            GWikiLog.warn("Cannot determine email for user: " + operator, ex);
          }
        }

        return StringUtils.join(mailAdresses, ",");
      }
    });
  }

  /**
   * saves the new comment for the page
   */
  private void applyComment()
  {
    Map<String, GWikiArtefakt< ? >> map = new HashMap<String, GWikiArtefakt< ? >>();
    page.collectParts(map);
    GWikiArtefakt< ? > artefakt = map.get("ChangeComment");
    if (artefakt instanceof GWikiChangeCommentArtefakt == false) {
      return;
    }

    GWikiChangeCommentArtefakt commentArtefakt = (GWikiChangeCommentArtefakt) artefakt;
    String oldText = commentArtefakt.getStorageData();
    String ntext;
    String uname = wikiContext.getWikiWeb().getAuthorization().getCurrentUserName(wikiContext);
    int prevVersion = page.getElementInfo().getProps().getIntValue(GWikiPropKeys.VERSION, 0);
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
   * Returns all page ids (current editied page and all selected dependent pages) that will be changes in this workflow step
   */
  private List<String> getAllPageIdsForUpdate() {
    List<String> pageIds = new ArrayList<String>();
    pageIds.add(pageId);
    if (selectedDepObjects == null) {
      return pageIds;
    }
    
    for (String depPage : selectedDepObjects) {
      pageIds.add(depPage);
    }
    return pageIds;
  }

  private List<GWikiElement> getAllPagesForUpdate() {
    List<GWikiElement> pages = new ArrayList<GWikiElement>();
    pages.add(page);
    if (selectedDepObjects == null) {
      return pages;
    }
    
    for (String depPage : selectedDepObjects) {
      if (depObjects.containsKey(depPage) == true) {
        pages.add(depObjects.get(depPage));
      }
    }
    return pages;
  }
  
  /**
   * returns the pagetitle of the current edited page
   */
  public String getPageTitle()
  {
    return page == null ? StringUtils.EMPTY : page.getElementInfo().getTitle();
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

  /**
   * @param commentRequired the commentRequired to set
   */
  public void setCommentRequired(boolean commentRequired)
  {
    this.commentRequired = commentRequired;
  }

  /**
   * @return the commentRequired
   */
  public boolean isCommentRequired()
  {
    return commentRequired;
  }

  /**
   * @param sendMail the sendMail to set
   */
  public void setSendMail(boolean sendMail)
  {
    this.sendMail = sendMail;
  }

  /**
   * @return the sendMail
   */
  public boolean isSendMail()
  {
    return sendMail;
  }

  /**
   * @return the depObjects
   */
  public Map<String, GWikiElement> getDepObjects()
  {
    if (depObjects == null) {
      depObjects = new HashMap<String, GWikiElement>();
    }
    return depObjects;
  }

  /**
   * @param addPages the addPages to set
   */
  public void setSelectedDepObjects(String[] addPages)
  {
    this.selectedDepObjects = addPages;
  }

  /**
   * @return the addPages
   */
  public String[] getSelectedDepObjects()
  {
    return selectedDepObjects;
  }
}