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
package de.micromata.genome.gwiki.page.impl.wiki.macros;

import java.util.Date;

import de.micromata.genome.gwiki.controls.GWikiEditPageActionBean;
import de.micromata.genome.gwiki.model.GWikiAuthorizationRights;
import de.micromata.genome.gwiki.model.GWikiElement;
import de.micromata.genome.gwiki.model.GWikiElementInfo;
import de.micromata.genome.gwiki.model.GWikiPropKeys;
import de.micromata.genome.gwiki.model.GWikiProps;
import de.micromata.genome.gwiki.model.GWikiSettingsProps;
import de.micromata.genome.gwiki.model.config.GWikiMetaTemplate;
import de.micromata.genome.gwiki.page.impl.GWikiDefaultFileNames;

/**
 * @author Roger Rene Kommer (r.kommer@micromata.de)
 * 
 */
public class GWikiPageCommentEditActionBean extends GWikiEditPageActionBean implements GWikiPropKeys
{
  /**
   * pageId replied to.
   */
  private String replyTo;

  /**
   * Part of page
   */
  private String partOf;

  @Override
  protected GWikiElement createNewElement()
  {
    GWikiProps props = new GWikiSettingsProps();
    GWikiMetaTemplate metaTemplate = GWikiPageCommentMacroActionBean.initMetaTemplate(wikiContext);
    // props.setStringValue(TYPE, metaTemplate.getElementType());
    props.setStringValue(WIKIMETATEMPLATE, GWikiDefaultFileNames.COMMENT_METATEMPLATE);
    props.setStringValue(TITLE, "Comment");
    props.setStringValue(CREATEDBY, wikiContext.getWikiWeb().getAuthorization().getCurrentUserName(wikiContext));
    props.setDateValue(CREATEDAT, new Date());
    props.setStringValue(MODIFIEDBY, wikiContext.getWikiWeb().getAuthorization().getCurrentUserName(wikiContext));
    props.setStringValue(AUTH_EDIT, GWikiAuthorizationRights.GWIKI_PRIVATE.name());
    props.setDateValue(MODIFIEDAT, new Date());
    props.setStringValue(PARTOF, partOf);
    String viewRight = GWikiPageCommentMacroActionBean.getViewRightFromParent(wikiContext, partOf);
    if (viewRight != null) {
      props.setStringValue(AUTH_VIEW, viewRight);
    }

    props.setStringValue(AUTH_EDIT, GWikiAuthorizationRights.GWIKI_PRIVATE.name());
    GWikiElementInfo ei = new GWikiElementInfo(props, wikiContext.getWikiWeb().findMetaTemplate(GWikiDefaultFileNames.COMMENT_METATEMPLATE));
    GWikiElement elementToEdit = getWikiContext().getWikiWeb().getStorage().createElement(ei);
    return elementToEdit;
  }

  public String getReplyTo()
  {
    return replyTo;
  }

  public void setReplyTo(String replyTo)
  {
    this.replyTo = replyTo;
  }

  public String getPartOf()
  {
    return partOf;
  }

  public void setPartOf(String partOf)
  {
    this.partOf = partOf;
  }
}
