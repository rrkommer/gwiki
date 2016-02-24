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
package de.micromata.genome.gwiki.pagelifecycle_1_0.action;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import de.micromata.genome.gwiki.model.AuthorizationFailedException;
import de.micromata.genome.gwiki.model.GWikiArtefakt;
import de.micromata.genome.gwiki.model.GWikiElement;
import de.micromata.genome.gwiki.model.GWikiElementInfo;
import de.micromata.genome.gwiki.model.GWikiEmailProvider;
import de.micromata.genome.gwiki.model.GWikiI18nProvider;
import de.micromata.genome.gwiki.model.GWikiPropKeys;
import de.micromata.genome.gwiki.model.GWikiProps;
import de.micromata.genome.gwiki.model.GWikiWeb;
import de.micromata.genome.gwiki.model.logging.GWikiLog;
import de.micromata.genome.gwiki.model.logging.GWikiLogAttributeType;
import de.micromata.genome.gwiki.model.logging.GWikiLogCategory;
import de.micromata.genome.gwiki.model.matcher.GWikiPageIdMatcher;
import de.micromata.genome.gwiki.page.GWikiContext;
import de.micromata.genome.gwiki.page.impl.GWikiChangeCommentArtefakt;
import de.micromata.genome.gwiki.pagelifecycle_1_0.artefakt.BranchFileStats;
import de.micromata.genome.gwiki.pagelifecycle_1_0.artefakt.FileStatsDO;
import de.micromata.genome.gwiki.pagelifecycle_1_0.artefakt.GWikiBranchFileStatsArtefakt;
import de.micromata.genome.gwiki.pagelifecycle_1_0.model.BranchState;
import de.micromata.genome.gwiki.pagelifecycle_1_0.model.FileState;
import de.micromata.genome.gwiki.pagelifecycle_1_0.model.PlcConstants;
import de.micromata.genome.gwiki.pagelifecycle_1_0.model.PlcUtils;
import de.micromata.genome.logging.GLog;
import de.micromata.genome.logging.GenomeAttributeType;
import de.micromata.genome.logging.LogAttribute;
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
  /** some global constants for possible user inputs from select boxes etc. */
  private static final String EMPTY_SELECTION = "-1";

  private static final String CURRENT_ASSIGNEE = "current";

  private static final String PREVIOUS_ASSIGNEE = "previous";

  private static final String NEW_BRANCH = "new";

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
  private List<GWikiElement> depObjects;

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
   * the new selected assignee (if <code>assignMode</code> was <code>true</code>)
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
    sendStatusUpdateMailToOperators();
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
    wikiContext.runInTenantContext(branch, getWikiSelector(), new CallableX<Void, RuntimeException>()
    {
      @Override
      public Void call() throws RuntimeException
      {
        GWikiElement page = wikiContext.getWikiWeb().getElement(pageId);
        WorkflowPopupActionBean.this.page = page;

        // check if dependent objects exists
        List<GWikiElementInfo> childs = wikiContext.getElementFinder().getAllDirectChilds(page.getElementInfo());
        for (final GWikiElementInfo child : childs) {
          getDepObjects().add(wikiContext.getWikiWeb().getElement(child));
          getSubpagesRec(child, 2);
        }
        return null;
      }

      /**
       * Recursive fetch of child elements
       */
      private void getSubpagesRec(GWikiElementInfo parent, int curDepth)
      {
        List<GWikiElementInfo> childs = wikiContext.getElementFinder().getAllDirectChilds(parent);
        for (final GWikiElementInfo child : childs) {
          getDepObjects().add(wikiContext.getWikiWeb().getElement(child));
          getSubpagesRec(child, ++curDepth);
        }
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
    wikiContext.runInTenantContext(branch, getWikiSelector(), new CallableX<Void, RuntimeException>()
    {
      @Override
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
    // create new branch
    if (NEW_BRANCH.equals(selectedBranch)) {
      createAndSetNewBranch();
    }
    List<String> updatePages = getAllPageIdsForUpdate();

    // copy current filestats entry to source target branch
    copyAndSaveFileStats(branch, selectedBranch, updatePages);

    // add new page information to filestats of new branch
    updateFileStats(FileState.valueOf(newPageState), selectedAssignee, selectedBranch, updatePages);

    // save page(s) in new branch
    wikiContext.runInTenantContext(selectedBranch, getWikiSelector(), new CallableX<Void, RuntimeException>()
    {
      @Override
      public Void call() throws RuntimeException
      {
        for (GWikiElement pageToUpdate : getAllPagesForUpdate()) {
          wikiContext.getWikiWeb().saveElement(wikiContext, pageToUpdate, true);
        }
        return null;
      }
    });

    // remove page(s) in old branch
    wikiContext.runInTenantContext(branch, getWikiSelector(), new CallableX<Void, RuntimeException>()
    {
      @Override
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
   * Creates a new a branch considering the meta information of currently transformed page
   */
  private void createAndSetNewBranch()
  {
    Date startDateOfArticle = getStartDateOfArticle();
    final String branchId = startDateOfArticle == null ? GWikiProps.formatTimeStamp(new Date()) : GWikiProps
        .formatTimeStamp(startDateOfArticle);

    wikiContext.runInTenantContext(branchId, getWikiSelector(), new CallableX<Void, RuntimeException>()
    {
      @Override
      public Void call() throws RuntimeException
      {
        final GWikiElement branchInfo = PlcUtils.createInfoElement(wikiContext, branchId, "", branchId, "");
        final GWikiElement branchFileStats = PlcUtils.createFileStats(wikiContext);
        wikiContext.getWikiWeb().getAuthorization().runAsSu(wikiContext, new CallableX<Void, RuntimeException>()
        {
          @Override
          public Void call() throws RuntimeException
          {
            wikiContext.getWikiWeb().saveElement(wikiContext, branchInfo, false);
            wikiContext.getWikiWeb().saveElement(wikiContext, branchFileStats, false);
            return null;
          }
        });
        GLog.note(GWikiLogCategory.Wiki, "Autocreate branch: " + branchId);
        return null;
      }
    });

    // set new created branch as selected
    selectedBranch = branchId;
  }

  /**
   * @return The startdate of the article, or empty string if no one is specified
   */
  public Date getStartDateOfArticle()
  {
    // lookup if article had specified a release date
    String startDate = wikiContext.runInTenantContext(branch, getWikiSelector(),
        new CallableX<String, RuntimeException>()
        {
          @Override
          public String call() throws RuntimeException
          {
            BranchFileStats branchFileStats = PlcUtils.getBranchFileStats(wikiContext);
            if (branchFileStats == null) {
              return null;
            }
            FileStatsDO fileStatsForPage = branchFileStats.getFileStatsForId(pageId);
            return fileStatsForPage.getStartAt();
          }
        });

    if (StringUtils.isNotBlank(startDate) == true) {
      return GWikiProps.parseTimeStamp(startDate);
    }

    return null;
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
        new CallableX<List<FileStatsDO>, RuntimeException>()
        {
          @Override
          public List<FileStatsDO> call() throws RuntimeException
          {
            BranchFileStats pageStats = PlcUtils.getBranchFileStats(wikiContext);
            List<FileStatsDO> fileStatsForPage = pageStats.getFileStatsForIds(pageIds);
            return fileStatsForPage;
          }
        });
    if (soureFileStats == null || soureFileStats.isEmpty() == true) {
      return;
    }

    // ...to destination
    wikiContext.runInTenantContext(targetTenant, getWikiSelector(), new CallableX<Void, RuntimeException>()
    {
      @Override
      public Void call() throws RuntimeException
      {
        final GWikiElement fileStats = wikiContext.getWikiWeb().findElement(PlcConstants.FILE_STATS_LOCATION);
        if (fileStats == null || fileStats.getMainPart() == null) {
          return null;
        }
        GWikiArtefakt<?> artefakt = fileStats.getMainPart();
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
        wikiContext.getWikiWeb().getAuthorization().runAsSu(wikiContext, new CallableX<Void, RuntimeException>()
        {
          @Override
          public Void call() throws RuntimeException
          {
            wikiContext.getWikiWeb().saveElement(wikiContext, fileStats, true);
            return null;
          }
        });

        GLog.note(GWikiLogCategory.Wiki,
            "Copied BranchFileStats information from " + sourceTenant + " to " + targetTenant,
            new LogAttribute(GWikiLogAttributeType.PageIds, StringUtils.join(pageIds, ",")));
        return null;
      }
    });
  }

  /**
   * Updates current page statuses in central filestats element in specified branch
   * 
   * @param branchId
   */
  private void updateFileStats(final FileState fileState, final String newAssignee, final String branchId,
      final List<String> pageIds)
  {
    wikiContext.runInTenantContext(branchId, getWikiSelector(), new CallableX<Void, RuntimeException>()
    {
      @Override
      public Void call() throws RuntimeException
      {
        final GWikiElement fileStats = wikiContext.getWikiWeb().findElement(PlcConstants.FILE_STATS_LOCATION);
        if (fileStats == null || fileStats.getMainPart() == null) {
          return null;
        }

        GWikiArtefakt<?> artefakt = fileStats.getMainPart();
        if (artefakt instanceof GWikiBranchFileStatsArtefakt == false) {
          return null;
        }
        GWikiBranchFileStatsArtefakt branchFilestatsArtefakt = (GWikiBranchFileStatsArtefakt) artefakt;
        BranchFileStats pageStats = branchFilestatsArtefakt.getCompiledObject();

        // update metadata of each page
        for (final String editPageId : pageIds) {
          FileStatsDO fileStatsForPage = pageStats.getFileStatsForId(editPageId);
          if (fileStatsForPage == null) {
            continue;
          }

          // set new file state
          fileStatsForPage.setFileState(fileState);
          fileStatsForPage.setLastModifiedAt(GWikiProps.formatTimeStamp(new Date()));
          fileStatsForPage
              .setLastModifiedBy(wikiContext.getWikiWeb().getAuthorization().getCurrentUserName(wikiContext));

          // set new assignee
          if (PREVIOUS_ASSIGNEE.equalsIgnoreCase(newAssignee) == true
              && StringUtils.isNotBlank(fileStatsForPage.getPreviousAssignee()) == true) {
            String currentAssignee = fileStatsForPage.getAssignedTo();
            fileStatsForPage.setAssignedTo(fileStatsForPage.getPreviousAssignee());
            fileStatsForPage.setPreviousAssignee(currentAssignee);
          } else if (CURRENT_ASSIGNEE.equalsIgnoreCase(newAssignee) == false) {
            fileStatsForPage.getOperators().add(newAssignee);
            fileStatsForPage.setPreviousAssignee(fileStatsForPage.getAssignedTo());
            fileStatsForPage.setAssignedTo(newAssignee);
          }
        }
        branchFilestatsArtefakt.setStorageData(pageStats.toString());

        // because filestats is located in /admin folder you need to be su to store/update that file
        wikiContext.getWikiWeb().getAuthorization().runAsSu(wikiContext, new CallableX<Void, RuntimeException>()
        {
          @Override
          public Void call() throws RuntimeException
          {
            wikiContext.getWikiWeb().saveElement(wikiContext, fileStats, true);
            return null;
          }
        });
        GLog.note(GWikiLogCategory.Wiki, "Update filestats for pages",
            new LogAttribute(GWikiLogAttributeType.BranchId, branchId),
            new LogAttribute(GWikiLogAttributeType.PageIds, StringUtils.join(pageIds, ",")),
            new LogAttribute(GenomeAttributeType.Miscellaneous,
                "newAssignee: " + newAssignee + "; pageState: " + fileState.name()));
        return null;
      }
    });
  }

  /**
   * Send an info mail to all operators who were involved in workflow process of current edited page
   */
  private void sendStatusUpdateMailToOperators()
  {
    if (sendMail == false) {
      return;
    }

    wikiContext.runInTenantContext(branch, getWikiSelector(), new CallableX<String, RuntimeException>()
    {
      @Override
      public String call() throws RuntimeException
      {
        BranchFileStats stats = PlcUtils.getBranchFileStats(wikiContext);
        if (stats == null) {
          return StringUtils.EMPTY;
        }
        FileStatsDO statsForId = stats.getFileStatsForId(pageId);
        if (statsForId == null) {
          return StringUtils.EMPTY;
        }

        // retrieve operators and send email to each of them in their specisifed language (if no language is specified english will be taken
        // as default)
        final String currentUserName = wikiContext.getWikiWeb().getAuthorization().getCurrentUserName(wikiContext);
        for (final String operator : statsForId.getOperators()) {
          try {
            wikiContext.getWikiWeb().getAuthorization().runAsUser(operator, wikiContext,
                new CallableX<Void, RuntimeException>()
            {

              GWikiI18nProvider i18n = wikiContext.getWikiWeb().getI18nProvider();

              @Override
              public Void call() throws RuntimeException
              {
                String email = wikiContext.getWikiWeb().getAuthorization().getCurrentUserEmail(wikiContext);
                if (StringUtils.isEmpty(email) == true) {
                  GLog.warn(GWikiLogCategory.Wiki, "User has no mail specified. No status article update mail sent.",
                      new LogAttribute(GenomeAttributeType.UserEmail, operator));
                  return null;
                }

                // prepare email contents
                String subject = i18n.translate(wikiContext, "gwiki.plc.dashpoard.popup.mail.subject",
                    "Article status updated",
                    getPageTitle());
                String bodyString = getBodyString(currentUserName);

                final Map<String, String> ctx = new HashMap<String, String>();
                ctx.put(GWikiEmailProvider.FROM, wikiContext.getWikiWeb().getWikiConfig().getSendEmail());
                ctx.put(GWikiEmailProvider.SUBJECT, subject);
                ctx.put(GWikiEmailProvider.TEXT, bodyString);
                ctx.put(GWikiEmailProvider.MAILTEMPLATE, "edit/pagelifecycle/mailtemplates/StatusUpdateMailTemplate");
                ctx.put(GWikiEmailProvider.TO, email);

                GLog.note(GWikiLogCategory.Wiki, "Sent status update mail to: " + email);
                wikiContext.getWikiWeb().getDaoContext().getEmailProvider().sendEmail(ctx);
                return null;
              }

              /**
               * Get body contents
               * 
               * @param currentUserName
               * @return
               */
              private String getBodyString(final String currentUserName)
              {
                StringBuffer bodyString = new StringBuffer();
                String body = i18n.translate(wikiContext, "gwiki.plc.dashpoard.popup.mail.body", "",
                    wikiContext.getWikiWeb()
                        .getAuthorization().getCurrentUserName(wikiContext),
                    getPageTitle(), wikiContext.globalUrl(pageId), currentUserName,
                    newPageState);
                bodyString.append(body);
                if (StringUtils.isNotBlank(comment) == true) {
                  bodyString.append(i18n.translate(wikiContext, "gwiki.plc.dashpoard.popup.mail.comment", "", comment));
                }
                bodyString.append(i18n.translate(wikiContext, "gwiki.plc.dashpoard.popup.mail.foot"));
                return bodyString.toString();
              }
            });
          } catch (AuthorizationFailedException ex) {
            GWikiLog.warn("Cannot determine email for user: " + operator, ex);
          }
        }
        return null;
      }
    });
  }

  /**
   * applies change comment to page
   */
  private void applyComment()
  {
    Map<String, GWikiArtefakt<?>> map = new HashMap<String, GWikiArtefakt<?>>();
    page.collectParts(map);
    GWikiArtefakt<?> artefakt = map.get("ChangeComment");
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
  public List<Map<String, Object>> getAvailableBranches()
  {
    List<String> allTenants = getWikiSelector().getMptIdSelector().getTenants(GWikiWeb.getRootWiki());
    List<Map<String, Object>> branchProps = new ArrayList<Map<String, Object>>();

    // default new branch is selected
    this.selectedBranch = NEW_BRANCH;

    for (String tenantId : allTenants) {

      // if approve you have to copy the file in a branch other than draft
      if (PlcConstants.DRAFT_ID.equalsIgnoreCase(tenantId) == true
          && FileState.APPROVED_CHIEF_EDITOR.name().equals(newPageState) == true) {
        continue;
      }

      GWikiProps branchInfoProp = wikiContext.runInTenantContext(tenantId, getWikiSelector(),
          new CallableX<GWikiProps, RuntimeException>()
          {
            @Override
            public GWikiProps call() throws RuntimeException
            {
              return PlcUtils.getBranchInfo(wikiContext);
            }
          });

      if (branchInfoProp == null) {
        continue;
      }

      // only add offline branches
      if (BranchState.OFFLINE.name()
          .equals(branchInfoProp.getStringValue(PlcConstants.BRANCH_INFO_BRANCH_STATE)) == true) {
        Map<String, Object> m = new HashMap<String, Object>();
        m.putAll(branchInfoProp.getMap());
        m.put("RELEASE_DATE_DATE",
            GWikiProps.parseTimeStamp(branchInfoProp.getStringValue(PlcConstants.BRANCH_INFO_RELEASE_DATE)));
        branchProps.add(m);

        // if branch release date matches article release date -> preselect branch
        String release = branchInfoProp.getStringValue(PlcConstants.BRANCH_INFO_RELEASE_DATE);
        Date branchReleaseDate = GWikiProps.parseTimeStamp(release);
        if (branchReleaseDate != null && branchReleaseDate.equals(getStartDateOfArticle())) {
          selectedBranch = branchInfoProp.getStringValue(PlcConstants.BRANCH_INFO_BRANCH_ID);
        }
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
    final List<GWikiElementInfo> userInfos = wikiContext.getElementFinder()
        .getPageInfos(new GWikiPageIdMatcher(wikiContext, m));

    List<String> users = new ArrayList<String>();
    for (final GWikiElementInfo user : userInfos) {
      users.add(GWikiContext.getNamePartFromPageId(user.getId()));
    }
    return users;
  }

  /**
   * Returns all page ids (current editied page and all selected dependent pages) that will be changes in this workflow
   * step
   */
  private List<String> getAllPageIdsForUpdate()
  {
    List<String> pageIds = new ArrayList<String>();
    pageIds.add(pageId);
    for (GWikiElement depPageId : getDepObjects()) {
      pageIds.add(depPageId.getElementInfo().getId());
    }
    return pageIds;
  }

  private List<GWikiElement> getAllPagesForUpdate()
  {
    List<GWikiElement> pages = new ArrayList<GWikiElement>();
    pages.add(page);
    for (GWikiElement depPage : getDepObjects()) {
      pages.add(depPage);
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
  public List<GWikiElement> getDepObjects()
  {
    if (depObjects == null) {
      depObjects = new ArrayList<GWikiElement>();
    }
    return depObjects;
  }
}