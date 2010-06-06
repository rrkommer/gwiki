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

package de.micromata.genome.gwiki.page.impl.wiki.macros;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections15.comparators.ReverseComparator;
import org.apache.commons.lang.StringUtils;

import de.micromata.genome.gdbfs.FileNameUtils;
import de.micromata.genome.gwiki.controls.GWikiLoginActionBean;
import de.micromata.genome.gwiki.controls.GWikiRegisterUserActionBean;
import de.micromata.genome.gwiki.model.GWikiArtefakt;
import de.micromata.genome.gwiki.model.GWikiAuthorizationRights;
import de.micromata.genome.gwiki.model.GWikiElement;
import de.micromata.genome.gwiki.model.GWikiElementInfo;
import de.micromata.genome.gwiki.model.GWikiExecutableArtefakt;
import de.micromata.genome.gwiki.model.GWikiPropKeys;
import de.micromata.genome.gwiki.model.GWikiProps;
import de.micromata.genome.gwiki.model.GWikiSettingsProps;
import de.micromata.genome.gwiki.model.config.GWikiMetaTemplate;
import de.micromata.genome.gwiki.model.matcher.GWikiPageIdMatcher;
import de.micromata.genome.gwiki.page.GWikiContext;
import de.micromata.genome.gwiki.page.impl.GWikiDefaultFileNames;
import de.micromata.genome.gwiki.page.impl.GWikiWikiPageArtefakt;
import de.micromata.genome.gwiki.page.impl.actionbean.ActionBeanBase;
import de.micromata.genome.gwiki.utils.ThrowableUtils;
import de.micromata.genome.util.matcher.string.SimpleWildcardMatcherFactory;

/**
 * ActionBean dealing with comment actions.
 * 
 * @author Roger Rene Kommer (r.kommer@micromata.de)
 * 
 */
public class GWikiPageCommentMacroActionBean extends ActionBeanBase implements GWikiPropKeys
{
  public static final String PROP_REPLY_TO = "PAGECOMMENT_REPLYTO";

  public static final String GWIKI_ANON_USERNAME_KEY = "gwiki.anonUserName";

  /**
   * Flat view or thread view.
   */
  private boolean hierarchicThreadView = true;

  /**
   * Comments for page
   */
  private String pageId;

  /**
   * pageId replied to.
   */
  private String replyTo;

  /**
   * Comments currently edited by user.
   */
  private String commentText;

  /**
   * In case of anon user.
   */
  private String userName;

  private String catchaText;

  private String catchaInput;

  private List<GWikiElementInfo> commentElements = new ArrayList<GWikiElementInfo>();

  private List<GWikiElementInfo> fullList = new ArrayList<GWikiElementInfo>();

  private boolean allowAnonComments = false;

  private boolean registerUserEnabled = false;

  private boolean anonUser = true;

  private boolean allowPost = false;

  protected boolean needCatcha()
  {
    return allowPost == true && anonUser == true;
  }

  public static List<GWikiElementInfo> getCommentsForPage(GWikiContext wikiContext, String pageId)
  {
    String pageName = GWikiContext.getNamePartFromPageId(pageId);

    String parentPath = GWikiContext.getParentDirPathFromPageId(pageId);
    String matchRule = parentPath + "comments/" + pageName + "/*";

    List<GWikiElementInfo> l = wikiContext.getElementFinder().getPageInfos(
        new GWikiPageIdMatcher(wikiContext, new SimpleWildcardMatcherFactory<String>().createMatcher(matchRule)));
    return l;
  }

  protected void collectComments()
  {
    List<GWikiElementInfo> l = getCommentsForPage(wikiContext, pageId);
    Collections.sort(l, new ReverseComparator<GWikiElementInfo>(new GWikiElementByPropComparator(GWikiPropKeys.CREATEDAT)));
    fullList = l;
    // TODO find reply/to which are deleted.
    if (hierarchicThreadView == true) {
      List<GWikiElementInfo> nl = new ArrayList<GWikiElementInfo>();
      for (GWikiElementInfo ei : l) {
        if (StringUtils.isEmpty(ei.getProps().getStringValue(PROP_REPLY_TO)) == true) {
          nl.add(ei);
        }
      }
      commentElements = nl;
    } else {
      commentElements = l;
    }
  }

  protected void init()
  {
    String commentConfigId = "admin/config/CommentConfig";
    GWikiProps props = wikiContext.getElementFinder().getConfigProps(commentConfigId);
    allowAnonComments = props.getBooleanValue("COMMENT_ALLOW_ANON");
    String autConfig = "admin/config/GWikiAuthConfig";
    props = wikiContext.getElementFinder().getConfigProps(autConfig);
    registerUserEnabled = props.getBooleanValue(GWikiLoginActionBean.AUTH_ALLOW_REGISTER_USER);
    anonUser = wikiContext.getWikiWeb().getAuthorization().needAuthorization(wikiContext);
    allowPost = allowAnonComments == true || anonUser == false;

  }

  public Object onInit()
  {
    init();
    return onInitImpl();
  }

  protected Object onInitImpl()
  {
    collectComments();
    if (needCatcha() == true) {
      catchaText = GWikiRegisterUserActionBean.calcCaptcha(wikiContext);
      if (StringUtils.isBlank(userName) == true) {
        userName = wikiContext.getCookie(GWIKI_ANON_USERNAME_KEY);
      }
    }
    return null;
  }

  public static GWikiMetaTemplate initMetaTemplate(GWikiContext wikiContext)
  {
    String metaTemplatePageId = GWikiDefaultFileNames.FRAGMENT_METATEMPLATE;
    return wikiContext.getWikiWeb().findMetaTemplate(metaTemplatePageId);
  }

  public static String getViewRightFromParent(GWikiContext wikiContext, String partOf)
  {
    GWikiElementInfo ei = wikiContext.getWikiWeb().findElementInfo(partOf);
    if (ei == null)
      return null;
    return ei.getProps().getStringValue(AUTH_VIEW);
  }

  protected GWikiElement createNewElement()
  {
    GWikiProps props = new GWikiSettingsProps();
    GWikiMetaTemplate metaTemplate = initMetaTemplate(wikiContext);
    props.setStringValue(TYPE, metaTemplate.getElementType());
    props.setStringValue(WIKIMETATEMPLATE, GWikiDefaultFileNames.COMMENT_METATEMPLATE);
    props.setStringValue(TITLE, "Comment");
    props.setStringValue(CREATEDBY, wikiContext.getWikiWeb().getAuthorization().getCurrentUserName(wikiContext));
    props.setDateValue(CREATEDAT, new Date());
    props.setStringValue(MODIFIEDBY, wikiContext.getWikiWeb().getAuthorization().getCurrentUserName(wikiContext));
    props.setStringValue(AUTH_EDIT, GWikiAuthorizationRights.GWIKI_PRIVATE.name());
    props.setDateValue(MODIFIEDAT, new Date());
    props.setStringValue(PARTOF, pageId);
    String viewRight = getViewRightFromParent(wikiContext, pageId);
    if (viewRight != null) {
      props.setStringValue(AUTH_VIEW, viewRight);
    }

    props.setStringValue(AUTH_EDIT, GWikiAuthorizationRights.GWIKI_PRIVATE.name());
    GWikiElementInfo ei = new GWikiElementInfo(props, wikiContext.getWikiWeb().findMetaTemplate(GWikiDefaultFileNames.COMMENT_METATEMPLATE));

    GWikiElement elementToEdit = getWikiContext().getWikiWeb().getStorage().createElement(ei);
    return elementToEdit;
  }

  public Object onSaveComment()
  {
    init();
    if (allowPost == false) {
      wikiContext.addSimpleValidationError("Not allowed to edit"); // TODO gwiki i18n
      return onInitImpl();
    }

    if (StringUtils.isEmpty(commentText) == true) {
      wikiContext.addSimpleValidationError("No comment text"); // TODO gwiki i18n
      return onInitImpl();
    }
    if (needCatcha() == true) {
      if (GWikiRegisterUserActionBean.checkCatcha(wikiContext, catchaInput) == false) {
        wikiContext.addValidationFieldError("gwiki.page.admin.RegisterUser.message.wrongcatcha", "catchaInput");
        return onInitImpl();
      }
      if (StringUtils.isEmpty(userName) == true) {
        wikiContext.addSimpleValidationError("Provide a user name");
        return onInitImpl();
      }
      wikiContext.setCookie(GWIKI_ANON_USERNAME_KEY, userName);
    }
    if (StringUtils.isEmpty(pageId) == true) {
      wikiContext.addSimpleValidationError("pageId missing");
      return onInitImpl();
    }

    String pageName = GWikiContext.getNamePartFromPageId(pageId);
    String parentPath = GWikiContext.getParentDirPathFromPageId(pageId);
    String newPageId = FileNameUtils.join(parentPath, "comments", pageName, GWikiProps.formatTimeStamp(new Date()));
    GWikiElement cel = createNewElement();
    cel.getElementInfo().setId(newPageId);
    if (StringUtils.isNotEmpty(replyTo) == true) {
      cel.getElementInfo().getProps().setStringValue(PROP_REPLY_TO, replyTo);
    }
    Map<String, GWikiArtefakt< ? >> map = new HashMap<String, GWikiArtefakt< ? >>();
    cel.collectParts(map);
    String partName = "MainPage";
    GWikiWikiPageArtefakt wka = (GWikiWikiPageArtefakt) map.get(partName);
    wka.setStorageData(commentText);
    try {
      wka.compileFragements(wikiContext);
    } catch (Exception ex) {
      String st = ThrowableUtils.getExceptionStacktraceForHtml(ex);
      wikiContext.addSimpleValidationError("Kann Wiki Seite nicht kompilieren: " + ex.getMessage() + "\n" + st);
      return onInitImpl();
    }
    if (StringUtils.isEmpty(userName) == false) {
      cel.getElementInfo().getProps().setStringValue(GWikiPropKeys.MODIFIEDBY, "anon/" + userName);
      if (wikiContext.getWikiWeb().findElementInfo(cel.getElementInfo().getId()) == null) {
        cel.getElementInfo().getProps().setStringValue(GWikiPropKeys.CREATEDBY, "anon/" + userName);
      }
    }
    getWikiContext().getWikiWeb().saveElement(wikiContext, cel, false);
    commentText = "";
    catchaInput = "";
    return onInitImpl();
  }

  /**
   * called by gspt
   * 
   * @param ei
   */
  public void renderCommentBody(GWikiElementInfo ei)
  {
    GWikiElement el = wikiContext.getWikiWeb().getElement(ei.getId());
    GWikiArtefakt< ? > art = el.getPart("MainPage");
    if (art instanceof GWikiExecutableArtefakt< ? >) {
      ((GWikiExecutableArtefakt< ? >) art).render(wikiContext);
    } else {
      wikiContext.getWikiWeb().serveWiki(wikiContext, el);
    }
  }

  public List<GWikiElementInfo> getChildComments(GWikiElementInfo ei)
  {
    if (ei == null || hierarchicThreadView == false) {
      return Collections.emptyList();
    }
    List<GWikiElementInfo> ret = new ArrayList<GWikiElementInfo>();
    for (GWikiElementInfo ci : fullList) {
      if (ei.getId().equals(ci.getProps().getStringValue(PROP_REPLY_TO)) == true) {
        ret.add(ci);
      }
    }
    return ret;
  }

  public String getCommentText()
  {
    return commentText;
  }

  public void setCommentText(String commentText)
  {
    this.commentText = commentText;
  }

  public String getPageId()
  {
    return pageId;
  }

  public void setPageId(String pageId)
  {
    this.pageId = pageId;
  }

  public List<GWikiElementInfo> getCommentElements()
  {
    return commentElements;
  }

  public void setCommentElements(List<GWikiElementInfo> commentElements)
  {
    this.commentElements = commentElements;
  }

  public boolean isHierarchicThreadView()
  {
    return hierarchicThreadView;
  }

  public void setHierarchicThreadView(boolean hierarchicThreadView)
  {
    this.hierarchicThreadView = hierarchicThreadView;
  }

  public String getReplyTo()
  {
    return replyTo;
  }

  public void setReplyTo(String replyTo)
  {
    this.replyTo = replyTo;
  }

  public List<GWikiElementInfo> getFullList()
  {
    return fullList;
  }

  public void setFullList(List<GWikiElementInfo> fullList)
  {
    this.fullList = fullList;
  }

  public boolean isAllowAnonComments()
  {
    return allowAnonComments;
  }

  public void setAllowAnonComments(boolean allowAnonComments)
  {
    this.allowAnonComments = allowAnonComments;
  }

  public boolean isRegisterUserEnabled()
  {
    return registerUserEnabled;
  }

  public void setRegisterUserEnabled(boolean registerUserEnabled)
  {
    this.registerUserEnabled = registerUserEnabled;
  }

  public boolean isAnonUser()
  {
    return anonUser;
  }

  public void setAnonUser(boolean anonUser)
  {
    this.anonUser = anonUser;
  }

  public boolean isAllowPost()
  {
    return allowPost;
  }

  public void setAllowPost(boolean allowPost)
  {
    this.allowPost = allowPost;
  }

  public String getUserName()
  {
    return userName;
  }

  public void setUserName(String userName)
  {
    this.userName = userName;
  }

  public String getCatchaText()
  {
    return catchaText;
  }

  public void setCatchaText(String catchaText)
  {
    this.catchaText = catchaText;
  }

  public String getCatchaInput()
  {
    return catchaInput;
  }

  public void setCatchaInput(String catchaInput)
  {
    this.catchaInput = catchaInput;
  }

}
