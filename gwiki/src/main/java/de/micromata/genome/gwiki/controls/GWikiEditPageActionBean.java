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

package de.micromata.genome.gwiki.controls;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;

import de.micromata.genome.gdbfs.FileNameUtils;
import de.micromata.genome.gwiki.model.AuthorizationFailedException;
import de.micromata.genome.gwiki.model.GWikiArtefakt;
import de.micromata.genome.gwiki.model.GWikiAttachment;
import de.micromata.genome.gwiki.model.GWikiAuthorization.UserPropStorage;
import de.micromata.genome.gwiki.model.GWikiAuthorizationRights;
import de.micromata.genome.gwiki.model.GWikiElement;
import de.micromata.genome.gwiki.model.GWikiElementInfo;
import de.micromata.genome.gwiki.model.GWikiGlobalConfig;
import de.micromata.genome.gwiki.model.GWikiLog;
import de.micromata.genome.gwiki.model.GWikiLogCategory;
import de.micromata.genome.gwiki.model.GWikiPropKeys;
import de.micromata.genome.gwiki.model.GWikiProps;
import de.micromata.genome.gwiki.model.GWikiSettingsProps;
import de.micromata.genome.gwiki.model.GWikiStorage;
import de.micromata.genome.gwiki.model.GWikiWeb;
import de.micromata.genome.gwiki.model.config.GWikiMetaTemplate;
import de.micromata.genome.gwiki.model.matcher.GWikiPageIdMatcher;
import de.micromata.genome.gwiki.page.GWikiContext;
import de.micromata.genome.gwiki.page.RenderModes;
import de.micromata.genome.gwiki.page.impl.GWikiDefaultFileNames;
import de.micromata.genome.gwiki.page.impl.GWikiEditableArtefakt;
import de.micromata.genome.gwiki.page.impl.GWikiEditorArtefakt;
import de.micromata.genome.gwiki.page.impl.GWikiWikiPageArtefakt;
import de.micromata.genome.gwiki.page.impl.actionbean.ActionMessage;
import de.micromata.genome.gwiki.page.impl.actionbean.ActionMessages;
import de.micromata.genome.gwiki.page.impl.wiki.GWikiMacroClassFactory;
import de.micromata.genome.gwiki.page.impl.wiki.GWikiMacroFactory;
import de.micromata.genome.gwiki.page.impl.wiki.GWikiMacroRte;
import de.micromata.genome.gwiki.page.impl.wiki.macros.GWikiHelpLinkMacro;
import de.micromata.genome.gwiki.page.impl.wiki.macros.GWikiHtmlBodyTagMacro;
import de.micromata.genome.gwiki.page.impl.wiki.macros.GWikiHtmlTagMacro;
import de.micromata.genome.gwiki.utils.CommaListParser;
import de.micromata.genome.gwiki.utils.html.Html2WikiFilter;
import de.micromata.genome.gwiki.utils.html.Html2WikiTransformInfo;
import de.micromata.genome.gwiki.utils.html.Html2WikiTransformer;
import de.micromata.genome.logging.GLog;
import de.micromata.genome.util.collections.ArrayMap;
import de.micromata.genome.util.matcher.BooleanListRulesFactory;
import de.micromata.genome.util.matcher.Matcher;
import de.micromata.genome.util.types.Converter;
import de.micromata.genome.util.types.Pair;

/**
 * ActionBean for editing/creating/deleting pages.
 * 
 * @author Roger Rene Kommer (r.kommer@micromata.de)
 * 
 */
public class GWikiEditPageActionBean extends GWikiEditElementBaseActionBean implements GWikiPropKeys
{
  public static final String NO_NOTIFICATION_EMAILS = "de.micromata.genome.gwiki.controls.GWikiEditPageActionBean.noNotificationEmails";

  public static final String GWIKI_DEFAULT_EDITOR = "gwikidefeditor";

  public static final String GWIKI_EDITOR_FULLSCREEN = "gwikideffullscreeneditor";

  /**
   * first is title, second id.
   */
  protected List<Pair<String, String>> availableMetaTemplates = new ArrayList<Pair<String, String>>();

  protected String metaTemplatePageId;

  protected boolean chooseMetaTemplate = false;

  protected Map<String, GWikiArtefakt<?>> parts = new ArrayMap<String, GWikiArtefakt<?>>();

  protected Map<String, GWikiEditorArtefakt<?>> editors = new ArrayMap<String, GWikiEditorArtefakt<?>>();

  protected GWikiElement elementToEdit;

  protected GWikiMetaTemplate metaTemplate;

  /**
   * path of storage for the element.
   */
  protected String path;

  /**
   * Title of the page.
   */
  protected String title;

  protected String copyFromPageId;

  protected boolean isInOnInit = false;

  /**
   * if set use this directory as storepath prefix.
   */
  protected String storePath = null;

  /**
   * After finished, local path to go back.
   */
  protected String backUrl = null;

  protected boolean launchPreview = false;

  protected boolean noNotificationEmails = false;

  /**
   * If backup copy exists.
   */
  protected GWikiElementInfo backupElementInfo;

  /**
   * If edit will be opened. has an initial backup.
   */
  protected boolean initalBackup = false;

  protected boolean disableBackup = false;

  protected String wikiDefaultEditor = "wiki";

  protected boolean gwikiEditDefaultFullscreen;

  /**
   * comma seperated list of parts with no editor.
   */
  protected String hideParts = "";

  public static List<Pair<String, String>> getAvailableTemplates(GWikiContext wikiContext)
  {
    GWikiMetaTemplate currentTemplate = null;

    if (wikiContext.getCurrentElement() != null) {
      currentTemplate = wikiContext.getCurrentElement().getMetaTemplate();
    }
    List<Pair<String, String>> availableMetaTemplates = new ArrayList<Pair<String, String>>();
    Matcher<String> m = new BooleanListRulesFactory<String>()
        .createMatcher("+admin/templates/*MetaTemplate,-admin/templates/intern/*");
    List<GWikiElementInfo> ret = wikiContext.getElementFinder().getPageInfos(new GWikiPageIdMatcher(wikiContext, m));

    ret.addAll(wikiContext.getWikiWeb().getDaoContext().getPluginRepository().getTemplates(wikiContext));

    for (GWikiElementInfo ei : ret) {
      GWikiMetaTemplate template = wikiContext.getWikiWeb().findMetaTemplate(ei.getId());
      if (template == null) {
        continue;
      }
      if (template.isNoNewPage() == true) {
        continue;
      }
      String editRight = template.getRequiredEditRight();
      if (editRight == null) {
        editRight = GWikiAuthorizationRights.GWIKI_CREATEPAGES.name();
      }
      if (wikiContext.isAllowTo(editRight) == false) {
        continue;
      }
      if (currentTemplate != null && currentTemplate.getAllowedNewChildMetaTemplates() != null) {
        if (currentTemplate.getAllowedNewChildMetaTemplates().match(template.getPageId()) == false) {
          continue;
        }
      }
      if (template.getAllowedNewParentMetaTemplates() != null) {
        if (currentTemplate == null
            || template.getAllowedNewParentMetaTemplates().match(currentTemplate.getPageId()) == false) {
          continue;
        }
      }
      availableMetaTemplates.add(Pair.make(wikiContext.getTranslatedProp(ei.getTitle()), ei.getId()));
    }
    Collections.sort(availableMetaTemplates, new Comparator<Pair<String, String>>()
    {

      @Override
      public int compare(Pair<String, String> o1, Pair<String, String> o2)
      {
        return o1.getFirst().compareTo(o2.getFirst());
      }
    });
    return availableMetaTemplates;
  }

  protected void fillAvailableTemplates()
  {
    this.availableMetaTemplates = getAvailableTemplates(wikiContext);

  }

  protected GWikiMetaTemplate initMetaTemplate()
  {
    if (metaTemplate != null) {
      return metaTemplate;
    }
    if (StringUtils.isBlank(metaTemplatePageId) == true) {
      if (newPage == true) {
        fillAvailableTemplates();
        chooseMetaTemplate = true;
        return null;
      }

      metaTemplatePageId = GWikiDefaultFileNames.DEFAULT_METATEMPLATE;
    }
    metaTemplate = getWikiContext().getWikiWeb().findMetaTemplate(metaTemplatePageId);
    if (StringUtils.isNotEmpty(metaTemplate.getCopyFromPageId()) == true) {
      copyFromPageId = metaTemplate.getCopyFromPageId();
    }
    return metaTemplate;
  }

  protected void getPageIdFromTitle()
  {
    if (StringUtils.isNotBlank(pageId) == true) {
      return;
    }
    if (StringUtils.isEmpty(title) == true) {
      return;
    }
    pageId = GWikiContext.getPageIdFromTitle(title);
    if (pageId.indexOf('/') != -1) {
      return;
    }
    if (StringUtils.isNotEmpty(storePath) == true) {
      pageId = FileNameUtils.join(storePath, pageId);
      storePath = null;
    } else if (StringUtils.isNotBlank(parentPageId) == true) {
      pageId = FileNameUtils.join(GWikiContext.getParentDirPathFromPageId(parentPageId), pageId);
    }
  }

  private String getNiceTitleForPageId()
  {
    String result = null;

    if (StringUtils.isNotBlank(pageId)) {
      final String[] pageIdParts = pageId.split("/");
      result = pageIdParts[pageIdParts.length - 1];
    }

    return result;
  }

  protected void initHelpPageLink()
  {
    if (metaTemplate == null) {
      return;
    }
    if (metaTemplate.getEditHelpPageId() == null) {
      return;
    }
    wikiContext.setRequestAttribute(GWikiHelpLinkMacro.REQATTR_HELPPAGE, metaTemplate.getEditHelpPageId());
  }

  protected GWikiElement createNewElement()
  {
    if (initMetaTemplate() == null) {
      return null;
    }

    if (StringUtils.isEmpty(pageId) == true) {
      getPageIdFromTitle();
    }
    if (StringUtils.isNotEmpty(pageId) == true) {
      if (StringUtils.isNotEmpty(storePath) == true) {
        pageId = storePath + pageId;
        storePath = null;
      }
    }
    GWikiProps props = new GWikiSettingsProps();

    // if (metaTemplate != null) {
    // props.setStringValue(TYPE, metaTemplate.getElementType());
    // }
    props.setStringValue(WIKIMETATEMPLATE, metaTemplatePageId);
    props.setStringValue(PARENTPAGE, parentPageId);
    props.setStringValue(TITLE, title);

    GWikiElementInfo ei = new GWikiElementInfo(props, metaTemplate);
    GWikiElement elementToEdit;
    if (isInOnInit == true && StringUtils.isNotEmpty(copyFromPageId) == true) {
      elementToEdit = wikiContext.getWikiWeb().loadNewElement(copyFromPageId);
      elementToEdit.setElementInfo(ei);
    } else {
      elementToEdit = getWikiContext().getWikiWeb().getStorage().createElement(ei);
    }
    // elementToEdit.setMetaTemplate(metaTemplate);
    return elementToEdit;
  }

  protected void initPartEditors()
  {
    elementToEdit.collectParts(parts);
    // parts.put("Settings", new GWikiPropsArtefakt(elementToEdit.getElementInfo().getProps()));
    List<String> hidePartList = Converter.parseStringTokens(hideParts, ", ", false);
    for (Map.Entry<String, GWikiArtefakt<?>> me : parts.entrySet()) {
      if (me.getValue() instanceof GWikiEditableArtefakt) {
        if (hidePartList.contains(me.getKey()) == true) {
          continue;
        }
        editors.put(me.getKey(), ((GWikiEditableArtefakt) me.getValue()).getEditor(elementToEdit, this, me.getKey()));
      }
    }
  }

  protected boolean initElement()
  {
    if (newPage == false && pageId == null) {
      wikiContext.addValidationError("gwiki.edit.EditPage.message.nopageidset");
      newPage = true;
      return false;
    }
    if (newPage == false) {
      // get a new fresh copy
      GWikiElementInfo ei = getWikiContext().getWikiWeb().getStorage().loadElementInfo(pageId);
      if (ei != null) {
        elementToEdit = getWikiContext().getWikiWeb().getStorage().loadElement(ei);
      }
      if (elementToEdit == null) {
        wikiContext.addValidationError("gwiki.edit.EditPage.message.pagenotfound", pageId);
        return false;
      }
      metaTemplate = elementToEdit.getMetaTemplate();
      parentPageId = elementToEdit.getElementInfo().getParentId();
      // metaTemplatePageId = metaTemplate.getPageId)()
    } else {
      elementToEdit = createNewElement();
      if (elementToEdit == null) {
        return false;
      }
    }
    if (elementToEdit != null) {
      if (StringUtils.isBlank(title) == true) {
        title = elementToEdit.getElementInfo().getTitle();
      }
    }
    if (StringUtils.isEmpty(title) == true) {
      title = getNiceTitleForPageId();
    }
    return true;
  }

  protected boolean init()
  {
    wikiContext.getWikiWeb().setPreviewPage(wikiContext, null);
    if (initElement() == false) {
      return false;
    }
    if (metaTemplate == null) {
      wikiContext.addValidationError("gwiki.edit.EditPage.message.nometatemplate");
      return false;
    }
    initHelpPageLink();
    if (elementToEdit instanceof GWikiAttachment) {
      disableBackup = true;
    }
    GWikiProps props = elementToEdit.getElementInfo().getProps();
    if (newPage == true) {
      // props.setStringValue(TYPE, metaTemplate.getElementType());
      props.setStringValue(TITLE, title);
      props.setStringValue(PARENTPAGE, parentPageId);
      if (StringUtils.isEmpty(storePath) && StringUtils.isNotBlank(parentPageId) == true) {
        storePath = GWikiContext.getParentDirPathFromPageId(parentPageId);
      }
    } else {
      initBackup();
      if (StringUtils.isBlank(title) == true) {
        String t = props.getStringValue(TITLE);
        if (StringUtils.isNotBlank(t) == true) {
          title = t;
        }
      } else {
        props.setStringValue(TITLE, title);
      }
      String t = props.getStringValue(PARENTPAGE);
      if (StringUtils.isNotBlank(t) == true) {
        parentPageId = t;
      } else {
        props.setStringValue(PARENTPAGE, parentPageId);
      }
    }
    initPartEditors();
    wikiDefaultEditor = wikiContext.getWikiWeb().getAuthorization().getUserProp(wikiContext, GWIKI_DEFAULT_EDITOR);
    gwikiEditDefaultFullscreen = StringUtils.equals(
        wikiContext.getWikiWeb().getAuthorization().getUserProp(wikiContext, GWIKI_EDITOR_FULLSCREEN), "true");
    return true;
  }

  protected void checkAccess()
  {
    if (newPage == true) {
      if (wikiContext.getWikiWeb().getAuthorization().isAllowToCreate(wikiContext,
          elementToEdit.getElementInfo()) == false) {
        throw new AuthorizationFailedException(
            translate("gwiki.authorization.message.cannotcreatepage", elementToEdit.getMetaTemplate()
                .getElementType()));
      }
    } else {
      if (wikiContext.getWikiWeb().getAuthorization().isAllowToEdit(wikiContext,
          elementToEdit.getElementInfo()) == false) {
        throw new AuthorizationFailedException(
            translate("gwiki.authorization.message.cannoteditpage", elementToEdit.getMetaTemplate()
                .getElementType()));
      }
    }
  }

  @Override
  public Object onInit()
  {
    isInOnInit = true;
    if (init() == false) {
      return null;
    }
    checkAccess();
    // elementProperties = buildDescription(elementToEdit.getElementInfo().getProps().getMap(), GWikiEditWikiPropsDescription.values());
    if (backupElementInfo != null) {
      initalBackup = true;
    }
    return null;
  }

  public Object onReloadTemplateList()
  {
    isInOnInit = true;
    chooseMetaTemplate = true;
    fillAvailableTemplates();
    if (init() == false) {
      return null;
    }

    checkAccess();
    return null;
  }

  protected void saveParts()
  {
    GWikiContext ctx = getWikiContext();

    elementToEdit.saveParts(ctx, editors);
  }

  protected Object goBack(boolean cancel)
  {
    if (StringUtils.isNotBlank(backUrl) == true) {
      return backUrl;
    }
    GWikiElement backElement = null;
    if (elementToEdit != null && elementToEdit.getElementInfo().isViewable() == true //
        && (elementToEdit instanceof GWikiAttachment) == false //
        && StringUtils.isBlank(pageId) == false //
        && (cancel == false || newPage == false) //
        && StringUtils.equals(elementToEdit.getElementInfo().getType(), "attachment") == false
        && (backElement = getWikiContext().getWikiWeb().findElement(pageId)) != null) {
      return backElement;
    } else if (StringUtils.isNotBlank(pageId) == true && wikiContext.getWikiWeb().findElement(pageId) != null //
        && (elementToEdit != null
            && StringUtils.equals(elementToEdit.getElementInfo().getType(), "attachment") == false)
        && wikiContext.getWikiWeb().findElement(pageId).getElementInfo().isViewable() == true) {
      return pageId;
    } else if (StringUtils.isNotBlank(parentPageId) == true) {
      return getWikiContext().getWikiWeb().findElement(parentPageId);
    } else {
      return getWikiContext().getWikiWeb().getHomeElement(getWikiContext());
    }
  }

  public Object onSave()
  {
    if (init() == false) {
      return null;
    }
    return onSaveImpl();
  }

  public Object onSaveEdit()
  {
    if (init() == false) {
      return null;
    }
    onSaveImpl();
    return null;
  }

  protected Object onSaveImpl()
  {

    checkAccess();
    if (wikiContext.hasValidationErrors() == true) {
      return null;
    }
    saveParts();
    if (StringUtils.isBlank(title) == true) {
      wikiContext.addValidationError("gwiki.edit.EditPage.message.titlemustnotempty");
      return null;
    }
    if (StringUtils.isEmpty(pageId) == true) {
      getPageIdFromTitle();
    }
    if (newPage == true) {
      if (wikiContext.getWikiWeb().findElementInfo(pageId) != null) {
        wikiContext.addValidationError("gwiki.edit.EditPage.message.pageidalreadyexists", pageId);
        pageId = null;
        return null;
      }
      elementToEdit.getElementInfo().setId(pageId);

    }
    if (wikiContext.hasValidationErrors() == true) {
      return null;
    }
    wikiContext.setRequestAttribute(NO_NOTIFICATION_EMAILS, noNotificationEmails);
    getWikiContext().getWikiWeb().saveElement(wikiContext, elementToEdit, false);
    removeBackup();
    return goBack(false);
  }

  public Object onCancel()
  {
    init();
    wikiContext.getWikiWeb().setPreviewPage(wikiContext, null);
    removeBackup();
    return goBack(true);
  }

  public Object onCopy()
  {
    init();
    saveParts();
    removeBackup();
    String oldPageId = pageId;
    newPage = true;

    this.pageId = null;

    if (this.elementToEdit != null) {
      this.elementToEdit.getElementInfo().setId(null);
      this.metaTemplatePageId = this.elementToEdit.getMetaTemplate().getPageId();
    }
    if (StringUtils.isEmpty(storePath) == true) {
      if (StringUtils.isNotBlank(parentPageId) == true) {
        storePath = GWikiContext.getParentDirPathFromPageId(parentPageId);
      } else if (StringUtils.isNotBlank(oldPageId) == true) {
        storePath = GWikiContext.getParentDirPathFromPageId(oldPageId);
      }
    }

    this.title = "";
    checkAccess();
    // elementProperties = buildDescription(elementToEdit.getElementInfo().getProps().getMap(), GWikiEditWikiPropsDescription.values());

    return null;
  }

  public Object onDelete()
  {
    if (init() == false) {
      return null;
    }
    checkAccess();
    wikiContext.ensureAllowTo(GWikiAuthorizationRights.GWIKI_DELETEPAGES.name());

    if (wikiContext.hasValidationErrors() == true) {
      return null;
    }

    GWikiWeb wikiWeb = getWikiContext().getWikiWeb();
    for (GWikiElementInfo ei : wikiWeb.getElementInfos()) {
      if (StringUtils.equals(ei.getParentId(), elementToEdit.getElementInfo().getId()) == true) {
        wikiContext.addValidationError("gwiki.edit.EditPage.message.cannotdeletebecausechilds", ei.getId());
        break;
      }
    }

    if (wikiContext.hasValidationErrors() == true) {
      return null;
    }

    wikiWeb.removeWikiPage(wikiContext, elementToEdit);
    removeBackup();
    return goBack(false);
  }

  public Object onPreview()
  {
    init();
    saveParts();

    if (StringUtils.isBlank(title) == true) {
      wikiContext.addValidationError("gwiki.edit.EditPage.message.titlemustnotempty");
      return null;
    }
    if (newPage == true) {
      // pageId = titleToId(title);
      elementToEdit.getElementInfo().setId(pageId);
    }
    if (wikiContext.hasValidationErrors() == true) {
      return null;
    }
    if (elementToEdit == null) {
      wikiContext.addValidationError("gwiki.edit.EditPage.message.previewnotpossible");
      return null;
    }
    if (wikiContext.hasValidationErrors() == true) {
      return null;
    }
    wikiContext.getWikiWeb().setPreviewPage(wikiContext, elementToEdit);
    launchPreview = true;
    return null;
  }

  public String getBackupId()
  {
    String currentUser = wikiContext.getWikiWeb().getAuthorization().getCurrentUserName(wikiContext);
    String tid = FileNameUtils.join("tmp/users", currentUser, "concepts", pageId);
    return tid;
  }

  public Object onInitDeleteBackup()
  {
    onInit();
    removeBackup();
    return null;
  }

  public Object onInitLoadBackup()
  {
    if (initElement() == false) {
      return null;
    }
    initBackup();
    if (backupElementInfo == null) {
      wikiContext.addValidationError("gwiki.edit.EditPage.message.nobackupavailable");
      return onInit();
    }
    GWikiElement backupElement = wikiContext.getWikiWeb().getDaoContext().getStorage().loadElement(backupElementInfo);
    backupElement.getElementInfo().setId(pageId);
    elementToEdit = backupElement;
    initPartEditors();
    removeBackup();
    return null;
  }

  protected void initBackup()
  {

    if (StringUtils.isEmpty(pageId) == true) {
      return;
    }
    String backupId = getBackupId();

    backupElementInfo = wikiContext.getWikiWeb().getDaoContext().getStorage().loadElementInfo(backupId);
  }

  protected void removeBackup()
  {
    String backupId = getBackupId();

    GWikiElement el = wikiContext.getWikiWeb().getDaoContext().getStorage().loadElement(backupId);
    if (el == null) {
      backupElementInfo = null;
      initalBackup = false;
      return;
    }
    wikiContext.setRequestAttribute(NO_NOTIFICATION_EMAILS, true);
    wikiContext.setRequestAttribute(GWikiStorage.STORE_NO_ARCHIVE, true);
    wikiContext.setRequestAttribute(GWikiStorage.STORE_NO_INDEX, true);
    wikiContext.getWikiWeb().removeWikiPage(wikiContext, el);
    backupElementInfo = null;
    initalBackup = false;

  }

  public Object onAsyncRemoveBackup()
  {
    init();
    removeBackup();
    return noForward();
  }

  public Object onAsyncSave()
  {
    if (init() == false) {
      return sendAsyncValidationError();
    }
    if (StringUtils.isBlank(pageId) == true) {
      wikiContext.append(wikiContext.getTranslated("gwiki.edit.EditPage.onlinebackup.naming") + "<br/>");
      wikiContext.flush();
      return noForward();
    }
    saveParts();
    wikiContext.setRequestAttribute(NO_NOTIFICATION_EMAILS, true);
    wikiContext.setRequestAttribute(GWikiStorage.STORE_NO_ARCHIVE, true);
    wikiContext.setRequestAttribute(GWikiStorage.STORE_NO_INDEX, true);

    String tid = getBackupId();
    elementToEdit.getElementInfo().setId(tid);

    getWikiContext().getWikiWeb().saveElement(wikiContext, elementToEdit, false);
    wikiContext.append("<a href=\"javascript:restoreBackup();\">"
        + wikiContext
            .getWikiWeb()
            .getI18nProvider()
            .translate(wikiContext, "gwiki.edit.EditPage.onlinebackup", "",
                wikiContext.getUserDateString(elementToEdit.getElementInfo().getModifiedAt()))
        + "</a><br/>");
    wikiContext.flush();
    return noForward();
  }

  protected Object sendAsyncValidationError()
  {
    wikiContext.getResponse().setStatus(201);
    ActionMessages amessages = wikiContext.getValidationErrors();
    StringBuilder sb = new StringBuilder();
    for (Map.Entry<String, List<ActionMessage>> me : amessages.entrySet()) {
      for (ActionMessage am : me.getValue()) {
        sb.append(am.getMessage(wikiContext.getWikiWeb().getAuthorization().getCurrentUserLocale(wikiContext)));
      }
    }
    wikiContext.getResponse().setStatus(201);
    wikiContext.append(sb.toString());
    wikiContext.flush();
    return noForward();
  }

  protected Object sendAsyncValidationError(String message)
  {
    wikiContext.getResponse().setStatus(201);
    wikiContext.append(message);
    wikiContext.flush();
    return noForward();

  }

  public Object onAsyncWikiView()
  {
    wikiContext.getWikiWeb().getAuthorization().setUserProp(wikiContext, GWIKI_DEFAULT_EDITOR, "wiki",
        UserPropStorage.Client);
    return noForward();
  }

  public Object onAsyncWikiPreview()
  {
    try {
      if (init() == false) {
        return sendAsyncValidationError();
      }

      saveParts();

      String partName = wikiContext.getRequestParameter("partName");
      if ((parts.get(partName) instanceof GWikiWikiPageArtefakt) == false) {
        wikiContext.append("no part name given");
        wikiContext.flush();
        return noForward();
      }
      GWikiWikiPageArtefakt wiki = (GWikiWikiPageArtefakt) parts.get(partName);
      wikiContext.append("<div class=\"gwikiContent\">");
      wiki.render(wikiContext);
      wikiContext.append("</div>\n");
      wikiContext.flush();
      return noForward();
    } catch (Exception ex) {
      GWikiLog.error("Failure onAsyncWikiPreview: " + ex.getMessage(), ex);
      return sendAsyncValidationError("Failure on Render Preview");
    }
  }

  public Object onAsyncRteCode()
  {
    try {
      if (init() == false) {
        return sendAsyncValidationError();
      }
      wikiContext.setRenderMode(RenderModes.combine(RenderModes.ForRichTextEdit));
      saveParts();

      String partName = wikiContext.getRequestParameter("partName");
      if ((parts.get(partName) instanceof GWikiWikiPageArtefakt) == false) {
        wikiContext.append("no part name given");
        return noForward();
      }
      wikiContext.getWikiWeb().getAuthorization().setUserProp(wikiContext, GWIKI_DEFAULT_EDITOR, "rte",
          UserPropStorage.Client);
      GWikiWikiPageArtefakt wiki = (GWikiWikiPageArtefakt) parts.get(partName);
      wikiContext.append("<div class=\"gwikiContent\">");
      wiki.render(wikiContext);
      wikiContext.append("</div>\n");
      wikiContext.flush();
      return noForward();
    } catch (Exception ex) {
      GWikiLog.error("Failure onAsyncRteCode: " + ex.getMessage(), ex);
      return sendAsyncValidationError("Failure on Render Preview");
    }
  }

  public Object onAsyncFullscreen()
  {
    String value = "false";
    if (StringUtils.equals(wikiContext.getRequestParameter("showFullScreen"), "true") == true) {
      value = "true";
    }
    wikiContext.getWikiWeb().getAuthorization().setUserProp(wikiContext, GWIKI_EDITOR_FULLSCREEN, value,
        UserPropStorage.Client);
    // GLog.note(GWikiLogCategory.Wiki, "Stored editmaximize: " + value);
    return noForward();
  }

  protected Set<String> getHtmlTagMacros()
  {
    Set<String> s = new HashSet<String>();
    GWikiGlobalConfig wikiConfig = wikiContext.getWikiWeb().getWikiConfig();
    Map<String, GWikiMacroFactory> macros = wikiConfig.getWikiMacros(wikiContext);
    for (Map.Entry<String, GWikiMacroFactory> me : macros.entrySet()) {
      if ((me.getValue() instanceof GWikiMacroClassFactory) == false) {
        continue;
      }
      GWikiMacroClassFactory cf = (GWikiMacroClassFactory) me.getValue();
      Class<?> cls = cf.getClazz();
      if (cls == null) {
        continue;
      }
      if (GWikiHtmlBodyTagMacro.class.isAssignableFrom(cls) || GWikiHtmlTagMacro.class.isAssignableFrom(cls)) {
        s.add(me.getKey());
      }
    }
    return s;
  }

  protected List<Html2WikiTransformer> collectHtml2WikiTransformers()
  {
    List<Html2WikiTransformer> transformers = new ArrayList<Html2WikiTransformer>();
    GWikiGlobalConfig wikiConfig = wikiContext.getWikiWeb().getWikiConfig();
    Map<String, GWikiMacroFactory> macros = wikiConfig.getWikiMacros(wikiContext);
    for (Map.Entry<String, GWikiMacroFactory> me : macros.entrySet()) {
      if (me.getValue().isRteMacro() == false) {
        continue;
      }
      GWikiMacroRte rteMacro = (GWikiMacroRte) me.getValue().createInstance();
      Html2WikiTransformInfo ti = rteMacro.getTransformInfo();
      if (ti != null) {
        transformers.add(ti);
      }
    }
    return transformers;
  }

  public Object onRteToWiki()
  {
    try {
      String htmlCode = wikiContext.getRequestParameter("htmlCode");
      Html2WikiFilter filter = new Html2WikiFilter();
      filter.setSupportedHtmlTags(getHtmlTagMacros());
      filter.setMacroTransformer(collectHtml2WikiTransformers());
      String ret = filter.transform(htmlCode);
      wikiContext.append(ret);
      wikiContext.flush();
      return noForward();
    } catch (Exception ex) {
      GWikiLog.error("Failure onRteToWiki: " + ex.getMessage(), ex);
      return sendAsyncValidationError("Failure on Render Preview");
    }
  }

  public Object onReorderChildsAsync()
  {
    elementToEdit = wikiContext.getWikiWeb().findElement(pageId);
    if (elementToEdit == null) {
      return noForward();
    }
    checkAccess();

    String page1 = getReqParam("p1");
    String page2 = getReqParam("p2");
    if (StringUtils.isEmpty(page1) == true || StringUtils.isEmpty(page2) == true) {

      return noForward();
    }
    String prefix = "chid_";
    if (page1.startsWith(prefix) == true) {
      page1 = page1.substring(prefix.length());
    }
    if (page2.startsWith(prefix) == true) {
      page2 = page2.substring(prefix.length());
    }
    GWikiElementInfo c1 = wikiContext.getWikiWeb().findElementInfo(page1);
    if (c1 == null) {
      GLog.note(GWikiLogCategory.Wiki, "Cannot find page: " + page1 + " for reordering");
      return noForward();
    }
    GWikiElementInfo c2 = wikiContext.getWikiWeb().findElementInfo(page2);
    if (c2 == null) {
      GLog.note(GWikiLogCategory.Wiki, "Cannot find page: " + page2 + " for reordering");
      return noForward();
    }
    if (StringUtils.equals(c1.getParentId(), pageId) == false
        || StringUtils.equals(c1.getParentId(), pageId) == false) {
      return noForward();
    }
    List<String> ochildList = elementToEdit.getElementInfo().getProps().getStringList(GWikiPropKeys.CHILDORDER);
    List<String> childList = new ArrayList<String>();
    if (ochildList != null) {
      childList.addAll(ochildList);
    }
    GLog.note(GWikiLogCategory.Wiki, "Reorder page " + page2 + " before " + page1);
    childList.remove(page2);
    int insPos = childList.indexOf(page1);
    if (insPos == -1) {
      childList.add(page2);
      childList.add(page1);
    } else {
      childList.add(insPos, page2);
    }
    elementToEdit.getElementInfo().getProps().setStringList(GWikiPropKeys.CHILDORDER, childList);
    wikiContext.getWikiWeb().saveElement(wikiContext, elementToEdit, false);
    return noForward();
  }

  public boolean isRteDefaultEditor()
  {
    return StringUtils.equals(wikiDefaultEditor, "rte");
  }

  public boolean isGwikiEditDefaultFullscreen()
  {
    return gwikiEditDefaultFullscreen;
  }

  public void setGwikiEditDefaultFullscreen(boolean gwikiEditDefaultFullscreen)
  {
    this.gwikiEditDefaultFullscreen = gwikiEditDefaultFullscreen;
  }

  /**
   * Helper method to check keywords properties.
   * 
   * @param ctx
   * @param value
   */
  public static void checkKeywordProperties(GWikiContext ctx, String value)
  {
    try {
      List<String> keywords = CommaListParser.parseCommaList(value);
      for (String kw : keywords) {
        kw = StringUtils.replace(kw, ")", "){0,1}");
        Pattern.compile(kw);
      }
    } catch (Exception ex) {
      ctx.addValidationError("gwiki.edit.EditPage.message.invalidkeywordformat", ex.getMessage());
    }
  }

  public void renderSelectedMetatemplateHelp()
  {
    String mit = getMetaTemplatePageId();
    if (mit == null) {
      if (availableMetaTemplates == null || availableMetaTemplates.isEmpty() == true) {
        return;
      }
      mit = availableMetaTemplates.get(0).getSecond();

    }
    GWikiMetaTemplate mt = wikiContext.getWikiWeb().findMetaTemplate(mit);
    String phelpid = mt.getEditHelpPageId();
    if (StringUtils.isEmpty(phelpid) == true) {
      return;
    }
    GWikiElement helpEl = wikiContext.getWikiWeb().findElement(phelpid);
    if (helpEl == null) {
      return;
    }
    wikiContext.includeArtefakt(phelpid, "MainPage");
  }

  public String getMetaTemplatePageId()
  {
    return metaTemplatePageId;
  }

  public void setMetaTemplatePageId(String metaTemplatePageId)
  {
    this.metaTemplatePageId = metaTemplatePageId;
  }

  public Map<String, GWikiArtefakt<?>> getParts()
  {
    return parts;
  }

  public void setParts(Map<String, GWikiArtefakt<?>> parts)
  {
    this.parts = parts;
  }

  public Map<String, GWikiEditorArtefakt<?>> getEditors()
  {
    return editors;
  }

  public void setEditors(Map<String, GWikiEditorArtefakt<?>> editors)
  {
    this.editors = editors;
  }

  public GWikiElement getElementToEdit()
  {
    return elementToEdit;
  }

  public void setElementToEdit(GWikiElement elementToEdit)
  {
    this.elementToEdit = elementToEdit;
  }

  public GWikiMetaTemplate getMetaTemplate()
  {
    return metaTemplate;
  }

  public void setMetaTemplate(GWikiMetaTemplate metaTemplate)
  {
    this.metaTemplate = metaTemplate;
  }

  public List<Pair<String, String>> getAvailableMetaTemplates()
  {
    return availableMetaTemplates;
  }

  public void setAvailableMetaTemplates(List<Pair<String, String>> availableMetaTemplates)
  {
    this.availableMetaTemplates = availableMetaTemplates;
  }

  public boolean isChooseMetaTemplate()
  {
    return chooseMetaTemplate;
  }

  public void setChooseMetaTemplate(boolean chooseMetaTemplate)
  {
    this.chooseMetaTemplate = chooseMetaTemplate;
  }

  public String getTitle()
  {
    return title;
  }

  public void setTitle(String title)
  {
    this.title = title;
  }

  public String getCopyFromPageId()
  {
    return copyFromPageId;
  }

  public void setCopyFromPageId(String copyFromPageId)
  {
    this.copyFromPageId = copyFromPageId;
  }

  public String getStorePath()
  {
    return storePath;
  }

  public void setStorePath(String storePath)
  {
    this.storePath = storePath;
  }

  public String getBackUrl()
  {
    return backUrl;
  }

  public void setBackUrl(String backUrl)
  {
    this.backUrl = backUrl;
  }

  public boolean isLaunchPreview()
  {
    return launchPreview;
  }

  public void setLaunchPreview(boolean launchPreview)
  {
    this.launchPreview = launchPreview;
  }

  public boolean isNoNotificationEmails()
  {
    return noNotificationEmails;
  }

  public void setNoNotificationEmails(boolean noNotificationEmails)
  {
    this.noNotificationEmails = noNotificationEmails;
  }

  public GWikiElementInfo getBackupElementInfo()
  {
    return backupElementInfo;
  }

  public void setBackupElementInfo(GWikiElementInfo backupElementInfo)
  {
    this.backupElementInfo = backupElementInfo;
  }

  public boolean isInitalBackup()
  {
    return initalBackup;
  }

  public void setInitalBackup(boolean initalBackup)
  {
    this.initalBackup = initalBackup;
  }

  public boolean isDisableBackup()
  {
    return disableBackup;
  }

  public void setDisableBackup(boolean disableBackup)
  {
    this.disableBackup = disableBackup;
  }

  public String getPath()
  {
    return path;
  }

  public void setPath(String path)
  {
    this.path = path;
  }

  public String getWikiDefaultEditor()
  {
    return wikiDefaultEditor;
  }

  public void setWikiDefaultEditor(String wikiDefaultEditor)
  {
    this.wikiDefaultEditor = wikiDefaultEditor;
  }

  public String getHideParts()
  {
    return hideParts;
  }

  public void setHideParts(String hideParts)
  {
    this.hideParts = hideParts;
  }

}
