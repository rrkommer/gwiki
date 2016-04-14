//
// Copyright (C) 2010-2016 Roger Rene Kommer & Micromata GmbH
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//  http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.
//

package de.micromata.genome.gwiki.pagetemplates_1_0;

import org.apache.commons.lang.StringUtils;

import de.micromata.genome.gwiki.model.GWikiElement;
import de.micromata.genome.gwiki.page.impl.GWikiEditorArtefakt;
import de.micromata.genome.gwiki.page.impl.actionbean.ActionBeanBase;
import de.micromata.genome.gwiki.pagetemplates_1_0.editor.PtWikiAttachmentEditor;
import de.micromata.genome.gwiki.pagetemplates_1_0.editor.PtWikiEditAttachmentEditor;
import de.micromata.genome.gwiki.pagetemplates_1_0.editor.PtWikiHeadlineEditor;
import de.micromata.genome.gwiki.pagetemplates_1_0.editor.PtWikiImageEditor;
import de.micromata.genome.gwiki.pagetemplates_1_0.editor.PtWikiLinkEditor;
import de.micromata.genome.gwiki.pagetemplates_1_0.editor.PtWikiRawTextEditor;
import de.micromata.genome.gwiki.pagetemplates_1_0.editor.PtWikiRichTextEditor;
import de.micromata.genome.gwiki.pagetemplates_1_0.editor.PtWikiSectionEditorArtefakt;

/**
 * @author Roger Rene Kommer (r.kommer@micromata.de)
 * 
 */
public class PtPageSectionEditorActionBean extends ActionBeanBase
{
  private static final String EDITOR_RTE = "rte";

  private static final String EDITOR_RAW = "text";

  private static final String EDITOR_HL = "headline";

  private static final String EDITOR_IMAGE = "image";

  private static final String EDITOR_LINK = "link";

  private static final String EDITOR_ATTACHMENT = "attachment";

  private String pageId;

  private String sectionName;

  private String editor;

  private String hint;

  private String maxWidth;

  private String maxFileSize;

  private boolean allowWikiSyntax;

  private PtWikiSectionEditorArtefakt< ? > secEditor;

  private GWikiElement element;

  private PtWikiSectionEditorArtefakt< ? > createEditor()
  {

    if (pageId == null) {
      return null;
    }
    element = wikiContext.getWikiWeb().getElement(pageId);

    String fieldNumber = wikiContext.getRequestParameter("field");

    if (StringUtils.equals(editor, EDITOR_RTE)) {
      return new PtWikiRichTextEditor(element, sectionName, editor, hint);
    } else if (StringUtils.equals(editor, EDITOR_RAW)) {
      return new PtWikiRawTextEditor(element, sectionName, editor, hint, allowWikiSyntax);
    } else if (StringUtils.equals(editor, EDITOR_HL)) {
      return new PtWikiHeadlineEditor(element, sectionName, editor, hint);
    } else if (StringUtils.equals(editor, EDITOR_IMAGE)) {
      return new PtWikiImageEditor(element, sectionName, editor, hint, maxWidth, maxFileSize);
    } else if (StringUtils.equals(editor, EDITOR_LINK)) {
      return new PtWikiLinkEditor(element, sectionName, editor, hint, fieldNumber);
    } else if (StringUtils.equals(editor, EDITOR_ATTACHMENT)) {
      if (StringUtils.isNotEmpty(fieldNumber)) {
        return new PtWikiEditAttachmentEditor(element, sectionName, editor, hint, fieldNumber);
      } else {
        return new PtWikiAttachmentEditor(element, sectionName, editor, hint, maxFileSize, fieldNumber);
      }
    }
    return null;
  }

  private void init()
  {
    secEditor = createEditor();
    if (secEditor != null) {
      secEditor.prepareHeader(wikiContext);
    }
  }

  public Object onInit()
  {
    init();
    return null;
  }

  public Object onSave()
  {
    init();

    secEditor.onSave(wikiContext);

    if (wikiContext.hasValidationErrors()) {
      return null;
    }

    wikiContext.getWikiWeb().saveElement(wikiContext, element, false);

    // returns noForward to close the fancybox!
    wikiContext.append("<script type='text/javascript'>parent.$.fancybox.close();window.parent.location.reload();</script>");
    wikiContext.flush();
    return noForward();
  }

  public Object onDelete()
  {
    init();

    secEditor.onDelete(wikiContext);

    if (wikiContext.hasValidationErrors()) {
      return null;
    }

    wikiContext.getWikiWeb().saveElement(wikiContext, element, false);

    /*
     * String actionPageId = wikiContext.getCurrentElement().getElementInfo().getId();
     * 
     * wikiContext.append("<script type='text/javascript'>window.location.href='/").append(actionPageId);
     * wikiContext.append("?pageId=").append(pageId); wikiContext.append("&sectionName=").append(sectionName);
     * wikiContext.append("&editor=").append(editor); wikiContext.append("'</script>");
     */

    wikiContext.append("<script type='text/javascript'>parent.$.fancybox.close();window.parent.location.reload();</script>");
    wikiContext.flush();

    return noForward();
  }

  public Object onCancel()
  {
    return pageId;
  }

  public String getPageId()
  {
    return pageId;
  }

  public void setPageId(String pageId)
  {
    this.pageId = pageId;
  }

  public String getSectionName()
  {
    return sectionName;
  }

  public void setSectionName(String sectionName)
  {
    this.sectionName = sectionName;
  }

  public String getEditor()
  {
    return editor;
  }

  public void setEditor(String editor)
  {
    this.editor = editor;
  }

  public String getHint()
  {
    return hint;
  }

  public void setHint(String hint)
  {
    this.hint = hint;
  }

  public GWikiEditorArtefakt< ? > getSecEditor()
  {
    return secEditor;
  }

  public void setSecEditor(PtWikiSectionEditorArtefakt< ? > secEditor)
  {
    this.secEditor = secEditor;
  }

  /**
   * @param wikiSyntax the wikiSyntax to set
   */
  public void setAllowWikiSyntax(boolean allowWikiSyntax)
  {
    this.allowWikiSyntax = allowWikiSyntax;
  }

  /**
   * @return the wikiSyntax
   */
  public boolean isAllowWikiSyntax()
  {
    return allowWikiSyntax;
  }

  /**
   * @param maxWidth the maxWidth to set
   */
  public void setMaxWidth(String maxWidth)
  {
    this.maxWidth = maxWidth;
  }

  /**
   * @return the maxWidth
   */
  public String getMaxWidth()
  {
    return maxWidth;
  }

  /**
   * @param maxFileSize the maxFileSize to set
   */
  public void setMaxFileSize(String maxFileSize)
  {
    this.maxFileSize = maxFileSize;
  }

  /**
   * @return the maxFileSize
   */
  public String getMaxFileSize()
  {
    return maxFileSize;
  }

}
