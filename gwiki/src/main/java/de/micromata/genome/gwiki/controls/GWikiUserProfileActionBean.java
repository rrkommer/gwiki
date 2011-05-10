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

package de.micromata.genome.gwiki.controls;

import org.apache.commons.lang.StringUtils;

import de.micromata.genome.gdbfs.FileNameUtils;
import de.micromata.genome.gwiki.model.GWikiAuthorizationRights;
import de.micromata.genome.gwiki.model.GWikiElementInfo;
import de.micromata.genome.gwiki.model.GWikiPropKeys;

/**
 * ActionBean for displaying user profile.
 * 
 * @author Roger Rene Kommer (r.kommer@micromata.de)
 * 
 */
public class GWikiUserProfileActionBean extends GWikiEditPageActionBean
{
  protected String userName;

  protected boolean initUser()
  {
    if (StringUtils.isEmpty(pageId) == false
        || (wikiContext.isAllowTo(GWikiAuthorizationRights.GWIKI_ADMIN.name()) == true && isNewPage() == true)) {
      if (StringUtils.isEmpty(pageId) == false) {
        userName = FileNameUtils.getNamePart(pageId);
      }
      return true;
    }
    userName = wikiContext.getWikiWeb().getAuthorization().getCurrentUserName(wikiContext);
    if (StringUtils.isEmpty(userName) == true) {
      wikiContext.addSimpleValidationError(wikiContext.getTranslated("gwiki.profile.user.nouser"));
      return false;
    }
    pageId = "admin/user/" + userName;
    elementToEdit = wikiContext.getWikiWeb().findElement("admin/user/" + userName);
    if (elementToEdit == null) {
      wikiContext.addSimpleValidationError(wikiContext.getTranslated("gwiki.profile.user.nouser"));
      return false;
    }
    metaTemplate = elementToEdit.getMetaTemplate();
    metaTemplatePageId = metaTemplate.getPageId();
    return true;
  }

  public Object onInit()
  {
    if (initUser() == false) {
      return null;
    }
    
    wikiContext.getRequiredJs().add("/static/js/jquery.fieldset-collapsible.js");
    
    Object ret = super.onInit();
    if (wikiContext.isAllowTo(GWikiAuthorizationRights.GWIKI_ADMIN.name()) == false) {
      super.getEditors().remove("Settings");
    }
    if (isNewPage() == true && elementToEdit != null) {
      GWikiElementInfo ei = elementToEdit.getElementInfo();
      ei.getProps().setStringValue(GWikiPropKeys.AUTH_VIEW, GWikiAuthorizationRights.GWIKI_PRIVATE.name());
      ei.getProps().setStringValue(GWikiPropKeys.AUTH_EDIT, GWikiAuthorizationRights.GWIKI_PRIVATE.name());
      ei.getProps().setStringValue(GWikiPropKeys.CREATEDBY, FileNameUtils.getNamePart(ei.getId()));
    }
    return ret;
  }

  @Override
  public Object onSave()
  {
    if (initUser() == false) {
      return null;
    }

    if (init() == false) {
      return null;
    }

    saveParts();
    if (wikiContext.isAllowTo(GWikiAuthorizationRights.GWIKI_ADMIN.name()) == false) {
      super.getEditors().remove("Settings");
      GWikiElementInfo prevElementInfo = wikiContext.getWikiWeb().findElementInfo(pageId);
      elementToEdit.getElementInfo().setProps(prevElementInfo.getProps());

    }
    if (wikiContext.hasValidationErrors() == true) {
      return null;
    }

    if (isNewPage() == true && elementToEdit != null) {
      // GWikiPropsArtefakt art = (GWikiPropsArtefakt) elementToEdit.getMainPart();
      GWikiElementInfo ei = elementToEdit.getElementInfo();
      pageId = "admin/user/" + title;
      ei.setId(pageId);
      ei.getProps().setStringValue(GWikiPropKeys.CREATEDBY, FileNameUtils.getNamePart(ei.getId()));
      // ei.getProps().setStringValue(GWikiPropKeys.TITLE, userName);
      // ei.getProps().setStringValue(GWikiPropKeys.TITLE, userName);
    }
    wikiContext.setRequestAttribute(NO_NOTIFICATION_EMAILS, noNotificationEmails);
    wikiContext.getWikiWeb().saveElement(wikiContext, elementToEdit, false);

    return goBack(false);

  }

  public static void renderUserProps()
  {

  }

  public String getUserName()
  {
    return userName;
  }

  public void setUserName(String userName)
  {
    this.userName = userName;
  }
}
